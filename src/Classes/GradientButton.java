package Classes;

import javax.swing.*;
import java.awt.*;

public class GradientButton extends JButton {

    private Color c1;
    private Color c2;

    public GradientButton(String text, Color c1, Color c2) {
        super(text);
        this.c1 = c1;
        this.c2 = c2;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 13));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setSize(getWidth() + 2, getHeight() + 2);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setSize(getWidth() - 2, getHeight() - 2);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(
                0, 0, c1,
                getWidth(), getHeight(), c2
        );

        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2.dispose();
        super.paintComponent(g);
    }
}
