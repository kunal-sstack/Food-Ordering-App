package in.bushansirgur.foodiesapi.Service.Impl;

import in.bushansirgur.foodiesapi.Entity.FoodEntity;
import in.bushansirgur.foodiesapi.Repository.FoodRepository;
import in.bushansirgur.foodiesapi.Service.FoodService;
import in.bushansirgur.foodiesapi.io.FoodRequest;
import in.bushansirgur.foodiesapi.io.FoodResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private FoodRepository foodRepository;

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.lastIndexOf('.') == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        String key = UUID.randomUUID() + "." + fileExtension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read") // NOTE: ACL can be set at bucket level too!
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if (response.sdkHttpResponse().isSuccessful()) {
                return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file to S3");
            }

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading file: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error uploading file: " + e.getMessage());
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        FoodEntity newFoodEntity = convertToEntity(request);
        String imageUrl = uploadFile(file);
        newFoodEntity.setImageUrl(imageUrl);
        newFoodEntity = foodRepository.save(newFoodEntity);
        return convertToResponse(newFoodEntity);
    }

    @Override
    public List<FoodResponse> getAllFoods() {
        List<FoodEntity> databaseEntries = foodRepository.findAll();
        return databaseEntries.stream().map(object -> convertToResponse(object)).collect(Collectors.toList());
    }

    @Override
    public FoodResponse getFoodById(String id) {
        FoodEntity existingFood = foodRepository.findById(id).orElseThrow(() -> new RuntimeException("Food not found for the id: "+id));
        return convertToResponse(existingFood);
    }

    @Override
    public boolean deleteFile(String filename) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filename).build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;

    }

    @Override
    public void deleteFood(String id) {
        FoodResponse response = getFoodById(id);
        String imageUrl = response.getImageUrl();
        String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        boolean isFileDeleted = deleteFile(filename);
        if(isFileDeleted) {
            foodRepository.deleteById(id);
        }
    }

    private FoodEntity convertToEntity(FoodRequest request){
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
    }

    private FoodResponse convertToResponse(FoodEntity foodEntity){
        return FoodResponse.builder()
                .id(foodEntity.getId())
                .name(foodEntity.getName())
                .description(foodEntity.getDescription())
                .price(foodEntity.getPrice())
                .category(foodEntity.getCategory())
                .imageUrl(foodEntity.getImageUrl())
                .build();
    }
}

