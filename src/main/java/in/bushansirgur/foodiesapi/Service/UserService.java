package in.bushansirgur.foodiesapi.Service;

import in.bushansirgur.foodiesapi.io.UserRequest;
import in.bushansirgur.foodiesapi.io.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request);

    String findByUserId();
}
