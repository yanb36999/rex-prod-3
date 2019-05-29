package com.zmcsoft.rex.tmb.illegal.impl.service;

import com.zmcsoft.rex.tmb.illegal.entity.*;
import com.zmcsoft.rex.tmb.illegal.impl.dao.CarIllegalCaseDao;
import com.zmcsoft.rex.tmb.illegal.service.DataCryptService;
import com.zmcsoft.rex.tmb.illegal.service.IllegalCaseService;
import com.zmcsoft.rex.tmb.illegal.service.NunciatorService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.Dump;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.expands.request.ftp.FtpRequest;
import org.hswebframework.ezorm.core.dsl.Query;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.hswebframework.web.service.DefaultDSLUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * @author zhouhao
 */
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.NOT_SUPPORTED)
@Service
@ConfigurationProperties(prefix = "com.zmcsoft.tmb.ftp")
@UseDataSource("tmbDataSource")
@Slf4j
//@Transactional()
public class LocalIllegalCaseService implements IllegalCaseService {

    private CarIllegalCaseDao carIllegalCaseDao;

    private RequestBuilder requestBuilder = new SimpleRequestBuilder();

    private NunciatorService nunciatorService;

    private DataCryptService dataCryptService;


    @Autowired
    public void setDataCryptService(DataCryptService dataCryptService) {
        this.dataCryptService = dataCryptService;
    }

    @Autowired(required = false)
    public void setRequestBuilder(RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    @Autowired
    public void setCarIllegalCaseDao(CarIllegalCaseDao carIllegalCaseDao) {
        this.carIllegalCaseDao = carIllegalCaseDao;
    }

    @Autowired
    public void setNunciatorService(NunciatorService nunciatorService) {
        this.nunciatorService = nunciatorService;
    }

    public CarIllegalCase applyProperties(CarIllegalCase illegalCase) {
        if (illegalCase == null) {
            return null;
        }
        illegalCase.setPlateTypeText(PlateType.codeOf(illegalCase.getPlateType()).getText());
        String co = illegalCase.getOrganization();
        NunciatorInfo nunciatorInfo = nunciatorService.getByOrgCode(co, true);
        //设置告知人1
        illegalCase.setNunciatorOne(Nunciator.builder()
                .name(nunciatorInfo.getName())
                .orgName(nunciatorInfo.getOrgName())
                .build());
        //设置告知人2
        illegalCase.setNunciatorTow(Nunciator.builder()
                .name(nunciatorInfo.getName2())
                .orgName(nunciatorInfo.getOrgName())
                .build());

        dataCryptService.decryptProperty(illegalCase::getPlateNumber, illegalCase::setPlateNumber);
        dataCryptService.decryptProperty(illegalCase::getDriverNumber, illegalCase::setDriverNumber);
        dataCryptService.decryptProperty(illegalCase::getCarDistinguishCode, illegalCase::setCarDistinguishCode);
        dataCryptService.decryptProperty(illegalCase::getParty, illegalCase::setParty);
        dataCryptService.decryptProperty(illegalCase::getFileNumber, illegalCase::setFileNumber);

        return illegalCase;
    }

    @Override
    public List<CarIllegalCase> getByUserCar(CarIllegalCaseQueryEntity car) {

        String plateNumber = car.getPlateNumber();
        Objects.requireNonNull(plateNumber);
        char c = plateNumber.charAt(0);
        if (!((c >= 0x4e00) && (c <= 0x9fbb)))
            plateNumber = "川" + plateNumber;
        return DefaultDSLQueryService.createQuery(carIllegalCaseDao)
                .sql("HPHM=" + DBFunctionDataCryptService.encryptFunctionSql, plateNumber)
//                .where("plateNumber",dataCryptService.encrypt(car.getPlateNumber()))
                .and("plateType", car.getPlateType())
                .and("handleSign", 0)
                .listNoPaging().stream()
                .peek(this::applyProperties)
                .collect(Collectors.toList());

//        return  carIllegalCaseDao.selectByMap(Maps.<String, Object>buildMap()
//                .put("plateType", car.getPlateType())
//                .put("plateNumber", car.getPlateNumber())
//                .put("handleSign", 0)
//                .get()).stream()
//                .peek(this::applyProperties)
//                .collect(Collectors.toList());
    }

    @Override
    public CarIllegalCase getById(String caseId) {
//        return  carIllegalCaseDao.selectByMap(Maps.<String, Object>buildMap()
//                .put("id", caseId)
//                .get()).stream().findAny().orElse(null);
        return applyProperties(DefaultDSLQueryService
                .createQuery(carIllegalCaseDao)
                .where("id", caseId).single());
    }

    @Override
    @Deprecated
    public boolean confirm(List<ConfirmInfo> confirmInfos) {
        StringBuilder sb = new StringBuilder();
        final String[] owner = new String[1];
        AtomicInteger handlerNum = new AtomicInteger();
        if (confirmInfos != null) {
            confirmInfos.forEach(confirmInfo -> {
                log.debug("开始上传处罚申请到ftp:{}", confirmInfo);

                CarIllegalCase info = getById(confirmInfo.getCaseId());
                if (info != null) {//查询出有数据
                    if (!"0".equals(info.getHandleSign())) {//0表示未处理的
                        log.warn("数据已经处理过:{}", info.getId());
                        return;//跳过已处理的数据
                    } else {
                        handlerNum.incrementAndGet();
                        /**
                         * 传输格式
                         * 业务ID|违法序号|号牌种类|号牌号码|身份证明种类(默认为Ａ)|身份证明号码｜档案编号｜采集机关名称|业务类型(默认为A)|
                         */
                        owner[0] = info.getDriverNumber();


                        if (!info.getOrganization().substring(0, 4).equals("5101")) {

                            info.setOrganization("510106000000");
                        }

                        sb.append(confirmInfo.getBusinessId()).append("|")          //businessId---自己生成的
                                .append(info.getIllegalNumber()).append("|")        //XH
                                .append(info.getPlateType()).append("|")            //HPZL
                                .append(info.getPlateNumber()).append("|")          //HPHM
                                .append("A").append("|")//身份证明种类,数据库无该字段    //A
                                .append(info.getDriverNumber()).append("|")         //JSZH
                                .append(info.getFileNumber()).append("|")           //DABH
//                                .append(nunciatorService.getByOrgCode(info.getOrganization(), true).getOrgCode()).append("|")
                                .append(info.getOrganization()).append("|")         //CJJG
                                .append("A\n");//业务类型默认为A
                        log.debug("报文内容:" + sb);
                    }
                }
            });
            if (handlerNum.get() > 0) {
                String fileName = "A_" + owner[0] + "_" + System.currentTimeMillis() + ".rexjtcf";
                FtpRequest ftpRequest = null;
//                try {
//                    log.debug("开始上传文件到ftp:{}\n", fileName, sb.toString());
//                    ftpRequest = requestBuilder.ftp(host, port, username, password)
//                            .setting(client -> {
//                                client.setConnectTimeout(1000);
//                                client.setDefaultTimeout(10000);
//                                client.enterLocalPassiveMode();
//                            });
//                    boolean uploadSuccess = ftpRequest.upload("/DataOut/" + fileName, new ByteArrayInputStream(sb.toString().getBytes()));
//                    log.info("上传{}到ftp结果:{}\n 内容{}", fileName, uploadSuccess,sb.toString());
//                    return uploadSuccess;
//                } catch (IOException e) {
//                    log.error("上传{}到ftp失败", e);
//                    throw new RuntimeException(e);
//                } finally {
//                    if (null != ftpRequest) {
//                        try {
//                            ftpRequest.logout();
//                        } catch (IOException e) {
//                            log.error("登出ftp失败", e);
//                        }
//                    }
//                }
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public CarIllegalData getDataByUserCar(CarIllegalCaseQueryEntity car) {
        //CJBJ  0:未处理 1:已处理
        //JKBJ  0:已交款 1:未交款

        //总笔数
        Query<CarIllegalCase, QueryParamEntity> entityQuery = DefaultDSLQueryService.createQuery(carIllegalCaseDao)
                .where("plateType", car.getPlateType())
                .and("plateNumber", car.getPlateNumber());


        //未处理笔数
        int noDispose = DefaultDSLQueryService.createQuery(carIllegalCaseDao)
                .where("plateType", car.getPlateType())
                .and("plateNumber", car.getPlateNumber())
                .and("handleSign", 0)
                .total();
        //已处理笔数
        int dispose = DefaultDSLQueryService.createQuery(carIllegalCaseDao)
                .where("plateType", car.getPlateType())
                .and("plateNumber", car.getPlateNumber())
                .and("handleSign", 1).and("paySign", 1)
                .total();
        //已处理未交款
        int disposeNoPay = DefaultDSLQueryService.createQuery(carIllegalCaseDao)
                .where("plateType", car.getPlateType())
                .and("plateNumber", car.getPlateNumber())
                .and("handleSign", 1).and("paySign", 0)
                .total();

        return CarIllegalData.builder()
                .noDispose(noDispose)
                .dispose(dispose)
                .disposeNoPay(disposeNoPay).build();
    }


}
