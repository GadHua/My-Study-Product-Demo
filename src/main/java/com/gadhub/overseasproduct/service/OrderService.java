package com.gadhub.overseasproduct.service;

import com.gadhub.overseasproduct.dto.OrderDto;

public interface OrderService {
    void addOrder(OrderDto orderDto);
    void updateOrder(OrderDto orderDto);
    void deleteOrder(Long id);
    OrderDto getOrderById(Long id);

}
