package view;

import controller.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class RoomNameFrm extends JFrame {

    private JTextField roomField;
    private JTextField passwordField;
    private JLabel statusLabel;

    public RoomNameFrm() {
        initFrame();
        initUI();
    }

    // ============================
    // FRAME SETTINGS
    // ============================
    private void initFrame() {
        setTitle("Caro Game");

        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/image/caroicon.png"));
        setIconImage(icon.getImage());

        setSize(360, 260);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ============================
    // UI LAYOUT
    // ============================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(60, 60, 60));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== TITLE =====
        JLabel title = new JLabel("Vào phòng", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        root.add(title, BorderLayout.NORTH);

        // ===== FORM PANEL =====
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel roomLabel = new JLabel("Nhập mã phòng:");
        roomLabel.setForeground(Color.WHITE);

        JLabel passwordLabel = new JLabel("Mật khẩu phòng:");
        passwordLabel.setForeground(Color.WHITE);

        roomField = new JTextField(12);
        passwordField = new JTextField(12);

        JLabel note = new JLabel("Nếu phòng không có mật khẩu hãy để trống");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        note.setForeground(Color.LIGHT_GRAY);

        // ==== LAYOUT ====
        c.insets = new Insets(6, 4, 6, 4);
        c.gridx = 0; c.gridy = 0; form.add(roomLabel, c);
        c.gridx = 1; form.add(roomField, c);

        c.gridx = 0; c.gridy = 1; form.add(passwordLabel, c);
        c.gridx = 1; form.add(passwordField, c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; form.add(note, c);

        root.add(form, BorderLayout.CENTER);

        // ===== BOTTOM =====
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);

        JButton joinButton = new JButton("Vào phòng");
        joinButton.addActionListener(this::onJoinRoom);

        statusLabel = new JLabel("Đang tìm kiếm phòng...");
        statusLabel.setForeground(Color.CYAN);
        statusLabel.setVisible(false);

        bottom.add(joinButton);
        bottom.add(statusLabel);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    // ============================
    // JOIN ROOM ACTION
    // ============================
    private void onJoinRoom(ActionEvent evt) {
        String roomName = roomField.getText().trim();

        if (roomName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng!");
            return;
        }

        String password = passwordField.getText().trim();
        if (password.isEmpty()) password = " ";

        try {
            Client.socketHandle.write("go-to-room," + roomName + "," + password);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
