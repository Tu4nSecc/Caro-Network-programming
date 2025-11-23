package view;

import controller.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class CreateRoomPasswordFrm extends JFrame {

    private JTextField passwordField;
    private JButton createButton;
    private JButton exitButton;

    public CreateRoomPasswordFrm() {
        initFrame();
        initUI();
    }

    // ========================
    // FRAME SETTINGS
    // ========================
    private void initFrame() {
        setTitle("Caro Game");

        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/image/caroicon.png"));
        setIconImage(icon.getImage());

        setSize(320, 220);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ========================
    // UI
    // ========================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(60, 60, 60));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== TITLE =====
        JLabel title = new JLabel("Tạo phòng", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        root.add(title, BorderLayout.NORTH);

        // ===== CENTER FORM =====
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel lbPassword = new JLabel("Nhập mật khẩu:");
        lbPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lbPassword.setForeground(Color.WHITE);

        passwordField = new JTextField(12);

        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0; c.gridy = 0;
        formPanel.add(lbPassword, c);

        c.gridx = 1;
        formPanel.add(passwordField, c);

        root.add(formPanel, BorderLayout.CENTER);

        // ===== BOTTOM BUTTONS =====
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);

        createButton = new JButton("Tạo");
        createButton.addActionListener(this::onCreateRoom);

        exitButton = new JButton();
        exitButton.setIcon(new ImageIcon(getClass().getResource("/assets/icon/door_exit.png")));
        exitButton.setPreferredSize(new Dimension(48, 48));
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(this::onExit);

        bottom.add(createButton);
        bottom.add(Box.createHorizontalStrut(15));
        bottom.add(exitButton);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    // ========================
    // BUTTON EVENTS
    // ========================
    private void onCreateRoom(ActionEvent evt) {
        try {
            String password = passwordField.getText().trim();

            if (password.isEmpty()) {
                throw new Exception("Vui lòng nhập mật khẩu!");
            }

            Client.socketHandle.write("create-room," + password);
            Client.closeView(Client.View.CREATEROOMPASSWORD);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void onExit(ActionEvent evt) {
        Client.closeView(Client.View.CREATEROOMPASSWORD);
        Client.openView(Client.View.HOMEPAGE);
    }
}
