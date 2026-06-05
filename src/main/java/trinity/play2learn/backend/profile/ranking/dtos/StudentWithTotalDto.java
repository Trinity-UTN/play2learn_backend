package trinity.play2learn.backend.profile.ranking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithTotalDto {

    private Student student;
    private Double total; // Total de monedas conseguidas (Puede ser el total o solo de una materia)
}
