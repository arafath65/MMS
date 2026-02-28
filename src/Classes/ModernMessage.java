package Classes;

import java.awt.Color;
import java.awt.Frame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ModernMessage {

    public static void showMessage(Frame owner, String message) {

        ModernDialog dialog = new ModernDialog(owner, 350, 180);
        JPanel panel = dialog.getContentPanel();

        JLabel lbl = new JLabel(message);
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(40, 50, 260, 30);
        panel.add(lbl);

        GradientButton ok = new GradientButton(
                "OK",
                new Color(0, 102, 204),
                new Color(0, 180, 255)
        );
        ok.setBounds(120, 100, 100, 40);
        ok.addActionListener(e -> dialog.dispose());

        panel.add(ok);

        dialog.setVisible(true);
    }
}
