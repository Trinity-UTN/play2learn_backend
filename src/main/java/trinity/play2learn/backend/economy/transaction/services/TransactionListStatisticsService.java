package trinity.play2learn.backend.economy.transaction.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionStatisticsResponseDto;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionListStatisticsService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;

@Service
@AllArgsConstructor
public class TransactionListStatisticsService implements ITransactionListStatisticsService {

    private final ITransactionRepository transactionRepository;
    
    @Override
    public List<TransactionStatisticsResponseDto> execute() {

        List<TransactionStatisticsResponseDto> result = new ArrayList<>();

        Double total = 0.0;
        Double totalCirculation = 0.0;
        Double totalReserve = 0.0;
        Double totalSubject = 0.0;
        Double totalStudent = 0.0;
        Double totalActivity = 0.0;

        for (Transaction transaction : transactionRepository.findAllByOrderByCreatedAtAsc()) {
            String desc = transaction.getDescription();
            Double amount = transaction.getAmount() != null ? transaction.getAmount() : 0.0;

            switch (desc) {
                case "Asignación de monedas mensual.":
                    // Se toman de la reserva si hay, sino se emiten nuevas
                    Double fromReserve = Math.min(amount, totalReserve);
                    Double minted = amount - fromReserve;

                    totalReserve -= fromReserve;
                    totalReserve = Math.max(0.0, totalReserve);

                    totalSubject += amount;
                    totalCirculation += amount;
                    total += minted;

                    break;

                default:
                    if (desc.startsWith("Actividad de")) {
                        totalSubject -= amount;
                        totalSubject = Math.max(0.0, totalSubject);

                        totalActivity += amount;
                    } else if (desc.equals("Recompensa por actividad completada")) {
                        totalActivity -= amount;
                        totalActivity = Math.max(0.0, totalActivity);

                        totalStudent += amount;
                    } else if (desc.equals("Compra de Aspecto") ||
                               desc.equals("Compra de beneficio") ||
                               desc.equals("Compra de acciones") ||
                               desc.equals("Inversion en plazo fijo.")) {
                        totalStudent -= amount;
                        totalStudent = Math.max(0.0, totalStudent);

                        totalReserve += amount;
                        totalCirculation -= amount;
                    } else if (desc.equals("Venta de acciones") ||
                               desc.equals("Reembolso de beneficio")) {
                        totalReserve -= amount;
                        totalReserve = Math.max(0.0, totalReserve);

                        totalStudent += amount;
                        totalCirculation += amount;
                    }
                    break;
            }

            // Creamos DTO con snapshot de totales en esta fecha
            TransactionStatisticsResponseDto dto = TransactionStatisticsResponseDto.builder()
                    .date(transaction.getCreatedAt())
                    .total(total)
                    .totalCirculation(totalCirculation)
                    .totalReserve(totalReserve)
                    .totalSubject(totalSubject)
                    .totalStudent(totalStudent)
                    .totalActivity(totalActivity)
                    .build();

            result.add(dto);
        }

        return result;
    }
    
}
