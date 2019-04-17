package com.rengao.homework.Util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.rengao.homework.Abstract.ResultCallback;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 封装联网层
 * DCL单例模式
 */

public class OkHttpEngine {
    private static final String TAG = "OkHttpEngine";
    private static volatile OkHttpEngine engine;
    private OkHttpClient okHttpClient;
    private Handler handler;

    public static OkHttpEngine getInstance(Context context) {
        if (engine == null) {
            synchronized (OkHttpClient.class) {
                if (engine == null) {
                    engine = new OkHttpEngine(context);
                }
            }
        }
        return engine;
    }

    private OkHttpEngine(Context context) {
        File sdcache = context.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;//10 MB
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
        okHttpClient = builder.build();
        handler = new Handler();

    }

    public void get(final String name, final int page, final ResultCallback callback) {
        String url = HttpStringUtil.generateURL(name, page);
        Log.d(TAG, "get: url = " + url) ;
        final Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(page, request, e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body;
                if (response.body() != null)
                    body = response.body().string();
                else
                    body = null;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(page, body);
                    }
                });
            }
        });
    }
}
