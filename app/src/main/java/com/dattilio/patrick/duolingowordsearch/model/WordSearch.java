package com.dattilio.patrick.duolingowordsearch.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WordSearch implements Parcelable {
  public static final Parcelable.Creator<WordSearch> CREATOR =
      new Parcelable.Creator<WordSearch>() {
        public WordSearch createFromParcel(Parcel source) {
          return new WordSearch(source);
        }

        public WordSearch[] newArray(int size) {
          return new WordSearch[size];
        }
      };
  public final @SerializedName("source_language") String sourceLanguage;
  public final @SerializedName("target_language") String targetLanguage;
  public final String word;
  public final @SerializedName("character_grid") String[][] characterGrid;
  public final @SerializedName("word_locations") List<String> translations;

  public WordSearch(String sourceLanguage, String targetLanguage, String word,
      String[][] characterGrid, List<String> translations) {
    this.sourceLanguage = sourceLanguage;
    this.targetLanguage = targetLanguage;
    this.word = word;
    this.characterGrid = characterGrid;
    this.translations = translations;
  }

  protected WordSearch(Parcel in) {
    this.sourceLanguage = in.readString();
    this.targetLanguage = in.readString();
    this.word = in.readString();
    int size = in.readInt();
    this.characterGrid = new String[size][];
    for (int i = 0; i < size; i++) {
      in.readStringArray(this.characterGrid[i]);
    }
    this.translations = in.createStringArrayList();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.sourceLanguage);
    dest.writeString(this.targetLanguage);
    dest.writeString(this.word);
    dest.writeInt(this.characterGrid.length);
    for (String[] array : this.characterGrid) {
      dest.writeStringArray(array);
    }
    dest.writeStringList(this.translations);
  }
}
