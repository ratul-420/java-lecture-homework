package hu.homework.homework;

import com.oanda.v20.Context;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@Controller
@RequestMapping("/forex")
public class ForexController {

    @GetMapping("/account")
    public String account(Model model) {
        try {
            Context ctx = new Context(Config.URL, Config.TOKEN);
            AccountSummary s = ctx.account.summary(Config.ACCOUNTID).getAccount();

            Map<String, String> rows = new LinkedHashMap<>();
            rows.put("Account ID", String.valueOf(s.getId()));
            rows.put("Alias", String.valueOf(s.getAlias()));
            rows.put("Currency", String.valueOf(s.getCurrency()));
            rows.put("Balance", String.valueOf(s.getBalance()));
            rows.put("NAV", String.valueOf(s.getNAV()));
            rows.put("Unrealized P/L", String.valueOf(s.getUnrealizedPL()));
            rows.put("Margin used", String.valueOf(s.getMarginUsed()));
            rows.put("Margin available", String.valueOf(s.getMarginAvailable()));
            rows.put("Open trades", String.valueOf(s.getOpenTradeCount()));
            rows.put("Open positions", String.valueOf(s.getOpenPositionCount()));
            model.addAttribute("rows", rows);
        } catch (Exception e) {
            model.addAttribute("error", "Could not read account: " + e.getMessage());
        }
        return "forex/account";
    }
    @GetMapping("/actprice")
    public String actPrice(@RequestParam(required = false) String instrument, Model model) {
        model.addAttribute("instruments", Config.INSTRUMENTS);
        model.addAttribute("selected", instrument);

        if (instrument != null && Config.INSTRUMENTS.contains(instrument)) {
            try {
                Context ctx = new Context(Config.URL, Config.TOKEN);
                PricingGetRequest request = new PricingGetRequest(Config.ACCOUNTID, List.of(instrument));
                PricingGetResponse resp = ctx.pricing.get(request);
                ClientPrice price = resp.getPrices().get(0);
                model.addAttribute("time", String.valueOf(price.getTime()));
                model.addAttribute("bid", String.valueOf(price.getCloseoutBid()));
                model.addAttribute("ask", String.valueOf(price.getCloseoutAsk()));
            } catch (Exception e) {
                model.addAttribute("error", "Could not read price: " + e.getMessage());
            }
        }
        return "forex/actprice";
    }
}