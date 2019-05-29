package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "违法代码信息")
public class IllegalCode extends SimpleGenericEntity<String> implements Serializable {

    /*@ApiModelProperty(value = "违法代码", required = true)
    private String illegalCode;

    @ApiModelProperty(value = "违法描述", required = true)
    private String illegalDescribe;

    @ApiModelProperty(value = "违法内容", required = true)
    private String illegalContent;

    @ApiModelProperty(value = "违法积分数", required = true)
    private Integer illegalScore;

    @ApiModelProperty(value = "罚款金额", required = true)
    private BigDecimal fineMoney;*/
    @ApiModelProperty(value = "违法代码", required = true)
    private String id;

    @ApiModelProperty(value = "违法大类", required = true)
    private String illegalLargeType;

    @ApiModelProperty(value = "违法小类", required = true)
    private String illegalSmallType;

    @ApiModelProperty(value = "违法内容简称", required = true)
    private String contentAbbreviate;

    @ApiModelProperty(value = "违法内容", required = true)
    private String illegalContent;

    @ApiModelProperty(value = "违法规定", required = true)
    private String illegalRegulations;

    @ApiModelProperty(value = "处罚依据", required = true)
    private String punishBasis;

    @ApiModelProperty(value = "违法记分", required = true)
    private Integer illegalScore;

    @ApiModelProperty(value = "罚款金额最小数", required = true)
    private BigDecimal minMoney;

    @ApiModelProperty(value = "罚款金额最大数", required = true)
    private BigDecimal maxMoney;

    @ApiModelProperty(value = "罚款金额默认值", required = true)
    private BigDecimal defaultMoney;

    @ApiModelProperty(value = "暂扣月数最小数", required = true)
    private String minMonth;

    @ApiModelProperty(value = "暂扣月数最大数", required = true)
    private String maxMonth;

    @ApiModelProperty(value = "暂扣月数默认值", required = true)
    private String defaultMonth;

    @ApiModelProperty(value = "拘留天数最小数", required = true)
    private String minDay;

    @ApiModelProperty(value = "拘留天数最大数", required = true)
    private String maxDay;

    @ApiModelProperty(value = "拘留天数默认值", required = true)
    private String defaultDay;

    @ApiModelProperty(value = "强制措施类型", required = true)
    private String forceActionType;

    @ApiModelProperty(value = "警告标记", required = true)
    private String warningSign;

    @ApiModelProperty(value = "罚款标记", required = true)
    private String fineSign;

    @ApiModelProperty(value = "暂扣标记", required = true)
    private String detentionSing;

    @ApiModelProperty(value = "吊销标记", required = true)
    private String revokeSign;

    @ApiModelProperty(value = "拘留标记", required = true)
    private String detentionSign;

    @ApiModelProperty(value = "撤销机动车登记许可标记", required = true)
    private String cancelCarPermit;

    @ApiModelProperty(value = "撤销驾驶人许可标记", required = true)
    private String cancelDriverPermit;

    @ApiModelProperty(value = "国标", required = true)
    private String international;

    @ApiModelProperty(value = "有效期始", required = true)
    private Date validityStart;

    @ApiModelProperty(value = "有效期至", required = true)
    private Date validityEnd;

    @ApiModelProperty(value = "管理部门  只用用于8开头的违法行为代码", required = true)
    private String manageDepratment;

    @ApiModelProperty(value = "传输标记")
    private String transferSign;

    @ApiModelProperty(value = "违法代码")
    private String serial;

    @ApiModelProperty(value = "部局传输标记")
    private String layoutTransferSign;

}
