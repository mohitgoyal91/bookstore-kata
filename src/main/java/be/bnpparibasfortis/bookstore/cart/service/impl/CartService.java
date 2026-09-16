package be.bnpparibasfortis.bookstore.cart.service.impl;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.exception.BookNotInStockException;
import be.bnpparibasfortis.bookstore.exception.InvalidQuantityException;
import be.bnpparibasfortis.bookstore.book.service.impl.BookService;
import be.bnpparibasfortis.bookstore.cart.repository.CartRepository;
import be.bnpparibasfortis.bookstore.cart.repository.entity.CartEntity;
import be.bnpparibasfortis.bookstore.cart.repository.entity.CartItemEntity;
import be.bnpparibasfortis.bookstore.cart.service.ICartService;
import be.bnpparibasfortis.bookstore.cart.service.models.Cart;
import be.bnpparibasfortis.bookstore.cart.service.models.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final BookService bookService;

    public CartService(CartRepository cartRepository, BookService bookService) {
        this.cartRepository = cartRepository;
        this.bookService = bookService;
    }

    @Override
    public Cart getOrCreateCart(UserEntity user) {
        return toCart(getOrCreateCartEntity(user));
    }

    @Transactional
    @Override
    public Cart updateCart(UserEntity user, CartItem cartItem) {
        CartEntity cartEntity = getOrCreateCartEntity(user);

        Optional<CartItemEntity> existingItem = findCartItem(cartEntity, cartItem.bookId());
        int updatedQuantity = calculateUpdatedQuantity(existingItem, cartItem.quantity());

        validateQuantity(updatedQuantity);
        validateStock(cartItem.bookId(), updatedQuantity);
        applyCartItemUpdate(cartEntity, existingItem, cartItem.bookId(), updatedQuantity);

        return toCart(cartEntity);
    }

    @Override
    public void clearCart(UUID id) {
        cartRepository.deleteById(id);
    }

    private Optional<CartItemEntity> findCartItem(CartEntity cartEntity, UUID bookId) {
        return cartEntity.getItems().stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst();
    }

    private int calculateUpdatedQuantity(
            Optional<CartItemEntity> existingItem,
            int quantity) {

        try {
            return Math.addExact(existingItem.map(CartItemEntity::getQuantity).orElse(0), quantity);
        } catch (ArithmeticException exception) {
            throw new InvalidQuantityException();
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new InvalidQuantityException();
        }
    }

    private void validateStock(UUID bookId, int quantity) {
        if (quantity > 0 && !isInStock(bookId, quantity)) {
            throw new BookNotInStockException();
        }
    }

    private void applyCartItemUpdate(
            CartEntity cartEntity,
            Optional<CartItemEntity> existingItem,
            UUID bookId,
            int updatedQuantity) {
        if (updatedQuantity == 0) {
            existingItem.ifPresent(cartEntity::removeItem);
            return;
        }

        existingItem.ifPresentOrElse(
                item -> item.setQuantity(updatedQuantity),
                () -> addNewItem(cartEntity, bookId, updatedQuantity)
        );
    }

    private void addNewItem(
            CartEntity cartEntity,
            UUID bookId,
            int quantity) {
        CartItemEntity item = new CartItemEntity(bookId, quantity);

        cartEntity.addItem(item);
    }

    private boolean isInStock(UUID bookId, int requestedQuantity) {
        return bookService.findBook(bookId).quantity() >= requestedQuantity;
    }

    private CartEntity getOrCreateCartEntity(UserEntity user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(new CartEntity(user.getId())));
    }

    private Cart toCart(CartEntity cartEntity) {
        return new Cart(
                cartEntity.getId(), cartEntity.getUserId(), toCartItems(cartEntity.getItems())
        );
    }

    private List<CartItem> toCartItems(List<CartItemEntity> items) {
        return items.stream()
                .map(this::toCartItem)
                .collect(Collectors.toList());
    }

    private CartItem toCartItem(CartItemEntity item) {
        return new CartItem(item.getBookId(), item.getQuantity());
    }
}
