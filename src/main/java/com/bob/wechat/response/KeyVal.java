package com.bob.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyVal {

    @JsonProperty("Key")
    private String key;

    @JsonProperty("Val")
    private String val;
}
