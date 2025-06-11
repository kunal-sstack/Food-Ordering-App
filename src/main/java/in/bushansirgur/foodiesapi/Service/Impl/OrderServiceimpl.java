package in.bushansirgur.foodiesapi.Service.Impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import in.bushansirgur.foodiesapi.Entity.OrderEntity;
import in.bushansirgur.foodiesapi.Repository.CartRepository;
import in.bushansirgur.foodiesapi.Repository.OrderRepository;
import in.bushansirgur.foodiesapi.Service.OrderService;
import in.bushansirgur.foodiesapi.Service.UserService;
import in.bushansirgur.foodiesapi.io.OrderRequest;
import in.bushansirgur.foodiesapi.io.OrderResponse;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceimpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;

    @Value("${razorpay_key}")
    private String RAZORPAY_KEY;

    @Value("${razorpay_secret}")
    private String RAZORPAY_SECRET;

    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) {
        OrderEntity newOrder = convertToEntity(request);
        newOrder = orderRepository.save(newOrder);

        try {
            RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", newOrder.getAmount()*100);
            orderRequest.put("currency", "INR");
            orderRequest.put("payment_capture", 1);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            newOrder.setRazorpayOrderId(razorpayOrder.get("id"));
            String loggedInUser = userService.findByUserId();
            newOrder.setUserId(loggedInUser);
            newOrder = orderRepository.save(newOrder);
            return convertToResponse(newOrder);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    @Override
    public void verifyPayment(Map<String, String> paymentData, String status) {
        String razorpayOrderId = paymentData.get("razorpay_order_id");
        OrderEntity existingOrder = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        existingOrder.setPaymentStatus(status);
        existingOrder.setRazorpaySignature(paymentData.get("signature"));
        existingOrder.setRazorpayPaymentId(paymentData.get("razorpay_payment_id"));
        orderRepository.save(existingOrder);
        if("paid".equalsIgnoreCase(status)) {
            cartRepository.deleteByUserId(existingOrder.getUserId());
        }
    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUser = userService.findByUserId();
        List<OrderEntity> list = orderRepository.findByUserId(loggedInUser);
        return list.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderResponse> getOrdersOfAllUsers() {
        List<OrderEntity> list = orderRepository.findAll();
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        OrderEntity existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        existingOrder.setOrderStatus(status);
        orderRepository.save(existingOrder);
    }


    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .id(newOrder.getId())
                .amount(newOrder.getAmount())
                .userAddress(newOrder.getUserAddress())
                .userId(newOrder.getUserId())
                .razorpayOrderId(newOrder.getRazorpayOrderId())
                .orderStatus(newOrder.getOrderStatus())
                .paymentStatus(newOrder.getPaymentStatus())
                .email(newOrder.getEmail())
                .phoneNumber(newOrder.getPhoneNumber())
                .orderitems(newOrder.getOrderitems())
                .build();
    }

    public OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .orderitems(request.getOrderedItems())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .orderStatus(request.getOrderStatus())
                .build();
    }
}

