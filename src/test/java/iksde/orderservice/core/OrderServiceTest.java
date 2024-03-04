package iksde.orderservice.core;

import iksde.orderservice.adapter.AccountApi;
import iksde.orderservice.adapter.OrderRepository;
import iksde.orderservice.adapter.PaymentApi;
import iksde.orderservice.adapter.TicketApi;
import iksde.orderservice.core.exception.OrderNotFoundException;
import iksde.orderservice.core.exception.TicketNotFoundException;
import iksde.orderservice.core.model.Order;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceTest {
    @Mock
    TicketApi ticketApi;
    @Mock
    AccountApi accountApi;
    @Mock
    PaymentApi paymentApi;

    @Autowired
    OrderRepository orderRepository;
    private static final long ACCOUNT_ID = 1L;
    private static final long PAYMENT_ID = 2L;
    private static final long TICKET_ID = 3L;

    @Test
    @org.junit.jupiter.api.Order(1)
    void placeOrderFacadeTest() {
        OrderService orderService = setUp();

        Mockito
                .when(accountApi.verify(ACCOUNT_ID))
                .thenReturn(true);
        Mockito
                .when(paymentApi.verify(PAYMENT_ID))
                .thenReturn(true);
        Mockito
                .when(ticketApi.verify(TICKET_ID))
                .thenReturn(true);

        Order order = orderService.save(ACCOUNT_ID, PAYMENT_ID, TICKET_ID);

        Assertions.assertEquals(Order.Status.DELIVERED, order.getStatus());
        Assertions.assertEquals(ACCOUNT_ID, order.getAccountId());
        Assertions.assertEquals(PAYMENT_ID, order.getPaymentId());
        Assertions.assertEquals(TICKET_ID, order.getTicketId());
    }

    private OrderService setUp() {
        return new OrderService(accountApi, paymentApi, ticketApi, orderRepository);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void placeOrderRepositoryTest() {
        Assertions.assertEquals(Order.Status.DELIVERED, orderRepository.getByIds(ACCOUNT_ID, PAYMENT_ID, TICKET_ID).get().getStatus());
        orderRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @org.junit.jupiter.api.Order(3)
    void cancelOrderTest() {
        OrderService orderService = setUp();

        Mockito
                .when(accountApi.verify(ACCOUNT_ID))
                .thenReturn(true);
        Mockito
                .when(paymentApi.verify(PAYMENT_ID))
                .thenReturn(true);
        Mockito
                .when(ticketApi.verify(TICKET_ID))
                .thenReturn(true);

        orderService.save(ACCOUNT_ID, PAYMENT_ID, TICKET_ID);
        Order order = orderService.cancel(ACCOUNT_ID, PAYMENT_ID, TICKET_ID);

        Assertions.assertEquals(Order.Status.CANCELED, order.getStatus());
        Assertions.assertEquals(ACCOUNT_ID, order.getAccountId());
        Assertions.assertEquals(PAYMENT_ID, order.getPaymentId());
        Assertions.assertEquals(TICKET_ID, order.getTicketId());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void orderCancelRepositoryTest() {
        Assertions.assertEquals(Order.Status.CANCELED, orderRepository.getByIds(ACCOUNT_ID, PAYMENT_ID, TICKET_ID).get().getStatus());
        orderRepository.deleteAll();
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void placeOrderWithoutPaymentTest() {
        OrderService orderService = setUp();

        Mockito
                .when(accountApi.verify(ACCOUNT_ID))
                .thenReturn(true);
        Mockito
                .when(paymentApi.verify(PAYMENT_ID))
                .thenReturn(false);
        Mockito
                .when(ticketApi.verify(TICKET_ID))
                .thenReturn(true);

        Order order = orderService.save(ACCOUNT_ID, PAYMENT_ID, TICKET_ID);

        Assertions.assertEquals(Order.Status.UNPAID, order.getStatus());
        Assertions.assertEquals(ACCOUNT_ID, order.getAccountId());
        Assertions.assertNull(order.getPaymentId());
        Assertions.assertEquals(TICKET_ID, order.getTicketId());
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void placeOrderWithoutPaymentRepositoryTest() {
        Assertions.assertEquals(Order.Status.UNPAID, orderRepository.getByIds(ACCOUNT_ID, null, TICKET_ID).get().getStatus());
        orderRepository.deleteAll();
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void placeOrderWithoutTicketTest() {
        OrderService orderService = setUp();

        Mockito
                .when(accountApi.verify(ACCOUNT_ID))
                .thenReturn(true);
        Mockito
                .when(paymentApi.verify(PAYMENT_ID))
                .thenReturn(true);
        Mockito
                .when(ticketApi.verify(TICKET_ID))
                .thenReturn(false);

        Assertions.assertThrows(TicketNotFoundException.class, () -> orderService.save(ACCOUNT_ID, PAYMENT_ID, TICKET_ID));
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void placeOrderWithoutTicketRepositoryTest() {
        Assertions.assertTrue(orderRepository.getByIds(ACCOUNT_ID, PAYMENT_ID, null).isEmpty());
        orderRepository.deleteAll();
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void placeOrderWithoutAccountTest() {
        OrderService orderService = setUp();

        Mockito
                .when(accountApi.verify(ACCOUNT_ID))
                .thenReturn(false);
        Mockito
                .when(paymentApi.verify(PAYMENT_ID))
                .thenReturn(true);
        Mockito
                .when(ticketApi.verify(TICKET_ID))
                .thenReturn(true);

        Order order = orderService.save(ACCOUNT_ID, PAYMENT_ID, TICKET_ID);

        Assertions.assertEquals(Order.Status.DELIVERED, order.getStatus());
        Assertions.assertNull(order.getAccountId());
        Assertions.assertEquals(PAYMENT_ID, order.getPaymentId());
        Assertions.assertEquals(TICKET_ID, order.getTicketId());
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void placeOrderWithoutAccountRepositoryTest() {
        Assertions.assertEquals(Order.Status.DELIVERED, orderRepository.getByIds(null, PAYMENT_ID, TICKET_ID).get().getStatus());
        orderRepository.deleteAll();
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void placeOrderWithoutAccountAndPaymentTest() {
        OrderService orderService = setUp();

        Mockito
                .when(accountApi.verify(ACCOUNT_ID))
                .thenReturn(false);
        Mockito
                .when(paymentApi.verify(PAYMENT_ID))
                .thenReturn(false);
        Mockito
                .when(ticketApi.verify(TICKET_ID))
                .thenReturn(true);

        Order order = orderService.save(ACCOUNT_ID, PAYMENT_ID, TICKET_ID);

        Assertions.assertEquals(Order.Status.UNPAID, order.getStatus());
        Assertions.assertNull(order.getAccountId());
        Assertions.assertNull(order.getPaymentId());
        Assertions.assertEquals(TICKET_ID, order.getTicketId());
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    void placeOrderWithoutAccountAndPaymentRepositoryTest() {
        Assertions.assertEquals(Order.Status.UNPAID, orderRepository.getByIds(null, null, TICKET_ID).get().getStatus());
        orderRepository.deleteAll();
    }

    @Test
    @org.junit.jupiter.api.Order(13)
    void placeOrderWithoutAccountAndPaymentAndTicketTest() {
        OrderService orderService = setUp();

        Mockito
                .when(accountApi.verify(ACCOUNT_ID))
                .thenReturn(false);
        Mockito
                .when(paymentApi.verify(PAYMENT_ID))
                .thenReturn(false);
        Mockito
                .when(ticketApi.verify(TICKET_ID))
                .thenReturn(false);

        Assertions.assertThrows(TicketNotFoundException.class, () -> orderService.save(ACCOUNT_ID, PAYMENT_ID, TICKET_ID));
    }

    @Test
    @org.junit.jupiter.api.Order(14)
    void placeOrderWithoutAccountAndPaymentAndTicketRepositoryTest() {
        Assertions.assertTrue(orderRepository.getByIds(null, null, null).isEmpty());
        orderRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @org.junit.jupiter.api.Order(15)
    void cancelOrderWithoutOrderTest() {
        OrderService orderService = setUp();

        Mockito
                .when(accountApi.verify(ACCOUNT_ID))
                .thenReturn(false);
        Mockito
                .when(paymentApi.verify(PAYMENT_ID))
                .thenReturn(false);
        Mockito
                .when(ticketApi.verify(TICKET_ID))
                .thenReturn(false);

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.cancel(ACCOUNT_ID, PAYMENT_ID, TICKET_ID));
    }

    @SneakyThrows
    @Test
    @org.junit.jupiter.api.Order(16)
    void getOrderTest() {
        OrderService orderService = setUp();
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.get(1L));
    }

    @SneakyThrows
    @Test
    @org.junit.jupiter.api.Order(17)
    void getOrderWhenOrderIsAbsentTest() {
        OrderService orderService = setUp();
        Order orderExpected = new Order(1L, ACCOUNT_ID, PAYMENT_ID, TICKET_ID, Order.Status.DELIVERED);
        orderRepository.save(orderExpected);

        Order orderActual = orderService.get(orderExpected.getOrderId());

        Assertions.assertEquals(orderExpected.getOrderId(), orderActual.getOrderId());
        Assertions.assertEquals(orderExpected.getAccountId(), orderActual.getAccountId());
        Assertions.assertEquals(orderExpected.getPaymentId(), orderActual.getPaymentId());
        Assertions.assertEquals(orderExpected.getTicketId(), orderActual.getTicketId());
        Assertions.assertEquals(orderExpected.getStatus(), orderActual.getStatus());

        orderRepository.deleteAll();
    }
}
