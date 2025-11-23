package view;

import controller.Room;
import controller.Server;
import controller.ServerThread;
import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Admin extends JFrame implements Runnable {

    private UserDAO userDAO;

    // Giữ nguyên tên biến để tránh lỗi chỗ khác (nếu có dùng trực tiếp)
    private JTextArea jTextArea1;
    public static JTextArea jTextArea2;
    private JTextField jTextField1;  // input thông báo
    private JTextField jTextField3;  // input userID
    private JComboBox<String> jComboBox1;
    private JButton jButton1; // xem danh sách luồng
    private JButton jButton2; // xem danh sách phòng
    private JButton jButton3; // phát thông báo
    private JButton jButton4; // Ban
    private JButton jButton5; // Cảnh cáo
    private JButton jButton6; // Huỷ Ban

    public Admin() {
        userDAO = new UserDAO();

        setTitle("Caro Game - Admin");
        try {
            setIconImage(new ImageIcon(getClass().getResource("/assets/image/caroicon.png")).getImage());
        } catch (Exception ignored) {}
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    // ======================= UI =======================

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 63, 65));

        JLabel title = new JLabel("Bảng điều khiển Admin", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);

        // ===== CENTER: LOG + DANH SÁCH =====
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        root.add(center, BorderLayout.CENTER);

        // ---- KHU VỰC THÔNG TIN SERVER (jTextArea1) ----
        JLabel lblInfo = new JLabel("Thông tin server / phòng / luồng");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(lblInfo);
        center.add(Box.createVerticalStrut(4));

        jTextArea1 = new JTextArea(8, 60);
        jTextArea1.setFont(new Font("Tahoma", Font.PLAIN, 13));
        jTextArea1.setEditable(false);
        JScrollPane scroll1 = new JScrollPane(jTextArea1);
        scroll1.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(scroll1);

        center.add(Box.createVerticalStrut(6));

        JPanel btnRowTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRowTop.setBackground(Color.WHITE);

        jButton1 = new JButton("Xem danh sách luồng");
        jButton2 = new JButton("Xem danh sách phòng");

        jButton1.addActionListener(e -> onViewThreads());
        jButton2.addActionListener(e -> onViewRooms());

        btnRowTop.add(jButton1);
        btnRowTop.add(jButton2);

        btnRowTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(btnRowTop);

        center.add(Box.createVerticalStrut(10));

        // ---- KHU VỰC LOG / THÔNG BÁO (jTextArea2) ----
        JLabel lblLog = new JLabel("Log & thông báo");
        lblLog.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLog.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(lblLog);
        center.add(Box.createVerticalStrut(4));

        jTextArea2 = new JTextArea(8, 60);
        jTextArea2.setFont(new Font("Tahoma", Font.PLAIN, 13));
        jTextArea2.setEditable(false);
        JScrollPane scroll2 = new JScrollPane(jTextArea2);
        scroll2.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(scroll2);

        center.add(Box.createVerticalStrut(6));

        // ---- DÒNG GỬI THÔNG BÁO ----
        JPanel announceRow = new JPanel(new BorderLayout(5, 0));
        announceRow.setBackground(Color.WHITE);
        announceRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        jTextField1 = new JTextField();
        jTextField1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jButton3 = new JButton("Phát thông báo");
        jButton3.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jButton3.addActionListener(e -> onSendBroadcast());

        announceRow.add(jTextField1, BorderLayout.CENTER);
        announceRow.add(jButton3, BorderLayout.EAST);

        center.add(announceRow);

        center.add(Box.createVerticalStrut(10));

        // ---- DÒNG BAN / CẢNH CÁO ----
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottomRow.setBackground(Color.WHITE);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        jTextField3 = new JTextField();
        jTextField3.setPreferredSize(new Dimension(70, 28));
        jTextField3.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        jComboBox1 = new JComboBox<>(new String[] {
                "Chọn lý do",
                "Ngôn ngữ thô tục - xúc phạm người khác",
                "Spam đăng nhập",
                "Sử dụng game với mục đích xấu",
                "Phát hiện rò rỉ bảo mật - tài khoản tạm thời bị khoá để kiểm tra thêm"
        });
        jComboBox1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jComboBox1.setPreferredSize(new Dimension(260, 28));

        jButton6 = new JButton("Huỷ Ban");
        jButton5 = new JButton("Cảnh cáo");
        jButton4 = new JButton("Ban");

        jButton4.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jButton5.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jButton6.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        jButton4.addActionListener(e -> onBanUser());
        jButton5.addActionListener(e -> onWarnUser());
        jButton6.addActionListener(e -> onUnbanUser());

        bottomRow.add(new JLabel("User ID:"));
        bottomRow.add(jTextField3);
        bottomRow.add(jComboBox1);
        bottomRow.add(jButton6);
        bottomRow.add(jButton5);
        bottomRow.add(jButton4);

        center.add(bottomRow);
    }

    // ======================= HÀM XỬ LÝ NÚT =======================

    private void onViewThreads() {
        StringBuilder res = new StringBuilder();
        String room;
        int i = 1;
        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
            if (serverThread.getRoom() == null)
                room = null;
            else
                room = "" + serverThread.getRoom().getID();

            if (serverThread.getUser() != null) {
                res.append(i)
                        .append(". Client-number: ").append(serverThread.getClientNumber())
                        .append(", User-ID: ").append(serverThread.getUser().getID())
                        .append(", Room: ").append(room)
                        .append("\n");
            } else {
                res.append(i)
                        .append(". Client-number: ").append(serverThread.getClientNumber())
                        .append(", User-ID: null")
                        .append(", Room: ").append(room)
                        .append("\n");
            }
            i++;
        }
        jTextArea1.setText(res.toString());
    }

    private void onViewRooms() {
        StringBuilder res = new StringBuilder();
        int i = 1;
        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
            Room room1 = serverThread.getRoom();
            String listUser = "List user ID: ";
            if (room1 != null) {
                if (room1.getNumberOfUser() == 1) {
                    listUser += room1.getUser1().getUser().getID();
                } else {
                    listUser += room1.getUser1().getUser().getID()
                            + ", " + room1.getUser2().getUser().getID();
                }
                res.append(i)
                        .append(". Room_ID: ").append(room1.getID())
                        .append(", Number of player: ").append(room1.getNumberOfUser())
                        .append(", ").append(listUser)
                        .append("\n");
                i++;
            }
        }
        jTextArea1.setText(res.toString());
    }

    private void onSendBroadcast() {
        sendMessage();
    }

    private void onBanUser() {
        try {
            if (jTextField3.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ID của User");
                return;
            }
            if (jComboBox1.getSelectedIndex() < 1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lý do");
                return;
            }
            int userId = Integer.parseInt(jTextField3.getText());
            User user = new User();
            user.setID(userId);
            userDAO.updateBannedStatus(user, true);

            ServerThread serverThread = Server.serverThreadBus.getServerThreadByUserID(userId);
            if (serverThread != null) {
                serverThread.write("banned-notice," + jComboBox1.getSelectedItem());
                if (serverThread.getRoom() != null) {
                    Room room = serverThread.getRoom();
                    ServerThread competitorThread = room.getCompetitor(serverThread.getClientNumber());
                    room.setUsersToNotPlaying();
                    if (competitorThread != null) {
                        room.decreaseNumberOfGame();
                        competitorThread.write("left-room,");
                        competitorThread.setRoom(null);
                    }
                    serverThread.setRoom(null);
                }
                serverThread.setUser(null);
            }

            Server.admin.addMessage("User có ID " + userId + " đã bị BAN");
            Server.serverThreadBus.boardCast(-1,
                    "chat-server," + "User có ID " + userId + " đã bị BAN");
            JOptionPane.showMessageDialog(this, "Đã BAN user " + userId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
        }
    }

    private void onUnbanUser() {
        try {
            if (jTextField3.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ID của User");
                return;
            }
            int userId = Integer.parseInt(jTextField3.getText());
            User user = new User();
            user.setID(userId);
            userDAO.updateBannedStatus(user, false);
            jTextField3.setText("");
            JOptionPane.showMessageDialog(this, "Đã huỷ BAN user " + userId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
        }
    }

    private void onWarnUser() {
        try {
            if (jTextField3.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ID của User");
                return;
            }
            if (jComboBox1.getSelectedIndex() < 1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lý do");
                return;
            }
            int userId = Integer.parseInt(jTextField3.getText());
            Server.serverThreadBus.sendMessageToUserID(userId,
                    "warning-notice," + jComboBox1.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Đã cảnh cáo user " + userId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
        }
    }

    // ======================= HỖ TRỢ GỬI THÔNG BÁO =======================

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == 10) { // Enter
            sendMessage();
        }
    }

    private void sendMessage() {
        String message = jTextField1.getText();
        if (message.length() == 0) return;

        String temp = jTextArea2.getText();
        temp += "Thông báo từ máy chủ : " + message + "\n";
        jTextArea2.setText(temp);
        jTextArea2.setCaretPosition(jTextArea2.getDocument().getLength());

        Server.serverThreadBus.boardCast(-1,
                "chat-server," + "Thông báo từ máy chủ : " + message);

        jTextField1.setText("");
    }

    public void addMessage(String message) {
        String tmp = jTextArea2.getText();
        tmp = tmp + message + "\n";
        jTextArea2.setText(tmp);
        jTextArea2.setCaretPosition(jTextArea2.getDocument().getLength());
    }

    // ======================= Runnable =======================

    @Override
    public void run() {
        new Admin().setVisible(true);
    }
}
