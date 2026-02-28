package Classes;

import javax.swing.*;
import java.awt.*;

public class ModernDialog extends JDialog {

    private JPanel contentPanel;

    public ModernDialog(Frame owner, int width, int height) {
        super(owner, true);

        setUndecorated(true);
        setSize(width, height);
        setLayout(null);
        setBackground(new Color(0, 0, 0, 0));
        setLocationRelativeTo(owner);

        contentPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(10, 10, getWidth() - 10, getHeight() - 10, 30, 30);

                // Background
                g2.setColor(Color.decode("#2B2B2B"));
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);

                g2.dispose();
            }
        };

        contentPanel.setOpaque(false);
        contentPanel.setBounds(0, 0, width, height);

        add(contentPanel);
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
