package com.example.gaming_ecomerce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.gaming_ecomerce.model.User;
import com.example.gaming_ecomerce.model.Client;
import com.example.gaming_ecomerce.repository.ClientRepository;
import com.example.gaming_ecomerce.repository.UserRepository;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:role-permissions;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.h2.console.enabled=false"
})
@AutoConfigureMockMvc
class SecurityAuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

        @Autowired
        private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void createAccountsForMockedUsers() {
        resetTestUser("user", "test-user@example.com");
        resetTestUser("admin", "test-admin@example.com");
                Client client = clientRepository.findByEmail("test-client@example.com").orElseGet(Client::new);
                client.setUsername("test-client");
                client.setEmail("test-client@example.com");
                client.setPassword(passwordEncoder.encode("test-password"));
                client.setFirstName("Test");
                client.setLastName("Client");
                clientRepository.save(client);
    }

    @Test
    void unauthenticatedUserCannotOpenUserManagement() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void csrfEndpointReturnsTokenForTheDashboard() throws Exception {
        mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "test-user@example.com", roles = "USER")
    void userCannotReadOrModifyUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user@example.com", roles = "USER")
    void authenticatedDashboardUserCannotAccessCustomerResources() throws Exception {
        mockMvc.perform(get("/api/clients/1/addresses"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/clients/1/wishlist"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/clients/1/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-client@example.com", roles = "USER")
    void customerCannotReadAnotherCustomersResources() throws Exception {
        Client client = clientRepository.findByEmail("test-client@example.com").orElseThrow();
        mockMvc.perform(get("/api/clients/{id}/addresses", client.getId() + 1))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/clients/{id}/wishlist", client.getId() + 1))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/clients/{id}/orders", client.getId() + 1))
                .andExpect(status().isForbidden());
    }

    @Test
    void registeringCustomerHashesPasswordAndCustomerCanStartSession() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"store-customer\",\"email\":\"store-customer@example.com\",\"password\":\"store-password\",\"firstName\":\"Store\",\"lastName\":\"Customer\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("store-customer@example.com"));

        Client client = clientRepository.findByEmail("store-customer@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("store-password", client.getPassword()));
        org.junit.jupiter.api.Assertions.assertNotEquals("store-password", client.getPassword());

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"store-customer@example.com\",\"password\":\"store-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(client.getId()))
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andReturn();

        mockMvc.perform(get("/api/auth/me")
                        .session((MockHttpSession) loginResult.getRequest().getSession(false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("store-customer@example.com"));
    }

    @Test
    @WithMockUser(username = "test-user@example.com", roles = "USER")
    void userCannotEditOrDeleteGamesAndPlatforms() throws Exception {
        mockMvc.perform(put("/api/games/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/games/1").with(csrf()))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/platforms/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/platforms/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-admin@example.com", roles = "ADMIN")
    void adminCanReachProtectedRoutes() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void loginStartsSessionAndUpgradesAnExistingPassword() throws Exception {
        User user = new User();
        user.setEmail("legacy-user@example.com");
        user.setPassword("legacy-password");
        user.setRole("user");
        user.setActive(true);
        user.setCreatedAt(LocalDate.now());
        userRepository.save(user);

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"legacy-user@example.com\",\"password\":\"legacy-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("user"))
                .andReturn();

        User savedUser = userRepository.findByEmail("legacy-user@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("legacy-password", savedUser.getPassword()));
        org.junit.jupiter.api.Assertions.assertFalse(savedUser.getPassword().equals("legacy-password"));

        mockMvc.perform(get("/api/auth/me").session((MockHttpSession) loginResult.getRequest().getSession(false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("legacy-user@example.com"));
    }

    @Test
    void roleChangesTakeEffectOnAnExistingSession() throws Exception {
        User admin = userRepository.findByEmail("test-admin@example.com").orElseThrow();
        admin.setPassword("initial-password");
        userRepository.save(admin);

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test-admin@example.com\",\"password\":\"initial-password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        admin.setRole("user");
        userRepository.save(admin);

        mockMvc.perform(get("/api/users")
                        .session((MockHttpSession) loginResult.getRequest().getSession(false)))
                .andExpect(status().isForbidden());
    }

    private void resetTestUser(String role, String email) {
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setEmail(email);
        if (user.getPassword() == null) {
            user.setPassword(passwordEncoder.encode("test-password"));
        }
        user.setRole(role);
        user.setActive(true);
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDate.now());
        userRepository.save(user);
    }
}
