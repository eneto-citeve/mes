package controllers;

public record OrderStatus(StatusOfOrder status, String orderId, StateOfOrder state) {
}
