package com.stocktrading.ui;

import com.stocktrading.exception.UserAlreadyExistsException;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog allowing a new user to register an account with a starting cash balance.
 */
public class RegisterDialog extends JDialog {

    private final AppContext context;
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JTextField startingBalanceField = new JTextField("100000", 16);

    public RegisterDialog(Frame owner, AppContext context) {
        super(owner, "Register New Account", true);
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setSize(380, 260);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Starting Balance:"), gbc);
        gbc.gridx = 1;
        form.add(startingBalanceField, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create Account");
        JButton cancelButton = new JButton("Cancel");
        buttons.add(createButton);
        buttons.add(cancelButton);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);

        createButton.addActionListener(e -> attemptRegister());
        cancelButton.addActionListener(e -> dispose());
    }

    private void attemptRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String balanceText = startingBalanceField.getText().trim();

        double startingBalance;
        try {
            startingBalance = Double.parseDouble(balanceText);
            if (startingBalance < 0) {
                throw new NumberFormatException("negative");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Starting balance must be a valid non-negative number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            context.userService.register(username, password, startingBalance);
            JOptionPane.showMessageDialog(this, "Account created successfully! You can now log in.");
            dispose();
        } catch (UserAlreadyExistsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}
