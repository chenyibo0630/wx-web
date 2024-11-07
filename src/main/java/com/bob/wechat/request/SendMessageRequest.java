package com.bob.wechat.request;

import com.bob.wechat.Context;
import com.bob.wechat.message.MsgType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class SendMessageRequest extends BaseRequest {

    @JsonIgnore
    private static final AtomicInteger ADD_UP = new AtomicInteger();

    @JsonProperty("Msg")
    private Message msg;

    @JsonProperty("Scene")
    private int scene;

    public SendMessageRequest(Context context) {
        super(context);
    }

    public static SendMessageRequest fromTextMsg(Context context, String toUserName, String content) {
        SendMessageRequest req = new SendMessageRequest(context);
        Message msg = new Message();
        msg.msgType = MsgType.TEXT;
        msg.fromUserName = context.myUserName();
        msg.toUserName = toUserName;
        msg.content = content;
        long id = System.currentTimeMillis() * 10 + ADD_UP.incrementAndGet() % 10;
        msg.localID = id;
        msg.clientMsgId = id;
        req.msg = msg;
        req.scene = 0;
        return req;
    }

    @Getter
    @Setter
    static class Message {

        @JsonProperty("Type")
        private int msgType;

        @JsonProperty("FromUserName")
        private String fromUserName;

        @JsonProperty("ToUserName")
        private String toUserName;

        @JsonProperty("Content")
        private String content;

        @JsonProperty("LocalID")
        private Long localID;

        @JsonProperty("ClientMsgId")
        private Long clientMsgId;
    }
}
