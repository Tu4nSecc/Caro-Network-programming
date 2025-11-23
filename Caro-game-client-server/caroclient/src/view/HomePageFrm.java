package view;

import controller.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class HomePageFrm extends JFrame {

    private JTextArea chatArea;
    private JTextField chatInput;

    public HomePageFrm() {

        setTitle("Caro Game");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 780);

        setIconImage(new ImageIcon("assets/image/caroicon.png").getImage());
        BackgroundPanel bg = new BackgroundPanel("assets/image/background.png");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        // === TITLE ===
        JLabel title = new JLabel("GAME CARO ONLINE", SwingConstants.CENTER);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        title.setForeground(new Color(255, 215, 0));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        bg.add(title, BorderLayout.NORTH);

        // === CENTER FIXED HEIGHT WRAPPER ===
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setPreferredSize(new Dimension(0, 500));
        bg.add(centerWrapper, BorderLayout.CENTER);

        // Center content with BoxLayout
        JPanel mainCenter = new JPanel();
        mainCenter.setOpaque(false);
        mainCenter.setLayout(new BoxLayout(mainCenter, BoxLayout.Y_AXIS));
        centerWrapper.add(mainCenter, BorderLayout.CENTER);

        // === USER PANEL ===
        mainCenter.add(createUserPanel());

        // === CHAT AREA ===
        chatArea = new JTextArea("<<Tin nhắn và tin tức>>\n");
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 16));
        chatArea.setForeground(Color.BLACK);
        chatArea.setOpaque(false); // TextArea không tự có nền

        // Panel nền trắng trong suốt bọc chatArea
        JPanel chatBgPanel = new JPanel(new BorderLayout());
        chatBgPanel.setOpaque(true);
        chatBgPanel.setBackground(new Color(255, 255, 255, 160)); // trắng trong
        chatBgPanel.add(chatArea, BorderLayout.CENTER);

        // ScrollPane chứa panel nền
        JScrollPane scroll = new JScrollPane(chatBgPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        // ⭐ KHUNG CỐ ĐỊNH CHIỀU CAO CHO VÙNG CHAT
        JPanel chatWrapper = new JPanel(new BorderLayout());
        chatWrapper.setOpaque(false);
        chatWrapper.setMinimumSize(new Dimension(0, 150));
        chatWrapper.setPreferredSize(new Dimension(0, 150));
        chatWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        chatWrapper.add(scroll, BorderLayout.CENTER);

        // THÊM KHUNG NÀY VÀO mainCenter (KHÔNG add scroll trực tiếp nữa)
        mainCenter.add(chatWrapper);

        // === CHAT INPUT + SEND ===
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        chatInput = new JTextField();
        chatInput.setFont(new Font("Arial", Font.PLAIN, 16));
        chatInput.addActionListener(e -> sendMessage());

        ImageIcon raw = new ImageIcon(getClass().getResource("/assets/image/send.png"));
        Image scaled = raw.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton btnSend = new JButton(new ImageIcon(scaled));
        btnSend.setBorderPainted(false);
        btnSend.setContentAreaFilled(false);
        btnSend.addActionListener(e -> sendMessage());

        bar.add(chatInput, BorderLayout.CENTER);
        bar.add(btnSend, BorderLayout.EAST);

        mainCenter.add(bar);

        // === MENU ZONE ===
        JPanel menuZone = new JPanel(new BorderLayout());
        menuZone.setOpaque(false);
        menuZone.setPreferredSize(new Dimension(0, 230));
        menuZone.add(createMenuPanel(), BorderLayout.CENTER);

        bg.add(menuZone, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ===========================================
    //           USER PANEL (Header Info)
    // ===========================================
    private JPanel createUserPanel() {

        // === PANEL CONTAINER ===
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 0, 0, 180));   // đen trong suốt
        panel.setOpaque(true);

        // Auto fit width theo frame – chừa 30px 2 bên
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        panel.setPreferredSize(new Dimension(0, 150));

        // ===== AVATAR BÊN TRÁI =====
        JLabel avatar = new JLabel();
        avatar.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon raw = new ImageIcon(getClass().getResource(
                    "/assets/avatar/" + Client.user.getAvatar() + ".jpg"));
            Image scaled = raw.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            avatar.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            System.out.println("Lỗi load avatar: " + e);
        }

        avatar.setPreferredSize(new Dimension(130, 130)); // cố định
        panel.add(avatar, BorderLayout.WEST);

        // ===== INFO PANEL =====
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;

        addVertical(infoPanel, c, 0, 0, "Nickname:", Client.user.getNickname(), 8, 30);
        addVertical(infoPanel, c, 1, 0, "Đã chơi:", "" + Client.user.getNumberOfGame(), 8, 30);
        addVertical(infoPanel, c, 2, 0, "Thắng:", "" + Client.user.getNumberOfwin(), 8, 30);
        addVertical(infoPanel, c, 3, 0, "Hòa:", "" + Client.user.getNumberOfDraw(), 8, 30);

        String rate = Client.user.getNumberOfGame() == 0 ?
                "-" :
                String.format("%.2f %%", Client.user.getNumberOfwin() * 100f / Client.user.getNumberOfGame());

        addVertical(infoPanel, c, 0, 1, "Tỉ lệ thắng:", rate, 8, 30);
        addVertical(infoPanel, c, 1, 1, "Điểm:", "" + (Client.user.getNumberOfGame() + Client.user.getNumberOfwin() * 10), 8, 30);
        addVertical(infoPanel, c, 2, 1, "Thứ hạng:", "" + Client.user.getRank(), 8, 30);

        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private void addVertical(JPanel parent, GridBagConstraints c,
                             int row, int col, String title, String value,
                             int rowGap, int colGap) {

        // TITLE
        JLabel left = new JLabel(title);
        left.setForeground(Color.WHITE);
        left.setFont(new Font("Arial", Font.BOLD, 15));

        c.gridx = col * 2;
        c.gridy = row;
        c.insets = new Insets(0, colGap, rowGap, colGap);
        parent.add(left, c);

        // VALUE
        JLabel right = new JLabel(value);
        right.setForeground(Color.WHITE);
        right.setFont(new Font("Arial", Font.PLAIN, 15));

        c.gridx = col * 2 + 1;
        parent.add(right, c);
    }


    // ===========================================
    //               MENU PANEL
    // ===========================================
    private JPanel createMenuPanel() {

        // Panel chứa 9 nút
        JPanel panel = new JPanel(new GridLayout(3, 3, 25, 15));
        // top, left, bottom, right  → chừa 60px hai bên
        panel.setBorder(BorderFactory.createEmptyBorder(10, 60, 20, 60));
        panel.setOpaque(false);

        panel.add(menuButton("Chơi nhanh",        this::openFindRoom));
        panel.add(menuButton("Vào phòng",         this::openRoomName));
        panel.add(menuButton("Tạo phòng",         this::createRoom));

        panel.add(menuButton("Tìm phòng",         this::openRoomList));
        panel.add(menuButton("Chơi với máy",      this::openAI));
        panel.add(menuButton("Danh sách bạn bè",  this::openFriendList));

        panel.add(menuButton("Bảng xếp hạng",     this::openRank));
        panel.add(menuButton("Đăng xuất",         this::logout));
        panel.add(menuButton("Thoát game",        this::exitGame));

        return panel;
    }

    private JButton menuButton(String text, Runnable event) {

        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);

        try {
            ImageIcon raw = new ImageIcon(getClass().getResource("/assets/image/btn.png"));
            Image scaled = raw.getImage().getScaledInstance(200, 120, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        } catch (Exception ignored) {}

        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);

        // cho Swing biết muốn size tối thiểu thế này (tránh bị bóp nhỏ)
        btn.setPreferredSize(new Dimension(250, 70));

        btn.addActionListener(e -> event.run());

        return btn;
    }



    // ===========================================
    //               BUTTON EVENTS
    // ===========================================
    private void openFindRoom() {
        Client.closeView(Client.View.HOMEPAGE);
        Client.openView(Client.View.FINDROOM);
    }

    private void openRoomName() {
        Client.openView(Client.View.ROOMNAMEFRM);
    }

    private void createRoom() {
        int res = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn đặt mật khẩu cho phòng không?",
                "Tạo phòng", JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
            Client.closeView(Client.View.HOMEPAGE);
            Client.openView(Client.View.CREATEROOMPASSWORD);

        } else if (res == JOptionPane.NO_OPTION) {
            try {
                Client.socketHandle.write("create-room,");
                Client.closeView(Client.View.HOMEPAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void openRoomList() {
        try {
            Client.closeView(Client.View.HOMEPAGE);
            Client.openView(Client.View.ROOMLIST);
            Client.socketHandle.write("view-room-list,");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void openFriendList() {
        Client.closeView(Client.View.HOMEPAGE);
        Client.openView(Client.View.FRIENDLIST);
    }

    private void openAI() {
        Client.openView(Client.View.GAMEAI);
    }

    private void openRank() {
        Client.openView(Client.View.RANK);
    }

    private void logout() {
        try {
            Client.socketHandle.write("offline," + Client.user.getID());
        } catch (Exception ignored) {}
        Client.closeView(Client.View.HOMEPAGE);
        Client.openView(Client.View.LOGIN);
    }

    private void exitGame() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }


    // ===========================================
    //                CHAT FUNCTIONS
    // ===========================================
    private void sendMessage() {
        try {
            if (chatInput.getText().trim().isEmpty())
                throw new Exception("Vui lòng nhập nội dung tin nhắn!");

            chatArea.append("Tôi: " + chatInput.getText() + "\n");
            Client.socketHandle.write("chat-server," + chatInput.getText());
            chatInput.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void addMessage(String msg) {
        chatArea.append(msg + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}
