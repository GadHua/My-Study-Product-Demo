package com.gadhub.overseasproduct.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.dto.CreateOrderDTO;
import com.gadhub.overseasproduct.service.OrderService;
import com.gadhub.overseasproduct.vo.OrderVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/backend/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 创建订单
    @PostMapping("/create")
    public Result createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        // TODO: 获取当前登录用户的ID
        Long userId = 1L; // 暂时写死，后面从登录信息中获取

        Long orderId = orderService.createOrder(createOrderDTO, userId);
        return Result.success(orderId);
    }

    // 查询订单详情
    @GetMapping("/{id}")
    public Result getOrderDetail(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    // 分页查询订单列表
    @GetMapping("/page")
    public Result getOrderPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        // TODO: 获取当前登录用户的ID
        Long userId = 1L; // 暂时写死

        Page<OrderVO> page = orderService.getOrderPage(userId, pageNum, pageSize);
        return Result.success(page);
    }

    // 取消订单
    @PutMapping("/{id}/cancel")
    public Result cancelOrder(@PathVariable Long id) {
        // TODO: 获取当前登录用户的ID
        Long userId = 1L; // 暂时写死

        orderService.cancelOrder(id, userId);
        return Result.success("订单已取消");
    }



}
