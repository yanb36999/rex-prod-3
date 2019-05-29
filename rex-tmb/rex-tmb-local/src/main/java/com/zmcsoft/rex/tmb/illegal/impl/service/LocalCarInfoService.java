package com.zmcsoft.rex.tmb.illegal.impl.service;

import com.zmcsoft.rex.tmb.illegal.entity.CarInfo;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoDetailRequest;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoRequest;
import com.zmcsoft.rex.tmb.illegal.impl.dao.CarInfoDao;
import com.zmcsoft.rex.tmb.illegal.service.CarInfoService;
import com.zmcsoft.rex.tmb.illegal.service.DataCryptService;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Throwable.class,propagation = Propagation.NOT_SUPPORTED)
@Service
@UseDataSource("tmbDataSource")
public class LocalCarInfoService implements CarInfoService{

    @Autowired
    private CarInfoDao carInfoDao;

    @Autowired
    private DataCryptService dataCryptService;


    private CarInfo applyProperties(CarInfo  carInfo){

        if(carInfo==null){
            return null;
        }

        dataCryptService.decryptProperty(carInfo::getOwner,carInfo::setOwner);
        dataCryptService.decryptProperty(carInfo::getIdCard,carInfo::setIdCard);
        dataCryptService.decryptProperty(carInfo::getPlateNumber,carInfo::setPlateNumber);
        dataCryptService.decryptProperty(carInfo::getPlateNumber2,carInfo::setPlateNumber2);
        dataCryptService.decryptProperty(carInfo::getVin,carInfo::setVin);


        return carInfo;

    }
    /**
     * 根据车辆所有者信息查询所有车辆信息
     * @param carInfoRequest
     * @return
     */
    @Override
    public List<CarInfo> getCarInfo(CarInfoRequest carInfoRequest) {

        return DefaultDSLQueryService.createQuery(carInfoDao)
                .where("owner", dataCryptService.encrypt(carInfoRequest.getOwner()))
                .and("idCard", dataCryptService.encrypt(carInfoRequest.getIdCard()))
                .listNoPaging().stream()
                .peek(this::applyProperties)
                .collect(Collectors.toList());
    }

    @Override
    public CarInfo carInfoDetail(CarInfoDetailRequest carInfoDetailRequest){

//        return carInfoDao.selectListByMap(Maps.<String, Object>buildMap()
//                .put("plateType", carInfoDetailRequest.getPlateType())
//                .put("plateNumber", carInfoDetailRequest.getPlateNumber())
//                .get()).stream().findAny().orElse(null);

        String plateNumber =carInfoDetailRequest.getPlateNumber();
        if(plateNumber.length()>6){
            plateNumber=plateNumber.substring(1,plateNumber.length());
        }
//        plateNumber=plateNumber.replace("川","");

        return applyProperties( DefaultDSLQueryService.createQuery(carInfoDao)
                .where("plateType",carInfoDetailRequest.getPlateType())
                .sql("HPHM="+DBFunctionDataCryptService.encryptFunctionSql,plateNumber)
//                .and("plateNumber",dataCryptService.encrypt(carInfoDetailRequest.getPlateNumber()))
                .single());
    }
}
