package hu.homework.homework;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class SoapController {

    private static final List<String> CURRENCIES =
            List.of("EUR", "USD", "GBP", "CHF", "JPY", "AUD", "CAD", "CZK", "PLN");

    @GetMapping("/soap")
    public String soap(@RequestParam(required = false) String currency,
                       @RequestParam(required = false) String start,
                       @RequestParam(required = false) String end,
                       Model model) {
        if (start == null || start.isBlank()) start = LocalDate.now().minusDays(14).toString();
        if (end == null || end.isBlank()) end = LocalDate.now().toString();

        model.addAttribute("currencies", CURRENCIES);
        model.addAttribute("selected", currency);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "soap";
    }
}