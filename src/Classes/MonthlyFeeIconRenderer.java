package Classes;

import java.awt.Component;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

public class MonthlyFeeIconRenderer extends TableGradientCell {

    private Map<String, String> chequeStatusMap;

    private Icon pendingIcon = new ImageIcon(getClass().getResource("/images/pendingorange16.png"));
    private Icon greenIcon = new ImageIcon(getClass().getResource("/images/greencircle.png"));
    private Icon waivedIcon = new ImageIcon(getClass().getResource("/images/waivedgrey16.png"));
    private Icon discountIcon = new ImageIcon(getClass().getResource("/images/discountpurple16.png"));
    private Icon dueIcon = new ImageIcon(getClass().getResource("/images/duered16.png"));

    public MonthlyFeeIconRenderer(Map<String, String> chequeStatusMap) {
        this.chequeStatusMap = chequeStatusMap;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        // 🔥 IMPORTANT UI FIXES
        label.setHorizontalAlignment(JLabel.LEFT);          // align left
        label.setHorizontalTextPosition(JLabel.RIGHT);      // text after icon
        label.setIconTextGap(6);                            // gap between icon & text
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 0)); // left padding

        label.setIcon(null);

        try {

            Object statusObj = table.getValueAt(row, 9);

            if (statusObj == null || statusObj.toString().trim().isEmpty()) {
                return label;
            }

            String status = statusObj.toString();

            int year = Integer.parseInt(table.getValueAt(row, 1).toString());
            String monthName = table.getValueAt(row, 2).toString();

            int monthNo = GeneralMethods.getMonthNumber(monthName);
            String monthFor = year + "-" + String.format("%02d", monthNo);

            String chequeStatus = chequeStatusMap.get(monthFor);

            // 🟡 CHEQUE PRIORITY
            if (chequeStatus != null
                    && (chequeStatus.equalsIgnoreCase("PENDING")
                    || chequeStatus.equalsIgnoreCase("BOUNCED")
                    || chequeStatus.equalsIgnoreCase("RETURNED"))) {

                label.setIcon(pendingIcon);
                return label;
            }

            // 🟢 PAID / PARTIAL
            if ("PAID".equalsIgnoreCase(status) || "PARTIAL".equalsIgnoreCase(status)) {
                label.setIcon(greenIcon);
            } // ⚫ WAIVED / DISCOUNT
            else if ("WAIVED".equalsIgnoreCase(status)) {
                label.setIcon(waivedIcon);
            } else if ("DISCOUNT".equalsIgnoreCase(status)) {
                label.setIcon(discountIcon);
            }else if ("PENDING".equalsIgnoreCase(status)) {
                label.setIcon(dueIcon);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return label;
    }

//    @Override
//    public Component getTableCellRendererComponent(JTable table, Object value,
//            boolean isSelected, boolean hasFocus, int row, int column) {
//
//        JLabel label = (JLabel) super.getTableCellRendererComponent(
//                table, value, isSelected, hasFocus, row, column);
//
//        label.setIcon(null);
//
//        try {
//
//            // =====================================================
//            // 🔥 STATUS COLUMN (index 9 in new table)
//            // =====================================================
//            Object statusObj = table.getValueAt(row, 9);
//
//            if (statusObj == null || statusObj.toString().trim().isEmpty()) {
//                return label; // no icon
//            }
//
//            String status = statusObj.toString();
//
//            // =====================================================
//            // 🔥 BUILD MONTH KEY SAFELY (YYYY-MM)
//            // =====================================================
//            int year = Integer.parseInt(table.getValueAt(row, 1).toString());
//            String monthName = table.getValueAt(row, 2).toString();
//
//            int monthNo = GeneralMethods.getMonthNumber(monthName);
//
//            String monthFor = year + "-" + String.format("%02d", monthNo);
//
//            // =====================================================
//            // 🔥 GET CHEQUE STATUS
//            // =====================================================
//            String chequeStatus = chequeStatusMap.get(monthFor);
//
//            // =====================================================
//            // 🟡 CHEQUE PROBLEM STATES (HIGHEST PRIORITY)
//            // =====================================================
//            if (chequeStatus != null
//                    && (chequeStatus.equalsIgnoreCase("PENDING")
//                    || chequeStatus.equalsIgnoreCase("BOUNCED")
//                    || chequeStatus.equalsIgnoreCase("RETURNED"))) {
//
//                label.setIcon(pendingIcon);
//                label.setIconTextGap(8);
//                return label;
//            }
//
//            // =====================================================
//            // 🟢 NORMAL PAID
//            // =====================================================
//            if ("PAID".equalsIgnoreCase(status) || "PARTIAL".equalsIgnoreCase(status)) {
//                label.setIcon(greenIcon);
//                label.setIconTextGap(8);
//            }
//
//            // =====================================================
//            // ❌ NO ICON for DISCOUNT / WAIVED / PENDING
//            // =====================================================
//            
//            if ("WAIVED".equalsIgnoreCase(status)) {
//                label.setIcon(waivedIcon);
//                label.setIconTextGap(8);
//            }else if ("DISCOUNT".equalsIgnoreCase(status)) {
//                label.setIcon(discountIcon);
//                label.setIconTextGap(8);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return label;
//    }
}
