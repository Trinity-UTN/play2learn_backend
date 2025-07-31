package trinity.play2learn.backend.admin.year.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearDeleteService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccesfullyMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RequestMapping("/admin/years")
@RestController
@AllArgsConstructor
public class YearDeleteController {

    private final IYearDeleteService yearDeleteService;
    

    /**
     * Deletes a year by its ID.
     *
     * @param id the ID of the year to delete
     * @return a response entity indicating the result of the deletion
     */
    @DeleteMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable String id) {
        yearDeleteService.cu11deleteYear(id);
        return ResponseFactory.noContent(
                SuccesfullyMessages.deletedSuccessfully("Año")
        );
    }
}
