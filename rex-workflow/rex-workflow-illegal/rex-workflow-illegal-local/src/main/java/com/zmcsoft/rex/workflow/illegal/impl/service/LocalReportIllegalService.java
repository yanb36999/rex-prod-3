package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.alibaba.fastjson.JSON;
import com.zmcsoft.rex.api.user.entity.CarIllegal;
import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.service.UserServiceManager;
import com.zmcsoft.rex.commons.district.api.service.RoadSegService;
import com.zmcsoft.rex.message.MessageSender;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.message.ftp.FTPMessageSender;
import com.zmcsoft.rex.message.ftp.FTPUpload;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfo;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoDetailRequest;
import com.zmcsoft.rex.tmb.illegal.service.CarInfoService;
import com.zmcsoft.rex.utils.FileUtils;
import com.zmcsoft.rex.workflow.illegal.api.IllegalCarStatus;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import com.zmcsoft.rex.workflow.illegal.api.service.*;
import com.zmcsoft.rex.workflow.illegal.impl.dao.CwtReportIllegalDetailDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.binary.Base64;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.expands.request.ftp.FtpRequest;
import org.hswebframework.ezorm.core.dsl.Query;
import org.hswebframework.ezorm.core.param.Term;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.commons.entity.PagerResult;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.datasource.annotation.UseDefaultDataSource;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;

@Service
@Slf4j(topic = "business.illegal.report")
public class LocalReportIllegalService implements ReportIllegalService {

    @Autowired
    private CwtReportIllegalDetailDao cwtReportIllegalDetailDao;

    @Autowired
    private IllegalCodeService illegalCodeService;

    @Autowired
    private UserServiceManager userServiceManager;

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private ReportIllegalInfoService illegalInfoService;

    @Autowired
    private ReportIllegalCarService reportIllegalCarService;

    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    private IllegalReportLogService reportLogService;

    @Autowired
    private PropertyRefactor propertyRefactor;

    @Autowired
    private MessageSenders messageSenders;



    @Override
    @UseDefaultDataSource
    public PagerResult<ReportIllegalDetail> getReport(List<String> orgCode, QueryParamEntity entity) {

        List<Term> terms = entity.getTerms();

        entity.setTerms(new ArrayList<>());

        Query.empty(entity)
                .when(orgCode != null && !orgCode.isEmpty(),
                        query -> query.in("illegalCar.signDept", orgCode));

        entity.nest().setTerms(terms);

        int total = cwtReportIllegalDetailDao.countFromRex(entity);

        if (0 == total) {
            return PagerResult.empty();
        }
        return PagerResult.of(total, cwtReportIllegalDetailDao.queryFromRex(entity));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    //车网通数据源
    @UseDataSource(value = "userSysDataSource", fallbackDefault = false)
    public int updateNewReportStatus(String reportId, String plateType, String plateNumber, String status) {
        Objects.requireNonNull(reportId, "reportId can not be null");
        Objects.requireNonNull(plateType, "plateType can not be null");
        Objects.requireNonNull(plateNumber, "plateNumber can not be null");
        Objects.requireNonNull(status, "status can not be null");

//        if (!plateNumber.startsWith("川")) {
//            plateNumber = "川" + plateNumber;
//        }
        return cwtReportIllegalDetailDao.updateStatus(reportId, plateType, plateNumber, status);
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    //车网通数据源
    @UseDataSource(value = "userSysDataSource", fallbackDefault = false)
    public int updateCheckFailStatus(String reportId, String plateType, String plateNumber, String status) {
        Objects.requireNonNull(reportId, "reportId can not be null");
        Objects.requireNonNull(plateType, "plateType can not be null");
        Objects.requireNonNull(plateNumber, "plateNumber can not be null");
        Objects.requireNonNull(status, "status can not be null");

//        if (!plateNumber.startsWith("川")) {
//            plateNumber = "川" + plateNumber;
//        }
        return cwtReportIllegalDetailDao.updateCheckFailStatus(reportId, plateType, plateNumber, status);
    }




    @Override
    @UseDefaultDataSource
    public ReportIllegalDetail getReportDetail(String id, String plateType, String plateNumber) {
        List<ReportIllegalDetail> details = Query.<ReportIllegalDetail, QueryParamEntity>empty(new QueryParamEntity())
                .setListExecutor(cwtReportIllegalDetailDao::queryFromRex)
                .where("reportInfo.reportId", id)
                .and("illegalCar.plateType", plateType)
                .and("illegalCar.plateNo", propertyRefactor.removePlateNumber(plateNumber))
                .doPaging(0, 1)
                .list();
        if (details.isEmpty()) {
            return null;
        }
        ReportIllegalDetail detail = details.get(0);
        detail.setIllegalInfo(illegalInfoService.getIllegalInfo(id, propertyRefactor.removePlateNumber(plateNumber), plateType));

        detail.setReportLogs(reportLogService.getByReportId(detail.getIllegalCar().getId()));

        return detail;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @UseDataSource(value = "userSysDataSource", fallbackDefault = false)
    public List<ReportIllegalDetail> getOld(String plateNo,String plateType){

       return DefaultDSLQueryService.createQuery(cwtReportIllegalDetailDao)
                .sql("t_illegal_user_order.f_zt = '2' " +
                        "and t_illegal_user_order.f_zt_lx = '201' " +
                        "and car.f_res = '1' " +
                        "and car.f_res_lx = '101'"+
                        "and car.f_wflx <> '1012'" +
                        "and car.f_status = '1' " +
                        "and car.f_hphm = ?" +
                        "and car.f_hpzl = ?",plateNo,plateType).listNoPaging();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //车网通数据源
    @UseDataSource(value = "userSysDataSource", fallbackDefault = false)
    public boolean updateOld(String reportId,String plateNo,String plateType) {

        int result = cwtReportIllegalDetailDao.acceptReportCar(reportId, plateNo, plateType);
        //如果车辆违法状态被改成有效的数据则举报状态也应该为有效状态
        if(result>0){
            log.info("更新车网通举报车辆数据{}条,车牌号码为:{}",result,plateNo);
            int i = cwtReportIllegalDetailDao.acciptReport(reportId);
            if (i>0){
                log.info("更新车网通举报数据{}条,reportId:{}",i,reportId);
            }else {
                log.error("更新车网通举报数据失败");
            }
        }else {

            log.error("更新车网通举报车辆数据失败,车牌号:{}",plateNo);
        }
        return true;
    }


    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    //车网通数据源
    @UseDataSource(value = "userSysDataSource", fallbackDefault = false)
    public PagerResult<ReportIllegalDetail> getNewReport(List<String> orgCodes, QueryParamEntity entity) {


        List<Term> terms = entity.getTerms();

        entity.setTerms(new ArrayList<>());

        Query.empty(entity)
                .sql("t_illegal_user_order.f_zt =1 " +
                        "and t_illegal_user_order.f_zt_lx = '101' " +
                        "and car.f_status = 1 " +
                        "and car.f_wflx <> '1012'" +
                        "and car.f_res in ('0','5')" +
                        "and car.f_res_lx = '0'"+
                        "and car.f_hphm like '川A%'")
                .when(orgCodes != null && !orgCodes.isEmpty(),
                        query -> query.nest().each(orgCodes, (q, code) -> {
                            q.or().sql("car.f_dw_code=?", code);
                        }));

        entity.nest().setTerms(terms);


        int total = cwtReportIllegalDetailDao.count(entity);

        if (0 == total) {
            return PagerResult.empty();
        }


        return PagerResult.of(total, cwtReportIllegalDetailDao.query(entity));
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    //车网通数据源
    @UseDataSource(value = "userSysDataSource", fallbackDefault = false)
    public List<IllegalReportLog> getCheckBeforeLogs(String reportId) {
        return new ArrayList<>();
//        return cwtReportIllegalDetailDao.selectLogsByReportId(reportId);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    //车网通数据源
    @UseDataSource(value = "userSysDataSource", fallbackDefault = false)
    public ReportIllegalDetail getNewReportDetail(String id, String plateType, String plateNumber) {
        ReportIllegalDetail detail = DefaultDSLQueryService.createQuery(cwtReportIllegalDetailDao)
                .where("reportInfo.reportId", id)
                .and("illegalCar.plateType", plateType)
                .and("illegalCar.plateNo", plateNumber)
                .single();

        if (null == detail) {
            return null;
        }
        if (detail.getIllegalCar() != null) {
            try {
                CarInfo carInfo = carInfoService
                        .carInfoDetail(CarInfoDetailRequest.builder()
                                .plateNumber(propertyRefactor.removePlateNumber(detail.getIllegalCar().getPlateNo()))
                                .plateType(detail.getIllegalCar().getPlateType()).build());

                if (carInfo == null) {
                    log.error("未能从交管局获取车辆信息 {} {} ", detail.getIllegalCar().getPlateNo(), detail.getIllegalCar().getPlateType());
                }
                if (carInfo != null) {
                    detail.getIllegalCar().setOwner(carInfo.getOwner());
                    detail.getIllegalCar().setFileNumber(carInfo.getFileNumber());
                    detail.getIllegalCar().setCarStatus(carInfo.getStatus());
                    detail.getIllegalCar().setCarType(carInfo.getVehicleType());
                    detail.getIllegalCar().setPhoneNo(carInfo.getMobilePhone());
                    detail.getIllegalCar().setNewEnergy(carInfo.getNewEnergy());
                }
            } catch (Exception e) {
                log.error("从交管局获取车辆信息 {} {} 失败", detail.getIllegalCar().getPlateNo(), detail.getIllegalCar().getPlateType(), e);
                // throw new BusinessException("获取车辆信息失败");
            }
        }
        detail.setReportLogs(getCheckBeforeLogs(id));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkReport(String id, ReportIllegalDetail detail) {

        log.info("checkReport plateNo:{},PlateType:{}", detail.getIllegalCar().getPlateNo(), detail.getIllegalCar().getPlateType());
        ReportIllegalCar car = reportIllegalCarService
                .getIllegalCarInfo(id, propertyRefactor
                        .removePlateNumber(detail.getIllegalCar().getPlateNo()), detail.getIllegalCar().getPlateType());
        if (car != null) {
            throw new BusinessException("该举报已被处理!");
        }

        detail.getIllegalCar().setMsgSendDate(new Date());
        //举报信息
        reportInfoService.insert(detail.getReportInfo());

        //违法车辆信息
        String carId = reportIllegalCarService.insert(detail.getIllegalCar());

        //日志
        detail.getReportLogs().forEach(reportLog -> {
            reportLog.setReportId(carId);
            reportLogService.insert(reportLog);
        });

        if (!IllegalCarStatus.CHECK_FAIL.eq(detail.getIllegalCar().getDspStatus())) {

            //发送ftp
            for (ReportIllegalInfo illegalInfo : detail.getIllegalInfo()) {
                illegalInfo.setReportId(id);
                illegalInfo.setDisposeStatus(String.valueOf(HandleStatusDefine.NEW.getCode()));
                illegalInfoService.insert(illegalInfo);
            }
            sendToFtp(detail.getIllegalCar(), detail.getReportInfo(), detail.getIllegalInfo());
            try{
                log.info("insertCarIllegal start");
                insertCarIllegal(detail);
                log.info("insertCarIllegal end");
            }catch (Exception e){
                log.error("insertCarIllegal error!",e);
            }
        }

    }

    private void insertCarIllegal(ReportIllegalDetail detail) {
        ReportIllegalInfo reportIllegalInfo = detail.getIllegalInfo().get(0);
        CarIllegal carIllegal = new CarIllegal();
        carIllegal.setCarBrand(detail.getIllegalCar().getCarBrand());
        carIllegal.setCreateTime(detail.getIllegalCar().getCreateTime());
        carIllegal.setIllegalAddress(reportIllegalInfo.getIllegalAddress());
        carIllegal.setIllegalBehaviorName(reportIllegalInfo.getIllegalBehaviorName());
        carIllegal.setIllegalId(detail.getReportInfo().getReportId());
        carIllegal.setIllegalTime(reportIllegalInfo.getIllegaltime());
        carIllegal.setOwner(detail.getIllegalCar().getOwner());
        carIllegal.setPhone(detail.getIllegalCar().getPhoneNo());
        carIllegal.setPlateNumber(detail.getIllegalCar().getPlateNo());
        carIllegal.setPlateType(detail.getIllegalCar().getPlateType());
        carIllegal.setReportDate(detail.getReportInfo().getReportDate());
        carIllegal.setReportName(detail.getReportInfo().getName());
        carIllegal.setSignDept(detail.getIllegalCar().getSignDept());
        carIllegal.setSignDeptName(detail.getIllegalCar().getSignDeptName());
        carIllegal.setSignReviewerName(detail.getIllegalCar().getSignReviewerName());
        carIllegal.setStatus(Byte.valueOf("1"));
        carIllegal.setSynStatus(Byte.valueOf("2"));
        carIllegal.setText("");
        carIllegal.setUpdateTime(detail.getIllegalCar().getCreateTime());
        userServiceManager.carIllegalService().insert(carIllegal);
    }

    public void sendToFtp(ReportIllegalCar illegalCar, ReportInfo reportInfo, List<ReportIllegalInfo> illegalInfoList) {

//        SubmitIllegalData submitIllegalData = new SubmitIllegalData();
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


        propertyRefactor.applyDict("report-type", wf_report::getReportType, wf_report::setReportType, ",");

        //交管数据库车辆信息
//        UserCar car = userServiceManager.userCarService()
//                .selectByPlateNumberAndPlateType(illegalCar.getPlateNo(), illegalCar.getPlateType());
        CarInfo carInfo = null;
        try {
            carInfo = carInfoService
                    .carInfoDetail(CarInfoDetailRequest.builder()
                            .plateNumber(propertyRefactor.removePlateNumber(illegalCar.getPlateNo()))
                            .plateType(illegalCar.getPlateType()).build());

        } catch (Exception e) {
            log.error("从交管局获取车辆信息 {} {} 失败", illegalCar.getPlateNo(), illegalCar.getPlateType(), e);
            // thro
        }
        if (carInfo == null) {
            log.error("从交管局获取车辆信息 {} {} 失败", illegalCar.getPlateNo(), illegalCar.getPlateType());
            carInfo = CarInfo.builder().build();
        }

        //响应违法车辆信息
        RspReportIllegalCar wf_car = new RspReportIllegalCar();

        //查询违法车辆信息
//        ReportIllegalCar illegalCar = reportIllegalCarService.getIllegalCarInfo(carInfo.getReportId(),carInfo.getPlateNo(),carInfo.getPlateType());
        wf_car.setReportId(reportInfo.getReportId());
        wf_car.setIdNo(illegalCar.getId());
        wf_car.setPlateType(illegalCar.getPlateType());
        wf_car.setPlateNo(illegalCar.getPlateNo());
        wf_car.setCarType(illegalCar.getCarType());
        wf_car.setCarStatus(illegalCar.getCarStatus());
        wf_car.setOwner(illegalCar.getOwner());
        wf_car.setUserNature(carInfo.getUseNature());
        wf_car.setVin(carInfo.getVin());
        wf_car.setContactPhone(illegalCar.getContactPhone());
        wf_car.setPhoneNo(illegalCar.getPhoneNo());
        wf_car.setFuelType(illegalCar.getFuelType());
        wf_car.setCarColor(illegalCar.getCarColor());
        wf_car.setCarBrand(illegalCar.getCarBrand());
        wf_car.setSignDept(illegalCar.getSignDept());
        wf_car.setSignDeptName(illegalCar.getSignDeptName());
        wf_car.setSignReviewer(illegalCar.getSignReviewer());
        wf_car.setSignReviewerName(illegalCar.getSignReviewerName());
        wf_car.setSignReviewStatus(illegalCar.getDspStatus());

        wf_car.setPunishSignStatus(IllegalCarStatus.ENTER_NO.code());

        wf_car.setReportId(illegalCar.getReportId());


        wf_car.setPunishSignDeptName("");
        wf_car.setPunishSignPerson("");
        wf_car.setPunishSignName("");
        wf_car.setPunishResult("");
        wf_car.setMsgInformTime(illegalCar.getMsgSendDate());
        wf_car.setStopCause(illegalCar.getStopCause());
        wf_car.setIllegalCount(illegalCar.getIllegalCount());
        wf_car.setOverCount("");//已处罚笔数

        wf_car.setMsgInformTime(illegalCar.getMsgSendDate());
        wf_car.setMsgInformStatus(illegalCar.getMsgSendStatus());
        wf_car.setWechatInformTime(new Date());
        wf_car.setWechatInformStatus("1");
        wf_car.setCreateTime(new Date());
        wf_car.setUpdateTime(new Date());


        //违法信息
        List<JsonReportIllegalInfo> wf_info = new ArrayList<>();


        Map<String, RspRerportInfo> wf_reports = new HashMap<>();
        for (ReportIllegalInfo illegalInfo : illegalInfoList) {
            JsonReportIllegalInfo wf = new JsonReportIllegalInfo();

            wf.setIllegalInfoSource("0");
            wf.setDspStatus(String.valueOf(HandleStatusDefine.NEW.getCode()));
            wf.setId(illegalInfo.getId());
            wf.setEnterOfficeCode(illegalCar.getSignDept());
            wf.setEnterOfficeName(illegalCar.getSignDeptName());
            wf.setPlateType(illegalInfo.getPlateType());
            wf.setPlateNo(illegalInfo.getPlateNo());
            wf.setUseNature(carInfo.getUseNature());
            wf.setEngineCode(carInfo.getEngineNo());//发动机号
            wf.setVin(carInfo.getVin());
            wf.setCarColor(illegalCar.getCarColor());
            wf.setCarBrand(illegalCar.getCarBrand());
            wf.setTrafficeWay(illegalCar.getCarType());
            wf.setCollectWay("");//采集方式
            wf.setIllegaltime(illegalInfo.getIllegaltime());

            wf.setAdminDivision(illegalInfo.getAdminDivision());
            wf.setIllegalAddressCode(illegalInfo.getIllegalAddress());
            wf.setIllegalSite(illegalInfo.getIllegalSite());
            wf.setRoadCode(illegalInfo.getRoadCode());
            wf.setRoadName(illegalInfo.getIllegalSite());
            wf.setIllegalBehaviorName(illegalCodeService.getByCode(illegalInfo.getIllegalBehavior()).getContentAbbreviate());
            wf.setIllegalBehaviorCode(illegalInfo.getIllegalBehavior());
            wf.setIllegalFine(illegalInfo.getIllegalFine());
            wf.setIllegalScore(illegalInfo.getIllegalScore());
           // wf.setDspStatus(illegalInfo.getDisposeStatus());
            //  wf.setIllegalInfoSource("1");//信息来源
            wf.setReportId(illegalInfo.getReportId());

//
//                    wf.setIllegalBehavior(illegalInfo.getIllegalBehavior());
//                    wf.setIllegalScore(illegalInfo.getIllegalScore());
//                    wf.setIllegalFine(illegalInfo.getIllegalFine());
//                    wf.setDutyPolice(illegalInfo.getDutyPolice());
//                    wf.setPayWay(illegalInfo.getPayWay());
//                    wf.setDspOffice(illegalInfo.getDspOffice());
//                    wf.setDspOfficeName(illegalInfo.getDspOfficeName());
//                    wf.setDspDate(illegalInfo.getDspDate());
//
//                    wf.setPaySign("1");
//                    wf.setPayDate(new Date());
//                    wf.setDataEntryClerk(illegalInfo.getDataEntryClerk());
//                    wf.setDataEntryTime(illegalInfo.getDataEntryTime());
//                    wf.setResponsibleOne(illegalInfo.getResponsibleOne());
//                    wf.setResponsibleTwo(illegalInfo.getResponsibleTwo());
//                    wf.setUpdateTime(new Date());

//
//            //网上调查申请信息
//            ReportApplyInfo wf_apply = new ReportApplyInfo();
//            wf_apply.setId(IDGenerator.MD5.generate());
//            wf_apply.setOpenId(reportInfo.getOpenId());
//            wf_apply.setLicenseType("驾驶证");
//            wf_apply.setLicenseNo(license.getLicenseNumber());
//            wf_apply.setParties(illegalInfo.getParties());
//            wf_apply.setFileNo(illegalInfo.getFileNo());
//            wf_apply.setLicenseScore(license.getTotalScore());
//            wf_apply.setLicenseStatus(license.getLicenseStatus());
//            wf_apply.setContactPhone(license.getPhone());
//            wf_apply.setGiveOffice(license.getSendOffice());
//            wf_apply.setDriverType(license.getDrivingModel());
//            wf_apply.setHomeAddress(license.getContactSite());
//            wf_apply.setSubmitTime(reqConfirmData.getConfirmTime());
//            wf_apply.setSubmitCount("");
//            wf_apply.setPlateType(illegalCar.getPlateType());
//            wf_apply.setPlateNo(illegalCar.getPlateNo());
//            wf_apply.setOperationTime(new Date());
//            wf_apply.setOperationContent("");
//            wf_apply.setResult("");
//            wf_apply.setReportId(illegalCar.getReportId());


            wf_info.add(wf);

        }

        wf_report.setWf_car(wf_car);

        ReportApplyInfo applyInfo = ReportApplyInfo.builder().build();
        applyInfo.setId(wf_car.getIdNo());
        applyInfo.setFileNo(illegalCar.getFileNumber());


        wf_car.setWf_apply(applyInfo);
        wf_car.setWf_info(wf_info);
        wf_reports.put("wf_report", wf_report);


        String fileNamePrefix = "JB_" + System.currentTimeMillis();

        String fileName = fileNamePrefix + ".jblrfh";


        //    wf_car.setImgPath(uploadImageFile(fileNamePrefix+"_",illegalCar.getVideoShotPath()));



        //获取默认的ftp工具
        FTPMessageSender sender = messageSenders.ftp();

        //准备图片文件到ftp上传队列
        List<String> newFileName = createImage(fileNamePrefix, illegalCar.getVideoShotPath(), sender);
        List<String> fileBase64 = createImageBase64(illegalCar.getVideoShotPath());
        wf_car.setImgPath(newFileName);
        wf_car.setImageBase64(fileBase64);
        String json = JSON.toJSONString(wf_reports, WriteDateUseDateFormat, DisableCircularReferenceDetect, WriteNullStringAsEmpty, WriteNullNumberAsZero);

        //准备json到ftp上传队列
        sender.upload("/DataOut/" + fileName, json);
        log.info("fileName:{}",fileName);
        //执行上传
        try {
            sender.send();
        } catch (Exception e) {
            log.error("上传文件到ftp失败!", e);
            throw new BusinessException("系统错误,请联系管理员!", e);
        }


        log.info("违法举报复核通过:ftp上传成功");
        // uploadFile(fileName, json);

        String title = wf_car.getPlateNo() + illegalCar.getOwner();

        //获取被举报人的信息
        UserCar userCar = userServiceManager.userCarService()
                .selectByPlateNumberAndPlateType(carInfo.getPlateNumber(), carInfo.getPlateType());

        wf_info.forEach(illegalInfo->{
            String plateNo = illegalInfo.getPlateNo();
            Date time = illegalInfo.getIllegaltime();
            String roadName = illegalInfo.getRoadName();
            String illegalBehaviorName = illegalInfo.getIllegalBehaviorName();
            String enterOfficeName = illegalInfo.getEnterOfficeName();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String illegaltime = simpleDateFormat.format(time);
            //注册就发微信
            if (userCar != null) {

                //发送微信消息提醒
//            messageSenders.wechat()
//                    .to(userCar.getUserId()) //userId 就是openId
//                    .content("你的车辆被举报信息：" + title) // TODO: 2017/11/7 提示内容待修改
//                    .keyword(title)
//                    .title(title)
//                    .send();
            }

            log.info("违法举报复核通过:通知电话号码:{}", wf_car.getPhoneNo());
            //发送短信提示
            if (!StringUtils.isEmpty(wf_car.getPhoneNo())) {
//                messageSenders.sms()
//                        .to(wf_car.getPhoneNo())
//                        .content("成都交警提示：根据提供的交通违法举报线索，"+"川"+plateNo+"于"+illegaltime+"在"+roadName+"实施"+illegalBehaviorName+"，" +
//                                "经调查复核，交通违法行为事实清楚，请于15日内到"+enterOfficeName+"" +
//                                "处理或者关注成都交警微信公众号“蓉e行”平台在线接受处理。")
//                        .send();

                boolean sendSmsRet = messageSenders.sms()
                        .to(wf_car.getPhoneNo())
                        .content("成都交警提示：根据“蓉e行”交通众治联盟市民提供的交通违法举报线索，"+"川"+plateNo+"于"+illegaltime+"在"+roadName+"实施"+illegalBehaviorName+"，" +
                                "经调查复核，交通违法行为事实清楚，请于15日内到"+enterOfficeName+"" +
                                "处理或者关注成都交警微信公众号“蓉e行”平台在线接受处理。")
                        .send();

                log.info("违法举报复核通过:短信发送 {} ", sendSmsRet);

            }
        });

        log.info("违法举报复核通过:sendtoftp完毕");

    }

    private List<String> createImage(String filePrefix, List<String> fileList, FTPMessageSender sender) {
        List<String> ftpFileList = new ArrayList<>();

        for (String file : fileList) {
            String fileName = file.substring(file.lastIndexOf("/") + 1, file.length());

            File tmpFile = new File("/tmp/images/" + fileName);
            try {
                log.debug("开始下载图片文件{}到{}", file, tmpFile);
                requestBuilder.http(file)
                        .download()
                        .write(tmpFile);
                //sender.upload("/DataOut/" + filePrefix + "_" + fileName, new FileInputStream(tmpFile));
            } catch (FileNotFoundException e) {
                log.error("图片文件不存在,{}", tmpFile);
            } catch (IOException e) {
                log.error("下载图片文件失败,{}", file);
            }
//            request.upload("/DataOut/"+filePrefix+fileName,new FileInputStream(tmpFile));
            ftpFileList.add(filePrefix + "_" + fileName);
        }
        return ftpFileList;
    }

    private List<String> createImageBase64(List<String> fileList) {
        List<String> ftpFileList = new ArrayList<>();

        for (String file : fileList) {
            String fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
            File tmpFile = new File("/tmp/images/" + fileName);
            String base64;
            try {
                base64 = FileUtils.convertFileToBase64("/tmp/images/" + fileName);
            } catch (FileNotFoundException e) {
                log.error("图片文件不存在,{}", tmpFile);
                throw new BusinessException("img error");
            } catch (IOException e) {
                log.error("下载图片文件失败,{}", file);
                throw new BusinessException("img error");
            }
            ftpFileList.add(base64);
            tmpFile.delete();
        }
        return ftpFileList;
    }


    private RequestBuilder requestBuilder = new SimpleRequestBuilder();


}
