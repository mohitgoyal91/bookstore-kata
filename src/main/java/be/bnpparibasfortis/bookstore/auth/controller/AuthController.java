package be.bnpparibasfortis.bookstore.auth.controller;

import be.bnpparibasfortis.bookstore.auth.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final IAuthService authService;
}
