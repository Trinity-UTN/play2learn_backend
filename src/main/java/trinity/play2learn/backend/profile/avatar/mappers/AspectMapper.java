package trinity.play2learn.backend.profile.avatar.mappers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import trinity.play2learn.backend.profile.avatar.dtos.request.AspectRegisterRequestDto;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;

public class AspectMapper {
    
    public static AspectRegisterRequestDto toRequestDto (
        String name,
        MultipartFile image,
        BigDecimal price,
        String type
    ) {
        return AspectRegisterRequestDto.builder()
            .name(name)
            .image(image)
            .price(price)
            .type(TypeAspect.valueOf(type))
            .build();
    }

    public static Aspect toModel (AspectRegisterRequestDto aspectDto, String image) {
        return Aspect.builder()
            .name(aspectDto.getName())
            .image(image)
            .price(aspectDto.getPrice())
            .type(aspectDto.getType())
            .build();
    }

    public static AspectResponseDto toDto (Aspect aspect) {
        return AspectResponseDto.builder()
            .id(aspect.getId())
            .name(aspect.getName())
            .image(aspect.getImage())
            .price(aspect.getPrice())
            .type(aspect.getType())
            .available(aspect.isAvailable())
            .build();
    }


    public static List<AspectResponseDto> toDtoList (List<Aspect> aspects) {
        return aspects
            .stream()
            .map(AspectMapper::toDto)
            .toList();
    }

    public static AspectResponseDto toMarketDto (Aspect aspect, boolean bought) {
        return AspectResponseDto.builder()
            .id(aspect.getId())
            .name(aspect.getName())
            .image(aspect.getImage())
            .price(aspect.getPrice())
            .type(aspect.getType())
            .available(aspect.isAvailable())
            .bought(bought)
            .build();
    }
}
