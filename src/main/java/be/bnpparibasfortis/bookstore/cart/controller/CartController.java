package be.bnpparibasfortis.bookstore.cart.controller;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.cart.service.ICartService;
import be.bnpparibasfortis.bookstore.cart.service.models.Cart;
import be.bnpparibasfortis.bookstore.cart.service.models.CartItem;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/api/cart")
public class CartController {

    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserEntity user){
        return ResponseEntity.ok(cartService.getOrCreateCart(user));
    }

    @PostMapping("/books")
    public ResponseEntity<Cart> updateCart(@AuthenticationPrincipal UserEntity user, @Valid @RequestBody CartItem cartItem) {
        return ResponseEntity.ok(cartService.updateCart(user, cartItem));
    }
}
