package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameNoticeFrm extends JFrame {

    private JLabel titleLabel;
    private JLabel messageLabel;
    private JLabel loadingGif;

    public GameNoticeFrm(String title, String message) {
        setTitle("Caro Game");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/assets/image/caroicon.png")).getImage());

        initUI(title, message);

        setSize(360, 280);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ================== UI ==================
    private void initUI(String title, String message) {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // ===== TITLE =====
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(40, 40, 40));
        root.add(titleLabel, BorderLayout.NORTH);

        // ===== CENTER BLOCK =====
        JPanel center = new JPanel();
        center.setBackground(Color.WHITE);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(center, BorderLayout.CENTER);

        // Loading GIF
        loadingGif = new JLabel();
        loadingGif.setIcon(new ImageIcon(getClass().getResource("/assets/icon/loading2.gif")));
        loadingGif.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(loadingGif);

        center.add(Box.createVerticalStrut(10));

        // ===== MESSAGE =====
        messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageLabel.setForeground(new Color(0, 102, 255));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(messageLabel);
    }
}
