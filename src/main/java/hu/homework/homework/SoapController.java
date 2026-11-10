package hu.homework.homework;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import soapclient.MNBArfolyamServiceSoap;
import soapclient.MNBArfolyamServiceSoapImpl;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SoapController {

    private static final List<String> CURRENCIES =
            List.of("EUR", "USD", "GBP", "CHF", "JPY", "AUD", "CAD", "CZK", "PLN");

    public record RateRow(String date, double rate) {}

    @GetMapping("/soap")
    public String soap(@RequestParam(required = false) String currency,
                       @RequestParam(required = false) String start,
                       @RequestParam(required = false) String end,
                       Model model) {
        boolean submitted = currency != null;
        if (start == null || start.isBlank()) start = LocalDate.now().minusDays(14).toString();
        if (end == null || end.isBlank()) end = LocalDate.now().toString();

        model.addAttribute("currencies", CURRENCIES);
        model.addAttribute("selected", currency);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        if (submitted) {
            try {
                if (!CURRENCIES.contains(currency)) {
                    model.addAttribute("error", "Choose a currency from the list.");
                } else if (LocalDate.parse(start).isAfter(LocalDate.parse(end))) {
                    model.addAttribute("error", "The start date must not be after the end date.");
                } else {
                    MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
                    MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();
                    String xml = service.getExchangeRates(start, end, currency);

                    List<RateRow> rows = parse(xml);
                    if (rows.isEmpty()) {
                        model.addAttribute("error", "No data for this period (weekends and holidays have no rates).");
                    } else {
                        model.addAttribute("rows", rows);
                    }
                }
            } catch (Exception e) {
                model.addAttribute("error", "Could not get MNB rates: " + e.getMessage());
            }
        }
        return "soap";
    }

    private List<RateRow> parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document doc = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        List<RateRow> rows = new ArrayList<>();
        NodeList days = doc.getElementsByTagName("Day");
        for (int i = 0; i < days.getLength(); i++) {
            Element day = (Element) days.item(i);
            NodeList rates = day.getElementsByTagName("Rate");
            if (rates.getLength() == 0) continue;
            Element rate = (Element) rates.item(0);
            double value = Double.parseDouble(rate.getTextContent().trim().replace(',', '.'));
            double unit = Double.parseDouble(rate.getAttribute("unit"));
            rows.add(new RateRow(day.getAttribute("date"), value / unit));
        }
        return rows;
    }
}