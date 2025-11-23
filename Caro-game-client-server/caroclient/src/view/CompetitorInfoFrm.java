package view;

import controller.Client;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class CompetitorInfoFrm extends JFrame {

    private boolean isFriend;
    private User user;

    private JLabel avatarLabel;
    private JButton friendButton;
    private JLabel friendStatusLabel;

    private JLabel nicknameValue;
    private JLabel gameValue;
    private JLabel winValue;
    private JLabel drawValue;
    private JLabel winRateValue;
    private JLabel scoreValue;
    private JLabel rankValue;

    public CompetitorInfoFrm(User user) {
        this.user = user;

        setTitle("Caro Game – Thông tin đối thủ");
        setIconImage(new ImageIcon(getClass().getResource("/assets/image/caroicon.png")).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();

        loadUserData();

        setSize(430, 530);
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            Client.socketHandle.write("check-friend," + user.getID());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ========================= UI =========================

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        add(root);

        // ================= TOP TITLE =================
        JLabel title = new JLabel("Thông tin đối thủ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        root.add(title, BorderLayout.NORTH);

        // ================= CENTER =================
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(Color.WHITE);
        root.add(center, BorderLayout.CENTER);

        // ==== LEFT: AVATAR + FRIEND BUTTON ====
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(avatarLabel);

        leftPanel.add(Box.createVerticalStrut(10));

        friendButton = new JButton();
        friendButton.setPreferredSize(new Dimension(120, 40));
        friendButton.setFocusPainted(false);
        friendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        friendButton.addActionListener(e -> onFriendAction());

        leftPanel.add(friendButton);

        center.add(leftPanel, BorderLayout.WEST);

        // ==== RIGHT: INFO BLOCK ====
        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 5, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font valueFont = new Font("Segoe UI", Font.BOLD, 14);

        nicknameValue = addRow(infoPanel, "Nickname:", labelFont, valueFont);
        gameValue = addRow(infoPanel, "Số ván chơi:", labelFont, valueFont);
        winValue = addRow(infoPanel, "Số ván thắng:", labelFont, valueFont);
        drawValue = addRow(infoPanel, "Số ván hòa:", labelFont, valueFont);
        winRateValue = addRow(infoPanel, "Tỉ lệ thắng:", labelFont, valueFont);
        scoreValue = addRow(infoPanel, "Điểm:", labelFont, valueFont);
        rankValue = addRow(infoPanel, "Thứ hạng:", labelFont, valueFont);

        center.add(infoPanel, BorderLayout.CENTER);

        // ================= BOTTOM FRIEND STATUS =================
        friendStatusLabel = new JLabel("", SwingConstants.CENTER);
        friendStatusLabel.setForeground(new Color(0, 102, 204));
        friendStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        root.add(friendStatusLabel, BorderLayout.SOUTH);
    }

    private JLabel addRow(JPanel panel, String label, Font lf, Font vf) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(lf);
        lbl.setForeground(new Color(60, 60, 60));
        panel.add(lbl);

        JLabel value = new JLabel("-");
        value.setFont(vf);
        value.setForeground(new Color(20, 20, 20));
        panel.add(value);

        return value;
    }

    // ========================= DATA =========================

    private void loadUserData() {
        avatarLabel.setIcon(new ImageIcon(getClass().getResource(
                "/assets/avatar/" + user.getAvatar() + ".jpg")));

        nicknameValue.setText(user.getNickname());
        gameValue.setText(String.valueOf(user.getNumberOfGame()));
        winValue.setText(String.valueOf(user.getNumberOfwin()));
        drawValue.setText(String.valueOf(user.getNumberOfDraw()));

        // Win rate
        if (user.getNumberOfGame() == 0) {
            winRateValue.setText("-");
        } else {
            float rate = (float) user.getNumberOfwin() / user.getNumberOfGame() * 100;
            winRateValue.setText(String.format("%.2f%%", rate));
        }

        // Score
        scoreValue.setText(String.valueOf(user.getNumberOfwin() * 10 + user.getNumberOfGame()));

        // Rank
        rankValue.setText(String.valueOf(user.getRank()));
    }

    // ========================= FRIEND ACTION =========================

    private void onFriendAction() {
        try {
            if (isFriend) {
                JOptionPane.showMessageDialog(this, "Bạn và người này đang là bạn bè.");
            } else {
                int res = JOptionPane.showConfirmDialog(this,
                        "Gửi lời mời kết bạn?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);

                if (res == JOptionPane.YES_OPTION) {
                    Client.socketHandle.write("make-friend," + user.getID());
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ====================== SERVER RESPONSE ======================

    public void checkFriend(boolean isFriend) {
        this.isFriend = isFriend;

        if (isFriend) {
            friendButton.setIcon(new ImageIcon(getClass().getResource("/assets/icon/friendship.png")));
            friendStatusLabel.setText("Hai bạn hiện đang là bạn bè");
        } else {
            friendButton.setIcon(new ImageIcon(getClass().getResource("/assets/icon/add-friend.png")));
            friendStatusLabel.setText("Kết bạn để chơi cùng nhau dễ dàng hơn");
        }
    }
}
