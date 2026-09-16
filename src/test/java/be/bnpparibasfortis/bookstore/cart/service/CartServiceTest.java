package be.bnpparibasfortis.bookstore.cart.service;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.exception.BookNotInStockException;
import be.bnpparibasfortis.bookstore.exception.InvalidQuantityException;
import be.bnpparibasfortis.bookstore.book.service.impl.BookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import be.bnpparibasfortis.bookstore.cart.repository.CartRepository;
import be.bnpparibasfortis.bookstore.cart.repository.entity.CartEntity;
import be.bnpparibasfortis.bookstore.cart.repository.entity.CartItemEntity;
import be.bnpparibasfortis.bookstore.cart.service.impl.CartService;
import be.bnpparibasfortis.bookstore.cart.service.models.Cart;
import be.bnpparibasfortis.bookstore.cart.service.models.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static be.bnpparibasfortis.bookstore.auth.Constants.*;
import static be.bnpparibasfortis.bookstore.auth.Constants.PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BookService bookService;

    private CartService cartService;

    protected UserEntity user;

    @BeforeEach
    public void setUp(){
        cartService = new CartService(cartRepository, bookService);
        user = new UserEntity(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD);
        user.setId(UUID.randomUUID());
    }

    @Nested
    class getOrCreateCart{

        @Test
        void whenNoCartExists_shouldCreateCartAndReturn() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.empty());
            when(cartRepository.save(any()))
                    .thenReturn(new CartEntity(user.getId()));
            Cart actual = cartService.getOrCreateCart(user);

            verify(cartRepository).findByUserId(user.getId());
            verify(cartRepository).save(any());
            assertEquals(user.getId(), actual.userId());
        }

        @Test
        void whenCartExists_shouldReturn() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(new CartEntity(user.getId())));
            Cart actual = cartService.getOrCreateCart(user);
            verify(cartRepository).findByUserId(user.getId());
            verify(cartRepository, never()).save(any());
            assertEquals(user.getId(), actual.userId());
        }
    }

    @Nested
    class updateCart {
        @Test
        void overflowingAdjustmentPreservesCartAndDoesNotCheckStock() {
            UUID bookId = UUID.randomUUID();
            CartItemEntity item = new CartItemEntity(bookId, Integer.MAX_VALUE);
            CartEntity cart = new CartEntity(user.getId(), new ArrayList<>(List.of(item)));
            when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
            assertThrows(InvalidQuantityException.class,
                    () -> cartService.updateCart(user, new CartItem(bookId, 1)));
            assertEquals(Integer.MAX_VALUE, item.getQuantity());
            verifyNoInteractions(bookService);
        }

        @Test
        void whenNoCartExists_shouldCreateCartAndReturn() {
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.empty());
            when(cartRepository.save(any()))
                    .thenReturn(new CartEntity(user.getId()));
            UUID bookId = UUID.randomUUID();
            when(bookService.findBook(bookId)).thenReturn(new Book(bookId, "", "", BigDecimal.ONE, 1));
            Cart actual = cartService.updateCart(user, new CartItem(bookId, 1));

            verify(cartRepository).findByUserId(user.getId());
            verify(cartRepository).save(any());
            assertEquals(actual.userId(), actual.userId());
        }

        @Test
        void whenCartExistsAndBookIsAdded_shouldAddAndReturn(){
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(new CartEntity(user.getId())));
            UUID bookId = UUID.randomUUID();
            when(bookService.findBook(bookId)).thenReturn(new Book(bookId, "", "", BigDecimal.ONE, 1));
            Cart actual = cartService.updateCart(user, new CartItem(bookId, 1));

            verify(cartRepository, never()).save(any());
            verify(cartRepository).findByUserId(user.getId());

            assertEquals(1, actual.items().size());
            assertEquals(actual.items().get(0).bookId(), bookId);
            assertEquals(1, actual.items().get(0).quantity());
        }

        @Test
        void whenItemExistsInCartAndRequestedQuantityIsPositive_shouldUpdateAndReturn(){
            UUID bookId = UUID.randomUUID();
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(new CartEntity(user.getId(), new ArrayList<>(List.of(new CartItemEntity(bookId, 1))))));

            when(bookService.findBook(bookId)).thenReturn(new Book(bookId, "", "", BigDecimal.ONE, 2));
            Cart actual = cartService.updateCart(user, new CartItem(bookId, 1));
            verify(cartRepository, never()).save(any());
            verify(cartRepository).findByUserId(user.getId());

            assertEquals(actual.items().size(), 1);
            assertEquals(actual.items().get(0).bookId(), bookId);
            assertEquals(actual.items().get(0).quantity(), 2);
        }

        @Test
        void whenItemExistsInCartAndRequestedQuantityIsNegative_shouldUpdateAndReturn(){
            UUID bookId = UUID.randomUUID();
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(new CartEntity(user.getId(), new ArrayList<>(List.of(new CartItemEntity(bookId, 2))))));

            when(bookService.findBook(bookId)).thenReturn(new Book(bookId, "", "", BigDecimal.ONE, 2));
            Cart actual = cartService.updateCart(user, new CartItem(bookId, -1));
            verify(cartRepository, never()).save(any());
            verify(cartRepository).findByUserId(user.getId());

            assertEquals(actual.items().size(), 1);
            assertEquals(actual.items().get(0).bookId(), bookId);
            assertEquals(actual.items().get(0).quantity(), 1);
        }

        @Test
        void whenItemExistsInCartAndUpdatedQuantityIsZero_shouldRemoveAndReturn(){
            UUID bookId = UUID.randomUUID();
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(new CartEntity(user.getId(), new ArrayList<>(List.of(new CartItemEntity(bookId, 2))))));

            when(bookService.findBook(bookId)).thenReturn(new Book(bookId, "", "", BigDecimal.ONE, 2));
            Cart actual = cartService.updateCart(user, new CartItem(bookId, 0));
            verify(cartRepository, never()).save(any());
            verify(cartRepository).findByUserId(user.getId());

            assertEquals(actual.items().size(), 1);
            assertEquals(actual.items().get(0).bookId(), bookId);
            assertEquals(actual.items().get(0).quantity(), 2);
        }

        @Test
        void whenItemExistsInCartAndResultingQuantityIsNegative_shouldThrowException(){
            UUID bookId = UUID.randomUUID();
            CartEntity cartEntity = new CartEntity(user.getId(), new ArrayList<>(List.of(new CartItemEntity(bookId, 2))));
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cartEntity));

            InvalidQuantityException exception = assertThrows(
                    InvalidQuantityException.class, () -> cartService.updateCart(user, new CartItem(bookId, -3))
            );
            verify(cartRepository, never()).save(any());
            verify(bookService, never()).findBook(any());
            verify(cartRepository).findByUserId(user.getId());

            assertNotNull(exception);
            assertEquals(2, cartEntity.getItems().get(0).getQuantity());
        }

        @Test
        void whenResultingQuantityExceedsStock_shouldThrowException() {
            UUID bookId = UUID.randomUUID();
            CartEntity cartEntity = new CartEntity(user.getId(), new ArrayList<>(List.of(new CartItemEntity(bookId, 2))));
            when(bookService.findBook(bookId)).thenReturn(new Book(bookId, "", "", BigDecimal.ONE, 2));
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(cartEntity));

            BookNotInStockException exception = assertThrows(
                    BookNotInStockException.class, () -> cartService.updateCart(user, new CartItem(bookId, 1))
            );
            verify(cartRepository, never()).save(any());
            verify(cartRepository).findByUserId(user.getId());

            assertNotNull(exception);
            assertEquals(2, cartEntity.getItems().get(0).getQuantity());
        }

        @Test
        void whenResultingQuantityEqualsStock_shouldSucceedAndReturn() {
            UUID bookId = UUID.randomUUID();
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(new CartEntity(user.getId(), new ArrayList<>(List.of(new CartItemEntity(bookId, 2))))));

            when(bookService.findBook(bookId)).thenReturn(new Book(bookId, "", "", BigDecimal.ONE, 3));
            Cart actual = cartService.updateCart(user, new CartItem(bookId, 1));
            verify(cartRepository, never()).save(any());
            verify(cartRepository).findByUserId(user.getId());

            assertEquals(actual.items().size(), 1);
            assertEquals(actual.items().get(0).bookId(), bookId);
            assertEquals(actual.items().get(0).quantity(), 3);
        }

        @Test
        void whenRemovingItem_shouldSucceedAndNotRetrieveBook() {
            UUID bookId = UUID.randomUUID();
            when(cartRepository.findByUserId(user.getId()))
                    .thenReturn(Optional.of(new CartEntity(user.getId(), new ArrayList<>(List.of(new CartItemEntity(bookId, 2))))));

            Cart actual = cartService.updateCart(user, new CartItem(bookId, -2));
            verify(cartRepository, never()).save(any());
            verify(cartRepository).findByUserId(user.getId());

            assertEquals(actual.items().size(), 0);
        }
    }
}
