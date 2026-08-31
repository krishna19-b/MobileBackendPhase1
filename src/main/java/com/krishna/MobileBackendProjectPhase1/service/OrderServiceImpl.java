package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.OrderItemRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.OrderRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.OrderResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Order;
import com.krishna.MobileBackendProjectPhase1.entity.OrderItem;
import com.krishna.MobileBackendProjectPhase1.entity.OrderStatus;
import com.krishna.MobileBackendProjectPhase1.entity.Product;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.ProductNotFoundException;
import com.krishna.MobileBackendProjectPhase1.exception.UserNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.OrderRepository;
import com.krishna.MobileBackendProjectPhase1.repository.ProductRepository;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // CREATE ORDER


    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Find User
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        // 2. Create Order
        Order order = new Order();
        order.setUser(user);
        // IMPORTANT
        order.setStatus(OrderStatus.PLACED);
        double totalAmount = 0;
        // 3. Process order items
        for (OrderItemRequest itemRequest : request.getItems()) {
            // 4. Lock Product row
            Product product = productRepository.findByIdForUpdate(itemRequest.getProductId()).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + itemRequest.getProductId()));
            // 5. Validate stock
            int quantity = itemRequest.getQuantity();
            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            // 6. Calculate subtotal
            double subtotal = product.getPrice() * quantity;
            // 7. Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            // 8. Add OrderItem
            order.addOrderItem(orderItem);
            // 9. Reduce stock
            product.setStockQuantity(product.getStockQuantity() - quantity);
            // 10. Calculate total
            totalAmount += subtotal;
        }
        // 11. Set total
        order.setTotalAmount(totalAmount);
        // 12. Save order
        Order savedOrder = orderRepository.save(order);
        return new OrderResponse(savedOrder);
    }

    // GET ORDER

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return new OrderResponse(order);
    }

    // GET USER ORDERS

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUser(Long userId, int page, int size, String sort) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        Pageable pageable = createPageable(page, size, sort);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(OrderResponse::new);
    }

    // CANCEL ORDER

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
         // Lock order row.
        Order order = orderRepository.findByIdForUpdate(orderId).orElseThrow(() ->new RuntimeException("Order not found with id: " + orderId));
        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new RuntimeException("Order is already cancelled");
        }

        // Restore stock

        for (OrderItem item : order.getOrderItems()) {

            Product product = productRepository.findByIdForUpdate(item.getProduct().getId()).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + item.getProduct().getId()));

            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        // Change status

        order.setStatus(OrderStatus.CANCELLED);



        return new OrderResponse(order);
    }


    // PAGINATION + SORTING


    private Pageable createPageable(int page, int size, String sort) {

        String[] sortParts = sort.split(",");

        String property = sortParts[0];

        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")) {

            direction = Sort.Direction.DESC;
        }

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}