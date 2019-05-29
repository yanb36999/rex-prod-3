package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.workflow.illegal.api.IllegalCarStatus;
import com.zmcsoft.rex.workflow.illegal.api.ReportStatus;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import com.zmcsoft.rex.workflow.illegal.api.service.ReportIllegalCarService;
import com.zmcsoft.rex.workflow.illegal.impl.dao.ReportIllegalCarDao;
import com.zmcsoft.rex.workflow.illegal.impl.dao.ReportIllegalInfoDao;
import com.zmcsoft.rex.workflow.illegal.impl.dao.ReportInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.hswebframework.web.service.DefaultDSLUpdateService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(rollbackFor = Throwable.class)
@Slf4j(topic = "business.illegal.report.service")
public class LocalReportIllegalCarService extends GenericEntityService<ReportIllegalCar, String> implements ReportIllegalCarService {

    @Autowired
    private ReportIllegalCarDao reportIllegalCarDao;

    @Autowired
    private ReportIllegalInfoDao reportIllegalInfoDao;

    @Autowired
    private ReportInfoDao reportInfoDao;

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<ReportIllegalCar, String> getDao() {
        return reportIllegalCarDao;
    }

    @Override
    public List<ReportIllegalCar> getIllegalCarInfo(String plateNo, String plateType) {
        return DefaultDSLQueryService.createQuery(reportIllegalCarDao)
                .where("plateNo",plateNo)
                .and("plateType",plateType)
                .listNoPaging();
    }

    @Override
    public List<ReportIllegalCar> getIllegalCarInfos(String waterId,String plateNo, String plateType) {
        return DefaultDSLQueryService.createQuery(reportIllegalCarDao)
                .where("plateNo", plateNo)
                .and("plateType", plateType)
                .and("reportId", waterId)
                .listNoPaging();
    }

    @Override
    public ReportIllegalCar getIllegalCarInfo(String waterId,String plateNo, String plateType) {
        return DefaultDSLQueryService.createQuery(reportIllegalCarDao)
                .where("plateNo", plateNo)
                .and("plateType", plateType)
                .and("reportId", waterId)
                .single();
    }

    @Override
    public List<ReportIllegalCar> getIllegalCarInfoByReportId(String reportId,String plateNo, String plateType) {
        return DefaultDSLQueryService.createQuery(reportIllegalCarDao)
                .where("illegalInfoId",reportId).listNoPaging();
    }

    @Override
    public List<ReportIllegalCar> getIllegalCarInfoByStatus(String plateNo, String plateType,String status){
        return DefaultDSLQueryService.createQuery(reportIllegalCarDao).
                where("plateNo",plateNo)
                .and("plateType",plateType)
                .and("dspStatus",status)
                .listNoPaging();
    }

    @Override
    public Boolean updateConfirmDate(ReqconfrimDetail reqConfirmData) {
        List<ConfirmIllegal> confirmIllegals = reqConfirmData.getConfirmIllegals();
        //1-更新违章数据
        if (confirmIllegals!=null&&confirmIllegals.size()>0){
            confirmIllegals.forEach(illegal->{
                log.info("微信确认更新数据Id:{}",illegal.getIllegalId());
                int illegalInfo = DefaultDSLUpdateService.createUpdate(reportIllegalInfoDao)
                        .where("id", illegal.getIllegalId())
                        .set("disposeStatus", String.valueOf(HandleStatusDefine.REQUEST.getCode()))
                        .set("driverNo", reqConfirmData.getLicenseNo())
                        .set("updateTime",illegal.getConfrimDate())
                        .exec();
                log.info("微信确认，更新违章数据{}条",illegalInfo);
            });
        }
        //获取改举报流水下的这辆车的未申请违章笔数
        int unDspCount = DefaultDSLQueryService.createQuery(reportIllegalInfoDao)
                .where("reportId", reqConfirmData.getReportId())
                .and("plateNo", reqConfirmData.getPlateNo())
                .and("plateType", reqConfirmData.getPlateType())
                .and("disposeStatus",String.valueOf(HandleStatusDefine.NEW.getCode()))//未处理的
                .total();
        //如果未申请笔数为0，违法车辆的状态则更改为已处理
        if (unDspCount==0){
            //更新违法车辆数据
            DefaultDSLUpdateService.createUpdate(reportIllegalCarDao)
                    .where("id", reqConfirmData.getCarId())
                    .set("confirmTime", reqConfirmData.getConfirmTime())
                    .set("dspStatus", IllegalCarStatus.ENTER_OK_W.code())
                    .exec();
            log.info("微信确认,更新车辆{}状态为{}",reqConfirmData.getPlateNo(),IllegalCarStatus.ENTER_OK_W.code());
            //TODO：检查这辆车的状态更新了以后，是否可以把整个举报事件都更新下状态位,具体就是检查该流水下全部不包括1101 1102 3102 如果不包括切整个流水的状态
            //记得改完这里一定要把自动处理的那个也改掉!!!!
        }

        //查询这个report ID 下的车辆待处理的状态
        int carTotal = DefaultDSLQueryService.createQuery(reportIllegalCarDao)
                .where("reportId", reqConfirmData.getReportId())
                .and("dspStatus", IllegalCarStatus.CHECK_OK.code())
                .total();
        //，如果查出来为0.则表示都已经处理了。则更新该举报的状态位
        if (carTotal==0){
            DefaultDSLUpdateService.createUpdate(reportInfoDao)
                    .where("reportId",reqConfirmData.getReportId())
                    .set("dspStatus",ReportStatus.CONFIRM_Ok.code());
            log.info("举报流水号{},下的所有车辆已经确认完毕，状态更新为{}",reqConfirmData.getReportId(),ReportStatus.CONFIRM_Ok.code());
        }
        return true;
    }

    @Override
    public ReportIllegalCar getIllegalCarById(String carId) {
        return DefaultDSLQueryService.createQuery(reportIllegalCarDao)
                .where("id",carId)
                .single();
    }


}
