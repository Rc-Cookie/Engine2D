package com.github.rccookie.engine2d.impl.awt;

import java.util.Map;

import com.github.rccookie.engine2d.impl.HTTPResponseData;
import com.github.rccookie.engine2d.impl.OnlineManager;
import com.github.rccookie.engine2d.online.HTTPRequest;
import com.github.rccookie.http.HttpRequest;
import com.github.rccookie.util.Future;
import com.github.rccookie.util.FutureImpl;
import com.github.rccookie.util.ThreadedFutureImpl;

import org.jetbrains.annotations.NotNull;

/**
 * AWT implementation of an {@link OnlineManager}.
 */
public class AWTOnlineManager implements OnlineManager {

    @Override
    public Future<HTTPResponseData> sendHTTPRequest(@NotNull String url, @NotNull HTTPRequest.Method method, @NotNull Map<String,String> header, String data) {
        FutureImpl<HTTPResponseData> result = new ThreadedFutureImpl<>();
        HttpRequest request = new HttpRequest(url).setMethod(HttpRequest.Method.valueOf(method.toString()));
        header.forEach(request::setHeaderField);
        request.send(data).response.then(r -> result.complete(new HTTPResponseData(r.code, r.data, r.header)));
        return result;
    }
}
