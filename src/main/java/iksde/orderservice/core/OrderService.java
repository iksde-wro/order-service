package iksde.orderservice.core;

import iksde.orderservice.adapter.AccountApi;
import iksde.orderservice.adapter.OrderRepository;
import iksde.orderservice.adapter.PaymentApi;
import iksde.orderservice.adapter.TicketApi;
import iksde.orderservice.core.exception.OrderCancellationException;
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
    public Order cancel(Long accountId, Long paymentId, Long ticketId) {
        if (orderRepo.getByIds(accountId, paymentId, ticketId).isPresent()) {
            try {
                paymentApi.cancel(paymentId);
                ticketApi.cancel(ticketId);
                orderRepo.updateStatus(accountId, paymentId, ticketId, Order.Status.CANCELED);
            } catch (Exception e) {
                throw new OrderCancellationException("Failed to cancel order. Reason: " + e.getMessage());
            }
            return orderRepo.getByIds(accountId, paymentId, ticketId).orElseThrow(() -> new OrderCancellationException("Failed to retrieve canceled order details."));
        } else {
            throw new OrderCancellationException("Order not found");
        }
    }
}
