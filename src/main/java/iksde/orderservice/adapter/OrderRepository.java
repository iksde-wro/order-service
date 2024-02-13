package iksde.orderservice.adapter;

import iksde.orderservice.core.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE "
            + "(:accountId IS NULL OR o.accountId = :accountId) AND "
            + "(:paymentId IS NULL OR o.paymentId = :paymentId) AND "
            + "(:ticketId IS NULL OR o.ticketId = :ticketId)")
    Optional<Order> getByIds(@Param("accountId") Long accountId,
                             @Param("paymentId") Long paymentId,
                             @Param("ticketId") Long ticketId);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :newStatus "
            + "WHERE (:accountId IS NULL OR o.accountId = :accountId) "
            + "AND (:paymentId IS NULL OR o.paymentId = :paymentId) "
            + "AND (:ticketId IS NULL OR o.ticketId = :ticketId)")
    int updateStatus(@Param("accountId") Long accountId,
                     @Param("paymentId") Long paymentId,
                     @Param("ticketId") Long ticketId,
                     @Param("newStatus") Order.Status newStatus);
}
