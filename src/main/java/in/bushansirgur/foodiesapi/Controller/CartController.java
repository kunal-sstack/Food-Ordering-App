package in.bushansirgur.foodiesapi.Controller;

import in.bushansirgur.foodiesapi.Service.CartService;
import in.bushansirgur.foodiesapi.io.CartRequest;
import in.bushansirgur.foodiesapi.io.CartResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartResponse addToCart(@RequestBody CartRequest request) {
        String foodid = request.getFoodId();
        if(foodid == null || foodid.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Food id not found");
        }
        CartResponse response = cartService.addToCart(request);  // call once
        return response;
    }

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(){
        cartService.clearCart();
    }

    @PostMapping("/remove")
    public CartResponse removeFromCart(@RequestBody CartRequest request) {
        String foodid = request.getFoodId();
        if(foodid == null || foodid.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Food id not found");
        }
        return cartService.removefromCart(request);
    }
}
