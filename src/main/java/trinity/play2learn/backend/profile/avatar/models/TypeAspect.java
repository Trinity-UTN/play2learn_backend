package trinity.play2learn.backend.profile.avatar.models;

import trinity.play2learn.backend.profile.profile.models.Profile;

public enum TypeAspect {
    CUERPO {
        @Override
        public Profile assign(Profile profile, Aspect aspect) {
            profile.setSelectedBody(aspect);
            return profile;
        }

        @Override
        public Profile unassing(Profile profile) {
            profile.setSelectedBody(null);
            return profile;
        }
    },
    REMERA {
        @Override
        public Profile assign(Profile profile, Aspect aspect) {
            profile.setSelectedShirt(aspect);
            return profile;
        }

        @Override
        public Profile unassing(Profile profile) {
            profile.setSelectedShirt(null);
            return profile;
        }
    },
    SOMBRERO {
        @Override
        public Profile assign(Profile profile, Aspect aspect) {
            profile.setSelectedHat(aspect);
            return profile;
        }

        @Override
        public Profile unassing(Profile profile) {
            profile.setSelectedHat(null);
            return profile;
        }
    };

    public abstract Profile assign(Profile profile, Aspect aspect);

    public abstract Profile unassing (Profile profile);
}

