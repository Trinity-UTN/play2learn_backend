package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.IActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;

@ExtendWith(MockitoExtension.class)
class ActivityGetServiceTest {

    @Mock
    private IActivityGetByIdService activityGetByIdService;

    @Mock
    private IActivityMapper ahorcadoMapper;

    private ActivityGetService activityGetService;

    @BeforeEach
    void setUp() {
        Map<String, IActivityMapper> mappers = new HashMap<>();
        mappers.put("ahorcadoMapper", ahorcadoMapper);
        activityGetService = new ActivityGetService(mappers, activityGetByIdService);
    }

    @Nested
    @DisplayName("cu64GetActivity")
    class GetActivity {

        @Test
        @DisplayName("Given existing Ahorcado activity When retrieving by id Then returns mapped response")
        void whenActivityExists_returnsDto() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            AhorcadoResponseDto expectedDto = AhorcadoResponseDto.builder()
                .id(ActivityTestMother.ACTIVITY_ID)
                .name("Ahorcado")
                .build();

            when(activityGetByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);
            when(ahorcadoMapper.toActivityDto(activity)).thenReturn(expectedDto);

            ActivityResponseDto response = activityGetService.cu64GetActivity(ActivityTestMother.ACTIVITY_ID);

            verify(activityGetByIdService).findActivityById(ActivityTestMother.ACTIVITY_ID);
            verify(ahorcadoMapper).toActivityDto(activity);
            assertThat(response)
                .isNotNull()
                .extracting(ActivityResponseDto::getId, ActivityResponseDto::getName)
                .containsExactly(ActivityTestMother.ACTIVITY_ID, "Ahorcado");
        }

        @Test
        @DisplayName("Given activity type without mapper When retrieving by id Then throws IllegalArgumentException")
        void whenMapperNotFound_throwsIllegalArgument() {
            Activity activity = ActivityTestMother.ahorcadoActivity(ActivityTestMother.ACTIVITY_ID);
            
            Map<String, IActivityMapper> emptyMappers = new HashMap<>();
            ActivityGetService serviceWithoutMapper = new ActivityGetService(emptyMappers, activityGetByIdService);

            when(activityGetByIdService.findActivityById(ActivityTestMother.ACTIVITY_ID)).thenReturn(activity);

            assertThatThrownBy(() -> serviceWithoutMapper.cu64GetActivity(ActivityTestMother.ACTIVITY_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No mapper found for activity type: Ahorcado");
        }

    }
}

