/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author UNKNOWN_UN
 */
public class InstallmentIconRenderer extends TableGradientCell {

//    private Icon chequeIcon = new ImageIcon(getClass().getResource("/images/timer16.png"));
    private Icon chequeIconYellow = new ImageIcon(getClass().getResource("/images/yellowcircle.png"));
    private Icon chequeIconGreen = new ImageIcon(getClass().getResource("/images/greencircle.png"));
    private Icon chequeIconGrey = new ImageIcon(getClass().getResource("/images/greycircle16.png"));
    private Icon chequeIconBlue = new ImageIcon(getClass().getResource("/images/bluecircle16.png"));

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        String paymentMethod = String.valueOf(table.getValueAt(row, 3));
        String paymentType = String.valueOf(table.getValueAt(row, 4));
        String status = String.valueOf(table.getValueAt(row, 5));

        label.setIcon(null);

        // =========================
        // ZERO → GREY
        // =========================
        if ("ZERO".equalsIgnoreCase(paymentType)) {
            label.setIcon(chequeIconGrey);

            // =========================
            // DISCOUNT → BLUE
            // =========================
        } else if ("DISCOUNT".equalsIgnoreCase(paymentType)) {
            label.setIcon(chequeIconBlue);

            // =========================
            // CHEQUE
            // =========================
        } else if ("CHEQUE".equalsIgnoreCase(paymentMethod)) {

            if ("CLEARED".equalsIgnoreCase(status)) {
                label.setIcon(chequeIconGreen);
            } else {
                label.setIcon(chequeIconYellow);
            }

            // =========================
            // CASH / CARD → GREEN
            // =========================
        } else {
            label.setIcon(chequeIconGreen);
        }

        label.setHorizontalTextPosition(SwingConstants.LEFT);

        return label;
    }

//    @Override
//    public Component getTableCellRendererComponent(JTable table, Object value,
//            boolean isSelected, boolean hasFocus, int row, int column) {
//
//        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//
//        String paymentMethod = String.valueOf(table.getValueAt(row, 3));
//        String chequeStatus = String.valueOf(table.getValueAt(row, 4));
//
//        label.setIcon(null);
//
//        if ("CHEQUE".equalsIgnoreCase(paymentMethod) && "PENDING".equalsIgnoreCase(chequeStatus)) {
//            label.setIcon(chequeIcon);
//            label.setIconTextGap(8);
//        }else if ("CASH".equalsIgnoreCase(paymentMethod) || "CARD".equalsIgnoreCase(paymentMethod)) {
//            label.setIcon(chequeIcon2);
//            label.setIconTextGap(8);
//        }
//
//        label.setHorizontalTextPosition(SwingConstants.LEFT);
//
//        return label;
//    }
}
