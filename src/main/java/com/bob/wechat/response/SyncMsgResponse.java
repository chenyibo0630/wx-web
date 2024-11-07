package com.bob.wechat.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SyncMsgResponse {

    @JsonProperty("BaseResponse")
    private BaseResponse baseResponse;

    @JsonProperty("SyncCheckKey")
    private SyncKey syncCheckKey;

    @JsonProperty("AddMsgList")
    private List<AddMsg> addMsgList;

    @JsonProperty("SyncKey")
    private SyncKey syncKey;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class BaseResponse {

        @JsonProperty("Ret")
        private int ret;

        @JsonProperty("ErrMsg")
      private String errMsg;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class AddMsg {

        @JsonProperty("MsgId")
        private String msgId;

        @JsonProperty("FromUserName")
        private String fromUserName;

        @JsonProperty("ToUserName")
        private String toUserName;

        @JsonProperty("MsgType")
        private int msgType;

        @JsonProperty("Content")
        private String content;

        @JsonProperty("Url")
        private String url;
//        private int status;
//        private int imgStatus;
//        private long createTime;
//        private int voiceLength;
//        private int playLength;
//        private String fileName;
//        private String fileSize;
//        private String mediaId;

//        private int appMsgType;
//        private int statusNotifyCode;
//        private String statusNotifyUserName;
//        private RecommendInfo recommendInfo;
//        private int forwardFlag;
//        private AppInfo appInfo;
//        private int hasProductId;
//        private String ticket;
//        private int imgHeight;
//        private int imgWidth;
//        private int subMsgType;
//        private long newMsgId;
//        private String oriContent;
//        private String encryFileName;
    }
}
