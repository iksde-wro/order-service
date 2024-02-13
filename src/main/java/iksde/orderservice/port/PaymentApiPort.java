package iksde.orderservice.port;

public interface PaymentApiPort {
    boolean verify(long id);

    void cancel(long id);
}
