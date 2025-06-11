package in.bushansirgur.foodiesapi.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodResponse {

    private String id;
    private String imageUrl;
    private String name;
    private String description;
    private double price;
    private String category;
}
