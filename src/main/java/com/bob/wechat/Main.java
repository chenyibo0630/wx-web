package com.bob.wechat;

import com.bob.wechat.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        WeixinClient client = new WeixinClient();
        client.start((context, msg) -> {
            try {
                log.info(Utils.objectMapper().writeValueAsString(msg));
                if (!msg.isGroup() && "text".equals(msg.getType())) {
                    client.sendText(msg.getFromUserName(), "hello");
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}