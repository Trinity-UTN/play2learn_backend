package trinity.play2learn.backend.configs.imgBB.dtos;

import lombok.Data;

@Data
public class ImgBBResponseDTO {
    private ImgBBDataDTO data;
    private boolean success;
    private int status;
}
