package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.investment.stock.dtos.response.CandleStickChartValueResponseDto;
import trinity.play2learn.backend.investment.stock.models.RangeValue;

public interface ICandleStickGetValuesService {

    public List<CandleStickChartValueResponseDto> cu83GetValuesCandleStick (Long stockId, RangeValue rangeValue);

} 