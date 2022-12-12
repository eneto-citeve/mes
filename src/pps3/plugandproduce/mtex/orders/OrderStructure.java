package pps3.plugandproduce.mtex.orders;

public class OrderStructure {
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private String orderId;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private int quantity;

    public String getRipId() {
        return ripId;
    }

    public void setRipId(String ripId) {
        this.ripId = ripId;
    }

    private String ripId;

    public int getOverprint() {
        return overprint;
    }

    public void setOverprint(int overprint) {
        this.overprint = overprint;
    }

    private int overprint;

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    private int stepCount;

    public int getOvenTemperature() {
        return ovenTemperature;
    }

    public void setOvenTemperature(int ovenTemperature) {
        this.ovenTemperature = ovenTemperature;
    }

    private int ovenTemperature;

    @Override
    public String toString() {
        return getClass().getName() +
                "\nOrder ID - " + this.orderId +
                "\nQuantity - " + this.quantity +
                "\nRIP ID - " + this.ripId +
                "\nOverprint - " + this.overprint +
                "\nStep Count - " + this.stepCount +
                "\nOven Temperature - " + this.ovenTemperature;
    }
}
