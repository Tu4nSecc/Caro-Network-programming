package view;

import controller.Client;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FriendListFrm extends JFrame {

    private JTable friendTable;
    private DefaultTableModel tableModel;

    private List<User> listFriend;

    private boolean isClicked = false;
    private Thread thread;

    public FriendListFrm() {
        initFrame();
        initUI();
        startAutoReload();
    }

    // ==================================
    // FRAME CẤU HÌNH
    // ==================================
    private void initFrame() {
        setTitle("Caro Game");

        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/image/caroicon.png"));
        setIconImage(icon.getImage());

        setSize(450, 520);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ==================================
    // UI CHÍNH
    // ==================================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(60, 60, 60));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ======== HEADER ========
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JButton closeBtn = new JButton("X");
        closeBtn.setFocusPainted(false);
        closeBtn.setBackground(Color.RED);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> {
            Client.closeView(Client.View.FRIENDLIST);
            Client.openView(Client.View.HOMEPAGE);
        });

        JLabel title = new JLabel("Danh sách bạn bè", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        top.add(closeBtn, BorderLayout.WEST);
        top.add(title, BorderLayout.CENTER);

        // ======== TABLE ========
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nickname", ""}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 2) ? ImageIcon.class : String.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        friendTable = new JTable(tableModel);
        friendTable.setRowHeight(60);
        friendTable.setFont(new Font("Tahoma", Font.BOLD, 16));
        friendTable.setSelectionBackground(new Color(170, 200, 255));

        friendTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                onFriendSelected();
            }
        });

        JScrollPane scrollPane = new JScrollPane(friendTable);

        root.add(top, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);

        add(root);
    }

    // ==================================
    // LOAD DANH SÁCH BẠN BÈ
    // ==================================
    public void updateFriendList(List<User> friends) {
        this.listFriend = friends;

        tableModel.setRowCount(0);

        for (User friend : listFriend) {

            ImageIcon icon;

            if (!friend.isIsOnline()) {
                icon = new ImageIcon(getClass().getResource("/assets/icon/offline.png"));
            } else if (friend.isIsPlaying()) {
                icon = new ImageIcon(getClass().getResource("/assets/icon/swords-mini.png"));
            } else {
                icon = new ImageIcon(getClass().getResource("/assets/icon/swords-1-mini.png"));
            }

            tableModel.addRow(new Object[]{
                    String.valueOf(friend.getID()),
                    friend.getNickname(),
                    icon
            });
        }
    }

    // ==================================
    // CLICK VÀO 1 NGƯỜI BẠN
    // ==================================
    private void onFriendSelected() {
        if (friendTable.getSelectedRow() == -1) return;

        try {
            User friend = listFriend.get(friendTable.getSelectedRow());

            if (!friend.isIsOnline()) throw new Exception("Người chơi không online");
            if (friend.isIsPlaying()) throw new Exception("Người chơi đang trong trận đấu");

            isClicked = true;

            int res = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có muốn thách đấu người bạn này không?",
                    "Xác nhận thách đấu",
                    JOptionPane.YES_NO_OPTION
            );

            if (res == JOptionPane.YES_OPTION) {
                Client.closeAllViews();
                Client.openView(Client.View.GAMENOTICE, "Thách đấu", "Đang chờ phản hồi từ đối thủ");
                Client.socketHandle.write("duel-request," + friend.getID());
            } else {
                isClicked = false;
                startAutoReload();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ==================================
    // TỰ ĐỘNG LOAD DANH SÁCH
    // ==================================
    private void startAutoReload() {
        thread = new Thread(() -> {
            while (isDisplayable() && !isClicked) {
                try {
                    Client.socketHandle.write("view-friend-list,");
                    Thread.sleep(500);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void stopAllThread() {
        isClicked = true;
    }
}
