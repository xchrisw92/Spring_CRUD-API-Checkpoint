package com.example.demo;


import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private User userOne = new User();
    private User userTwo = new User();

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository repository;

    @BeforeEach
    void setup(){
        this.userOne.setEmail("weltoncm@yahoo.com");
        this.userOne.setPassword("password");
        this.userTwo.setEmail("chris.welton.m@gmail.com");
        this.userTwo.setPassword("1234");
        this.repository.save(userOne);
        this.repository.save(userTwo);
    }

    @Transactional
    @Rollback
    @Test
    void getUserReturnsListOfAllUsers() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.get("/user")
                .contentType(MediaType.APPLICATION_JSON);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email", is(this.userOne.getEmail())));
    }

    @Transactional
    @Rollback
    @Test
    void addUserSetsNewUser() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.post("/user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\" : \"weltoncm@yahoo.com\" , \" password\" : \"password\" }");
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(userOne.getEmail())));
    }

    @Transactional
    @Rollback
    @Test
    void getUserWithPathVariableReturnsSelectedUser() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/" + userOne.getId())
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(userOne.getEmail())));
    }

    @Transactional
    @Rollback
    @Test
    void updateUserDataWithPatch() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.patch("/user/" + userOne.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"sk8rboi92@gmail.com\"}");

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(userOne.getEmail())));
    }

    @Transactional
    @Rollback
    @Test
    void updateUserPasswordWithPatch() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.patch("/user/" + userOne.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"sk8rboi92@gmail.com\" , \"password\" : \"helloWorld\"}");
        this.mvc.perform(request)
                .andExpect(status().isOk());
        Assertions.assertEquals("helloWorld", userOne.getPassword());
    }

    @Transactional
    @Rollback
    @Test
    void deleteUser() throws Exception{
        Long id = userOne.getId();
        RequestBuilder request = MockMvcRequestBuilders.delete("/user/" + id)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + id).doesNotExist())
                .andReturn();
    }

    @Transactional
    @Rollback
    @Test
    void authenticateUserReturnsCorrectResponseWithGivenPassword() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.post("/user/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\" : \"weltoncm@yahoo.com\" , \"password\" : \"password\" }");

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authentication", is(true )))
                .andExpect(jsonPath("$.user.email" , is(userOne.getEmail())));
    }
    @Transactional
    @Rollback
    @Test
    void authenticateUserReturnsFalseWhenPasswordIsIncorrect() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.post("/user/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\" : \"weltoncm@yahoo.com\" , \"password\" : \"pas5sword\" }");
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authentication", is(false)));
    }

    @Transactional
    @Rollback
    @Test
    void authenticateUserReturnsStringWhenEmailIsWrong() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders.post("/user/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\" : \"weltonc@yahoo.com\" , \"password\" : \"password\" }");
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authentication", is(false )));
    }


}
