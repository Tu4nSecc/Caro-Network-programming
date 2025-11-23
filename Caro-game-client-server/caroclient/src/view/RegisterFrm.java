package view;

import controller.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;

public class RegisterFrm extends JFrame {

    private RoundPanel mainPanel;

    private RoundTextField txtUsername;
    private RoundPasswordField txtPassword;
    private RoundTextField txtNickname;

    private JLabel lblTitle;
    private JLabel lblLoginLink;

    private RoundButton btnRegister;

    private JComboBox<ImageIcon> avatarCombo;

    public RegisterFrm() {

        // ===== nền background =====
        setContentPane(new BackgroundPanel("assets/image/background.jpeg"));
        setLayout(null);

        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Caro Game");
        setIconImage(new ImageIcon("assets/image/caroicon.png").getImage());

        // load font Ultra
        loadUltraFont();

        initUI();

        setVisible(true);
    }

    private void initUI() {

        // ===== PANEL CHÍNH =====
        mainPanel = new RoundPanel();
        mainPanel.setBounds(300, 70, 420, 500);
        mainPanel.setBackgroundColor(new Color(173, 216, 230));
        mainPanel.setLayout(null);
        add(mainPanel);

        // ===== TIÊU ĐỀ =====
        lblTitle = new JLabel("Đăng Ký");
        lblTitle.setFont(new Font("Ultra", Font.PLAIN, 34));
        lblTitle.setForeground(new Color(40, 70, 120));
        lblTitle.setBounds(140, 20, 300, 40);
        mainPanel.add(lblTitle);

        // ===== USERNAME =====
        txtUsername = new RoundTextField();
        txtUsername.setPlaceholder("Tài khoản...");
        txtUsername.setBounds(80, 100, 260, 40);
        mainPanel.add(txtUsername);

        // ===== PASSWORD =====
        txtPassword = new RoundPasswordField();
        txtPassword.setPlaceholder("Mật khẩu...");
        txtPassword.setBounds(80, 160, 260, 40);
        mainPanel.add(txtPassword);

        // ===== NICKNAME =====
        txtNickname = new RoundTextField();
        txtNickname.setPlaceholder("Nickname...");
        txtNickname.setBounds(80, 220, 260, 40);
        mainPanel.add(txtNickname);

        // ===== AVATAR COMBO =====
        avatarCombo = new JComboBox<>();
        avatarCombo.setBounds(140, 280, 140, 90);

        for (int i = 0; i <= 5; i++) {
            avatarCombo.addItem(new ImageIcon("assets/avatar/" + i + ".jpg"));
        }

        mainPanel.add(avatarCombo);

        // ===== BUTTON ĐĂNG KÝ =====
        btnRegister = new RoundButton("Đăng ký");
        btnRegister.setBounds(130, 390, 160, 45);

        btnRegister.addActionListener(e -> registerAction());
        mainPanel.add(btnRegister);

        // ===== LINK "Đã có tài khoản" =====
        lblLoginLink = new JLabel("<HTML><U>Đã có tài khoản? Đăng nhập</U></HTML>");
        lblLoginLink.setForeground(new Color(20, 20, 200));
        lblLoginLink.setBounds(120, 450, 250, 25);

        lblLoginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Client.closeView(Client.View.REGISTER);
                Client.openView(Client.View.LOGIN);
            }
        });

        mainPanel.add(lblLoginLink);
    }

    // ===== XỬ LÝ ĐĂNG KÝ =====
    private void registerAction() {
        try {
            String username = txtUsername.getText();
            if (username.isEmpty())
                throw new Exception("Vui lòng nhập tên tài khoản");

            String password = new String(txtPassword.getPassword());
            if (password.isEmpty())
                throw new Exception("Vui lòng nhập mật khẩu");

            String nickname = txtNickname.getText();
            if (nickname.isEmpty())
                throw new Exception("Vui lòng nhập nickname");

            int avatar = avatarCombo.getSelectedIndex();
            if (avatar == -1)
                throw new Exception("Vui lòng chọn avatar");

            Client.closeAllViews();
            Client.openView(Client.View.GAMENOTICE,
                    "Đăng ký tài khoản", "Đang chờ phản hồi...");

            Client.socketHandle.write("register," +
                    username + "," +
                    password + "," +
                    nickname + "," +
                    avatar);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ===== LOAD FONT ULTRA =====
    private void loadUltraFont() {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                    new File("assets/fonts/Ultra-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (Exception ignored) {}
    }
}
