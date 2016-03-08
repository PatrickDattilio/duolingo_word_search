package com.dattilio.patrick.duolingowordsearch;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.dattilio.patrick.duolingowordsearch.model.DuolingoApi;
import com.dattilio.patrick.duolingowordsearch.model.WordSearch;
import com.riclage.boardview.BoardWord;
import com.riclage.boardview.WordSearchBoardView;
import java.util.ArrayList;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WordSearchFragment extends Fragment {

  private static final String CURRENT_POSITION = "CURRENT_POSITION";
  private static final String WORDSEARCHES = "WORDSEARCHES";
  @Bind(R.id.board_view) WordSearchBoardView boardView;
  @Bind(R.id.source_word) TextView sourceWord;
  @Bind(R.id.remaining) TextView remaining;
  @Bind(R.id.next) Button nextButton;

  private int position = -1;
  private DisplayMetrics metrics;
  private ArrayList<WordSearch> wordSearches;
  private WordSearchBoardView.OnWordSelectedListener selectedListener;
  private WordSearchBoardView.OnWordHighlightedListener highlightedListener;
  private float boardX;
  private Animator.AnimatorListener animatorListener;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      position = savedInstanceState.getInt(CURRENT_POSITION);
      wordSearches = savedInstanceState.getParcelableArrayList(WORDSEARCHES);
    } else {
      DuolingoApi.get()
          .getWordSearches()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Action1<ArrayList<WordSearch>>() {
            @Override public void call(ArrayList<WordSearch> wordSearchList) {
              wordSearches = wordSearchList;
              position = 0;
              setupWordSearch(wordSearches.get(position), boardView, sourceWord);
            }
          });
    }
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    metrics = new DisplayMetrics();
    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_wordsearch, container, false);
    ButterKnife.bind(this, view);
    boardView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            boardX = boardView.getX();
            if (Build.VERSION.SDK_INT < 16) {
              boardView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
              boardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
          }
        });
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
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
        //Remove the valid word from our translations. If we are out of translations, we should allow
        // the user to transition to the next WordSearch. If we are all out of WordSearches then they have won!
        wordSearches.get(position).translations.remove(boardWord.toString());
        if (wordSearches.get(position).translations.isEmpty()) {
          if (position == wordSearches.size() - 1) {
            //Win
          } else {
            animateBoardStatus();
          }
        } else {
          if (remaining.getAlpha() == 0) {
            remaining.animate().alpha(1f).start();
          }
          int count = wordSearches.get(position).translations.size();
          remaining.setText(
              getResources().getQuantityString(R.plurals.translations_remaining, count, count));
        }
      }
    };

    animatorListener = new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {
      }

      @Override public void onAnimationEnd(Animator animation) {
        boardView.clearBoard();
        position++;
        setupWordSearch(wordSearches.get(position), boardView, sourceWord);
        boardView.setX(metrics.widthPixels);
      }

      @Override public void onAnimationCancel(Animator animation) {
      }

      @Override public void onAnimationRepeat(Animator animation) {
      }
    };
    return view;
  }

  @Override public void onResume() {
    super.onResume();
    if (wordSearches != null) {
      setupWordSearch(wordSearches.get(position), boardView, sourceWord);
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
    ObjectAnimator nextButtonAlphaOut = ObjectAnimator.ofFloat(nextButton, "alpha", 0);
    ObjectAnimator remainingAlphaOut = ObjectAnimator.ofFloat(remaining, "alpha", 0);
    ObjectAnimator boardExit = ObjectAnimator.ofFloat(boardView, "x", -boardView.getWidth());
    boardExit.addListener(animatorListener);

    AnimatorSet out = new AnimatorSet();
    out.playTogether(nextButtonAlphaOut, remainingAlphaOut, boardExit);

    Animator boardEnter = ObjectAnimator.ofFloat(boardView, "x", boardX);

    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(out, boardEnter);
    animatorSet.start();
  }

  private void animateBoardStatus() {

    ObjectAnimator remainingAlphaOut = ObjectAnimator.ofFloat(remaining, "alpha", 0);
    remainingAlphaOut.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {

      }

      @Override public void onAnimationEnd(Animator animation) {
        int count = wordSearches.size() - position;
        remaining.setText(
            getResources().getQuantityString(R.plurals.word_searches_remaining, count, count));
      }

      @Override public void onAnimationCancel(Animator animation) {

      }

      @Override public void onAnimationRepeat(Animator animation) {

      }
    });

    ObjectAnimator nextButtonAlphaIn = ObjectAnimator.ofFloat(nextButton, "alpha", 1f);
    ObjectAnimator remainingAlphaIn = ObjectAnimator.ofFloat(remaining, "alpha", 1f);

    AnimatorSet alphaIn = new AnimatorSet();
    alphaIn.playTogether(nextButtonAlphaIn, remainingAlphaIn);

    AnimatorSet set = new AnimatorSet();
    set.playSequentially(remainingAlphaOut, alphaIn);
    set.start();
  }
}
