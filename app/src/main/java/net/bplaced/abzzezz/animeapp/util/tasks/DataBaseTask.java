/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:13
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import net.bplaced.abzzezz.animeapp.util.scripter.AniDBSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class DataBaseTask implements Callable<JSONObject> {

    private final AniDBSearch aniDBSearch;
    private String id;

    public DataBaseTask(final JSONObject details, final AniDBSearch aniDBSearch) {
        this.aniDBSearch = aniDBSearch;
        try {
            this.id = details.getString(StringHandler.SHOW_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public DataBaseTask(String id, AniDBSearch aniDBSearch) {
        this.aniDBSearch = aniDBSearch;
        this.id = id;
    }

    @Override
    public JSONObject call() {
        try {
            return getDetails(new JSONObject(aniDBSearch.getShowDetails("{\"aid\":\"" + id.concat("\""))));
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject inf = new JSONObject();
        inf.put(StringHandler.SHOW_ID, id);
        inf.put(StringHandler.SHOW_IMAGE_URL, StringHandler.COVER_DATABASE.concat(jsonObject.getString("image_id")));
        inf.put(StringHandler.SHOW_EPISODES_COUNT, jsonObject.getString("Letzte"));
        inf.put(StringHandler.SHOW_TITLE, jsonObject.getString("titel"));
        inf.put(StringHandler.SHOW_LANG, jsonObject.getString("Untertitel"));
        inf.put(StringHandler.SHOW_YEAR, jsonObject.getString("Jahr"));
        return inf;
    }
}
