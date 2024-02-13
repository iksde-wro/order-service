package iksde.orderservice.adapter;

import iksde.orderservice.core.model.Order;
import iksde.orderservice.port.OrderPort;

public class OrderApi implements OrderPort {
    public Order placeOrder(Order order) {
        return order;
    }
}
