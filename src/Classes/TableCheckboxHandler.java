

package Classes;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class TableCheckboxHandler {

    private JTable table;
    private JTextField totalField;
    private JTextField chequeTotal;
    private JTextField remainingField;

    // ✅ COLUMN INDEXES
    private final int COL_DUE = 8;
    private final int COL_PAYABLE = 9;
    private final int COL_CHECK = 10;

    private JCheckBox headerCheckBox;
    private boolean isUpdating = false;
    public boolean isProgrammaticUpdate = false;

    public TableCheckboxHandler(JTable table,
            JTextField totalField, JTextField chequeTotal, JTextField remainingField) {

        this.table = table;
        this.totalField = totalField;
        this.chequeTotal = chequeTotal;
        this.remainingField = remainingField;

        addHeaderCheckbox();
        addRowCheckboxListener();
        addTooltipSupport();
    }

    public void forceUpdateTotal() {
        updateTotal();
    }

    // =========================
    // TOOLTIP SUPPORT
    // =========================
    private void addTooltipSupport() {

        JTableHeader header = table.getTableHeader();
        header.setToolTipText("Select rows or edit payable amounts. Auto capped.");

        TableCellRenderer baseRenderer = table.getDefaultRenderer(Object.class);

        table.getColumnModel().getColumn(COL_PAYABLE).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {

            Component c = baseRenderer.getTableCellRendererComponent(
                    tbl, value, isSelected, hasFocus, row, col
            );

            if (c instanceof JComponent) {
                ((JComponent) c).setToolTipText("Enter amount (auto capped)");
            }

            return c;
        });
    }

    // =========================
    // HEADER CHECKBOX
    // =========================
    private void addHeaderCheckbox() {

        JTableHeader header = table.getTableHeader();

        headerCheckBox = new JCheckBox();
        headerCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
        headerCheckBox.setOpaque(false);

        table.getColumnModel().getColumn(COL_CHECK)
                .setHeaderRenderer((tbl, value, isSelected, hasFocus, row, col) -> headerCheckBox);

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int col = table.columnAtPoint(e.getPoint());

                if (col == COL_CHECK) {

                    boolean checked = !headerCheckBox.isSelected();
                    headerCheckBox.setSelected(checked);

                    setAllRows(checked);
                }
            }
        });
    }

    // =========================
    // SET ALL ROWS (FIXED)
    // =========================
    private void setAllRows(boolean checked) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {

            model.setValueAt(checked, i, COL_CHECK);

            if (checked) {

                double due = GeneralMethods.parseCommaNumber(
                        model.getValueAt(i, COL_DUE).toString()
                );

                double allowed = getRemainingAllowed(i);

                double finalAmount;

                if (allowed <= 0) {
                    finalAmount = 0;
                } else {
                    finalAmount = Math.min(due, allowed);
                }

                model.setValueAt(
                        finalAmount == 0 ? "" : GeneralMethods.formatWithComma(finalAmount),
                        i,
                        COL_PAYABLE
                );

            } else {
                model.setValueAt("", i, COL_PAYABLE);
            }
        }

        updateHeaderState();
        updateTotal();
    }

    // =========================
    // LISTENER
    // =========================
    private void addRowCheckboxListener() {

        table.getModel().addTableModelListener(e -> {

//            if (isUpdating) {
//                return;
//            }
            if (isUpdating || isProgrammaticUpdate) {
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
                if (col == COL_CHECK) {

                    Boolean checked = (Boolean) table.getValueAt(row, COL_CHECK);

                    if (checked != null && checked) {

                        double due = GeneralMethods.parseCommaNumber(
                                table.getValueAt(row, COL_DUE).toString()
                        );

                        double allowed = getRemainingAllowed(row);

                        if (allowed <= 0) {
                            table.setValueAt(false, row, COL_CHECK);
                            showCellTooltip(row, COL_PAYABLE, "No remaining balance!");
                            return;
                        }

                        double finalAmount = Math.min(due, allowed);

                        table.setValueAt(
                                GeneralMethods.formatWithComma(finalAmount),
                                row,
                                COL_PAYABLE
                        );
                    }

                    updatePayable(row, checked != null && checked);
                    updateHeaderState();
                    updateTotal();
                }

                // =========================
                // PAYABLE EDIT
                // =========================
                if (col == COL_PAYABLE) {

                    Object val = table.getValueAt(row, COL_PAYABLE);

                    if (val != null && !val.toString().trim().isEmpty()) {

                        double amount = GeneralMethods.parseCommaNumber(val.toString());

                        double due = GeneralMethods.parseCommaNumber(
                                table.getValueAt(row, COL_DUE).toString()
                        );

                        if (amount > due) {
                            amount = due;
                            showCellTooltip(row, COL_PAYABLE, "Adjusted to due");
                        }

                        double allowed = getRemainingAllowed(row);

                        if (amount > allowed) {

                            if (allowed <= 0) {
                                table.setValueAt("", row, COL_PAYABLE);
                                table.setValueAt(false, row, COL_CHECK);
                                showCellTooltip(row, COL_PAYABLE, "No remaining!");
                                return;
                            }

                            amount = allowed;
                            showCellTooltip(row, COL_PAYABLE, "Capped to remaining");
                        }

                        table.setValueAt(GeneralMethods.formatWithComma(amount), row, COL_PAYABLE);
                        table.setValueAt(true, row, COL_CHECK);

                    } else {
                        table.setValueAt(false, row, COL_CHECK);
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
    // UPDATE PAYABLE
    // =========================
    private void updatePayable(int row, boolean checked) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        if (checked) {

            Object currentVal = model.getValueAt(row, COL_PAYABLE);

            if (currentVal == null || currentVal.toString().trim().isEmpty()) {

                double due = GeneralMethods.parseCommaNumber(
                        model.getValueAt(row, COL_DUE).toString()
                );

                model.setValueAt(GeneralMethods.formatWithComma(due), row, COL_PAYABLE);
            }

        } else {
            model.setValueAt("", row, COL_PAYABLE);
        }
    }

    // =========================
    // HEADER STATE
    // =========================
    private void updateHeaderState() {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        boolean allChecked = true;

        for (int i = 0; i < model.getRowCount(); i++) {

            Boolean val = (Boolean) model.getValueAt(i, COL_CHECK);

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

            Object val = model.getValueAt(i, COL_PAYABLE);

            if (val != null && !val.toString().trim().isEmpty()) {
                total += GeneralMethods.parseCommaNumber(val.toString());
            }
        }

        totalField.setText(GeneralMethods.formatWithComma(total));
        chequeTotal.setText(GeneralMethods.formatWithComma(total));
    }

    // =========================
    // LIMIT LOGIC
    // =========================
    private double getRemainingAllowed(int currentRow) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        double total = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            if (i == currentRow) {
                continue;
            }

            Object val = model.getValueAt(i, COL_PAYABLE);

            if (val != null && !val.toString().trim().isEmpty()) {
                total += GeneralMethods.parseCommaNumber(val.toString());
            }
        }

        double remaining;

        try {
            remaining = GeneralMethods.parseCommaNumber(remainingField.getText());
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }

        return remaining - total;
    }

    // =========================
    // TOOLTIP
    // =========================
    private void showCellTooltip(int row, int col, String message) {

        try {
            Rectangle rect = table.getCellRect(row, col, true);
            Point p = rect.getLocation();
            SwingUtilities.convertPointToScreen(p, table);

            JToolTip tip = table.createToolTip();
            tip.setTipText(message);

            Popup popup = PopupFactory.getSharedInstance()
                    .getPopup(table, tip, p.x, p.y - 25);

            popup.show();

            new javax.swing.Timer(1500, e -> popup.hide()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
