package com.bob.wechat;

import com.bob.wechat.response.InitInfo;
import com.bob.wechat.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Context {

    private String uuid;

    private String ticket;

    private String skey;

    private String wxsid;

    private String wxuin;

    private String passTicket;

//    private String deviceId;

    private InitInfo initInfo;

    public String myUserName() {
        return initInfo.getUser().getUserName();
    }

    @Override
    public String toString() {
        try {
            return Utils.objectMapper().writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }
}
