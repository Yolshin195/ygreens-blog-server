package ru.ygreens.blog.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Stubber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.ygreens.blog.dto.LoginRequest;
import ru.ygreens.blog.models.ERole;
import ru.ygreens.blog.models.Role;
import ru.ygreens.blog.models.User;
import ru.ygreens.blog.repository.RoleRepository;
import ru.ygreens.blog.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/auth/signin")
    void signInTest() throws Exception {
        Role mockRole = new Role(ERole.ROLE_USER);
        Mockito.doReturn(Optional.of(mockRole)).when(roleRepository).findByName(ERole.ROLE_USER);

        User mockUser = new User("Oleg", "oleg@gmail.com", "89857680405", passwordEncoder.encode("123456789"));
        mockUser.setRoles(Set.of(mockRole));
        Mockito.doReturn(Optional.of(mockUser)).when(userRepository).findByUsername("Oleg");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("Oleg");
        loginRequest.setPassword("123456789");

        ObjectMapper mapperJson = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/signin")
                .content(mapperJson.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/signup")
    void signupTest() throws Exception {

    }
}
