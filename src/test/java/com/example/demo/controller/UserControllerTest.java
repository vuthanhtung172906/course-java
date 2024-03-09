package com.example.demo.controller;


import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController(null, null, null);
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("tungvt13");
        user.setPassword("tungpass");
        user.setCart(cart);
        when(userRepo.findByUsername("tungvt13")).thenReturn(user);
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.of(user));
        when(userRepo.findByUsername("someone")).thenReturn(null);

    }

    @Test
    public void create_user_happy_case() {
        when(encoder.encode("plain_password")).thenReturn("hassed_password");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("plain_password");
        r.setConfirmPassword("plain_password");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("hassed_password", u.getPassword());

    }

    @Test
    public void create_user_password_short() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("short");
        r.setConfirmPassword("short");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void create_user_password_confirm_mismatch() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("kkkkkkkkkkkkkkkk");
        r.setConfirmPassword("ppppppp");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_name_happy_case() {
        final ResponseEntity<User> response = userController.findByUserName("tungvt13");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals("tungvt13", u.getUsername());
    }

    @Test
    public void find_user_by_name_not_exist() {
        final ResponseEntity<User> response = userController.findByUserName("someone");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_id_happy_path() {
        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());;
    }

    @Test
    public void find_user_by_id_doesnt_exist() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}