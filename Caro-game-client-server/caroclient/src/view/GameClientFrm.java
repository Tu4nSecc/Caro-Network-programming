package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.Client;
import model.User;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GameClientFrm extends JFrame {

    // =========================
    // GAME DATA
    // =========================
    private final User competitor;
    private final int size = 15;

    private JButton[][] button;
    private int[][] competitorMatrix;
    private int[][] matrix;
    private int[][] userMatrix;

    private int numberOfMatch;
    private int userWin;
    private int competitorWin;

    // timer
    private Timer timer;
    private Integer second, minute;

    // icon paths (classpath)
    private String[] normalItem;
    private String[] winItem;
    private String[] iconItem;
    private String[] preItem;

    private JButton preButton;

    // voice
    private Thread sendThread;
    private boolean isSending;
    private Thread listenThread;
    private boolean isListening;
    private final String competitorIP;

    // =========================
    // UI COMPONENTS
    // =========================
    private JPanel boardPanel;

    private JTextArea chatArea;
    private JTextField chatField;
    private JButton sendButton;

    private JLabel yourTurnLabel;
    private JLabel competitorTurnLabel;
    private JLabel timerLabel;

    private JLabel userNickLabel;
    private JLabel userGameCountLabel;
    private JLabel userWinCountLabel;

    private JLabel competitorNickLabel;
    private JLabel competitorGameCountLabel;
    private JLabel competitorWinCountLabel;

    private JLabel scoreLabel;          // "Tỉ số: x-y"
    private JLabel userXOIconLabel;     // icon X của bạn
    private JLabel competitorXOIconLabel; // icon O của đối thủ

    private JButton drawButton;
    private JButton micButton;
    private JButton speakerButton;

    private JLabel roomLabel;
    private JLabel userAvatarLabel;
    private JLabel vsIconLabel;
    private JButton competitorInfoButton;
    private JProgressBar voiceLevelBar;

    // ======= MENU =======
    private JMenuItem miNewGame;
    private JMenuItem miExit;
    private JMenuItem miHelp;

    // ===== NEON THEME =====
    private static final Color BG_MAIN      = new Color(8, 16, 24);    // nền toàn màn
    private static final Color BG_PANEL     = new Color(16, 32, 48);   // panel trái
    private static final Color BG_BOARD     = new Color(24, 32, 40);   // nền bảng caro
    private static final Color BG_TILE      = new Color(40, 48, 56);   // ô caro
    private static final Color NEON_CYAN    = new Color(0, 220, 255);
    private static final Color NEON_ORANGE  = new Color(255, 160, 0);
    private static final Color TEXT_PRIMARY = new Color(220, 230, 240);

    private static final Font  FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font  FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_CHAT    = new Font("Segoe UI", Font.PLAIN, 13);

    // =========================
    // CONSTRUCTOR
    // =========================
    public GameClientFrm(User competitor, int room_ID, int isStart, String competitorIP) {
        // FlatLaf
        try {
            FlatLightLaf.setup();
        } catch (Exception ignored) {
        }

        this.numberOfMatch = isStart;
        this.competitor = competitor;
        this.competitorIP = competitorIP;

        isSending = false;
        isListening = false;
        userWin = 0;
        competitorWin = 0;

        initFrameBasics();
        initGameArrays();
        initTimer();
        initIcons();
        initMenuBar();
        initUI(room_ID);
        setupButton();

        setEnableButton(true);
        updateScoreLabel();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // =========================
    // INIT FRAME + MENU
    // =========================
    private void initFrameBasics() {
        setTitle("Caro Game");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/image/caroicon.png"));
        setIconImage(icon.getImage());

        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(BG_MAIN);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BG_PANEL);
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

        JMenu menuGame = new JMenu("Menu");
        JMenu menuHelp = new JMenu("Help");
        menuGame.setForeground(TEXT_PRIMARY);
        menuHelp.setForeground(TEXT_PRIMARY);

        miNewGame = new JMenuItem("Game mới");
        miNewGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK));
        miNewGame.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Tính năng đang được phát triển", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE));

        miExit = new JMenuItem("Thoát");
        miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        miExit.addActionListener(e -> exitGame());

        menuGame.add(miNewGame);
        menuGame.add(miExit);

        miHelp = new JMenuItem("Trợ giúp");
        miHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK));
        miHelp.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Luật chơi: luật quốc tế 5 nước chặn 2 đầu\n"
                        + "Hai người chơi luân phiên nhau chơi trước\n"
                        + "Người chơi trước đánh X, người chơi sau đánh O\n"
                        + "Bạn có 20 giây cho mỗi lượt đánh, quá 20 giây bạn sẽ thua\n"
                        + "Khi cầu hòa, nếu đối thủ đồng ý thì ván hiện tại được hủy kết quả\n"
                        + "Với mỗi ván chơi bạn có thêm 1 điểm, nếu hòa bạn được thêm 5 điểm,\n"
                        + "nếu thắng bạn được thêm 10 điểm\n"
                        + "Chúc bạn chơi game vui vẻ"));

        menuHelp.add(miHelp);

        menuBar.add(menuGame);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    // =========================
    // INIT DATA / TIMER / ICONS
    // =========================
    private void initGameArrays() {
        button = new JButton[size][size];
        competitorMatrix = new int[size][size];
        matrix = new int[size][size];
        userMatrix = new int[size][size];
    }

    private void initTimer() {
        second = 60;
        minute = 0;
        timer = new Timer(1000, e -> {
            String mm = minute.toString();
            String ss = second.toString();
            if (mm.length() == 1) mm = "0" + mm;
            if (ss.length() == 1) ss = "0" + ss;

            timerLabel.setText("Thời gian: " + mm + ":" + ss);

            if (second == 0) {
                second = 60;
                minute = 0;
                try {
                    Client.openView(Client.View.GAMECLIENT,
                            "Bạn đã thua do quá thời gian",
                            "Đang thiết lập ván chơi mới");
                    increaseWinMatchToCompetitor();
                    Client.socketHandle.write("lose,");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            } else {
                second--;
            }
        });
    }

    private void initIcons() {
        normalItem = new String[2];
        normalItem[1] = "/assets/image/o2.jpg";
        normalItem[0] = "/assets/image/x2.jpg";

        winItem = new String[2];
        winItem[1] = "/assets/image/owin.jpg";
        winItem[0] = "/assets/image/xwin.jpg";

        iconItem = new String[2];
        iconItem[1] = "/assets/image/o3.jpg";
        iconItem[0] = "/assets/image/x3.jpg";

        preItem = new String[2];
        preItem[1] = "/assets/image/o2_pre.jpg";
        preItem[0] = "/assets/image/x2_pre.jpg";
    }

    // =========================
    // INIT UI (NO NETBEANS)
    // =========================
    private void initUI(int roomId) {
        // ========= RIGHT: BOARD =========
        boardPanel = new JPanel(new GridLayout(size, size, 1, 1));
        boardPanel.setPreferredSize(new Dimension(568, 666));
        boardPanel.setBackground(BG_BOARD);
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 70, 100)),
                new EmptyBorder(4, 4, 4, 4)
        ));
        getContentPane().add(boardPanel, BorderLayout.EAST);

        // ========= LEFT: INFO + CHAT =========
        JPanel leftRoot = new JPanel();
        leftRoot.setLayout(new BorderLayout(0, 8));
        leftRoot.setBorder(new EmptyBorder(8, 8, 8, 8));
        leftRoot.setBackground(BG_MAIN);
        getContentPane().add(leftRoot, BorderLayout.CENTER);

        // ===== TOP: PLAYER & OPPONENT INFO =====
        JPanel topInfo = new JPanel();
        topInfo.setOpaque(false);
        topInfo.setLayout(new BoxLayout(topInfo, BoxLayout.Y_AXIS));

        topInfo.add(buildUserPanel());
        topInfo.add(Box.createVerticalStrut(6));
        topInfo.add(buildCompetitorPanel());
        topInfo.add(Box.createVerticalStrut(6));
        topInfo.add(buildRoomAndVoicePanel());
        topInfo.add(Box.createVerticalStrut(4));
        topInfo.add(buildScoreAndXOPanel());

        leftRoot.add(topInfo, BorderLayout.NORTH);

        // ===== CENTER: CHAT + TURN/TIMER =====
        JPanel centerArea = new JPanel(new BorderLayout(0, 4));
        centerArea.setOpaque(false);

        // turn + timer
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);

        JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        turnPanel.setOpaque(false);

        yourTurnLabel = new JLabel("Đến lượt bạn");
        yourTurnLabel.setForeground(NEON_ORANGE);
        yourTurnLabel.setFont(FONT_LABEL);
        yourTurnLabel.setVisible(false);

        competitorTurnLabel = new JLabel("Đến lượt đối thủ");
        competitorTurnLabel.setForeground(NEON_CYAN);
        competitorTurnLabel.setFont(FONT_LABEL);
        competitorTurnLabel.setVisible(false);

        timerLabel = new JLabel("Thời gian: 00:20");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(Color.RED);
        timerLabel.setFont(FONT_LABEL);
        timerLabel.setVisible(false);

        turnPanel.add(yourTurnLabel);
        turnPanel.add(timerLabel);
        turnPanel.add(competitorTurnLabel);

        statusPanel.add(turnPanel, BorderLayout.CENTER);

        centerArea.add(statusPanel, BorderLayout.NORTH);

        // chat area
        chatArea = new JTextArea();
        chatArea.setFont(FONT_CHAT);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(BG_PANEL);
        chatArea.setForeground(TEXT_PRIMARY);
        chatArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.getViewport().setBackground(BG_PANEL);
        chatScroll.setBorder(BorderFactory.createLineBorder(new Color(40, 80, 110)));

        centerArea.add(chatScroll, BorderLayout.CENTER);

        // chat input
        JPanel chatInputPanel = new JPanel(new BorderLayout(4, 0));
        chatInputPanel.setOpaque(false);

        chatField = new JTextField();
        chatField.setFont(FONT_CHAT);
        chatField.setBackground(new Color(18, 26, 38));
        chatField.setForeground(TEXT_PRIMARY);
        chatField.setCaretColor(TEXT_PRIMARY);
        chatField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 80, 120)),
                new EmptyBorder(2, 4, 2, 4)
        ));
        chatField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendChat();
                }
            }
        });

        sendButton = new JButton();
        sendButton.setPreferredSize(new Dimension(40, 28));
        sendButton.setFocusPainted(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setOpaque(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendChat());
        sendButton.setIcon(new ImageIcon(getClass().getResource("/assets/image/send2.png")));

        chatInputPanel.add(chatField, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        centerArea.add(chatInputPanel, BorderLayout.SOUTH);

        leftRoot.add(centerArea, BorderLayout.CENTER);

        // ===== BOTTOM: DRAW BUTTON =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        drawButton = new JButton("Cầu hòa");
        drawButton.setFocusPainted(false);
        drawButton.setBackground(new Color(60, 80, 110));
        drawButton.setForeground(TEXT_PRIMARY);
        drawButton.setFont(FONT_LABEL);
        drawButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_CYAN),
                new EmptyBorder(4, 16, 4, 16)
        ));
        drawButton.addActionListener(e -> onDrawRequest());

        JPanel drawWrapper = new JPanel();
        drawWrapper.setOpaque(false);
        drawWrapper.add(drawButton);

        bottomPanel.add(drawWrapper, BorderLayout.CENTER);

        leftRoot.add(bottomPanel, BorderLayout.SOUTH);

        // ===== INIT SCORE / TEXTS =====
        roomLabel.setText("Phòng: " + roomId);

        userNickLabel.setText(Client.user.getNickname());
        userGameCountLabel.setText(String.valueOf(Client.user.getNumberOfGame()));
        userWinCountLabel.setText(String.valueOf(Client.user.getNumberOfwin()));

        competitorNickLabel.setText(competitor.getNickname());
        competitorGameCountLabel.setText(String.valueOf(competitor.getNumberOfGame()));
        competitorWinCountLabel.setText(String.valueOf(competitor.getNumberOfwin()));

        scoreLabel.setText("Tỉ số: 0-0");
    }

    // ============= BUILD SUB PANELS =============
    private JPanel buildUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 150, 200)),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel title = new JLabel("Bạn");
        title.setForeground(NEON_CYAN);
        title.setFont(FONT_TITLE);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(3, 2, 4, 4));
        content.setOpaque(false);

        JLabel lblNick = new JLabel("Nickname");
        JLabel lblGame = new JLabel("Số ván chơi");
        JLabel lblWin = new JLabel("Số ván thắng");
        lblNick.setForeground(TEXT_PRIMARY);
        lblGame.setForeground(TEXT_PRIMARY);
        lblWin.setForeground(TEXT_PRIMARY);
        lblNick.setFont(FONT_LABEL);
        lblGame.setFont(FONT_LABEL);
        lblWin.setFont(FONT_LABEL);

        userNickLabel = new JLabel("{nickname}");
        userGameCountLabel = new JLabel("{game}");
        userWinCountLabel = new JLabel("{win}");
        userNickLabel.setForeground(TEXT_PRIMARY);
        userGameCountLabel.setForeground(TEXT_PRIMARY);
        userWinCountLabel.setForeground(TEXT_PRIMARY);
        userNickLabel.setFont(FONT_LABEL);
        userGameCountLabel.setFont(FONT_LABEL);
        userWinCountLabel.setFont(FONT_LABEL);

        content.add(lblNick);
        content.add(userNickLabel);
        content.add(lblGame);
        content.add(userGameCountLabel);
        content.add(lblWin);
        content.add(userWinCountLabel);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildCompetitorPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PANEL);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 150, 200)),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JLabel title = new JLabel("Đối thủ");
        title.setForeground(NEON_ORANGE);
        title.setFont(FONT_TITLE);
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 4, 4));
        infoGrid.setOpaque(false);

        JLabel lblNick = new JLabel("Nickname");
        JLabel lblGame = new JLabel("Số ván chơi");
        JLabel lblWin = new JLabel("Số ván thắng");
        lblNick.setForeground(TEXT_PRIMARY);
        lblGame.setForeground(TEXT_PRIMARY);
        lblWin.setForeground(TEXT_PRIMARY);
        lblNick.setFont(FONT_LABEL);
        lblGame.setFont(FONT_LABEL);
        lblWin.setFont(FONT_LABEL);

        competitorNickLabel = new JLabel("{nickname}");
        competitorGameCountLabel = new JLabel("{game}");
        competitorWinCountLabel = new JLabel("{win}");
        competitorNickLabel.setForeground(TEXT_PRIMARY);
        competitorGameCountLabel.setForeground(TEXT_PRIMARY);
        competitorWinCountLabel.setForeground(TEXT_PRIMARY);
        competitorNickLabel.setFont(FONT_LABEL);
        competitorGameCountLabel.setFont(FONT_LABEL);
        competitorWinCountLabel.setFont(FONT_LABEL);

        infoGrid.add(lblNick);
        infoGrid.add(competitorNickLabel);
        infoGrid.add(lblGame);
        infoGrid.add(competitorGameCountLabel);
        infoGrid.add(lblWin);
        infoGrid.add(competitorWinCountLabel);

        content.add(infoGrid, BorderLayout.CENTER);

        // right column: avatar + sword + btn xem đối thủ
        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBorder(new EmptyBorder(0, 8, 0, 0));

        userAvatarLabel = new JLabel();
        userAvatarLabel.setPreferredSize(new Dimension(60, 60));
        userAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userAvatarLabel.setVerticalAlignment(SwingConstants.CENTER);
        userAvatarLabel.setIcon(new ImageIcon(getClass().getResource(
                "/assets/game/" + Client.user.getAvatar() + ".jpg")));

        vsIconLabel = new JLabel();
        vsIconLabel.setPreferredSize(new Dimension(60, 60));
        vsIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vsIconLabel.setVerticalAlignment(SwingConstants.CENTER);
        vsIconLabel.setIcon(new ImageIcon(getClass().getResource("/assets/game/swords-1.png")));

        competitorInfoButton = new JButton();
        competitorInfoButton.setPreferredSize(new Dimension(60, 60));
        competitorInfoButton.setIcon(new ImageIcon(getClass().getResource(
                "/assets/game/" + competitor.getAvatar() + ".jpg")));
        competitorInfoButton.setToolTipText("Xem thông tin đối thủ");
        competitorInfoButton.setFocusPainted(false);
        competitorInfoButton.setBorder(BorderFactory.createLineBorder(NEON_CYAN));
        competitorInfoButton.addActionListener(e ->
                Client.openView(Client.View.COMPETITORINFO, competitor));

        rightCol.add(userAvatarLabel);
        rightCol.add(Box.createVerticalStrut(6));
        rightCol.add(vsIconLabel);
        rightCol.add(Box.createVerticalStrut(6));
        rightCol.add(competitorInfoButton);

        content.add(rightCol, BorderLayout.EAST);

        root.add(content, BorderLayout.CENTER);

        return root;
    }

    private JPanel buildRoomAndVoicePanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PANEL);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 150, 200)),
                new EmptyBorder(4, 6, 4, 6)
        ));

        roomLabel = new JLabel("{Tên Phòng}");
        roomLabel.setForeground(TEXT_PRIMARY);
        roomLabel.setFont(FONT_LABEL);

        root.add(roomLabel, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        right.setOpaque(false);

        micButton = new JButton();
        micButton.setPreferredSize(new Dimension(30, 30));
        micButton.setIcon(new ImageIcon(getClass().getResource("/assets/game/mute.png")));
        micButton.setToolTipText("Bật mic để nói chuyện cùng nhau");
        micButton.setFocusPainted(false);
        micButton.setContentAreaFilled(false);
        micButton.setBorderPainted(false);
        micButton.addActionListener(e -> onMicToggle());

        speakerButton = new JButton();
        speakerButton.setPreferredSize(new Dimension(30, 30));
        speakerButton.setIcon(new ImageIcon(getClass().getResource("/assets/game/mutespeaker.png")));
        speakerButton.setToolTipText("Âm thanh trò chuyện đang tắt");
        speakerButton.setFocusPainted(false);
        speakerButton.setContentAreaFilled(false);
        speakerButton.setBorderPainted(false);
        speakerButton.addActionListener(e -> onSpeakerToggle());

        right.add(micButton);
        right.add(speakerButton);

        root.add(right, BorderLayout.EAST);

        // voice level bar
        voiceLevelBar = new JProgressBar(0, 100);
        voiceLevelBar.setPreferredSize(new Dimension(200, 4));
        voiceLevelBar.setBorderPainted(false);
        voiceLevelBar.setForeground(NEON_CYAN);
        voiceLevelBar.setBackground(new Color(20, 30, 40));
        root.add(voiceLevelBar, BorderLayout.SOUTH);

        return root;
    }

    private JPanel buildScoreAndXOPanel() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(4, 0, 4, 0));

        // score line
        scoreLabel = new JLabel("Tỉ số: 0-0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setForeground(TEXT_PRIMARY);
        scoreLabel.setFont(FONT_TITLE);

        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        scoreRow.setOpaque(false);

        userXOIconLabel = new JLabel();
        userXOIconLabel.setPreferredSize(new Dimension(28, 28));
        userXOIconLabel.setIcon(new ImageIcon(getClass().getResource(iconItem[numberOfMatch % 2])));

        competitorXOIconLabel = new JLabel();
        competitorXOIconLabel.setPreferredSize(new Dimension(28, 28));
        competitorXOIconLabel.setIcon(new ImageIcon(getClass().getResource(
                iconItem[not(numberOfMatch % 2)])));

        scoreRow.add(userXOIconLabel);
        scoreRow.add(scoreLabel);
        scoreRow.add(competitorXOIconLabel);

        root.add(scoreRow);

        return root;
    }

    // =========================
    // CHAT
    // =========================
    private void sendChat() {
        try {
            if (chatField.getText().isEmpty()) {
                return;
            }
            String temp = chatArea.getText();
            temp += "Tôi: " + chatField.getText() + "\n";
            chatArea.setText(temp);
            Client.socketHandle.write("chat," + chatField.getText());
            chatField.setText("");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public void addMessage(String message) {
        String temp = chatArea.getText();
        temp += competitor.getNickname() + ": " + message + "\n";
        chatArea.setText(temp);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void addVoiceMessage(String message) {
        String temp = chatArea.getText();
        temp += competitor.getNickname() + " " + message + "\n";
        chatArea.setText(temp);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // =========================
    // EXIT / DRAW / TURN
    // =========================
    public void exitGame() {
        try {
            timer.stop();
            voiceCloseMic();
            voiceStopListening();
            Client.socketHandle.write("left-room,");
            Client.closeAllViews();
            Client.openView(Client.View.HOMEPAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void onDrawRequest() {
        try {
            int res = JOptionPane.showConfirmDialog(this,
                    "Bạn có thực sự muốn cầu hòa ván chơi này",
                    "Yêu cầu cầu hòa",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                Client.socketHandle.write("draw-request,");
                timer.stop();
                setEnableButton(false);
                Client.openView(Client.View.GAMENOTICE,
                        "Yêu cầu hòa",
                        "Đang chờ phản hồi từ đối thủ");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public void displayDrawRefuse() {
        JOptionPane.showMessageDialog(this,
                "Đối thủ không chấp nhận hòa, mời bạn chơi tiếp");
        timer.start();
        setEnableButton(true);
    }

    public void displayCompetitorTurn() {
        timerLabel.setVisible(false);
        competitorTurnLabel.setVisible(true);
        yourTurnLabel.setVisible(false);
        drawButton.setVisible(false);
    }

    public void displayUserTurn() {
        timerLabel.setVisible(false);
        competitorTurnLabel.setVisible(false);
        yourTurnLabel.setVisible(true);
        drawButton.setVisible(true);
    }

    public void startTimer() {
        timerLabel.setVisible(true);
        second = 60;
        minute = 0;
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
    }

    // =========================
    // SOUND
    // =========================
    public void playSound() {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(
                    new File("assets/sound/click.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playSound1() {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(
                    new File("assets/sound/1click.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playSound2() {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(
                    new File("assets/sound/win.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // =========================
    // BUTTON / BOARD
    // =========================
    int not(int i) {
        return (i == 1) ? 0 : 1;
    }

    void setupButton() {
        boardPanel.removeAll();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final int a = i, b = j;

                JButton btn = new JButton();
                btn.setBackground(BG_TILE);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setIcon(new ImageIcon(getClass().getResource("/assets/image/blank.jpg")));
                btn.setDisabledIcon(new ImageIcon(getClass().getResource("/assets/image/border.jpg")));

                btn.addActionListener(e -> {
                    try {
                        btn.setDisabledIcon(new ImageIcon(
                                getClass().getResource(normalItem[not(numberOfMatch % 2)])));
                        btn.setEnabled(false);
                        playSound();
                        second = 60;
                        minute = 0;
                        matrix[a][b] = 1;
                        userMatrix[a][b] = 1;

                        if (checkRowWin() == 1 || checkColumnWin() == 1
                                || checkRightCrossWin() == 1 || checkLeftCrossWin() == 1) {
                            setEnableButton(false);
                            increaseWinMatchToUser();
                            Client.openView(Client.View.GAMENOTICE,
                                    "Bạn đã thắng",
                                    "Đang thiết lập ván chơi mới");
                            Client.socketHandle.write("win," + a + "," + b);
                        } else {
                            Client.socketHandle.write("caro," + a + "," + b);
                            displayCompetitorTurn();
                        }

                        setEnableButton(false);
                        timer.stop();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }
                });

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (btn.isEnabled()) {
                            btn.setBackground(new Color(60, 90, 120));
                            btn.setIcon(new ImageIcon(
                                    getClass().getResource(normalItem[not(numberOfMatch % 2)])));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (btn.isEnabled()) {
                            btn.setBackground(BG_TILE);
                            btn.setIcon(new ImageIcon(
                                    getClass().getResource("/assets/image/blank.jpg")));
                        }
                    }
                });

                button[a][b] = btn;
                boardPanel.add(btn);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public void setEnableButton(boolean b) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] == 0) {
                    button[i][j].setEnabled(b);
                }
            }
        }
    }

    public void blockgame() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                button[i][j].setBackground(BG_TILE);
                button[i][j].setDisabledIcon(new ImageIcon(
                        getClass().getResource("/assets/image/border.jpg")));
                button[i][j].setText("");
                competitorMatrix[i][j] = 0;
                matrix[i][j] = 0;
            }
        }
        timer.stop();
        setEnableButton(false);
        drawButton.setVisible(false);
    }

    // =========================
    // TURN / GAME FLOW (SERVER)
    // =========================
    public void addCompetitorMove(String x, String y) {
        displayUserTurn();
        startTimer();
        setEnableButton(true);
        caro(x, y);
    }

    public void setLose(String xx, String yy) {
        caro(xx, yy);
    }

    public void newgame() {
        if (numberOfMatch % 2 == 0) {
            JOptionPane.showMessageDialog(this, "Đến lượt bạn đi trước");
            startTimer();
            displayUserTurn();
        } else {
            JOptionPane.showMessageDialog(this, "Đối thủ đi trước");
            displayCompetitorTurn();
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                button[i][j].setIcon(new ImageIcon(
                        getClass().getResource("/assets/image/blank.jpg")));
                button[i][j].setDisabledIcon(new ImageIcon(
                        getClass().getResource("/assets/image/border.jpg")));
                competitorMatrix[i][j] = 0;
                matrix[i][j] = 0;
                userMatrix[i][j] = 0;
            }
        }

        setEnableButton(true);
        if (numberOfMatch % 2 != 0) {
            blockgame();
        }

        userXOIconLabel.setIcon(new ImageIcon(
                getClass().getResource(iconItem[numberOfMatch % 2])));
        competitorXOIconLabel.setIcon(new ImageIcon(
                getClass().getResource(iconItem[not(numberOfMatch % 2)])));

        preButton = null;
        numberOfMatch++;
    }

    public void updateNumberOfGame() {
        competitor.setNumberOfGame(competitor.getNumberOfGame() + 1);
        competitorGameCountLabel.setText(String.valueOf(competitor.getNumberOfGame()));

        Client.user.setNumberOfGame(Client.user.getNumberOfGame() + 1);
        userGameCountLabel.setText(String.valueOf(Client.user.getNumberOfGame()));
    }

    // =========================
    // SCORE
    // =========================
    private void updateScoreLabel() {
        scoreLabel.setText("Tỉ số: " + userWin + "-" + competitorWin);
    }

    public void increaseWinMatchToUser() {
        Client.user.setNumberOfwin(Client.user.getNumberOfwin() + 1);
        userWinCountLabel.setText(String.valueOf(Client.user.getNumberOfwin()));

        userWin++;
        updateScoreLabel();

        String tmp = chatArea.getText();
        tmp += "--Bạn đã thắng, tỉ số hiện tại là " + userWin + "-" + competitorWin + "--\n";
        chatArea.setText(tmp);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void increaseWinMatchToCompetitor() {
        competitor.setNumberOfwin(competitor.getNumberOfwin() + 1);
        competitorWinCountLabel.setText(String.valueOf(competitor.getNumberOfwin()));

        competitorWin++;
        updateScoreLabel();

        String tmp = chatArea.getText();
        tmp += "--Bạn đã thua, tỉ số hiện tại là " + userWin + "-" + competitorWin + "--\n";
        chatArea.setText(tmp);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void displayDrawGame() {
        String tmp = chatArea.getText();
        tmp += "--Ván chơi hòa--\n";
        chatArea.setText(tmp);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void showDrawRequest() {
        int res = JOptionPane.showConfirmDialog(this,
                "Đối thử muốn cầu hóa ván này, bạn đồng ý chứ",
                "Yêu cầu cầu hòa",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                timer.stop();
                setEnableButton(false);
                Client.socketHandle.write("draw-confirm,");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else {
            try {
                Client.socketHandle.write("draw-refuse,");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    // =========================
    // VOICE
    // =========================
    private void onMicToggle() {
        if (isSending) {
            try {
                Client.socketHandle.write("voice-message,close-mic");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
            }
            micButton.setIcon(new ImageIcon(
                    getClass().getResource("/assets/game/mute.png")));
            voiceCloseMic();
            micButton.setToolTipText("Mic đang tắt");
        } else {
            try {
                Client.socketHandle.write("voice-message,open-mic");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
            }
            micButton.setIcon(new ImageIcon(
                    getClass().getResource("/assets/game/88634.png")));
            voiceOpenMic();
            micButton.setToolTipText("Mic đang bật");
        }
    }

    private void onSpeakerToggle() {
        if (isListening) {
            try {
                Client.socketHandle.write("voice-message,close-speaker");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
            }
            speakerButton.setIcon(new ImageIcon(
                    getClass().getResource("/assets/game/mutespeaker.png")));
            voiceStopListening();
            speakerButton.setToolTipText("Âm thanh trò chuyện đang tắt");
        } else {
            try {
                Client.socketHandle.write("voice-message,open-speaker");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
            }
            voiceListening();
            speakerButton.setIcon(new ImageIcon(
                    getClass().getResource("/assets/game/speaker.png")));
            speakerButton.setToolTipText("Âm thanh trò chuyện đang bật");
        }
    }

    public void voiceOpenMic() {
        sendThread = new Thread(() -> {
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
            try {
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int numBytesRead;
                int CHUNK_SIZE = 1024;
                byte[] data = new byte[microphone.getBufferSize() / 5];
                microphone.start();

                InetAddress address = InetAddress.getByName(competitorIP);
                DatagramSocket socket = new DatagramSocket();

                isSending = true;
                while (isSending) {
                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    out.write(data, 0, numBytesRead);
                    DatagramPacket request = new DatagramPacket(data, numBytesRead, address, 5555);
                    socket.send(request);
                }
                out.close();
                socket.close();
                microphone.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sendThread.start();
    }

    public void voiceCloseMic() {
        isSending = false;

        try {
            if (sendThread != null && sendThread.isAlive()) {
                sendThread.interrupt();
            }
        } catch (Exception ignored) {}
    }

    public void stopAllThread() {
        try {
            if (timer != null) {
                timer.stop();
            }

            voiceCloseMic();
            voiceStopListening();

            isSending = false;
            isListening = false;

        } catch (Exception ignored) {}
    }

    public void voiceListening() {
        listenThread = new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(info);
                speakers.open(format);
                speakers.start();

                DatagramSocket serverSocket = new DatagramSocket(5555);
                isListening = true;

                while (isListening) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(response);
                    speakers.write(response.getData(), 0, response.getData().length);
                    voiceLevelBar.setValue((int) volumeRMS(response.getData()));
                }
                speakers.close();
                serverSocket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        listenThread.start();
    }

    public void voiceStopListening() {
        isListening = false;

        try {
            if (listenThread != null && listenThread.isAlive()) {
                listenThread.interrupt();
            }
        } catch (Exception ignored) {}
    }

    public double volumeRMS(byte[] raw) {
        double sum = 0d;
        if (raw.length == 0) return sum;
        for (byte b : raw) sum += b;
        double average = sum / raw.length;

        double sumMeanSquare = 0d;
        for (byte b : raw) sumMeanSquare += Math.pow(b - average, 2d);
        double averageMeanSquare = sumMeanSquare / raw.length;
        return Math.sqrt(averageMeanSquare);
    }

    // =========================
    // CHECK WIN LOGIC (KEEP)
    // =========================
    public int checkRow() {
        int win = 0, hang = 0;
        boolean check = false;
        List<JButton> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (check) {
                    if (competitorMatrix[i][j] == 1) {
                        hang++;
                        list.add(button[i][j]);
                        if (hang > 4) {
                            for (JButton jb : list) {
                                jb.setDisabledIcon(new ImageIcon(
                                        getClass().getResource(winItem[numberOfMatch % 2])));
                            }
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        list = new ArrayList<>();
                        check = false;
                        hang = 0;
                    }
                }
                if (competitorMatrix[i][j] == 1) {
                    check = true;
                    list.add(button[i][j]);
                    hang++;
                } else {
                    list = new ArrayList<>();
                    check = false;
                }
            }
            list = new ArrayList<>();
            hang = 0;
        }
        return win;
    }

    public int checkColumn() {
        int win = 0, cot = 0;
        boolean check = false;
        List<JButton> list = new ArrayList<>();
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                if (check) {
                    if (competitorMatrix[i][j] == 1) {
                        cot++;
                        list.add(button[i][j]);
                        if (cot > 4) {
                            for (JButton jb : list) {
                                jb.setDisabledIcon(new ImageIcon(
                                        getClass().getResource(winItem[numberOfMatch % 2])));
                            }
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        check = false;
                        cot = 0;
                        list = new ArrayList<>();
                    }
                }
                if (competitorMatrix[i][j] == 1) {
                    check = true;
                    list.add(button[i][j]);
                    cot++;
                } else {
                    check = false;
                }
            }
            list = new ArrayList<>();
            cot = 0;
        }
        return win;
    }

    public int checkRightCross() {
        int win = 0, cheop = 0, n = 0;
        boolean check = false;
        List<JButton> list = new ArrayList<>();
        for (int i = size - 1; i >= 0; i--) {
            for (int j = 0; j < size; j++) {
                if (check) {
                    if (n - j >= 0 && competitorMatrix[n - j][j] == 1) {
                        cheop++;
                        list.add(button[n - j][j]);
                        if (cheop > 4) {
                            for (JButton jb : list) {
                                jb.setDisabledIcon(new ImageIcon(
                                        getClass().getResource(winItem[numberOfMatch % 2])));
                            }
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        list = new ArrayList<>();
                        check = false;
                        cheop = 0;
                    }
                }
                if (competitorMatrix[i][j] == 1) {
                    n = i + j;
                    check = true;
                    list.add(button[i][j]);
                    cheop++;
                } else {
                    check = false;
                    list = new ArrayList<>();
                }
            }
            cheop = 0;
            check = false;
            list = new ArrayList<>();
        }
        return win;
    }

    public int checkLeftCross() {
        int win = 0, cheot = 0, n = 0;
        boolean check = false;
        List<JButton> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = size - 1; j >= 0; j--) {
                if (check) {
                    if (n - j - 2 * cheot >= 0 && competitorMatrix[n - j - 2 * cheot][j] == 1) {
                        list.add(button[n - j - 2 * cheot][j]);
                        cheot++;
                        if (cheot > 4) {
                            for (JButton jb : list) {
                                jb.setDisabledIcon(new ImageIcon(
                                        getClass().getResource(winItem[numberOfMatch % 2])));
                            }
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        list = new ArrayList<>();
                        check = false;
                        cheot = 0;
                    }
                }
                if (competitorMatrix[i][j] == 1) {
                    list.add(button[i][j]);
                    n = i + j;
                    check = true;
                    cheot++;
                } else {
                    check = false;
                }
            }
            list = new ArrayList<>();
            n = 0;
            cheot = 0;
            check = false;
        }
        return win;
    }

    public int checkRowWin() {
        int win = 0, hang = 0;
        boolean check = false;
        List<JButton> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (check) {
                    if (userMatrix[i][j] == 1) {
                        hang++;
                        list.add(button[i][j]);
                        if (hang > 4) {
                            for (JButton jb : list) {
                                jb.setDisabledIcon(new ImageIcon(
                                        getClass().getResource(winItem[not(numberOfMatch % 2)])));
                            }
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        list = new ArrayList<>();
                        check = false;
                        hang = 0;
                    }
                }
                if (userMatrix[i][j] == 1) {
                    check = true;
                    list.add(button[i][j]);
                    hang++;
                } else {
                    list = new ArrayList<>();
                    check = false;
                }
            }
            list = new ArrayList<>();
            hang = 0;
        }
        return win;
    }

    public int checkColumnWin() {
        int win = 0, cot = 0;
        boolean check = false;
        List<JButton> list = new ArrayList<>();
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                if (check) {
                    if (userMatrix[i][j] == 1) {
                        cot++;
                        list.add(button[i][j]);
                        if (cot > 4) {
                            for (JButton jb : list) {
                                jb.setDisabledIcon(new ImageIcon(
                                        getClass().getResource(winItem[not(numberOfMatch % 2)])));
                            }
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        check = false;
                        cot = 0;
                        list = new ArrayList<>();
                    }
                }
                if (userMatrix[i][j] == 1) {
                    check = true;
                    list.add(button[i][j]);
                    cot++;
                } else {
                    check = false;
                }
            }
            list = new ArrayList<>();
            cot = 0;
        }
        return win;
    }

    public int checkRightCrossWin() {
        int win = 0, cheop = 0, n = 0;
        boolean check = false;
        List<JButton> list = new ArrayList<>();
        for (int i = size - 1; i >= 0; i--) {
            for (int j = 0; j < size; j++) {
                if (check) {
                    if (n >= j && userMatrix[n - j][j] == 1) {
                        cheop++;
                        list.add(button[n - j][j]);
                        if (cheop > 4) {
                            for (JButton jb : list) {
                                jb.setDisabledIcon(new ImageIcon(
                                        getClass().getResource(winItem[not(numberOfMatch % 2)])));
                            }
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        list = new ArrayList<>();
                        check = false;
                        cheop = 0;
                    }
                }
                if (userMatrix[i][j] == 1) {
                    n = i + j;
                    check = true;
                    list.add(button[i][j]);
                    cheop++;
                } else {
                    check = false;
                    list = new ArrayList<>();
                }
            }
            cheop = 0;
            check = false;
            list = new ArrayList<>();
        }
        return win;
    }

    public int checkLeftCrossWin() {
        int win = 0, cheot = 0, n = 0;
        boolean check = false;
        List<JButton> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = size - 1; j >= 0; j--) {
                if (check) {
                    if (n - j - 2 * cheot >= 0 && userMatrix[n - j - 2 * cheot][j] == 1) {
                        list.add(button[n - j - 2 * cheot][j]);
                        cheot++;
                        if (cheot > 4) {
                            for (JButton jb : list) {
                                jb.setDisabledIcon(new ImageIcon(
                                        getClass().getResource(winItem[not(numberOfMatch % 2)])));
                            }
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        list = new ArrayList<>();
                        check = false;
                        cheot = 0;
                    }
                }
                if (userMatrix[i][j] == 1) {
                    list.add(button[i][j]);
                    n = i + j;
                    check = true;
                    cheot++;
                } else {
                    check = false;
                }
            }
            list = new ArrayList<>();
            n = 0;
            cheot = 0;
            check = false;
        }
        return win;
    }

    public void caro(String x, String y) {
        int xx = Integer.parseInt(x);
        int yy = Integer.parseInt(y);

        competitorMatrix[xx][yy] = 1;
        matrix[xx][yy] = 1;
        button[xx][yy].setEnabled(false);
        playSound1();

        if (preButton != null) {
            preButton.setDisabledIcon(new ImageIcon(
                    getClass().getResource(normalItem[numberOfMatch % 2])));
        }
        preButton = button[xx][yy];
        preButton.setDisabledIcon(new ImageIcon(
                getClass().getResource(preItem[numberOfMatch % 2])));

        if (checkRow() == 1 || checkColumn() == 1
                || checkLeftCross() == 1 || checkRightCross() == 1) {
            timer.stop();
            setEnableButton(false);
            increaseWinMatchToCompetitor();
            Client.openView(Client.View.GAMENOTICE,
                    "Bạn đã thua",
                    "Đang thiết lập ván chơi mới");
        }
    }

}
