package com.fun.chufengpro.util;

import android.util.Log;

import com.fun.chufengpro.upload.MyStringCallBack;
import com.lzy.imagepicker.bean.ImageItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static final String TAG = "HttpUtil";
    private PostFormBuilder mPost;
    private GetBuilder mGet;

    public HttpUtil() {
        OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(15 * 1000L, TimeUnit.MILLISECONDS)
                .build();

        mPost = OkHttpUtils.post();
        mGet = OkHttpUtils.get();
    }

    //封装请求
    public void postRequest(String url, Map<String, String> params, MyStringCallBack callback) {
        mPost.url(url)
                .params(params)
                .build()
                .execute(callback);
    }

    //上传文件
    public void postFileRequest(String url, Map<String, String> params, ArrayList<ImageItem> pathList, MyStringCallBack callback) {

        Map<String,File> files = new HashMap<>();
        for (int i = 0; i < pathList.size(); i++) {
            String newPath = BitmapUtils.compressImageUpload(pathList.get(i).path);
            files.put(pathList.get(i).name+i,new File(newPath));
        }

        mPost.url(url)
                .files("files",files)
                .build()
                .execute(callback);
    }
//    上传文件
    public static String uploadFile(String uploadUrl, File file) {
        String encode = "utf-8";
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        Log.d(TAG, "upload url=" + uploadUrl);
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setDoInput(true); // 允许输入流


            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", encode); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            DataOutputStream dos = new DataOutputStream(
                    conn.getOutputStream());

            Log.d(TAG, "uploadFile: FILE是否为空" + file == null? "true":"false" + "文件名称:" + file.getPath());
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */

                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"Filedata\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + encode + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();

                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.d("Upload", "response code:" + res);
                // if(res==200)
                // {
                Log.d("Upload", "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.d("Upload", "result : " + result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
