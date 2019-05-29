package com.zmcsoft.rex.workflow.illegal.controller;

import com.alibaba.fastjson.JSONObject;
import com.zmcsoft.rex.commons.district.api.entity.Road;
import com.zmcsoft.rex.commons.district.api.entity.RoadSeg;
import com.zmcsoft.rex.commons.district.api.service.RoadSegService;
import com.zmcsoft.rex.commons.district.api.service.RoadService;
import com.zmcsoft.rex.workflow.illegal.api.IllegalCarStatus;
import com.zmcsoft.rex.workflow.illegal.api.ReportStatus;
import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalReportLog;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReportIllegalDetail;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReportIllegalInfo;
import com.zmcsoft.rex.workflow.illegal.api.service.DictService;
import com.zmcsoft.rex.workflow.illegal.api.service.PropertyRefactor;
import com.zmcsoft.rex.workflow.illegal.api.service.ReportIllegalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.NotFoundException;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.Permission;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.exception.UnAuthorizedException;
import org.hswebframework.web.commons.entity.PagerResult;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.entity.organizational.OrganizationalEntity;
import org.hswebframework.web.organizational.authorization.PersonnelAuthorization;
import org.hswebframework.web.service.organizational.DistrictService;
import org.hswebframework.web.service.organizational.OrganizationalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/illegal/report")
@Api(tags = "违法举报信息")
@Authorize(permission = "illegal-report")
@Slf4j(topic = "business.illegal-report")
public class ReportIllegalDetailController {

    @Autowired
    private ReportIllegalService illegalService;

    @Autowired
    private OrganizationalService organizationalService;

    @Autowired
    private PropertyRefactor propertyRefactor;

    @Autowired
    private DictService dictService;

    @Autowired
    private RoadSegService roadSegService;

    @Autowired
    private RoadService roadService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private Environment env;

    private boolean isProd = false;

    @PostConstruct
    public void init() {
        isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    @GetMapping("/checked")
    @ApiOperation("查询已处理的举报信息")
    @Authorize(action = Permission.ACTION_QUERY)
    public ResponseMessage<PagerResult<ReportIllegalDetail>> getCheckedReportList(QueryParamEntity entity) {

        PagerResult<ReportIllegalDetail> detailPagerResult = illegalService
                .getReport(getLoginUserOrgCodes(), entity);

        detailPagerResult.getData().forEach(this::applyText);

        return ResponseMessage.ok(detailPagerResult);
    }

    @GetMapping("/checked/{id}/{plateType}/{plateNumber}")
    @ApiOperation("查询已处理的举报详情")
    @Authorize(action = Permission.ACTION_QUERY)
    public ResponseMessage<ReportIllegalDetail> getCheckedReportDetail(@PathVariable String id
            , @PathVariable String plateType, @PathVariable String plateNumber) {
        // plateNumber=plateNumber.replace("川","");


        return ResponseMessage.ok(applyText(illegalService.getReportDetail(id, plateType, plateNumber)));
    }


    public List<String> getLoginUserOrgCodes() {
        PersonnelAuthorization personnelAuthorization = PersonnelAuthorization.current().orElse(null);
        List<String> orgCode = null;

        if (personnelAuthorization != null) {
            orgCode = personnelAuthorization.getAllOrgId().parallelStream()
                    .map(organizationalService::selectByPk)
                    .filter(Objects::nonNull)
                    .map(OrganizationalEntity::getCode)
                    .collect(Collectors.toList());
        }
        return orgCode;
    }

    @GetMapping("/new")
    @ApiOperation("查询未处理的举报信息")
    @Authorize(action = Permission.ACTION_QUERY)
    public ResponseMessage<PagerResult<ReportIllegalDetail>> getNewReportList(QueryParamEntity entity) {

        PagerResult<ReportIllegalDetail> detailPagerResult = illegalService
                .getNewReport(getLoginUserOrgCodes(), entity);

        detailPagerResult.getData().forEach(this::applyText);

        return ResponseMessage.ok(detailPagerResult);
    }

    @GetMapping("/new/{id}/{plateType}/{plateNumber}")
    @ApiOperation("查询未处理的举报详情")
    @Authorize(action = Permission.ACTION_QUERY)
    public ResponseMessage<ReportIllegalDetail> getNewReportDetail(@PathVariable String id
            , @PathVariable String plateType, @PathVariable String plateNumber) {
        return ResponseMessage.ok(applyText(illegalService.getNewReportDetail(id, plateType, plateNumber)));
    }

    public ReportIllegalDetail applyText(ReportIllegalDetail detail) {

        propertyRefactor.applyDict("plate-type", detail.getIllegalCar()::getPlateType, detail.getIllegalCar()::setPlateTypeText);
        propertyRefactor.applyPlateNumber(detail.getIllegalCar()::getPlateNo, detail.getIllegalCar()::setPlateNo);

        propertyRefactor.applyDict("report-type", detail.getReportInfo()::getReportType, detail.getReportInfo()::setReportType, ",");

        String newEnergy = detail.getIllegalCar().getNewEnergy();
        detail.getIllegalCar().setNewEnergy("1".equals(newEnergy) ? "是" : "否");
        return detail;
    }

    @GetMapping("/new/{id}/logs")
    @ApiOperation("查询未处理的举报日志")
    @Authorize(action = Permission.ACTION_QUERY)
    public ResponseMessage<List<IllegalReportLog>> getCheckBeforeLogs(@PathVariable String id, @PathVariable String plateType, @PathVariable String plateNumber) {

        return ResponseMessage.ok(illegalService.getCheckBeforeLogs(id));
    }

    @PostMapping("/check/{id}/{plateType}/{plateNumber}/ok")
    @ApiOperation("复核通过")
    @Authorize(action = "check")
    public ResponseMessage<Boolean> checkOk(@PathVariable String id,
                                            @PathVariable String plateType,
                                            @PathVariable String plateNumber,
                                            @RequestBody ReportIllegalDetail detail) {
        log.info("check ok start!id:{},plateNumber:{},plateType:{},detail:{}",id,plateNumber,plateType, JSONObject.toJSON(detail));

        List<ReportIllegalInfo> illegalInfoList = detail.getIllegalInfo();

        //提交日志
        IllegalReportLog reportLog = IllegalReportLog.builder()
                .reportId(id)
                .handleTime(new Date())
                .status(1).build();


        ReportIllegalDetail old = buildDetail(id, plateType, plateNumber, reportLog);

        for (ReportIllegalInfo illegalInfo : illegalInfoList) {

            //提交时 去掉川字
            illegalInfo.setPlateNo(propertyRefactor.removePlateNumber(old.getIllegalCar().getPlateNo()));
            illegalInfo.setPlateType(old.getIllegalCar().getPlateType());

            String illegalAddress = illegalInfo.getIllegalAddress();

            Road road = roadService.selectByCode(illegalAddress);
            if (null != road) {
                illegalInfo.setIllegalSite(road.getName());
            }

            RoadSeg seg = roadSegService.selectByCode(illegalInfo.getRoadCode());
            if (null != seg) {
                illegalInfo.setRoadName(seg.getName());
            } else if (null != road) {
                illegalInfo.setRoadName(road.getName());
            }
        }
        if (detail.getIllegalCar() != null) {
            old.getIllegalCar().setVideoShotPath(detail.getIllegalCar().getVideoShotPath());
        }
//        old.getIllegalCar().setPenalizeResult("1");
        old.getIllegalCar().setSignReviewStatus(IllegalCarStatus.ENTER_NO.code());
        old.getIllegalCar().setDspStatus(IllegalCarStatus.ENTER_NO.code());
        old.getReportInfo().setDspStatus(ReportStatus.CHECk_OK.code());//2017-11-9 修改录入状态，原录入状态为IllegalCarStatus.ENTER_NO.code();
        old.getIllegalCar().setIllegalCount(detail.getIllegalInfo().size());
        old.setIllegalInfo(illegalInfoList);

        log.info("check ok cost ");
        long start = System.currentTimeMillis()/1000;
        illegalService.checkReport(id, old);
        long end = System.currentTimeMillis()/1000;
        log.info("check ok cost:{}",end-start);
        //修改车网通数据状态，这里没有使用jta事务，所以如果失败应该记录业务日志
        if (isProd) {
            String plateNo =propertyRefactor.applyPlateNumber(old.getIllegalCar().getPlateNo());

            try {
                illegalService.updateNewReportStatus(id, old.getIllegalCar().getPlateType(), plateNo, "");
                log.info("修改车网通违法举报: 案件{}, 车辆{}-{} 状态:{} 成功", id, old.getIllegalCar().getPlateType(), plateNo, "");

            } catch (Exception e) {
                log.error("修改车网通违法举报: 案件{}, 车辆{}-{} 状态:{}失败", id, old.getIllegalCar().getPlateType(), plateNo, "", e);
            }
        }
        log.info("check ok end!");
        return ResponseMessage.ok();
    }

    public ReportIllegalDetail buildDetail(String id, String plateType, String plateNumber, IllegalReportLog log) {
        ReportIllegalDetail old = illegalService.getNewReportDetail(id, plateType, plateNumber);
        if (old == null) {
            throw new BusinessException("举报信息不存在或者已经复核");
        }
        String plateNo = old.getIllegalCar().getPlateNo();

        if (!plateNo.substring(0,2).equals("川A")){
            throw new BusinessException("该车辆不是成都籍");
        }
        Authentication authentication = Authentication.current().orElseThrow(UnAuthorizedException::new);

        log.setHandlerId(authentication.getUser().getId());

        log.setHandlerName(authentication.getUser().getName());


        PersonnelAuthorization personnelAuthorization = PersonnelAuthorization.current().orElse(null);
        if (personnelAuthorization != null) {
            OrganizationalEntity org = organizationalService.selectByPk(personnelAuthorization.getRootOrgId().iterator().next());
            old.getIllegalCar().setSignDept(org.getCode());
            old.getIllegalCar().setSignDeptName(org.getName());
            old.getIllegalCar().setSignReviewerName(authentication.getUser().getName());
            old.getIllegalCar().setSignReviewer(authentication.getUser().getId());
            log.setHandlerId(authentication.getUser().getId());
            log.setHandlerName(authentication.getUser().getName());
            log.setOrgCode(org.getCode());
            log.setOrgName(org.getName());
        } else {
            throw new BusinessException("当前用户未关联机构信息,无法处理此业务!");
        }

        old.getIllegalCar().setPlateNo(propertyRefactor.removePlateNumber(old.getIllegalCar().getPlateNo()));

//        old.getIllegalCar().setCreateTime(new Date());

        //复核前的日志是从车网通获取的。因此将旧的日志一起保存到蓉e行数据库
        old.getReportLogs().add(log);

        return old;
    }

    @PostMapping("/check/{id}/{plateType}/{plateNumber}/fail")
    @ApiOperation("复核不通过")
    @Authorize(action = "check")
    public ResponseMessage<Boolean> checkFail(@PathVariable String id,
                                              @PathVariable String plateType,
                                              @PathVariable String plateNumber,
                                              @RequestBody List<String> reasons) {
        log.info("check fail start!id:{},plateNumber:{},plateType:{},detail:{}",id,plateNumber,plateType, JSONObject.toJSON(reasons));
        String codesString = reasons.stream()
                .reduce((s, s2) -> String.join(",", s, s2))
                .orElse("");

        String reasonString = reasons.stream().map(code ->
                dictService.getString("check-fail-reason", code, code))
                .reduce((s, s2) -> String.join(",", s, s2))
                .orElse("");
        //提交日志
        IllegalReportLog reportLog = IllegalReportLog.builder()
                .reportId(id)
                .handleTime(new Date())
                .reasonCode(codesString)
                .reason(reasonString)
                .status(0).build();

        ReportIllegalDetail old = buildDetail(id, plateType, plateNumber, reportLog);

        old.getIllegalCar().setSignReviewStatus(IllegalCarStatus.CHECK_FAIL.code());
        old.getIllegalCar().setDspStatus(IllegalCarStatus.CHECK_FAIL.code());

        old.getIllegalCar().setStopCause(codesString);
        old.getReportInfo().setDspStatus(ReportStatus.CHECK_ERR.code());
        illegalService.checkReport(id, old);
        if (isProd) {
            try {
                String plateNo =propertyRefactor.applyPlateNumber(old.getIllegalCar().getPlateNo());
                illegalService.updateCheckFailStatus(id, old.getIllegalCar().getPlateType(), plateNo, "");
                log.info("修改车网通违法举报: 案件{}, 车辆{}-{} 状态:{} 成功", id, old.getIllegalCar().getPlateType(), old.getIllegalCar().getPlateNo(), "");

            } catch (Exception e) {
                log.error("修改车网通违法举报: 案件{}, 车辆{}-{} 状态:{}失败", id, old.getIllegalCar().getPlateType(), old.getIllegalCar().getPlateNo(), "", e);
            }
        }
        log.info("check fail end!");
        return ResponseMessage.ok();
    }

}
