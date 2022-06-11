package com.github.rccookie.engine2d.impl.awt;

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
import com.github.rccookie.util.Future;
import com.github.rccookie.util.FutureImpl;
import com.github.rccookie.util.ThreadedFutureImpl;

import org.jetbrains.annotations.NotNull;

/**
 * AWT implementation of an {@link OnlineManager}.
 */
public class AWTOnlineManager implements OnlineManager {

    @Override
    public Future<HTTPResponseData> sendHTTPRequest(@NotNull String url, HTTPRequest.@NotNull Method method, @NotNull Map<String,String> header, String data) {
        //noinspection DuplicatedCode
        FutureImpl<HTTPResponseData> result = new ThreadedFutureImpl<>();
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

                result.complete(new HTTPResponseData(code, responseData, stringHeaders));
            } catch (IOException e) {
                result.fail(e);
            }
        }).start();
        return result;
    }
}
