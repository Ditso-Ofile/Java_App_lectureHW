package com.example.homework.forex;

import com.example.homework.forex.ForexForms.MessageActPrice;
import com.example.homework.forex.ForexForms.MessageClosePosition;
import com.example.homework.forex.ForexForms.MessageHistPrice;
import com.example.homework.forex.ForexForms.MessageOpenPosition;
import com.oanda.v20.Context;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.InstrumentName;
import com.oanda.v20.trade.Trade;
import com.oanda.v20.trade.TradeCloseRequest;
import com.oanda.v20.trade.TradeSpecifier;
import com.oanda.v20.transaction.OrderFillTransaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Forex menus: Account, ActPrice, HistPrice, Open, Pos, Close (Oanda v20 API). */
@Controller
public class ForexController {

    static final List<String> INSTRUMENTS = List.of(
            "EUR_USD", "GBP_USD", "USD_JPY", "USD_CHF", "AUD_USD", "USD_CAD", "NZD_USD", "EUR_GBP", "EUR_JPY", "EUR_HUF", "USD_HUF");
    static final List<String> GRANULARITIES = List.of("M1", "M5", "M15", "H1", "H4", "D", "W", "M");

    private final OandaConfig config;

    public ForexController(OandaConfig config) {
        this.config = config;
    }

    private static String str(Object o) {
        return o == null ? "-" : o.toString();
    }

    // ---------------- Forex-Account ----------------
    @GetMapping("/forex/account")
    public String account(Model model) throws Exception {
        AccountSummary s = config.context().account.summary(config.accountId()).getAccount();
        Map<String, String> rows = new LinkedHashMap<>();
        rows.put("Account ID", str(s.getId()));
        rows.put("Alias", str(s.getAlias()));
        rows.put("Currency", str(s.getCurrency()));
        rows.put("Balance", str(s.getBalance()));
        rows.put("NAV (Net Asset Value)", str(s.getNAV()));
        rows.put("Unrealized P/L", str(s.getUnrealizedPL()));
        rows.put("Realized P/L", str(s.getPl()));
        rows.put("Financing", str(s.getFinancing()));
        rows.put("Margin rate", str(s.getMarginRate()));
        rows.put("Margin used", str(s.getMarginUsed()));
        rows.put("Margin available", str(s.getMarginAvailable()));
        rows.put("Position value", str(s.getPositionValue()));
        rows.put("Open trades", str(s.getOpenTradeCount()));
        rows.put("Open positions", str(s.getOpenPositionCount()));
        rows.put("Pending orders", str(s.getPendingOrderCount()));
        rows.put("Hedging enabled", str(s.getHedgingEnabled()));
        rows.put("Created", str(s.getCreatedTime()));
        rows.put("Last transaction ID", str(s.getLastTransactionID()));
        model.addAttribute("rows", rows);
        return "forex_account";
    }

    // ---------------- Forex-ActPrice ----------------
    @GetMapping("/forex/actprice")
    public String actPriceForm(Model model) {
        model.addAttribute("actForm", new MessageActPrice());
        model.addAttribute("instruments", INSTRUMENTS);
        return "forex_actprice";
    }

    @PostMapping("/forex/actprice")
    public String actPrice(@ModelAttribute("actForm") MessageActPrice par, Model model) throws Exception {
        model.addAttribute("instruments", INSTRUMENTS);
        List<String> instruments = new ArrayList<>();
        instruments.add(par.getInstrument());
        PricingGetRequest request = new PricingGetRequest(config.accountId(), instruments);
        PricingGetResponse resp = config.context().pricing.get(request);
        if (!resp.getPrices().isEmpty()) {
            ClientPrice p = resp.getPrices().get(0);
            model.addAttribute("time", str(p.getTime()));
            model.addAttribute("bid", p.getBids().isEmpty() ? "-" : str(p.getBids().get(0).getPrice()));
            model.addAttribute("ask", p.getAsks().isEmpty() ? "-" : str(p.getAsks().get(0).getPrice()));
            model.addAttribute("tradeable", str(p.getTradeable()));
        }
        model.addAttribute("instr", par.getInstrument());
        return "forex_actprice";
    }

    // ---------------- Forex-HistPrice ----------------
    @GetMapping("/forex/histprice")
    public String histPriceForm(Model model) {
        model.addAttribute("histForm", new MessageHistPrice());
        model.addAttribute("instruments", INSTRUMENTS);
        model.addAttribute("granularities", GRANULARITIES);
        return "forex_histprice";
    }

    @PostMapping("/forex/histprice")
    public String histPrice(@ModelAttribute("histForm") MessageHistPrice param, Model model) throws Exception {
        model.addAttribute("instruments", INSTRUMENTS);
        model.addAttribute("granularities", GRANULARITIES);

        InstrumentCandlesRequest request = new InstrumentCandlesRequest(new InstrumentName(param.getInstrument()));
        request.setGranularity(CandlestickGranularity.valueOf(param.getGranularity()));
        request.setCount(10L); // the last 10 prices
        InstrumentCandlesResponse resp = config.context().instrument.candles(request);

        List<Map<String, String>> candles = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> closes = new ArrayList<>();
        for (Candlestick c : resp.getCandles()) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("time", str(c.getTime()));
            row.put("open", str(c.getMid().getO()));
            row.put("high", str(c.getMid().getH()));
            row.put("low", str(c.getMid().getL()));
            row.put("close", str(c.getMid().getC()));
            candles.add(row);
            labels.add(str(c.getTime()).replace("T", " ").replaceAll("\\.\\d+Z$", ""));
            closes.add(c.getMid().getC().doubleValue());
        }
        model.addAttribute("candles", candles);
        model.addAttribute("labels", labels);
        model.addAttribute("closes", closes);
        model.addAttribute("instr", param.getInstrument());
        model.addAttribute("granularity", param.getGranularity());
        return "forex_histprice";
    }

    // ---------------- Forex-Open ----------------
    @GetMapping("/forex/open")
    public String openForm(Model model) {
        model.addAttribute("openForm", new MessageOpenPosition());
        model.addAttribute("instruments", INSTRUMENTS);
        return "forex_open";
    }

    @PostMapping("/forex/open")
    public String open(@ModelAttribute("openForm") MessageOpenPosition param, Model model) throws Exception {
        model.addAttribute("instruments", INSTRUMENTS);
        if (param.getUnits() == 0) {
            model.addAttribute("error", "Units must not be 0 (positive = Long, negative = Short).");
            return "forex_open";
        }
        OrderCreateRequest request = new OrderCreateRequest(config.accountId());
        MarketOrderRequest marketOrder = new MarketOrderRequest();
        marketOrder.setInstrument(new InstrumentName(param.getInstrument()));
        marketOrder.setUnits(param.getUnits());
        request.setOrder(marketOrder);
        OrderCreateResponse response = config.context().order.create(request);

        OrderFillTransaction fill = response.getOrderFillTransaction();
        if (fill == null) {
            model.addAttribute("error", "The order was not filled: "
                    + str(response.getOrderCancelTransaction() == null ? null : response.getOrderCancelTransaction().getReason()));
            return "forex_open";
        }
        model.addAttribute("instr", param.getInstrument());
        model.addAttribute("units", param.getUnits());
        model.addAttribute("direction", param.getUnits() > 0 ? "Long (buy)" : "Short (sell)");
        model.addAttribute("tradeId", fill.getTradeOpened() != null ? str(fill.getTradeOpened().getTradeID()) : str(fill.getId()));
        model.addAttribute("price", str(fill.getPrice()));
        return "forex_open";
    }

    // ---------------- Forex-Pos ----------------
    @GetMapping("/forex/positions")
    public String positions(Model model) throws Exception {
        Context ctx = config.context();
        List<Trade> trades = ctx.trade.listOpen(config.accountId()).getTrades();
        model.addAttribute("trades", trades);
        return "forex_positions";
    }

    // ---------------- Forex-Close ----------------
    @GetMapping("/forex/close")
    public String closeForm(Model model) {
        model.addAttribute("closeForm", new MessageClosePosition());
        return "forex_close";
    }

    @PostMapping("/forex/close")
    public String close(@ModelAttribute("closeForm") MessageClosePosition param, Model model) throws Exception {
        String tradeId = String.valueOf(param.getTradeId());
        config.context().trade.close(new TradeCloseRequest(config.accountId(), new TradeSpecifier(tradeId)));
        model.addAttribute("closed", tradeId);
        return "forex_close";
    }
}
