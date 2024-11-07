package com.bob.wechat.service;

import com.bob.wechat.Context;
import com.bob.wechat.message.MessageCache;
import com.bob.wechat.message.MessageHandler;
import com.bob.wechat.message.WxMessage;

public class LoopHandleService {

    private final Context context;

    private final MessageHandler handler;

    public LoopHandleService(Context context, MessageHandler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void start() {
        while (true) {
            WxMessage msg = MessageCache.get();
            handler.handle(context, msg);
        }
    }
}
