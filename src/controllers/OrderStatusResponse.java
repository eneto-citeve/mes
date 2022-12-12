package controllers;

public class OrderStatusResponse {
    public String getOrderId() {
        return orderId;
    }

    private final String orderId;

    public int getState() {
        return state;
    }

    private final int state;

    public OrderStatusResponse(String orderId, int state) {
        this.orderId = orderId;
        this.state = state;
    }
}
