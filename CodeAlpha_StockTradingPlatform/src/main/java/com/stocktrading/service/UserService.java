package com.stocktrading.service;

import com.stocktrading.exception.AuthenticationException;
import com.stocktrading.exception.UserAlreadyExistsException;
import com.stocktrading.model.User;
import com.stocktrading.repository.UserRepository;

/**
 * Handles registration, login and account-level operations for users.
 */
public class UserService {

    private final UserRepository userRepository;
    private User currentUser; // the currently logged-in user (single-session desktop app)

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password, double startingBalance) throws UserAlreadyExistsException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (username.contains(",") || password.contains(",")) {
            throw new IllegalArgumentException("Username and password cannot contain a comma.");
        }
        if (userRepository.exists(username)) {
            throw new UserAlreadyExistsException("Username '" + username + "' is already taken.");
        }
        return userRepository.create(username, password, startingBalance);
    }

    public User login(String username, String password) throws AuthenticationException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));
        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid username or password.");
        }
        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void persist(User user) {
        userRepository.update(user);
    }
}
