package be.bnpparibasfortis.bookstore.order.controller;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.order.service.IOrderService;
import be.bnpparibasfortis.bookstore.order.service.models.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@AuthenticationPrincipal UserEntity user) {

        return ResponseEntity.ok(orderService.createOrder(user));
    }
}
