package com.bob.wechat.request;

import com.bob.wechat.Context;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusNotifyRequest extends BaseRequest {

    @JsonProperty("Code")
    private String code;

    @JsonProperty("FromUserName")
    private String fromUserName;

    @JsonProperty("ToUserName")
    private String toUserName;

    @JsonProperty("ClientMsgId")
    private String clientMsgId;

    public StatusNotifyRequest(Context context) {
        super(context);
    }
}
