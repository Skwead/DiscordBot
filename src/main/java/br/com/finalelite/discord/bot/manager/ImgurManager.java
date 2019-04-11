package br.com.finalelite.discord.bot.manager;

import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.val;
import okhttp3.*;

import java.io.IOException;

@RequiredArgsConstructor
public class ImgurManager {

    private final String clientId;

    private static final String UPLOAD_URL = "https://api.imgur.com/3/upload";

    public String upload(MediaType mediaType, byte[] bytes) {
        return upload(
                RequestBody
                        .create(mediaType, bytes)
        );
    }

    public String upload(String url) {
        return upload(
                new FormBody.Builder()
                        .add("image", url).build()
        );
    }

    /*
    https://apidocs.imgur.com/#c85c9dfc-7487-4de2-9ecd-66f727cf3139
     */
    public String upload(RequestBody body) {
        val client = new OkHttpClient();

        val request = new Request.Builder()
                .header("Authorization", "Client-ID " + clientId)
                .url(UPLOAD_URL)
                .post(body)
                .build();

        try (val response = client.newCall(request).execute()) {
            val rawJson = response.body().string();

            val json = new JsonParser().parse(rawJson).getAsJsonObject();
            val success = json.get("success").getAsBoolean();

            if (!success)
                return null;

            return json.get("data").getAsJsonObject().get("link").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
