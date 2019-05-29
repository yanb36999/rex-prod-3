package com.zmcsoft.rex.workflow.illegal.controller.my;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zmcsoft.rex.api.user.entity.User;
import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.api.user.service.UserCarService;
import com.zmcsoft.rex.api.user.service.UserServiceManager;
import com.zmcsoft.rex.tmb.illegal.entity.NunciatorInfo;
import com.zmcsoft.rex.tmb.illegal.service.NunciatorService;
import com.zmcsoft.rex.workflow.illegal.api.Constants;
import com.zmcsoft.rex.workflow.illegal.api.DriverLicenseErrStatus;
import com.zmcsoft.rex.workflow.illegal.api.IllegalCarStatus;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import com.zmcsoft.rex.workflow.illegal.api.service.*;
import com.zmcsoft.rex.workflow.illegal.impl.service.LocalCarIllegalCaseHandleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.expands.request.ftp.FtpRequest;
import org.hswebframework.expands.request.http.HttpRequest;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.NotFoundException;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.exception.UnAuthorizedException;
import org.hswebframework.web.authorization.token.TokenState;
import org.hswebframework.web.commons.entity.GenericEntity;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.entity.organizational.OrganizationalEntity;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.organizational.OrganizationalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.*;
import sun.net.www.content.text.Generic;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;


@RestController
@Api(tags = "举报违法信息")
@RequestMapping("/report-illegal")
@Authorize()
@ConfigurationProperties(prefix = "com.zmcsoft.tmb.ftp")
@Slf4j(topic = "business.illegal.report")
public class IllegalReportController {


    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    private UserServiceManager userServiceManager;

    @Autowired
    private ReportIllegalInfoService reportIllegalInfoService;

    @Autowired
    private ReportIllegalCarService reportIllegalCarService;

    @Autowired
    private OrganizationalService organizationalService;

    @Autowired
    private PropertyRefactor propertyRefactor;

    @Autowired
    private NunciatorService nunciatorService;

    @Autowired
    private IllegalCodeService illegalCodeService;

    @Autowired
    private RequestBuilder requestBuilder = new SimpleRequestBuilder();

    @Autowired
    private ReportIllegalService illegalService;


    @GetMapping("/old")
    @ApiModelProperty("获取历史纪录车辆数据+举报数据")
    public ResponseMessage<List<RspReportCarData>> getOld(Authentication authentication){
        List<UserCar> userCars = userServiceManager.userCarService().getByUserId(authentication.getUser().getId());
        List<RspReportCarData> list = new ArrayList<>();
        userCars.forEach(car->{
            RspReportCarData rspReportCarData = new RspReportCarData();

            String plateNumber = propertyRefactor.applyPlateNumber(car.getPlateNumber());

            List<ReportIllegalDetail> details = illegalService.getOld(plateNumber, car.getPlateType());

            List<ReportInfo> reportInfoList = new ArrayList<>();
            details.forEach(detail->
                    reportInfoList.add(detail.getReportInfo())
            );
            propertyRefactor.applyPlateNumber(car::getPlateNumber,car::setPlateNumber);
            rspReportCarData.setUserCar(car);
            rspReportCarData.setReportInfoList(reportInfoList);
            list.add(rspReportCarData);
        });
        return ResponseMessage.ok(list);
    }


    @GetMapping("/report-list/old")
    @ApiModelProperty("获取历史纪录车辆数据+举报数据")
    public ResponseMessage<List<RspReportCarData>> getOldRecord(Authentication authentication){
        List<UserCar> userCars = userServiceManager.userCarService().getByUserId(authentication.getUser().getId());
        List<RspReportCarData> list = new ArrayList<>();
        userCars.forEach(car->{
            RspReportCarData rspReportCarData = new RspReportCarData();

            String plateNumber = propertyRefactor.applyPlateNumber(car.getPlateNumber());

            List<ReportIllegalDetail> details = illegalService.getOld(plateNumber, car.getPlateType());

            List<ReportInfo> reportInfoList = new ArrayList<>();

                details.forEach(detail-> {
                    ReportInfo reportInfo = detail.getReportInfo();
                    propertyRefactor.applyDict("report-type", reportInfo::getReportType, reportInfo::setReportType);
                    reportInfoList.add(reportInfo);
                }
            );
            propertyRefactor.applyPlateNumber(car::getPlateNumber,car::setPlateNumber);
            rspReportCarData.setUserCar(car);
            rspReportCarData.setReportInfoList(reportInfoList.stream().sorted(Comparator.comparing(ReportInfo::getReportDate)).collect(Collectors.toList()));
            list.add(rspReportCarData);
        });
        return ResponseMessage.ok(list);
    }

    @GetMapping("/accept-report/{reportId}/{plateNo}/{plateType}")
    @ApiModelProperty("更新历史举报状态")
    public ResponseMessage acceptReport(@PathVariable String reportId,@PathVariable String plateNo,@PathVariable String plateType){
        return ResponseMessage.ok(illegalService.updateOld(reportId,plateNo,plateType));
    }

    //根据用户微信Id 获取违法举报信息
    @GetMapping("/report-list/{status}")
    @ApiOperation("根据用户OpenId和举报状态获取被举报信息【废弃】")
    public ResponseMessage<List<RspReportCarData>> reportInfo(Authentication authentication,@PathVariable String status){
        //获取到用户得车辆
        List<UserCar> userCars = userServiceManager.userCarService().getByUserId(authentication.getUser().getId());
        List<RspReportCarData> list = new ArrayList<>();
        //以后这个接口就拿来查询未处理的举报车辆数据!
        //String dspStatus = IllegalCarStatus.ENTER_NO.code();
        if (userCars!=null) {
            userCars.forEach(car -> {
                RspReportCarData rspReportCarData = new RspReportCarData();
                List<ReportInfo> reportInfoList = new ArrayList<>();
                //查询举报车辆信息 状态位为1101的数据
                List<ReportIllegalCar> illegalCarList = reportIllegalCarService.getIllegalCarInfoByStatus(car.getPlateNumber(),
                        car.getPlateType(),
                        String.valueOf(IllegalCarStatus.ENTER_NO.code()));


                illegalCarList.forEach(illegalCar -> {
                    ReportInfo reportInfo = reportInfoService.getByWaterId(illegalCar.getReportId());
                    if (reportInfo != null) {
                        propertyRefactor.applyDict("report-type", reportInfo::getReportType, reportInfo::setReportType);
                        reportInfoList.add(reportInfo);
                    }
                });
                if (car != null) {
                    propertyRefactor.applyPlateNumber(car::getPlateNumber, car::setPlateNumber);
                    propertyRefactor.applyDict("plate-type", car::getPlateType, car::setPlateTypeText);
                }
                rspReportCarData.setUserCar(car);
                rspReportCarData.setReportInfoList(reportInfoList);
                list.add(rspReportCarData);
            });
        }else {
            throw new BusinessException("该用户尚未绑定机动车");
        }
        return ResponseMessage.ok(list);
    }

    //根据用户微信Id 获取违法举报信息【新接口】
    @GetMapping("/report-list/new")
    @ApiOperation("根据用户OpenId和举报状态获取被举报信息【新接口】")
    public ResponseMessage<List<RspReportCarData>> reportInfoList(Authentication authentication){
        //获取到用户得车辆
        List<UserCar> userCars = userServiceManager.userCarService().getByUserId(authentication.getUser().getId());
        List<RspReportCarData> list = new ArrayList<>();
        //以后这个接口就拿来查询未处理的举报车辆数据!
        //String dspStatus = IllegalCarStatus.ENTER_NO.code();
        if (userCars!=null) {
            userCars.forEach(car -> {
                RspReportCarData rspReportCarData = new RspReportCarData();
                List<ReportInfo> reportInfoList = new ArrayList<>();
                //查询举报车辆信息 状态位为1101的数据
                List<ReportIllegalCar> illegalCarList = reportIllegalCarService.getIllegalCarInfoByStatus(car.getPlateNumber(),
                        car.getPlateType(),
                        String.valueOf(IllegalCarStatus.ENTER_NO.code()));


                illegalCarList.forEach(illegalCar -> {
                    ReportInfo reportInfo = reportInfoService.getByWaterId(illegalCar.getReportId());
                    if (reportInfo != null) {
                        propertyRefactor.applyDict("report-type", reportInfo::getReportType, reportInfo::setReportType);
                        reportInfoList.add(reportInfo);
                    }
                });
                if (car != null) {
                    propertyRefactor.applyPlateNumber(car::getPlateNumber, car::setPlateNumber);
                    propertyRefactor.applyDict("plate-type", car::getPlateType, car::setPlateTypeText);
                }
                rspReportCarData.setUserCar(car);
                rspReportCarData.setReportInfoList(reportInfoList.stream().sorted(Comparator.comparing(ReportInfo::getReportDate)).collect(Collectors.toList()));
                list.add(rspReportCarData);
            });
        }else {
            throw new BusinessException("该用户尚未绑定机动车");
        }
        return ResponseMessage.ok(list);
    }


    @GetMapping("/report-info/{waterId}")
    @ApiModelProperty("根据举报流水号，获取举报详情")
    public ResponseMessage<ReportInfo> reportInfo(String waterId){
        ReportInfo reportInfo = reportInfoService.getByWaterId(waterId);
        propertyRefactor.applyDict("report-type", reportInfo::getReportType, reportInfo::setReportType);
        return ResponseMessage.ok(reportInfo);
    }

    @GetMapping("/inform-data/{signDept}")
    @ApiOperation("根据签收部门代码(signDept)获取部门信息")
    public ResponseMessage<NunciatorInfo> informData(@PathVariable String signDept){
        return ResponseMessage.ok(nunciatorService.getByOrgCode(signDept,true));
    }


    @GetMapping("/info/{reportId}/{plateNo}/{plateType}")
    @ApiOperation("根据举报Id，车辆信息获取违法信息")
    public ResponseMessage<ReportRspDetailInfo> illegalDetailInfo(@PathVariable String reportId, @PathVariable String plateNo, @PathVariable String plateType){
        plateNo =propertyRefactor.removePlateNumber(plateNo);
        ReportRspDetailInfo reportRspDetailInfo = new ReportRspDetailInfo();
        //根据当前登录用户查询违法车辆信息
        ReportIllegalCar carInfo = reportIllegalCarService.getIllegalCarInfo(reportId, plateNo, plateType);
        List<ReportIllegalInfo> illegalInfoList;
        if (carInfo!=null){
            //中文车牌类别
            propertyRefactor.applyDict("plate-type",carInfo::getPlateType,carInfo::setPlateTypeText);
            //加川字的车辆
            propertyRefactor.applyPlateNumber(carInfo::getPlateNo,carInfo::setPlateNo);
            //设置签收部门全称
            OrganizationalEntity organizational = organizationalService.selectByCode(carInfo.getSignDept());
            carInfo.setSignDeptFullName(organizational.getFullName());
            reportRspDetailInfo.setReportIllegalCar(carInfo);
            illegalInfoList = reportIllegalInfoService.getIllegalInfo(reportId, plateNo, plateType);
            illegalInfoList.forEach(illegalInfo->{
                String illegalBehavior = illegalInfo.getIllegalBehavior();
                propertyRefactor.applyPlateNumber(illegalInfo::getPlateNo,illegalInfo::setPlateNo);
                illegalInfo.setIllegalBehaviorName(illegalCodeService.getByCode(illegalBehavior).getContentAbbreviate());
            });
            reportRspDetailInfo.setReportIllegalInfoList(illegalInfoList);
        }
        return ResponseMessage.ok(reportRspDetailInfo);
    }

    /**
     * 签收案件
     * @param illegalId
     * @return
     */
    @PostMapping("/sign/{illegalId}")
    @PatchMapping
    @ApiModelProperty("根据案件ID签收该案件")
    public ResponseMessage<Boolean> signIllegal(@ApiParam(value = "违法ID", required = true) @PathVariable String illegalId){

        return ResponseMessage.ok(reportIllegalInfoService.sign(illegalId));
    }


    /**
     * 根据当前登录的用户信息获取用户驾驶证信息
     * @param authentication
     * @return
     */
    @GetMapping("/driver-license-old")
    @ApiOperation("根据当前登录信息获取驾驶证信息")
    public ResponseMessage<UserDriverLicense> licenseInfo(Authentication authentication){
        UserDriverLicense driverLicense = userServiceManager.userDriverLicenseService().getByUserId(authentication.getUser().getId());

//        CarIllegalCaseHandle caseLicense = reportIllegalInfoService.getDriverLicense(authentication.getUser().getId());
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date updateTime = caseLicense.getUpdateTime();
//        String preDate = sdf.format(new Date());//当前时间
//        String updateDate = sdf.format(updateTime);//分数更新时间
//
//        if (caseLicense!=null&&preDate.equals(updateDate)){
//            //判断case表中记分数据是否是当天更新的，。如果是则将驾照信息的记分数替换成case表中的分数;
//            Integer licenseScore = caseLicense.getLicenseScore();
//            driverLicense.setTotalScore(licenseScore);
//        }

        if (driverLicense==null){
            return (ResponseMessage)ResponseMessage.ok("");
        }else {
            return ResponseMessage.ok(driverLicense);
        }

    }


    @GetMapping("/driver-license")
    @ApiModelProperty("车网通实时查询驾驶证分数接口")
    public ResponseMessage<UserDriverLicense> licenseInfoByCwt(Authentication authentication){

        UserDriverLicense driverLicense = userServiceManager.userDriverLicenseService().getByUserId(authentication.getUser().getId());

        //车网通提供的查询驾照分数接口
        // String licenseInfoUrl = "http://cdjg.gov.cn:8090/WeChat/system/query!getDrvInfo?zjbh=";
        String licenseInfoUrl = "http://178.16.13.73:8090/WeChat/system/query!getDrvInfo?zjbh=";
        try {
            String licenseInfo = requestBuilder.http(licenseInfoUrl + driverLicense.getLicenseNumber())
                    .get().asString();
            log.info("licenseInfo:{}",licenseInfo);
            JSONObject licenseMap = JSON.parseObject(licenseInfo);
            Map info = (Map) licenseMap.get("data");
            String score = (String) info.get("LJJF");
            driverLicense.setTotalScore(Integer.parseInt(score));
            DriverLicenseErrStatus driverLicenseErrStatus = DriverLicenseErrStatus.ofCode(driverLicense.getLicenseStatus());
            String message = "您的驾驶证状态，不属于业务办理范围！";
            driverLicense.setStatusName(driverLicenseErrStatus!=null?driverLicenseErrStatus.message():message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseMessage.ok(driverLicense);
    }


    @GetMapping("/illegal-list")
    @ApiOperation("根据当前登录获取用户车辆的违法信息")
    public ResponseMessage<List<RspSignIllegalInfo>> illegalInfoList(Authentication authentication){
        //获取到用户得车辆
        List<UserCar> userCars = userServiceManager.userCarService().getByUserId(authentication.getUser().getId());
        List<RspSignIllegalInfo> signIllegalInfoList = new ArrayList<>();
        userCars.forEach(car->{
            RspSignIllegalInfo signIllegalInfo = new RspSignIllegalInfo();
            List<ReportIllegalInfo> illegalInfo = reportIllegalInfoService.getIllegalInfo(car.getPlateNumber(), car.getPlateType());
            illegalInfo.forEach(
                    illegal->{
                        IllegalCode behaviorName = illegalCodeService.getByCode(illegal.getIllegalBehavior());
                        illegal.setDspStatusName(HandleStatus.of(Integer.parseInt(illegal.getDisposeStatus())).getComment());
                        illegal.setIllegalBehaviorName(behaviorName.getContentAbbreviate());
                    }
            );
            propertyRefactor.applyPlateNumber(car::getPlateNumber,car::setPlateNumber);
            signIllegalInfo.setUserCar(car);
            signIllegalInfo.setReportIllegalInfoList(illegalInfo);
            signIllegalInfoList.add(signIllegalInfo);
        });
        return ResponseMessage.ok(signIllegalInfoList);
    }


    @GetMapping("illegal-list/history")
    @ApiModelProperty("根据当前登录用户查询车辆历史违法信息")
    public ResponseMessage<List<RspSignIllegalInfo>> illegalInfoHistoryList(Authentication authentication){
        //获取到用户得车辆
        List<UserCar> userCars = userServiceManager.userCarService().getByUserId(authentication.getUser().getId());
        List<RspSignIllegalInfo> signIllegalInfoList = new ArrayList<>();
        userCars.forEach(car->{
            RspSignIllegalInfo signIllegalInfo = new RspSignIllegalInfo();
            List<ReportIllegalInfo> illegalInfo = reportIllegalInfoService.getIllegalInfoHistory(car.getPlateNumber(), car.getPlateType());
            illegalInfo.forEach(
                    illegal->illegal.setDspStatusName(HandleStatus.of(Integer.parseInt(illegal.getDisposeStatus())).getComment())
            );
            signIllegalInfo.setUserCar(car);
//            Collections.sort(illegalInfo,Comparator.comparing(ReportIllegalInfo::getUpdateTime));
//            signIllegalInfo.setReportIllegalInfoList(illegalInfo.stream().sorted(Comparator.comparing(ReportIllegalInfo::getUpdateTime)).collect(Collectors.toList()));
            signIllegalInfoList.add(signIllegalInfo);
        });
        return ResponseMessage.ok(signIllegalInfoList);
    }



    @PostMapping("/confirm")
    @ApiOperation("提交确认处罚信息")
    public ResponseMessage<Boolean> reportConfirm(Authentication authentication ,@ApiParam(value = "确认参数", required = true)
    @RequestBody ReqconfrimDetail reqConfirmData){
        //更新数据
        Boolean result = reportIllegalCarService.updateConfirmDate(reqConfirmData);
        //组装报文数据
        reportIllegalInfoService.fileData(reqConfirmData,authentication);
        return ResponseMessage.ok(result);
    }




}
