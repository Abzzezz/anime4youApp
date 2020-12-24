/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:07
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import net.bplaced.abzzezz.animeapp.util.scripter.Anime4YouDBSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class Anime4YouDataBaseTask implements Callable<JSONObject> {

    private final Anime4YouDBSearch anime4YouDBSearch;
    private String id;

    public Anime4YouDataBaseTask(final JSONObject details, final Anime4YouDBSearch anime4YouDBSearch) {
        this.anime4YouDBSearch = anime4YouDBSearch;
        try {
            this.id = details.getString(StringHandler.SHOW_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Anime4YouDataBaseTask(String id, Anime4YouDBSearch anime4YouDBSearch) {
        this.anime4YouDBSearch = anime4YouDBSearch;
        this.id = id;
    }

    @Override
    public JSONObject call() {
        try {
            return getDetails(new JSONObject(anime4YouDBSearch.getShowDetails("{\"aid\":\"" + id.concat("\""))));
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getDetails(final JSONObject jsonObject) throws Exception {
        return new JSONObject()
                .put(StringHandler.SHOW_ID, id)
                .put(StringHandler.SHOW_IMAGE_URL, StringHandler.COVER_DATABASE.concat(jsonObject.getString("image_id")))
                .put(StringHandler.SHOW_EPISODES_COUNT, jsonObject.getString("Letzte"))
                .put(StringHandler.SHOW_TITLE, jsonObject.getString("titel"))
                .put(StringHandler.SHOW_LANG, jsonObject.getString("Untertitel"))
                .put(StringHandler.SHOW_YEAR, jsonObject.getString("Jahr"))
                .put(StringHandler.SHOW_PROVIDER, StringHandler.SHOW_PROVIDER_ANIME4YOU);
    }
}
