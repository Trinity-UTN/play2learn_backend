package trinity.play2learn.backend.configs.seed.simulation.templates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.models.RiskLevel;

/**
 * Genera datos de catálogo de acciones simuladas.
 */
public final class StockSimulationTemplateFactory {

    private static final String[] COMPANY_NAMES = {
        "NovaTech", "RíoSur", "Altavista", "CampoVerde", "PampaDigital",
        "AndesLogistics", "LumenSoft", "HorizonteBio", "CóndorNet", "EstrellaRoja",
        "ValleGrande", "TierraFirme"
    };

    private static final String[] ABBREVIATIONS = {
        "NVT", "RSU", "AVS", "CVG", "PDG", "ALG", "LMS", "HRB", "CDN", "ESR", "VGD", "TFR"
    };

    private StockSimulationTemplateFactory() {
    }

    public static List<StockRegisterRequestDto> buildCatalog(
        int count,
        double initialPrice,
        Random random
    ) {
        List<StockRegisterRequestDto> catalog = new ArrayList<>();
        Set<String> usedAbbreviations = new HashSet<>();
        RiskLevel[] levels = RiskLevel.values();

        for (int i = 0; i < count; i++) {
            int index = i % COMPANY_NAMES.length;
            String name = COMPANY_NAMES[index] + " Sim";
            String abbreviation = resolveAbbreviation(index, usedAbbreviations, i);

            double priceJitter = 0.85 + random.nextDouble() * 0.3;
            double price = Math.round(initialPrice * priceJitter * 100.0) / 100.0;

            catalog.add(StockRegisterRequestDto.builder()
                .name(name)
                .abbreviation(abbreviation)
                .initialPrice(price)
                .totalAmount(BigInteger.valueOf(10_000))
                .riskLevel(levels[i % levels.length])
                .build());
        }
        return catalog;
    }

    private static String resolveAbbreviation(int index, Set<String> used, int fallbackIndex) {
        String base = ABBREVIATIONS[index % ABBREVIATIONS.length];
        String candidate = base;
        int suffix = 1;
        while (used.contains(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        used.add(candidate);
        return candidate;
    }
}
