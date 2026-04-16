package Classes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class TableCheckboxHandler {

    private JTable table;
    private int checkboxColumnIndex;
    private JTextField totalField;
    private JTextField chequeTotal;// 🔥 NEW

    private JCheckBox headerCheckBox;
    private boolean isUpdating = false;

    public TableCheckboxHandler(JTable table, int checkboxColumnIndex, JTextField totalField, JTextField chequeTotal) {
        this.table = table;
        this.checkboxColumnIndex = checkboxColumnIndex;
        this.totalField = totalField; // 🔥 NEW
        this.chequeTotal = chequeTotal;

        addHeaderCheckbox();
        addRowCheckboxListener();
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
                    updateTotal(); // 🔥
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
                return; // 🔥 STOP LOOP
            }
            int row = e.getFirstRow();
            int col = e.getColumn();

            if (row < 0) {
                return;
            }

            try {
                isUpdating = true; // 🔥 LOCK

                // =========================
                // CHECKBOX CLICK
                // =========================
                if (col == checkboxColumnIndex) {

                    Boolean checked = (Boolean) table.getValueAt(row, checkboxColumnIndex);

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

                        if (amount > 0) {

                            // 🔥 FORMAT VALUE AFTER ENTER
                            table.setValueAt(GeneralMethods.formatWithComma(amount), row, 5);

                            // 🔥 AUTO CHECK
                            table.setValueAt(true, row, checkboxColumnIndex);
                        }

                    } else {
                        table.setValueAt(false, row, checkboxColumnIndex);
                    }

                    updateHeaderState();
                    updateTotal();
                }
//                if (col == 5) {
//
//                    Object val = table.getValueAt(row, 5);
//
//                    if (val != null && !val.toString().trim().isEmpty()) {
//
//                        double amount = GeneralMethods.parseCommaNumber(val.toString());
//
//                        if (amount > 0) {
//                            table.setValueAt(true, row, checkboxColumnIndex);
//                        }
//                    } else {
//                        table.setValueAt(false, row, checkboxColumnIndex);
//                    }
//
//                    updateHeaderState();
//                    updateTotal();
//                }

            } finally {
                isUpdating = false; // 🔥 UNLOCK
            }
        });
    }

    // =========================
    // UPDATE PAYABLE
    // =========================
    private void updatePayable(int row, boolean checked) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        Object currentVal = model.getValueAt(row, 5);

        if (checked) {

            // ✅ ONLY SET if EMPTY (important fix)
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
//    private void updatePayable(int row, boolean checked) {
//
//    DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//    if (checked) {
//        double due = GeneralMethods.parseCommaNumber(model.getValueAt(row, 4).toString());
//        model.setValueAt(GeneralMethods.formatWithComma(due), row, 5);
//    } else {
//        model.setValueAt("", row, 5);
//    }
//}

    // =========================
    // UPDATE HEADER STATE
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
    // 🔥 TOTAL CALCULATION
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
}
