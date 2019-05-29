package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.alibaba.fastjson.JSONObject;
import com.zmcsoft.rex.api.user.entity.User;
import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.api.user.service.UserServiceManager;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.tmb.illegal.service.NunciatorService;
import com.zmcsoft.rex.workflow.illegal.api.filter.IllegalCaseFilter;
import com.zmcsoft.rex.workflow.illegal.api.filter.IllegalCaseFilterDispatcher;
import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCaseHistory;
import com.zmcsoft.rex.tmb.illegal.entity.*;
import com.zmcsoft.rex.tmb.illegal.service.CarInfoService;
import com.zmcsoft.rex.tmb.illegal.service.DriverLicenseService;
import com.zmcsoft.rex.tmb.illegal.service.IllegalCaseService;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import com.zmcsoft.rex.workflow.illegal.api.service.*;
import com.zmcsoft.rex.workflow.illegal.impl.dao.CarIllegalCaseHandleDao;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.expands.request.ftp.FtpRequest;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.authorization.oauth2.client.exception.OAuth2RequestException;
import org.hswebframework.web.concurrent.lock.annotation.Lock;
import org.hswebframework.web.id.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.zmcsoft.rex.workflow.illegal.api.Constants.LOGGER_KEY;
import static com.zmcsoft.rex.workflow.illegal.api.Constants.LOGGER_KEY_TEXT;

/**
 * @author zhouhao
 */
@Service
@Slf4j(topic = "business.UserCarIllegal")
//@ConfigurationProperties(prefix = "com.zmcsoft.tmb.ftp")
public class LocalUserCarIllegalService implements UserCarIllegalService {
    @Autowired
    private IllegalCaseService illegalCaseService;

    @Autowired
    private UserServiceManager userServiceManager;

    @Autowired
    private CarIllegalCaseHandleService handleService;

    @Autowired
    private MessageSenders messageSenders;

    @Autowired
    private NunciatorService nunciatorService;
//    @Autowired
//    private CarInfoService carInfoService;
//
//    @Autowired
//    private DriverLicenseService licenseService;

    @Autowired
    private IllegalCaseHistoryService illegalCaseHistoryService;

//    @Autowired
//    private IllegalCodeService illegalCodeService;

    @Autowired
    private PropertyRefactor propertyRefactor;

//    @Autowired
//    private DictService dictService;

    @Autowired
    private IllegalCaseFilterDispatcher filterDispatcher;

    private ExecutorService executorService;

    private RequestBuilder requestBuilder = new SimpleRequestBuilder();


    @SuppressWarnings("all")
    @PostConstruct
    public void init() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 15);
        }
    }

    @Autowired(required = false)
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Lock(value = "illegal_confirm:${#userId}")
    public boolean confirm(String channel, String userId, List<ConfirmRequest> confirmRequest) {
        String bussinessId = IDGenerator.MD5.generate();

        Map<String, UserCar> userCarMapping = userServiceManager
                .userCarService()
                .getByUserId(userId)
                .stream()
                .collect(Collectors.toMap(car -> car.getPlateType() + propertyRefactor.removePlateNumber(car.getPlateNumber()), Function.identity()));
        UserDriverLicense license = userServiceManager
                .userDriverLicenseService()
                .getByUserId(userId);
        log.debug("提交处罚申请:{}", confirmRequest);
        List<ConfirmInfo> confirmInfoList = new ArrayList<>();

        //用于同一批次文件名
        final String[] owner = new String[1];
        //计算处理次数
        AtomicInteger handlerNum = new AtomicInteger();
        //传输内容
        StringBuilder sb = new StringBuilder();

        confirmRequest.forEach(req -> {
            Objects.requireNonNull(req.getCaseId(), "caseId can not be null");
            Objects.requireNonNull(req.getConfirmDate(), "confirmDate can not be null");

            String caseId = req.getCaseId();
            //判断案件是否已经被处理过
            CarIllegalCaseHandle handle = handleService.selectByCaseId(caseId);
            if (handle != null) {
                log.error("案件已经提交过:{}", handle.getId());
                throw new BusinessException("案件已处理,请勿重复提交");
            }
            //从交管局获取案件
            CarIllegalCase illegalCase = illegalCaseService.getById(req.getCaseId());
            if (illegalCase == null) {
                throw new BusinessException("案件:{" + req.getCaseId() + "}不存在");
            }
            illegalCase.setDriverNumber(license.getLicenseNumber());
            illegalCase.setFileNumber(license.getFileNumber());
            illegalCase.setParty(license.getDriverName());

            handlerNum.incrementAndGet();
            owner[0] = illegalCase.getDriverNumber();
//            业务ID|违法序号|号牌种类|号牌号码|身份证明种类(默认为Ａ)|身份证明号码｜档案编号｜采集机关名称|业务类型(默认为A)|
            sb.append(bussinessId).append("|")
                    .append(illegalCase.getId() == null ? "" : illegalCase.getId()).append("|")
                    .append(illegalCase.getPlateType() == null ? "" : illegalCase.getPlateType()).append("|")
                    .append(illegalCase.getPlateNumber() == null ? "" : illegalCase.getPlateNumber()).append("|")
                    .append("A").append("|")
                    .append(illegalCase.getDriverNumber() == null ? "" : illegalCase.getDriverNumber()).append("|")
//                    .append(illegalCase.getFileNumber()==null?"":illegalCase.getFileNumber()).append("|")

                    .append(nunciatorService.getByOrgCode(illegalCase.getOrganization(), true).getOrgCode()).append("|")
//                    .append(illegalCase.getOrganization() == null ? "" : illegalCase.getOrganization()).append("|")
                    .append("A|\n");


            UserCar userCar = userCarMapping.get(illegalCase.getPlateType() + propertyRefactor.removePlateNumber(illegalCase.getPlateNumber()));
            if (null == userCar) {
                throw new BusinessException("只能确认自己的车辆违法信息");
            }
            CarIllegalCaseHandle caseHandle =
                    CarIllegalCaseHandle.builder()
                            .handleChannel(channel)
                            .handleStatus(HandleStatusDefine.REQUEST.getCode())
                            .confirmTime(req.getConfirmDate())
                            .userId(userId)
                            .driverLicense(license)
                            .illegalCase(illegalCase)
                            .carInfo(userCar)
                            .build();
            //业务ID
            caseHandle.setBusinessId(bussinessId);
            String id = handleService.insert(caseHandle);
            IllegalCaseHistory logger = IllegalCaseHistory.builder()
                    .key(LOGGER_KEY)
                    .keyText(LOGGER_KEY_TEXT)
                    .action("request")
                    .actionText("提交处理申请")
                    .comment("用户发起违法处理申请")
                    .createTime(new Date())
                    .creatorId(userId)
                    .caseId(id)
                    .detail("")
                    .build();
            illegalCaseHistoryService.insert(logger);
            confirmInfoList.add(ConfirmInfo.builder()
                    .caseId(caseId)
                    .businessId(id)
                    .confirmDate(req.getConfirmDate())
                    .build());
        });

        log.info("电子眼提交的报文：" + sb);
        if (handlerNum.get() > 0) {
            String fileName = "A_" + owner[0] + "_" + System.currentTimeMillis() + ".rexjtcf";
            try {
                log.info("开始上传{}文件到ftp、内容:{}\n", fileName, sb.toString());
                //将文件写入到本地
//                OutputStream out = new FileOutputStream(new File("/data/rex-commit/" + fileName));
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/data/rex-commit/" + fileName)));
                bw.write(sb.toString());
                bw.flush();
                boolean uploadSuccess = messageSenders.ftp().upload("/DataOut/" + fileName, new ByteArrayInputStream(sb.toString().getBytes()))
                        .send();
                log.info("上传{}到ftp结果:{}", fileName, uploadSuccess);
                return uploadSuccess;
            } catch (IOException e) {
                log.error("上传{}到ftp失败",fileName, e);
                throw new RuntimeException(e);
            }
        }

        //向交管局发起确认请求
//        try {
//            boolean success = illegalCaseService.confirm(confirmInfoList);
//            if (!success) {
//                throw new BusinessException("提交数据失败", new RuntimeException("向交管局提交确认请求失败"));
//            }
//        } catch (Exception e) {
//            throw new BusinessException("提交数据失败", e);
//        }
        return true;
    }


    /**
     * 根据用户id获取用户车辆全部违法信息
     *
     * @param userId 用户id
     * @return 1.0
     */
    @Override
    public UserCarIllegalInfo getByUserId(String userId) {

        List<UserCar> userCars = userServiceManager
                .userCarService()
                .getByUserId(userId);

        log.info("userCars:{}", JSONObject.toJSONString(userCars));

        List<CarIllegalCaseDetail> list = new ArrayList<>();
        List<CountDownLatch> countDownLatches = new ArrayList<>();
        UserCarIllegalInfo info = UserCarIllegalInfo.builder()
                .build();

        AtomicInteger errorCount = new AtomicInteger(0);

        List<CarIllegalCaseHandle> handleList = handleService
                .selectByUserId(userId, HandleStatus.allCode());

        log.info("handleList:{}", JSONObject.toJSONString(handleList));

        //查询出已经存到蓉e行数据库到案件处理数据，并以车牌号进行分组
        Map<String, List<CarIllegalCaseHandle>> cache = handleList.stream()
                .collect(Collectors.groupingBy(handle -> propertyRefactor.applyPlateNumber(handle.getCarInfo().getPlateNumber())));

        Map<String, CarIllegalCaseHandle> idCache = handleList.stream()
                .collect(Collectors.toMap(handle -> handle.getIllegalCase().getId(), Function.identity()));


        userCars.forEach(userCar -> {
            propertyRefactor.applyCarText(userCar);

            List<CarIllegalCase> allCase = new ArrayList<>();
            CarIllegalCaseDetail carIllegalCaseDetail = new CarIllegalCaseDetail();
            carIllegalCaseDetail.setCar(userCar);
            carIllegalCaseDetail.setIllegalCaseList(allCase);
            list.add(carIllegalCaseDetail);

            CountDownLatch downLatch = new CountDownLatch(3);
            countDownLatches.add(downLatch);

            List<HandleStatus.HandleStatusCount> statusCounts = new ArrayList<>();

            carIllegalCaseDetail.setHandleStatus(statusCounts);
            //从交管局获取驾照的完整信息
            executorService.execute(() -> {
                try {
                    User user = userServiceManager.userService().getById(userId);
                    log.info("user:{}", JSONObject.toJSONString(user));
                    info.setUser(user);
                    UserDriverLicense userDriverLicense = userServiceManager
                            .userDriverLicenseService()
                            .getByUserId(userId);
                    if (userDriverLicense != null) {
                        propertyRefactor.applyLicenseText(userDriverLicense);
                        info.setDriverLicense(userDriverLicense);
                    }

                } catch (OAuth2RequestException e) {
                    errorCount.incrementAndGet();
                    log.error("从交管局获取驾照的完整信息失败:" + e.getResponse().asString(), e);
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    log.error("从交管局获取驾照的完整信息失败", e);
                } finally {
                    downLatch.countDown();
                }
            });
            //从交管局获取未处理的违法案件
            executorService.execute(() -> {
                try {
                    CarIllegalCaseQueryEntity queryEntity = CarIllegalCaseQueryEntity.builder()
                            .plateNumber(userCar.getPlateNumber())
                            .newIllegal(true)
                            .plateType(userCar.getPlateType())
                            .build();
                    AtomicInteger count = new AtomicInteger();


                    List<CarIllegalCase> noHandleCase = illegalCaseService
                            .getByUserCar(queryEntity)
                            .stream()
                            .filter(illegalCase -> {
                                boolean handled = idCache.containsKey(illegalCase.getId());
                                if (!handled) {
                                    illegalCase.setBusinessStatus(HandleStatusDefine.NEW.getCode());
                                    count.incrementAndGet();
                                }
                                return !handled;
                            }).collect(Collectors.toList());

                    allCase.addAll(noHandleCase);
                } catch (OAuth2RequestException e) {
                    errorCount.incrementAndGet();
                    log.error("从交管局获取驾照的完整信息失败:{}", e.getResponse().asString(), e);
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    log.error("从交管局获取未处理的违法案件失败:{}", e.getMessage(), e);
                } finally {
                    downLatch.countDown();
                }
            });
            //获取本地的案件处理记录
            executorService.execute(() -> {
                try {
                    //从缓存中获取
                    List<CarIllegalCaseHandle> handles =
                            cache.computeIfAbsent(propertyRefactor.applyPlateNumber(userCar.getPlateNumber()), plateNumber -> new ArrayList<>());
//                    AtomicInteger noPay = new AtomicInteger();
//                    AtomicInteger disPos = new AtomicInteger();

                    allCase.addAll(handles.stream()
                            .map(handle -> {
//                                if (HandleStatusDefine.NO_PAY.eq(handle.getHandleStatus())) {
//                                    noPay.incrementAndGet();
//                                }
//                                if (HandleStatusDefine.PAY.eq(handle.getHandleStatus())) {
//                                    disPos.incrementAndGet();
//                                }
                                CarIllegalCase carIllegalCase = handle.getIllegalCase();
                                carIllegalCase.setBusinessStatus(handle.getHandleStatus());
                                carIllegalCase.setDecisionNumber(handle.getDecisionNumber());
                                carIllegalCase.setHandlerTime(handle.getHandleTime());
                                carIllegalCase.setRemark(handle.getRemark());
                                //carIllegalCase.setPlateTypeText(PlateType.codeOf(carIllegalCase.getPlateType()).getText());
                                //applyCaseText(carIllegalCase);
                                return carIllegalCase;
                            })
                            .collect(Collectors.toList()));
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    log.error("获取本地的案件处理记录失败", e);
                } finally {
                    downLatch.countDown();
                }
            });
        });

        //等待线程全部结束
        countDownLatches.forEach(countDownLatch -> {
            try {
                countDownLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new BusinessException("服务器繁忙", e);
            }
        });
        if (errorCount.get() > 0) {
            throw new BusinessException("服务器繁忙");
        }

        //处理重复的数据
        //主要为: 提交给交管局，但是交管局未及时更新，再次到交管局查询的话还会存在
        list.forEach(detail -> {

            List<HandleStatus.HandleStatusCount> statusCounts = new ArrayList<>();

            List<CarIllegalCase> newList =
                    detail.getIllegalCaseList()
                            .stream()
                            .sorted(Comparator.comparing(CarIllegalCase::getUpdateTime))
                            //按id分组
                            .collect(Collectors.groupingBy(CarIllegalCase::getId))
                            .entrySet()
                            .stream()
                            .map(entry -> {
                                List<CarIllegalCase> lst = entry.getValue()
                                        .stream()
                                        .filter(illegalCase -> {
                                            // if (!HandleStatusDefine.NEW.eq(illegalCase.getBusinessStatus())) {
                                            //      return true;
                                            //    }
                                            //通过过滤来判断哪些案件需要处罚
                                            IllegalCaseFilter.FilterResult result = filterDispatcher
                                                    .doFilter(detail.getCar(), info.getDriverLicense(), illegalCase);
                                            if (!result.isPass()) {
                                                log.debug("获取到不能处理到案件:{}({} {})", illegalCase.getId(), result.getCode(), result.getReason());
                                                illegalCase.setBusinessStatus(HandleStatusDefine.CANT_REQUEST.getCode());

                                                illegalCase.setRemark(result.getReason());
                                            }
                                            return true;
                                        })
                                        .peek(propertyRefactor::applyCaseText)
                                        .collect(Collectors.toList());

                                return lst;
                            })
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());


            Map<Byte, Long> statusCount = newList.stream()
                                        .collect(Collectors.groupingBy(CarIllegalCase::getBusinessStatus, Collectors.counting()));

                                //设置处理数量
                                for (Byte aByte : HandleStatus.allCode()) {
                                    int total = statusCount.getOrDefault(aByte, 0L).intValue();
                                    statusCounts.add(HandleStatus.ofCount(aByte, total));
                                }
            detail.setHandleStatus(statusCounts);
            detail.setIllegalCaseList(newList);
        });

        info.setCaseDetailList(list);

        return info;
    }
}
