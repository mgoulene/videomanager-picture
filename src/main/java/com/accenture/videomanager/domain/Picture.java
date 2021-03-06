package com.accenture.videomanager.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Picture.
 */

@Document(collection = "picture")
public class Picture implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("image")
    private byte[] image;

    @Field("image_content_type")
    private String imageContentType;

    @Field("size")
    private Integer size;

    @NotNull
    @Size(max = 100)
    @Field("tmdb_id")
    private String tmdbId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public Picture image(byte[] image) {
        this.image = image;
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public Picture imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Integer getSize() {
        return size;
    }

    public Picture size(Integer size) {
        this.size = size;
        return this;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getTmdbId() {
        return tmdbId;
    }

    public Picture tmdbId(String tmdbId) {
        this.tmdbId = tmdbId;
        return this;
    }

    public void setTmdbId(String tmdbId) {
        this.tmdbId = tmdbId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Picture picture = (Picture) o;
        if (picture.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, picture.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Picture{" +
            "id=" + id +
            ", image='" + image + "'" +
            ", imageContentType='" + imageContentType + "'" +
            ", size='" + size + "'" +
            ", tmdbId='" + tmdbId + "'" +
            '}';
    }
}
