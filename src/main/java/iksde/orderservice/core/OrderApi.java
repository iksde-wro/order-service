package iksde.orderservice.core;

import iksde.orderservice.core.model.Order;

public interface OrderApi {
    Order save(Long accountId, Long paymentId, Long tickerId);

    Order cancel(Long accountId, Long paymentId, Long tickerId);
}
