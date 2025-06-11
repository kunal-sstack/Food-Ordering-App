package in.bushansirgur.foodiesapi.Service.Impl;

import in.bushansirgur.foodiesapi.Entity.UserEntity;
import in.bushansirgur.foodiesapi.Repository.UserRepository;
import in.bushansirgur.foodiesapi.Service.AuthenticationFacade;
import in.bushansirgur.foodiesapi.Service.UserService;
import in.bushansirgur.foodiesapi.io.UserRequest;
import in.bushansirgur.foodiesapi.io.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public UserResponse registerUser(UserRequest request) {
        UserEntity newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    @Override
    public String findByUserId() {
        String loggedInUserEmail = authenticationFacade.getAuthentication().getName();
        UserEntity loggedInUser = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return loggedInUser.getId();
    }


    private UserEntity convertToEntity(UserRequest request){
        return UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();
    }

    private UserResponse convertToResponse(UserEntity registeredUser){
        return UserResponse.builder()
                .email(registeredUser.getEmail())
                .name(registeredUser.getName())
                .id(registeredUser.getId())
                .build();
    }
}
