package com.dattilio.patrick.duolingowordsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameOverFragment extends Fragment {

  public static final String FRAGMENT_GAME_OVER = "FRAGMENT_GAME_OVER";

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_game_over, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @OnClick(R.id.fragment_game_over_play_again) public void onClickPlayAgain() {
    getActivity().getSupportFragmentManager()
        .beginTransaction()
        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        .replace(R.id.frame, new WordSearchFragment(), WordSearchFragment.FRAGMENT_WORDSEARCH)
        .commit();
  }
}
