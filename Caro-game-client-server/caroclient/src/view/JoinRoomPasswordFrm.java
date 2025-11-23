package view;

import controller.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JoinRoomPasswordFrm extends JFrame {

    private int room;
    private String password;

    private JLabel titleLabel;
    private JLabel infoLabel;
    private JTextField passwordField;
    private JButton joinButton;
    private JButton exitButton;

    public JoinRoomPasswordFrm(int room, String password) {
        this.room = room;
        this.password = password;

        setTitle("Caro Game");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/assets/image/caroicon.png")).getImage());

        initUI();

        setSize(350, 260);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ============================== UI ==============================

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // ---------- Header ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        // icon thoát
        exitButton = new JButton();
        exitButton.setBackground(Color.WHITE);
        exitButton.setBorder(null);
        exitButton.setFocusPainted(false);
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitButton.setIcon(new ImageIcon(getClass().getResource("/assets/icon/door_exit.png")));
        exitButton.addActionListener(e -> onExit());

        titleLabel = new JLabel("Vào phòng " + room, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(40, 40, 40));

        header.add(exitButton, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // ---------- Center block ----------
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        center.setBorder(new EmptyBorder(20, 20, 10, 20));
        root.add(center, BorderLayout.CENTER);

        infoLabel = new JLabel("Nhập mật khẩu", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(infoLabel);

        center.add(Box.createVerticalStrut(12));

        passwordField = new JTextField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        center.add(passwordField);

        center.add(Box.createVerticalStrut(14));

        joinButton = new JButton("Vào phòng");
        joinButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        joinButton.setFocusPainted(false);
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton.addActionListener(e -> onJoin());
        center.add(joinButton);
    }

    // ============================== Actions ==============================

    private void onJoin() {
        try {
            String input = passwordField.getText();

            if (!input.equals(password)) {
                throw new Exception("Mật khẩu không chính xác");
            }

            Client.socketHandle.write("join-room," + room);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void onExit() {
        Client.closeView(Client.View.JOINROOMPASSWORD);
        Client.openView(Client.View.HOMEPAGE);
    }
}
