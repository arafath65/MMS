package Panels;

import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LedgerHelper;
import Classes.LogHelper;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import JPA_DAO.Accounts.Cheque_Dao;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
import com.formdev.flatlaf.FlatClientProperties;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

public class Cheque_Handling extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();
    styleDateChooser stDateChooser = new styleDateChooser();

    private File selectedImageFile;

    private int selectedStudentId;
    String username;
    String role;

    public Cheque_Handling(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        chq_handling_cheq_details_table.setDefaultRenderer(Object.class, new TableGradientCell());
        chq_handling_cheq_details_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        chq_handling_cheq_details_table.getColumnModel().getColumn(10).setMinWidth(0);
        chq_handling_cheq_details_table.getColumnModel().getColumn(10).setMaxWidth(0);
        chq_handling_cheq_details_table.getColumnModel().getColumn(10).setWidth(0);

        loadChequeTable(chq_handling_chq_status_combo.getSelectedItem().toString(), chq_handling_cheq_details_table);
        //   setChequeActionCombo(chq_handling_cheq_details_table, chq_handling_chq_status_combo.getSelectedItem().toString());
        setupComboSelectionListeners(chq_handling_chq_status_combo, chq_handling_table_sorter_text);

        chq_handling_table_sorter_text.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                applyTableFilter(chq_handling_cheq_details_table, chq_handling_table_sorter_text.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyTableFilter(chq_handling_cheq_details_table, chq_handling_table_sorter_text.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyTableFilter(chq_handling_cheq_details_table, chq_handling_table_sorter_text.getText());
            }
        });

    }

    // THIS IS THE METHOD YOU CLICK AFTER LOADING COMBO 
    private boolean itemSelectedByUsers = false;
    //  private boolean itemSelectedByUsers2 = false;

    public void setupComboSelectionListeners(JComboBox<String> comboBox, JComponent nextFocusComponent) {
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                itemSelectedByUsers = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (itemSelectedByUsers) {
                    Object selected = comboBox.getSelectedItem();
                    if (selected != null) {
                        String selectedValue = selected.toString().trim();
                        if (!selectedValue.isEmpty() && isValueFromList(comboBox, selectedValue)) {

                            loadChequeTable(chq_handling_chq_status_combo.getSelectedItem().toString(), chq_handling_cheq_details_table);

                            nextFocusComponent.requestFocus();
                        }
                    }
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e
            ) {
                itemSelectedByUsers = false;
            }
        }
        );

        // ✅ Detect user selection via Enter or click
        comboBox.addActionListener(e
                -> {
            if (comboBox.isPopupVisible()) {
                itemSelectedByUsers = true;
            }
        }
        );
    }

    private boolean isValueFromList(JComboBox<String> comboBox, String value) {
        ComboBoxModel<String> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            String item = model.getElementAt(i);
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private void setChequeActionCombo(JTable table, String filterStatus) {

        TableColumn actionColumn = table.getColumnModel().getColumn(9);

        String[] actions;

        switch (filterStatus.toUpperCase()) {

            case "PENDING":
                actions = new String[]{"PENDING", "CLEARED", "RETURNED", "BOUNCED", "CANCELLED"};
                break;

            case "CLEARED":
                actions = new String[]{"CLEARED", "PENDING"};
                break;

            case "RETURNED":
                actions = new String[]{"RETURNED", "PENDING"};
                break;

            case "BOUNCED":
                actions = new String[]{"BOUNCED", "PENDING"};
                break;

            case "CANCELLED":
                actions = new String[]{"CANCELLED", "PENDING"};
                break;

            default:
                actions = new String[]{"-"};
        }

        JComboBox<String> combo = new JComboBox<>(actions);

        DefaultCellEditor editor = new DefaultCellEditor(combo);

        // 🔥 VERY IMPORTANT: update table value immediately
        combo.addActionListener(e -> {
            if (table.isEditing()) {
                int row = table.getEditingRow();
                Object selected = combo.getSelectedItem();

                table.getModel().setValueAt(selected, row, 9); // 🔥 force update
                table.getCellEditor().stopCellEditing();
            }
        });

        actionColumn.setCellEditor(editor);
    }

    public void loadChequeTable(String status, JTable table) {

        Cheque_Dao dao = new Cheque_Dao();
        List<Object[]> list = dao.getChequeListByStatus(status);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        int rowNo = 1;

        for (Object[] row : list) {

            int chequeId = ((Number) row[0]).intValue();

            String chequeNo = String.valueOf(row[1]);
            String bank = String.valueOf(row[2]);
            String branch = String.valueOf(row[3]);   // ✅ NEW
            String chequeDate = String.valueOf(row[4]);

            int amount = ((Number) row[5]).intValue(); // ✅ FIXED (SUM amount)
            String chequeStatus = String.valueOf(row[6]);

            String admissionNo = String.valueOf(row[7]);
            String studentName = String.valueOf(row[8]);
            String batch = String.valueOf(row[9]);
            String course = String.valueOf(row[10]);

            model.addRow(new Object[]{
                rowNo++,
                admissionNo,
                studentName,
                batch,
                course,
                chequeNo,
                bank + " - " + branch,
                chequeDate,
                GeneralMethods.formatWithComma(amount),
                chequeStatus, // ✅ important fix
                chequeId
            });

//            model.addRow(new Object[]{
//                rowNo++,
//                admissionNo,
//                studentName,
//                batch,
//                course,
//                chequeNo,
//                bank + " - " + branch, // ✅ combined (nice UI)
//                chequeDate,
//                GeneralMethods.formatWithComma(amount),
//                chequeStatus, // will be replaced by combo
//                chequeId
//            });
        }

        // ============================
        // HIDE CHEQUE ID COLUMN
        // ============================
        table.getColumnModel().getColumn(10).setMinWidth(0);
        table.getColumnModel().getColumn(10).setMaxWidth(0);

        // ============================
        // SET ACTION COMBO
        // ============================
        setChequeActionCombo(table, status);

    }

    private void applyTableFilter(JTable table, String text) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

        table.setRowSorter(sorter);

        if (text == null || text.trim().isEmpty()) {
            sorter.setRowFilter(null); // show all
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // case-insensitive
        }
    }

    private void processChequeClearance() {

        String filterStatus = chq_handling_chq_status_combo.getSelectedItem().toString();

        DefaultTableModel model = (DefaultTableModel) chq_handling_cheq_details_table.getModel();
        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            for (int i = 0; i < model.getRowCount(); i++) {

                String action = String.valueOf(model.getValueAt(i, 9));

                if (action == null || action.equalsIgnoreCase(filterStatus)) {
                    continue; // skip unchanged rows
                }

                String chequeNo = String.valueOf(model.getValueAt(i, 5));
                String bankBranch = String.valueOf(model.getValueAt(i, 6));
                String chequeDate = String.valueOf(model.getValueAt(i, 7));

                String bank = bankBranch.split("-")[0].trim();

                // ============================
                // 🔥 GET PAYMENT DETAILS
                // ============================
                List<Object[]> paymentList = em.createNativeQuery(
                        "SELECT sfp.student_fee_payments_id, "
                        + "SUM(scd.cheque_amount), "
                        + "MAX(sfi.remarks) "
                        + "FROM student_fee_cheque_details scd "
                        + "JOIN student_fee_installments sfi ON scd.student_fee_installments_id = sfi.student_fee_installments_id "
                        + "JOIN student_fee_payments sfp ON sfi.student_fee_payments_id = sfp.student_fee_payments_id "
                        + "WHERE scd.cheque_no = ? AND scd.bank = ? AND scd.cheque_date = ? "
                        + "GROUP BY sfp.student_fee_payments_id"
                )
                        .setParameter(1, chequeNo)
                        .setParameter(2, bank)
                        .setParameter(3, chequeDate)
                        .getResultList();

                // ============================
                // UPDATE CHEQUE STATUS
                // ============================
                em.createNativeQuery(
                        "UPDATE student_fee_cheque_details "
                        + "SET cheque_status = ? "
                        + "WHERE cheque_no = ? AND bank = ? AND cheque_date = ?"
                )
                        .setParameter(1, action)
                        .setParameter(2, chequeNo)
                        .setParameter(3, bank)
                        .setParameter(4, chequeDate)
                        .executeUpdate();

                // ============================
                // 🔥 HANDLE PAYMENT IMPACT
                // ============================
                for (Object[] p : paymentList) {

                    int paymentId = ((Number) p[0]).intValue();
                    int chequeAmount = ((Number) p[1]).intValue();
                    String remarks = String.valueOf(p[2]); // ADMISSION / NORMAL

                    // ============================
                    // CASE 1: PENDING → CLEARED
                    // ============================
                    if (filterStatus.equalsIgnoreCase("PENDING")
                            && action.equalsIgnoreCase("CLEARED")) {

                        // Update total_paid
                        em.createNativeQuery(
                                "UPDATE student_fee_payments "
                                + "SET total_paid = total_paid + ? "
                                + "WHERE student_fee_payments_id = ?"
                        )
                                .setParameter(1, chequeAmount)
                                .setParameter(2, paymentId)
                                .executeUpdate();

                        // Recalculate balance
                        em.createNativeQuery(
                                "UPDATE student_fee_payments "
                                + "SET total_balance = total_fee - total_paid "
                                + "WHERE student_fee_payments_id = ?"
                        )
                                .setParameter(1, paymentId)
                                .executeUpdate();

                        // Ledger entry for cleared cheque
                        LedgerHelper.saveLedger(em,
                                chequeAmount,
                                "CREDIT",
                                "Cheque cleared - " + chequeNo + " | Bank: " + bankBranch,
                                "STUDENT_PAYMENT",
                                paymentId,
                                "CHEQUE",
                                "MONTHLY_FEE",
                                username
                        );

                        // ✅ LOG: Cheque Cleared
                        logHelper.log(
                                "CHEQUE_HANDLING",
                                paymentId,
                                "CLEARED",
                                "CHQ-" + chequeNo,
                                (double) chequeAmount,
                                String.format("Cheque #%s CLEARED. Amount added to student balance. Bank: %s", chequeNo, bankBranch),
                                username
                        );

                    } // ============================
                    // CASE 2: CLEARED → PENDING (REVERSE)
                    // ============================
                    else if (filterStatus.equalsIgnoreCase("CLEARED")
                            && action.equalsIgnoreCase("PENDING")) {

                        // Subtract from total_paid
                        em.createNativeQuery(
                                "UPDATE student_fee_payments "
                                + "SET total_paid = total_paid - ? "
                                + "WHERE student_fee_payments_id = ?"
                        )
                                .setParameter(1, chequeAmount)
                                .setParameter(2, paymentId)
                                .executeUpdate();

                        // Recalculate balance
                        em.createNativeQuery(
                                "UPDATE student_fee_payments "
                                + "SET total_balance = total_fee - total_paid "
                                + "WHERE student_fee_payments_id = ?"
                        )
                                .setParameter(1, paymentId)
                                .executeUpdate();

                        // Ledger deduction
                        LedgerHelper.saveLedger(em,
                                chequeAmount,
                                "DEBIT",
                                "Cheque " + action + " - " + chequeNo + " | Bank: " + bankBranch,
                                "STUDENT_PAYMENT",
                                paymentId,
                                "CHEQUE",
                                "MONTHLY_FEE",
                                username
                        );

                        // LOG: Cheque Reversed
                        logHelper.log(
                                "CHEQUE_HANDLING",
                                paymentId,
                                "REVERSED",
                                "CHQ-" + chequeNo,
                                (double) chequeAmount,
                                String.format("REVERSAL: Cheque #%s moved back to PENDING. Amount deducted from student balance.", chequeNo),
                                username
                        );

                    } // ============================
                    // CASE 3: RETURNED / BOUNCED / CANCELLED → PENDING
                    // Only status update, no money impact
                    // ============================
                    else if ((filterStatus.equalsIgnoreCase("RETURNED")
                            || filterStatus.equalsIgnoreCase("BOUNCED")
                            || filterStatus.equalsIgnoreCase("CANCELLED"))
                            && action.equalsIgnoreCase("PENDING")) {
                        // Nothing to do, status already updated above
                        // LOG: Status Change
                        logHelper.log(
                                "CHEQUE_HANDLING",
                                paymentId,
                                action.toUpperCase(),
                                "CHQ-" + chequeNo,
                                (double) chequeAmount,
                                String.format("Status update for Cheque #%s: %s to %s", chequeNo, filterStatus, action),
                                username
                        );
                    }
                }
            }

            em.getTransaction().commit();

            JOptionPane.showMessageDialog(this, "Cheque Update Completed!");

            loadChequeTable(filterStatus, chq_handling_cheq_details_table);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } finally {
            em.close();
        }
    }

//    private void processChequeClearance() {
//
//        String filterStatus = chq_handling_chq_status_combo.getSelectedItem().toString();
//
//        DefaultTableModel model = (DefaultTableModel) chq_handling_cheq_details_table.getModel();
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            em.getTransaction().begin();
//
//            for (int i = 0; i < model.getRowCount(); i++) {
//
//                String action = String.valueOf(model.getValueAt(i, 9));
//
//                if (action == null || action.equalsIgnoreCase(filterStatus)) {
//                    continue; // skip unchanged rows
//                }
//
//                String chequeNo = String.valueOf(model.getValueAt(i, 5));
//                String bankBranch = String.valueOf(model.getValueAt(i, 6));
//                String chequeDate = String.valueOf(model.getValueAt(i, 7));
//
//                String bank = bankBranch.split("-")[0].trim();
//
//                // ============================
//                // 🔥 GET PAYMENT + TYPE
//                // ============================
//                List<Object[]> paymentList = em.createNativeQuery(
//                        "SELECT sfp.student_fee_payments_id, "
//                        + "SUM(scd.cheque_amount), "
//                        + "MAX(sfi.remarks) "
//                        + "FROM student_fee_cheque_details scd "
//                        + "JOIN student_fee_installments sfi ON scd.student_fee_installments_id = sfi.student_fee_installments_id "
//                        + "JOIN student_fee_payments sfp ON sfi.student_fee_payments_id = sfp.student_fee_payments_id "
//                        + "WHERE scd.cheque_no = ? AND scd.bank = ? AND scd.cheque_date = ? "
//                        + "GROUP BY sfp.student_fee_payments_id"
//                )
//                        .setParameter(1, chequeNo)
//                        .setParameter(2, bank)
//                        .setParameter(3, chequeDate)
//                        .getResultList();
//
//                // ============================
//                // UPDATE CHEQUE STATUS
//                // ============================
//                em.createNativeQuery(
//                        "UPDATE student_fee_cheque_details "
//                        + "SET cheque_status = ? "
//                        + "WHERE cheque_no = ? AND bank = ? AND cheque_date = ?"
//                )
//                        .setParameter(1, action)
//                        .setParameter(2, chequeNo)
//                        .setParameter(3, bank)
//                        .setParameter(4, chequeDate)
//                        .executeUpdate();
//
//                // ============================
//                // 🔥 HANDLE LOGIC
//                // ============================
//                for (Object[] p : paymentList) {
//
//                    int paymentId = ((Number) p[0]).intValue();
//                    int chequeAmount = ((Number) p[1]).intValue();
//                    String remarks = String.valueOf(p[2]); // ADMISSION / NORMAL
//
//                    // ============================
//                    // CASE 1: PENDING → CLEARED
//                    // ============================
//                    if (filterStatus.equalsIgnoreCase("PENDING")
//                            && action.equalsIgnoreCase("CLEARED")) {
//
//                        // 🔥 BOTH ADMISSION + NORMAL SAME BEHAVIOR
//                        em.createNativeQuery(
//                                "UPDATE student_fee_payments "
//                                + "SET total_paid = total_paid + ?, "
//                                + "total_balance = total_fee - (total_paid + ?) "
//                                + "WHERE student_fee_payments_id = ?"
//                        )
//                                .setParameter(1, chequeAmount)
//                                .setParameter(2, chequeAmount)
//                                .setParameter(3, paymentId)
//                                .executeUpdate();
//
//                    } // ============================
//                    // CASE 2: CLEARED → PENDING (REVERSE)
//                    // ============================
//                    else if (filterStatus.equalsIgnoreCase("CLEARED")
//                            && action.equalsIgnoreCase("PENDING")) {
//
//                        em.createNativeQuery(
//                                "UPDATE student_fee_payments "
//                                + "SET total_paid = total_paid - ?, "
//                                + "total_balance = total_fee - (total_paid - ?) "
//                                + "WHERE student_fee_payments_id = ?"
//                        )
//                                .setParameter(1, chequeAmount)
//                                .setParameter(2, chequeAmount)
//                                .setParameter(3, paymentId)
//                                .executeUpdate();
//                    } // ============================
//                    // CASE 3:
//                    // RETURNED / BOUNCED / CANCELLED → PENDING
//                    // ONLY STATUS CHANGE (NO MONEY)
//                    // ============================
//                    else if ((filterStatus.equalsIgnoreCase("RETURNED")
//                            || filterStatus.equalsIgnoreCase("BOUNCED")
//                            || filterStatus.equalsIgnoreCase("CANCELLED"))
//                            && action.equalsIgnoreCase("PENDING")) {
//
//                        // 🔥 DO NOTHING (only status already updated)
//                    }
//                }
//            }
//
//            em.getTransaction().commit();
//
//            JOptionPane.showMessageDialog(this, "Cheque Update Completed!");
//
//            loadChequeTable(filterStatus, chq_handling_cheq_details_table);
//
//        } catch (Exception e) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
//        } finally {
//            em.close();
//        }
//    }
    // OLD ******************************************
//    private void processChequeClearance() {
//
//        String filterStatus = chq_handling_chq_status_combo.getSelectedItem().toString();
//
//        DefaultTableModel model = (DefaultTableModel) chq_handling_cheq_details_table.getModel();
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            em.getTransaction().begin();
//
//            for (int i = 0; i < model.getRowCount(); i++) {
//
//                String action = String.valueOf(model.getValueAt(i, 9));
//
//                if (action == null || action.equalsIgnoreCase(filterStatus)) {
//                    continue; // skip unchanged
//                }
//
//                String chequeNo = String.valueOf(model.getValueAt(i, 5));
//                String bankBranch = String.valueOf(model.getValueAt(i, 6));
//                String chequeDate = String.valueOf(model.getValueAt(i, 7));
//
//                String bank = bankBranch.split("-")[0].trim();
//
//                // ============================
//                // UPDATE CHEQUE STATUS
//                // ============================
//                em.createNativeQuery(
//                        "UPDATE student_fee_cheque_details "
//                        + "SET cheque_status = ? "
//                        + "WHERE cheque_no = ? AND bank = ? AND cheque_date = ?"
//                )
//                        .setParameter(1, action)
//                        .setParameter(2, chequeNo)
//                        .setParameter(3, bank)
//                        .setParameter(4, chequeDate)
//                        .executeUpdate();
//
//                // ============================
//                // 🔥 HANDLE PAYMENT IMPACT
//                // ============================
//                // ---- CASE 1: PENDING → CLEARED ----
//                if (filterStatus.equalsIgnoreCase("PENDING")
//                        && action.equalsIgnoreCase("CLEARED")) {
//
//                    List<Object[]> paymentList = em.createNativeQuery(
//                            "SELECT sfp.student_fee_payments_id, SUM(scd.cheque_amount) "
//                            + "FROM student_fee_cheque_details scd "
//                            + "JOIN student_fee_installments sfi ON scd.student_fee_installments_id = sfi.student_fee_installments_id "
//                            + "JOIN student_fee_payments sfp ON sfi.student_fee_payments_id = sfp.student_fee_payments_id "
//                            + "WHERE scd.cheque_no = ? AND scd.bank = ? AND scd.cheque_date = ? "
//                            + "GROUP BY sfp.student_fee_payments_id"
//                    )
//                            .setParameter(1, chequeNo)
//                            .setParameter(2, bank)
//                            .setParameter(3, chequeDate)
//                            .getResultList();
//
//                    for (Object[] p : paymentList) {
//
//                        int paymentId = ((Number) p[0]).intValue();
//                        int chequeAmount = ((Number) p[1]).intValue();
//
//                        em.createNativeQuery(
//                                "UPDATE student_fee_payments "
//                                + "SET total_paid = total_paid + ?, "
//                                + "total_balance = total_fee - (total_paid + ?) "
//                                + "WHERE student_fee_payments_id = ?"
//                        )
//                                .setParameter(1, chequeAmount)
//                                .setParameter(2, chequeAmount)
//                                .setParameter(3, paymentId)
//                                .executeUpdate();
//                    }
//                } // ---- CASE 2: CLEARED → PENDING (REVERSE) ----
//                else if (filterStatus.equalsIgnoreCase("CLEARED")
//                        && action.equalsIgnoreCase("PENDING")) {
//
//                    List<Object[]> paymentList = em.createNativeQuery(
//                            "SELECT sfp.student_fee_payments_id, SUM(scd.cheque_amount) "
//                            + "FROM student_fee_cheque_details scd "
//                            + "JOIN student_fee_installments sfi ON scd.student_fee_installments_id = sfi.student_fee_installments_id "
//                            + "JOIN student_fee_payments sfp ON sfi.student_fee_payments_id = sfp.student_fee_payments_id "
//                            + "WHERE scd.cheque_no = ? AND scd.bank = ? AND scd.cheque_date = ? "
//                            + "GROUP BY sfp.student_fee_payments_id"
//                    )
//                            .setParameter(1, chequeNo)
//                            .setParameter(2, bank)
//                            .setParameter(3, chequeDate)
//                            .getResultList();
//
//                    for (Object[] p : paymentList) {
//
//                        int paymentId = ((Number) p[0]).intValue();
//                        int chequeAmount = ((Number) p[1]).intValue();
//
//                        em.createNativeQuery(
//                                "UPDATE student_fee_payments "
//                                + "SET total_paid = total_paid - ?, "
//                                + "total_balance = total_fee - (total_paid - ?) "
//                                + "WHERE student_fee_payments_id = ?"
//                        )
//                                .setParameter(1, chequeAmount)
//                                .setParameter(2, chequeAmount)
//                                .setParameter(3, paymentId)
//                                .executeUpdate();
//                    }
//                } // ---- CASE 3: RETURNED / BOUNCED / CANCELLED → PENDING ----
//                else if ((filterStatus.equalsIgnoreCase("RETURNED")
//                        || filterStatus.equalsIgnoreCase("BOUNCED")
//                        || filterStatus.equalsIgnoreCase("CANCELLED"))
//                        && action.equalsIgnoreCase("PENDING")) {
//                    
//                }
//
//                // ---- CASE 3: OTHER STATUS CHANGES ----
//                // RETURNED / BOUNCED / CANCELLED
//                // → only status updated (already done above)
//            }
//
//            em.getTransaction().commit();
//
//            JOptionPane.showMessageDialog(this, "Cheque Update Completed!");
//
//            loadChequeTable(filterStatus, chq_handling_cheq_details_table);
//
//        } catch (Exception e) {
//            em.getTransaction().rollback();
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
//        } finally {
//            em.close();
//        }
//    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        chq_handling_chq_status_combo = new javax.swing.JComboBox<>();
        chq_handling_table_sorter_text = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chq_handling_cheq_details_table = new javax.swing.JTable();
        firstName_label5 = new javax.swing.JLabel();
        cheque_details_value_label = new javax.swing.JLabel();

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Table Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel31.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(232, 232, 232));
        jLabel31.setText("Cheque Status");

        chq_handling_chq_status_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        chq_handling_chq_status_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PENDING", "CLEARED", "RETURNED", "CANCELLED", "BOUNCED" }));

        chq_handling_table_sorter_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        chq_handling_table_sorter_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chq_handling_table_sorter_textActionPerformed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(232, 232, 232));
        jLabel37.setText("Sort Table");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chq_handling_chq_status_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(chq_handling_table_sorter_text))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chq_handling_chq_status_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chq_handling_table_sorter_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setToolTipText("Siblings");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setToolTipText("Course Enrolment");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cheque Informations", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        chq_handling_cheq_details_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Admission", "Student", "Batch", "Course", "Cheque number", "Bank Name", "cheque_date", "cheque_amount", "Action", "Cheque ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(chq_handling_cheq_details_table);
        if (chq_handling_cheq_details_table.getColumnModel().getColumnCount() > 0) {
            chq_handling_cheq_details_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            chq_handling_cheq_details_table.getColumnModel().getColumn(1).setPreferredWidth(100);
            chq_handling_cheq_details_table.getColumnModel().getColumn(2).setPreferredWidth(120);
            chq_handling_cheq_details_table.getColumnModel().getColumn(3).setPreferredWidth(100);
            chq_handling_cheq_details_table.getColumnModel().getColumn(4).setPreferredWidth(150);
            chq_handling_cheq_details_table.getColumnModel().getColumn(5).setPreferredWidth(150);
        }

        firstName_label5.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        firstName_label5.setText("Total Value");

        cheque_details_value_label.setFont(new java.awt.Font("Roboto Medium", 1, 12)); // NOI18N
        cheque_details_value_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheque_details_value_label.setText("0.00");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1324, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(firstName_label5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cheque_details_value_label, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cheque_details_value_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(firstName_label5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chq_handling_table_sorter_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chq_handling_table_sorter_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chq_handling_table_sorter_textActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        processChequeClearance();

    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel cheque_details_value_label;
    private javax.swing.JTable chq_handling_cheq_details_table;
    private javax.swing.JComboBox<String> chq_handling_chq_status_combo;
    private javax.swing.JTextField chq_handling_table_sorter_text;
    private javax.swing.JLabel firstName_label5;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
