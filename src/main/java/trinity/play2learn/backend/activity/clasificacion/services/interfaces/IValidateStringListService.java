package trinity.play2learn.backend.activity.clasificacion.services.interfaces;

import java.util.List;

public interface IValidateStringListService {
    void validateDuplicateStringsInList(List<String> listStrings, String message);
}
