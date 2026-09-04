package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.OrderItemRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.OrderRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.OrderStatusUpdateRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.OrderResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Order;
import com.krishna.MobileBackendProjectPhase1.entity.OrderItem;
import com.krishna.MobileBackendProjectPhase1.entity.OrderStatus;
import com.krishna.MobileBackendProjectPhase1.entity.Product;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.InsufficientStockException;
import com.krishna.MobileBackendProjectPhase1.exception.InvalidOrderStatusException;
import com.krishna.MobileBackendProjectPhase1.exception.OrderNotFoundException;
import com.krishna.MobileBackendProjectPhase1.exception.ProductNotFoundException;
import com.krishna.MobileBackendProjectPhase1.exception.UserNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.OrderRepository;
import com.krishna.MobileBackendProjectPhase1.repository.ProductRepository;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        User loggedInUser = getLoggedInUser();

        // USER can create order only for himself
        // ADMIN can create order for any user
        if (!hasRole("ADMIN") && !loggedInUser.getId().equals(request.getUserId())) {

            throw new AccessDeniedException("You can create an order only for yourself");
        }
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        double totalAmount = 0;
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product =
                    productRepository.findByIdForUpdate(
                            itemRequest.getProductId()
                    ).orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found with id: "
                                            + itemRequest.getProductId()
                            )
                    );

            int quantity = itemRequest.getQuantity();

            if (product.getStockQuantity() < quantity) {

                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            double subtotal =
                    product.getPrice() * quantity;

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);

            order.addOrderItem(orderItem);

            product.setStockQuantity(
                    product.getStockQuantity() - quantity
            );

            totalAmount += subtotal;
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder =
                orderRepository.save(order);

        return new OrderResponse(savedOrder);
    }

    // GET ORDER BY ID
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id
                        )
                );

        checkOrderOwnership(order);

        return new OrderResponse(order);
    }

    // GET USER ORDERS
    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUser(
            Long userId,
            int page,
            int size,
            String sort) {

        User loggedInUser = getLoggedInUser();

        if (!hasRole("ADMIN")
                && !loggedInUser.getId().equals(userId)) {

            throw new AccessDeniedException(
                    "You can access only your own orders"
            );
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "User not found with id: " + userId
            );
        }

        Pageable pageable =
                createPageable(page, size, sort);

        Page<Order> orders =
                orderRepository.findByUserId(
                        userId,
                        pageable
                );

        return orders.map(OrderResponse::new);
    }

    // GET ALL ORDERS - ADMIN
    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(
            int page,
            int size,
            String sort) {

        if (!hasRole("ADMIN")) {
            throw new AccessDeniedException(
                    "Only ADMIN can access all orders"
            );
        }

        Pageable pageable =
                createPageable(page, size, sort);

        Page<Order> orders =
                orderRepository.findAll(pageable);

        return orders.map(OrderResponse::new);
    }

    // UPDATE ORDER STATUS - ADMIN
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatusUpdateRequest request) {

        if (!hasRole("ADMIN")) {
            throw new AccessDeniedException(
                    "Only ADMIN can update order status"
            );
        }

        Order order =
                orderRepository.findByIdForUpdate(orderId)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + orderId
                                )
                        );

        validateStatusChange(order);

        order.setStatus(request.getStatus());

        return new OrderResponse(order);
    }

    // CANCEL ORDER
    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {

        Order order =
                orderRepository.findByIdForUpdate(orderId)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + orderId
                                )
                        );

        checkOrderOwnership(order);

        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new InvalidOrderStatusException(
                    "Order is already cancelled"
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {

            throw new InvalidOrderStatusException(
                    "Delivered order cannot be cancelled"
            );
        }

        for (OrderItem item : order.getOrderItems()) {

            Product product =
                    productRepository.findByIdForUpdate(
                            item.getProduct().getId()
                    ).orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found with id: "
                                            + item.getProduct().getId()
                            )
                    );

            product.setStockQuantity(
                    product.getStockQuantity()
                            + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        return new OrderResponse(order);
    }

    // =========================================================
    // DRIVER FUNCTIONALITY
    // =========================================================

    // ADMIN ASSIGNS DRIVER TO ORDER
    @Override
    @Transactional
    public OrderResponse assignDriver(
            Long orderId,
            Long driverId) {

        // Only ADMIN can assign drivers
        if (!hasRole("ADMIN")) {
            throw new AccessDeniedException(
                    "Only ADMIN can assign a driver"
            );
        }

        Order order =
                orderRepository.findByIdForUpdate(orderId)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + orderId
                                )
                        );

        User driver =
                userRepository.findById(driverId)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found with id: "
                                                + driverId
                                )
                        );

        // Make sure selected user is actually a DRIVER
        if (!"DRIVER".equalsIgnoreCase(
                driver.getRole())) {

            throw new AccessDeniedException(
                    "Selected user is not a DRIVER"
            );
        }

        // Don't assign driver to cancelled order
        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new InvalidOrderStatusException(
                    "Cannot assign driver to a cancelled order"
            );
        }

        // Don't assign driver to delivered order
        if (order.getStatus() == OrderStatus.DELIVERED) {

            throw new InvalidOrderStatusException(
                    "Cannot assign driver to a delivered order"
            );
        }

        order.setDriver(driver);

        return new OrderResponse(order);
    }

    // DRIVER GETS ONLY HIS ASSIGNED ORDERS
    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAssignedOrders(
            Long driverId,
            int page,
            int size,
            String sort) {

        User loggedInUser = getLoggedInUser();

        // Driver can only access his own assigned orders
        if (!hasRole("ADMIN")
                && (!hasRole("DRIVER")
                || !loggedInUser.getId().equals(driverId))) {

            throw new AccessDeniedException(
                    "You can access only your assigned orders"
            );
        }

        if (!userRepository.existsById(driverId)) {
            throw new UserNotFoundException(
                    "Driver not found with id: " + driverId
            );
        }

        Pageable pageable =
                createPageable(page, size, sort);

        Page<Order> orders =
                orderRepository.findByDriverId(
                        driverId,
                        pageable
                );

        return orders.map(OrderResponse::new);
    }

    // DRIVER UPDATES STATUS OF HIS ASSIGNED ORDER
    @Override
    @Transactional
    public OrderResponse updateDriverOrderStatus(
            Long orderId,
            OrderStatusUpdateRequest request) {

        User loggedInUser = getLoggedInUser();

        if (!hasRole("DRIVER")) {
            throw new AccessDeniedException(
                    "Only DRIVER can update delivery status"
            );
        }

        Order order =
                orderRepository.findByIdForUpdate(orderId)
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: "
                                                + orderId
                                )
                        );

        // IDOR protection:
        // Driver must be assigned to this order
        if (order.getDriver() == null
                || !order.getDriver().getId()
                .equals(loggedInUser.getId())) {

            throw new AccessDeniedException(
                    "You are not assigned to this order"
            );
        }

        validateStatusChange(order);

        OrderStatus newStatus =
                request.getStatus();

        // Driver can update only delivery-related statuses
        if (!isDriverAllowedStatus(newStatus)) {

            throw new InvalidOrderStatusException(
                    "Driver is not allowed to set status: "
                            + newStatus
            );
        }

        order.setStatus(newStatus);

        return new OrderResponse(order);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    // CHECK ORDER OWNERSHIP
    private void checkOrderOwnership(Order order) {

        // ADMIN can access every order
        if (hasRole("ADMIN")) {
            return;
        }

        User loggedInUser = getLoggedInUser();

        // DRIVER can access only his assigned order
        if (hasRole("DRIVER")) {

            if (order.getDriver() == null
                    || !order.getDriver().getId()
                    .equals(loggedInUser.getId())) {

                throw new AccessDeniedException(
                        "You are not assigned to this order"
                );
            }

            return;
        }

        // USER can access only his own order
        if (!order.getUser().getId()
                .equals(loggedInUser.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to access this order"
            );
        }
    }

    // GET LOGGED-IN USER
    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "User is not authenticated"
            );
        }

        return (User) authentication.getPrincipal();
    }

    // CHECK ROLE
    private boolean hasRole(String role) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_" + role)
                );
    }

    // COMMON STATUS VALIDATION
    private void validateStatusChange(Order order) {

        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new InvalidOrderStatusException(
                    "Cancelled order status cannot be changed"
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {

            throw new InvalidOrderStatusException(
                    "Delivered order status cannot be changed"
            );
        }
    }

    // STATUSES THAT DRIVER CAN SET
    private boolean isDriverAllowedStatus(
            OrderStatus status) {

        return status == OrderStatus.OUT_FOR_DELIVERY
                || status == OrderStatus.DELIVERED;
    }

    // PAGINATION + SORTING
    private Pageable createPageable(
            int page,
            int size,
            String sort) {

        String[] sortParts = sort.split(",");

        String property = sortParts[0];

        Sort.Direction direction =
                Sort.Direction.ASC;

        if (sortParts.length > 1
                && sortParts[1].equalsIgnoreCase("desc")) {

            direction = Sort.Direction.DESC;
        }

        return PageRequest.of(
                page,
                size,
                Sort.by(direction, property)
        );
    }
}