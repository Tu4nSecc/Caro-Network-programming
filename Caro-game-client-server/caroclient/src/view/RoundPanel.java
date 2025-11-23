package view;

import java.awt.*;
import javax.swing.JPanel;

public class RoundPanel extends JPanel {

    private Color backgroundColor = new Color(173, 216, 230); // xanh da trời nhạt
    private int cornerRadius = 25;

    public RoundPanel() {
        setOpaque(false);
    }

    public void setBackgroundColor(Color c) {
        backgroundColor = c;
    }

    public void setCornerRadius(int r) {
        cornerRadius = r;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose();
        super.paintComponent(g);
    }
}
