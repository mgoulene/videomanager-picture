package com.accenture.videomanager.repository;

import com.accenture.videomanager.domain.Picture;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Picture entity.
 */
@SuppressWarnings("unused")
public interface PictureRepository extends MongoRepository<Picture,String> {

    public Picture findOneByTmbdIdAndSize(String tmdbId, Integer size);
}
