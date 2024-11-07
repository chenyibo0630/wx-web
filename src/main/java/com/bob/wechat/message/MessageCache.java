package com.bob.wechat.message;

import lombok.SneakyThrows;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageCache {

    private static final BlockingQueue<WxMessage> QUEUE = new LinkedBlockingQueue<>();

    @SneakyThrows
    public static void add(WxMessage msg) {
        QUEUE.put(msg);
    }

    @SneakyThrows
    public static WxMessage get() {
        return QUEUE.take();
    }
}
