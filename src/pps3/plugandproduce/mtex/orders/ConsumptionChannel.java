package pps3.plugandproduce.mtex.orders;

public class ConsumptionChannel {
    public float getInkUsageMagenta() {
        return inkUsageMagenta;
    }

    private final float inkUsageMagenta;

    public float getInkUsageBlack() {
        return inkUsageBlack;
    }

    private final float inkUsageBlack;

    public float getInkUsageYellow() {
        return inkUsageYellow;
    }

    private final float inkUsageYellow;

    public float getInkUsageCyan() {
        return inkUsageCyan;
    }

    private final float inkUsageCyan;

    public float getRapportLength() {
        return rapportLength;
    }

    private final float rapportLength;

    public ConsumptionChannel(int inkUsageMagenta, int inkUsageBlack, int inkUsageYellow, int inkUsageCyan, int rapportLength) {
        this.inkUsageMagenta = (float) (inkUsageMagenta/1000);
        this.inkUsageBlack = (float) (inkUsageBlack/1000);
        this.inkUsageYellow = (float) (inkUsageYellow/1000);
        this.inkUsageCyan = (float) (inkUsageCyan/1000);
        this.rapportLength = (float) (rapportLength/1000);
    }
}
