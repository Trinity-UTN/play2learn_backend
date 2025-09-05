package trinity.play2learn.backend.admin.student.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.economy.wallet.dtos.response.WalletResponseDto;
import trinity.play2learn.backend.profile.profile.dtos.response.ProfileResponseDto;
import trinity.play2learn.backend.user.dtos.user.UserResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {
    
    private Long id;

    private String name;

    private String lastname;

    private String dni;

    private LocalDate birthdate;

    private String emailTutor;
    
    private UserResponseDto user;

    private CourseResponseDto course;

    private boolean active;

    private ProfileResponseDto profile;

    private WalletResponseDto wallet;


}
