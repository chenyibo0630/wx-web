package com.bob.wechat.service;

import com.bob.wechat.APIUrl;
import com.bob.wechat.Context;
import com.bob.wechat.cache.LoginCache;
import com.bob.wechat.request.StatusNotifyRequest;
import com.bob.wechat.utils.OkHttpUtils;
import com.bob.wechat.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.Date;
import java.util.regex.Matcher;

@Slf4j
public class SyncCheckService {

    private final OkHttpClient client = OkHttpUtils.getClient();

    private final MessageService messageService = new MessageService();

    public void syncCheck(Context context) {
        // notify
        try (Response rsp = client.newCall(buildStatusNotifyRequest(context)).execute()) {
            log.info("status notify, code: {}", rsp.code());
            if (rsp.code() != 200) {
                return;
            }
        } catch (Exception e) {
            log.error("status notify fail", e);
            return;
        }
        // loop check
        while (true) {
            log.debug("sync check");
            try (Response rsp = client.newCall(buildSyncCheckRequest(context)).execute()) {
                assert rsp.body() != null;
                String result = rsp.body().string();
                String regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"}";
                Matcher matcher = Utils.getMatcher(regEx, result);
                if (!matcher.find() || matcher.group(1).equals("2")) {
                    log.info(String.format("Unexpected sync check result: %s", result));
                } else {
                    boolean success = handleResult(context, matcher.group(1), matcher.group(2));
                    if (!success) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("sync check fail", e);
                break;
            }
        }
    }

    @SneakyThrows
    private boolean handleResult(Context context, String code, String selector) {
        RetCodeEnum retCodeEnum = RetCodeEnum.from(code);
        if (retCodeEnum == RetCodeEnum.NORMAL) {
            messageService.syncMessage(context);
            SelectorEnum selectorEnum = SelectorEnum.from(selector);
            switch (selectorEnum) {
                case MEW_MSG:
                    log.info("receive new message");
                    break;
                case ADD_DEL_CONTACT:
                    log.info("add or delete contact");

                    break;
                default:
                    log.info("sync check selector code: {}, desc: {}", selectorEnum.code, selectorEnum.desc);
                    break;
            }
            return true;
        } else {
            log.error("sync check ret code: {}, desc: {}", retCodeEnum.code, retCodeEnum.desc);
            LoginCache.clear();
            return false;
        }
    }

    @SneakyThrows
    private Request buildStatusNotifyRequest(Context context) {
        StatusNotifyRequest req = new StatusNotifyRequest(context);
        req.setCode("3");
        req.setFromUserName("");
        req.setToUserName(context.myUserName());
        req.setClientMsgId(String.valueOf(System.currentTimeMillis()));
        return new Request.Builder()
                .url(String.format(APIUrl.STATUS_NOTIFY_URL, context.getPassTicket()))
                .post(RequestBody.create(Utils.objectMapper().writeValueAsBytes(req)))
                .build();
    }

    private Request buildSyncCheckRequest(Context context) {
        return new Request.Builder().url(APIUrl.SYNC_CHECK_URL + "?" + paramString(context)).get().build();
    }


    private String paramString(Context context) {
        long now = new Date().getTime();
        return "uin=" + context.getWxuin() + "&" + "sid=" + context.getWxsid() + "&" + "skey=" + context.getSkey() + "&" + "deviceid=" + context.getPassTicket() + "&" + "synckey=" + context.getInitInfo().getSyncKey() + "&" + "r=" + now + "&" + "_=" + now;
    }

    enum RetCodeEnum {

        NORMAL("0", "普通"),

        LOGIN_OUT("1102", "退出"),

        LOGIN_OTHERWHERE("1101", "其它地方登陆"),

        MOBILE_LOGIN_OUT("1102", "移动端退出"),

        UNKOWN("9999", "未知");


        private final String code;

        private final String desc;

        RetCodeEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static RetCodeEnum from(String code) {
            for (RetCodeEnum e : RetCodeEnum.values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            log.warn("unknown ret code: {}", code);
            return UNKOWN;
        }
    }

    enum SelectorEnum {

        NORMAL("0", "normal"),

        MEW_MSG("2", "new message"),

        MOD_CONTACT("4", "modify contact"),

        ADD_DEL_CONTACT("6", "add or delete contact"),

        MOD_CHAT("7", "enter or leave chat"),

        UNKNOWN("9999", "unknown");

        private final String code;

        private final String desc;

        SelectorEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static SelectorEnum from(String code) {
            for (SelectorEnum e : SelectorEnum.values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            log.warn("unknown selector code: {}", code);
            return UNKNOWN;
        }
    }
}
