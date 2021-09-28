package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserDto;
import com.example.demo.model.Views;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/user")
    @JsonView(Views.UserView.class)
    public Iterable<User> getAllUsers(){
        return this.repository.findAll();
    }

    @PostMapping("/user")
    @JsonView(Views.UserView.class)
    public User addNewUser(@RequestBody User inputUser){
        return this.repository.save(inputUser);
    }

    @GetMapping("/user/{userId}")
    @JsonView(Views.UserView.class)
    public Optional<User> getUserFromGivenId(@PathVariable Long userId){
        return this.repository.findById(userId);
    }

    @PatchMapping("/user/{userId}")
    @JsonView(Views.UserView.class)
    public Optional<User> updateUserInformation(@PathVariable Long userId, @RequestBody User userData){
        boolean successfulUpdate = false;
        Optional<User> userToBeUpdated = this.repository.findById(userId);
        if(userToBeUpdated != null){
            if(userData.getEmail() != null){
                userToBeUpdated.get().setEmail(userData.getEmail());
                this.repository.save(userToBeUpdated.get());
                successfulUpdate = true;
            }
            if(userData.getPassword() != null){
                userToBeUpdated.get().setPassword(userData.getPassword());
                this.repository.save(userToBeUpdated.get());
                successfulUpdate = true;
            }
        } else if(successfulUpdate = false){

        }
        return userToBeUpdated; 
    }

    @DeleteMapping("/user/{userId}")
    public String deleteUserFromGivenId(@PathVariable Long userId){
        Long numUsers = this.repository.count();
        this.repository.deleteById(userId);


        return String.format("{count : %d}", numUsers);
    }

    @PostMapping("/user/authenticate")
    public UserDto authenticateUserFromEmailAndPassword(@RequestBody User userInfo){
        UserDto resultView = new UserDto();
        for (User u: this.repository.findAll()) {
            if(userInfo.getEmail().equals(u.getEmail()) && userInfo.getPassword().equals(u.getPassword())){
              u.setAuthentication(true);
              resultView.setAuthentication(true);
              resultView.setUser(u);
            } else if(userInfo.getEmail().equals(u.getEmail())){
                resultView.setAuthentication(false);
            }
        }
        return resultView;
    }
}
