package com.dattilio.patrick.duolingowordsearch.model;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * An OkHttp interceptor which transforms the input text from a series of Json objects delineated
 * by
 * newlines into an array of Json objects.
 */
public class DuolingoInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    String bodyString = response.body().string();
    StringBuilder builder = new StringBuilder("[]");
    builder.insert(1, bodyString.replaceAll("\n", ","));
    ResponseBody body = ResponseBody.create(response.body().contentType(), builder.toString());
    return response.newBuilder().body(body).build();
  }
}
