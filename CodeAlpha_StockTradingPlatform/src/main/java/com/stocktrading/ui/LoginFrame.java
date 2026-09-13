package com.stocktrading.ui;

import com.stocktrading.exception.AuthenticationException;
import com.stocktrading.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * The first screen shown to the user: login, with an option to register.
 */
public class LoginFrame extends JFrame {

    private final AppContext context;
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);

    public LoginFrame(AppContext context) {
        super("Stock Trading Platform - Login");
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 260);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Stock Trading Platform", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register New Account");
        buttons.add(loginButton);
        buttons.add(registerButton);
        root.add(buttons, BorderLayout.SOUTH);

        JLabel hint = new JLabel("Demo login: demo / demo123", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        root.add(hint, BorderLayout.PAGE_END);

        setContentPane(root);

        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
        registerButton.addActionListener(e -> {
            RegisterDialog dialog = new RegisterDialog(this, context);
            dialog.setVisible(true);
        });
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = context.userService.login(username, password);
            JOptionPane.showMessageDialog(this, "Welcome back, " + user.getUsername() + "!");
            new DashboardFrame(context).setVisible(true);
            this.dispose();
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
