package com.github.rccookie.engine2d.impl.greenfoot.online;

import java.util.HashMap;
import java.util.Map;

import com.github.rccookie.engine2d.impl.HTTPResponseData;
import com.github.rccookie.engine2d.impl.OnlineManager;
import com.github.rccookie.engine2d.online.HTTPRequest;
import com.github.rccookie.engine2d.util.Future;
import com.github.rccookie.engine2d.util.FutureImpl;
import com.github.rccookie.engine2d.util.Pool;

import org.teavm.jso.JSBody;

public class OnlineGreenfootOnlineManager implements OnlineManager {

    public static final OnlineGreenfootOnlineManager INSTANCE = new OnlineGreenfootOnlineManager();

    private static int nextID = 0;
    private static final Pool<Integer> resultIDs = new Pool<>(() -> nextID++);
    private static final Map<Integer, FutureImpl<HTTPResponseData>> runningResults = new HashMap<>();

    static {
        if(System.currentTimeMillis() < 0) {
            acceptResult(-1, -1, null, null);
            acceptError(-1);
        }
    }





    @SuppressWarnings("unchecked")
    @Override
    public Future<HTTPResponseData> sendHTTPRequest(String url, HTTPRequest.Method method, Map<String, String> header, String data) {
        FutureImpl<HTTPResponseData> result = new FutureImpl<>();
        int id = resultIDs.get();
        runningResults.put(id, result);

        String[] headerArray = new String[header.size() * 2];
        Object[] headerEntries = header.entrySet().toArray();
        for(int i=0; i<headerEntries.length; i++) {
            headerArray[2*i] = ((Map.Entry<String,String>) headerEntries[i]).getKey();
            headerArray[2*i+1] = ((Map.Entry<String,String>) headerEntries[i]).getValue();
        }

        sendHTTPRequest0(id, url, method.toString(), headerArray, data);
        return result;
    }

    @JSBody(params = { "id", "url", "method", "header", "data" }, script =
            "var request = new XMLHttpRequest();" +
            "request.open(method, url, true);" +
            "var oldCookies = null;" +
            "var cookies = null;" +
            "for(let i=0; i<header.length / 2; i++) {" +
            "    if('Cookie' !== header[2*i])" +
            "        request.setRequestHeader(header[2*i], header[2*i+1]);" +
            "    else {" +
            "        oldCookies = document.cookie;" +
            "        document.cookie = cookies = header[2*i+1];" +
            "    }" +
            "}" +
            "request.onload = () => {" +
            "        if(cookies != null) {" +
            "            document.cookie = cookies.replaceAll('=.*;', '=').replaceAll('=.*$');" +
            "            document.cookie = oldCookies;" +
            "        }" +
            "        cgreigo_OnlineGreenfootOnlineManager_acceptResult(id, request.status, $rt_str(request.responseText), $rt_str(request.getAllResponseHeaders()));" +
            "};" +
            "request.onerror = () => {" +
            "        if(cookies != null) {" +
            "            document.cookie = cookies.replaceAll('=.*;', '=').replaceAll('=.*$');" +
            "            document.cookie = oldCookies;" +
            "        }" +
            "        cgreigo_OnlineGreenfootOnlineManager_acceptError(id);" +
            "};" +
            "request.send(data);"
    )
    private static native void sendHTTPRequest0(int id, String url, String method, String[] header, String data);



    private static void acceptResult(int id, int code, String data, String header) {
        FutureImpl<HTTPResponseData> result = runningResults.get(id);
        runningResults.remove(id);
        resultIDs.returnObject(id);

        Map<String,String> headerMap = new HashMap<>();
        if(header != null)
            for(String headerEntry : split(header, "\r\n"))
                if(headerEntry.contains(": "))
                    headerMap.put(headerEntry.substring(0, headerEntry.indexOf(": ")), headerEntry.substring(headerEntry.indexOf(": ") + 2));

        result.setValue(new HTTPResponseData(code, data, headerMap));
    }

    private static void acceptError(int id) {
        FutureImpl<HTTPResponseData> result = runningResults.get(id);
        runningResults.remove(id);
        resultIDs.returnObject(id);
        result.cancel();
    }

    @JSBody(params = { "str", "regex" }, script = "return str.split(regex)")
    private static native String[] split(String str, String regex);
}
