package iksde.orderservice.core;

import iksde.orderservice.adapter.AccountApi;
import iksde.orderservice.adapter.OrderRepository;
import iksde.orderservice.adapter.PaymentApi;
import iksde.orderservice.adapter.TicketApi;
import iksde.orderservice.core.exception.OrderNotFoundException;
import iksde.orderservice.core.exception.TicketNotFoundException;
import iksde.orderservice.core.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderApi {
    private final AccountApi accountApi;
    private final PaymentApi paymentApi;
    private final TicketApi ticketApi;
    private final OrderRepository orderRepo;

    @SneakyThrows
    @Override
    public Order get(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new OrderNotFoundException(String.format("Order with ID %d not found", id)));
    }

    @SneakyThrows
    @Override
    public Order getByIds(Long accountId, Long paymentId, Long ticketId) {
        return orderRepo.getByIds(accountId, paymentId, ticketId).orElseThrow(() -> new OrderNotFoundException(String.format("Order with ID %d not found", accountId)));
    }

    @SneakyThrows
    @Override
    public Order save(Long accountId, Long paymentId, Long tickerId) {
        return orderRepo.save(createOrder(accountId, paymentId, tickerId));
    }

    public Order createOrder(Long accountId, Long paymentId, Long ticketId) throws TicketNotFoundException {
        boolean isAccount = verifyAccount(accountId);
        boolean isPayment = verifyPayment(paymentId);
        verifyTicket(ticketId);

        return buildOrder(accountId, paymentId, ticketId, isAccount, isPayment);
    }

    private boolean verifyAccount(Long accountId) {
        return accountApi.verify(accountId);
    }

    private boolean verifyPayment(Long paymentId) {
        return paymentApi.verify(paymentId);
    }

    private boolean verifyTicket(Long ticketId) throws TicketNotFoundException {
        boolean isTicket = ticketApi.verify(ticketId);
        if (!isTicket) {
            throw new TicketNotFoundException(String.format("Ticket with ID %d not found", ticketId));
        }
        return true;
    }

    private Order buildOrder(Long accountId, Long paymentId, Long ticketId, boolean isAccount, boolean isPayment) {
        return Order.builder()
                .ticketId(ticketId)
                .accountId(isAccount ? accountId : null)
                .paymentId(isPayment ? paymentId : null)
                .status(isPayment ? Order.Status.DELIVERED : Order.Status.UNPAID)
                .build();
    }

    @Override
    @SneakyThrows
    public Order cancel(Long id) {
        return orderRepo.findById(id)
                .map(order -> {
                    paymentApi.cancel(order.getPaymentId());
                    ticketApi.cancel(order.getPaymentId());
                    return orderRepo.save(new Order(order.getOrderId(), order.getAccountId(), order.getPaymentId(), order.getTicketId(), Order.Status.CANCELED));
                })
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    @Override
    @SneakyThrows
    public Order cancel(Long accountId, Long paymentId, Long ticketId) {
        return orderRepo.getByIds(accountId, paymentId, ticketId)
                .map(order -> {
                    paymentApi.cancel(paymentId);
                    ticketApi.cancel(ticketId);
                    return orderRepo.save(new Order(order.getOrderId(), order.getAccountId(), order.getPaymentId(), order.getTicketId(), Order.Status.CANCELED));
                })
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }
}
