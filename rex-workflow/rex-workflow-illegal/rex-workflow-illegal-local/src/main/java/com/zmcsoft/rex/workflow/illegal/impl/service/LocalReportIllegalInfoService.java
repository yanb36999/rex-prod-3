package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.api.user.service.UserServiceManager;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.message.ftp.FTPMessageSender;
import com.zmcsoft.rex.tmb.illegal.entity.DriverLicense;
import com.zmcsoft.rex.workflow.illegal.api.IllegalCarStatus;
import com.zmcsoft.rex.workflow.illegal.api.ReportStatus;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import com.zmcsoft.rex.workflow.illegal.api.service.*;
import com.zmcsoft.rex.workflow.illegal.impl.dao.ReportIllegalCarDao;
import com.zmcsoft.rex.workflow.illegal.impl.dao.ReportIllegalInfoDao;
import com.zmcsoft.rex.workflow.illegal.impl.dao.ReportInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.runtime.Desc;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.expands.request.ftp.FtpRequest;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.concurrent.lock.annotation.Lock;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.datasource.annotation.UseDefaultDataSource;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.hswebframework.web.service.DefaultDSLUpdateService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;
import static org.hswebframework.web.oauth2.core.OAuth2Constants.username;

@Service
@Transactional(rollbackFor = Throwable.class)
@ConfigurationProperties(prefix = "com.zmcsoft.tmb.ftp")
@Slf4j(topic = "business.illegal.report")
public class LocalReportIllegalInfoService extends GenericEntityService<ReportIllegalInfo, String> implements ReportIllegalInfoService {


    @Autowired
    private IllegalReportLogService reportLogService;

    @Autowired
    private ReportIllegalInfoDao reportIllegalInfoDao;

    @Autowired
    private ReportIllegalCarDao reportIllegalCarDao;

    @Autowired
    private CarIllegalCaseHandleService carIllegalCaseHandleService;

    @Autowired
    private MessageSenders messageSenders;

    @Autowired
    private UserServiceManager userServiceManager;

    @Autowired
    private PropertyRefactor propertyRefactor;

    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    private ReportIllegalCarService reportIllegalCarService;

    @Autowired
    private IllegalCodeService illegalCodeService;


    @Autowired
    private ReportIllegalInfoService reportIllegalInfoService;

    @Autowired
    private RequestBuilder requestBuilder = new SimpleRequestBuilder();

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<ReportIllegalInfo, String> getDao() {
        return reportIllegalInfoDao;
    }


    @Override
    public List<ReportIllegalInfo> getIllegalInfo(String plateNo, String plateType) {
        return DefaultDSLQueryService.createQuery(reportIllegalInfoDao)
                .where("plateNo", plateNo)
                .and("plateType", plateType)
                .orderByDesc("updateTime")
                .listNoPaging();
    }

    @Override
    public List<ReportIllegalInfo> getIllegalInfoHistory(String plateNo, String plateType) {
        return DefaultDSLQueryService.createQuery(reportIllegalInfoDao)
                .where("plateNo", plateNo)
                .and("plateType", plateType)
                .in("disposeStatus", HandleStatusDefine.SUCCESS.getCode(),HandleStatusDefine.STOP.getCode())
                .orderByDesc("updateTime")
                .listNoPaging();
    }

    @Override
    public List<ReportIllegalInfo> getIllegalInfo(String reportId, String plateNo, String plateType) {
        return DefaultDSLQueryService.createQuery(reportIllegalInfoDao)
                .where("reportId", reportId)
                .and("plateType", plateType)
                .and("plateNo", plateNo)
                .listNoPaging();
    }

    @Override
    public boolean updateStatusByCarInfo(String plateNo, String plateType, String reportId, String licenseNo) {
        int exec = DefaultDSLUpdateService.createUpdate(reportIllegalInfoDao)
                .where("plateNo", plateNo)
                .and("plateType", plateType)
                .and("reportId", reportId)
                .set("disposeStatus", HandleStatusDefine.REQUEST)
                .set("licenseNo", licenseNo)
                .exec();
        return exec == 1;

        //return exec == 1 ? true : false;
    }

    @Override
    public boolean sign(String illegalId) {
        DefaultDSLUpdateService.createUpdate(reportIllegalInfoDao)
                .where("id", illegalId)
                .set("disposeStatus", HandleStatusDefine.NO_PAY)
                .exec();
        return true;
    }

    @Override
    public boolean updatePayStatusByDecisionNo(String decisionNumber, String handleStatus) {
        int i = createUpdate()
                .set("disposeStatus", handleStatus)
                .where("decisionNo", decisionNumber)

                .when(HandleStatusDefine.SUCCESS.eq(Byte.valueOf(handleStatus)),
                        update -> update.where().not("disposeStatus", String.valueOf(HandleStatusDefine.PAY.getCode())))

                .when(HandleStatusDefine.PAY_FAIL.eq(Byte.valueOf(handleStatus)),
                        update -> update.where("disposeStatus", String.valueOf(HandleStatusDefine.NO_PAY.getCode())))
                .exec();
        return i == 1;
    }


    @Scheduled(cron = "0/50 * * * * ?")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @UseDefaultDataSource
    @Lock("autoSyncHandleReportFromFtp")
    public void autoSyncHandleReportFromFtp() throws Exception {

        String path = "/DataIn2/";

        messageSenders.ftp()
                .list(path, fileName -> {
                    try {
                        ByteArrayOutputStream data = new ByteArrayOutputStream();
                        if (fileName.getName().endsWith(".rexresult")) {
                            //下载文件
                            messageSenders.ftp().download(path + fileName.getName(), data).send();

                            data.flush();
                            data.close();

                            String json = data.toString();
                            log.debug("开始解析违法举报报文:{}\n{}", fileName.getName(), json);

//                            RspRerportInfo parseObject = JSON.parseObject(json, RspReportIllegalCar.class, new ParserConfig(), 989);

                            RspRerportInfo rerportInfo = JSON.parseObject(json).getJSONObject("WF_REPORT").toJavaObject(RspRerportInfo.class);

                            ReportIllegalCar reportIllegalCar = new ReportIllegalCar();

                            RspReportIllegalCar wf_car = rerportInfo.getWf_car();
                            //更新违法车辆信息
                            reportIllegalCar.setId(wf_car.getIdNo());//未收到IdNo数据


                            reportIllegalCar.setPunishSignDept(wf_car.getPunishSignDept());
                            reportIllegalCar.setPunishSignDeptName(wf_car.getPunishSignDeptName());
                            reportIllegalCar.setPunishSignStatus(wf_car.getPunishSignStatus());
                            reportIllegalCar.setPunishSignPerson(wf_car.getPunishSignPerson());
                            reportIllegalCar.setPunishSignName(wf_car.getPunishSignName());
                            reportIllegalCar.setPunishResult(wf_car.getPunishResult());
                            reportIllegalCar.setStopCause(wf_car.getStopCause());
                            reportIllegalCar.setUpdateTime(wf_car.getUpdateTime());
                            reportIllegalCar.setDspStatus(wf_car.getPunishSignStatus());


                            int i = DefaultDSLUpdateService.createUpdate(reportIllegalCarDao, reportIllegalCar)
                                    .where("id", reportIllegalCar.getId())
                                    .exec();


                            log.debug("更新违法车辆数据{}条\n", i, JSON.toJSONString(reportIllegalCar, SerializerFeature.PrettyFormat));

                            List<JsonReportIllegalInfo> wf_infoList = wf_car.getWf_info();

                            AtomicInteger atomicInteger = new AtomicInteger();
                            for (JsonReportIllegalInfo wf_info : wf_infoList) {
                                ReportIllegalInfo reportIllegalInfo = new ReportIllegalInfo();

                                //修改状态位为已签收
                                reportIllegalInfo.setDisposeStatus(
                                        wf_info.getDspStatus().equals(String.valueOf(HandleStatusDefine.NO_SIGN.getCode())) ?
                                                String.valueOf(HandleStatusDefine.NO_PAY.getCode()) : wf_info.getDspStatus());
                                reportIllegalInfo.setDspOffice(wf_info.getDspOffice());
                                reportIllegalInfo.setDspOfficeName(wf_info.getDspOfficeName());
                                reportIllegalInfo.setDspDate(wf_info.getDspDate());
                                reportIllegalInfo.setId(wf_info.getId());
                                reportIllegalInfo.setDecisionNo(wf_info.getDecisionNo());
                                reportIllegalInfo.setDecisionType(wf_info.getDecisionType());
                                reportIllegalInfo.setCheckNo(wf_info.getCheckNo());
                                reportIllegalInfo.setCancelCause(wf_info.getCancelCause());
                                reportIllegalInfo.setCancelSign(wf_info.getCancelSign());

                                int updateInfoCount = DefaultDSLUpdateService.createUpdate(reportIllegalInfoDao, reportIllegalInfo)
                                        .where("id", reportIllegalInfo.getId()).exec();


                                //发送微信提醒，如果该车辆未在蓉E行注册则发送短信提醒用户注册
                                StringBuilder sb = new StringBuilder();
                                sb.append("您的违法行为已处理（决定书编号：")
                                        .append(wf_info.getDecisionNo())
                                        .append("），请及时缴纳罚款，逾期将产生滞纳金。（如您已成功缴款，请不要重复缴款）");
//                                messageSenders.wechat()
//                                        .to(userId)
//                                        .content(sb+"") //
//                                        .keyword("违法处理成功")
//                                        .title("成都交警温馨提示")
//                                        .send();

                                log.info("获取Ftp报文，更新数据ReportIllegal{}条", updateInfoCount);


                            }


                            //查询该举报流水号的所有未处理违章数据总数
                            int total = DefaultDSLQueryService.createQuery(reportIllegalInfoDao)
                                    .where("disposeStatus", HandleStatusDefine.NEW.getCode())
                                    .and("reportId", wf_car.getReportId())
                                    .and("plateNo", wf_car.getPlateNo())
                                    .and("platType", wf_car.getPlateType())
                                    .total();

                            IllegalReportLog reportLog = IllegalReportLog.builder()
                                    .handlerId("system")
                                    .handlerName(wf_car.getPunishSignName())
                                    .orgCode(wf_car.getPunishSignDept())
                                    .reportId(reportIllegalCar.getId())
                                    .orgName(wf_car.getPunishSignDeptName())
                                    .status(wf_car.getDisposeStatus())//2017/11/9 获取处理的状态
                                    .build();

                            reportLogService.insert(reportLog);



//                            将文件写入到本地
                            OutputStream out = new FileOutputStream(new File("/data/rex-dispose/" + fileName.getName()));
                            out.write(data.toByteArray());
                            //处理文本
                            out.flush();
                            out.close();

                            //log.info("获取的报文{},内容:{}", fileName.getName(), data);
                            //删除ftp上面已经处理了的文本
                            boolean success = messageSenders.ftp().delete(path + fileName.getName()).send();
                            log.debug("删除FTP文件{} {}", path + fileName.getName(), success);

                        }
                    } catch (Exception e) {
                        log.error("解析违法举报响应报文失败", e);
                        throw new RuntimeException(e);
                        //  e.printStackTrace();
                    }
                })
                .send();
    }


    public void fileData(ReqConfirmData reqConfirmData, Authentication authentication) {

        ///替换川字
        if (reqConfirmData.getPlateNo() != null) {
            propertyRefactor.removePlateNumber(reqConfirmData::getPlateNo, reqConfirmData::setPlateNo);
//            reqConfirmData.setPlateNo(reqConfirmData.getPlateNo().replace("川",""));
        }

        ReportInfo reportInfo = reportInfoService.getByWaterId(reqConfirmData.getReportId());
        UserDriverLicense license = userServiceManager.userDriverLicenseService().getByUserId(authentication.getUser().getId());

        //举报信息
        RspRerportInfo wf_report = new RspRerportInfo();
        wf_report.setId(reportInfo.getId());
        wf_report.setWaterId(reportInfo.getReportId());
        wf_report.setName(reportInfo.getName());
        wf_report.setReportDate(reportInfo.getReportDate());
        wf_report.setOpenId(reportInfo.getOpenId());
        wf_report.setIdNumber(reportInfo.getIdNumber());
        wf_report.setPhone(reportInfo.getPhone());
        wf_report.setDescribe(reportInfo.getDescribe());
        wf_report.setAddress(reportInfo.getAddress());
        wf_report.setIllegalDate(reportInfo.getReportDate());
        wf_report.setReportType(reportInfo.getReportType());
        wf_report.setDspStatus(reportInfo.getDspStatus());


        //交管数据库车辆信息
        UserCar car = userServiceManager.userCarService().selectByPlateNumberAndPlateType(reqConfirmData.getPlateNo(), reqConfirmData.getPlateType());
        //响应违法车辆信息
        RspReportIllegalCar wf_car = new RspReportIllegalCar();
        //查询违法车辆信息
        ReportIllegalCar illegalCar = reportIllegalCarService.getIllegalCarInfo(reqConfirmData.getReportId(), reqConfirmData.getPlateNo(), reqConfirmData.getPlateType());

        wf_car.setReportId(reportInfo.getReportId());
        wf_car.setIdNo(illegalCar.getId());//车辆Id
        wf_car.setPlateType(illegalCar.getPlateType());
        wf_car.setPlateNo(illegalCar.getPlateNo());
        wf_car.setCarType(illegalCar.getCarType());
        wf_car.setCarStatus(illegalCar.getCarStatus());
        wf_car.setOwner(illegalCar.getOwner());
        wf_car.setUserNature(car.getUseNature());
        wf_car.setVin(car.getFrameNumber());
        wf_car.setContactPhone(illegalCar.getContactPhone());
        wf_car.setPhoneNo(illegalCar.getPhoneNo());
        wf_car.setFuelType(illegalCar.getFuelType());
        wf_car.setCarColor(illegalCar.getCarColor());
        wf_car.setCarBrand(illegalCar.getCarBrand());
        wf_car.setSignDept(illegalCar.getSignDept());
        wf_car.setSignDeptName(illegalCar.getSignDeptName());
        wf_car.setSignReviewer(illegalCar.getSignReviewer());
        wf_car.setSignReviewerName(illegalCar.getSignReviewerName());
        wf_car.setSignReviewStatus(illegalCar.getSignReviewStatus());


        String signDept = illegalCar.getPunishSignDept() + "000000000000";
        wf_car.setPunishSignDept(signDept.substring(0, 12));
        wf_car.setPunishSignDeptName(illegalCar.getPunishSignDeptName());
        wf_car.setPunishSignStatus(illegalCar.getDspStatus());
        wf_car.setPunishSignPerson(illegalCar.getPunishSignPerson());
        wf_car.setPunishSignName(illegalCar.getPunishSignName());
        wf_car.setPunishResult(illegalCar.getPunishResult());


        wf_car.setStopCause(illegalCar.getStopCause());
        wf_car.setIllegalCount(illegalCar.getIllegalCount());
        wf_car.setOverCount("");//已处罚笔数
        wf_car.setImgPath(illegalCar.getVideoShotPath());
        wf_car.setMsgInformTime(illegalCar.getMsgSendDate());
        wf_car.setMsgInformStatus(illegalCar.getMsgSendStatus());
        wf_car.setWechatInformTime(new Date());
        wf_car.setWechatInformStatus("1");
        wf_car.setCreateTime(new Date());
        wf_car.setUpdateTime(new Date());


        //违法信息
        List<JsonReportIllegalInfo> wf_info = new ArrayList<>();

        List<ReportIllegalInfo> illegalInfoList = reportIllegalInfoService.getIllegalInfo(reqConfirmData.getReportId(), reqConfirmData.getPlateNo(), reqConfirmData.getPlateType());

        String s = "";
        Map<String, RspRerportInfo> wf_reports = new HashMap<>();

        //网上调查申请信息
        ReportApplyInfo wf_apply = new ReportApplyInfo();

        if (illegalInfoList != null && illegalInfoList.size() > 0) {

            for (ReportIllegalInfo illegalInfo : illegalInfoList) {

                JsonReportIllegalInfo wf = new JsonReportIllegalInfo();
                wf.setReportId(reportInfo.getReportId());
                wf.setId(illegalInfo.getId());
                wf.setEnterOfficeCode(illegalCar.getSignDept());
                wf.setEnterOfficeName(illegalCar.getSignDeptName());
                wf.setPlateType(illegalInfo.getPlateType());
                wf.setPlateNo(illegalInfo.getPlateNo());
                wf.setUseNature(car.getUseNature());
                wf.setEngineCode(car.getFrameNumber());//发动机号
                wf.setVin(car.getFrameNumber());
                wf.setCarColor(illegalCar.getCarColor());
                wf.setCarBrand(illegalCar.getCarBrand());
                wf.setTrafficeWay(illegalCar.getCarType());
                wf.setCollectWay("1");//采集方式
                wf.setIllegaltime(illegalInfo.getIllegaltime());
                wf.setAdminDivision(illegalCar.getCarType());//车辆分类
                wf.setIllegalAddressCode(illegalInfo.getIllegalAddress());
                wf.setIllegalSite(illegalInfo.getIllegalSite());
                wf.setRoadCode(illegalInfo.getRoadCode());
                wf.setRoadName(illegalInfo.getRoadName());
                wf.setIllegalBehaviorName(illegalCodeService.getByCode(illegalInfo.getIllegalBehavior()).getContentAbbreviate());
                wf.setIllegalBehaviorCode(illegalInfo.getIllegalBehavior());
                wf.setIllegalFine(illegalInfo.getIllegalFine());
                wf.setIllegalScore(illegalInfo.getIllegalScore());
                wf.setDspStatus(illegalInfo.getDisposeStatus());
                wf.setIllegalInfoSource("1");//信息来源
                wf.setReportId(illegalInfo.getReportId());
                wf_info.add(wf);

                wf_apply.setResult(illegalInfo.getDisposeStatus());
            }
            wf_apply.setReportId(reportInfo.getReportId());
            wf_apply.setSubmitCount(String.valueOf(illegalInfoList.size()));
            wf_apply.setId(IDGenerator.MD5.generate());
            wf_apply.setOpenId(reportInfo.getOpenId());
            wf_apply.setLicenseType("驾驶证");
            wf_apply.setFileNo(license.getFileNumber());
            wf_apply.setLicenseNo(license.getLicenseNumber());
            wf_apply.setParties(license.getDriverName());
            wf_apply.setLicenseScore(license.getTotalScore());
            wf_apply.setLicenseStatus(license.getLicenseStatus());
            wf_apply.setContactPhone(wf_car.getContactPhone());
            wf_apply.setGiveOffice(license.getSendOffice());
            wf_apply.setDriverType(license.getDrivingModel());
            wf_apply.setHomeAddress(license.getContactSite());
            wf_apply.setSubmitTime(reqConfirmData.getConfirmTime());

            wf_apply.setPlateType(illegalCar.getPlateType());
            wf_apply.setPlateNo(illegalCar.getPlateNo());
            wf_apply.setOperationTime(new Date());
            wf_apply.setReportId(illegalCar.getReportId());


            wf_report.setWf_car(wf_car);
            wf_car.setWf_apply(wf_apply);
            wf_car.setWf_info(wf_info);
            wf_reports.put("wf_report", wf_report);

            s = JSON.toJSONString(wf_reports, WriteDateUseDateFormat, DisableCircularReferenceDetect, WriteNullStringAsEmpty, WriteNullNumberAsZero);

            String fileName = "JB_" + System.currentTimeMillis() + ".rexjbcf";
            //将文件写入到本地

            try {

                OutputStream out = new FileOutputStream(new File("/data/rex-dispose-report/" + fileName));
//                out.write(wf_reports);
                out.write(wf_reports.toString().getBytes());
                //处理文本
                out.flush();
                out.close();
                log.info("微信确认举报数据保存到本地，文件名{}", fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            uploadFile(fileName, s);//上传FTP文件
            log.info("IllegalReportController-fileData上传违法json报文{}结果{}", fileName);

            log.info("IllegalReportController-fileData上传文件内容:{}", s);


        }
    }

    @Override
    public CarIllegalCaseHandle getDriverLicense(String userId) {

        CarIllegalCaseHandle caseHandle = carIllegalCaseHandleService.getLicenseScore(userId);

        return caseHandle;
    }


    //上传文本到Ftp
    public void uploadFile(String fileName, String json) {
        //获取默认的ftp工具
        FTPMessageSender sender = messageSenders.ftp();
        //准备json到ftp上传队列
        sender.upload("/DataOut/" + fileName, json);
        //执行上传
        try {
            sender.send();
        } catch (Exception e) {
            log.error("上传文件到ftp失败!", e);
            throw new BusinessException("系统错误,请联系管理员!", e);
        }

    }

}
