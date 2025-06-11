package in.bushansirgur.foodiesapi.Service;

import in.bushansirgur.foodiesapi.io.CartRequest;
import in.bushansirgur.foodiesapi.io.CartResponse;

public interface CartService {


    CartResponse
    addToCart(CartRequest cartRequest);

    CartResponse getCart();

    void clearCart();

    CartResponse removefromCart(CartRequest cartRequest);
}
