package com.accenture.videomanager.web.rest;

import com.accenture.videomanager.VmpictureApp;

import com.accenture.videomanager.domain.Picture;
import com.accenture.videomanager.repository.PictureRepository;
import com.accenture.videomanager.service.PictureService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PictureResource REST controller.
 *
 * @see PictureResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VmpictureApp.class)
public class PictureResourceIntTest {

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_TMBD_ID = "AAAAAAAAAA";
    private static final String UPDATED_TMBD_ID = "BBBBBBBBBB";

    private static final Integer DEFAULT_SIZE = 1;
    private static final Integer UPDATED_SIZE = 2;

    @Inject
    private PictureRepository pictureRepository;

    @Inject
    private PictureService pictureService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPictureMockMvc;

    private Picture picture;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PictureResource pictureResource = new PictureResource();
        ReflectionTestUtils.setField(pictureResource, "pictureService", pictureService);
        this.restPictureMockMvc = MockMvcBuilders.standaloneSetup(pictureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Picture createEntity() {
        Picture picture = new Picture()
                .image(DEFAULT_IMAGE)
                .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
                .tmbdId(DEFAULT_TMBD_ID)
                .size(DEFAULT_SIZE);
        return picture;
    }

    @Before
    public void initTest() {
        pictureRepository.deleteAll();
        picture = createEntity();
    }

    @Test
    public void createPicture() throws Exception {
        int databaseSizeBeforeCreate = pictureRepository.findAll().size();

        // Create the Picture

        restPictureMockMvc.perform(post("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(picture)))
            .andExpect(status().isCreated());

        // Validate the Picture in the database
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeCreate + 1);
        Picture testPicture = pictureList.get(pictureList.size() - 1);
        assertThat(testPicture.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testPicture.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testPicture.getTmbdId()).isEqualTo(DEFAULT_TMBD_ID);
        assertThat(testPicture.getSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    public void createPictureWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = pictureRepository.findAll().size();

        // Create the Picture with an existing ID
        Picture existingPicture = new Picture();
        existingPicture.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restPictureMockMvc.perform(post("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingPicture)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkImageIsRequired() throws Exception {
        int databaseSizeBeforeTest = pictureRepository.findAll().size();
        // set the field null
        picture.setImage(null);

        // Create the Picture, which fails.

        restPictureMockMvc.perform(post("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(picture)))
            .andExpect(status().isBadRequest());

        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkTmbdIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = pictureRepository.findAll().size();
        // set the field null
        picture.setTmbdId(null);

        // Create the Picture, which fails.

        restPictureMockMvc.perform(post("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(picture)))
            .andExpect(status().isBadRequest());

        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllPictures() throws Exception {
        // Initialize the database
        pictureRepository.save(picture);

        // Get all the pictureList
        restPictureMockMvc.perform(get("/api/pictures?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(picture.getId())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].tmbdId").value(hasItem(DEFAULT_TMBD_ID.toString())))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE)));
    }

    @Test
    public void getPicture() throws Exception {
        // Initialize the database
        pictureRepository.save(picture);

        // Get the picture
        restPictureMockMvc.perform(get("/api/pictures/{id}", picture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(picture.getId()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.tmbdId").value(DEFAULT_TMBD_ID.toString()))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE));
    }

    @Test
    public void getNonExistingPicture() throws Exception {
        // Get the picture
        restPictureMockMvc.perform(get("/api/pictures/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updatePicture() throws Exception {
        // Initialize the database
        pictureService.save(picture);

        int databaseSizeBeforeUpdate = pictureRepository.findAll().size();

        // Update the picture
        Picture updatedPicture = pictureRepository.findOne(picture.getId());
        updatedPicture
                .image(UPDATED_IMAGE)
                .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
                .tmbdId(UPDATED_TMBD_ID)
                .size(UPDATED_SIZE);

        restPictureMockMvc.perform(put("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPicture)))
            .andExpect(status().isOk());

        // Validate the Picture in the database
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeUpdate);
        Picture testPicture = pictureList.get(pictureList.size() - 1);
        assertThat(testPicture.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPicture.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPicture.getTmbdId()).isEqualTo(UPDATED_TMBD_ID);
        assertThat(testPicture.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    public void updateNonExistingPicture() throws Exception {
        int databaseSizeBeforeUpdate = pictureRepository.findAll().size();

        // Create the Picture

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPictureMockMvc.perform(put("/api/pictures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(picture)))
            .andExpect(status().isCreated());

        // Validate the Picture in the database
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deletePicture() throws Exception {
        // Initialize the database
        pictureService.save(picture);

        int databaseSizeBeforeDelete = pictureRepository.findAll().size();

        // Get the picture
        restPictureMockMvc.perform(delete("/api/pictures/{id}", picture.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Picture> pictureList = pictureRepository.findAll();
        assertThat(pictureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
