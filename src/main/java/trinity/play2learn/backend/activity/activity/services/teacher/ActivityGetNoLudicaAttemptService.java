package trinity.play2learn.backend.activity.activity.services.teacher;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.noLudica.NoLudicaAttemptResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.NoLudicaAttemptMapper;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetNoLudicaAttemptService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.fileUpload.dtos.FileDownloadData;
import trinity.play2learn.backend.configs.fileUpload.models.StoredFile;
import trinity.play2learn.backend.configs.fileUpload.services.interfaces.IDownloadFromUploadCareService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityGetNoLudicaAttemptService implements IActivityGetNoLudicaAttemptService {

    private final IActivityGetCompletedByIdService activityGetCompletedByIdService;
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IDownloadFromUploadCareService downloadFromUploadCareService;

    @Override
    public NoLudicaAttemptResponseDto cu127GetNoLudicaAttempt(Long activityId, User user) {

        ActivityCompleted activityCompleted = findAndValidateActivityCompleted(activityId, user);

        NoLudicaAttempt noLudicaAttempt = activityCompleted.getNoLudicaAttempt();
        String plainText = noLudicaAttempt.getPlainText();
        Boolean hasFile = false;
        String fileName = null;
        String downloadUrl = null;

        if (noLudicaAttempt.getFile() != null) {
            hasFile = true;
            fileName = noLudicaAttempt.getFile().getFileName();
            downloadUrl = "/activity/teacher/noLudica/" + activityCompleted.getId() + "/file";
        }

        return NoLudicaAttemptMapper.toDto(plainText, hasFile, fileName, downloadUrl);
    }

    @Override
    public FileDownloadData cu128GetNoLudicaAttemptFile(Long activityCompletedId, User user) {

        ActivityCompleted activityCompleted = findAndValidateActivityCompleted(activityCompletedId, user);

        NoLudicaAttempt noLudicaAttempt = activityCompleted.getNoLudicaAttempt();

        if (noLudicaAttempt.getFile() == null) {
            throw new ConflictException("The No ludica attempt do not have a file");
        }
        StoredFile file = noLudicaAttempt.getFile();
        FileDownloadData fileDownloadData = null;

        return fileDownloadData;
    }

    private ActivityCompleted findAndValidateActivityCompleted(Long activityCompletedId, User user) {

        ActivityCompleted activityCompleted = activityGetCompletedByIdService
                .findActivityCompletedById(activityCompletedId);

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        if (!activityCompleted.getActivity().getSubject().getTeacher().equals(teacher)) {
            throw new ConflictException("The teacher is not the owner of the activity");
        }

        if (!activityCompleted.getActivity().getName().equals("No Ludica")) {
            throw new ConflictException("The activity is not a No Ludica activity");
        }

        if (activityCompleted.getNoLudicaAttempt() == null) {
            throw new ConflictException("The activity Completed do not have a No Ludica attempt");
        }

        return activityCompleted;
    }

}
