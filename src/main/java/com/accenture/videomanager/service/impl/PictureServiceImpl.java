package com.accenture.videomanager.service.impl;

import com.accenture.videomanager.service.PictureService;
import com.accenture.videomanager.domain.Picture;
import com.accenture.videomanager.repository.PictureRepository;
import com.accenture.videomanager.service.utils.TmdbDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Picture.
 */
@Service
public class PictureServiceImpl implements PictureService{

    private final Logger log = LoggerFactory.getLogger(PictureServiceImpl.class);

    @Inject
    private PictureRepository pictureRepository;

    /**
     * Save a picture.
     *
     * @param picture the entity to save
     * @return the persisted entity
     */
    public Picture save(Picture picture) {
        log.debug("Request to save Picture : {}", picture);
        Picture result = pictureRepository.save(picture);
        return result;
    }

    /**
     *  Get all the pictures.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    public Page<Picture> findAll(Pageable pageable) {
        log.debug("Request to get all Pictures");
        Page<Picture> result = pictureRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one picture by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    public Picture findOne(String id) {
        log.debug("Request to get Picture : {}", id);
        Picture picture = pictureRepository.findOne(id);
        return picture;
    }

    /**
     *  Delete the  picture by id.
     *
     *  @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Picture : {}", id);
        pictureRepository.delete(id);
    }

    @Override
    public Picture findTmdbPicture(String id) {
        log.debug("Request to get Picture : {}", id);
        Picture picture = pictureRepository.findOneByTmdbIdAndSize(id, null);
        if (picture == null) {
            byte[] bytes = TmdbDataLoader.the().getImageData("/"+id+".jpg");
            picture = new Picture();
            picture.image(bytes)
                .imageContentType(MimeTypeUtils.IMAGE_JPEG.getType())
                .size(null)
                .tmdbId(id);
            picture = pictureRepository.save(picture);
        }
        //picture.setId("1");
        return picture;
    }
}
