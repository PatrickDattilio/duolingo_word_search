package com.dattilio.patrick.duolingowordsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private static final String FRAGMENT_WORDSEARCH = "FRAGMENT_WORDSEARCH";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_WORDSEARCH);
    if (fragment == null) {
      fragment = new WordSearchFragment();
    }
    fragmentManager.beginTransaction().replace(R.id.frame, fragment, FRAGMENT_WORDSEARCH).commit();
  }
}
