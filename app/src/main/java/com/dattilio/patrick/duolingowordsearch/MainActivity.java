package com.dattilio.patrick.duolingowordsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = fragmentManager.findFragmentByTag(WordSearchFragment.FRAGMENT_WORDSEARCH);
    if (fragment == null) {
      fragment = new WordSearchFragment();
    }
    fragmentManager.beginTransaction()
        .replace(R.id.frame, fragment, WordSearchFragment.FRAGMENT_WORDSEARCH)
        .commit();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.app_menu, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.app_menu_about) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment fragment = fragmentManager.findFragmentByTag(AboutFragment.FRAGMENT_ABOUT);
      if (fragment == null) {
        fragment = new AboutFragment();
      }
      fragmentManager.beginTransaction()
          .replace(R.id.frame, fragment, AboutFragment.FRAGMENT_ABOUT)
          .addToBackStack(AboutFragment.FRAGMENT_ABOUT)
          .commit();
    }
    return super.onOptionsItemSelected(item);
  }
}
