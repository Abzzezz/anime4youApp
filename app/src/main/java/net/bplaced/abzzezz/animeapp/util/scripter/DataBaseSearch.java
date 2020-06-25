/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:05
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;

import java.net.MalformedURLException;
import java.net.URL;


public class DataBaseSearch {


    /**
     * @param aid
     * @return
     */

    public String getSubstringFromDB(String aid) {
        String realSeries = "";
        try {
            String line = URLUtil.getURLContentAsString(new URL(URLHandler.dataBase));
            realSeries = StringUtil.getStringFromLong(line, "\"aid\"" + ":" + "\"" + aid + "\"", "}");
        } catch (StringIndexOutOfBoundsException | MalformedURLException e) {
            Logger.log("Checking Database: " + e.getMessage(), Logger.LogType.ERROR);
            URLHandler.dataBase = "http://abzzezz.bplaced.net/list.txt";
        }
        return realSeries;
    }
}
