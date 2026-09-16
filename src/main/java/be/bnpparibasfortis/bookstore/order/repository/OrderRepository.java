package be.bnpparibasfortis.bookstore.order.repository;

import be.bnpparibasfortis.bookstore.order.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
