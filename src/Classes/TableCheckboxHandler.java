package Classes;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class TableCheckboxHandler {

    private JTable table;
    private int checkboxColumnIndex;
    private JTextField totalField;
    private JTextField chequeTotal;
    private JTextField remainingField;

    private JCheckBox headerCheckBox;
    private boolean isUpdating = false;

    public TableCheckboxHandler(JTable table, int checkboxColumnIndex,
            JTextField totalField, JTextField chequeTotal, JTextField remainingField) {

        this.table = table;
        this.checkboxColumnIndex = checkboxColumnIndex;
        this.totalField = totalField;
        this.chequeTotal = chequeTotal;
        this.remainingField = remainingField;

        addHeaderCheckbox();
        addRowCheckboxListener();
        addTooltipSupport(); // 🔥 NEW
    }

    // =========================
    // TOOLTIP SUPPORT
    // =========================
    private void addTooltipSupport() {

        // Tooltip on HEADER
        JTableHeader header = table.getTableHeader();
        header.setToolTipText("Select rows or edit payable amounts. Values are auto-capped to due and remaining balance.");

        // Tooltip on PAYABLE column (index 5)
        TableCellRenderer baseRenderer = table.getDefaultRenderer(Object.class);

        table.getColumnModel().getColumn(5).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {

            Component c = baseRenderer.getTableCellRendererComponent(
                    tbl, value, isSelected, hasFocus, row, col
            );

            if (c instanceof JComponent) {
                ((JComponent) c).setToolTipText("Enter amount (auto capped to due & remaining)");
            }

            return c;
        });
//        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
//            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
//
//            @Override
//            public Component getTableCellRendererComponent(JTable tbl, Object value,
//                    boolean isSelected, boolean hasFocus, int row, int col) {
//
//                Component c = defaultRenderer.getTableCellRendererComponent(
//                        tbl, value, isSelected, hasFocus, row, col
//                );
//
//                if (c instanceof JComponent) {
//                    ((JComponent) c).setToolTipText("Enter amount (auto adjusted to due & remaining if exceeded)");
//                }
//
//                return c;
//            }
//        });
    }

    // =========================
    // HEADER CHECKBOX
    // =========================
    private void addHeaderCheckbox() {

        JTableHeader header = table.getTableHeader();

        headerCheckBox = new JCheckBox();
        headerCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
        headerCheckBox.setOpaque(false);

        table.getColumnModel().getColumn(checkboxColumnIndex)
                .setHeaderRenderer((tbl, value, isSelected, hasFocus, row, col) -> headerCheckBox);

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int col = table.columnAtPoint(e.getPoint());

                if (col == checkboxColumnIndex) {

                    boolean checked = !headerCheckBox.isSelected();
                    headerCheckBox.setSelected(checked);

                    setAllRows(checked);
                    updateTotal();
                }
            }
        });
    }

    // =========================
    // SET ALL ROWS
    // =========================
    private void setAllRows(boolean checked) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {

            if (checked) {

                double due = GeneralMethods.parseCommaNumber(
                        model.getValueAt(i, 4).toString()
                );

                if (!isWithinLimit(i, due)) {
                    continue;
                }
            }

            model.setValueAt(checked, i, checkboxColumnIndex);
            updatePayable(i, checked);
        }
    }

    // =========================
    // LISTENER
    // =========================
    private void addRowCheckboxListener() {

        table.getModel().addTableModelListener(e -> {

            if (isUpdating) {
                return;
            }

            int row = e.getFirstRow();
            int col = e.getColumn();

            if (row < 0) {
                return;
            }

            try {
                isUpdating = true;

                // =========================
                // CHECKBOX CLICK
                // =========================
                if (col == checkboxColumnIndex) {

                    Boolean checked = (Boolean) table.getValueAt(row, checkboxColumnIndex);

                    if (checked != null && checked) {

                        double due = GeneralMethods.parseCommaNumber(
                                table.getValueAt(row, 4).toString()
                        );

                        if (!isWithinLimit(row, due)) {

                            double allowed = getRemainingAllowed(row);

                            if (allowed <= 0) {
                                table.setValueAt(false, row, checkboxColumnIndex);
                                //  showTooltip("No remaining balance!");
                                showCellTooltip(row, 5, "No remaining balance!");
                                return;
                            }

                            table.setValueAt(GeneralMethods.formatWithComma(allowed), row, 5);
                        }
                    }

                    updatePayable(row, checked != null && checked);
                    updateHeaderState();
                    updateTotal();
                }

                // =========================
                // PAYABLE EDIT
                // =========================
                if (col == 5) {

                    Object val = table.getValueAt(row, 5);

                    if (val != null && !val.toString().trim().isEmpty()) {

                        double amount = GeneralMethods.parseCommaNumber(val.toString());

                        double due = GeneralMethods.parseCommaNumber(
                                table.getValueAt(row, 4).toString()
                        );

                        // 🔥 CAP TO ROW DUE
                        if (amount > due) {
                            amount = due;
                            //showTooltip("Capped to row due");
                            showCellTooltip(row, 5, "Adjusted to row due");
                        }

                        // 🔥 CAP TO REMAINING
                        if (!isWithinLimit(row, amount)) {

                            double allowed = getRemainingAllowed(row);

                            if (allowed <= 0) {
                                table.setValueAt("", row, 5);
                                table.setValueAt(false, row, checkboxColumnIndex);
                                showTooltip("No remaining balance!");
                                return;
                            }

                            amount = Math.min(amount, allowed);
                            //  showTooltip("Capped to remaining balance");
                            showCellTooltip(row, 5, "Capped to remaining balance");
                        }

                        table.setValueAt(GeneralMethods.formatWithComma(amount), row, 5);
                        table.setValueAt(true, row, checkboxColumnIndex);

                    } else {
                        table.setValueAt(false, row, checkboxColumnIndex);
                    }

                    updateHeaderState();
                    updateTotal();
                }

            } finally {
                isUpdating = false;
            }
        });
    }

    // =========================
    // TOOLTIP MESSAGE
    // =========================
    private void showTooltip(String msg) {
        table.setToolTipText(msg);
        ToolTipManager.sharedInstance().mouseMoved(
                new MouseEvent(table, 0, 0, 0, 0, 0, 0, false)
        );
    }

    // =========================
    // UPDATE PAYABLE
    // =========================
    private void updatePayable(int row, boolean checked) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        Object currentVal = model.getValueAt(row, 5);

        if (checked) {

            if (currentVal == null || currentVal.toString().trim().isEmpty()) {

                double due = GeneralMethods.parseCommaNumber(
                        model.getValueAt(row, 4).toString()
                );

                model.setValueAt(GeneralMethods.formatWithComma(due), row, 5);
            }

        } else {
            model.setValueAt("", row, 5);
        }
    }

    // =========================
    // HEADER STATE
    // =========================
    private void updateHeaderState() {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        boolean allChecked = true;

        for (int i = 0; i < model.getRowCount(); i++) {

            Boolean val = (Boolean) model.getValueAt(i, checkboxColumnIndex);

            if (val == null || !val) {
                allChecked = false;
                break;
            }
        }

        headerCheckBox.setSelected(allChecked);
        table.getTableHeader().repaint();
    }

    // =========================
    // TOTAL
    // =========================
    private void updateTotal() {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        double total = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object val = model.getValueAt(i, 5);

            if (val != null && !val.toString().trim().isEmpty()) {
                total += GeneralMethods.parseCommaNumber(val.toString());
            }
        }

        totalField.setText(GeneralMethods.formatWithComma(total));
        chequeTotal.setText(GeneralMethods.formatWithComma(total));
    }

    // =========================
    // LIMIT CHECK
    // =========================
    private boolean isWithinLimit(int row, double newValue) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        double total = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            if (i == row) {
                continue;
            }

            Object val = model.getValueAt(i, 5);

            if (val != null && !val.toString().trim().isEmpty()) {
                total += GeneralMethods.parseCommaNumber(val.toString());
            }
        }

        double remaining = GeneralMethods.parseCommaNumber(remainingField.getText());

        return (total + newValue) <= remaining;
    }

    private double getRemainingAllowed(int currentRow) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        double total = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            if (i == currentRow) {
                continue;
            }

            Object val = model.getValueAt(i, 5);

            if (val != null && !val.toString().trim().isEmpty()) {
                total += GeneralMethods.parseCommaNumber(val.toString());
            }
        }

        double remaining = GeneralMethods.parseCommaNumber(remainingField.getText());

        return remaining - total;
    }

    private void showCellTooltip(int row, int col, String message) {

        try {
            Rectangle rect = table.getCellRect(row, col, true);

            // Convert to screen position
            Point p = rect.getLocation();
            SwingUtilities.convertPointToScreen(p, table);

            // Create tooltip
            JToolTip tip = table.createToolTip();
            tip.setTipText(message);

            PopupFactory factory = PopupFactory.getSharedInstance();
            Popup popup = factory.getPopup(table, tip, p.x, p.y - 25); // above cell

            popup.show();

            // Auto hide after 1.5 seconds
            new javax.swing.Timer(1500, e -> popup.hide()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}

//package Classes;
//
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import javax.swing.*;
//import javax.swing.event.TableModelEvent;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//
//public class TableCheckboxHandler {
//
//    private JTable table;
//    private int checkboxColumnIndex;
//    private JTextField totalField;
//    private JTextField chequeTotal;// 🔥 NEW
//    private JTextField remainingField;
//
//    private JCheckBox headerCheckBox;
//    private boolean isUpdating = false;
//
//    public TableCheckboxHandler(JTable table, int checkboxColumnIndex,
//            JTextField totalField, JTextField chequeTotal, JTextField remainingField) {
//
//        this.table = table;
//        this.checkboxColumnIndex = checkboxColumnIndex;
//        this.totalField = totalField;
//        this.chequeTotal = chequeTotal;
//        this.remainingField = remainingField;
//
//        addHeaderCheckbox();
//        addRowCheckboxListener();
//    }
//
//    private boolean isWithinLimit(int row, double newValue) {
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//        double total = 0;
//
//        for (int i = 0; i < model.getRowCount(); i++) {
//
//            if (i == row) {
//                continue; // skip current row
//            }
//            Object val = model.getValueAt(i, 5);
//
//            if (val != null && !val.toString().trim().isEmpty()) {
//                total += GeneralMethods.parseCommaNumber(val.toString());
//            }
//        }
//
//        double remaining = GeneralMethods.parseCommaNumber(remainingField.getText());
//
//        return (total + newValue) <= remaining;
//    }
//
//    // =========================
//    // HEADER CHECKBOX
//    // =========================
//    private void addHeaderCheckbox() {
//
//        JTableHeader header = table.getTableHeader();
//
//        headerCheckBox = new JCheckBox();
//        headerCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
//        headerCheckBox.setOpaque(false);
//
//        table.getColumnModel().getColumn(checkboxColumnIndex)
//                .setHeaderRenderer((tbl, value, isSelected, hasFocus, row, col) -> headerCheckBox);
//
//        header.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//
//                int col = table.columnAtPoint(e.getPoint());
//
//                if (col == checkboxColumnIndex) {
//
//                    boolean checked = !headerCheckBox.isSelected();
//                    headerCheckBox.setSelected(checked);
//
//                    setAllRows(checked);
//                    updateTotal(); // 🔥
//                }
//            }
//        });
//    }
//
//    // =========================
//    // SET ALL ROWS
//    // =========================
//    private void setAllRows(boolean checked) {
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//        for (int i = 0; i < model.getRowCount(); i++) {
//
//            if (checked) {
//
//                double due = GeneralMethods.parseCommaNumber(
//                        model.getValueAt(i, 4).toString()
//                );
//
//                if (!isWithinLimit(i, due)) {
//                    continue; // skip if exceeds
//                }
//            }
//
//            model.setValueAt(checked, i, checkboxColumnIndex);
//            updatePayable(i, checked);
//          //  updateTotal();
//        }
//    }
////    private void setAllRows(boolean checked) {
////
////        DefaultTableModel model = (DefaultTableModel) table.getModel();
////
////        for (int i = 0; i < model.getRowCount(); i++) {
////
////            model.setValueAt(checked, i, checkboxColumnIndex);
////            updatePayable(i, checked);
////        }
////    }
//
//    // =========================
//    // LISTENER
//    // =========================
//    private void addRowCheckboxListener() {
//
//        table.getModel().addTableModelListener(e -> {
//
//            if (isUpdating) {
//                return; // 🔥 STOP LOOP
//            }
//            int row = e.getFirstRow();
//            int col = e.getColumn();
//
//            if (row < 0) {
//                return;
//            }
//
//            try {
//                isUpdating = true; // 🔥 LOCK
//
//                // =========================
//                // CHECKBOX CLICK
//                // =========================
//                if (col == checkboxColumnIndex) {
//
//                    Boolean checked = (Boolean) table.getValueAt(row, checkboxColumnIndex);
//
//                    if (checked != null && checked) {
//
//                        double due = GeneralMethods.parseCommaNumber(
//                                table.getValueAt(row, 4).toString()
//                        );
//
//                        // 🔥 CHECK LIMIT
//                        if (!isWithinLimit(row, due)) {
//
//                            JOptionPane.showMessageDialog(null, "Amount exceeds remaining balance!");
//
//                            table.setValueAt(false, row, checkboxColumnIndex);
//                           // updateTotal();
//                            return;
//                        }
//                    }
//
//                    updatePayable(row, checked != null && checked);
//                    updateHeaderState();
//                    updateTotal();
//                }
////                if (col == checkboxColumnIndex) {
////
////                    Boolean checked = (Boolean) table.getValueAt(row, checkboxColumnIndex);
////
////                    updatePayable(row, checked != null && checked);
////                    updateHeaderState();
////                    updateTotal();
////                }
//
//                // =========================
//                // PAYABLE EDIT
//                // =========================
//                if (col == 5) {
//
//                    Object val = table.getValueAt(row, 5);
//
//                    if (val != null && !val.toString().trim().isEmpty()) {
//
//                        double amount = GeneralMethods.parseCommaNumber(val.toString());
//
//                        if (amount > 0) {
//
//                            // 🔥 CHECK LIMIT
//                            if (!isWithinLimit(row, amount)) {
//
//                                JOptionPane.showMessageDialog(null, "Amount exceeds remaining balance!");
//
//                                table.setValueAt("", row, 5);
//                                table.setValueAt(false, row, checkboxColumnIndex);
//                                return;
//                            }
//
//                            // ✅ FORMAT
//                            table.setValueAt(GeneralMethods.formatWithComma(amount), row, 5);
//
//                            // ✅ AUTO CHECK
//                            table.setValueAt(true, row, checkboxColumnIndex);
//                        }
//
//                    } else {
//                        table.setValueAt(false, row, checkboxColumnIndex);
//                    }
//
//                    updateHeaderState();
//                    updateTotal();
//                }
////                if (col == 5) {
////
////                    Object val = table.getValueAt(row, 5);
////
////                    if (val != null && !val.toString().trim().isEmpty()) {
////
////                        double amount = GeneralMethods.parseCommaNumber(val.toString());
////
////                        if (amount > 0) {
////
////                            // 🔥 FORMAT VALUE AFTER ENTER
////                            table.setValueAt(GeneralMethods.formatWithComma(amount), row, 5);
////
////                            // 🔥 AUTO CHECK
////                            table.setValueAt(true, row, checkboxColumnIndex);
////                        }
////
////                    } else {
////                        table.setValueAt(false, row, checkboxColumnIndex);
////                    }
////
////                    updateHeaderState();
////                    updateTotal();
////                }
//
//            } finally {
//                isUpdating = false; // 🔥 UNLOCK
//            }
//        });
//    }
//
//    // =========================
//    // UPDATE PAYABLE
//    // =========================
//    private void updatePayable(int row, boolean checked) {
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//        Object currentVal = model.getValueAt(row, 5);
//
//        if (checked) {
//
//            // ✅ ONLY SET if EMPTY (important fix)
//            if (currentVal == null || currentVal.toString().trim().isEmpty()) {
//
//                double due = GeneralMethods.parseCommaNumber(
//                        model.getValueAt(row, 4).toString()
//                );
//
//                model.setValueAt(GeneralMethods.formatWithComma(due), row, 5);
//            }
//
//        } else {
//            model.setValueAt("", row, 5);
//        }
//    }
////    private void updatePayable(int row, boolean checked) {
////
////    DefaultTableModel model = (DefaultTableModel) table.getModel();
////
////    if (checked) {
////        double due = GeneralMethods.parseCommaNumber(model.getValueAt(row, 4).toString());
////        model.setValueAt(GeneralMethods.formatWithComma(due), row, 5);
////    } else {
////        model.setValueAt("", row, 5);
////    }
////}
//
//    // =========================
//    // UPDATE HEADER STATE
//    // =========================
//    private void updateHeaderState() {
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//        boolean allChecked = true;
//
//        for (int i = 0; i < model.getRowCount(); i++) {
//
//            Boolean val = (Boolean) model.getValueAt(i, checkboxColumnIndex);
//
//            if (val == null || !val) {
//                allChecked = false;
//                break;
//            }
//        }
//
//        headerCheckBox.setSelected(allChecked);
//        table.getTableHeader().repaint();
//    }
//
//    // =========================
//    // 🔥 TOTAL CALCULATION
//    // =========================
//    private void updateTotal() {
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//        double total = 0;
//
//        for (int i = 0; i < model.getRowCount(); i++) {
//
//            Object val = model.getValueAt(i, 5);
//
//            if (val != null && !val.toString().trim().isEmpty()) {
//                total += GeneralMethods.parseCommaNumber(val.toString());
//            }
//        }
//
//        totalField.setText(GeneralMethods.formatWithComma(total));
//        chequeTotal.setText(GeneralMethods.formatWithComma(total));
//    }
//}
