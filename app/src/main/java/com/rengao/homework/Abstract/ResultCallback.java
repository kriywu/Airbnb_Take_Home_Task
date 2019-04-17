package com.rengao.homework.Abstract;

import okhttp3.Request;

/**
 * 用于联网回调接口
 */
public interface ResultCallback {
    void onSuccess(int page, String str);

    void onFailed(int page, Request request, Exception e);
}
