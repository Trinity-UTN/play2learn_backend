package trinity.play2learn.backend.profile.ranking.services.commons;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingGetStudentPositionService;

@Service
public class RankingGetStudentPositionService implements IRankingGetStudentPositionService {

    // Devuelve la posicion del estudiante en el ranking de monedas
    @Override
    public Long getStudentRankingPositionByCoins(List<StudentWithTotalDto> list,
            StudentWithTotalDto targetDto) {
        // Ordenamos la lista de mayor a menor recompensa
        List<StudentWithTotalDto> sortedList = list.stream()
                .sorted(Comparator.comparing(StudentWithTotalDto::getTotal).reversed())
                .collect(Collectors.toList());

        // Buscamos la posición comparando el ID del estudiante dentro del DTO
        for (int i = 0; i < sortedList.size(); i++) {
            if (sortedList.get(i).getStudent().getId().equals(targetDto.getStudent().getId())) {
                return (long) i + 1; // +1 para que empiece en 1
            }
        }

        return -1L; // No encontrado
    }
}
