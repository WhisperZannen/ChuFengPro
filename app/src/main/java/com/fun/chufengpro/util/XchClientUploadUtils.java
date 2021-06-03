package com.fun.chufengpro.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.*;


public class XchClientUploadUtils {
    public static ResponseBody upload(String url, String filePath, String fileName,UUID uuid) throws Exception{
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",fileName,RequestBody.create(MediaType.parse("multipart/form-data"),new File(filePath)))
                .build();
        Request request = new Request.Builder()
//                .header("Authorization","Client-ID" + uuid)
                .header("Authorization",uuid+"")
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code" + response);
        return response.body();
    }
}
