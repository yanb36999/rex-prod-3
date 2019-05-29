package com.zmcsoft.rex.workflow.illegal.api.service;


import com.zmcsoft.rex.workflow.illegal.api.entity.ConfirmIllegal;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReportIllegalCar;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReqConfirmData;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReqconfrimDetail;
import org.hswebframework.web.service.CrudService;

import java.util.Date;
import java.util.List;

public interface ReportIllegalCarService extends CrudService<ReportIllegalCar,String>{

    List<ReportIllegalCar> getIllegalCarInfos(String waterId,String plateNo, String plateType);


    ReportIllegalCar getIllegalCarInfo(String waterId,String plateNo, String plateType);

    List<ReportIllegalCar> getIllegalCarInfo(String plateNo, String plateType);

    List<ReportIllegalCar> getIllegalCarInfoByReportId(String reportId,String plateNo, String plateType);

    List<ReportIllegalCar> getIllegalCarInfoByStatus(String plateNo, String plateType,String status);

    Boolean updateConfirmDate(ReqconfrimDetail reqConfirmData);


    ReportIllegalCar getIllegalCarById(String carId);

}
