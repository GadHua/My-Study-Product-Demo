
package com.gadhub.overseasproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.dto.CreateOrderDTO;
import com.gadhub.overseasproduct.dto.OrderItemDTO;
import com.gadhub.overseasproduct.entity.Order;
import com.gadhub.overseasproduct.entity.OrderItem;
import com.gadhub.overseasproduct.entity.Product;
import com.gadhub.overseasproduct.mapper.OrderItemMapper;
import com.gadhub.overseasproduct.mapper.OrderMapper;
import com.gadhub.overseasproduct.mapper.ProductMapper;
import com.gadhub.overseasproduct.service.OrderService;
import com.gadhub.overseasproduct.vo.OrderItemVO;
import com.gadhub.overseasproduct.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.gadhub.overseasproduct.common.constant.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    @Override
    public Long createOrder(CreateOrderDTO createOrderDTO, Long userId) {
        // 1. 获取商品列表
        List<OrderItemDTO> items = createOrderDTO.getItems();

        log.info("开始创建订单, userId: {}, 商品数量: {}", userId, items.size());

        // 2. 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO item : items) {
            Product product = productMapper.selectById(item.getProductId());
            // 验证商品是否存在、是否上架
            if (product == null){
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            if (!(product.getStatus()==1)){
                throw new BusinessException(ErrorCode.PRODUCT_OFF_SHELF);
            }

            if(product.getStock() < item.getQuantity()){
                 throw new BusinessException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT);
            }


            LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Product::getId, product.getId())
                    .setSql("stock = stock-"+item.getQuantity());
            productMapper.update(null, updateWrapper);

            // 累加总金额
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        // 3. 创建订单主表
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 0=待支付
        orderMapper.insert(order);

        // 4. 创建订单明细
        Long orderId = order.getId();
        for (OrderItemDTO item : items) {
            Product product = productMapper.selectById(item.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemMapper.insert(orderItem);
        }
        log.info("订单创建成功, orderId: {}, userId: {}, totalAmount: {}", orderId, userId, totalAmount);

        return orderId;
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        if (orderId == null){ // 判断传的是不是正常值
            throw new BusinessException(ErrorCode.ORDER_ID_REQUIRED);
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null){ // 在表里面找有没有这个订单
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 把要给前端的数据放进 OrderVO 对象，填充信息
        OrderVO orderVO = new OrderVO();
        orderVO.setId(order.getId());
        orderVO.setUserId(order.getUserId());
        orderVO.setTotalAmount(order.getTotalAmount());
        orderVO.setStatus(order.getStatus());
        orderVO.setCreatedAt(order.getCreatedAt());
        orderVO.setPayTime(order.getPayTime());

        // 4. 查询订单明细列表
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(wrapper);


        // 5. 将 OrderItem 转换为 OrderItemVO
        List<OrderItemVO> itemVOList = new ArrayList<>();
        for (OrderItem item : orderItems) {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setOrderId(item.getOrderId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductPrice(item.getProductPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVOList.add(itemVO);
        }

        // 6. 设置订单商品列表
        orderVO.setItems(itemVOList);
        return orderVO;
    }

    @Override
    public Page<OrderVO> getOrderPage(Long userId, Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<Order> page = new Page<>(pageNum, pageSize);

        //  构建查询条件（按用户ID查询，按创建时间降序）
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedAt);

        //  执行分页查询
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);

        // 转换为 VO 分页对象
        Page<OrderVO> voPage = new Page<>();
        voPage.setCurrent(orderPage.getCurrent());
        voPage.setSize(orderPage.getSize());
        voPage.setTotal(orderPage.getTotal());
        voPage.setPages(orderPage.getPages());

        // 转换每条订单记录
        List<OrderVO> voList = new ArrayList<>();
        for (Order order : orderPage.getRecords()) {
            OrderVO orderVO = new OrderVO();
            orderVO.setId(order.getId());
            orderVO.setUserId(order.getUserId());
            orderVO.setTotalAmount(order.getTotalAmount());
            orderVO.setStatus(order.getStatus());
            orderVO.setCreatedAt(order.getCreatedAt());
            orderVO.setPayTime(order.getPayTime());

            // 查询订单明细
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);

            // 转换为 OrderItemVO
            List<OrderItemVO> itemVOList = new ArrayList<>();
            for (OrderItem item : orderItems) {
                OrderItemVO itemVO = new OrderItemVO();
                itemVO.setId(item.getId());
                itemVO.setOrderId(item.getOrderId());
                itemVO.setProductId(item.getProductId());
                itemVO.setProductName(item.getProductName());
                itemVO.setProductPrice(item.getProductPrice());
                itemVO.setQuantity(item.getQuantity());
                itemVOList.add(itemVO);
            }

            orderVO.setItems(itemVOList);
            voList.add(orderVO);
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId, Long userId) {

        log.info("开始取消订单, orderId: {}, userId: {}", orderId, userId);

        if (orderId == null){ //有没有订单ID
           throw new BusinessException(ErrorCode.ORDER_ID_REQUIRED);
       }

       Order order = orderMapper.selectById(orderId);
       if (order == null){ // 订单不存在
           throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
       }

       if (!order.getUserId().equals(userId)){ // 订单不属于当前用户
           throw new BusinessException(ErrorCode.ORDER_NOT_BELONG_TO_USER);
       }

       if (order.getStatus() != 0){ // 订单状态不是待付款
           throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
       }

       List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : orderItems) {
            LambdaUpdateWrapper<Product> stockWrapper = new LambdaUpdateWrapper<>();
            stockWrapper.eq(Product::getId, item.getProductId())
                    .setSql("stock = stock + " + item.getQuantity());
            productMapper.update(null, stockWrapper);
        }

       LambdaUpdateWrapper<Order> statusWrapper = new LambdaUpdateWrapper<>();
            statusWrapper.eq(Order::getId, orderId)
                .set(Order::getStatus, 2); // 2=已取消
        orderMapper.update(null, statusWrapper);

        log.info("订单取消成功, orderId: {}", orderId);
    }
}

