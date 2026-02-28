/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 *
 * @author UNKNOWN_UN
 */
public class styleDateChooser {

    public static void applyDarkTheme(JDateChooser chooser) {
        JTextFieldDateEditor editor = (JTextFieldDateEditor) chooser.getDateEditor();

        // Text field colors
        editor.setForeground(Color.WHITE);      // text color
        editor.setBackground(new Color(45, 45, 45)); // dark background
        editor.setCaretColor(Color.WHITE);     // caret color
        editor.setBorder(BorderFactory.createLineBorder(new Color(102, 102, 102)));

        // Force white text on selection/change
        editor.addPropertyChangeListener("date", evt -> {
            editor.setForeground(Color.WHITE);
        });

        // Optional: popup calendar colors
        chooser.getJCalendar().setDecorationBackgroundVisible(true);
        chooser.getJCalendar().setBackground(new Color(45, 45, 45));
        chooser.getJCalendar().setForeground(Color.WHITE);
    }

}
