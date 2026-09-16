package be.bnpparibasfortis.bookstore.order.service.impl;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.exception.EmptyCartException;
import be.bnpparibasfortis.bookstore.book.service.IBookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import be.bnpparibasfortis.bookstore.cart.service.ICartService;
import be.bnpparibasfortis.bookstore.cart.service.models.Cart;
import be.bnpparibasfortis.bookstore.cart.service.models.CartItem;
import be.bnpparibasfortis.bookstore.order.repository.OrderRepository;
import be.bnpparibasfortis.bookstore.order.repository.entity.OrderEntity;
import be.bnpparibasfortis.bookstore.order.repository.entity.OrderItemEntity;
import be.bnpparibasfortis.bookstore.order.service.IOrderService;
import be.bnpparibasfortis.bookstore.order.service.models.Order;
import be.bnpparibasfortis.bookstore.order.service.models.OrderItem;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;

    private final ICartService cartService;
    private final IBookService bookService;

    public OrderService(OrderRepository orderRepository, ICartService cartService, IBookService bookService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.bookService = bookService;
    }

    @Override
    @Transactional
    public Order createOrder(UserEntity user) {
        Cart cart = cartService.getOrCreateCart(user);

        if(cart.items().isEmpty()) throw new EmptyCartException();

        cart.items().forEach(item ->
                bookService.decreaseStock(
                        item.bookId(),
                        item.quantity()
                )
        );

        List<OrderItemEntity> orderItems = cart.items()
                .stream()
                .map(this::toOrderItemEntity)
                .toList();

        OrderEntity orderEntity = new OrderEntity(user.getId());
        orderItems.forEach(orderEntity::addItem);

        OrderEntity savedOrder = orderRepository.save(orderEntity);
        cartService.clearCart(cart.id());

        return toOrder(savedOrder);
    }

    private Order toOrder(OrderEntity orderEntity) {
        return Order.builder()
                .id(orderEntity.getId())
                .totalPrice(orderEntity.getTotalPrice())
                .items(toOrderItems(orderEntity.getItems()))
                .build();
    }

    private List<OrderItem> toOrderItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(this::toOrderItem)
                .toList();
    }

    private OrderItem toOrderItem(OrderItemEntity item) {
        return OrderItem.builder()
                .id(item.getId())
                .bookId(item.getBookId())
                .title(item.getTitle())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private OrderItemEntity toOrderItemEntity(CartItem item) {
        Book book = bookService.findBook(item.bookId());
        return new OrderItemEntity(
                item.bookId(),
                book.title(),
                book.price(),
                item.quantity()
        );
    }
}
