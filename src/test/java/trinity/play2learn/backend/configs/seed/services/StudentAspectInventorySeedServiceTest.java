package trinity.play2learn.backend.configs.seed.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;
import trinity.play2learn.backend.profile.profile.models.Profile;

class StudentAspectInventorySeedServiceTest {

    private StudentAspectInventorySeedService service;

    private Aspect bodyA;
    private Aspect bodyB;
    private Aspect shirtA;
    private Aspect shirtB;
    private Aspect hatA;
    private Aspect hatB;

    @BeforeEach
    void setUp() {
        service = new StudentAspectInventorySeedService();

        bodyA = aspect(1L, "Cuerpo A", TypeAspect.CUERPO);
        bodyB = aspect(2L, "Cuerpo B", TypeAspect.CUERPO);
        shirtA = aspect(3L, "Remera A", TypeAspect.REMERA);
        shirtB = aspect(4L, "Remera B", TypeAspect.REMERA);
        hatA = aspect(5L, "Sombrero A", TypeAspect.SOMBRERO);
        hatB = aspect(6L, "Sombrero B", TypeAspect.SOMBRERO);
    }

    @Test
    @DisplayName("groupByType agrupa el catálogo por TypeAspect")
    void groupByType_groupsCatalogByType() {
        Map<TypeAspect, List<Aspect>> grouped = service.groupByType(
            List.of(bodyA, bodyB, shirtA, shirtB, hatA, hatB));

        assertThat(grouped.get(TypeAspect.CUERPO)).containsExactly(bodyA, bodyB);
        assertThat(grouped.get(TypeAspect.REMERA)).containsExactly(shirtA, shirtB);
        assertThat(grouped.get(TypeAspect.SOMBRERO)).containsExactly(hatA, hatB);
    }

    @Test
    @DisplayName("pickRandomStarterKit devuelve exactamente un aspecto por tipo")
    void pickRandomStarterKit_returnsOnePerType() {
        Map<TypeAspect, List<Aspect>> grouped = service.groupByType(
            List.of(bodyA, bodyB, shirtA, shirtB, hatA, hatB));

        List<Aspect> kit = service.pickRandomStarterKit(grouped, new Random(42));

        assertThat(kit).hasSize(3);
        assertThat(kit).extracting(Aspect::getType)
            .containsExactlyInAnyOrder(TypeAspect.CUERPO, TypeAspect.REMERA, TypeAspect.SOMBRERO);
    }

    @Test
    @DisplayName("pickRandomStarterKit con seed fijo es determinista")
    void pickRandomStarterKit_withFixedSeed_isDeterministic() {
        Map<TypeAspect, List<Aspect>> grouped = service.groupByType(
            List.of(bodyA, bodyB, shirtA, shirtB, hatA, hatB));

        List<Aspect> first = service.pickRandomStarterKit(grouped, new Random(42));
        List<Aspect> second = service.pickRandomStarterKit(grouped, new Random(42));

        assertThat(first).extracting(Aspect::getId).isEqualTo(second.stream().map(Aspect::getId).toList());
    }

    @Test
    @DisplayName("pickRandomStarterKit falla si falta un tipo en el catálogo")
    void pickRandomStarterKit_failsWhenTypeMissing() {
        Map<TypeAspect, List<Aspect>> grouped = service.groupByType(List.of(bodyA, shirtA, hatA));
        grouped.remove(TypeAspect.SOMBRERO);

        assertThatThrownBy(() -> service.pickRandomStarterKit(grouped, new Random(42)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("SOMBRERO");
    }

    @Test
    @DisplayName("applyStarterKit agrega inventario y equipa el perfil")
    void applyStarterKit_addsInventoryAndEquipsProfile() {
        Profile profile = Profile.builder()
            .id(1L)
            .ownedAspects(new java.util.ArrayList<>())
            .build();

        List<Aspect> kit = List.of(bodyA, shirtA, hatA);
        service.applyStarterKit(profile, kit);

        assertThat(profile.getOwnedAspects()).containsExactly(bodyA, shirtA, hatA);
        assertThat(profile.getSelectedBody()).isEqualTo(bodyA);
        assertThat(profile.getSelectedShirt()).isEqualTo(shirtA);
        assertThat(profile.getSelectedHat()).isEqualTo(hatA);
    }

    @Test
    @DisplayName("Dos estudiantes con misma semilla pueden recibir kits distintos al avanzar el Random")
    void pickRandomStarterKit_differentStudentsCanDiffer() {
        Map<TypeAspect, List<Aspect>> grouped = service.groupByType(
            List.of(bodyA, bodyB, shirtA, shirtB, hatA, hatB));
        Random random = new Random(42);

        List<Aspect> studentOne = service.pickRandomStarterKit(grouped, random);
        List<Aspect> studentTwo = service.pickRandomStarterKit(grouped, random);

        assertThat(studentOne).hasSize(3);
        assertThat(studentTwo).hasSize(3);
    }

    private static Aspect aspect(Long id, String name, TypeAspect type) {
        return Aspect.builder()
            .id(id)
            .name(name)
            .image("https://example.com/" + name)
            .price(BigDecimal.TEN)
            .type(type)
            .build();
    }
}
