package com.accenture.videomanager.service.utils;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.core.ResponseStatusException;
import info.movito.themoviedbapi.model.people.PersonPeople;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by vagrant on 12/11/16.
 */
public class TmdbDataLoader {

    private final Logger log = LoggerFactory.getLogger(TmdbDataLoader.class);

    private static String TMDB_KEY = "c344516cac0ae134d50ea9ed99e6a42c";

    private static TmdbDataLoader _the;

    private TmdbApi api;

    public static synchronized TmdbDataLoader the() {
        if (_the == null) {
            _the = new TmdbDataLoader();
        }
        return _the;
    }

    private TmdbDataLoader() {
        api = new TmdbApi(TMDB_KEY);
    }


    public byte[] getImageData(String path) {
        return GetPictureFromURL.getBytes(api, path);
    }


}
