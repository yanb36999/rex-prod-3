package com.zmcsoft.rex.tmb.illegal.impl.service;


import com.zmcsoft.rex.tmb.illegal.entity.DriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.DriverLicenseScore;
import com.zmcsoft.rex.tmb.illegal.impl.dao.DriverLicenseDao;
import com.zmcsoft.rex.tmb.illegal.impl.dao.DriverLicenseScoreDao;
import com.zmcsoft.rex.tmb.illegal.service.DataCryptService;
import com.zmcsoft.rex.tmb.illegal.service.DriverLicenseService;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.NotFoundException;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Transactional(rollbackFor = Throwable.class)
@Service
@UseDataSource("tmbDataSource")
public class LocalDriverLicenseService implements DriverLicenseService{

    @Autowired
    private DriverLicenseScoreDao driverLicenseScoreDao;


    @Autowired
    private DriverLicenseDao driverLicenseDao;

    @Autowired
    private DataCryptService dataCryptService;


    @Transactional(readOnly = true)
    @Override
    public Integer getScore(DriverLicenseScore driverLicenseScore) {
        DriverLicenseScore score = DefaultDSLQueryService.createQuery(driverLicenseScoreDao)
                .where("fileNum", driverLicenseScore.getFileNumber())
                .and("name",driverLicenseScore.getName())
                .and("driverNumber",driverLicenseScore.getDriverNumber())
                .single();
        if (score==null){
            throw new BusinessException("驾照不存在");
        }
        return score.getScore();
    }

    @Override
    public DriverLicense driverDetail(String idCard) {
        DriverLicense license=  DefaultDSLQueryService.createQuery(driverLicenseDao)
                .sql("SFZMHM="+DBFunctionDataCryptService.encryptFunctionSql,idCard)

                .single();

        if(null==license){
            return null;
        }
        dataCryptService.decryptProperty(license::getName,license::setName);
        dataCryptService.decryptProperty(license::getIdCard,license::setIdCard);
        dataCryptService.decryptProperty(license::getFileNumber,license::setFileNumber);
        return license;
    }
}
