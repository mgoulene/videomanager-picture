package com.accenture.videomanager.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.accenture.videomanager.domain.Picture;
import com.accenture.videomanager.service.PictureService;
import com.accenture.videomanager.web.rest.util.HeaderUtil;
import com.accenture.videomanager.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Picture.
 */
@RestController
@RequestMapping("/api")
public class PictureResource {

    private final Logger log = LoggerFactory.getLogger(PictureResource.class);

    @Inject
    private PictureService pictureService;

    /**
     * POST  /pictures : Create a new picture.
     *
     * @param picture the picture to create
     * @return the ResponseEntity with status 201 (Created) and with body the new picture, or with status 400 (Bad Request) if the picture has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pictures")
    @Timed
    public ResponseEntity<Picture> createPicture(@Valid @RequestBody Picture picture) throws URISyntaxException {
        log.debug("REST request to save Picture : {}", picture);
        if (picture.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("picture", "idexists", "A new picture cannot already have an ID")).body(null);
        }
        Picture result = pictureService.save(picture);
        return ResponseEntity.created(new URI("/api/pictures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("picture", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pictures : Updates an existing picture.
     *
     * @param picture the picture to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated picture,
     * or with status 400 (Bad Request) if the picture is not valid,
     * or with status 500 (Internal Server Error) if the picture couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pictures")
    @Timed
    public ResponseEntity<Picture> updatePicture(@Valid @RequestBody Picture picture) throws URISyntaxException {
        log.debug("REST request to update Picture : {}", picture);
        if (picture.getId() == null) {
            return createPicture(picture);
        }
        Picture result = pictureService.save(picture);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("picture", picture.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pictures : get all the pictures.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pictures in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/pictures")
    @Timed
    public ResponseEntity<List<Picture>> getAllPictures(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Pictures");
        Page<Picture> page = pictureService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pictures");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pictures/:id : get the "id" picture.
     *
     * @param id the id of the picture to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the picture, or with status 404 (Not Found)
     */
    @GetMapping("/pictures/{id}")
    @Timed
    public ResponseEntity<Picture> getPicture(@PathVariable String id) {
        log.debug("REST request to get Picture : {}", id);
        Picture picture = pictureService.findOne(id);
        return Optional.ofNullable(picture)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /pictures/:id : get the "id" picture.
     *
     * @param id the id of the picture to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the picture, or with status 404 (Not Found)
     */
    @GetMapping("/tmdb-pictures/{id}")
    @Timed
    public ResponseEntity<Picture> getTmdbPicture(@PathVariable String id, @RequestParam(value = "size") Integer size) {
        log.debug("REST request to get getTmdbPicture : {}", id, size);
        Picture picture = pictureService.findTmdbPicture(id, size);
        return Optional.ofNullable(picture)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * DELETE  /pictures/:id : delete the "id" picture.
     *
     * @param id the id of the picture to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pictures/{id}")
    @Timed
    public ResponseEntity<Void> deletePicture(@PathVariable String id) {
        log.debug("REST request to delete Picture : {}", id);
        pictureService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("picture", id.toString())).build();
    }

}
