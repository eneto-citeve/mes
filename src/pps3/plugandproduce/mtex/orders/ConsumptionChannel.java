package pps3.plugandproduce.mtex.orders;

public class ConsumptionChannel {
    public float getInkUsageMagentaReal() {
        return inkUsageMagentaReal;
    }

    private final float inkUsageMagentaReal;

    public float getInkUsageBlackReal() {
        return inkUsageBlackReal;
    }

    private final float inkUsageBlackReal;

    public float getInkUsageYellowReal() {
        return inkUsageYellowReal;
    }

    private final float inkUsageYellowReal;

    public float getInkUsageCyanReal() {
        return inkUsageCyanReal;
    }

    private final float inkUsageCyanReal;

    public float getInkUsageWReal() {
        return inkUsageWReal;
    }

    private final float inkUsageWReal;

    public float getInkUsageXReal() {
        return inkUsageXReal;
    }

    private final float inkUsageXReal;

    public float getInkUsageYReal() {
        return inkUsageYReal;
    }

    private final float inkUsageYReal;

    public float getInkUsageZReal() {
        return inkUsageZReal;
    }

    private final float inkUsageZReal;

    public float getInkUsageMagentaTheorical() {
        return inkUsageMagentaTheorical;
    }

    private final float inkUsageMagentaTheorical;

    public float getInkUsageBlackTheorical() {
        return inkUsageBlackTheorical;
    }

    private final float inkUsageBlackTheorical;

    public float getInkUsageYellowTheorical() {
        return inkUsageYellowTheorical;
    }

    private final float inkUsageYellowTheorical;

    public float getInkUsageCyanTheorical() {
        return inkUsageCyanReal;
    }

    private final float inkUsageCyanTheorical;

    public float getInkUsageWTheorical() {
        return inkUsageWTheorical;
    }

    private final float inkUsageWTheorical;

    public float getInkUsageXTheorical() {
        return inkUsageXTheorical;
    }

    private final float inkUsageXTheorical;

    public float getInkUsageYTheorical() {
        return inkUsageYTheorical;
    }

    private final float inkUsageYTheorical;

    public float getInkUsageZTheorical() {
        return inkUsageZTheorical;
    }

    private final float inkUsageZTheorical;

    public float getRapportLength() {
        return rapportLength;
    }

    private final float rapportLength;

    public float getEnergyConsumption() {
        return energyConsumption;
    }

    private final float energyConsumption;

    public ConsumptionChannel(Object[] array) {
        if (array.length < 18) {
            throw new IllegalArgumentException("Array must have at least 18 elements");
        }

        this.inkUsageMagentaReal = (float) ((int) array[1] /1000);
        this.inkUsageBlackReal = (float) ((int) array[2] /1000);
        this.inkUsageYellowReal = (float) ((int) array[3] /1000);
        this.inkUsageCyanReal = (float) ((int) array[4] /1000);
        this.inkUsageWReal = (float) ((int) array[5] /1000);
        this.inkUsageXReal = (float) ((int) array[6] /1000);
        this.inkUsageYReal = (float) ((int) array[7] /1000);
        this.inkUsageZReal = (float) ((int) array[8] /1000);
        this.inkUsageMagentaTheorical = (float) ((int) array[11] /1000);
        this.inkUsageBlackTheorical = (float) ((int) array[12] /1000);
        this.inkUsageYellowTheorical = (float) ((int) array[13] /1000);
        this.inkUsageCyanTheorical = (float) ((int) array[14] /1000);
        this.inkUsageWTheorical = (float) ((int) array[15] /1000);
        this.inkUsageXTheorical = (float) ((int) array[16] /1000);
        this.inkUsageYTheorical = (float) ((int) array[17] /1000);
        this.inkUsageZTheorical = (float) ((int) array[18] /1000);
        this.rapportLength = (float) ((int) array[10] /1000);
        this.energyConsumption = (float) ((int) array[9] /1000);
    }
}
