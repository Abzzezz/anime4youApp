/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 05.03.21, 13:52
 */

package net.bplaced.abzzezz.animeapp.util.provider;

import net.bplaced.abzzezz.animeapp.util.provider.providers.AniFlix;
import net.bplaced.abzzezz.animeapp.util.provider.providers.AnimePahe;
import net.bplaced.abzzezz.animeapp.util.provider.providers.GogoAnime;
import net.bplaced.abzzezz.animeapp.util.provider.providers.Twistmoe;

public enum Providers {

    GOGOANIME(new GogoAnime()),
    TWISTMOE(new Twistmoe()),
    ANIMEPAHE(new AnimePahe()),
    ANIFLIX(new AniFlix()),
    //Null provider, for old providers; Skipped when iterating
    NULL(null);

    private final Provider provider;

    Providers(Provider provider) {
        this.provider = provider;
    }

    public static Provider getProvider(final String enumValue) {
        return valueOf(enumValue).getProvider();
    }

    public Provider getProvider() {
        return provider;
    }
}
