package com.github.rccookie.engine2d.impl.greenfoot.offline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rccookie.engine2d.impl.HTTPResponseData;
import com.github.rccookie.engine2d.impl.OnlineManager;
import com.github.rccookie.engine2d.online.HTTPRequest;
import com.github.rccookie.engine2d.util.Future;
import com.github.rccookie.engine2d.util.FutureImpl;

public enum OfflineGreenfootOnlineManager implements OnlineManager {

    INSTANCE;

    @Override
    public Future<HTTPResponseData> sendHTTPRequest(String url, HTTPRequest.Method method, Map<String, String> header, String data) {
        FutureImpl<HTTPResponseData> result = new FutureImpl<>();
        new Thread(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setRequestMethod(method.toString());
                header.forEach(con::setRequestProperty);

                if(data != null) {
                    con.setDoOutput(true);
                    con.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
                }

                int code = con.getResponseCode();
                InputStream in = code < 400 ? con.getInputStream() : con.getErrorStream();
                ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int length; (length = in.read(buffer)) != -1; ) {
                    resultStream.write(buffer, 0, length);
                }
                String responseData = resultStream.toString(StandardCharsets.UTF_8);
                Map<String, List<String>> resultHeader = con.getHeaderFields();
                Map<String, String> stringHeaders = new HashMap<>(resultHeader.size());
                resultHeader.forEach((k, vs) -> stringHeaders.put(k, String.join(";", vs)));

                result.setValue(new HTTPResponseData(code, responseData, stringHeaders));
            } catch (IOException e) {
                result.cancel();
            }
        }).start();
        return result;
    }
}
