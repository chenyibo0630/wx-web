package com.bob.wechat.service;

import com.bob.wechat.APIUrl;
import com.bob.wechat.Context;
import com.bob.wechat.cache.LoginCache;
import com.bob.wechat.message.MessageCache;
import com.bob.wechat.message.MsgType;
import com.bob.wechat.message.WxMessage;
import com.bob.wechat.request.SyncMsgRequest;
import com.bob.wechat.response.SyncMsgResponse;
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
public class MessageService {

    private final OkHttpClient client = OkHttpUtils.getClient();

    private final DownloadService downloadService = new DownloadService();

    public void syncMessage(Context context) {
        log.info("sync message");
        try (Response rsp = client.newCall(buildSyncMsgRequest(context)).execute()) {
            assert rsp.body() != null;
            String result = rsp.body().string();
            SyncMsgResponse response = Utils.objectMapper().readValue(result, SyncMsgResponse.class);
            if (response.getBaseResponse().getRet() != 0) {
                log.error("sync message response fail, ret: {}, error message: {}", response.getBaseResponse().getRet(), response.getBaseResponse().getErrMsg());
                return;
            }
            // init sync key
            context.getInitInfo().setSyncKey(response.getSyncKey());
            LoginCache.store(context);
            // convert messages
            for (SyncMsgResponse.AddMsg msg : response.getAddMsgList()) {
                WxMessage wxMsg = convert(context, msg);
                if (!wxMsg.isIgnore()) {
                    MessageCache.add(wxMsg);
                }
            }
        } catch (Exception e) {
            log.error("sync check fail", e);
        }
    }

    @SneakyThrows
    private Request buildSyncMsgRequest(Context context) {
        SyncMsgRequest req = new SyncMsgRequest(context);
        req.setSyncKey(context.getInitInfo().getSyncKey());
        req.setRr(-new Date().getTime() / 1000);
        return new Request.Builder()
                .url(String.format(APIUrl.SYNC_MESSAGE_URL, context.getWxsid(), context.getSkey(), context.getPassTicket()))
                .post(RequestBody.create(Utils.objectMapper().writeValueAsBytes(req)))
                .build();
    }

    private WxMessage convert(Context context, SyncMsgResponse.AddMsg msg) {
        WxMessage rs = new WxMessage();
        // not Nick Name
        rs.setFromUserName(msg.getFromUserName());
        // ignore group message
        if (msg.getFromUserName().contains("@@")) {
            rs.setGroup(true);
            return rs;
        }
//        // ignore self send message
//        if (!msg.getToUserName().equals(context.myUserName())) {
//            rs.setIgnore(true);
//            return rs;
//        }
        switch (msg.getMsgType()) {
            case MsgType.TEXT:
                String data = msg.getContent();
                if (msg.getUrl() != null && !msg.getUrl().isEmpty()) {
                    String regEx = "(.+?\\(.+?\\))";
                    Matcher matcher = Utils.getMatcher(regEx, data);
                    if (matcher.find()) {
                        data = matcher.group(1);
                    }
                }
                rs.setType("text");
                rs.setData(data);
                break;
            case MsgType.IMAGE:
            case MsgType.EMOJI:
                rs.setType("image");
                rs.setFilePath(downloadService.getImg(msg.getMsgId(), context.getSkey()));
                break;
            case MsgType.VOICE:
                rs.setType("voice");
                rs.setFilePath(downloadService.getVoice(msg.getMsgId(), context.getSkey()));
                break;
            case MsgType.VIDEO:
                rs.setType("video");
                rs.setFilePath(downloadService.getVideo(msg.getMsgId(), context.getSkey()));
                break;
            case MsgType.STATUS_NOTIFY:
                rs.setType("notify");
                break;
            default:
                break;
        }
        return rs;
    }

}
