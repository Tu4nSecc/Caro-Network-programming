package view;

import java.awt.*;
import javax.swing.JButton;

public class RoundButton extends JButton {

    private final int arc = 20;
    private Color color = new Color(70, 150, 220);   // xanh đẹp
    private Color hover = new Color(40, 120, 200);

    public RoundButton(String text) {
        super(text);
        setOpaque(false);
        setForeground(Color.WHITE);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                color = hover;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                color = new Color(70, 150, 220);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        super.paintComponent(g);
        g2.dispose();
    }
}
