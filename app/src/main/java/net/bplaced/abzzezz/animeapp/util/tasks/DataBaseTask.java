/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:13
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class DataBaseTask implements Callable<JSONObject> {

    private final DataBaseSearch dataBaseSearch;
    private String id;

    public DataBaseTask(final JSONObject details, final DataBaseSearch dataBaseSearch) {
        this.dataBaseSearch = dataBaseSearch;
        try {
            this.id = details.getString(StringHandler.SHOW_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public DataBaseTask(String id, DataBaseSearch dataBaseSearch) {
        this.dataBaseSearch = dataBaseSearch;
        this.id = id;
    }

    @Override
    public JSONObject call() {
        try {
            return getDetails(new JSONObject(dataBaseSearch.getShowDetails("{\"aid\":\"" + id.concat("\""))));
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
