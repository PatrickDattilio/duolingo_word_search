package com.dattilio.patrick.duolingowordsearch.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 */
public class WordSearchDeserializer implements JsonDeserializer<WordSearch> {
  @Override
  public WordSearch deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    String sourceLanguage = object.get("source_language").getAsString();
    String targetLanguage = object.get("target_language").getAsString();
    String word = object.get("word").getAsString();
    String[][] characterGrid = context.deserialize(object.get("character_grid"), String[][].class);

    Type mapType = new TypeToken<Map<String, String>>() {
    }.getType();
    Map<String, String> translations = context.deserialize(object.get("word_locations"), mapType);
    return new WordSearch(sourceLanguage, targetLanguage, word, characterGrid,
        new ArrayList<>(translations.values()));
  }
}
