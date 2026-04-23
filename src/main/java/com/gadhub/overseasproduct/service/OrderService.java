package com.gadhub.overseasproduct.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gadhub.overseasproduct.dto.CreateOrderDTO;
import com.gadhub.overseasproduct.vo.OrderVO;


public interface OrderService {
    // 创建订单
    Long createOrder(CreateOrderDTO createOrderDTO, Long userId);

    // 查询订单详情
    OrderVO getOrderDetail(Long orderId);

    // 分页查询订单列表
    Page<OrderVO> getOrderPage(Long userId, Integer pageNum, Integer pageSize);

    // 取消订单
    void cancelOrder(Long orderId, Long userId);

    void cancelTimeoutOrders();

}
