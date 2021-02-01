/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 17:41
 */

package net.bplaced.abzzezz.animeapp.util.provider.impl;

import java.util.Random;

public interface TwistmoeHolder {

    String BASE_URL = "https://twist.moe/";

    String API_URL = "https://twist.moe/api/";

    String SHOW_API = "https://twist.moe/api/anime/";

    String KEY = "LXgIVP&PorO68Rq7dTx8N^lP!Fa5sGJ^*XK";

    String STREAM_URL = "https://twistcdn.bunny.sh";

    String SHOW_BASE_URL = "https://twist.moe/a/";


    default String getRequestToken() {
        final String[] tokens = {"0df14814b9e590a1f26d3071a4ed7974"};
        return tokens[new Random().nextInt(tokens.length)];
    }
}
