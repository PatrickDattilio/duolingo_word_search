package com.dattilio.patrick.duolingowordsearch;

import android.app.Application;
import com.dattilio.patrick.duolingowordsearch.model.DuolingoApi;
import com.dattilio.patrick.duolingowordsearch.model.DuolingoInterceptor;
import com.dattilio.patrick.duolingowordsearch.model.WordSearch;
import com.dattilio.patrick.duolingowordsearch.model.WordSearchDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();

    OkHttpClient.Builder builder =
        new OkHttpClient.Builder().addInterceptor(new DuolingoInterceptor());
    if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor httpLogger = new HttpLoggingInterceptor();
      httpLogger.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(httpLogger);
    }
    OkHttpClient client = builder.build();

    Gson gson =
        new GsonBuilder().registerTypeAdapter(WordSearch.class, new WordSearchDeserializer())
            .create();

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3.amazonaws.com")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();

    DuolingoApi.set(retrofit.create(DuolingoApi.DuolingoService.class));
  }
}
