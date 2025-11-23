package view;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private Image background;

    public BackgroundPanel(String path) {
        try {
            // Load từ classpath
            background = new ImageIcon(
                    getClass().getClassLoader().getResource(path)
            ).getImage();
        } catch (Exception e) {
            System.out.println("Không tìm thấy ảnh: " + path);
            background = null;
        }
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
