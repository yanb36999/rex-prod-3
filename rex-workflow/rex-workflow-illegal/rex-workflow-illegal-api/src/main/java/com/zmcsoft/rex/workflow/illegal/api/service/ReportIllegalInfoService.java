package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.tmb.illegal.entity.DriverLicense;
import com.zmcsoft.rex.workflow.illegal.api.entity.CarIllegalCaseHandle;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReportIllegalInfo;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReqConfirmData;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.service.CrudService;

import java.util.List;

public interface ReportIllegalInfoService extends CrudService<ReportIllegalInfo,String> {

    List<ReportIllegalInfo> getIllegalInfo(String plateNo, String plateType);

    public List<ReportIllegalInfo> getIllegalInfoHistory(String plateNo, String plateType);

    List<ReportIllegalInfo> getIllegalInfo(String reportId,String plateNo, String plateType);

    boolean updateStatusByCarInfo(String reportId,String plateNo,String plateType,String licenceNo);

    boolean sign(String illegalId);

    boolean updatePayStatusByDecisionNo(String decisionNumber,String handleStatus);

    void fileData(ReqConfirmData reqConfirmData, Authentication authentication);

    CarIllegalCaseHandle getDriverLicense(String userId);
}
