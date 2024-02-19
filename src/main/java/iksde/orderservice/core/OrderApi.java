package iksde.orderservice.core;

import iksde.orderservice.core.model.Order;

public interface OrderApi {

    Order get(Long id);

    Order getByIds(Long accountId, Long paymentId, Long tickerId);

    Order save(Long accountId, Long paymentId, Long tickerId);

    Order cancel(Long accountId, Long paymentId, Long tickerId);
}
