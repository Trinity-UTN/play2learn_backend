package trinity.play2learn.backend.profile.avatar.models;

import trinity.play2learn.backend.profile.profile.models.Profile;

public enum TypeAspect {
    CUERPO {
        @Override
        public void assign(Profile profile, Aspect aspect) {
            profile.setSelectedBody(aspect);
        }
    },
    REMERA {
        @Override
        public void assign(Profile profile, Aspect aspect) {
            profile.setSelectedShirt(aspect);
        }
    },
    SOMBRERO {
        @Override
        public void assign(Profile profile, Aspect aspect) {
            profile.setSelectedHat(aspect);
        }
    };

    public abstract void assign(Profile profile, Aspect aspect);
}

