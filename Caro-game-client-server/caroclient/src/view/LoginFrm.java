package view;

import controller.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class LoginFrm extends JFrame {

    // ===== COMPONENT TỰ TẠO =====
    private RoundPanel mainPanel;
    private RoundTextField txtUsername;
    private RoundPasswordField txtPassword;
    private RoundButton btnLogin;
    private RoundButton btnRegister;
    private JLabel lblTitle;

    public LoginFrm() {

        // ---- Nền background ----
        setContentPane(new BackgroundPanel("assets/image/background.jpeg"));
        setLayout(null);

        setSize(1000, 600); // kích thước window
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Caro Game");
        setIconImage(new ImageIcon("assets/image/caroicon.png").getImage());

        // load font Ultra
        loadUltraFont();

        // ===== TẠO UI =====
        initUI();

        setVisible(true);
    }

    private void initUI() {

        // ================= PANEL CHÍNH ==================
        mainPanel = new RoundPanel();
        mainPanel.setBounds(250, 120, 380, 300);  // nằm giữa ảnh
        mainPanel.setBackgroundColor(new Color(173, 216, 230)); // xanh nhạt
        mainPanel.setLayout(null);
        add(mainPanel);

        // ================= TITLE ==================
        lblTitle = new JLabel("Đăng Nhập");
        lblTitle.setFont(new Font("Ultra", Font.PLAIN, 32));
        lblTitle.setForeground(new Color(40, 70, 120));
        lblTitle.setBounds(100, 20, 250, 40);
        mainPanel.add(lblTitle);

        // ================= INPUT USERNAME ==================
        txtUsername = new RoundTextField();
        txtUsername.setPlaceholder("Tài khoản...");
        txtUsername.setBounds(60, 90, 260, 35);
        mainPanel.add(txtUsername);

        // ================= INPUT MẬT KHẨU ==================
        txtPassword = new RoundPasswordField();
        txtPassword.setPlaceholder("Mật khẩu...");
        txtPassword.setBounds(60, 140, 260, 35);
        mainPanel.add(txtPassword);

        // ================= BUTTON ĐĂNG NHẬP ==================
        btnLogin = new RoundButton("Đăng nhập");
        btnLogin.setBounds(110, 190, 160, 40);
        btnLogin.setForeground(new Color(40, 70, 120));
        btnLogin.addActionListener(e -> loginAction());

        mainPanel.add(btnLogin);

        // ================= BUTTON ĐĂNG KÝ ==================
        btnRegister = new RoundButton("Đăng ký");
        btnRegister.setBounds(110, 240, 160, 40);
        btnRegister.setForeground(new Color(40, 70, 120));
        btnRegister.addActionListener(e -> {
            Client.closeView(Client.View.LOGIN);
            Client.openView(Client.View.REGISTER);
        });

        mainPanel.add(btnRegister);
    }

    // ====== HÀM LOGIN GIỮ NGUYÊN LOGIC ======
    private void loginAction() {
        try {
            String taiKhoan = txtUsername.getText();
            if (taiKhoan.isEmpty()) throw new Exception("Vui lòng nhập tên tài khoản");

            String matKhau = String.valueOf(txtPassword.getPassword());
            if (matKhau.isEmpty()) throw new Exception("Vui lòng nhập mật khẩu");

            Client.closeAllViews();
            Client.openView(Client.View.GAMENOTICE,
                    "Đăng nhập", "Đang xác thực thông tin đăng nhập");

            Client.socketHandle.write("client-verify," + taiKhoan + "," + matKhau);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    // ====== LOAD FONT ULTRA ======
    private void loadUltraFont() {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                    new File("assets/fonts/Ultra-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        } catch (Exception ignored) {}
    }
}
