package com.accenture.videomanager.service;

import com.accenture.videomanager.domain.Picture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Picture.
 */
public interface PictureService {

    /**
     * Save a picture.
     *
     * @param picture the entity to save
     * @return the persisted entity
     */
    Picture save(Picture picture);

    /**
     *  Get all the pictures.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Picture> findAll(Pageable pageable);

    /**
     *  Get the "id" picture.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Picture findOne(String id);

    /**
     *  Delete the "id" picture.
     *
     *  @param id the id of the entity
     */
    void delete(String id);

    Picture findTmdbPicture(String id, Integer size);
}
