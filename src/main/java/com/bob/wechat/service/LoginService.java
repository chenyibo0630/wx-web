package com.bob.wechat.service;

import com.bob.wechat.APIUrl;
import com.bob.wechat.Context;
import com.bob.wechat.request.BaseRequest;
import com.bob.wechat.response.InitInfo;
import com.bob.wechat.utils.OkHttpUtils;
import com.bob.wechat.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;

@Slf4j
public class LoginService {

    private static final String QR_PATH = "qrcode.jpg";

    private final OkHttpClient client = OkHttpUtils.getClient();

    public String generateUid() {
        try (Response rsp = client.newCall(generateUidRequest()).execute()) {
            if (rsp.code() != 200) {
                log.error("generate uid fail, code: {}", rsp.code());
                return null;
            }
            assert rsp.body() != null;
            String result = rsp.body().string();
            String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
            Matcher matcher = Utils.getMatcher(regEx, result);
            if (matcher.find()) {
                if (("200".equals(matcher.group(1)))) {
                    return matcher.group(2);
                }
            }
        } catch (Exception e) {
            log.error("generate uuid fail", e);
        }
        return null;
    }

    public void generateQrCode(String uuid) {
        try (Response rsp = client.newCall(generateQrCodeRequest(uuid)).execute()) {
            assert rsp.body() != null;
            InputStream in = rsp.body().byteStream();
            OutputStream out = new FileOutputStream(QR_PATH);
            IOUtils.copy(in, out);
        } catch (Exception e) {
            log.error("generate QR fail", e);
            System.exit(1);
        }
    }

    public String checkLogin(String uuid) {
        Request request = checkLoginRequest(uuid);
        while (true) {
            try (Response rsp = client.newCall(request).execute()) {
                if (rsp.code() != 200) {
                    log.error("check login status fail, code: {}", rsp.code());
                    break;
                }
                assert rsp.body() != null;
                String result = rsp.body().string();
                log.debug("check login result: {}", result);
                String codeReg = "window.code=(\\d+);.*";
                Matcher matcher = Utils.getMatcher(codeReg, result);
                boolean match = false;
                if (matcher.find()) {
                    String code = matcher.group(1);
                    switch (code) {
                        case "200":
                            String redirectReg = "ticket=(\\S+?)&.*";
                            matcher = Utils.getMatcher(redirectReg, result);
                            if (matcher.find()) {
                                return matcher.group(1);
                            }
                            break;
                        case "408":
                            log.info("not scan");
                            match = true;
                            break;
                        case "201":
                            log.info("not confirm");
                            match = true;
                            break;
                    }
                }
                if (!match) {
                    log.error("Unexpected result: {}", result);
                }
            } catch (Exception e) {
                log.error("check login fail", e);
                System.exit(1);
            }
        }
        return null;
    }

    public void newLogin(Context context) {
        Request request = newLoginRequest(context.getUuid(), context.getTicket());
        try (Response rsp = client.newCall(request).execute()) {
            assert rsp.body() != null;
            String xml = rsp.body().string();
            log.debug("new login result: {}", xml);
            Document doc = Utils.xmlParser(xml);
            if (!"0".equals(doc.getElementsByTagName("ret").item(0).getFirstChild()
                    .getNodeValue())) {
                throw new IllegalStateException("call login fail");
            }
            // from xml
            context.setSkey(doc.getElementsByTagName("skey").item(0).getFirstChild()
                    .getNodeValue());
            context.setWxsid(doc.getElementsByTagName("wxsid").item(0).getFirstChild()
                    .getNodeValue());
            context.setWxuin(doc.getElementsByTagName("wxuin").item(0).getFirstChild()
                    .getNodeValue());
            context.setPassTicket(doc.getElementsByTagName("pass_ticket").item(0).getFirstChild()
                    .getNodeValue());
            // generate device ID
//            context.setDeviceId("e202410182024101");
        } catch (Exception e) {
            log.error("new login fail", e);
            System.exit(1);
        }
    }

    public void init(Context context) {
        try (Response rsp = client.newCall(initRequest(context)).execute()) {
            assert rsp.body() != null;
            String initBody = rsp.body().string();
            log.debug("init result: {}", initBody);
            InitInfo initInfo = Utils.objectMapper().readValue(initBody, InitInfo.class);
            context.setInitInfo(initInfo);
        } catch (Exception e) {
            log.error("init fail", e);
            System.exit(1);
        }
    }

    private Request generateUidRequest() {
        return new Request.Builder()
                .url(APIUrl.GENERATE_UID_URL)
                .get()
                .build();
    }

    private Request generateQrCodeRequest(String uuid) {
        return new Request.Builder()
                .url(String.format(APIUrl.GENERATE_QR_URL, uuid))
                .get()
                .build();
    }

    private Request checkLoginRequest(String uuid) {
        return new Request.Builder()
                .url(String.format(APIUrl.CHECK_LOGIN_URL, uuid))
                .get()
                .build();
    }

    private Request newLoginRequest(String uuid, String ticket) {
        return new Request.Builder()
                .url(String.format(APIUrl.LOGIN_URL, ticket, uuid))
                .addHeader("client-version", "2.0.0")
                .addHeader("extspam", "Go8FCIkFEokFCggwMDAwMDAwMRAGGvAESySibk50w5Wb3uTl2c2h64jVVrV7gNs06GFlWplHQbY/5FfiO++1yH4ykCyNPWKXmco+wfQzK5R98D3so7rJ5LmGFvBLjGceleySrc3SOf2Pc1gVehzJgODeS0lDL3/I/0S2SSE98YgKleq6Uqx6ndTy9yaL9qFxJL7eiA/R3SEfTaW1SBoSITIu+EEkXff+Pv8NHOk7N57rcGk1w0ZzRrQDkXTOXFN2iHYIzAAZPIOY45Lsh+A4slpgnDiaOvRtlQYCt97nmPLuTipOJ8Qc5pM7ZsOsAPPrCQL7nK0I7aPrFDF0q4ziUUKettzW8MrAaiVfmbD1/VkmLNVqqZVvBCtRblXb5FHmtS8FxnqCzYP4WFvz3T0TcrOqwLX1M/DQvcHaGGw0B0y4bZMs7lVScGBFxMj3vbFi2SRKbKhaitxHfYHAOAa0X7/MSS0RNAjdwoyGHeOepXOKY+h3iHeqCvgOH6LOifdHf/1aaZNwSkGotYnYScW8Yx63LnSwba7+hESrtPa/huRmB9KWvMCKbDThL/nne14hnL277EDCSocPu3rOSYjuB9gKSOdVmWsj9Dxb/iZIe+S6AiG29Esm+/eUacSba0k8wn5HhHg9d4tIcixrxveflc8vi2/wNQGVFNsGO6tB5WF0xf/plngOvQ1/ivGV/C1Qpdhzznh0ExAVJ6dwzNg7qIEBaw+BzTJTUuRcPk92Sn6QDn2Pu3mpONaEumacjW4w6ipPnPw+g2TfywJjeEcpSZaP4Q3YV5HG8D6UjWA4GSkBKculWpdCMadx0usMomsSS/74QgpYqcPkmamB4nVv1JxczYITIqItIKjD35IGKAUwAA==")
                .get()
                .build();
    }

    private Request initRequest(Context context) throws JsonProcessingException {
        BaseRequest req = new BaseRequest(context);
        return new Request.Builder()
                .url(String.format(APIUrl.INIT_URL, context.getPassTicket()))
                .post(RequestBody.create(Utils.objectMapper().writeValueAsBytes(req)))
                .build();
    }

}
