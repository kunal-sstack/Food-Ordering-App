package in.bushansirgur.foodiesapi.Controller;

import in.bushansirgur.foodiesapi.Service.UserService;
import in.bushansirgur.foodiesapi.io.UserRequest;
import in.bushansirgur.foodiesapi.io.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody UserRequest userRequest) {
        return userService.registerUser(userRequest);
    }
}
