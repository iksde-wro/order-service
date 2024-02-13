package iksde.orderservice.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "order_table")
@RequiredArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long accountId;
    private Long paymentId;
    private Long ticketId;
    private Status status;

    public enum Status {
        CANCELED,
        DELIVERED,
        UNPAID,
    }
}
