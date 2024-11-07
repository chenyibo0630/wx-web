package com.bob.wechat.message;

import com.bob.wechat.Context;

public interface MessageHandler {

    void handle(Context context, WxMessage msg);
}
