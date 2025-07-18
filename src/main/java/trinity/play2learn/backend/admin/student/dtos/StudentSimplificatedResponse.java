package trinity.play2learn.backend.admin.student.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StudentSimplificatedResponse { //DTO de respuesta simplificado de estudiante
    
    private Long id;
    private String name;
    private String lastname;
    private String dni;
}
