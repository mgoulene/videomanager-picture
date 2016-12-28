package com.accenture.videomanager.service.utils;

import info.movito.themoviedbapi.TmdbApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by vagrant on 12/11/16.
 */
public class TmdbDataLoader {

    private static String TMDB_KEY = "c344516cac0ae134d50ea9ed99e6a42c";
    private static TmdbDataLoader _the;
    private final Logger log = LoggerFactory.getLogger(TmdbDataLoader.class);
    private TmdbApi api;

    private TmdbDataLoader() {
        api = new TmdbApi(TMDB_KEY);
    }

    public static synchronized TmdbDataLoader the() {
        if (_the == null) {
            _the = new TmdbDataLoader();
        }
        return _the;
    }

    public byte[] getImageData(String path) {
        return GetPictureFromURL.getBytes(api, path);
    }

    public byte[] getResizeImageDate(byte[] input, int size) {
        ByteArrayInputStream in = new ByteArrayInputStream(input);
        try {
            BufferedImage img = ImageIO.read(in);
            int height = size;
            int width = 0;
            if (height == 0) {
                height = (width * img.getHeight()) / img.getWidth();
            }
            if (width == 0) {
                width = (height * img.getWidth()) / img.getHeight();
            }
            Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();


            ImageIO.write(imageBuff, "jpg", buffer);
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

}
