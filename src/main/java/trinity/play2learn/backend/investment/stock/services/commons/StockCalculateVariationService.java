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

    private static final double PRICE_FLOOR = 10.0;
    private static final double PRICE_CEILING_MULTIPLIER = 2.5;
    private static final double MEAN_REVERSION_FACTOR = 0.8;

    private final IStockHistoryFindLastService stockHistoryFindLastService;

    private final IStockHistoryCalculateTrendService stockHistoryCalculateTrendService;
    
    @Override
    public Double execute (Stock stock) {
        // Calcula la variacion del stock segun su riesgo, tendencia, ventas recientes y posicion en el rango

        // Obtiene el ultimo StockHistory registrado
        StockHistory lastHistory = stockHistoryFindLastService.execute(stock);

        // Calcula el cambio en las ventas de acciones
        // Esto puede dar un valor positivo o negativo dependiendo si se han vendido mas o menos acciones
        BigInteger stocksChange = stock.getSoldAmount().subtract(lastHistory.getSoldAmount());

        // Calculo el sesgo basado en el cambio de ventas
        Double bias = stocksChange.doubleValue() / stock.getTotalAmount().doubleValue();

        int risk = stock.getRiskLevel().getValor();
        Random random = new Random();

        // Calculo el rango de variacion basado en el riesgo del stock y el sesgo
        Double min = Math.max(-risk + risk * bias, -risk);
        Double max = Math.min(risk + risk * bias, risk);

        // Contrarian: limita el movimiento en la direccion de la tendencia reciente
        if (stockHistoryCalculateTrendService.execute(stock)) {
            max = max / 2;
        } else {
            min = min / 2;
        }

        // Reversion a la media segun la posicion del precio dentro del rango permitido
        double ceiling = stock.getInitialPrice() * PRICE_CEILING_MULTIPLIER;
        double position = (stock.getCurrentPrice() - PRICE_FLOOR) / (ceiling - PRICE_FLOOR);
        double meanReversion = (0.5 - position) * risk * MEAN_REVERSION_FACTOR;
        min += meanReversion;
        max += meanReversion;
        
        // Retorna un valor aleatorio dentro del rango calculado
        return (random.nextDouble() * (max - min) + min);
    }
    
}
