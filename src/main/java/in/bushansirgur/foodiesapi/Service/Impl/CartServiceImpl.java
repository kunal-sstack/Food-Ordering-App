package in.bushansirgur.foodiesapi.Service.Impl;

import in.bushansirgur.foodiesapi.Entity.CartEntity;
import in.bushansirgur.foodiesapi.Repository.CartRepository;
import in.bushansirgur.foodiesapi.Service.CartService;
import in.bushansirgur.foodiesapi.Service.UserService;
import in.bushansirgur.foodiesapi.io.CartRequest;
import in.bushansirgur.foodiesapi.io.CartResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CartResponse addToCart(CartRequest cartRequest) {
        String userId = userService.findByUserId();
        Optional<CartEntity> optionalCart = cartRepository.findByUserId(userId);

        CartEntity cart = optionalCart.orElseGet(() -> new CartEntity(userId, new HashMap<>()));
        Map<String, Integer> items = cart.getItems();

        Integer currentQty = items.get(cartRequest.getFoodId());
        if (currentQty == null) {
            items.put(cartRequest.getFoodId(), 1);
        } else {
            items.put(cartRequest.getFoodId(), currentQty + 1);
        }


        cart.setItems(items);
        CartEntity savedCart = cartRepository.save(cart);
        return convertToCartResponse(savedCart);
    }

    @Override
    public CartResponse getCart() {
        String userId = userService.findByUserId();
        CartEntity cart = cartRepository.findByUserId(userId).orElse(new CartEntity(userId, new HashMap<>()));
        return convertToCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        String userId = userService.findByUserId();
        cartRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public CartResponse removefromCart(CartRequest cartRequest) {
        String userId = userService.findByUserId();
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Map<String, Integer> items = cart.getItems();
        String foodId = cartRequest.getFoodId();

        if (items.containsKey(foodId)) {
            int currentQty = items.get(foodId);
            if (currentQty > 1) {
                items.put(foodId, currentQty - 1);
            } else {
                // Remove item entirely if quantity goes to 0
                items.remove(foodId);
            }

            cart.setItems(items);
            cartRepository.save(cart);
        }

        return convertToCartResponse(cart);
    }

    private CartResponse convertToCartResponse(CartEntity cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(cart.getItems())
                .build();
    }
}
