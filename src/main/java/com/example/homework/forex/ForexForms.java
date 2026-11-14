package com.example.homework.forex;

/** Form-data classes of the Forex menus (same as in the lecture, grouped in one file). */
public final class ForexForms {
    private ForexForms() {}

    public static class MessageActPrice {
        private String instrument = "EUR_USD";
        public String getInstrument() { return instrument; }
        public void setInstrument(String instrument) { this.instrument = instrument; }
    }

    public static class MessageHistPrice {
        private String instrument = "EUR_USD";
        private String granularity = "D";
        public String getInstrument() { return instrument; }
        public void setInstrument(String instrument) { this.instrument = instrument; }
        public String getGranularity() { return granularity; }
        public void setGranularity(String granularity) { this.granularity = granularity; }
    }

    public static class MessageOpenPosition {
        private String instrument = "EUR_USD";
        private int units = 10;
        public String getInstrument() { return instrument; }
        public void setInstrument(String instrument) { this.instrument = instrument; }
        public int getUnits() { return units; }
        public void setUnits(int units) { this.units = units; }
    }

    public static class MessageClosePosition {
        private int tradeId;
        public int getTradeId() { return tradeId; }
        public void setTradeId(int tradeId) { this.tradeId = tradeId; }
    }
}
