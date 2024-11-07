package com.bob.wechat.request;

import com.bob.wechat.Context;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseRequest {

    @JsonProperty("BaseRequest")
    private Req baseRequest;

    public BaseRequest(Context context) {
        this.baseRequest = new Req();
        baseRequest.deviceId = context.getPassTicket();
        baseRequest.sKey = context.getSkey();
        baseRequest.uin = context.getWxuin();
        baseRequest.sid = context.getWxsid();
    }

    @Getter
    @Setter
    static class Req {

        @JsonProperty("DeviceID")
        private String deviceId;

        @JsonProperty("Skey")
        private String sKey;

        @JsonProperty("Uin")
        private String uin;

        @JsonProperty("Sid")
        private String sid;
    }
}
