package com.bob.wechat.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SyncKey {

    @JsonProperty("Count")
    private int count;

    @JsonProperty("List")
    private List<KeyVal> list;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append("|");
            }
            sb.append(list.get(i).getKey()).append("_").append(list.get(i).getVal());
        }
        return sb.toString();
    }
}
