package com.bob.wechat.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class InitInfo {

    @JsonProperty("SyncKey")
    private SyncKey syncKey;

    @JsonProperty("User")
    private User user;

}
