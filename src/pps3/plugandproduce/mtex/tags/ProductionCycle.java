package pps3.plugandproduce.mtex.tags;

public enum ProductionCycle {
    PRODUCTION_CYCLE_OFF(false),
    PRODUCTION_CYCLE_ON(true);

    private final boolean code;

    private ProductionCycle(boolean code) {
        this.code = code;
    }

    public static ProductionCycle findByCode(boolean code) {
        for (ProductionCycle cycle : values()) {
            if (cycle.code == code) {
                return cycle;
            }
        }
        return null;
    }

    public static boolean getCode(ProductionCycle tag) {
        return tag.code;
    }
}
