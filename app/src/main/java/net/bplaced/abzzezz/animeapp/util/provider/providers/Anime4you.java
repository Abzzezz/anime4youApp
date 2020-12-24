/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:28
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.ProviderType;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONException;
import org.json.JSONObject;

public class Anime4you extends Provider {

    public Anime4you() {
        super(ProviderType.ANIME4YOU, StringHandler.DATABASE);
    }

    @Override
    public JSONObject format(final Show show) throws JSONException {
        return new JSONObject()
                .put(StringHandler.SHOW_ID, show.getID())
                .put(StringHandler.SHOW_IMAGE_URL, StringHandler.COVER_DATABASE.concat(show.getShowJSON().getString("image_id")))
                .put(StringHandler.SHOW_EPISODE_COUNT, show.getShowJSON().getString("Letzte"))
                .put(StringHandler.SHOW_TITLE, show.getShowJSON().getString("titel"))
                .put(StringHandler.SHOW_LANG, show.getShowJSON().getString("Untertitel"))
                .put(StringHandler.SHOW_YEAR, show.getShowJSON().getString("Jahr"))
                .put(StringHandler.SHOW_PROVIDER, StringHandler.SHOW_PROVIDER_ANIME4YOU);
    }

    @Override
    public void handleDownload() {

    }
}
