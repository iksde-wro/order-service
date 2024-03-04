package iksde.orderservice.port;

import iksde.orderservice.core.model.Order;

public interface OrderPort {
    Order placeOrder(Long accountId, Long paymentId, Long ticketId);

    Order getOrder(Long id);

    Order cancelOrder(Long id);
}
