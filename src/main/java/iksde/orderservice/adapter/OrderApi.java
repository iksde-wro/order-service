package iksde.orderservice.adapter;

import iksde.orderservice.core.OrderService;
import iksde.orderservice.core.model.Order;
import iksde.orderservice.port.OrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class OrderApi implements OrderPort {

    private final OrderService orderService;

    @QueryMapping
    @Override
    public Order placeOrder(@Argument Long accountId, @Argument Long paymentId, @Argument Long ticketId) {
        return orderService.save(accountId, paymentId, ticketId);
    }

    @QueryMapping
    @Override
    public Order getOrder(@Argument Long id) {
        return orderService.get(id);
    }

    @QueryMapping
    @Override
    public Order cancelOrder(@Argument Long id) {
        return orderService.cancel(id);
    }
}
