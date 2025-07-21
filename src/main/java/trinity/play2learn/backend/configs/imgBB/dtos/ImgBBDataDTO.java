package trinity.play2learn.backend.configs.imgBB.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ImgBBDataDTO {

    private String id;
    private String title;

    @JsonProperty("url_viewer")
    private String urlViewer;

    private String url;

    @JsonProperty("display_url")
    private String displayUrl;

    private int width;
    private int height;
    private int size;
    private long time;
    private int expiration;

    private ImgBBImage image;
    private ImgBBImage thumb;
    private ImgBBImage medium;

    @JsonProperty("delete_url")
    private String deleteUrl;

    @Data
    public static class ImgBBImage {
        private String filename;
        private String name;
        private String mime;
        private String extension;
        private String url;
    }
}