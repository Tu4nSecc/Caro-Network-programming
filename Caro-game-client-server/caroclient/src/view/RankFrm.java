package view;

import controller.Client;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RankFrm extends JFrame {

    private JTable rankTable;
    private DefaultTableModel tableModel;

    private List<User> listUserStatics;
    private List<String> rankSrc;

    public RankFrm() {
        initFrame();
        initRankIcons();
        initUI();
        requestRankData();
    }

    // ========================================
    // FRAME SETTINGS
    // ========================================
    private void initFrame() {
        setTitle("Caro Game");

        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/image/caroicon.png"));
        setIconImage(icon.getImage());

        setSize(420, 600);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ========================================
    // RANK ICONS
    // ========================================
    private void initRankIcons() {
        rankSrc = new ArrayList<>();
        rankSrc.add("rank-gold");
        rankSrc.add("rank-sliver");
        rankSrc.add("bronze-rank");

        for (int i = 0; i < 5; i++) {
            rankSrc.add("nomal-rank");
        }
    }

    // ========================================
    // UI
    // ========================================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(60, 60, 60));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== TITLE =====
        JLabel title = new JLabel("Bảng xếp hạng", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        root.add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(new Object[]{"Hạng", "Nickname", "Rank"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 2) ? ImageIcon.class : String.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        rankTable = new JTable(tableModel);
        rankTable.setRowHeight(64);
        rankTable.setFont(new Font("Tahoma", Font.BOLD, 16));
        rankTable.setSelectionBackground(new Color(180, 200, 255));

        rankTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onRowSelected();
            }
        });

        JScrollPane scrollPane = new JScrollPane(rankTable);

        root.add(scrollPane, BorderLayout.CENTER);

        add(root);
    }

    // ========================================
    // CLICK EVENT
    // ========================================
    private void onRowSelected() {
        if (rankTable.getSelectedRow() == -1) return;

        int index = rankTable.getSelectedRow();
        User user = listUserStatics.get(index);

        if (user.getID() == Client.user.getID()) {
            JOptionPane.showMessageDialog(this,
                    "Thứ hạng của bạn là " + (index + 1));
            return;
        }

        Client.openView(Client.View.COMPETITORINFO, user);
    }

    // ========================================
    // REQUEST DATA FROM SERVER
    // ========================================
    private void requestRankData() {
        try {
            Client.socketHandle.write("get-rank-charts,");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ========================================
    // UPDATE TABLE
    // ========================================
    public void setDataToTable(List<User> users) {
        this.listUserStatics = users;

        tableModel.setRowCount(0);

        int i = 0;
        for (User user : listUserStatics) {
            ImageIcon rankIcon = new ImageIcon(getClass().getResource(
                    "/assets/icon/" + rankSrc.get(i) + ".png"
            ));

            tableModel.addRow(new Object[]{
                    (i + 1),
                    user.getNickname(),
                    rankIcon
            });

            i++;
        }
    }
}
