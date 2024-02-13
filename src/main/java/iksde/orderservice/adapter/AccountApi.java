package iksde.orderservice.adapter;

import iksde.orderservice.port.AccountApiPort;
import org.springframework.stereotype.Service;

@Service
public class AccountApi implements AccountApiPort {

    @Override
    public boolean verify(long id) {
        return false;
    }
}
