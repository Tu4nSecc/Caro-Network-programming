package view;

import controller.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Vector;

public class RoomListFrm extends JFrame {

    private Vector<String> listRoom;
    private Vector<String> listPassword;

    private Thread thread;
    private boolean isPlayThread = true;
    private boolean isFiltered = false;

    private JTable roomTable;
    private DefaultTableModel tableModel;

    public RoomListFrm() {
        initFrame();
        initUI();
        startRoomUpdateThread();
    }

    // ================================
    // INIT FRAME
    // ================================
    private void initFrame() {
        setTitle("Caro Game");
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/image/caroicon.png"));
        setIconImage(icon.getImage());
        setSize(420, 520);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ================================
    // UI LAYOUT
    // ================================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(40, 40, 40));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== TOP BAR =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(60, 60, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton closeBtn = new JButton("X");
        closeBtn.setFocusPainted(false);
        closeBtn.setBackground(Color.RED);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> {
            Client.closeView(Client.View.ROOMLIST);
            Client.openView(Client.View.HOMEPAGE);
        });

        JLabel title = new JLabel("Danh sách phòng trống", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        topPanel.add(closeBtn, BorderLayout.WEST);
        topPanel.add(title, BorderLayout.CENTER);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(new Object[]{"Tên phòng", ""}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 1) ? ImageIcon.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(60);
        roomTable.setFont(new Font("Tahoma", Font.BOLD, 18));
        roomTable.setBackground(new Color(230, 230, 230));
        roomTable.setSelectionBackground(new Color(180, 200, 240));

        roomTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onRoomSelected();
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomTable);

        root.add(topPanel, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);

        add(root);
    }

    // ================================
    // ROOM UPDATE THREAD
    // ================================
    private void startRoomUpdateThread() {
        thread = new Thread(() -> {
            while (isDisplayable() && isPlayThread && !isFiltered) {
                try {
                    Client.socketHandle.write("view-room-list,");
                    Thread.sleep(500);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });
        thread.start();
    }

    // ================================
    // UPDATE TABLE
    // ================================
    public void updateRoomList(Vector<String> listData, Vector<String> listPassword) {
        this.listRoom = listData;
        this.listPassword = listPassword;

        tableModel.setRowCount(0);

        for (int i = 0; i < listRoom.size(); i++) {
            ImageIcon icon;

            if (listPassword.get(i).equals(" ")) {
                icon = new ImageIcon(getClass().getResource("/assets/icon/swords-1-mini.png"));
            } else {
                icon = new ImageIcon(getClass().getResource("/assets/icon/swords-1-lock-mini.png"));
            }

            tableModel.addRow(new Object[]{listRoom.get(i), icon});
        }
    }

    // ================================
    // CLICK EVENT
    // ================================
    private void onRoomSelected() {
        int row = roomTable.getSelectedRow();
        if (row == -1) return;

        try {
            isPlayThread = false;

            int room = Integer.parseInt(listRoom.get(row).split(" ")[1]);
            String password = listPassword.get(row);

            if (password.equals(" ")) {
                Client.socketHandle.write("join-room," + room);
                Client.closeView(Client.View.ROOMLIST);
            } else {
                Client.closeView(Client.View.ROOMLIST);
                Client.openView(Client.View.JOINROOMPASSWORD, room, password);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
