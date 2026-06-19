package trinity.play2learn.backend.configs.seed.services;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;
import trinity.play2learn.backend.profile.profile.models.Profile;

/**
 * Asigna a cada estudiante un kit inicial de aspectos: un CUERPO, una REMERA y un SOMBRERO
 * elegidos al azar del catálogo, equipados en el perfil.
 */
@Service
public class StudentAspectInventorySeedService {

    /**
     * Agrupa el catálogo por {@link TypeAspect}.
     */
    public Map<TypeAspect, List<Aspect>> groupByType(List<Aspect> catalog) {
        return catalog.stream().collect(Collectors.groupingBy(
            Aspect::getType,
            () -> new EnumMap<>(TypeAspect.class),
            Collectors.toList()
        ));
    }

    /**
     * Elige exactamente un aspecto por cada {@link TypeAspect} usando el {@link Random} provisto.
     *
     * @throws IllegalStateException si falta algún tipo en el catálogo
     */
    public List<Aspect> pickRandomStarterKit(Map<TypeAspect, List<Aspect>> grouped, Random random) {
        List<Aspect> starterKit = new ArrayList<>(TypeAspect.values().length);

        for (TypeAspect type : TypeAspect.values()) {
            List<Aspect> pool = grouped.get(type);
            if (pool == null || pool.isEmpty()) {
                throw new IllegalStateException(
                    "Catálogo de aspectos incompleto: no hay aspectos de tipo " + type);
            }
            starterKit.add(pool.get(random.nextInt(pool.size())));
        }

        return starterKit;
    }

    /**
     * Agrega el kit al inventario del perfil y equipa cada pieza (selectedBody/Shirt/Hat).
     */
    public void applyStarterKit(Profile profile, List<Aspect> starterKit) {
        for (Aspect aspect : starterKit) {
            if (!profile.getOwnedAspects().contains(aspect)) {
                profile.getOwnedAspects().add(aspect);
            }
            aspect.getType().assign(profile, aspect);
        }
    }
}
