package com.gadhub.overseasproduct.controller;

import com.gadhub.overseasproduct.common.annotation.RateLimit;
import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.dto.AddToCartDTO;
import com.gadhub.overseasproduct.dto.UpdateCartDTO;
import com.gadhub.overseasproduct.service.CartService;
import com.gadhub.overseasproduct.util.UserContextUtil;
import com.gadhub.overseasproduct.vo.CartVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/backend/cart")
@Tag(name = "购物车管理")
public class CartController {

    @Autowired
    private CartService cartService;

    // 添加商品到购物车
    @PostMapping("/add")
    @RateLimit(time = 60, count = 50)
    public Result addToCart(@RequestBody @Valid AddToCartDTO addToCartDTO) {

        Long userId = UserContextUtil.getCurrentUserId();

        cartService.addToCart(addToCartDTO, userId);
        return Result.success("添加成功");
    }

    // 查询购物车列表
    @GetMapping("/list")
    public Result getCartList() {

        Long userId = UserContextUtil.getCurrentUserId();

        List<CartVO> cartList = cartService.getCartList(userId);
        return Result.success(cartList);
    }

    // 更新购物车商品数量
    @PutMapping("/update")
    @RateLimit(time = 60, count = 20)
    public Result updateCartItem(@RequestBody @Valid UpdateCartDTO updateCartDTO) {

        Long userId = UserContextUtil.getCurrentUserId();

        cartService.updateCart(updateCartDTO.getQuantity(), updateCartDTO.getCartId(), userId);
        return Result.success("更新成功");
    }

    // 删除购物车商品
    @DeleteMapping("/{cartId}")
    public Result deleteCartItem(@PathVariable Long cartId) {

        Long userId = UserContextUtil.getCurrentUserId();

        cartService.deleteCart(cartId, userId);
        return Result.success("删除成功");
    }

    @DeleteMapping("/clear")
    public Result clearCart() {
        Long userId = UserContextUtil.getCurrentUserId();
        cartService.clearCart(userId);
        return Result.success();
    }


}
