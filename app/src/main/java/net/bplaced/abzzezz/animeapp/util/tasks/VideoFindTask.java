/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 09.07.20, 22:54
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.ScriptUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class VideoFindTask extends TaskExecutor implements Callable<String> {

    private final int aid;
    private final int episode;

    public VideoFindTask(int aid, int episode) {
        this.aid = aid;
        this.episode = (episode + 1);
    }

    public <R> void executeAsync(Callback<String> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public String call() throws Exception {
        return new BufferedReader(new InputStreamReader(
                URLUtil.createHTTPURLConnection(StringHandler.REQUEST_URL, "POST",
                        new String[]{"User-Agent", episode + StringUtil.splitter + aid + StringUtil.splitter.concat(ScriptUtil.generateRandomKey())},
                        new String[]{"Referer", AnimeAppMain.getInstance().getAndroidId()}).getInputStream())).lines().collect(Collectors.joining());
    }
}
