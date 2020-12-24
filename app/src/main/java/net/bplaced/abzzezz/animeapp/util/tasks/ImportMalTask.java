/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.11.20, 21:37
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class ImportMalTask extends TaskExecutor implements Callable<String> {

    private final String url;
    private String dataBase;

    public ImportMalTask(final String url) {
        this.url = url;
    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }
    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    @Override
    public String call() throws Exception {
        dataBase = URLUtil.getURLContentAsString(new URL(StringHandler.DATABASE));
        for (final String[] strings : getSimilar()) {
            AnimeAppMain.getInstance().getShowSaver().addShow(getPrefDub(strings));
        }
        return null;
    }

    public <R> void executeAsync(final Callback<String> callback) {
        super.executeAsync(this, callback);
    }

    private JSONObject getPrefDub(final String[] show) {
        final String showName = show[0];
        final Collection<JSONObject> shows = new ArrayList<>();
        int showIndex = dataBase.indexOf("\"titel\":\"" + showName + "\"");

        while (showIndex != -1) {
            try {
                final JSONObject listObject = new JSONObject(dataBase.substring(dataBase.lastIndexOf("{", showIndex), dataBase.indexOf("}", showIndex) + 1));
                final JSONObject converted = new JSONObject();

                converted.put("id", listObject.getString("aid"));
                converted.put("image_url", StringHandler.COVER_DATABASE.concat(listObject.getString("image_id")));
                converted.put("episodes", listObject.getString("Letzte"));
                converted.put("title", listObject.getString("titel"));
                converted.put("language", listObject.getString("Untertitel"));
                converted.put("year", listObject.getString("Jahr"));
                shows.add(converted);
            } catch (JSONException e) {
                e.printStackTrace();
                break;
            }
            showIndex = dataBase.indexOf("\"titel\":\"" + showName + "\"", showIndex + 1);
        }
        final JSONObject gerSub = shows.stream().findAny().get();

        return shows.stream().filter(jsonObject -> {
            try {
                return jsonObject.getString("language").equals("gerdub");
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }).findAny().orElse(gerSub);
    }

    private Collection<String[]> getSimilar() throws MalformedURLException {
        final List<String> allAvailable = getAllTitles();
        final List<String> animeMAL = getMalTitles();

        return allAvailable.parallelStream().map(s -> s.split(StringUtil.splitter)).filter(strings -> {
            final Optional<String[]> contains = animeMAL.stream().map(s -> s.split(StringUtil.splitter)).filter(s -> similarity(strings[0], s[0]) > 0.9F).findFirst();
            return contains.isPresent() && contains.get()[1].equals(strings[1]);
        }).collect(Collectors.toList());
    }

    private List<String> getAllTitles() {
        final List<String> titleList = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder(dataBase);
        final String title = "\"titel\":\"";
        final String episode = "\"Letzte\":\"";
        while (stringBuilder.indexOf(title) != -1) {
            final int start = stringBuilder.indexOf(title);
            final int end = stringBuilder.indexOf("\"", start + title.length());
            final int smallStart = stringBuilder.lastIndexOf("{", start);
            final int smallEnd = stringBuilder.indexOf("}", start);
            final String smallString = stringBuilder.substring(smallStart, smallEnd);
            final String name = stringBuilder.substring(start + title.length(), end);
            final String episodes = StringUtil.getStringFromLong(smallString, episode, "\"");
            final String s = name + StringUtil.splitter + episodes;
            if (!titleList.contains(s)) titleList.add(s);
            stringBuilder.delete(smallStart, smallEnd);
        }
        Logger.log("Done getting Anime4you.", Logger.LogType.INFO);
        return titleList;
    }

    private List<String> getMalTitles() throws MalformedURLException {
        final List<String> titles = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder();

        for (final String line : URLUtil.getURLContentAsArray(new URL(url))) {
            if (line.contains("<table class=\"list-table\" data-items=\"")) {
                stringBuilder.append(line, line.indexOf("<table class=\"list-table\" data-items=\""), line.indexOf(">"));
                break;
            }
        }

        final String title = "anime_title&quot;:&quot;";
        final String episode = "anime_num_episodes&quot;:";

        while (stringBuilder.indexOf(title) != -1) {
            final int start = stringBuilder.indexOf(title) + title.length();
            final int end = stringBuilder.indexOf("&", start);
            final String smallString = stringBuilder.substring(start, stringBuilder.indexOf("}", start));
            final int episodeStart = smallString.indexOf(episode) + episode.length();

            final String name = stringBuilder.substring(start, end);
            final String episodes = smallString.substring(episodeStart, smallString.indexOf(",", episodeStart));
            titles.add(name + StringUtil.splitter + episodes);

            stringBuilder.delete(start - title.length(), start + smallString.length());
        }

        Logger.log("Got all titles from MAL", Logger.LogType.INFO);
        return titles;
    }

}
