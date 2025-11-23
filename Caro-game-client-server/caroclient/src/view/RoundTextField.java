package view;

import java.awt.*;
import javax.swing.JTextField;

public class RoundTextField extends JTextField {

    private final int arc = 20;
    private String placeholder = "";

    public RoundTextField() {
        setOpaque(false);
        setMargin(new Insets(5, 10, 5, 10));
    }

    public void setPlaceholder(String text) {
        placeholder = text;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(210, 235, 245)); // xanh nhạt hơn panel
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        super.paintComponent(g);

        if (getText().isEmpty() && !isFocusOwner()) {
            g2.setColor(Color.GRAY);
            g2.drawString(placeholder, 12, getHeight() / 2 + 5);
        }

        g2.dispose();
    }
}
