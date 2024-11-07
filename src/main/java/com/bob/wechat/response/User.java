package com.bob.wechat.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class User {

    @JsonProperty("Uin")
    private String uin;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("NickName")
    private String nickName;

    @JsonProperty("HeadImgUrl")
    private String headImgUrl;

    @JsonProperty("RemarkName")
    private String remarkName;

    @JsonProperty("PYInitial")
    private String pyInitial;

    @JsonProperty("PYQuanPin")
    private String pyQuanPin;

    @JsonProperty("RemarkPYInitial")
    private String remarkPyInitial;

    @JsonProperty("RemarkPYQuanPin")
    private String remarkPyQuanPin;

    @JsonProperty("HideInputBarFlag")
    private int hideInputBarFlag;

    @JsonProperty("StarFriend")
    private int starFriend;

    @JsonProperty("Sex")
    private int sex;

    @JsonProperty("Signature")
    private String signature;

    @JsonProperty("AppAccountFlag")
    private int appAccountFlag;

    @JsonProperty("VerifyFlag")
    private int verifyFlag;

    @JsonProperty("ContactFlag")
    private int contactFlag;

    @JsonProperty("WebWxPluginSwitch")
    private int webWxPluginSwitch;

    @JsonProperty("HeadImgFlag")
    private int headImgFlag;

    @JsonProperty("SnsFlag")
    private int snsFlag;
}
