package com.zmcsoft.rex.api.user.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;

/**
 * 注册用户信息
 *
 * @author zhouhao
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "注册用户信息")
public class User extends SimpleGenericEntity<String> {

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号", required = true)
    private String idCard;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别", required = true)
    private Byte sex;

    /**
     * 国籍
     */
    @ApiModelProperty(value = "国籍")
    private String nationality;

    /**
     * 家庭住址
     */
    @ApiModelProperty(value = "家庭住址")
    private String address;

    /**
     * 生日
     */
    @ApiModelProperty(value = "生日")
    private Date birthday;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话", required = true)
    private String phone;

    /**
     * 电子邮件
     */
    @ApiModelProperty(value = "电子邮箱")
    private String email;

    /**
     * 头像图片地址
     */
    @ApiModelProperty(value = "头像图片地址")
    private String headImg;

    /**
     * 用户状态
     */
    @ApiModelProperty(hidden = true)
    private Byte status;

    /**
     * 用户类型
     */
    @ApiModelProperty(hidden = true)
    private Byte type;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;

    public interface Status {
        /**
         * 未阅读法律声明
         */
        byte READ_LEGAL_NO = 0;

        /**
         * 已未阅读法律声明
         */
        byte READ_LEGAL_YES = 1;
    }

    public interface Type {
        /**
         * 普通用户
         */
        byte normal        = 0;
        /**
         * 认证用户
         */
        byte authenticated = 0;
    }
}
