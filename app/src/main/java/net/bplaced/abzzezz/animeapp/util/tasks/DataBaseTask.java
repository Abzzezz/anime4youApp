/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:13
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import ga.abzzezz.util.stringing.StringUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class DataBaseTask implements Callable<String[]> {

    private final String aid;
    private final DataBaseSearch dataBaseSearch;
    private final String[] search;

    public DataBaseTask(final String aid, final DataBaseSearch dataBaseSearch, String... search) {
        this.aid = aid;
        this.dataBaseSearch = dataBaseSearch;
        this.search = search;
    }


    @Override
    public String[] call() {
        String realSeries = dataBaseSearch.getSubstringFromDB(aid);
        String[] re = new String[search.length];
        for (int i = 0; i < re.length; i++) {
            String in = realSeries.isEmpty() ? "-1" : StringUtil.getStringFromLong(realSeries, search[i], "\"");
            re[i] = in;
        }
        return re;

/*
        String coverURL = realSeries.isEmpty() ? "0" : StringUtil.getStringFromLong(realSeries, "src=\\\"", "\\\"");
        String episodesString = realSeries.isEmpty() ? "0" : StringUtil.getStringFromLong(realSeries, "\"Letzte\":\"", "\"");
        String seriesName = realSeries.isEmpty() ? "ERROR" : StringUtil.getStringFromLong(realSeries, "\"titel\":\"", "\"");
        String language = realSeries.isEmpty() ? "ERROR" : StringUtil.getStringFromLong(realSeries, "\"Untertitel\":\"", "\"");

 */
    }
}
