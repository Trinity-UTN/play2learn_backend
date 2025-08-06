package trinity.play2learn.backend.profile.avatar.models;

import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.profile.profile.models.Profile;

public enum TypeAspect {
    CUERPO {
        @Override
        public void assign(Profile profile, Aspect aspect) {
            if (aspect.equals(profile.getSelectedBody())) {
                throw new ConflictException("El aspecto ya está seleccionado");
            }
            profile.setSelectedBody(aspect);
        }
    },
    REMERA {
        @Override
        public void assign(Profile profile, Aspect aspect) {
            if (aspect.equals(profile.getSelectedShirt())) {
                throw new ConflictException("El aspecto ya está seleccionado");
            }
            profile.setSelectedShirt(aspect);
        }
    },
    SOMBRERO {
        @Override
        public void assign(Profile profile, Aspect aspect) {
            if (aspect.equals(profile.getSelectedHat())) {
                throw new ConflictException("El aspecto ya está seleccionado");
            }
            profile.setSelectedHat(aspect);
        }
    };

    public abstract void assign(Profile profile, Aspect aspect);
}

