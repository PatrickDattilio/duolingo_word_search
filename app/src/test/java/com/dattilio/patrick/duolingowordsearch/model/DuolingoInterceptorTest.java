package com.dattilio.patrick.duolingowordsearch.model;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DuolingoInterceptorTest {

  public final MockWebServer server = new MockWebServer();
  private final DuolingoInterceptor interceptor = new DuolingoInterceptor();
  private OkHttpClient client;
  private String host;
  private HttpUrl url;

  @Before public void setUp() {
    client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    host = server.getHostName() + ":" + server.getPort();
    url = server.url("/");
  }

  @Test public void interceptorEmptyTest() throws IOException {
    server.enqueue(new MockResponse().setBody(""));
    Response response = client.newCall(request().build()).execute();
    assertEquals("[]", response.body().string());
  }

  @Test public void interceptorSingleTest() throws IOException {
    server.enqueue(new MockResponse().setBody("{}"));
    Response response = client.newCall(request().build()).execute();
    assertEquals("[{}]", response.body().string());
  }

  @Test public void interceptorDoubleTest() throws IOException {
    server.enqueue(new MockResponse().setBody("{}\n{}"));
    Response response = client.newCall(request().build()).execute();
    assertEquals("[{},{}]", response.body().string());
  }

  private Request.Builder request() {
    return new Request.Builder().url(url);
  }
}