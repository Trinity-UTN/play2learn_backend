package trinity.play2learn.backend.activity.memorama;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.memorama.dtos.CouplesMemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.models.CouplesMemorama;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.admin.subject.models.Subject;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemoramaTestMother {

    public static final String DEFAULT_CONCEPT = "Concepto";
    public static final String MAX_LENGTH_CONCEPT = "a".repeat(50);
    public static final Double DEFAULT_INITIAL_BALANCE = 100.0;

    public static MemoramaRequestDto memoramaRequestDto(
        List<CouplesMemoramaRequestDto> couples
    ) {
        LocalDateTime now = LocalDateTime.now();
        MemoramaRequestDto dto = new MemoramaRequestDto();
        dto.setDescription("Descripción de la actividad de memorama");
        dto.setStartDate(now.plusDays(1));
        dto.setEndDate(now.plusDays(7));
        dto.setDifficulty(Difficulty.FACIL);
        dto.setMaxTime(30);
        dto.setAttempts(3);
        dto.setSubjectId(ActivityTestMother.SUBJECT_ID);
        dto.setInitialBalance(DEFAULT_INITIAL_BALANCE);
        dto.setCouples(couples);
        return dto;
    }

    public static MemoramaRequestDto validMemoramaRequestDto() {
        return memoramaRequestDto(
            List.of(
                couple("Perro"),
                couple("Gato"),
                couple("Pájaro"),
                couple("Peces")
            )
        );
    }

    public static CouplesMemoramaRequestDto couple(String concept) {
        CouplesMemoramaRequestDto couple = new CouplesMemoramaRequestDto();
        couple.setConcept(concept);
        couple.setImage(mockImage());
        return couple;
    }

    public static CouplesMemoramaRequestDto couple(String concept, MockMultipartFile image) {
        CouplesMemoramaRequestDto couple = new CouplesMemoramaRequestDto();
        couple.setConcept(concept);
        couple.setImage(image);
        return couple;
    }

    public static CouplesMemoramaRequestDto coupleWithoutImage(String concept) {
        CouplesMemoramaRequestDto couple = new CouplesMemoramaRequestDto();
        couple.setConcept(concept);
        couple.setImage(null);
        return couple;
    }

    public static MockMultipartFile mockImage() {
        return new MockMultipartFile(
            "image",
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );
    }

    public static MockMultipartFile mockImage(String name) {
        return new MockMultipartFile(
            "images",
            name,
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );
    }

    public static List<CouplesMemoramaRequestDto> couples(int count) {
        List<String> validConcepts = List.of("Perro", "Gato", "Pájaro", "Peces", "Conejo", "Tortuga", "Elefante", "León");
        List<CouplesMemoramaRequestDto> couples = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String concept = i < validConcepts.size() ? validConcepts.get(i) : "Concepto" + (char)('A' + i % 26);
            couples.add(couple(concept));
        }
        return couples;
    }

    public static MemoramaResponseDto memoramaResponseDto(
        Long id,
        List<?> couples
    ) {
        return MemoramaResponseDto.builder()
            .id(id)
            .name("Memorama")
            .description("Descripción de la actividad de memorama")
            .couples(couples != null ? (List) couples : List.of())
            .build();
    }

    public static MemoramaResponseDto validMemoramaResponseDto(Long id) {
        return memoramaResponseDto(id, List.of());
    }

    public static Memorama savedMemorama(Long id, Subject subject) {
        return Memorama.builder()
            .id(id)
            .name("Memorama")
            .description("Descripción de la actividad de memorama")
            .startDate(ActivityTestMother.START_DATE)
            .endDate(ActivityTestMother.END_DATE)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subject(subject)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .actualBalance(0.0)
            .couples(List.of())
            .build();
    }

    public static List<CouplesMemorama> savedCouples(Memorama memorama, int count) {
        List<CouplesMemorama> couples = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            couples.add(CouplesMemorama.builder()
                .id((long) (i + 1))
                .concept("Concepto " + (i + 1))
                .url("https://example.com/image" + (i + 1) + ".jpg")
                .memorama(memorama)
                .build());
        }
        return couples;
    }
}

