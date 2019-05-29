package com.zmcsoft.rex.workflow.illegal.api.entity;

/**
 * @author zhouhao
 * @since 1.0
 */
public enum HandleStatusDefine {
    NEW(new HandleStatus("未处理", (byte) 0, "未处理")),//----新举报违章信息
    REQUEST(new HandleStatus("后台审核中", (byte) 1, "已提交处罚申请")),
    REQUEST_FAIL(new HandleStatus("申请失败", (byte) -1, "申请失败")),
    REQUEST_FAIL_RETRY(new HandleStatus("系统异常",(byte)-2,"系统异常")),
    STOP(new HandleStatus("已终止", (byte) -3, "已终止")),
    NO_SIGN(new HandleStatus("审核成功", (byte) 2, "处罚申请成功")),//----待签收的违章信息
    NO_PAY(new HandleStatus("已处罚成功", (byte) 3, "已签收,未缴费")),//-----待缴费的违章信息
    PAY(new HandleStatus("缴费成功", (byte) 4, "已缴费")),
    PAY_FAIL(new HandleStatus("缴费失败", (byte) -4, "支付成功,缴费失败")),
    SUCCESS(new HandleStatus("已完成处罚", (byte) 5, "已完成处罚")),//-------已处理完成的违章信息
    UNKNOWN(new HandleStatus("未知", (byte) -30, "未知状态")),
    CANT_REQUEST(new HandleStatus("不能申请", (byte) -20, "不能申请"));
    final HandleStatus status;

    HandleStatusDefine(HandleStatus status) {
        this.status = status;
    }

    public HandleStatus getStatus() {
        return status;
    }

    public byte getCode() {
        return getStatus().getCode();
    }

    public boolean eq(Byte code) {
        return code != null && code.equals(getCode());
    }
}
