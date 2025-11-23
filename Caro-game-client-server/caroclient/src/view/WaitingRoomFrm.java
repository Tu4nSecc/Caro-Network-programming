package view;

import controller.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class WaitingRoomFrm extends JFrame {

    private boolean isOpenning;

    private JLabel roomNameLabel;
    private JLabel passwordLabel;
    private JLabel statusLabel;
    private JLabel loadingGif;
    private JButton exitButton;

    public WaitingRoomFrm() {
        isOpenning = false;
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

        setSize(360, 320);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ============================
    // UI LAYOUT
    // ============================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(60, 60, 60));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== TOP: ROOM HEADER =====
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        roomNameLabel = new JLabel("Phòng {}", SwingConstants.CENTER);
        roomNameLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        roomNameLabel.setForeground(Color.WHITE);

        passwordLabel = new JLabel("Mật khẩu:", SwingConstants.CENTER);
        passwordLabel.setFont(new Font("Tahoma", Font.ITALIC, 14));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setVisible(false);

        topPanel.add(roomNameLabel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(passwordLabel);

        root.add(topPanel, BorderLayout.NORTH);

        // ===== CENTER: LOADING + STATUS =====
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        loadingGif = new JLabel();
        loadingGif.setHorizontalAlignment(SwingConstants.CENTER);
        loadingGif.setIcon(new ImageIcon(getClass().getResource("/assets/icon/loading2.gif")));

        statusLabel = new JLabel("Đang chờ người chơi khác vào phòng", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Tahoma", Font.ITALIC, 14));
        statusLabel.setForeground(Color.WHITE);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(loadingGif);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(statusLabel);

        root.add(centerPanel, BorderLayout.CENTER);

        // ===== BOTTOM: EXIT BUTTON =====
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);

        exitButton = new JButton();
        exitButton.setPreferredSize(new Dimension(50, 50));
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitButton.setIcon(new ImageIcon(getClass().getResource("/assets/icon/door_exit.png")));
        exitButton.addActionListener(this::onExitRoom);

        bottom.add(exitButton);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    // ============================
    // API CHO SERVER
    // ============================
    public void setRoomName(String roomName) {
        roomNameLabel.setText("Phòng " + roomName);
    }

    public void setRoomPassword(String password) {
        passwordLabel.setText("Mật khẩu: " + password);
        passwordLabel.setVisible(true);
    }

    public void showFindedCompetitor() {
        isOpenning = true;
        statusLabel.setText("Đã tìm thấy đối thủ, đang vào phòng");
        statusLabel.setForeground(Color.CYAN);
        exitButton.setVisible(false);
    }

    // ============================
    // BUTTON EVENT
    // ============================
    private void onExitRoom(ActionEvent evt) {
        if (isOpenning) return;

        try {
            Client.closeView(Client.View.WAITINGROOM);
            Client.openView(Client.View.HOMEPAGE);
            Client.socketHandle.write("cancel-room,");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
