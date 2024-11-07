package com.bob.wechat.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WxMessage {

    private boolean group;

    private boolean ignore;

    private String fromUserName;

    private String type;

    private String data;

    private String filePath;

}
