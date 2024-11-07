package com.bob.wechat.service;

import com.bob.wechat.APIUrl;
import com.bob.wechat.message.MsgType;
import com.bob.wechat.utils.OkHttpUtils;
import kotlin.text.Charsets;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
public class DownloadService {

    private static final String CACHE_DIR = "cache/";

    private final OkHttpClient client = OkHttpUtils.getClient();

    public String getImg(String msgId, String sKey) {
        Request req = new Request.Builder()
                .url(getUrl(MsgType.IMAGE, msgId, sKey))
                .get()
                .build();
        try (Response rsp = client.newCall(req).execute()) {
            if (rsp.body() != null) {
                Path path = Paths.get(CACHE_DIR + UUID.randomUUID() + ".jpg");
                Files.createDirectories(path.getParent());
                Files.copy(rsp.body().byteStream(), path, StandardCopyOption.REPLACE_EXISTING);
                return path.toAbsolutePath().toString();
            }
        } catch (IOException e) {
            log.error("download img fail", e);
        }
        return null;
    }

    public String getVoice(String msgId, String sKey) {
        Request req = new Request.Builder()
                .url(getUrl(MsgType.VOICE, msgId, sKey))
                .get()
                .build();
        try (Response rsp = client.newCall(req).execute()) {
            if (rsp.body() != null) {
                Path path = Paths.get(CACHE_DIR + UUID.randomUUID() + ".mp3");
                Files.createDirectories(path.getParent());
                Files.copy(rsp.body().byteStream(), path, StandardCopyOption.REPLACE_EXISTING);
                return path.toAbsolutePath().toString();
            }
        } catch (IOException e) {
            log.error("download voice fail", e);
        }
        return null;
    }

    public String getVideo(String msgId, String sKey) {
        Request req = new Request.Builder()
                .url(getUrl(MsgType.VIDEO, msgId, sKey))
                .addHeader("Range", "bytes=0-")
                .get()
                .build();
        try (Response rsp = client.newCall(req).execute()) {
            if (rsp.body() != null) {
                Path path = Paths.get(CACHE_DIR + UUID.randomUUID() + ".mp4");
                Files.createDirectories(path.getParent());
                Files.copy(rsp.body().byteStream(), path, StandardCopyOption.REPLACE_EXISTING);
                return path.toAbsolutePath().toString();
            }
        } catch (IOException e) {
            log.error("download voice fail", e);
        }
        return null;
    }

    private String getUrl(int type, String msgId, String sKey) {
        String baseUrl = switch (type) {
            case MsgType.IMAGE -> APIUrl.GET_MSG_IMG;
            case MsgType.VOICE -> APIUrl.GET_MSG_VOICE;
            case MsgType.VIDEO -> APIUrl.GET_MSG_VIDEO;
            default -> "";
        };
        return baseUrl + "?msgid=" + URLEncoder.encode(msgId, Charsets.UTF_8) + "&skey=" + URLEncoder.encode(sKey, Charsets.UTF_8);
    }
}
