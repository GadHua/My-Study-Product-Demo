package com.gadhub.overseasproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gadhub.overseasproduct.common.constant.ErrorCode;
import com.gadhub.overseasproduct.common.constant.ProductStatus;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.dto.AddToCartDTO;
import com.gadhub.overseasproduct.entity.Cart;
import com.gadhub.overseasproduct.entity.Product;
import com.gadhub.overseasproduct.mapper.CartMapper;
import com.gadhub.overseasproduct.mapper.ProductMapper;
import com.gadhub.overseasproduct.service.CartService;
import com.gadhub.overseasproduct.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private static final int MAX_CART_ITEM_QUANTITY = 99;
    private static final String CART_CACHE_PREFIX = "cart:user:";
    private static final long CACHE_EXPIRE_TIME = 30;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void clearCart(Long userId) {

        if (userId == null){
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);

        cartMapper.delete(wrapper);

        String cacheKey = CART_CACHE_PREFIX + userId;
        redisTemplate.delete(cacheKey);

        log.info("购物车清空成功, userId: {}", userId);

    }

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

        String cacheKey = CART_CACHE_PREFIX + userId;
        redisTemplate.delete(cacheKey);

        log.info("删除购物车商品成功, cartId: {}, userId: {}", cartId, userId);

    }

    @Transactional
    @Override
    public void updateCart(Integer quantity, Long cartId,Long userId) {
        if (quantity <= 0){
            throw new BusinessException(ErrorCode.CART_QUANTITY_INVALID);
        }

        if (quantity > MAX_CART_ITEM_QUANTITY){
            throw new BusinessException(ErrorCode.CART_ITEM_QUANTITY_EXCEED_LIMIT);
        }

        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId);

        Cart cart = cartMapper.selectOne(queryWrapper);

        if (cart == null){
            throw new BusinessException(ErrorCode.CART_NOT_FOUND);
        }

        Product product = productMapper.selectById(cart.getProductId());
        if (product == null){
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (product.getStatus().equals(ProductStatus.OFF_SHELF.getCode())) {
            throw new BusinessException(ErrorCode.PRODUCT_OFF_SHELF);
        }

        if (product.getStock() < quantity){
            throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT);
        }

        LambdaUpdateWrapper<Cart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Cart::getId, cartId)
                .set(Cart::getQuantity, quantity);
        cartMapper.update(null, updateWrapper);

        String cacheKey = CART_CACHE_PREFIX + userId;
        redisTemplate.delete(cacheKey);

        log.info("更新购物车数量成功, cartId: {}, quantity: {}", cartId, quantity);

    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        String cacheKey = CART_CACHE_PREFIX + userId;

        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.info("从Redis缓存获取购物车数据, userId: {}", userId);
                return objectMapper.convertValue(cachedData, new TypeReference<List<CartVO>>() {});
            }
        } catch (Exception e) {
            log.warn("Redis缓存读取失败，降级到数据库查询, userId: {}, error: {}", userId, e.getMessage());
        }

        LambdaQueryWrapper<Cart> cartQueryWrapper = new LambdaQueryWrapper<>();
        cartQueryWrapper.eq(Cart::getUserId, userId);

        List<Cart> cartList = cartMapper.selectList(cartQueryWrapper);

        if (cartList.isEmpty()){
            return new ArrayList<>();
        }

        List<Long> productIds = cartList.stream()
                .map(Cart::getProductId)
                .distinct()
                .collect(Collectors.toList());

        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.in(Product::getId, productIds);
        List<Product> products = productMapper.selectList(productWrapper);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<CartVO> cartVOList = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = productMap.get(cart.getProductId());
            if (product != null) {
                CartVO cartVO = new CartVO();
                cartVO.setCartId(cart.getId());
                cartVO.setProductId(product.getId());
                cartVO.setProductName(product.getName());
                cartVO.setPrice(product.getPrice());
                cartVO.setQuantity(cart.getQuantity());
                cartVO.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
                cartVOList.add(cartVO);
            }
        }

        try {
            redisTemplate.opsForValue().set(cacheKey, cartVOList, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            log.info("购物车数据已缓存到Redis, userId: {}", userId);
        } catch (Exception e) {
            log.warn("Redis缓存写入失败, userId: {}, error: {}", userId, e.getMessage());
        }

        return cartVOList;
    }

    @Transactional
    @Override
    public void addToCart(AddToCartDTO addToCartDTO, Long userId) {
        if (userId == null){
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        if (addToCartDTO.getQuantity() <= 0){
            throw new BusinessException(ErrorCode.CART_QUANTITY_INVALID);
        }

        if (addToCartDTO.getQuantity() > MAX_CART_ITEM_QUANTITY){
            throw new BusinessException(ErrorCode.CART_ITEM_QUANTITY_EXCEED_LIMIT);
        }

        Product product = productMapper.selectById(addToCartDTO.getProductId());

        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (product.getStatus().equals(ProductStatus.OFF_SHELF.getCode())){
            throw new BusinessException(ErrorCode.PRODUCT_OFF_SHELF);
        }

        if (product.getStock() < addToCartDTO.getQuantity()){
            throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT);
        }

        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Cart::getUserId,userId)
                .eq(Cart::getProductId,addToCartDTO.getProductId());

        Cart duplicateProducts = cartMapper.selectOne(queryWrapper);

        if (duplicateProducts != null){
            int newQuantity = duplicateProducts.getQuantity() + addToCartDTO.getQuantity();
            if (newQuantity > MAX_CART_ITEM_QUANTITY){
                throw new BusinessException(ErrorCode.CART_ITEM_QUANTITY_EXCEED_LIMIT);
            }

            LambdaUpdateWrapper<Cart> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Cart::getId, duplicateProducts.getId())
                    .setSql("quantity = quantity + " + addToCartDTO.getQuantity());
            cartMapper.update(null, updateWrapper);

            log.info("更新购物车商品数量, userId: {}, productId: {}, newQuantity: {}",
                    userId, addToCartDTO.getProductId(), newQuantity);

        }else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(addToCartDTO.getProductId());
            cart.setQuantity(addToCartDTO.getQuantity());
            cartMapper.insert(cart);
            log.info("添加商品到购物车, userId: {}, productId: {}, quantity: {}",
                    userId, addToCartDTO.getProductId(), addToCartDTO.getQuantity());
        }

        String cacheKey = CART_CACHE_PREFIX + userId;
        redisTemplate.delete(cacheKey);
        log.info("购物车缓存已失效, userId: {}", userId);

    }
}
