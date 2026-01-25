package eu.wodrobina.rodos.sensorreading;

public enum SensorUnit {
    CELSIUS("°C");

    private final String symbol;

    SensorUnit(String unit) {
        this.symbol = unit;
    }

    public String getUnit() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
