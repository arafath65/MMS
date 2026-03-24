package Classes;

import java.awt.Component;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class MonthlyFeeIconRenderer extends TableGradientCell {

    private Map<String, String> chequeStatusMap;

    private Icon pendingIcon = new ImageIcon(getClass().getResource("/images/yellowcircle.png"));
    private Icon greenIcon = new ImageIcon(getClass().getResource("/images/greencircle.png"));

    public MonthlyFeeIconRenderer(Map<String, String> chequeStatusMap) {
        this.chequeStatusMap = chequeStatusMap;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        label.setIcon(null);

        try {
            // ============================
            // 🔥 CHECK STATUS COLUMN FIRST (index 4)
            // ============================
            Object statusObj = table.getValueAt(row, 4);

            if (statusObj == null || statusObj.toString().trim().isEmpty()) {
                return label; // ❌ no icon
            }

            // ============================
            // GET MONTH KEY (index 5)
            // ============================
            String monthFor = String.valueOf(table.getValueAt(row, 5));

            String chequeStatus = chequeStatusMap.get(monthFor);

            // ============================
            // 🟡 BAD CHEQUE STATES
            // ============================
            if (chequeStatus != null
                    && (chequeStatus.equalsIgnoreCase("PENDING")
                    || chequeStatus.equalsIgnoreCase("BOUNCED")
                    || chequeStatus.equalsIgnoreCase("RETURNED"))) {

                label.setIcon(pendingIcon);
                label.setIconTextGap(8);

            } else {
                // 🟢 GOOD (PAID / CASH / CARD / CLEARED)
                label.setIcon(greenIcon);
                label.setIconTextGap(8);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return label;
    }
}
