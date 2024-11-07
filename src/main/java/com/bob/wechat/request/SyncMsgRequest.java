package com.bob.wechat.request;

import com.bob.wechat.Context;
import com.bob.wechat.response.SyncKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncMsgRequest extends BaseRequest {

    public SyncMsgRequest(Context context) {
        super(context);
    }

    @JsonProperty("SyncKey")
    private SyncKey syncKey;

    @JsonProperty("rr")
    private Long rr;

}
