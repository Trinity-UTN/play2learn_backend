package trinity.play2learn.backend.investment.stock.services.commons;

import java.math.BigInteger;
import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateVariationService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryCalculateTrendService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindLastService;

@Service
@AllArgsConstructor
public class StockCalculateVariationService implements IStockCalculateVariationService {

    private final IStockHistoryFindLastService stockHistoryFindLastService;

    private final IStockHistoryCalculateTrendService stockHistoryCalculateTrendService;
    
    @Override
    public Double cu84calculateVariation (Stock stock) {
        // Calcula la variacion del stock segun su riesgo, tendencia y ventas recientes

        // Obtiene el ultimo StockHistory registrado
        StockHistory lastHistory = stockHistoryFindLastService.execute(stock);

        // Calcula el cambio en las ventas de acciones
        // Esto puede dar un valor positivo o negativo dependiendo si se han vendido mas o menos acciones
        BigInteger stocksChange = stock.getSoldAmount().subtract(lastHistory.getSoldAmount());

        // Calculo el sesgo basado en el cambio de ventas
        Double bias = stocksChange.doubleValue() / stock.getTotalAmount().doubleValue();

        Random random = new Random();

        // Calculo el rango de variacion basado en el riesgo del stock y el sesgo
        Double min = Math.max(-stock.getRiskLevel().getValor() + stock.getRiskLevel().getValor() * bias, -stock.getRiskLevel().getValor());

        Double max = Math.min(stock.getRiskLevel().getValor() + stock.getRiskLevel().getValor() * bias, stock.getRiskLevel().getValor());

        // Ajusta el rango segun la tendencia del stock
        if (stockHistoryCalculateTrendService.execute(stock)) {
            min = min / 2;
        } else {
            max = max / 2;
        }
        
        // Retorna un valor aleatorio dentro del rango calculado
        return (random.nextDouble() * (max - min) + min);
    }
    
}
