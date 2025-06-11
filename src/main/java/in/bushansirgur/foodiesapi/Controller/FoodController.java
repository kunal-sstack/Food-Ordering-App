package in.bushansirgur.foodiesapi.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.bushansirgur.foodiesapi.Service.FoodService;
import in.bushansirgur.foodiesapi.io.FoodRequest;
import in.bushansirgur.foodiesapi.io.FoodResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
@CrossOrigin("*")
public class FoodController {

    private final ObjectMapper objectMapper;

    private final FoodService foodService;

    @PostMapping
    public FoodResponse addFood(@RequestPart("food") String foodString, @RequestPart("file")MultipartFile file){
        ObjectMapper objectMapper = new ObjectMapper();
        FoodRequest request = null;

        try{
            request = objectMapper.readValue(foodString, FoodRequest.class);
        }catch (JsonProcessingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        }

        FoodResponse response = foodService.addFood(request,file);
        return response;
    }

    @GetMapping
    public List<FoodResponse> getAllFoods(){
        return foodService.getAllFoods();
    }

    @GetMapping("/{id}")
    public FoodResponse getFoodById(@PathVariable String id){
        return foodService.getFoodById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodById(@PathVariable String id){
        foodService.deleteFood(id);
    }
}
