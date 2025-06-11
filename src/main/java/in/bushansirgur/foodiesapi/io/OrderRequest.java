package in.bushansirgur.foodiesapi.io;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequest {

    private List<Orderitem> orderedItems;
    private String userAddress;
    private double amount;
    private String email;
    private String phoneNumber;
    private String orderStatus;
}
