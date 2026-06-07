package trinity.play2learn.backend.configs.seed.simulation.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.templates.StockSimulationTemplateFactory;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;
import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;

@ExtendWith(MockitoExtension.class)
class StockSimulationCatalogServiceTest {

    @Mock
    private SimulationProperties properties;

    @Mock
    private IStudentRepository studentRepository;

    @Mock
    private IStockRepository stockRepository;

    @Mock
    private IStockHistoryRepository stockHistoryRepository;

    @InjectMocks
    private StockSimulationCatalogService stockSimulationCatalogService;

    @Test
    @DisplayName("Given existing stocks When simulating Then skips creation")
    void existingStocks_skipsCreation() {
        when(properties.getRandomSeed()).thenReturn(42L);
        SimulationTimeline timeline = new SimulationTimeline(
            java.time.LocalDateTime.of(2025, 1, 1, 8, 0),
            java.time.LocalDateTime.of(2025, 3, 31, 18, 0),
            properties
        );
        SimulationDataCollector collector = new SimulationDataCollector();
        when(stockRepository.findAll()).thenReturn(List.of(Stock.builder().id(1L).build()));

        stockSimulationCatalogService.simulate(timeline, collector);

        verify(stockRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given empty catalog When building templates Then prices are accessible")
    void templatePrices_accessible() {
        when(properties.getRandomSeed()).thenReturn(42L);
        SimulationTimeline timeline = new SimulationTimeline(
            java.time.LocalDateTime.of(2025, 1, 1, 8, 0),
            java.time.LocalDateTime.of(2025, 3, 31, 18, 0),
            properties
        );
        List<StockRegisterRequestDto> catalog = StockSimulationTemplateFactory.buildCatalog(
            5, 25.0, timeline.random()
        );
        assertThat(catalog).hasSize(5);
        assertThat(catalog).allMatch(dto -> dto.getInitialPrice() >= 10.0);
    }
}
