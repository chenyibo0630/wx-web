package com.bob.wechat.utils;

import lombok.Getter;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.net.Proxy;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";

    @Getter
    private static final OkHttpClient client;

    static {
        ConnectionPool connectionPool = new ConnectionPool(10, 5, TimeUnit.MINUTES);
        client = new OkHttpClient.Builder()
                .proxy(Proxy.NO_PROXY)
//                .hostnameVerifier((s, sslSession) -> true)
                .cookieJar(new CookieJarImpl())
                .connectionPool(connectionPool)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    static class CookieJarImpl implements CookieJar {

        private final TreeSet<Cookie> cookies = new TreeSet<>(new CookieIdentityComparator());

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
            List<Cookie> rs = new ArrayList<>();
            Iterator<Cookie> iter = cookies.iterator();
            long current = System.currentTimeMillis();
            while (iter.hasNext()) {
                Cookie cookie = iter.next();
                if (cookie.expiresAt() < current) {
                    iter.remove();
                    continue;
                }
                if (cookie.matches(httpUrl)) {
                    rs.add(cookie);
                }
            }
            return rs;
        }

        @Override
        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            cookies.addAll(list);
        }
    }

    static class CookieIdentityComparator implements Comparator<Cookie> {

        @Override
        public int compare(final Cookie c1, final Cookie c2) {
            int res = c1.name().compareTo(c2.name());
            if (res == 0) {
                // do not differentiate empty and null domains
                String d1 = c1.domain();
                if (d1.indexOf('.') == -1) {
                    d1 = d1 + ".local";
                }
                String d2 = c2.domain();
                if (d2.indexOf('.') == -1) {
                    d2 = d2 + ".local";
                }
                res = d1.compareToIgnoreCase(d2);
            }
            if (res == 0) {
                String p1 = c1.path();
                String p2 = c2.path();
                res = p1.compareTo(p2);
            }
            return res;
        }

    }

}
