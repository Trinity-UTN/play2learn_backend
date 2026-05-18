package trinity.play2learn.backend.profile.ranking.services.commons;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import trinity.play2learn.backend.profile.ranking.dtos.StudentWithRewardTotalDto;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingGetTop10StudentsService;

@Service
public class RankingGetTop10StudentsService implements IRankingGetTop10StudentsService {

    //Devuelve los 10 estudiantes con mayor cantidad de monedas ordenados de forma descendente
    @Override
    public List<StudentWithRewardTotalDto> getTop10StudentsByCoins(List<StudentWithRewardTotalDto> students) {
        return students.stream()
                // Ordena de forma descendente por totalReward
                .sorted(Comparator.comparing(StudentWithRewardTotalDto::getTotalReward).reversed())
                // Toma solo los primeros 10
                .limit(10)
                .collect(Collectors.toList());
    }
}
