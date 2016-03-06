package com.dattilio.patrick.duolingowordsearch;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import com.dattilio.patrick.duolingowordsearch.model.DuolingoInterceptor;
import com.dattilio.patrick.duolingowordsearch.model.WordSearch;
import com.dattilio.patrick.duolingowordsearch.model.WordSearchDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.riclage.boardview.BoardWord;
import com.riclage.boardview.WordSearchBoardView;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  public static final String CURRENT_POSITION = "CURRENT_POSITION";
  public static final String WORDSEARCHES = "WORDSEARCHES";
  private WordSearchBoardView boardView;
  private int position = -1;
  private TextView textView;
  private Button nextButton;
  private DisplayMetrics metrics;
  private ArrayList<WordSearch> wordSearches;
  private WordSearchBoardView.OnWordSelectedListener selectedListener;
  private WordSearchBoardView.OnWordHighlightedListener highlightedListener;
  private float originalPosition;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    OkHttpClient client =
        new OkHttpClient.Builder().addInterceptor(new DuolingoInterceptor()).build();

    Gson gson =
        new GsonBuilder().registerTypeAdapter(WordSearch.class, new WordSearchDeserializer())
            .create();

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3.amazonaws.com")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();

    DuolingoService service = retrofit.create(DuolingoService.class);
    boardView = (WordSearchBoardView) findViewById(R.id.board_view);
    boardView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            originalPosition = boardView.getX();
            if (Build.VERSION.SDK_INT < 16) {
              boardView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
              boardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
          }
        });

    textView = (TextView) findViewById(R.id.source_word);
    nextButton = (Button) findViewById(R.id.next);
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        nextButton.animate().alpha(0).start();
        replaceBoard();
      }
    });

    selectedListener = new WordSearchBoardView.OnWordSelectedListener() {
      @Override public boolean onWordSelected(BoardWord selectedWord) {
        return wordSearches.get(position).translations.contains(selectedWord.toString());
      }
    };

    highlightedListener = new WordSearchBoardView.OnWordHighlightedListener() {
      @Override public void onWordHighlighted(BoardWord boardWord) {
        wordSearches.get(position).translations.remove(boardWord.toString());
        if (wordSearches.get(position).translations.isEmpty()) {
          nextButton.animate().alpha(1f).start();
        }
      }
    };

    if (savedInstanceState != null) {
      position = savedInstanceState.getInt(CURRENT_POSITION);
      wordSearches = savedInstanceState.getParcelableArrayList(WORDSEARCHES);
      setupWordSearch(wordSearches.get(position), boardView, textView);
    } else {

      service.getWordSearches()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Action1<ArrayList<WordSearch>>() {
            @Override public void call(ArrayList<WordSearch> wordSearchList) {
              wordSearches = wordSearchList;
              position = 0;
              setupWordSearch(wordSearches.get(position), boardView, textView);
            }
          });
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putInt(CURRENT_POSITION, position);
    outState.putParcelableArrayList(WORDSEARCHES, wordSearches);
    super.onSaveInstanceState(outState);
  }

  private void setupWordSearch(final WordSearch search, final WordSearchBoardView boardView,
      final TextView textView) {
    boardView.setOnWordSelectedListener(selectedListener);
    boardView.setOnWordHighlightedListener(highlightedListener);
    textView.setText(search.word);
    boardView.setLetterBoard(search.characterGrid);
    if (search.translations.isEmpty()) {
      nextButton.animate().alpha(1f).start();
    }
  }

  private void replaceBoard() {
    Animator boardExit = ObjectAnimator.ofFloat(boardView, "x", -boardView.getWidth());
    boardExit.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {
      }

      @Override public void onAnimationEnd(Animator animation) {
        boardView.clearBoard();
        position++;
        setupWordSearch(wordSearches.get(position), boardView, textView);
        boardView.setX(metrics.widthPixels);
      }

      @Override public void onAnimationCancel(Animator animation) {
      }

      @Override public void onAnimationRepeat(Animator animation) {
      }
    });

    Animator boardEnter = ObjectAnimator.ofFloat(boardView, "x", originalPosition);
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(boardExit, boardEnter);
    animatorSet.start();
  }
}
