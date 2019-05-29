package com.zmcsoft.rex.workflow.illegal.impl.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmcsoft.rex.api.user.entity.User;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.api.user.service.UserDriverLicenseService;
import com.zmcsoft.rex.api.user.service.UserServiceManager;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.workflow.illegal.api.Constants;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import com.zmcsoft.rex.workflow.illegal.api.service.CarIllegalCaseHandleService;
import com.zmcsoft.rex.workflow.illegal.api.service.IllegalCaseHistoryService;
import com.zmcsoft.rex.workflow.illegal.api.service.IllegalCodeService;
import com.zmcsoft.rex.workflow.illegal.api.service.PropertyRefactor;
import com.zmcsoft.rex.workflow.illegal.impl.dao.CarIllegalCaseHandleDao;
import com.zmcsoft.rex.workflow.illegal.impl.message.IllegalMessageSendTemplate;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.concurrent.lock.annotation.Lock;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.datasource.annotation.UseDefaultDataSource;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author zhouhao
 */
@Transactional(rollbackFor = Throwable.class)
@Service
@ConfigurationProperties(prefix = "com.zmcsoft.tmb.ftp")
@Slf4j(topic = "business.CarIllegalCaseHandle")
public class LocalCarIllegalCaseHandleService extends GenericEntityService<CarIllegalCaseHandle, String>
        implements CarIllegalCaseHandleService {

    private CarIllegalCaseHandleDao carIllegalCaseHandleDao;

    private UserServiceManager userServiceManager;

    private IllegalCaseHistoryService illegalCaseHistoryService;

    private UserDriverLicenseService userDriverLicenseService;

    private RequestBuilder requestBuilder = new SimpleRequestBuilder();

    private PropertyRefactor propertyRefactor;

    @Autowired
    private IllegalCodeService illegalCodeService;

    @Autowired
    private MessageSenders messageSenders;

    @Override
    public List<CarIllegalCaseHandle> selectByUserId(String userId, Byte... handleStatus) {
        Objects.requireNonNull(userId, "userId can not be null");
        return createQuery().where("userId", userId)
                .when(handleStatus.length > 0, query -> query.in("handleStatus", Arrays.asList(handleStatus)))
                .listNoPaging();
    }

    @Override
    public CarIllegalCaseHandle selectByCaseId(String caseId) {
        Objects.requireNonNull(caseId, "caseId can not be null");
        return createQuery().where("illegalCase.id", caseId).single();
    }

    @Override
    public UserCarIllegalDetail getUserCarIllegalDetail(String id, String userId, String plateNumber, String plateType) {
        User user = userServiceManager.userService().getById(userId);
        CarIllegalCaseHandle carIllegalCaseHandle = createQuery().where().is("id", id).single();
        UserCarIllegalDetail detail = UserCarIllegalDetail.builder()
                .user(user)
                .carIllegalCaseHandle(carIllegalCaseHandle)
                .illegalCaseHistories(illegalCaseHistoryService.selectByCaseId(id))
                .build();
        return detail;
    }

    @Override
    @Lock("illegal-case:${#caseId}")
    public boolean sign(String caseId) {
        CarIllegalCaseHandle handle = selectByCaseId(caseId);
        assertNotNull(handle);
        if (!HandleStatusDefine.NO_SIGN.eq(handle.getHandleStatus())) {
            throw new BusinessException("不能签收此案件");
        }
        createUpdate().set("handleStatus", HandleStatusDefine.NO_PAY.getCode())
                .where("id", handle.getId())
                .exec();

        IllegalCaseHistory logger = IllegalCaseHistory.builder()
                .key(Constants.LOGGER_KEY)
                .keyText(Constants.LOGGER_KEY_TEXT)
                .action("sign")
                .actionText("签收")
                .createTime(new Date())
                .caseId(handle.getId())
                .creatorId(handle.getUserId())
                .build();

        illegalCaseHistoryService.insert(logger);

        return true;
    }

    @Override
    public boolean updatePayStatus(String decisionNumber, String paySign, Byte handleStatus) {
        int i = createUpdate()
                .set("illegalCase.paySign", paySign)
                .set("handleStatus", handleStatus)
                .where("decisionNumber", decisionNumber)
                //如果更新为其他渠道缴费，增加 where handleStatus != 4 
                .when(HandleStatusDefine.SUCCESS.eq(handleStatus), update -> update.where().not("handleStatus", HandleStatusDefine.PAY.getCode()))
                //状态为支付失败,增加一个 where handleStatus = 3 条件
                .when(HandleStatusDefine.PAY_FAIL.eq(handleStatus), update -> update.where("handleStatus", HandleStatusDefine.NO_PAY.getCode()))
                .exec();

        return i == 1;
    }

    @Override
    public List<CarIllegalCaseHandle> getByHandleStatus(Byte handleStatus) {
        return createQuery().where("handleStatus", handleStatus).list();
    }

    @Override
    public List<CarIllegalCaseHandle> getByIllegalXhs(List<String> xhs) {
        return createQuery().in("illegalCase.id", xhs).listNoPaging();
    }

    @Override
    public CarIllegalCaseHandle getLicenseScore(String userId) {
        return createQuery().where("userId", userId).orderByDesc("updateTime").single();
    }

    @Override
    public boolean afreshCommit(String caseId) {
        int exec = createUpdate()
                .set("handleStatus", HandleStatusDefine.NEW.getCode())
                .where("illegalCase.id", caseId)
                .exec();

        log.info("交管处理失败数据，重新处理{}条", exec);
        return exec > 0 ? true : false;
    }

    @Override
    public CarIllegalCaseHandle getByDecisionNo(String decisionNo) {
        return createQuery().sql("substr(decision_number,1,15)=?", decisionNo).single();
//        return createQuery().like("decisionNumber",decisionNo+"%").single();
    }

    @Override
    public PenaltyDecision selectPenaltyByCaseId(String caseId) {
        CarIllegalCaseHandle caseHandle = selectByCaseId(caseId);
        if (null == caseHandle) {
            return null;
        }
        propertyRefactor.applyCaseText(caseHandle.getIllegalCase());

        UserDriverLicense license = userDriverLicenseService.getByUserId(caseHandle.getUserId());

        propertyRefactor.applyLicenseText(license);
//        caseHandle.setIllegalCase(null);
        return PenaltyDecision.builder()
                .carIllegalCase(caseHandle.getIllegalCase())
                .illegalCaseHandle(caseHandle)
                .driverLicense(license)
                .build();
    }


    private final AtomicLong counter = new AtomicLong();

    @Scheduled(cron = "0/50 * * * * ?")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @UseDefaultDataSource
    @Lock("autoSyncHandleFromFtp")
    public void autoSyncHandleFromFtp() {

        //车网通提供的扣分接口
        // String deductScoreUrl = "http://cdjg.gov.cn:8090/WeChat/system/submit!updateLjjf.action";

        String deductScoreUrl = "http://178.16.13.73:8090/WeChat/system/submit!updateLjjf.action";
        try {
            String path = "/DataIn2/";
            if (counter.incrementAndGet() == 600) {
                //10分钟 记录一次日志
                log.info("开始扫描文件 *.rexjtcf");
                counter.set(0);
            }
            messageSenders
                    .ftp()
                    .list(path, fileName -> {
                        ByteArrayOutputStream data = new ByteArrayOutputStream();
                        try {
                            if (fileName.getName().endsWith(".rexjtcf")) {
                                log.info("从ftp下载文件{}", fileName.toFormattedString());
                                //下载文件
                                messageSenders.ftp().download(path + fileName.getName(), data).send();

                                //ftp.download(path + fileName.getName(), data);
                                byte[] da = data.toByteArray();
                                String str = new String(da, "UTF-8");

                                String localFile = "/data/rex-dispose/" + fileName.getName();

                                //将文件写入到本地
                                OutputStream out = new FileOutputStream(localFile);
                                out.write(da);
                                out.flush();
                                out.close();
                                log.info("从ftp下载文件{}到{}", fileName.getName(), localFile + " 成功!");
                                log.info("获取{}报文:\n{}", fileName.getName(), str);
                                if (str != null && !str.isEmpty() && !str.equals("")) {

                                    //处理文本
                                    Arrays.stream(str.split("\n"))
                                            .map(result -> result.split("[|]"))
                                            .forEach(arr -> {
                                                BiFunction<Integer, String, String> function = (index, defaultValue) -> {
                                                    if (arr.length > index) {
                                                        return arr[index];
                                                    }
                                                    return defaultValue;
                                                };
                                                String caseId = arr[2];
                                                String status = arr[3];


                                                //查询
                                                CarIllegalCaseHandle result = createQuery()
                                                        .where("illegalCase.id", caseId != null ? caseId : "")
                                                        .single();


                                                if (result == null) {
                                                    log.warn("fpt文件{}报文中 案件id {} 不存在!", fileName.getName(), caseId);
                                                    return;
                                                }

                                                //判断是否已经处理过
                                                /**
                                                 if (!HandleStatusDefine.REQUEST.eq(result.getHandleStatus())) {
                                                 log.warn("fpt文件{}报文中 案件id:{} 已处理", fileName.getName(), caseId);
                                                 return;
                                                 }
                                                 */
                                                int total = 0;

                                                Byte preStatus = result.getHandleStatus();
                                                //只有已申请、申请失败和系统异常情况下，内网数据可以重新覆盖我们的状态位
                                                if (HandleStatusDefine.REQUEST.eq(preStatus)
                                                        || HandleStatusDefine.REQUEST_FAIL.eq(preStatus)
                                                        || HandleStatusDefine.REQUEST_FAIL_RETRY.eq(preStatus)) {

                                                    if (("1").equals(status)) {

                                                        CarIllegalCaseHandle caseHandle = createQuery().where("illegalCase.id", arr[2]).single();
                                                        BigDecimal payMoney = caseHandle.getIllegalCase().getPayMoney();
                                                        Integer score = caseHandle.getIllegalCase().getScore();
                                                        BigDecimal sign = BigDecimal.valueOf(0);

                                                        if (payMoney.compareTo(sign) == 0 && score.equals(0)) {
//修改状态
                                                            //判断如果该笔违章是0分0元，则直接修改为处理完成
                                                            //TODO 核实前端状态5有没有取。
                                                            total = createUpdate()
                                                                    .set("handleStatus", HandleStatusDefine.PAY.getCode())
                                                                    .set("decisionNumber", function.apply(1, ""))
                                                                    .set("execResult", function.apply(3, ""))
                                                                    .set("remark", function.apply(4, ""))
                                                                    .set("handler", function.apply(5, ""))
                                                                    .set("disposeOffice", function.apply(7, ""))
                                                                    .set("againOffice", function.apply(8, ""))
                                                                    .set("fileContent", String.join("|", arr))
                                                                    .set("fileName", fileName.getName())
                                                                    .set("handleTime", new Date())
                                                                    .set("updateTime", new Date())
                                                                    .where("illegalCase.id", arr[2])
                                                                    .exec();

                                                        } else {

                                                            //修改状态
                                                            total = createUpdate()
                                                                    .set("handleStatus", HandleStatusDefine.NO_PAY.getCode())
                                                                    .set("decisionNumber", function.apply(1, ""))
                                                                    .set("execResult", function.apply(3, ""))
                                                                    .set("remark", function.apply(4, ""))
                                                                    .set("handler", function.apply(5, ""))
                                                                    .set("disposeOffice", function.apply(7, ""))
                                                                    .set("againOffice", function.apply(8, ""))
                                                                    .set("fileContent", String.join("|", arr))
                                                                    .set("licenseScore", Integer.parseInt(function.apply(9, "0")))
                                                                    .set("fileName", fileName.getName())
                                                                    .set("handleTime", new Date())
                                                                    .set("updateTime", new Date())
                                                                    .where("illegalCase.id", arr[2])
                                                                    .exec();


                                                            //车网通分数扣除接口
                                                            try {
                                                                String deductResult = requestBuilder.http(deductScoreUrl)
                                                                        .param("zjbh", function.apply(10, ""))//驾驶证号
                                                                        .param("ljjf", function.apply(9, ""))//累计记分
                                                                        .post()
                                                                        .asString();
                                                                JSONObject jsonObject = JSON.parseObject(deductResult);
                                                                Boolean success = (Boolean) jsonObject.get("success");
                                                                if (success) {
                                                                    log.info("电子眼-该笔违章分数扣除成功");
                                                                }
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }


                                                    } else if (("-1").equals(status)) {
                                                        total = createUpdate()
                                                                .set("handleStatus", HandleStatusDefine.REQUEST_FAIL_RETRY.getCode())
                                                                .set("execResult", function.apply(3, ""))
                                                                .set("remark", function.apply(4, ""))
                                                                .set("fileContent", String.join("|", arr))
                                                                .set("fileName", fileName.getName())
                                                                .set("handleTime", new Date())
                                                                .set("updateTime", new Date())
                                                                .where("illegalCase.id", arr[2])
                                                                .exec();
                                                        logger.error("系统错误{}", String.join("|", arr));
                                                    } else {
                                                        total = createUpdate()
                                                                .set("handleStatus", HandleStatusDefine.REQUEST_FAIL.getCode())
                                                                .set("execResult", function.apply(3, ""))
                                                                .set("remark", function.apply(4, ""))
                                                                .set("fileContent", String.join("|", arr))
                                                                .set("fileName", fileName.getName())
                                                                .set("handleTime", new Date())
                                                                .set("updateTime", new Date())
                                                                .where("illegalCase.id", arr[2])
                                                                .exec();
                                                        logger.error("系统错误{}", String.join("|", arr));
                                                    }
                                                } else {
                                                    log.warn("fpt文件{}报文中 案件Id{} 状态:{} 已经处理过", fileName.getName(), caseId, status);
                                                }
                                                log.warn("fpt文件{}报文中 案件id:{} 状态:{} 已修改数据数量:{} ", fileName.getName(), caseId, status, total);
                                                //记录业务日志
                                                IllegalCaseHistory logger = IllegalCaseHistory.builder()
                                                        .action("request_success")
                                                        .actionText("申请成功")
                                                        .detail("")
                                                        .comment("")
                                                        .createTime(new Date())
                                                        .caseId(arr[0])
                                                        .creatorId("-")
                                                        .creatorName("系统")
                                                        .build();

//                                            illegalCaseHistoryService.insert(logger);

                                                String title = "";
                                                List<HandleStatus> list = Arrays.stream(HandleStatusDefine.values())
                                                        .map(HandleStatusDefine::getStatus)
                                                        .collect(Collectors.toList());
                                                for (HandleStatus handleStatus : list) {
                                                    if (handleStatus.getCode() == result.getHandleStatus()) {
                                                        title = handleStatus.getText();
                                                    }
                                                }

                                                if (result != null) {
                                                    log.info("电子眼违法，推送微信信息");
                                                    Date illegalTime = result.getIllegalCase().getIllegalTime();
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    String userId = result.getUserId();
                                                    String decisionNumber = function.apply(1, "");
                                                    String illegalActivities = result.getIllegalCase().getIllegalActivities();
                                                    String illegalContent = illegalCodeService.getByCode(illegalActivities).getIllegalContent();
                                                    String illegalAddress = result.getIllegalCase().getIllegalAddress();
                                                    String time = simpleDateFormat.format(illegalTime);
                                                    String punishResult = result.getIllegalCase().getPayMoney() + "元/" + result.getIllegalCase().getScore() + "分";

                                                    String wtetle = result.getIllegalCase().getPlateNumber() + "的交通违法处理已申请成功";
                                                    messageSenders.wechat("illegal")
                                                            .template(IllegalMessageSendTemplate.builder()
                                                                    .illegalDecision(decisionNumber)
                                                                    .illegalBehavior(illegalContent)
                                                                    .illegalAddress(illegalAddress)
                                                                    .illeaglTime(time)
                                                                    .punishResult(punishResult).build().getParamMap())
                                                            .title(wtetle)
                                                            .to(userId)
                                                            .send();
                                                }
                                            });
                                } else {
                                    log.info("获取到的报文{}:为空", fileName.getName());
                                }

                                //删除ftp上面已经处理了的文本
                                boolean deleted = messageSenders.ftp().delete(path + fileName.getName()).send();
                                log.info("删除FTP文件{} : {}", path + fileName.getName(), deleted);

                            }

                        } catch (Exception e) {
                            log.error("处理ftp文件{}失败", fileName, e);
                        }
                    })
                    .send();

        } catch (Exception e) {
            log.error("定时扫描ftp文件失败:", e);
        }

    }

    @Autowired
    public void setUserDriverLicenseService(UserDriverLicenseService userDriverLicenseService) {
        this.userDriverLicenseService = userDriverLicenseService;
    }

    @Autowired
    public void setIllegalCodeDaoDao(CarIllegalCaseHandleDao carIllegalCaseHandleDao) {
        this.carIllegalCaseHandleDao = carIllegalCaseHandleDao;
    }

    @Autowired
    public void setUserServiceManager(UserServiceManager userServiceManager) {
        this.userServiceManager = userServiceManager;
    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<CarIllegalCaseHandle, String> getDao() {
        return carIllegalCaseHandleDao;
    }

    @Autowired
    public void setPropertyRefactor(PropertyRefactor propertyRefactor) {
        this.propertyRefactor = propertyRefactor;
    }

    @Autowired
    public void setIllegalCaseHistoryService(IllegalCaseHistoryService illegalCaseHistoryService) {
        this.illegalCaseHistoryService = illegalCaseHistoryService;
    }

//    @Autowired
//    public void setWechatSendLoggerService(WechatSendLoggerService wechatSendLoggerService) {
//        this.wechatSendLoggerService = wechatSendLoggerService;
//    }
}
