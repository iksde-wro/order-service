package iksde.orderservice.core;

import iksde.orderservice.core.model.Order;

public interface OrderApi {

    Order get(Long id);

    Order getByIds(Long accountId, Long paymentId, Long ticketId);

    Order save(Long accountId, Long paymentId, Long ticketId);

    Order cancel(Long accountId, Long paymentId, Long ticketId);

    Order cancel(Long id);
}
