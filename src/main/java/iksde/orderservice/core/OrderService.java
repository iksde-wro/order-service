package iksde.orderservice.core;

import iksde.orderservice.core.model.Order;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements OrderApi {

    @Override
    public Order save(Long accountId, Long paymentId, Long tickerId) {
        return null;
    }

    @SneakyThrows
    public Order cancel(Long accountId, Long paymentId, Long ticketId) {
        return null;
    }
}
