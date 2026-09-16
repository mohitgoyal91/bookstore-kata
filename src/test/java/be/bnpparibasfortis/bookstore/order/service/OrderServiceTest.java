package be.bnpparibasfortis.bookstore.order.service;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.exception.BookNotInStockException;
import be.bnpparibasfortis.bookstore.exception.EmptyCartException;
import be.bnpparibasfortis.bookstore.book.service.IBookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import be.bnpparibasfortis.bookstore.cart.service.ICartService;
import be.bnpparibasfortis.bookstore.cart.service.models.Cart;
import be.bnpparibasfortis.bookstore.cart.service.models.CartItem;
import be.bnpparibasfortis.bookstore.order.repository.OrderRepository;
import be.bnpparibasfortis.bookstore.order.repository.entity.OrderEntity;
import be.bnpparibasfortis.bookstore.order.service.impl.OrderService;
import be.bnpparibasfortis.bookstore.order.service.models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static be.bnpparibasfortis.bookstore.auth.Constants.*;
import static be.bnpparibasfortis.bookstore.auth.Constants.PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ICartService cartService;

    @Mock
    private IBookService bookService;

    OrderService orderService;

    protected UserEntity user;

    @BeforeEach
    public void setUp(){
        orderService = new OrderService(orderRepository, cartService, bookService);
        user = new UserEntity(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD);
        user.setId(UUID.randomUUID());
    }

    @Nested
    class createOrder {
        @Test
        void whenCartIsEmpty_shuoldThrowException(){
            when(cartService.getOrCreateCart(user)).thenReturn(new Cart(UUID.randomUUID(), user.getId(), List.of()));
            EmptyCartException exception = assertThrows(
                    EmptyCartException.class, () -> orderService.createOrder(user)
            );
            assertNotNull(exception);
        }

        @Test
        void whenBookNotInStock_shouldThrowException(){
            CartItem cartItem = new CartItem(UUID.randomUUID(), 2);
            when(cartService.getOrCreateCart(user)).thenReturn(new Cart(UUID.randomUUID(), user.getId(), List.of(cartItem)));
            doThrow(new BookNotInStockException())
                    .when(bookService)
                    .decreaseStock(any(), anyInt());

            BookNotInStockException exception = assertThrows(
                    BookNotInStockException.class, () -> orderService.createOrder(user)
            );
            assertNotNull(exception);
        }

        @Test
        void whenRequestIsOk_shouldCreateOrderAndReturn(){
            CartItem cartItem = new CartItem(UUID.randomUUID(), 2);
            when(cartService.getOrCreateCart(user)).thenReturn(new Cart(UUID.randomUUID(), user.getId(), List.of(cartItem)));
            when(bookService.findBook(any())).thenReturn(new Book(UUID.randomUUID(), "", "", BigDecimal.ONE, 2));
            when(orderRepository.save(any())).thenReturn(new OrderEntity(user.getId(), BigDecimal.valueOf(2)));
            doNothing().when(bookService).decreaseStock(any(), anyInt());
            doNothing().when(cartService).clearCart(any());

            Order actual = orderService.createOrder(user);

            verify(cartService).getOrCreateCart(any());
            verify(bookService).decreaseStock(any(), anyInt());
            verify(cartService).clearCart(any());
            assertEquals(BigDecimal.valueOf(2), actual.totalPrice());
        }
    }
}
