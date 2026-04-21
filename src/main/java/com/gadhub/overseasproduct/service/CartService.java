package com.gadhub.overseasproduct.service;

import com.gadhub.overseasproduct.dto.AddToCartDTO;
import com.gadhub.overseasproduct.vo.CartVO;

import java.util.List;

public interface CartService {
    void addToCart(AddToCartDTO addToCartDTO, Long userId);

    List<CartVO> getCartList(Long userId);

    void deleteCart(Long cartId, Long userId);

    void updateCart(Integer quantity, Long cartId , Long userId);
}
