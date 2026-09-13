package com.stocktrading.repository;

import com.stocktrading.model.User;
import com.stocktrading.util.CsvUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File-based persistence for User records. Data is kept in an in-memory list
 * and the whole file is rewritten on every save (simple approach, fine for a
 * student-scale project).
 */
public class UserRepository {

    private static final Path FILE_PATH = Path.of("data", "users.csv");
    private static final String HEADER = "id,username,password,balance,startingBalance,createdAt";

    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    public UserRepository() {
        load();
    }

    private void load() {
        users.clear();
        for (String[] cols : CsvUtil.readRows(FILE_PATH)) {
            int id = Integer.parseInt(cols[0]);
            String username = cols[1];
            String password = cols[2];
            double balance = Double.parseDouble(cols[3]);
            double startingBalance = Double.parseDouble(cols[4]);
            LocalDateTime createdAt = LocalDateTime.parse(cols[5]);
            users.add(new User(id, username, password, balance, startingBalance, createdAt));
            if (id >= nextId) {
                nextId = id + 1;
            }
        }
    }

    public void save() {
        List<String> rows = new ArrayList<>();
        for (User u : users) {
            rows.add(String.join(",",
                    String.valueOf(u.getId()),
                    u.getUsername(),
                    u.getPassword(),
                    String.valueOf(u.getBalance()),
                    String.valueOf(u.getStartingBalance()),
                    u.getCreatedAt().toString()));
        }
        CsvUtil.writeRows(FILE_PATH, HEADER, rows);
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public Optional<User> findByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public Optional<User> findById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    public boolean exists(String username) {
        return findByUsername(username).isPresent();
    }

    public User create(String username, String password, double startingBalance) {
        User user = new User(nextId++, username, password, startingBalance, startingBalance, LocalDateTime.now());
        users.add(user);
        save();
        return user;
    }

    /**
     * Persists changes to an already-tracked user (e.g. after balance update).
     */
    public void update(User user) {
        save();
    }
}
