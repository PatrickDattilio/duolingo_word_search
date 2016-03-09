package com.dattilio.patrick.duolingowordsearch.model;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DuolingoInterceptorTest {

  @Test public void interceptorTest() throws IOException {
    Interceptor interceptor = new DuolingoInterceptor();
    Interceptor.Chain chain = mock(Interceptor.Chain.class);
    Request request = mock(Request.class);
    Response mockResponse = mock(Response.class);
    ResponseBody responseBody = mock(ResponseBody.class);

    when(chain.request()).thenReturn(request);
    when(chain.proceed(request)).thenReturn(mockResponse);
    when(mockResponse.body()).thenReturn(responseBody);
    when(responseBody.string()).thenReturn("");
    Response response = interceptor.intercept(chain);
    assertEquals("[]", response.body().string());
  }
}