package com.dattilio.patrick.duolingowordsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

  public static final String FRAGMENT_ABOUT = "FRAGMENT_ABOUT";

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_about, container, false);
    String about = getString(R.string.about_data);
    ((TextView) view.findViewById(R.id.fragment_about_text)).setText(Html.fromHtml(about));
    return view;
  }
}
