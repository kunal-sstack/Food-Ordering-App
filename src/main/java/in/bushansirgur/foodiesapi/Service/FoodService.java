package in.bushansirgur.foodiesapi.Service;

import in.bushansirgur.foodiesapi.io.FoodRequest;
import in.bushansirgur.foodiesapi.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);

    List<FoodResponse> getAllFoods();

    FoodResponse getFoodById(String id);

    boolean deleteFile(String filename);

    void deleteFood(String id);
}
