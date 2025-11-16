package trinity.play2learn.backend.economy.transaction.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.economy.EconomyTestMother;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionStatisticsResponseDto;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.repositories.ITransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionListStatisticsServiceTest {

    @Mock
    private ITransactionRepository transactionRepository;

    private TransactionListStatisticsService transactionListStatisticsService;

    @BeforeEach
    void setUp() {
        transactionListStatisticsService = new TransactionListStatisticsService(transactionRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given empty transaction list When calculating statistics Then returns empty list")
        void whenEmptyTransactionList_returnsEmptyList() {
            // Given - Lista vacía de transacciones
            List<Transaction> transactions = List.of();
            when(transactionRepository.findAllByOrderByCreatedAtAsc()).thenReturn(transactions);

            // When - Calcular estadísticas
            List<TransactionStatisticsResponseDto> result = transactionListStatisticsService.execute();

            // Then - Debe retornar lista vacía
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Given transaction list with monthly assignment When calculating statistics Then calculates totals correctly")
        void whenTransactionListWithMonthlyAssignment_calculatesTotalsCorrectly() {
            // Given - Lista de transacciones con asignación mensual
            LocalDateTime date1 = LocalDateTime.now();
            Transaction transaction1 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID,
                "Asignación de monedas mensual.",
                10000.0
            );
            transaction1.setCreatedAt(date1);

            List<Transaction> transactions = Arrays.asList(transaction1);
            when(transactionRepository.findAllByOrderByCreatedAtAsc()).thenReturn(transactions);

            // When - Calcular estadísticas
            List<TransactionStatisticsResponseDto> result = transactionListStatisticsService.execute();

            // Then - Debe calcular totales correctamente
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            
            TransactionStatisticsResponseDto dto = result.get(0);
            assertThat(dto.getDate()).isEqualTo(date1);
            assertThat(dto.getTotal()).isEqualTo(10000.0); // Minted (no hay reserva)
            assertThat(dto.getTotalCirculation()).isEqualTo(10000.0);
            assertThat(dto.getTotalSubject()).isEqualTo(10000.0);
            assertThat(dto.getTotalReserve()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Given transaction list with activity When calculating statistics Then calculates totals correctly")
        void whenTransactionListWithActivity_calculatesTotalsCorrectly() {
            // Given - Lista de transacciones con actividad
            LocalDateTime date1 = LocalDateTime.now();
            Transaction transaction1 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID,
                "Actividad de Matemática",
                2000.0
            );
            transaction1.setCreatedAt(date1);

            List<Transaction> transactions = Arrays.asList(transaction1);
            when(transactionRepository.findAllByOrderByCreatedAtAsc()).thenReturn(transactions);

            // When - Calcular estadísticas
            List<TransactionStatisticsResponseDto> result = transactionListStatisticsService.execute();

            // Then - Debe calcular totales correctamente
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            
            TransactionStatisticsResponseDto dto = result.get(0);
            // Nota: El servicio usa Math.max(0.0, totalSubject) para evitar valores negativos
            // Como totalSubject inicia en 0 y se resta 2000, queda en 0 (no negativo)
            assertThat(dto.getTotalSubject()).isEqualTo(0.0);
            assertThat(dto.getTotalActivity()).isEqualTo(2000.0);
        }

        @Test
        @DisplayName("Given transaction list with reward When calculating statistics Then calculates totals correctly")
        void whenTransactionListWithReward_calculatesTotalsCorrectly() {
            // Given - Lista de transacciones con recompensa
            LocalDateTime date1 = LocalDateTime.now();
            Transaction transaction1 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID,
                "Recompensa por actividad completada",
                1500.0
            );
            transaction1.setCreatedAt(date1);

            List<Transaction> transactions = Arrays.asList(transaction1);
            when(transactionRepository.findAllByOrderByCreatedAtAsc()).thenReturn(transactions);

            // When - Calcular estadísticas
            List<TransactionStatisticsResponseDto> result = transactionListStatisticsService.execute();

            // Then - Debe calcular totales correctamente
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            
            TransactionStatisticsResponseDto dto = result.get(0);
            // Nota: El servicio usa Math.max(0.0, totalActivity) para evitar valores negativos
            // Como totalActivity inicia en 0 y se resta 1500, queda en 0 (no negativo)
            assertThat(dto.getTotalActivity()).isEqualTo(0.0);
            assertThat(dto.getTotalStudent()).isEqualTo(1500.0);
        }

        @Test
        @DisplayName("Given transaction list with purchase When calculating statistics Then calculates totals correctly")
        void whenTransactionListWithPurchase_calculatesTotalsCorrectly() {
            // Given - Lista de transacciones con compra
            LocalDateTime date1 = LocalDateTime.now();
            Transaction transaction1 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID,
                "Compra de beneficio",
                100.0
            );
            transaction1.setCreatedAt(date1);

            List<Transaction> transactions = Arrays.asList(transaction1);
            when(transactionRepository.findAllByOrderByCreatedAtAsc()).thenReturn(transactions);

            // When - Calcular estadísticas
            List<TransactionStatisticsResponseDto> result = transactionListStatisticsService.execute();

            // Then - Debe calcular totales correctamente
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            
            TransactionStatisticsResponseDto dto = result.get(0);
            // Nota: El servicio usa Math.max(0.0, totalStudent) pero NO aplica Math.max a totalCirculation
            // totalStudent inicia en 0, se resta 100, queda en 0 con Math.max
            // totalCirculation inicia en 0, se resta 100, queda en -100 (puede ser negativo)
            assertThat(dto.getTotalStudent()).isEqualTo(0.0);
            assertThat(dto.getTotalReserve()).isEqualTo(100.0);
            assertThat(dto.getTotalCirculation()).isEqualTo(-100.0);
        }

        @Test
        @DisplayName("Given transaction list with refund When calculating statistics Then calculates totals correctly")
        void whenTransactionListWithRefund_calculatesTotalsCorrectly() {
            // Given - Lista de transacciones con reembolso
            LocalDateTime date1 = LocalDateTime.now();
            Transaction transaction1 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID,
                "Reembolso de beneficio",
                100.0
            );
            transaction1.setCreatedAt(date1);

            List<Transaction> transactions = Arrays.asList(transaction1);
            when(transactionRepository.findAllByOrderByCreatedAtAsc()).thenReturn(transactions);

            // When - Calcular estadísticas
            List<TransactionStatisticsResponseDto> result = transactionListStatisticsService.execute();

            // Then - Debe calcular totales correctamente
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            
            TransactionStatisticsResponseDto dto = result.get(0);
            // Nota: El servicio usa Math.max(0.0, totalReserve) para evitar valores negativos
            // Como totalReserve inicia en 0 y se resta 100, queda en 0 (no negativo)
            assertThat(dto.getTotalReserve()).isEqualTo(0.0);
            assertThat(dto.getTotalStudent()).isEqualTo(100.0);
            assertThat(dto.getTotalCirculation()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("Given transaction list with multiple types When calculating statistics Then calculates cumulative totals correctly")
        void whenTransactionListWithMultipleTypes_calculatesCumulativeTotalsCorrectly() {
            // Given - Lista de transacciones con múltiples tipos
            LocalDateTime date1 = LocalDateTime.now().minusDays(2);
            LocalDateTime date2 = LocalDateTime.now().minusDays(1);
            LocalDateTime date3 = LocalDateTime.now();

            Transaction transaction1 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID,
                "Asignación de monedas mensual.",
                10000.0
            );
            transaction1.setCreatedAt(date1);

            Transaction transaction2 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID + 1,
                "Compra de beneficio",
                500.0
            );
            transaction2.setCreatedAt(date2);

            Transaction transaction3 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID + 2,
                "Reembolso de beneficio",
                100.0
            );
            transaction3.setCreatedAt(date3);

            List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3);
            when(transactionRepository.findAllByOrderByCreatedAtAsc()).thenReturn(transactions);

            // When - Calcular estadísticas
            List<TransactionStatisticsResponseDto> result = transactionListStatisticsService.execute();

            // Then - Debe calcular totales acumulados correctamente
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            
            // Primera transacción: asignación
            TransactionStatisticsResponseDto dto1 = result.get(0);
            assertThat(dto1.getTotal()).isEqualTo(10000.0);
            assertThat(dto1.getTotalCirculation()).isEqualTo(10000.0);
            
            // Segunda transacción: compra
            // Nota: totalStudent inicia en 0 (desde dto1), se resta 500, queda en 0 con Math.max
            TransactionStatisticsResponseDto dto2 = result.get(1);
            assertThat(dto2.getTotalStudent()).isEqualTo(0.0);
            assertThat(dto2.getTotalReserve()).isEqualTo(500.0);
            assertThat(dto2.getTotalCirculation()).isEqualTo(9500.0);
            
            // Tercera transacción: reembolso
            // Nota: totalReserve es 500 (desde dto2), se resta 100, queda en 400
            // totalStudent es 0 (desde dto2), se suma 100, queda en 100
            TransactionStatisticsResponseDto dto3 = result.get(2);
            assertThat(dto3.getTotalReserve()).isEqualTo(400.0);
            assertThat(dto3.getTotalStudent()).isEqualTo(100.0);
            assertThat(dto3.getTotalCirculation()).isEqualTo(9600.0);
        }

        @Test
        @DisplayName("Given transaction with null amount When calculating statistics Then treats as zero")
        void whenTransactionWithNullAmount_treatsAsZero() {
            // Given - Transacción con monto null
            LocalDateTime date1 = LocalDateTime.now();
            Transaction transaction1 = EconomyTestMother.transactionWithDescription(
                EconomyTestMother.DEFAULT_TRANSACTION_ID,
                "Compra de beneficio",
                100.0
            );
            transaction1.setAmount(null);
            transaction1.setCreatedAt(date1);

            List<Transaction> transactions = Arrays.asList(transaction1);
            when(transactionRepository.findAllByOrderByCreatedAtAsc()).thenReturn(transactions);

            // When - Calcular estadísticas
            List<TransactionStatisticsResponseDto> result = transactionListStatisticsService.execute();

            // Then - Debe tratar monto null como cero
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            
            TransactionStatisticsResponseDto dto = result.get(0);
            assertThat(dto.getTotalStudent()).isEqualTo(0.0);
            assertThat(dto.getTotalReserve()).isEqualTo(0.0);
        }
    }
}

