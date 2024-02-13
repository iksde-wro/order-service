package iksde.orderservice.port;

public interface TicketApiPort {
    boolean verify(long id);

    void cancel(long id);
}
