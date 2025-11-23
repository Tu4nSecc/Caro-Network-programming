package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.Client;
import model.Point;
import model.XOButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameAIFrm extends JFrame {

    // ===========================
    // GAME CONSTANTS
    // ===========================
    private static final int ROW = 20;
    private static final int COL = 20;
    private static final int WIN_SCORE = 100000000;

    // ===========================
    // BOARD DATA
    // ===========================
    private XOButton[][] boardButtons = new XOButton[COL][ROW];
    private final ArrayList<Point> availablePoints = new ArrayList<>();
    private JLabel youScoreLabel;
    private JLabel aiScoreLabel;
    private int gameCount = 0;
    private int userWins = 0;
    private int aiWins = 0;

    // ===========================
    // UI COMPONENTS
    // ===========================
    private JPanel boardPanel;

    private JLabel nicknameLabel;
    private JLabel userGameCountLabel;
    private JLabel userWinCountLabel;

    private JLabel aiGameCountLabel;
    private JLabel aiWinCountLabel;

    private JLabel scoreLabel; // nếu sau này muốn hiện tổng tỉ số dạng "You: x | AI: y"

    public GameAIFrm() {
        // FlatLaf Look & Feel
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }

        initFrame();
        initMenuBar();
        initUI();
        initBoard();

        gameCount++;
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ==============================
    //   INIT FRAME
    // ==============================
    private void initFrame() {
        setTitle("Caro Game - Chơi với máy");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        ImageIcon appIcon = new ImageIcon(getClass().getResource("/assets/image/caroicon.png"));
        setIconImage(appIcon.getImage());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 243, 248)); // nền kiểu Fluent
    }

    // ==============================
    //   MENU BAR
    // ==============================
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuGame = new JMenu("Menu");

        JMenuItem miNewGame = new JMenuItem("Game mới");
        miNewGame.setAccelerator(KeyStroke.getKeyStroke("ctrl F1"));
        miNewGame.addActionListener(e -> newGame());

        JMenuItem miExit = new JMenuItem("Thoát");
        miExit.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        miExit.addActionListener(e -> dispose());

        menuGame.add(miNewGame);
        menuGame.add(miExit);

        JMenu menuHelp = new JMenu("Help");
        JMenuItem miHelp = new JMenuItem("Trợ giúp");
        miHelp.setAccelerator(KeyStroke.getKeyStroke("ctrl F2"));
        miHelp.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        """
                                Luật chơi: luật quốc tế 5 nước chặn 2 đầu
                                Hai người chơi luân phiên nhau
                                Người chơi trước đánh X, sau đánh O
                                Thời gian 20s mỗi lượt
                                Cầu hoà nếu đối thủ đồng ý
                                Thắng +10 điểm, hoà +5 điểm
                                Chúc bạn chơi vui vẻ!
                                """
                )
        );

        menuHelp.add(miHelp);

        menuBar.add(menuGame);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    // ==============================
    //   UI (Material3 / Fluent)
    // ==============================
    private void initUI() {

        // ===== LEFT SIDEBAR =====
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(270, 600));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(240, 243, 248)); // xám nhạt
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(leftPanel, BorderLayout.WEST);

        Font titleFont = new Font("Segoe UI", Font.BOLD, 13);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 13);

        // === PLAYER PROFILE CARD ===
        JPanel playerCard = createCardPanel();
        JLabel playerTitle = new JLabel("PLAYER PROFILE");
        playerTitle.setFont(titleFont);
        playerTitle.setForeground(new Color(130, 130, 130));

        // icon avatar
        ImageIcon avatarIcon = new ImageIcon(
                getClass().getResource("/assets/avatar/" + Client.user.getAvatar() + ".jpg"));

        JLabel playerName = new JLabel(Client.user.getNickname(), avatarIcon, JLabel.LEFT);
        playerName.setFont(new Font("Segoe UI", Font.BOLD, 15));

        nicknameLabel = playerName;
        userGameCountLabel = new JLabel("Matches Played: " + Client.user.getNumberOfGame());
        userWinCountLabel = new JLabel("Wins: " + Client.user.getNumberOfwin());
        userGameCountLabel.setFont(valueFont);
        userWinCountLabel.setFont(valueFont);

        playerCard.add(playerTitle);
        playerCard.add(Box.createVerticalStrut(10));
        playerCard.add(playerName);
        playerCard.add(Box.createVerticalStrut(5));
        playerCard.add(userGameCountLabel);
        playerCard.add(userWinCountLabel);

        // === OPPONENT CARD ===
        JPanel aiCard = createCardPanel();
        JLabel aiTitle = new JLabel("OPPONENT");
        aiTitle.setFont(titleFont);
        aiTitle.setForeground(new Color(130, 130, 130));

        ImageIcon aiIcon = new ImageIcon(getClass().getResource("/assets/image/ai.png"));
        JLabel aiName = new JLabel("Máy", aiIcon, JLabel.LEFT);
        aiName.setFont(new Font("Segoe UI", Font.BOLD, 15));

        aiGameCountLabel = new JLabel("Matches Played: Many");
        aiWinCountLabel = new JLabel("Wins: Many");
        aiGameCountLabel.setFont(valueFont);
        aiWinCountLabel.setFont(valueFont);

        aiCard.add(aiTitle);
        aiCard.add(Box.createVerticalStrut(10));
        aiCard.add(aiName);
        aiCard.add(Box.createVerticalStrut(5));
        aiCard.add(aiGameCountLabel);
        aiCard.add(aiWinCountLabel);

        // === SCORE CARD ===
        JPanel scoreCard = createCardPanel();
        scoreCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel scoreTitle = new JLabel("SCORE");
        scoreTitle.setFont(titleFont);
        scoreTitle.setForeground(new Color(130, 130, 130));
        scoreTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        scoreCard.add(scoreTitle);
        scoreCard.add(Box.createVerticalStrut(10));

        // ==== Row You ====
        JPanel youRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        youRow.setOpaque(false);
        youRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        ImageIcon xIcon = new ImageIcon(getClass().getResource("/assets/image/x3.jpg"));
        JLabel xIconLabel = new JLabel(xIcon);

        youScoreLabel = new JLabel("You: " + userWins);
        youScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        youScoreLabel.setForeground(new Color(230, 80, 80));

        youRow.add(xIconLabel);
        youRow.add(youScoreLabel);

        // ==== Row AI ====
        JPanel aiRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        aiRow.setOpaque(false);
        aiRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        ImageIcon oIcon = new ImageIcon(getClass().getResource("/assets/image/o3.jpg"));
        JLabel oIconLabel = new JLabel(oIcon);

        aiScoreLabel = new JLabel("AI: " + aiWins);
        aiScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        aiScoreLabel.setForeground(new Color(80, 130, 230));

        aiRow.add(oIconLabel);
        aiRow.add(aiScoreLabel);

        scoreCard.add(youRow);
        scoreCard.add(aiRow);

        // === ADD CARDS TO LEFT PANEL ===
        leftPanel.add(playerCard);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(aiCard);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(scoreCard);
        leftPanel.add(Box.createVerticalGlue());

        // ===== BOARD =====
        boardPanel = new JPanel(new GridLayout(ROW, COL));
        boardPanel.setPreferredSize(new Dimension(600, 600));
        boardPanel.setBackground(new Color(230, 233, 240));
        boardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(boardPanel, BorderLayout.CENTER);

        pack();
    }

    // helper tạo card kiểu Material
    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private String buildScoreText() {
        return "You: " + userWins + "  |  AI: " + aiWins;
    }

    // ==============================
    //   INIT BOARD
    // ==============================
    private void initBoard() {
        for (int i = 0; i < COL; i++) {
            for (int j = 0; j < ROW; j++) {

                Point point = new Point(i, j);
                XOButton btn = new XOButton(i, j);

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (btn.isEnabled()) {
                            handleClick(point);
                        }
                    }
                });

                boardButtons[i][j] = btn;
                boardPanel.add(btn);
                availablePoints.add(point);
            }
        }
    }

    // ==============================
    //   HANDLE CLICK
    // ==============================
    private void handleClick(Point point) {
        boardButtons[point.x][point.y].setState(true);
        boardButtons[point.x][point.y].setEnabled(false);

        // Người thắng?
        if (getScore(getMatrixBoard(), true, false) >= WIN_SCORE) {
            userWins++;
            updateScoreLabel();

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn đã thắng!\nBạn có muốn chơi tiếp không?\n(Bạn sẽ đi trước)",
                    "Chiến thắng!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                startNewMatchUserFirst();  // bạn đi trước
            } else {
                dispose(); // thoát game
            }
            return;
        }

        // Máy đánh
        int[] aiMove = calcNextMove(3);

        if (aiMove != null) {
            boardButtons[aiMove[0]][aiMove[1]].setState(false);
            boardButtons[aiMove[0]][aiMove[1]].setEnabled(false);
        }

        // Máy thắng?
        if (getScore(getMatrixBoard(), false, true) >= WIN_SCORE) {
            aiWins++;
            updateScoreLabel();

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn đã thua!\nBạn có muốn chơi tiếp không?\n(Máy sẽ đi trước)",
                    "Thua cuộc!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                startNewMatchAIFirst();   // máy đi trước
            } else {
                dispose();
            }
        }
    }

    private void updateScoreLabel() {
        youScoreLabel.setText("You: " + userWins);
        aiScoreLabel.setText("AI: " + aiWins);
        // nếu muốn dùng scoreLabel tổng thì:
        // if (scoreLabel != null) scoreLabel.setText(buildScoreText());
    }

    private void startNewMatchUserFirst() {
        // reset board
        for (XOButton[] row : boardButtons) {
            for (XOButton btn : row) {
                btn.resetState();
            }
        }
        JOptionPane.showMessageDialog(this, "Bạn đi trước!");
    }

    private void startNewMatchAIFirst() {
        for (XOButton[] row : boardButtons) {
            for (XOButton btn : row) {
                btn.resetState();
            }
        }

        JOptionPane.showMessageDialog(this, "Máy đi trước!");
        // Máy đánh nước đầu tiên (ở giữa bàn cờ)
        int cx = ROW / 2;
        int cy = COL / 2;
        boardButtons[cx][cy].setState(false);
        boardButtons[cx][cy].setEnabled(false);
    }

    // ==============================
    //   NEW GAME (menu)
    // ==============================
    private void newGame() {
        for (XOButton[] row : boardButtons) {
            for (XOButton btn : row) {
                btn.resetState();
            }
        }

        gameCount++;

        if (gameCount % 2 == 0) {
            JOptionPane.showMessageDialog(this, "Máy đi trước");

            int cx = ROW / 2;
            int cy = COL / 2;
            boardButtons[cx][cy].setState(false);
            boardButtons[cx][cy].setEnabled(false);

            if (getScore(getMatrixBoard(), false, true) >= WIN_SCORE) {
                aiWins++;
                updateScoreLabel();
                newGame();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Bạn đi trước");
        }
    }

    // ======================================================
    // ===================== AI LOGIC =======================
    // ======================================================

    public int[] calcNextMove(int depth) {
        int[][] board = getMatrixBoard();
        Object[] bestMove = searchWinningMove(board);
        Object[] badMove = searchLoseMove(board);

        int[] move = new int[2];

        if (badMove[1] != null && badMove[2] != null) {
            move[0] = (Integer) badMove[1];
            move[1] = (Integer) badMove[2];
            return move;
        }

        if (bestMove[1] != null && bestMove[2] != null) {
            move[0] = (Integer) bestMove[1];
            move[1] = (Integer) bestMove[2];
        } else {
            bestMove = minimaxSearchAB(depth, board, true, -1.0, WIN_SCORE);
            if (bestMove[1] == null) {
                move = null;
            } else {
                move[0] = (Integer) bestMove[1];
                move[1] = (Integer) bestMove[2];
            }
        }
        return move;
    }

    public int[][] playNextMove(int[][] board, int[] move, boolean isUserTurn) {
        int i = move[0], j = move[1];
        int[][] newBoard = new int[ROW][COL];
        for (int h = 0; h < ROW; h++) {
            for (int k = 0; k < COL; k++) {
                newBoard[h][k] = board[h][k];
            }
        }
        newBoard[i][j] = isUserTurn ? 2 : 1;
        return newBoard;
    }

    private Object[] searchWinningMove(int[][] matrix) {
        ArrayList<int[]> allPossibleMoves = generateMoves(matrix);
        Object[] winningMove = new Object[3];

        for (int[] move : allPossibleMoves) {
            int[][] dummyBoard = playNextMove(matrix, move, false);
            if (getScore(dummyBoard, false, false) >= WIN_SCORE) {
                winningMove[1] = move[0];
                winningMove[2] = move[1];
                return winningMove;
            }
        }
        return winningMove;
    }

    private Object[] searchLoseMove(int[][] matrix) {
        ArrayList<int[]> allPossibleMoves = generateMoves(matrix);
        Object[] losingMove = new Object[3];

        for (int[] move : allPossibleMoves) {
            int[][] dummyBoard = playNextMove(matrix, move, true);
            if (getScore(dummyBoard, true, false) >= WIN_SCORE) {
                losingMove[1] = move[0];
                losingMove[2] = move[1];
                return losingMove;
            }
        }
        return losingMove;
    }

    public Object[] minimaxSearchAB(int depth, int[][] board, boolean max, double alpha, double beta) {
        if (depth == 0) {
            return new Object[]{evaluateBoardForWhite(board, !max), null, null};
        }

        ArrayList<int[]> allPossibleMoves = generateMoves(board);

        if (allPossibleMoves.size() == 0) {
            return new Object[]{evaluateBoardForWhite(board, !max), null, null};
        }

        Object[] bestMove = new Object[3];

        if (max) {
            bestMove[0] = -1.0;

            for (int[] move : allPossibleMoves) {
                int[][] dummyBoard = playNextMove(board, move, false);
                Object[] tempMove = minimaxSearchAB(depth - 1, dummyBoard, !max, alpha, beta);

                if ((Double) tempMove[0] > alpha) {
                    alpha = (Double) tempMove[0];
                }
                if ((Double) tempMove[0] >= beta) {
                    return tempMove;
                }
                if ((Double) tempMove[0] > (Double) bestMove[0]) {
                    bestMove = tempMove;
                    bestMove[1] = move[0];
                    bestMove[2] = move[1];
                }
            }
        } else {
            bestMove[0] = 100000000.0;
            bestMove[1] = allPossibleMoves.get(0)[0];
            bestMove[2] = allPossibleMoves.get(0)[1];

            for (int[] move : allPossibleMoves) {
                int[][] dummyBoard = playNextMove(board, move, true);
                Object[] tempMove = minimaxSearchAB(depth - 1, dummyBoard, !max, alpha, beta);

                if ((Double) tempMove[0] < beta) {
                    beta = (Double) tempMove[0];
                }
                if ((Double) tempMove[0] <= alpha) {
                    return tempMove;
                }
                if ((Double) tempMove[0] < (Double) bestMove[0]) {
                    bestMove = tempMove;
                    bestMove[1] = move[0];
                    bestMove[2] = move[1];
                }
            }
        }
        return bestMove;
    }

    public double evaluateBoardForWhite(int[][] board, boolean userTurn) {
        double blackScore = getScore(board, true, userTurn);
        double whiteScore = getScore(board, false, userTurn);

        if (blackScore == 0) blackScore = 1.0;
        return whiteScore / blackScore;
    }

    public ArrayList<int[]> generateMoves(int[][] boardMatrix) {
        ArrayList<int[]> moveList = new ArrayList<>();
        int size = boardMatrix.length;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                if (boardMatrix[i][j] > 0) continue;

                if (i > 0) {
                    if (j > 0) {
                        if (boardMatrix[i - 1][j - 1] > 0 || boardMatrix[i][j - 1] > 0) {
                            moveList.add(new int[]{i, j});
                            continue;
                        }
                    }
                    if (j < size - 1) {
                        if (boardMatrix[i - 1][j + 1] > 0 || boardMatrix[i][j + 1] > 0) {
                            moveList.add(new int[]{i, j});
                            continue;
                        }
                    }
                    if (boardMatrix[i - 1][j] > 0) {
                        moveList.add(new int[]{i, j});
                        continue;
                    }
                }

                if (i < size - 1) {
                    if (j > 0) {
                        if (boardMatrix[i + 1][j - 1] > 0 || boardMatrix[i][j - 1] > 0) {
                            moveList.add(new int[]{i, j});
                            continue;
                        }
                    }
                    if (j < size - 1) {
                        if (boardMatrix[i + 1][j + 1] > 0 || boardMatrix[i][j + 1] > 0) {
                            moveList.add(new int[]{i, j});
                            continue;
                        }
                    }
                    if (boardMatrix[i + 1][j] > 0) {
                        moveList.add(new int[]{i, j});
                    }
                }
            }
        }
        return moveList;
    }

    public int getScore(int[][] board, boolean forX, boolean playersTurn) {
        return evaluateHorizontal(board, forX, playersTurn) +
                evaluateVertical(board, forX, playersTurn) +
                evaluateDiagonal(board, forX, playersTurn);
    }

    public static int evaluateHorizontal(int[][] board, boolean forX, boolean playersTurn) {
        int consecutive = 0, blocks = 2, score = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {

                if (board[i][j] == (forX ? 2 : 1)) {
                    consecutive++;
                } else if (board[i][j] == 0) {
                    if (consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    } else blocks = 1;
                } else if (consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                } else blocks = 2;
            }

            if (consecutive > 0)
                score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);

            consecutive = 0;
            blocks = 2;
        }
        return score;
    }

    public static int evaluateVertical(int[][] board, boolean forX, boolean playersTurn) {
        int consecutive = 0, blocks = 2, score = 0;

        for (int j = 0; j < board[0].length; j++) {
            for (int i = 0; i < board.length; i++) {

                if (board[i][j] == (forX ? 2 : 1)) {
                    consecutive++;
                } else if (board[i][j] == 0) {
                    if (consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    } else blocks = 1;
                } else if (consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                } else blocks = 2;
            }

            if (consecutive > 0)
                score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);

            consecutive = 0;
            blocks = 2;
        }
        return score;
    }

    public static int evaluateDiagonal(int[][] board, boolean forX, boolean playersTurn) {
        int size = board.length;
        int consecutive = 0, blocks = 2, score = 0;

        // Diagonal /
        for (int k = 0; k <= 2 * (size - 1); k++) {
            int iStart = Math.max(0, k - size + 1);
            int iEnd = Math.min(size - 1, k);

            for (int i = iStart; i <= iEnd; ++i) {
                int j = k - i;

                if (board[i][j] == (forX ? 2 : 1)) {
                    consecutive++;
                } else if (board[i][j] == 0) {
                    if (consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    } else blocks = 1;
                } else if (consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                } else blocks = 2;
            }

            if (consecutive > 0)
                score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);

            consecutive = 0;
            blocks = 2;
        }

        // Diagonal \
        for (int k = 1 - size; k < size; k++) {
            int iStart = Math.max(0, k);
            int iEnd = Math.min(size + k - 1, size - 1);

            for (int i = iStart; i <= iEnd; ++i) {
                int j = i - k;

                if (board[i][j] == (forX ? 2 : 1)) {
                    consecutive++;
                } else if (board[i][j] == 0) {
                    if (consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                        consecutive = 0;
                        blocks = 1;
                    } else blocks = 1;
                } else if (consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);
                    consecutive = 0;
                    blocks = 2;
                } else blocks = 2;
            }

            if (consecutive > 0)
                score += getConsecutiveSetScore(consecutive, blocks, forX == playersTurn);

            consecutive = 0;
            blocks = 2;
        }
        return score;
    }

    public static int getConsecutiveSetScore(int count, int blocks, boolean currentTurn) {
        final int WIN_GUARANTEE = 1000000;

        if (blocks == 2 && count <= 5) return 0;

        switch (count) {
            case 5:
                return WIN_SCORE;

            case 4:
                if (currentTurn) return WIN_GUARANTEE;
                else return (blocks == 0) ? WIN_GUARANTEE / 4 : 200;

            case 3:
                if (blocks == 0) return currentTurn ? 50000 : 200;
                else return currentTurn ? 10 : 5;

            case 2:
                if (blocks == 0) return currentTurn ? 7 : 5;
                return 3;

            case 1:
                return 1;
        }
        return WIN_SCORE * 2;
    }

    public int[][] getMatrixBoard() {
        int[][] matrix = new int[ROW][COL];

        for (int i = 0; i < COL; i++) {
            for (int j = 0; j < ROW; j++) {
                matrix[i][j] = boardButtons[i][j].value;
            }
        }
        return matrix;
    }
}
