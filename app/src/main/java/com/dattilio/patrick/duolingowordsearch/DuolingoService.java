package com.dattilio.patrick.duolingowordsearch;

import com.dattilio.patrick.duolingowordsearch.model.WordSearch;
import java.util.ArrayList;
import java.util.List;
import retrofit2.http.GET;
import rx.Observable;

public interface DuolingoService {

  @GET("/duolingo-data/s3/js2/find_challenges.txt") Observable<ArrayList<WordSearch>> getWordSearches();
}
