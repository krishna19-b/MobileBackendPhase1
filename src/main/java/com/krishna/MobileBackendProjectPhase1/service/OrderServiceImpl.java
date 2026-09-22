package com.krishna.MobileBackendProjectPhase1.service;

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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final NotificationServiceImpl notificationService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            NotificationServiceImpl notificationService
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        User loggedInUser = getLoggedInUser();

        if (hasRole("USER") && !loggedInUser.getId().equals(request.getUserId())) {
            throw new RuntimeException("Users can create orders only for themselves");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + request.getUserId()
                        )
                );

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(0);

        double totalAmount = 0;

        for (var itemRequest : request.getItems()) {

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found with id: " +
                                            itemRequest.getProductId()
                            )
                    );

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " +
                                product.getName()
                );
            }

            double subtotal =
                    product.getPrice() * itemRequest.getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubtotal(subtotal);

            order.addOrderItem(orderItem);

            product.setStockQuantity(
                    product.getStockQuantity() -
                            itemRequest.getQuantity()
            );

            totalAmount += subtotal;
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        notificationService.sendOrderCreatedNotification(
                savedOrder.getId(),
                user.getId()
        );

        return new OrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(
            int page,
            int size,
            String sort
    ) {

        if (!hasRole("ADMIN")) {
            throw new RuntimeException("Only admin can view all orders");
        }

        PageRequest pageable = createPageable(page, size, sort);

        return orderRepository.findAll(pageable)
                .map(OrderResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUser(
            Long userId,
            int page,
            int size,
            String sort
    ) {

        User loggedInUser = getLoggedInUser();

        if (hasRole("USER") &&
                !loggedInUser.getId().equals(userId)) {

            throw new RuntimeException(
                    "Users can view only their own orders"
            );
        }

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        PageRequest pageable = createPageable(page, size, sort);

        return orderRepository.findByUserId(userId, pageable)
                .map(OrderResponse::new);
    }

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

    @Transactional
    public OrderResponse updateOrderStatus(
            Long id,
            OrderStatusUpdateRequest request
    ) {

        if (!hasRole("ADMIN")) {
            throw new RuntimeException(
                    "Only admin can update order status"
            );
        }

        Order order = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id
                        )
                );

        validateStatusChange(order);

        order.setStatus(request.getStatus());

        return new OrderResponse(
                orderRepository.save(order)
        );
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {

        Order order = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id
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

        for (OrderItem orderItem : order.getOrderItems()) {

            Product product = productRepository.findByIdForUpdate(
                    orderItem.getProduct().getId()
            ).orElseThrow(() ->
                    new ProductNotFoundException(
                            "Product not found with id: " +
                                    orderItem.getProduct().getId()
                    )
            );

            product.setStockQuantity(
                    product.getStockQuantity() +
                            orderItem.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        return new OrderResponse(
                orderRepository.save(order)
        );
    }

    @Transactional
    public OrderResponse assignDriver(
            Long orderId,
            Long driverId
    ) {

        if (!hasRole("ADMIN")) {
            throw new RuntimeException(
                    "Only admin can assign drivers"
            );
        }

        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        User driver = userRepository.findById(driverId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Driver not found with id: " + driverId
                        )
                );

        if (!"DRIVER".equalsIgnoreCase(driver.getRole())) {
            throw new RuntimeException(
                    "Selected user is not a driver"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED ||
                order.getStatus() == OrderStatus.DELIVERED) {

            throw new InvalidOrderStatusException(
                    "Driver cannot be assigned to this order"
            );
        }

        order.setDriver(driver);

        return new OrderResponse(
                orderRepository.save(order)
        );
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAssignedOrders(
            Long driverId,
            int page,
            int size,
            String sort
    ) {

        User loggedInUser = getLoggedInUser();

        if (!hasRole("DRIVER") && !hasRole("ADMIN")) {
            throw new RuntimeException(
                    "Only driver or admin can view assigned orders"
            );
        }

        if (hasRole("DRIVER") &&
                !loggedInUser.getId().equals(driverId)) {

            throw new RuntimeException(
                    "Drivers can view only their assigned orders"
            );
        }

        userRepository.findById(driverId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Driver not found with id: " + driverId
                        )
                );

        PageRequest pageable = createPageable(page, size, sort);

        return orderRepository.findByDriverId(driverId, pageable)
                .map(OrderResponse::new);
    }

    @Transactional
    public OrderResponse updateDriverOrderStatus(
            Long id,
            OrderStatusUpdateRequest request
    ) {

        User loggedInUser = getLoggedInUser();

        if (!hasRole("DRIVER")) {
            throw new RuntimeException(
                    "Only driver can update driver order status"
            );
        }

        Order order = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + id
                        )
                );

        if (order.getDriver() == null ||
                !order.getDriver().getId().equals(loggedInUser.getId())) {

            throw new RuntimeException(
                    "You are not assigned to this order"
            );
        }

        if (!isDriverAllowedStatus(request.getStatus())) {
            throw new InvalidOrderStatusException(
                    "Driver can update only to OUT_FOR_DELIVERY or DELIVERED"
            );
        }

        validateStatusChange(order);

        order.setStatus(request.getStatus());

        return new OrderResponse(
                orderRepository.save(order)
        );
    }

    private void checkOrderOwnership(Order order) {

        User loggedInUser = getLoggedInUser();

        if (hasRole("ADMIN")) {
            return;
        }

        if (hasRole("DRIVER")) {

            if (order.getDriver() == null ||
                    !order.getDriver().getId().equals(loggedInUser.getId())) {

                throw new RuntimeException(
                        "You are not assigned to this order"
                );
            }

            return;
        }

        if (order.getUser() == null ||
                !order.getUser().getId().equals(loggedInUser.getId())) {

            throw new RuntimeException(
                    "You can access only your own orders"
            );
        }
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Authentication required"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException(
                    "Invalid authenticated user"
            );
        }

        return (User) principal;
    }

    private boolean hasRole(String role) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_" + role)
                );
    }

    private void validateStatusChange(Order order) {

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                    "Cancelled order cannot be updated"
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException(
                    "Delivered order cannot be updated"
            );
        }
    }

    private boolean isDriverAllowedStatus(OrderStatus status) {

        return status == OrderStatus.OUT_FOR_DELIVERY ||
                status == OrderStatus.DELIVERED;
    }

    private PageRequest createPageable(
            int page,
            int size,
            String sort
    ) {

        String[] sortParts = sort.split(",");

        String property = sortParts[0];

        Sort.Direction direction =
                sortParts.length > 1 &&
                        "desc".equalsIgnoreCase(sortParts[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return PageRequest.of(
                page,
                size,
                Sort.by(direction, property)
        );
    }
}