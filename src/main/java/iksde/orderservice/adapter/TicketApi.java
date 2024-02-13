package iksde.orderservice.adapter;

import iksde.orderservice.port.TicketApiPort;
import org.springframework.stereotype.Service;

@Service
public class TicketApi implements TicketApiPort {

    @Override
    public boolean verify(long id) {
        return false;
    }

    @Override
    public void cancel(long id) {
    }
}
