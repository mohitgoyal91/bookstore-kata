package be.bnpparibasfortis.bookstore.cart.controller;

import be.bnpparibasfortis.bookstore.cart.repository.models.Cart;
import be.bnpparibasfortis.bookstore.cart.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> findCart(){
        return ResponseEntity.ok(null);
    }
}
