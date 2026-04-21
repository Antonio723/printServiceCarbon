package cars.carbon.printService.production.invoice.aging;

public enum AgingBucket {
    CURRENT("0–7 dias",  0,  7),
    ATTENTION("8–15 dias", 8, 15),
    WARNING("16–30 dias", 16, 30),
    CRITICAL("> 30 dias",  31, Integer.MAX_VALUE);

    public final String label;
    public final int minDays;
    public final int maxDays;

    AgingBucket(String label, int minDays, int maxDays) {
        this.label   = label;
        this.minDays = minDays;
        this.maxDays = maxDays;
    }

    public static AgingBucket of(long days) {
        for (AgingBucket b : values()) {
            if (days >= b.minDays && days <= b.maxDays) return b;
        }
        return CRITICAL;
    }
}
