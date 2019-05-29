package com.zmcsoft.rex.workflow.illegal.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zmcsoft.rex.pay.PayRequest;
import com.zmcsoft.rex.pay.PayResponse;
import com.zmcsoft.rex.pay.RexPayChannel;
import com.zmcsoft.rex.pay.RexPayService;
import com.zmcsoft.rex.workflow.illegal.api.entity.HandleStatusDefine;
import com.zmcsoft.rex.workflow.illegal.api.service.CarIllegalCaseHandleService;
import com.zmcsoft.rex.workflow.illegal.api.service.PayVerificationService;
import com.zmcsoft.rex.workflow.illegal.api.service.ReportIllegalInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.id.IDGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RequestMapping("/illegal/pay")
@Slf4j(topic = "business.illegal.pay")
@RestController
@Api("违法缴费")
public class IllegalPayController {

    @Autowired
    private RexPayService rexPayService;

    private Set<String> tokens = new HashSet<>();

    @Autowired
    private PayVerificationService payVerificationService;

    @Autowired
    private CarIllegalCaseHandleService carIllegalCaseHandleService;

    @Autowired
    private ReportIllegalInfoService reportIllegalInfoService;

    @Autowired
    private Environment env;

    private static Set<Integer> denyHours = new HashSet<>(Arrays.asList(23, 0, 1, 2, 3));

    @GetMapping("/pay-info/{decisionNo}")
    @Authorize
    public ResponseMessage<PayVerificationService.VerificationResult<BigDecimal>> getRealPayMoneyInfo(@PathVariable String decisionNo) {
        return ResponseMessage.ok(payVerificationService.getRealPayAmount(decisionNo));
    }

    @RequestMapping("/callback")
    @ApiOperation(value = "支付模块回调", hidden = true)
    public ResponseMessage<String> callback(@RequestParam Map<String, String> param) {
        log.info("违法缴费收到支付模块回调:{}", JSON.toJSONString(param, SerializerFeature.PrettyFormat));
        try {
            String token = param.get("token");
            if (token == null) {
                log.warn("非法请求:{}", param);
                throw new BusinessException("非法请求");
            }
            tokens.remove(token);
            String decisionNo = param.get("orderId");
            String payId = param.get("id");

            //支付是否成功
            boolean success = "true".equals(param.get("success"));
            String errorCode = "0";
            HandleStatusDefine define = HandleStatusDefine.PAY;
            if (RexPayChannel.icbc.equals(param.get("channel"))) {
                String amount = param.get("amount");
                //先判断是否已经在其他渠道缴费
                PayVerificationService.VerificationResult<BigDecimal> realPayAmount = payVerificationService.getRealPayAmount(decisionNo);
                if (!realPayAmount.isSuccess()) {
                    if (realPayAmount.getCodeMapping() == PayVerificationService.CodeMapping.payed) {

                        define = HandleStatusDefine.SUCCESS;
                        //工行支付成功，但是已经在其他渠道付款，说明重复缴费
                        // TODO: 2017/11/28 是否就是工行缴费。由于重复回调引起?
                        if (success) {
                            if (null != payId) {
                                try {
                                    boolean marSuccess = rexPayService.markRepeatPay(payId);
                                    log.error("标记[{}]重复缴费:{}", payId, marSuccess);
                                } catch (Exception e) {
                                    log.error("标记[{}]重复缴费失败!", payId, e);
                                }
                            } else {
                                log.error("标记重复缴费失败:无法获取订单id,决定书编号:{}", decisionNo);
                            }
                        } else {
                            log.warn("工行缴费失败,但是在其他渠道已经缴费!payId={},decisionNo={}", payId, decisionNo);
                        }
                        success = true;
                    }
                } else if (success) {
                    //缴费成功，发送报文
                    PayVerificationService.VerificationResult<Boolean> paySuccess = payVerificationService.paySuccess(decisionNo, new BigDecimal(amount));
                    success = paySuccess.isSuccess();
                    if (!success) {
                        switch (paySuccess.getCodeMapping()) {
                            case sendError:
                            case requestError:
                                //支付成功 但是向工行接口发送付款请求时发生了系统级错误。
                                errorCode = "-1";
                                log.error("支付成功,缴费失败(发送报文失败):[{}]", decisionNo);
                                break;
                            default:
                                log.error("工行付款成功,缴费失败{}:{}",decisionNo, paySuccess.getCodeMapping());
                        }
                    }
                }
            }
            if (success) {
                log.info("缴费成功:\n{}", param);
                doPayIsSuccess(decisionNo, define);
            } else {
                log.error("缴费失败[{}]:\n{}", param, errorCode);
                doPayIsFail(decisionNo, errorCode);
            }
        } catch (Exception e) {
            log.error("处理支付回调失败:\n{}", param, e);
        }
        return ResponseMessage.ok();
    }

    private void doPayIsSuccess(String decisionNo, HandleStatusDefine statusDefine) {
        // TODO: 2017/11/13 应该判断实际更新是否成功，应该检查是否重复提交(可在更新到时候加入条件，只有未缴费状态才更新)
        carIllegalCaseHandleService.updatePayStatus(decisionNo, "1", statusDefine.getCode());
        reportIllegalInfoService.updatePayStatusByDecisionNo(decisionNo, String.valueOf(statusDefine.getCode()));
    }

    private void doPayIsFail(String decisionNo, String errorCode) {
        // TODO: 2017/11/13 将errorCode更新到表中，应该检查是否重复提交(可在更新到时候加入条件，只有未缴费状态才更新)
        carIllegalCaseHandleService.updatePayStatus(decisionNo, "-1", HandleStatusDefine.PAY_FAIL.getCode());
        reportIllegalInfoService.updatePayStatusByDecisionNo(decisionNo, String.valueOf(HandleStatusDefine.PAY_FAIL.getCode()));
    }

    @PostMapping("/{channel}/{decisionNo}")
    @ApiOperation("申请支付")
    public ResponseMessage<String> requestChannel(
            @ApiParam("渠道: icbc,unionpay....")
            @PathVariable String channel,
            @ApiParam("处决书编号")
            @PathVariable String decisionNo,
            @ApiParam("支付成功跳转页面")
            @RequestParam String redirectUrl) {
        Map<String, String> parameter = new HashMap<>();


        boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
        if (isProd && denyHours.contains(new DateTime().getHourOfDay())) {
            log.warn("系统维护时间发起支付请求{}.{}", channel, decisionNo);
            return ResponseMessage.error("当前为系统维护时间,暂停缴费功能!");
        }
//         if (RexPayChannel.icbc.equals(channel)) { update by 2017/11/23 放开限制
        //所有渠道都先校验是否已经在其他渠道完成了缴费
        PayVerificationService.VerificationResult<BigDecimal> realPayAmount = payVerificationService.getRealPayAmount(decisionNo);
        if (!realPayAmount.isSuccess()) {
            switch (realPayAmount.getCodeMapping()) {
                //已在其他渠道付款
                case payed:
                    //修改状态为已缴费
                    doPayIsSuccess(decisionNo, HandleStatusDefine.SUCCESS);
                    log.warn("已在其他渠道支付[{}]", decisionNo);
                    return ResponseMessage.error("已经在其他渠道完成缴费");
                default:
                    //其他错误抛出异常
                    throw new BusinessException(realPayAmount.getCodeMapping().message);
            }
        }
        BigDecimal amount = realPayAmount.getResult();

        log.info("获取到{}实际缴费金额{}", decisionNo, amount);
        //!!!!!!!!生产环境传入实际金额!!!!!!!!!!!!!!!!
        if (isProd) {
            parameter.put("amount", amount.toString());
        } else {
            parameter.put("amount", "0.01"); //测试环境只需1分钱
        }
//         }

        String token = IDGenerator.MD5.generate();
        tokens.add(token);
        //罚款金额
        parameter.put("channel", channel);
        parameter.put("orderId", decisionNo);  //增加时间戳
        parameter.put("redirectUrl", redirectUrl);
        parameter.put("token", token);
        parameter.put("callback", isProd ?
                "http://api.rex.cdjg.gov.cn:8090/illegal/pay/callback" //生产的回调
                : "http://api.test.rex.hsweb.me:8099/illegal/pay/callback");//测试的回调


        PayResponse response = rexPayService.requestPay(channel, PayRequest.builder().parameters(parameter).build());


        if (response.isSuccess()) {
            return ResponseMessage.ok(response.getHtmlForm());
        }
        return ResponseMessage.error(response.getMessage());
    }
}
