package be.bnpparibasfortis.bookstore.order.service;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.order.service.models.Order;

public interface IOrderService {
    Order createOrder(UserEntity user);
}
