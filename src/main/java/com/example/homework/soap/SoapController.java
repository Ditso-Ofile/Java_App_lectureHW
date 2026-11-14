package com.example.homework.soap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

/** SOAP menu: MNB exchange rates in a table + Chart.js line chart. */
@Controller
public class SoapController {

    static final List<String> CURRENCIES = List.of(
            "EUR", "USD", "GBP", "CHF", "JPY", "CZK", "PLN", "RON", "SEK", "NOK", "DKK", "CAD", "AUD", "CNY", "TRY");

    private final MnbService mnbService;

    public SoapController(MnbService mnbService) {
        this.mnbService = mnbService;
    }

    @GetMapping("/soap")
    public String form(Model model) {
        MessagePrice param = new MessagePrice();
        param.setEndDate(LocalDate.now().toString());
        param.setStartDate(LocalDate.now().minusMonths(1).toString());
        model.addAttribute("priceForm", param);
        model.addAttribute("currencies", CURRENCIES);
        return "soap";
    }

    @PostMapping("/soap")
    public String result(@ModelAttribute("priceForm") MessagePrice param, Model model) throws Exception {
        model.addAttribute("currencies", CURRENCIES);

        if (param.getStartDate() == null || param.getStartDate().isBlank()
                || param.getEndDate() == null || param.getEndDate().isBlank()) {
            model.addAttribute("error", "Please choose both a start and an end date.");
            return "soap";
        }
        if (param.getStartDate().compareTo(param.getEndDate()) > 0) {
            model.addAttribute("error", "The start date must be before the end date.");
            return "soap";
        }

        String xml = mnbService.getExchangeRatesXml(param.getStartDate(), param.getEndDate(), param.getCurrency());
        List<MnbService.RatePoint> points = mnbService.parse(xml);

        model.addAttribute("points", points);
        model.addAttribute("labels", points.stream().map(MnbService.RatePoint::date).toList());
        model.addAttribute("values", points.stream().map(MnbService.RatePoint::rate).toList());
        model.addAttribute("rawXml", xml);
        if (points.isEmpty()) {
            model.addAttribute("error", "MNB returned no data for this currency and period.");
        }
        return "soap";
    }
}
