package com.Auroral.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 用户信息vo
 *
 * @author Auroral
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "用户信息对象")
public class UserInfoVO {

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    @ApiModelProperty(name = "nickname", value = "昵称", dataType = "String")
    private String nickname;

    /**
     * 用户简介
     */
    @ApiModelProperty(name = "intro", value = "介绍", dataType = "String")
    private String intro;

    /**
     * 个人网站
     */
    @ApiModelProperty(name = "webSite", value = "个人网站", dataType = "String")
    private String webSite;
}
