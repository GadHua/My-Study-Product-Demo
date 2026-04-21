package com.gadhub.overseasproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gadhub.overseasproduct.common.constant.ErrorCode;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.dto.AddToCartDTO;
import com.gadhub.overseasproduct.entity.Cart;
import com.gadhub.overseasproduct.entity.Product;
import com.gadhub.overseasproduct.mapper.CartMapper;
import com.gadhub.overseasproduct.mapper.ProductMapper;
import com.gadhub.overseasproduct.service.CartService;
import com.gadhub.overseasproduct.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Override
    public void deleteCart(Long cartId, Long userId) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId);
        Cart cart = cartMapper.selectOne(queryWrapper);

        if (cart == null){
            throw new BusinessException(ErrorCode.CART_NOT_FOUND);
        }

        cartMapper.delete(queryWrapper);
    }

    @Override
    public void updateCart(Integer quantity, Long cartId,Long userId) {
        if (quantity <= 0){
            throw new BusinessException(ErrorCode.CART_QUANTITY_INVALID);
        }

        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId);

        Cart cart = cartMapper.selectOne(queryWrapper);

        if (cart == null){
            throw new BusinessException(ErrorCode.CART_NOT_FOUND);
        }

        LambdaUpdateWrapper<Cart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Cart::getId, cartId)
                .set(Cart::getQuantity, quantity);
        cartMapper.update(null, updateWrapper);
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        LambdaQueryWrapper<Cart> cartQueryWrapper = new LambdaQueryWrapper<>();
        cartQueryWrapper.eq(Cart::getUserId, userId);

//        商品列表
        List<Cart> cartList = cartMapper.selectList(cartQueryWrapper);

        if (cartList.isEmpty()){
            return List.of();
        }

        List<CartVO> cartVOList = new ArrayList<>();
//       遍历cartList
        for (Cart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            CartVO cartVO = new CartVO();
            cartVO.setCartId(cart.getId());
            cartVO.setProductId(product.getId());
            cartVO.setProductName(product.getName());
            cartVO.setPrice(product.getPrice());
            cartVO.setQuantity(cart.getQuantity());
            cartVO.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            cartVOList.add(cartVO);
        }


        return cartVOList;
    }

    @Override
    public void addToCart(AddToCartDTO addToCartDTO, Long userId) {
        if (userId == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        Product product = productMapper.selectById(addToCartDTO.getProductId());
        if (product == null) {  // 判断商品是否存在
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (!(addToCartDTO.getQuantity()>0)){
            throw new BusinessException(ErrorCode.CART_QUANTITY_INVALID);
        }

        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Cart::getUserId,userId)
                    .eq(Cart::getProductId,addToCartDTO.getProductId());

        Cart duplicateProducts = cartMapper.selectOne(queryWrapper);

        if (duplicateProducts != null){
            LambdaUpdateWrapper<Cart> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Cart::getId, duplicateProducts.getId())
                    .setSql("quantity = quantity + " + addToCartDTO.getQuantity());
            cartMapper.update(null, updateWrapper);
        }else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(addToCartDTO.getProductId());
            cart.setQuantity(addToCartDTO.getQuantity());
            cartMapper.insert(cart);
        }


    }
}
