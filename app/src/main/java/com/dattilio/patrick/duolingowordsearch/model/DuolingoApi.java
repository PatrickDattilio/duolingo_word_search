package com.dattilio.patrick.duolingowordsearch.model;

import java.util.ArrayList;
import retrofit2.http.GET;
import rx.Observable;

public class DuolingoApi {

  private static DuolingoService INSTANCE;

  public static DuolingoService get() {
    return INSTANCE;
  }

  public static void set(DuolingoService service) {
    INSTANCE = service;
  }

  public interface DuolingoService {

    @GET("/duolingo-data/s3/js2/find_challenges.txt")
    Observable<ArrayList<WordSearch>> getWordSearches();
  }
}
