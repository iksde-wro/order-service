package iksde.orderservice.adapter;

import iksde.orderservice.port.PaymentApiPort;
import org.springframework.stereotype.Service;

@Service
public class PaymentApi implements PaymentApiPort {

    @Override
    public boolean verify(long id) {
        return false;
    }

    @Override
    public void cancel(long id) {
    }
}
