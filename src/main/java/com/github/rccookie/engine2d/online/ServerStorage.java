package com.github.rccookie.engine2d.online;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonArray;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.util.Console;

class ServerStorage {

    private static final JsonObject DEFAULT = new JsonObject(
            "score", 0,
            "num", new JsonArray(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            "str", new JsonArray("", "", "", "", ""),
            "username", "RcCookie",
            "rank", 1,
            "imageUrl", "/photo_attachments/0002/6442/8B16AF38-EF5D-47B2-A318-65163E99D90C_thumb.jpeg"
    );

    @SuppressWarnings("SpellCheckingInspection")
    public static JsonObject read() throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(
                "https://www.greenfoot.org/scenarios/29116/userinfo/all_user_data.json?user_id=52320&passcode=0C17E040CAB9BBF9").openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Cookie", "_myothergame_session=UnFvNGFuWDJDUnF5ZzJSTDBpWS9McnU4cS9lMWs2cHRLWlRLUDRHTC9Lb25DK0dOeVdkRlBTQzA4NjlMc0pIZDBFTVB2NTljNGpmQk1LSHJSQzIxRnc4N1NlaDdYQ29yWUxURk91TlZWWk5CT0tXb0JHZzU3WHkrMmkwVFR2OXdBQ2NSOXd1MU85VnNTZmd2dlRaQUNRPT0tLTI1MVhCcDhNYnZsKzZiU0toNkVpTUE9PQ%3D%3D--c29403dc878706ea10536fad7bbce22041fa30ca;user_code=75c92c1fa1b6d5731f27a2071ac7163c327e6c98bd007e0c04eaa3168e78efb0;user_id=52320");

        return Json.parse(connection.getInputStream()).get(0).or(DEFAULT);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static void write(JsonObject data) throws Exception {
        data = data.clone();
        System.out.println(data);
        data.combine(read());
        System.out.println(data);
        HttpsURLConnection connection = (HttpsURLConnection) new URL(
                "https://www.greenfoot.org/scenarios/29116/userinfo/set_user_data.json?" +
                        "scenario_id=29116&" +
                        "&passcode=0C17E040CAB9BBF9&" +
                        "&score=" + data.getInt("score") +
                        "&num0=" +  data.getArray("num").getInt(0) +
                        "&num1=" +  data.getArray("num").getInt(1) +
                        "&num2=" +  data.getArray("num").getInt(2) +
                        "&num3=" +  data.getArray("num").getInt(3) +
                        "&num4=" +  data.getArray("num").getInt(4) +
                        "&num5=" +  data.getArray("num").getInt(5) +
                        "&num6=" +  data.getArray("num").getInt(6) +
                        "&num7=" +  data.getArray("num").getInt(7) +
                        "&num8=" +  data.getArray("num").getInt(8) +
                        "&num9=" +  data.getArray("num").getInt(9) +
                        "&str0=" +  toValidString(data.getArray("str").getString(0)) +
                        "&str1=" +  toValidString(data.getArray("str").getString(1)) +
                        "&str2=" +  toValidString(data.getArray("str").getString(2)) +
                        "&str3=" +  toValidString(data.getArray("str").getString(3)) +
                        "&str4=" +  toValidString(data.getArray("str").getString(4))
        ).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Cookie", "_myothergame_session=UnFvNGFuWDJDUnF5ZzJSTDBpWS9McnU4cS9lMWs2cHRLWlRLUDRHTC9Lb25DK0dOeVdkRlBTQzA4NjlMc0pIZDBFTVB2NTljNGpmQk1LSHJSQzIxRnc4N1NlaDdYQ29yWUxURk91TlZWWk5CT0tXb0JHZzU3WHkrMmkwVFR2OXdBQ2NSOXd1MU85VnNTZmd2dlRaQUNRPT0tLTI1MVhCcDhNYnZsKzZiU0toNkVpTUE9PQ%3D%3D--c29403dc878706ea10536fad7bbce22041fa30ca;user_code=75c92c1fa1b6d5731f27a2071ac7163c327e6c98bd007e0c04eaa3168e78efb0;user_id=52320");

        connection.connect();
        Console.map("Response code", connection.getResponseCode());
    }

    private static String toValidString(String str) {
        return URLEncoder.encode(str.length() > 50 ? str.substring(0, 50) : str, StandardCharsets.UTF_8);
    }
}
