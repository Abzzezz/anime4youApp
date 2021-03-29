/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:23
 */

package net.bplaced.abzzezz.animeapp.util.show;

import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.string.StringHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class Show {

    private final String malID;
    private final String showTitle;
    private final double showScore;
    private final String imageURL;
    private final int episodeCount;

    private final JSONObject providers;

    /**
     * Basic Show object constructed from MAL data
     *
     * @param id           MAL ID
     * @param title        Title fetched from MAL
     * @param episodeCount episode count
     * @param imageURL     image url from MAL
     * @param showScore    the year the show was released
     */
    public Show(String id, String title, int episodeCount, String imageURL, final double showScore) {
        this.malID = id;
        this.showTitle = title;
        this.episodeCount = episodeCount;
        this.imageURL = imageURL;
        this.showScore = showScore;
        this.providers = new JSONObject();
    }

    /**
     * Retrieve Show from formatted data
     *
     * @param showJSON showJSON to restore the show from
     * @throws JSONException json
     */
    public Show(final JSONObject showJSON) throws JSONException {
        this.malID = showJSON.getString(StringHandler.SHOW_ID);
        this.showTitle = showJSON.getString(StringHandler.SHOW_TITLE);
        this.episodeCount = showJSON.getInt(StringHandler.SHOW_EPISODE_COUNT);
        this.imageURL = showJSON.getString(StringHandler.SHOW_IMAGE_URL);
        this.showScore = showJSON.getDouble(StringHandler.SHOW_SCORE);
        this.providers = showJSON.getJSONObject("provider_info");
    }

    /**
     * Gets all episodes from a certain provider
     *
     * @param provider provider to get the episodes from
     * @return JSONArray with all episode referrals
     */
    public JSONArray getShowEpisodes(final Provider provider) {
        try {
            final String providerName = provider.getName();
            if (!providers.has(providerName)) {
                Logger.log("Cannot retrieve provider information. No json object with the given provider was found", Logger.LogType.ERROR);
                return new JSONArray();
            }

            final JSONObject providerJSON = providers.getJSONObject(providerName); //Retrieve information
            return providerJSON.getJSONArray("episodes");
        } catch (final JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    /**
     * Calculates the difference between the timestamp of the provider & the current system time
     * Used to calculate the age of the episode referrals
     *
     * @param provider provider to get the timestamp of
     * @return Difference between the current time and the timestamp
     */
    public long getTimestampDifference(final Provider provider) {
        return this.getProviderJSON(provider)
                .map(jsonObject -> System.currentTimeMillis() - jsonObject.optLong("time"))
                .orElse(System.currentTimeMillis());
    }

    public void addEpisodesForProvider(final JSONArray episodes, final Provider provider) {
        this.getProviderJSON(provider).ifPresent(providerJSON -> {
            try {
                providerJSON.put("episodes", episodes); //Update
                providerJSON.put("time", System.currentTimeMillis());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.updateProviderJSON(provider, providerJSON);
        });
    }

    /**
     * Gets the providers JSON
     *
     * @param provider Provider to get the JSONObject of
     * @return the provider's json object, wrapped in an optional. Returns Optional.empty() if no key was found
     */
    public Optional<JSONObject> getProviderJSON(final Provider provider) {
        return Optional.ofNullable(providers.optJSONObject(provider.getName()));
    }

    /**
     * Updates the provider's JSON
     *
     * @param provider     provider to update the JSON of
     * @param providerJSON provider Json to write
     */
    public void updateProviderJSON(final Provider provider, final JSONObject providerJSON) {
        try {
            this.providers.put(provider.getName(), providerJSON);
            this.updateShow();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates this show at it's given index
     */
    public void updateShow() {
        final int index = AnimeAppMain.getInstance().getShowSaver().getIndex(this);
        if (index == -1) {
            throw new IndexOutOfBoundsException("Index out of range");
        } else
            AnimeAppMain.getInstance().getShowSaver().refreshShow(this, index);
    }

    /**
     * @return JSONObject with all needed information
     */
    @Override
    public String toString() {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put(StringHandler.SHOW_ID, getID())
                    .put(StringHandler.SHOW_TITLE, getShowTitle())
                    .put(StringHandler.SHOW_SCORE, getShowScore())
                    .put(StringHandler.SHOW_IMAGE_URL, getImageURL())
                    .put(StringHandler.SHOW_EPISODE_COUNT, getEpisodeCount())
                    .put("provider_info", providers);
            return jsonObject.toString();
        } catch (final JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public double getShowScore() {
        return showScore;
    }

    public String getID() {
        return malID;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public String getImageURL() {
        return imageURL;
    }

    public JSONObject getProviders() {
        return providers;
    }
}
