package com.bob.wechat;

import com.bob.wechat.cache.LoginCache;
import com.bob.wechat.message.MessageHandler;
import com.bob.wechat.request.BaseRequest;
import com.bob.wechat.request.SendMessageRequest;
import com.bob.wechat.service.LoginService;
import com.bob.wechat.service.LoopHandleService;
import com.bob.wechat.service.SyncCheckService;
import com.bob.wechat.utils.OkHttpUtils;
import com.bob.wechat.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WeixinClient {

    private final LoginService loginService = new LoginService();

    private final SyncCheckService syncCheckService = new SyncCheckService();

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 4,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    private final Context context;

    private final OkHttpClient httpClient;

    public WeixinClient() {
        this.context = LoginCache.load();
        this.httpClient = OkHttpUtils.getClient();
    }

    public void start(MessageHandler handler) {
        login();
        getContact();
        executor.execute(() -> syncCheckService.syncCheck(context));
        LoopHandleService service = new LoopHandleService(context, handler);
        executor.execute(service::start);
    }

    @SneakyThrows
    public void sendText(String toUserName, String content) {
        SendMessageRequest sendMsgReq = SendMessageRequest.fromTextMsg(context, toUserName, content);
        Request req = new Request.Builder()
                .url(APIUrl.SEND_MSG_URL)
                .post(RequestBody.create(Utils.objectMapper().writeValueAsBytes(sendMsgReq)))
                .build();
        try (Response rsp = httpClient.newCall(req).execute()) {
            if (rsp.code() == 200) {
                log.info("send success");
            }
        } catch (Exception e) {
            log.error("send message fail", e);
        }
    }

    private void login() {
        if (context.getInitInfo() != null) {
            // already login
            return;
        }
        // generate uuid
        String uuid = loginService.generateUid();
        if (uuid == null) {
            throw new RuntimeException("uuid not found");
        }
        log.info("uuid: {}", uuid);
        context.setUuid(uuid);
        // generate qr code
        loginService.generateQrCode(uuid);
        // check login
        String ticket = loginService.checkLogin(uuid);
        if (ticket == null) {
            throw new RuntimeException("pass ticket not found");
        }
        log.info("ticket: {}", ticket);
        context.setTicket(ticket);
        // new login
        loginService.newLogin(context);
        // init
        loginService.init(context);
        // cache login context
        LoginCache.store(context);
        log.debug("context: {}", context);
    }

    @SneakyThrows
    private void getContact() {
        BaseRequest baseRequest = new BaseRequest(context);
        Request req = new Request.Builder()
                .url(APIUrl.GET_CONTACT_URL)
                .post(RequestBody.create(Utils.objectMapper().writeValueAsBytes(baseRequest)))
                .build();
        try (Response rsp = httpClient.newCall(req).execute()) {
            if (rsp.code() != 200) {
                log.info("get contact fail, code: {}", rsp.code());
            }
        } catch (Exception e) {
            log.error("get contact fail", e);
        }
    }
}
