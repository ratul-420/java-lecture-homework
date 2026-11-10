package hu.homework.homework;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.primitives.InstrumentName;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/forex")
public class ForexDataController {

    private static final Map<String, CandlestickGranularity> GRANULARITIES = Map.of(
            "M1", CandlestickGranularity.M1,
            "H1", CandlestickGranularity.H1,
            "D", CandlestickGranularity.D,
            "W", CandlestickGranularity.W,
            "M", CandlestickGranularity.M);

    private static final List<String> GRANULARITY_NAMES = List.of("M1", "H1", "D", "W", "M");

    public record PriceRow(String time, String close) {}

    @GetMapping("/histprice")
    public String histPrice(@RequestParam(required = false) String instrument,
                            @RequestParam(required = false) String granularity,
                            Model model) {
        model.addAttribute("instruments", Config.INSTRUMENTS);
        model.addAttribute("granularities", GRANULARITY_NAMES);
        model.addAttribute("selected", instrument);
        model.addAttribute("selectedGranularity", granularity);

        if (instrument != null && granularity != null) {
            if (!Config.INSTRUMENTS.contains(instrument) || !GRANULARITIES.containsKey(granularity)) {
                model.addAttribute("error", "Choose an instrument and a granularity from the lists.");
                return "forex/histprice";
            }
            try {
                Context ctx = new Context(Config.URL, Config.TOKEN);
                InstrumentCandlesRequest request = new InstrumentCandlesRequest(new InstrumentName(instrument));
                request.setGranularity(GRANULARITIES.get(granularity));
                request.setCount(10L);
                InstrumentCandlesResponse resp = ctx.instrument.candles(request);

                List<PriceRow> rows = new ArrayList<>();
                for (Candlestick candle : resp.getCandles()) {
                    rows.add(new PriceRow(String.valueOf(candle.getTime()),
                            String.valueOf(candle.getMid().getC())));
                }
                if (rows.isEmpty()) {
                    model.addAttribute("error", "No historical prices returned.");
                } else {
                    model.addAttribute("rows", rows);
                }
            } catch (Exception e) {
                model.addAttribute("error", "Could not read historical prices: " + e.getMessage());
            }
        }
        return "forex/histprice";
    }
}