/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 14:08
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShowSaver {

    /**
     * Editor and preferences
     */
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences publicPreferences;

    private final List<Show> shows = new ArrayList<>();

    @SuppressLint("CommitPrefEdits")
    public ShowSaver(final Context context) {
        this.preferences = context.getSharedPreferences("List", Context.MODE_PRIVATE);
        this.editor = preferences.edit();
        this.publicPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        for (int i = 0; i < preferences.getAll().size(); i++) {
            try {
                final String iString = String.valueOf(i);
                final JSONObject json = new JSONObject(preferences.getString(iString, "{}"));
                this.shows.add(new Show(json));
            } catch (JSONException e) {
                Log.e("Show loading", "Loading shows");
                e.printStackTrace();
            }
        }
        Logger.log("Saver set up.", Logger.LogType.INFO);
    }


    private void commitShow(final Show show) {
        final String preferenceSize = String.valueOf(preferences.getAll().size());
        editor.putString(preferenceSize, show.toString());
        editor.commit();
    }

    private void updateShow(final Show show, final int index) {
        editor.putString(String.valueOf(index), show.toString());
        editor.commit();
    }

    private void removeShow(final int index) {
        //Remove key (int)
        editor.remove(String.valueOf(index));
        /*
        Move all upcoming entries one down
         */
        for (int i = index; i < preferences.getAll().size() - /*One gone */ 1; i++) {
            editor.putString(String.valueOf(i), preferences.getString(String.valueOf(i + /* Next one */ 1), "NULL"));
            editor.remove(String.valueOf(i + 1));
        }
        //Apply to file
        editor.commit();
    }


    /**
     * Add anime with key and values to preference hashmap
     * then commit
     *
     * @param show
     */
    public void addShow(final Show show) throws JSONException {
        if (publicPreferences.getBoolean("check_existing", false) && containsShow(show)) return;

        this.shows.add(show);
        this.commitShow(show);
    }

    /**
     * Add anime with key and values to preference hashmap
     * then commit
     *
     * @param jsonObject
     */
    public void addShow(final JSONObject jsonObject) throws JSONException {
        final Show show = new Show(jsonObject);
        if (publicPreferences.getBoolean("check_existing", false) && containsShow(show)) return;

        this.shows.add(show);
        this.commitShow(show);
    }

    /**
     * Refresh show index
     *
     * @param show  show details to overwrite
     * @param index shows index
     */
    public void refreshShow(final Show show, final int index) {
        shows.set(index, show);
        this.updateShow(show, index);
    }


    /**
     * Check if preferences contain a certain id
     *
     * @param show to search
     * @return id contained
     */
    public boolean containsShow(final Show show) {
        return shows.contains(show);
    }

    /**
     * Remove key from map then instantly commit
     *
     * @param index
     */
    public void remove(final int index) {
        shows.remove(index);
        this.removeShow(index);
    }

    /**
     * @param index key
     * @return new JSON object
     */
    public Optional<Show> getShow(final int index) {
        return Optional.ofNullable(shows.get(index));
    }

    /**
     * @return all size
     */
    public int getShowSize() {
        return shows.size();
    }

}
