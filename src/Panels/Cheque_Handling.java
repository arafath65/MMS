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

//        chq_handling_cheq_details_table.getColumnModel().getColumn(9).setMinWidth(0);
//        chq_handling_cheq_details_table.getColumnModel().getColumn(9).setMaxWidth(0);
//        chq_handling_cheq_details_table.getColumnModel().getColumn(9).setWidth(0);
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

        TableColumn actionColumn = table.getColumnModel().getColumn(8);

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

                table.getModel().setValueAt(selected, row, 8); // 🔥 force update
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

            String category = String.valueOf(row[1]);
            String subCategory = String.valueOf(row[2]);
            String studentName = String.valueOf(row[3]);

            String chequeNo = String.valueOf(row[4]);
            String bank = String.valueOf(row[5]);
            String branch = String.valueOf(row[6]);
            String chequeDate = String.valueOf(row[7]);

            double amount = ((Number) row[8]).doubleValue();
            String chequeStatus = String.valueOf(row[9]);

            model.addRow(new Object[]{
                rowNo++,
                category,
                subCategory,
                studentName,
                chequeNo,
                bank + " - " + branch,
                chequeDate,
                GeneralMethods.formatWithComma(amount),
                chequeStatus,
                chequeId
            });
        }

        // ============================
        // ACTION COMBO
        // ============================
        setChequeActionCombo(table, status);
        getTotalFromTable();
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
        System.out.println("FILTER STATUS = " + filterStatus);

        DefaultTableModel model = (DefaultTableModel) chq_handling_cheq_details_table.getModel();
        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            for (int i = 0; i < model.getRowCount(); i++) {

                String action = String.valueOf(model.getValueAt(i, 8));

                if (action == null || action.equalsIgnoreCase(filterStatus)) {
                    continue;
                }

                String chequeNo = String.valueOf(model.getValueAt(i, 4));
                String bankBranch = String.valueOf(model.getValueAt(i, 5));
                String chequeDate = String.valueOf(model.getValueAt(i, 6));

                String bank = bankBranch.split("-")[0].trim();

                // ============================
                // GET CHEQUE IDS
                // ============================
                List<Integer> chequeIds = em.createNativeQuery(
                        "SELECT student_fee_cheque_details_id "
                        + "FROM student_fee_cheque_details "
                        + "WHERE cheque_no=? AND bank=? AND cheque_date=? AND status=1"
                )
                        .setParameter(1, chequeNo)
                        .setParameter(2, bank)
                        .setParameter(3, chequeDate)
                        .getResultList();

                if (chequeIds.isEmpty()) {
                    continue;
                }

                // ============================
                // UPDATE STATUS
                // ============================
                em.createNativeQuery(
                        "UPDATE student_fee_cheque_details "
                        + "SET cheque_status=? "
                        + "WHERE cheque_no=? AND bank=? AND cheque_date=?"
                )
                        .setParameter(1, action)
                        .setParameter(2, chequeNo)
                        .setParameter(3, bank)
                        .setParameter(4, chequeDate)
                        .executeUpdate();

                // ============================
                // LOOP EACH CHEQUE RECORD
                // ============================
                for (Integer chequeId : chequeIds) {

                    Object[] row = (Object[]) em.createNativeQuery(
                            "SELECT reference_type, reference_id, cheque_amount "
                            + "FROM student_fee_cheque_details "
                            + "WHERE student_fee_cheque_details_id=?"
                    )
                            .setParameter(1, chequeId)
                            .getSingleResult();

                    String refType = row[0] != null ? row[0].toString() : "";
                    int refId = row[1] != null ? ((Number) row[1]).intValue() : 0;
                    double amount = row[2] != null ? ((Number) row[2]).doubleValue() : 0;

                    // --- 1. Get Student ID for the log ---
                    int logStudentId = 0;
                    try {
                        if (refType.equalsIgnoreCase("ADMISSION")) {
                            logStudentId = ((Number) em.createNativeQuery("SELECT student_id FROM student_fee_payments WHERE enrollment_id=?")
                                    .setParameter(1, refId).getSingleResult()).intValue();
                        } else if (refType.equalsIgnoreCase("ROUND")) {
                            logStudentId = ((Number) em.createNativeQuery("SELECT student_id FROM student_fee_round_payment_master WHERE student_fee_round_payment_master_id=?")
                                    .setParameter(1, refId).getSingleResult()).intValue();
                        }
                    } catch (Exception e) {
                        logStudentId = 0;
                    }

                    // =========================================================
                    // CASE 1: PENDING → CLEARED
                    // =========================================================
                    if (filterStatus.equalsIgnoreCase("PENDING")) {

                        if (action.equalsIgnoreCase("CLEARED")) {

                            processClearance(em, refType, refId, amount);

                        } else if (action.equalsIgnoreCase("RETURNED") || action.equalsIgnoreCase("CANCELLED") || action.equalsIgnoreCase("BOUNCED")) {
                            processInvalidCheque(em, chequeId);
                        }

                    } // =========================================================
                    // CASE 2: CLEARED → PENDING (REVERSE)
                    // =========================================================
                    else if (filterStatus.equalsIgnoreCase("CLEARED")
                            && action.equalsIgnoreCase("PENDING")) {

                        // ---------- ADMISSION REVERSE ----------
                        if (refType.equalsIgnoreCase("ADMISSION")) {

                            em.createNativeQuery(
                                    "UPDATE student_fee_payments "
                                    + "SET payment_status='ACTIVE', "
                                    + "total_paid = total_paid - ?, "
                                    + "total_balance = total_fee - total_paid "
                                    + "WHERE enrollment_id=?"
                            )
                                    .setParameter(1, amount)
                                    .setParameter(2, refId)
                                    .executeUpdate();
                        } // ---------- ROUND REVERSE ----------
                        else if (refType.equalsIgnoreCase("ROUND")) {

                            List<Object[]> details = em.createNativeQuery(
                                    "SELECT reference_type, reference_id, enrollment_id, paid_amount "
                                    + "FROM student_fee_round_payment_master_details "
                                    + "WHERE student_fee_round_payment_master_id=? AND status=1"
                            )
                                    .setParameter(1, refId)
                                    .getResultList();

                            for (Object[] d : details) {

                                String type = d[0] != null ? d[0].toString() : "";
                                int refIdInner = d[1] != null ? ((Number) d[1]).intValue() : 0;
                                int enrollmentId = d[2] != null ? ((Number) d[2]).intValue() : 0;
                                double paid = d[3] != null ? ((Number) d[3]).doubleValue() : 0;

                                int targetEnrollmentId = (refIdInner != 0) ? refIdInner : enrollmentId;

                                // COURSE REVERSE
                                if (type.equalsIgnoreCase("COURSE")) {

                                    em.createNativeQuery(
                                            "UPDATE student_fee_payments "
                                            + "SET total_paid = total_paid - ?, "
                                            + "total_balance = total_fee - total_paid "
                                            + "WHERE enrollment_id=?"
                                    )
                                            .setParameter(1, paid)
                                            .setParameter(2, targetEnrollmentId)
                                            .executeUpdate();
                                } // ADDITIONAL REVERSE
                                else if (type.equalsIgnoreCase("ADDITIONAL")) {

                                    em.createNativeQuery(
                                            "DELETE FROM student_additional_fee_payments "
                                            + "WHERE student_fee_round_payment_master_id=? "
                                            + "AND student_additional_fees_id=? "
                                            + "AND payment_method='CHEQUE'"
                                    )
                                            .setParameter(1, refId)
                                            .setParameter(2, refIdInner)
                                            .executeUpdate();
                                }
                            }
                        }

                        // --- 2. LOG THE ACTION ---
                        String logDesc = String.format("Cheque #%s (%s) status changed: %s -> %s. (Ref: %s #%d)",
                                chequeNo, bank, filterStatus.toUpperCase(), action.toUpperCase(), refType, refId);

                        logHelper.log(
                                "CHEQUE_MANAGEMENT", // action_type
                                logStudentId, // student_id
                                action.toUpperCase(),// action_performed (e.g., CLEARED, RETURNED, PENDING)
                                "CHEQUE", // payment_mode
                                amount, // amount
                                logDesc, // description
                                username // user (assuming username is stored here)
                        );
                    } // =========================================================
                    // CASE 3: RETURNED / BOUNCED / CANCELLED
                    // =========================================================
                    else if ((filterStatus.equalsIgnoreCase("RETURNED")
                            || filterStatus.equalsIgnoreCase("BOUNCED")
                            || filterStatus.equalsIgnoreCase("CANCELLED"))
                            && action.equalsIgnoreCase("PENDING")) {

                        System.out.println("@@@@@@@@@@ Cheq id = " + chequeId);
                        processtoPendingCheque(em, chequeId);

                        String logDesc = String.format(
                                "RE-ACTIVATED CHEQUE: Status changed from %s back to PENDING. Chq #%s (%s), Ref: %s #%d",
                                filterStatus.toUpperCase(), chequeNo, bank, refType, refId
                        );

                        logHelper.log(
                                "CHEQUE_MANAGEMENT", // action_type
                                logStudentId, // student_id (fetched at top of loop)
                                "STATUS_RESET", // action_performed
                                "CHEQUE", // payment_mode
                                amount, // amount
                                logDesc, // description
                                username // user
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

    private void processClearance(EntityManager em, String refType, int refId, double amount) {

        // ---------- ADMISSION ----------
        if (refType.equalsIgnoreCase("ADMISSION")) {

            em.createNativeQuery(
                    "UPDATE student_fee_payments "
                    + "SET payment_status='PAID', "
                    + "total_paid = total_paid + ?, "
                    + "total_balance = total_fee - total_paid "
                    + "WHERE enrollment_id=?"
            )
                    .setParameter(1, amount)
                    .setParameter(2, refId)
                    .executeUpdate();
        } // ---------- ROUND ----------
        else if (refType.equalsIgnoreCase("ROUND")) {

            List<Object[]> details = em.createNativeQuery(
                    "SELECT reference_type, reference_id, enrollment_id, paid_amount "
                    + "FROM student_fee_round_payment_master_details "
                    + "WHERE student_fee_round_payment_master_id=? AND status=1"
            )
                    .setParameter(1, refId)
                    .getResultList();

            for (Object[] d : details) {

                String type = d[0] != null ? d[0].toString() : "";
                int refIdInner = d[1] != null ? ((Number) d[1]).intValue() : 0;
                int enrollmentId = d[2] != null ? ((Number) d[2]).intValue() : 0;
                double paid = d[3] != null ? ((Number) d[3]).doubleValue() : 0;

                int targetEnrollmentId = (refIdInner != 0) ? refIdInner : enrollmentId;

                if (type.equalsIgnoreCase("COURSE")) {

                    em.createNativeQuery(
                            "UPDATE student_fee_payments "
                            + "SET total_paid = total_paid + ?, "
                            + "total_balance = total_fee - total_paid "
                            + "WHERE enrollment_id=?"
                    )
                            .setParameter(1, paid)
                            .setParameter(2, targetEnrollmentId)
                            .executeUpdate();

                    // ✅ FIX: status update
                    em.createNativeQuery(
                            "UPDATE student_fee_payments "
                            + "SET payment_status = CASE "
                            + "WHEN total_balance <= 0 THEN 'COMPLETED' "
                            + "ELSE 'ACTIVE' END "
                            + "WHERE enrollment_id = ?"
                    )
                            .setParameter(1, targetEnrollmentId)
                            .executeUpdate();

                } else if (type.equalsIgnoreCase("ADDITIONAL")) {

                    em.createNativeQuery(
                            "INSERT INTO student_additional_fee_payments "
                            + "(student_additional_fees_id, student_fee_round_payment_master_id, paid_date, amount_paid, payment_method, user, status) "
                            + "VALUES (?, ?, NOW(), ?, 'CHEQUE', ?, 1)"
                    )
                            .setParameter(1, refIdInner)
                            .setParameter(2, refId)
                            .setParameter(3, paid)
                            .setParameter(4, username)
                            .executeUpdate();
                }
            }
        }
    }

    private void processInvalidCheque(EntityManager em, int chequeId) {

        Object[] row = (Object[]) em.createNativeQuery(
                "SELECT reference_type, reference_id, cheque_amount "
                + "FROM student_fee_cheque_details "
                + "WHERE student_fee_cheque_details_id=?"
        )
                .setParameter(1, chequeId)
                .getSingleResult();

        String refType = row[0] != null ? row[0].toString() : "";
        int refId = row[1] != null ? ((Number) row[1]).intValue() : 0;

        // =====================================================
        // 🔥 DISABLE INSTALLMENTS (ALL TYPES)
        // =====================================================
        em.createNativeQuery(
                "UPDATE student_fee_installments "
                + "SET status = 0 "
                + "WHERE student_fee_round_payment_master_id = ?"
        )
                .setParameter(1, refId)
                .executeUpdate();

        // =====================================================
        // 🔥 REMOVE PAYMENT EFFECT
        // =====================================================
        if (refType.equalsIgnoreCase("ADMISSION")) {

            // restore installment rows
            em.createNativeQuery(
                    "UPDATE student_fee_installments "
                    + "SET status = 0 "
                    + "WHERE enrollment_id = ? "
                    + "AND payment_type = 'ADMISSION'"
            )
                    .setParameter(1, refId)
                    .executeUpdate();

            // restore payment table
            em.createNativeQuery(
                    "UPDATE student_fee_payments "
                    + "SET payment_status='PENDING', "
                    + "status = 0 "
                    + "WHERE enrollment_id=? "
                    + "AND payment_type='ADMISSION'"
            )
                    .setParameter(1, refId)
                    .executeUpdate();

        } else if (refType.equalsIgnoreCase("ROUND")) {

            em.createNativeQuery(
                    "UPDATE student_fee_round_payment_master SET status=0 WHERE student_fee_round_payment_master_id=?"
            ).setParameter(1, refId).executeUpdate();

            em.createNativeQuery(
                    "UPDATE student_fee_round_payment_master_details SET status=0 WHERE student_fee_round_payment_master_id=?"
            ).setParameter(1, refId).executeUpdate();
        }
    }

    private void processtoPendingCheque(EntityManager em, int chequeId) {

        Object[] row = (Object[]) em.createNativeQuery(
                "SELECT reference_type, reference_id, cheque_amount "
                + "FROM student_fee_cheque_details "
                + "WHERE student_fee_cheque_details_id=?"
        )
                .setParameter(1, chequeId)
                .getSingleResult();

        String refType = row[0] != null ? row[0].toString() : "";
        int refId = row[1] != null ? ((Number) row[1]).intValue() : 0;

        System.out.println("refType = " + refType);
        System.out.println("refId = " + refId);

        // =====================================================
        // 🔥 DISABLE INSTALLMENTS (ALL TYPES)
        // =====================================================
        em.createNativeQuery(
                "UPDATE student_fee_installments "
                + "SET status = 1 "
                + "WHERE student_fee_round_payment_master_id = ?"
        )
                .setParameter(1, refId)
                .executeUpdate();

        // =====================================================
        // 🔥 REMOVE PAYMENT EFFECT
        // =====================================================
        if (refType.equalsIgnoreCase("ADMISSION")) {

            // restore installment rows
            em.createNativeQuery(
                    "UPDATE student_fee_installments "
                    + "SET status = 1 "
                    + "WHERE enrollment_id = ? "
                    + "AND payment_type = 'ADMISSION'"
            )
                    .setParameter(1, refId)
                    .executeUpdate();

            // restore payment table
            em.createNativeQuery(
                    "UPDATE student_fee_payments "
                    + "SET payment_status='PENDING', "
                    + "status = 1 "
                    + "WHERE enrollment_id=? "
                    + "AND payment_type='ADMISSION'"
            )
                    .setParameter(1, refId)
                    .executeUpdate();

        } else if (refType.equalsIgnoreCase("ROUND")) {

            System.out.println("RESTORE ROUND -> refId = " + refId);

            // 1. restore round master
            int masterUpdated = em.createNativeQuery(
                    "UPDATE student_fee_round_payment_master "
                    + "SET status = 1 "
                    + "WHERE student_fee_round_payment_master_id = ?"
            )
                    .setParameter(1, refId)
                    .executeUpdate();

            System.out.println("MASTER UPDATED = " + masterUpdated);

            // 2. restore round details
            int detailUpdated = em.createNativeQuery(
                    "UPDATE student_fee_round_payment_master_details "
                    + "SET status = 1 "
                    + "WHERE student_fee_round_payment_master_id = ?"
            )
                    .setParameter(1, refId)
                    .executeUpdate();

            System.out.println("DETAILS UPDATED = " + detailUpdated);

            // 3. restore installments linked to this round
            int installmentUpdated = em.createNativeQuery(
                    "UPDATE student_fee_installments "
                    + "SET status = 1 "
                    + "WHERE student_fee_round_payment_master_id = ?"
            )
                    .setParameter(1, refId)
                    .executeUpdate();

            System.out.println("INSTALLMENTS UPDATED = " + installmentUpdated);
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
//                String action = String.valueOf(model.getValueAt(i, 8)); // action column
//
//                if (action == null || action.equalsIgnoreCase(filterStatus)) {
//                    continue;
//                }
//
//                String chequeNo = String.valueOf(model.getValueAt(i, 4));
//                String bankBranch = String.valueOf(model.getValueAt(i, 5));
//                String chequeDate = String.valueOf(model.getValueAt(i, 6));
//
//                String bank = bankBranch.split("-")[0].trim();
//
//                // ============================
//                // GET CHEQUE IDS
//                // ============================
//                List<Integer> chequeIds = em.createNativeQuery(
//                        "SELECT student_fee_cheque_details_id "
//                        + "FROM student_fee_cheque_details "
//                        + "WHERE cheque_no=? AND bank=? AND cheque_date=? AND status=1"
//                )
//                        .setParameter(1, chequeNo)
//                        .setParameter(2, bank)
//                        .setParameter(3, chequeDate)
//                        .getResultList();
//
//                if (chequeIds.isEmpty()) {
//                    continue;
//                }
//                System.out.println("&&& CHKK IDS = " + chequeIds);
//
//                // ============================
//                // UPDATE STATUS
//                // ============================
//                em.createNativeQuery(
//                        "UPDATE student_fee_cheque_details "
//                        + "SET cheque_status=? "
//                        + "WHERE cheque_no=? AND bank=? AND cheque_date=?"
//                )
//                        .setParameter(1, action)
//                        .setParameter(2, chequeNo)
//                        .setParameter(3, bank)
//                        .setParameter(4, chequeDate)
//                        .executeUpdate();
//
//                // =========================================================
//                // CASE 1: PENDING → CLEARED
//                // =========================================================
//                if (filterStatus.equalsIgnoreCase("PENDING")
//                        && action.equalsIgnoreCase("CLEARED")) {
//
//                    for (Integer chequeId : chequeIds) {
//
//                        Object[] row = (Object[]) em.createNativeQuery(
//                                "SELECT reference_type, reference_id, cheque_amount "
//                                + "FROM student_fee_cheque_details "
//                                + "WHERE student_fee_cheque_details_id=?"
//                        )
//                                .setParameter(1, chequeId)
//                                .getSingleResult();
//
//                        String refType = row[0].toString();
//                        int refId = ((Number) row[1]).intValue();
//                        double amount = ((Number) row[2]).doubleValue();
//
//                        System.out.println("&&& CHKK REF IDS = " + refId + "REF TYPE = " + refType);
//
//                        // ============================
//                        // ADMISSION
//                        // ============================
//                        if (refType.equalsIgnoreCase("ADMISSION")) {
//
//                            em.createNativeQuery(
//                                    "UPDATE student_fee_payments "
//                                    + "SET payment_status='PAID', "
//                                    + "total_paid = total_paid + ?, "
//                                    + "total_balance = total_fee - total_paid "
//                                    + "WHERE enrollment_id=?"
//                            )
//                                    .setParameter(1, amount)
//                                    .setParameter(2, refId)
//                                    .executeUpdate();
//                        } // ============================
//                        // ROUND
//                        // ============================
//                        else if (refType.equalsIgnoreCase("ROUND")) {
//
//                            System.out.println("&&& ROUND INSIDE");
//
//                            List<Object[]> details = em.createNativeQuery(
//                                    "SELECT reference_type, reference_id, enrollment_id, paid_amount "
//                                    + "FROM student_fee_round_payment_master_details "
//                                    + "WHERE student_fee_round_payment_master_id=? AND status=1"
//                            )
//                                    .setParameter(1, refId)
//                                    .getResultList();
//
//                            for (Object[] d : details) {
//
//                                String type = d[0] != null ? d[0].toString() : "";
//
//                                int refIdInner = (d[1] != null) ? ((Number) d[1]).intValue() : 0;
//                                int enrollmentId = (d[2] != null) ? ((Number) d[2]).intValue() : 0;
//                                double paid = (d[3] != null) ? ((Number) d[3]).doubleValue() : 0;
//
//                                // ============================
//                                // COURSE
//                                // ============================
//                                if (type.equalsIgnoreCase("COURSE")) {
//
//                                    int targetEnrollmentId = (refIdInner != 0) ? refIdInner : enrollmentId;
//
//                                    System.out.println("&&& COURSE ENROLLMENT ID = " + targetEnrollmentId);
//
//                                    em.createNativeQuery(
//                                            "UPDATE student_fee_payments "
//                                            + "SET total_paid = total_paid + ?, "
//                                            + "total_balance = total_fee - total_paid "
//                                            + "WHERE enrollment_id=?"
//                                    )
//                                            .setParameter(1, paid)
//                                            .setParameter(2, targetEnrollmentId)
//                                            .executeUpdate();
//                                } // ============================
//                                // ADDITIONAL
//                                // ============================
//                                else if (type.equalsIgnoreCase("ADDITIONAL")) {
//
//                                    em.createNativeQuery(
//                                            "INSERT INTO student_additional_fee_payments "
//                                            + "(student_additional_fees_id, student_fee_round_payment_master_id, paid_date, amount_paid, payment_method, user, status) "
//                                            + "VALUES (?, ?, NOW(), ?, 'CHEQUE', ?, 1)"
//                                    )
//                                            .setParameter(1, refIdInner)
//                                            .setParameter(2, refId)
//                                            .setParameter(3, paid)
//                                            .setParameter(4, username)
//                                            .executeUpdate();
//                                }
//                            }
//                        }
//                    }
//                } // =========================================================
//                // CASE 2: CLEARED → PENDING (REVERSE)
//                // =========================================================
//                else if (refType.equalsIgnoreCase("ROUND")) {
//
//                    List<Object[]> details = em.createNativeQuery(
//                            "SELECT reference_type, reference_id, enrollment_id, paid_amount "
//                            + "FROM student_fee_round_payment_master_details "
//                            + "WHERE student_fee_round_payment_master_id=? AND status=1"
//                    )
//                            .setParameter(1, refId)
//                            .getResultList();
//
//                    for (Object[] d : details) {
//
//                        String type = d[0] != null ? d[0].toString() : "";
//
//                        int refIdInner = (d[1] != null) ? ((Number) d[1]).intValue() : 0;
//                        int enrollmentId = (d[2] != null) ? ((Number) d[2]).intValue() : 0;
//                        double paid = (d[3] != null) ? ((Number) d[3]).doubleValue() : 0;
//
//                        // ============================
//                        // COURSE REVERSE
//                        // ============================
//                        if (type.equalsIgnoreCase("COURSE")) {
//
//                            int targetEnrollmentId = (refIdInner != 0) ? refIdInner : enrollmentId;
//
//                            System.out.println("REVERSE COURSE → ENROLLMENT ID = " + targetEnrollmentId);
//
//                            em.createNativeQuery(
//                                    "UPDATE student_fee_payments "
//                                    + "SET total_paid = total_paid - ?, "
//                                    + "total_balance = total_fee - total_paid "
//                                    + "WHERE enrollment_id=?"
//                            )
//                                    .setParameter(1, paid)
//                                    .setParameter(2, targetEnrollmentId)
//                                    .executeUpdate();
//                        } // ============================
//                        // ADDITIONAL REVERSE
//                        // ============================
//                        else if (type.equalsIgnoreCase("ADDITIONAL")) {
//
//                            em.createNativeQuery(
//                                    "DELETE FROM student_additional_fee_payments "
//                                    + "WHERE student_fee_round_payment_master_id=? "
//                                    + "AND student_additional_fees_id=? "
//                                    + "AND payment_method='CHEQUE'"
//                            )
//                                    .setParameter(1, refId)
//                                    .setParameter(2, refIdInner)
//                                    .executeUpdate();
//                        }
//                    }
//                } // =========================================================
//                // CASE 3: RETURNED / BOUNCED / CANCELLED
//                // =========================================================
//                else if ((filterStatus.equalsIgnoreCase("RETURNED")
//                        || filterStatus.equalsIgnoreCase("BOUNCED")
//                        || filterStatus.equalsIgnoreCase("CANCELLED"))
//                        && action.equalsIgnoreCase("PENDING")) {
//                    // only status change already done
//                }
//            }
//
//            em.getTransaction().commit();
//
//            JOptionPane.showMessageDialog(this, "Cheque Update Completed!");
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
//                String action = String.valueOf(model.getValueAt(i, 8)); // action column
//
//                if (action == null || action.equalsIgnoreCase(filterStatus)) {
//                    continue;
//                }
//
//                String subCategory = String.valueOf(model.getValueAt(i, 2)); // ROUND / ADMISSION
//                String chequeNo = String.valueOf(model.getValueAt(i, 4));
//                String bankBranch = String.valueOf(model.getValueAt(i, 5));
//                String chequeDate = String.valueOf(model.getValueAt(i, 6));
//
//                String bank = bankBranch.split("-")[0].trim();
//
//                // ============================
//                // 🔥 GET CHEQUE IDS
//                // ============================
//                List<Integer> chequeIds = em.createNativeQuery(
//                        "SELECT student_fee_cheque_details_id "
//                        + "FROM student_fee_cheque_details "
//                        + "WHERE cheque_no=? AND bank=? AND cheque_date=? AND status=1"
//                )
//                        .setParameter(1, chequeNo)
//                        .setParameter(2, bank)
//                        .setParameter(3, chequeDate)
//                        .getResultList();
//
//                if (chequeIds.isEmpty()) {
//                    continue;
//                }
//
//                // ============================
//                // UPDATE CHEQUE STATUS
//                // ============================
//                em.createNativeQuery(
//                        "UPDATE student_fee_cheque_details "
//                        + "SET cheque_status=? "
//                        + "WHERE cheque_no=? AND bank=? AND cheque_date=?"
//                )
//                        .setParameter(1, action)
//                        .setParameter(2, chequeNo)
//                        .setParameter(3, bank)
//                        .setParameter(4, chequeDate)
//                        .executeUpdate();
//
//                // =========================================================
//                // 🔥 CASE 1: PENDING → CLEARED
//                // =========================================================
//                if (filterStatus.equalsIgnoreCase("PENDING")
//                        && action.equalsIgnoreCase("CLEARED")) {
//
//                    for (Integer chequeId : chequeIds) {
//
//                        Object[] row = (Object[]) em.createNativeQuery(
//                                "SELECT reference_type, reference_id, cheque_amount "
//                                + "FROM student_fee_cheque_details "
//                                + "WHERE student_fee_cheque_details_id=?"
//                        )
//                                .setParameter(1, chequeId)
//                                .getSingleResult();
//
//                        String refType = row[0].toString();
//                        int refId = ((Number) row[1]).intValue();
//                        double amount = ((Number) row[2]).doubleValue();
//
//                        // ============================
//                        // 🔹 ROUND PAYMENT
//                        // ============================
//                        if (refType.equalsIgnoreCase("ROUND")) {
//
//                            List<Object[]> details = em.createNativeQuery(
//                                    "SELECT reference_type, reference_id, paid_amount "
//                                    + "FROM student_fee_round_payment_master_details "
//                                    + "WHERE student_fee_round_payment_master_id=? AND status=1"
//                            )
//                                    .setParameter(1, refId)
//                                    .getResultList();
//
//                            for (Object[] d : details) {
//
//                                String type = d[0].toString();
//
//                                if (d[1] == null) {
//                                    continue; // skip broken DB record
//                                }
//
//                                int id = ((Number) d[1]).intValue();
//                                double paid = ((Number) d[2]).doubleValue();
//
//                                // COURSE
//                                if (type.equalsIgnoreCase("COURSE")) {
//
//                                    em.createNativeQuery(
//                                            "UPDATE student_fee_payments "
//                                            + "SET total_paid = total_paid + ?, "
//                                            + "total_balance = total_fee - (total_paid + ?) "
//                                            + "WHERE enrollment_id=?"
//                                    )
//                                            .setParameter(1, paid)
//                                            .setParameter(2, paid)
//                                            .setParameter(3, id)
//                                            .executeUpdate();
//                                } // ADDITIONAL
//                                else if (type.equalsIgnoreCase("ADDITIONAL")) {
//
//                                    em.createNativeQuery(
//                                            "INSERT INTO student_additional_fee_payments "
//                                            + "(student_additional_fees_id, student_fee_round_payment_master_id, paid_date, amount_paid, payment_method, user, status) "
//                                            + "VALUES (?, ?, NOW(), ?, 'CHEQUE', ?, 1)"
//                                    )
//                                            .setParameter(1, id)
//                                            .setParameter(2, refId)
//                                            .setParameter(3, paid)
//                                            .setParameter(4, username)
//                                            .executeUpdate();
//                                }
//                            }
//                        } // ============================
//                        // 🔹 ADMISSION PAYMENT
//                        // ============================
//                        else if (refType.equalsIgnoreCase("ADMISSION")) {
//
//                            em.createNativeQuery(
//                                    "UPDATE student_fee_payments "
//                                    + "SET payment_status='PAID', "
//                                    + "total_paid = total_fee, "
//                                    + "total_balance = 0 "
//                                    + "WHERE enrollment_id=?"
//                            )
//                                    .setParameter(1, refId)
//                                    .executeUpdate();
//                        }
//                    }
//                } // =========================================================
//                // 🔥 CASE 2: CLEARED → PENDING (REVERSE)
//                // =========================================================
//                else if (filterStatus.equalsIgnoreCase("CLEARED")
//                        && action.equalsIgnoreCase("PENDING")) {
//
//                    for (Integer chequeId : chequeIds) {
//
//                        Object[] row = (Object[]) em.createNativeQuery(
//                                "SELECT reference_type, reference_id, cheque_amount "
//                                + "FROM student_fee_cheque_details "
//                                + "WHERE student_fee_cheque_details_id=?"
//                        )
//                                .setParameter(1, chequeId)
//                                .getSingleResult();
//
//                        String refType = row[0].toString();
//                        int refId = ((Number) row[1]).intValue();
//
//                        // ============================
//                        // 🔹 ROUND REVERSE
//                        // ============================
//                        if (refType.equalsIgnoreCase("ROUND")) {
//
//                            List<Object[]> details = em.createNativeQuery(
//                                    "SELECT reference_type, reference_id, paid_amount "
//                                    + "FROM student_fee_round_payment_master_details "
//                                    + "WHERE student_fee_round_payment_master_id=? AND status=1"
//                            )
//                                    .setParameter(1, refId)
//                                    .getResultList();
//
//                            for (Object[] d : details) {
//
//                                String type = d[0].toString();
//                                int id = ((Number) d[1]).intValue();
//                                double paid = ((Number) d[2]).doubleValue();
//
//                                // COURSE REVERSE
//                                if (type.equalsIgnoreCase("COURSE")) {
//
//                                    em.createNativeQuery(
//                                            "UPDATE student_fee_payments "
//                                            + "SET total_paid = total_paid - ?, "
//                                            + "total_balance = total_fee - (total_paid - ?) "
//                                            + "WHERE enrollment_id=?"
//                                    )
//                                            .setParameter(1, paid)
//                                            .setParameter(2, paid)
//                                            .setParameter(3, id)
//                                            .executeUpdate();
//                                } // ADDITIONAL REVERSE
//                                else if (type.equalsIgnoreCase("ADDITIONAL")) {
//
//                                    em.createNativeQuery(
//                                            "DELETE FROM student_additional_fee_payments "
//                                            + "WHERE student_fee_round_payment_master_id=? "
//                                            + "AND student_additional_fees_id=? "
//                                            + "AND payment_method='CHEQUE'"
//                                    )
//                                            .setParameter(1, refId)
//                                            .setParameter(2, id)
//                                            .executeUpdate();
//                                }
//                            }
//                        } // ============================
//                        // 🔹 ADMISSION REVERSE
//                        // ============================
//                        else if (refType.equalsIgnoreCase("ADMISSION")) {
//
//                            em.createNativeQuery(
//                                    "UPDATE student_fee_payments "
//                                    + "SET payment_status='PENDING', "
//                                    + "total_paid = 0, "
//                                    + "total_balance = total_fee "
//                                    + "WHERE enrollment_id=?"
//                            )
//                                    .setParameter(1, refId)
//                                    .executeUpdate();
//                        }
//                    }
//                } // =========================================================
//                // 🔥 CASE 3: RETURNED / BOUNCED / CANCELLED → PENDING
//                // =========================================================
//                else if ((filterStatus.equalsIgnoreCase("RETURNED")
//                        || filterStatus.equalsIgnoreCase("BOUNCED")
//                        || filterStatus.equalsIgnoreCase("CANCELLED"))
//                        && action.equalsIgnoreCase("PENDING")) {
//                    // ✅ ONLY STATUS CHANGE (already done above)
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
    private void getTotalFromTable() {

        DefaultTableModel model = (DefaultTableModel) chq_handling_cheq_details_table.getModel();

        double total = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object val = GeneralMethods.parseCommaNumber(model.getValueAt(i, 7).toString()); // balance column

            if (val != null) {
                total += GeneralMethods.parseCommaNumber(val.toString());
            }
        }

        cheque_details_value_label.setText(GeneralMethods.formatWithComma(total));
    }

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
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save32.png"))); // NOI18N
        jButton1.setToolTipText("Cheque Clearance");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cheque Informations", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        chq_handling_cheq_details_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Category", "Sub Category", "Name", "Cheque number", "Bank Name", "cheque_date", "cheque_amount", "Action", "Cheque ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(chq_handling_cheq_details_table);
        if (chq_handling_cheq_details_table.getColumnModel().getColumnCount() > 0) {
            chq_handling_cheq_details_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            chq_handling_cheq_details_table.getColumnModel().getColumn(1).setPreferredWidth(100);
            chq_handling_cheq_details_table.getColumnModel().getColumn(2).setPreferredWidth(100);
            chq_handling_cheq_details_table.getColumnModel().getColumn(3).setPreferredWidth(120);
            chq_handling_cheq_details_table.getColumnModel().getColumn(4).setPreferredWidth(150);
            chq_handling_cheq_details_table.getColumnModel().getColumn(9).setMinWidth(50);
            chq_handling_cheq_details_table.getColumnModel().getColumn(9).setPreferredWidth(50);
            chq_handling_cheq_details_table.getColumnModel().getColumn(9).setMaxWidth(50);
        }

        firstName_label5.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        firstName_label5.setText("Total Value");

        cheque_details_value_label.setFont(new java.awt.Font("Roboto Medium", 1, 14)); // NOI18N
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
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(firstName_label5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cheque_details_value_label, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cheque_details_value_label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        DefaultTableModel model = (DefaultTableModel) chq_handling_cheq_details_table.getModel();
        if (model.getRowCount() == -1) {
            return;
        }
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
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
