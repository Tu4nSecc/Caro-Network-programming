package view;

import controller.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class FindRoomFrm extends JFrame {

    private Timer timer;
    private boolean isFinded;

    private JLabel timeLabel;
    private JLabel findingLabel;
    private JLabel foundLabel;
    private JLabel loadingGif;

    private JProgressBar progressBar;
    private JButton cancelButton;

    public FindRoomFrm() {
        setTitle("Caro Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/assets/image/caroicon.png")).getImage());

        initUI();

        isFinded = false;
        startFind();
        sendFindRequest();

        setSize(450, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ========================= UI =========================

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // ===== TITLE =====
        JLabel title = new JLabel("Tìm phòng nhanh", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        root.add(title, BorderLayout.NORTH);

        // ===== CENTER BLOCK =====
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        center.setBorder(new EmptyBorder(15, 10, 10, 10));
        root.add(center, BorderLayout.CENTER);

        // Time countdown
        timeLabel = new JLabel("00:20", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(timeLabel);
        center.add(Box.createVerticalStrut(10));

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(70);
        progressBar.setPreferredSize(new Dimension(360, 22));
        progressBar.setMaximumSize(new Dimension(360, 22));
        center.add(progressBar);
        center.add(Box.createVerticalStrut(12));

        // "Đang tìm đối thủ"
        findingLabel = new JLabel("Đang tìm đối thủ", SwingConstants.CENTER);
        findingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        findingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(findingLabel);

        // "Đã tìm thấy"
        foundLabel = new JLabel("Đã tìm thấy đối thủ, đang vào phòng", SwingConstants.CENTER);
        foundLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        foundLabel.setForeground(new Color(0, 80, 200));
        foundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        foundLabel.setVisible(false);
        center.add(foundLabel);

        // Loading GIF
        loadingGif = new JLabel();
        loadingGif.setIcon(new ImageIcon(getClass().getResource("/assets/icon/loading1.gif")));
        loadingGif.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingGif.setVisible(false);
        center.add(loadingGif);

        center.add(Box.createVerticalStrut(20));

        // Cancel button
        cancelButton = new JButton();
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(50, 50));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setBorder(null);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.setIcon(new ImageIcon(getClass().getResource("/assets/icon/door_exit.png")));
        cancelButton.addActionListener(e -> onCancel());
        center.add(cancelButton);
    }

    // ========================= FINDING LOGIC =========================

    private void startFind() {
        foundLabel.setVisible(false);
        loadingGif.setVisible(false);

        timer = new Timer(1000, new AbstractAction() {
            int count = 20;

            @Override
            public void actionPerformed(ActionEvent e) {
                count--;

                if (count >= 0) {
                    if (count >= 10)
                        timeLabel.setText("00:" + count);
                    else
                        timeLabel.setText("00:0" + count);

                    progressBar.setValue(Math.round(count * 5));
                } else {
                    timer.stop();
                    onFindFailed();
                }
            }
        });

        timer.setInitialDelay(0);
        timer.start();
    }

    private void sendFindRequest() {
        try {
            Client.socketHandle.write("quick-room,");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void stopAllThread() {
        if (timer != null) timer.stop();
    }

    public void showFindedRoom() {
        isFinded = true;
        if (timer != null) timer.stop();

        findingLabel.setVisible(false);
        foundLabel.setVisible(true);
        loadingGif.setVisible(true);
    }

    // ========================= ACTIONS =========================

    private void onFindFailed() {
        try {
            Client.socketHandle.write("cancel-room,");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        int res = JOptionPane.showConfirmDialog(
                this,
                "Tìm kiếm thất bại, bạn muốn thử lại lần nữa chứ?",
                "Thông báo",
                JOptionPane.YES_NO_OPTION
        );

        if (res == JOptionPane.YES_OPTION) {
            startFind();
            sendFindRequest();
        } else {
            Client.closeView(Client.View.FINDROOM);
            Client.openView(Client.View.HOMEPAGE);
        }
    }

    private void onCancel() {
        if (isFinded) return;

        try {
            Client.socketHandle.write("cancel-room,");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        stopAllThread();
        Client.closeView(Client.View.FINDROOM);
        Client.openView(Client.View.HOMEPAGE);
    }
}
