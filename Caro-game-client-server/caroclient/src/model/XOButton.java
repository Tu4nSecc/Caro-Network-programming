package model;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class XOButton extends JButton {

    private ImageIcon X;
    private ImageIcon O;
    private ImageIcon BLANK;

    public Point point;
    public int value = 0; // 0 = rỗng, 1 = O (AI), 2 = X (người)

    public XOButton(int x, int y) {

        // Load ảnh đúng chuẩn từ resources
        X = new ImageIcon(getClass().getResource("/assets/image/x3.jpg"));
        O = new ImageIcon(getClass().getResource("/assets/image/o3.jpg"));
        BLANK = new ImageIcon(getClass().getResource("/assets/image/blank.jpg"));

        this.point = new Point(x, y);

        // Setup icon ban đầu
        setIcon(BLANK);
        setDisabledIcon(BLANK);

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

    }

    // Gán trạng thái X hoặc O
    public void setState(boolean isUserMove) {
        if (isUserMove) {
            // User = X
            setIcon(X);
            setDisabledIcon(X);
            value = 2;
        } else {
            // AI = O
            setIcon(O);
            setDisabledIcon(O);
            value = 1;
        }
        setEnabled(false); // Sau khi đánh thì disable
    }

    // Reset để chơi ván mới
    public void resetState() {
        value = 0;
        setEnabled(true);
        setIcon(BLANK);
        setDisabledIcon(BLANK);
    }
}
