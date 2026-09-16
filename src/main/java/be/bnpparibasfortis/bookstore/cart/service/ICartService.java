package be.bnpparibasfortis.bookstore.cart.service;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.cart.service.models.Cart;
import be.bnpparibasfortis.bookstore.cart.service.models.CartItem;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ICartService {
    Cart getOrCreateCart(UserEntity user);
    Cart updateCart(UserEntity user, @Valid CartItem cartItem);
    void clearCart(UUID id);
}
