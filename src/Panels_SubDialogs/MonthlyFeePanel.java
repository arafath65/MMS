package Panels_SubDialogs;

import Additional.LedgerDAO;
import Classes.ChequeNumberFormatter;
import Classes.GeneralMethods;
import Classes.GradientButton;
import Classes.HibernateConfig;
import Classes.LedgerHelper;
import Classes.LogHelper;
import Classes.ModernDialog;
import Classes.NumberOnlyFilter;
import Classes.styleDateChooser;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
import Panels.Fees_Management;
import static Panels.Fees_Management.selectedEnrollmentId;
import static Panels.Fees_Management.selectedStudentIds;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;

public class MonthlyFeePanel extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    String username = "";

    public MonthlyFeePanel() {
        initComponents();

        mm_fees_monthly_table.getColumnModel().getColumn(5).setMinWidth(0);
        mm_fees_monthly_table.getColumnModel().getColumn(5).setMaxWidth(0);
        mm_fees_monthly_table.getColumnModel().getColumn(5).setWidth(0);

//        mm_fees_monthly_table.getColumnModel().getColumn(4).setMinWidth(0);
//        mm_fees_monthly_table.getColumnModel().getColumn(4).setMaxWidth(0);
//        mm_fees_monthly_table.getColumnModel().getColumn(4).setWidth(0);
        mm_fees_Monthly_payment_date.setDate(new Date());
        // fm_fees_oneTime_payment_date.setDate(new Date());

        styleDateChooser.applyDarkTheme(mm_fees_Monthly_payment_date);
        styleDateChooser.applyDarkTheme(mm_fees_cheq_cheque_date);

        mm_fees_Monthly_fee_cal_Textfield.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        mm_fees_Monthly_fee_cal_Textfield.putClientProperty("JComponent.focusWidth", 2);

        mm_fees_Monthly_total_paid_Textfield.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        mm_fees_Monthly_total_paid_Textfield.putClientProperty("JComponent.focusWidth", 2);

        fm_fees_cheq_full_fees_cal_Textfield.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        fm_fees_cheq_full_fees_cal_Textfield.putClientProperty("JComponent.focusWidth", 2);

        mm_fees_cheq_cheque_number.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        mm_fees_cheq_cheque_number.putClientProperty("JComponent.focusWidth", 2);

        mm_fees_cheq_cheque_amount.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        mm_fees_cheq_cheque_amount.putClientProperty("JComponent.focusWidth", 2);

        mm_fees_cheq_cheque_bank.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        mm_fees_cheq_cheque_bank.putClientProperty("JComponent.focusWidth", 2);

        JComboPopulatesBankInfo();
    }

    private void JComboPopulatesBankInfo() {
        // Medicine brand combo
        mm_fees_cheq_cheque_bank.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = mm_fees_cheq_cheque_bank.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(mm_fees_cheq_cheque_bank, "bank_names", "bank_names_srilanka", input);
            }

        });
        setupComboSelectionListener(mm_fees_cheq_cheque_bank, mm_fees_cheq_cheque_branch);

        new ChequeNumberFormatter(mm_fees_cheq_cheque_number, mm_fees_cheq_cheque_bank, mm_fees_cheq_cheque_branch);
        PlainDocument doc = (PlainDocument) mm_fees_cheq_cheque_number.getDocument();
        doc.setDocumentFilter(new NumberOnlyFilter());
    }

    private boolean itemSelectedByUser = false;

    public void setupComboSelectionListener(JComboBox<String> comboBox, JComponent nextFocusComponent) {
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                itemSelectedByUser = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (itemSelectedByUser) {
                    Object selected = comboBox.getSelectedItem();
                    if (selected != null) {
                        String selectedValue = selected.toString().trim();
                        if (!selectedValue.isEmpty() && isValueFromList(comboBox, selectedValue)) {
                            nextFocusComponent.requestFocus();
                        }
                    }
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                itemSelectedByUser = false;
            }
        });

        // Detect user selection from keyboard (Enter) or mouse (click)
        comboBox.addActionListener(e -> {
            if (comboBox.isPopupVisible()) {
                itemSelectedByUser = true;
            }
        });

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

    private int getTotalBalanceFromTable() {

        DefaultTableModel model = (DefaultTableModel) Fees_Management.fm_fees_course_table.getModel();

        int totalBalance = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object val = model.getValueAt(i, 9); // balance column

            if (val != null) {
                totalBalance += GeneralMethods.parseCommaNumber(val.toString());
            }
        }

        return totalBalance;
    }

    public void distributeMonthlyPayment(
            JTable table,
            JTextField totalPaidField,
            JTextField monthlyFeeField
    ) {

        try {

            int totalPaid = GeneralMethods.parseCommaNumber(totalPaidField.getText().trim());
            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());

            if (monthlyFee <= 0) {
                JOptionPane.showMessageDialog(null, "Monthly fee invalid!");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();

            // ============================
            // 1. CLEAR PREVIOUS INPUT
            // ============================
            for (int i = 0; i < model.getRowCount(); i++) {

                Object statusObj = model.getValueAt(i, 4);
                String status = (statusObj != null) ? statusObj.toString() : "";

                if (!"PAID".equalsIgnoreCase(status)) {
                    model.setValueAt(0, i, 3);
                }
            }

            // ============================
            // 2. FULL MONTH DISTRIBUTION
            // ============================
            int monthsToFill = totalPaid / monthlyFee;
            int count = 0;

            for (int i = 0; i < model.getRowCount(); i++) {

                if (count >= monthsToFill) {
                    break;
                }

                Object statusObj = model.getValueAt(i, 4);
                String status = (statusObj != null) ? statusObj.toString() : "";

                if ("PAID".equalsIgnoreCase(status)) {
                    continue;
                }

                model.setValueAt(monthlyFee, i, 3);
                model.setValueAt("Now Paid", i, 4);
                count++;
            }

            // ============================
            // 3. PARTIAL AMOUNT (REMAINING)
            // ============================
            int remaining = totalPaid - (monthsToFill * monthlyFee);

            if (remaining > 0) {

                for (int i = 0; i < model.getRowCount(); i++) {

                    Object statusObj = model.getValueAt(i, 4);
                    String status = (statusObj != null) ? statusObj.toString() : "";

                    if (!"PAID".equalsIgnoreCase(status)
                            && !"Now Paid".equalsIgnoreCase(status)) {

                        model.setValueAt(remaining, i, 3);
                        model.setValueAt("Now Paid", i, 4);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input!");
        }
    }

    public void saveMonthlyFullPayment(
            int enrollmentId,
            JTable table,
            JTextField monthlyFeeField,
            String paymentMethod
    ) {
        EntityManager em = HibernateConfig.getEntityManager();

        try {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int rowCount = model.getRowCount();

            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
            int payingFee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_paid_Textfield.getText().trim());
            int balanceFee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_tot_pending_balancee_Textfield.getText());
            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();

            // =====================================================
            // VALIDATION
            // =====================================================
            if (payingFee < 0) {
                JOptionPane.showMessageDialog(null, "Invalid payment amount!");
                return;
            }

            if (payingFee > balanceFee) {
                JOptionPane.showMessageDialog(null, "Cannot exceeded total balance!");
                return;
            }

            if (payingFee == 0 && (note == null || note.trim().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Zero payment requires remarks!");
                return;
            }

            em.getTransaction().begin();

            int paymentId = ((Number) em.createNativeQuery(
                    "SELECT student_fee_payments_id FROM student_fee_payments "
                    + "WHERE enrollment_id = ? AND status = 1"
            ).setParameter(1, enrollmentId).getSingleResult()).intValue();

            Object lastNoObj = em.createNativeQuery(
                    "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
            ).setParameter(1, enrollmentId).getSingleResult();

            int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;

            int remainingAmount = payingFee;

            // =====================================================
            // DEBUG: FIND MONTHS WITH REMARKS
            // =====================================================
            System.out.println("===== DEBUG: PARTIAL MONTHS WITH REMARKS =====");

            List<Object[]> debugList = em.createNativeQuery(
                    "SELECT month_for, COALESCE(SUM(amount_paid),0), GROUP_CONCAT(remarks) "
                    + "FROM student_fee_installments "
                    + "WHERE enrollment_id=? AND status=1 "
                    + "GROUP BY month_for"
            )
                    .setParameter(1, enrollmentId)
                    .getResultList();

            Set<String> skipMonths = new HashSet<>();

            for (Object[] row : debugList) {
                String m = row[0].toString();
                int paid = ((Number) row[1]).intValue();
                String remarks = row[2] != null ? row[2].toString() : "";

                if (remarks != null && !remarks.trim().isEmpty() && paid < monthlyFee) {
                    System.out.println("⛔ SKIP MONTH (remarks exist): " + m + " | Paid: " + paid + " | Remarks: " + remarks);
                    skipMonths.add(m);
                }
            }

            // =====================================================
            // ZERO PAYMENT
            // =====================================================
            if (payingFee == 0) {

                List<Object[]> lastPaidRows = em.createNativeQuery(
                        "SELECT month_for FROM student_fee_installments "
                        + "WHERE enrollment_id=? AND status=1 "
                        + "ORDER BY month_for DESC"
                ).setParameter(1, enrollmentId).setMaxResults(1).getResultList();

                String targetMonth;

                if (!lastPaidRows.isEmpty()) {
                    String lastMonth = lastPaidRows.get(0)[0].toString();

                    String[] parts = lastMonth.split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);

                    month++;
                    if (month > 12) {
                        month = 1;
                        year++;
                    }

                    targetMonth = String.format("%04d-%02d", year, month);
                } else {
                    targetMonth = model.getValueAt(0, 1) + "-"
                            + String.format("%02d", GeneralMethods.getMonthNumber(model.getValueAt(0, 2).toString()));
                }

                installmentNo++;

                em.createNativeQuery(
                        "INSERT INTO student_fee_installments "
                        + "(student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
                        + "payment_date, payment_method, payment_type, month_for, remarks, status) "
                        + "VALUES (?, ?, ?, 0, NOW(), ?, ?, ?, ?, 1)"
                )
                        .setParameter(1, paymentId)
                        .setParameter(2, enrollmentId)
                        .setParameter(3, installmentNo)
                        .setParameter(4, paymentMethod)
                        .setParameter(5, "MONTHLY")
                        .setParameter(6, targetMonth)
                        .setParameter(7, note)
                        .executeUpdate();

                int installmentId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).intValue();

                // ✅ LEDGER
                LedgerHelper.saveLedger(
                        em,
                        0,
                        "CREDIT",
                        "Zero payment for month " + targetMonth,
                        "STUDENT_FEES",
                        installmentId,
                        paymentMethod,
                        "MONTHLY_FEE",
                        username
                );

                // ✅ LOG
                LogHelper.saveLog(
                        em,
                        "STUDENT_FEES",
                        installmentId,
                        "INSERT",
                        0,
                        paymentMethod,
                        "Zero payment for " + targetMonth + " | " + note,
                        username
                );

                em.getTransaction().commit();
                JOptionPane.showMessageDialog(null, "Zero payment saved for " + targetMonth);
                return;
            }

            // =====================================================
            // LESS THAN MONTH VALIDATION
            // =====================================================
            if (payingFee < monthlyFee && (note == null || note.trim().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Remarks required!");
                return;
            }

            Set<String> filledMonths = new HashSet<>();

            // =====================================================
            // FILL PREVIOUS PARTIAL (NO REMARKS ONLY)
            // =====================================================
            List<Object[]> partialMonths = em.createNativeQuery(
                    "SELECT month_for, COALESCE(SUM(amount_paid),0) "
                    + "FROM student_fee_installments "
                    + "WHERE enrollment_id=? AND status=1 "
                    + "GROUP BY month_for "
                    + "HAVING SUM(amount_paid) < ? "
                    + "ORDER BY month_for ASC"
            )
                    .setParameter(1, enrollmentId)
                    .setParameter(2, monthlyFee)
                    .getResultList();

            for (Object[] row : partialMonths) {

                if (remainingAmount <= 0) {
                    break;
                }

                String monthFor = row[0].toString();
                int paidSoFar = ((Number) row[1]).intValue();

                if (skipMonths.contains(monthFor)) {
                    continue;
                }
                if (filledMonths.contains(monthFor)) {
                    continue;
                }

                int balance = monthlyFee - paidSoFar;
                if (balance <= 0) {
                    continue;
                }

                int payNow = Math.min(balance, remainingAmount);

                installmentNo++;

                em.createNativeQuery(
                        "INSERT INTO student_fee_installments VALUES (NULL,?,?,?,?,NOW(),?,?,?, ?,1)"
                )
                        .setParameter(1, paymentId)
                        .setParameter(2, enrollmentId)
                        .setParameter(3, installmentNo)
                        .setParameter(4, payNow)
                        .setParameter(5, paymentMethod)
                        .setParameter(6, "MONTHLY")
                        .setParameter(7, monthFor)
                        .setParameter(8, note)
                        .executeUpdate();

                int installmentId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).intValue();
                // ✅ LEDGER
                LedgerHelper.saveLedger(
                        em,
                        payNow,
                        "CREDIT",
                        "Fee payment for " + monthFor,
                        "STUDENT_FEES",
                        installmentId,
                        paymentMethod,
                        "MONTHLY_FEE",
                        username
                );

                // ✅ LOG
                LogHelper.saveLog(
                        em,
                        "STUDENT_FEES",
                        installmentId,
                        "INSERT",
                        payNow,
                        paymentMethod,
                        "Partial payment for " + monthFor,
                        username
                );

                remainingAmount -= payNow;
                filledMonths.add(monthFor);
            }

            // =====================================================
            // DISTRIBUTE TO TABLE MONTHS
            // =====================================================
            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {

                int year = Integer.parseInt(model.getValueAt(i, 1).toString());
                String monthName = model.getValueAt(i, 2).toString();
                String ym = String.format("%04d-%02d", year, GeneralMethods.getMonthNumber(monthName));

                int paid = GeneralMethods.parseCommaNumber(
                        model.getValueAt(i, 3).toString().isEmpty() ? "0" : model.getValueAt(i, 3).toString()
                );

                if (skipMonths.contains(ym)) {
                    continue;
                }
                if (filledMonths.contains(ym)) {
                    continue;
                }
                if (paid >= monthlyFee) {
                    continue;
                }

                int payNow = Math.min(monthlyFee - paid, remainingAmount);

                installmentNo++;

                em.createNativeQuery(
                        "INSERT INTO student_fee_installments VALUES (NULL,?,?,?,?,NOW(),?,?,?, ?,1)"
                )
                        .setParameter(1, paymentId)
                        .setParameter(2, enrollmentId)
                        .setParameter(3, installmentNo)
                        .setParameter(4, payNow)
                        .setParameter(5, paymentMethod)
                        .setParameter(6, "MONTHLY")
                        .setParameter(7, ym)
                        .setParameter(8, note)
                        .executeUpdate();

                int installmentId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).intValue();

                // ✅ LEDGER
                LedgerHelper.saveLedger(
                        em,
                        payNow,
                        "CREDIT",
                        "Fee payment for " + ym,
                        "STUDENT_FEES",
                        installmentId,
                        paymentMethod,
                        "MONTHLY_FEE",
                        username
                );

                // ✅ LOG
                LogHelper.saveLog(
                        em,
                        "STUDENT_FEES",
                        installmentId,
                        "INSERT",
                        payNow,
                        paymentMethod,
                        "Payment applied to " + ym,
                        username
                );

                remainingAmount -= payNow;
                filledMonths.add(ym);
            }

            // =====================================================
            // UPDATE MASTER
            // =====================================================
            int totalPaid = ((Number) em.createNativeQuery(
                    "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id=?"
            ).setParameter(1, enrollmentId).getSingleResult()).intValue();

            int totalFee = rowCount * monthlyFee;

            em.createNativeQuery(
                    "UPDATE student_fee_payments SET total_paid=?, total_balance=?, payment_status=? WHERE enrollment_id=?"
            )
                    .setParameter(1, totalPaid)
                    .setParameter(2, totalFee - totalPaid)
                    .setParameter(3, totalPaid == totalFee ? "COMPLETED" : "ACTIVE")
                    .setParameter(4, enrollmentId)
                    .executeUpdate();

            em.getTransaction().commit();

            JOptionPane.showMessageDialog(null, "Monthly Payment Saved Successfully!");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

//    public void saveMonthlyFullPayment(
//            int enrollmentId,
//            JTable table,
//            JTextField monthlyFeeField,
//            String paymentMethod
//    ) {
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            if (table.isEditing()) {
//                table.getCellEditor().stopCellEditing();
//            }
//
//            DefaultTableModel model = (DefaultTableModel) table.getModel();
//            int rowCount = model.getRowCount();
//
//            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
//            int payingFee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_paid_Textfield.getText().trim());
//            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();
//
//            // =====================================================
//            // 🔥 VALIDATION
//            // =====================================================
//            if (payingFee < 0) {
//                JOptionPane.showMessageDialog(null, "Invalid payment amount!");
//                return;
//            }
//
//            if (payingFee == 0 && (note == null || note.trim().isEmpty())) {
//                JOptionPane.showMessageDialog(null, "Zero payment requires remarks!");
//                return;
//            }
//
//            em.getTransaction().begin();
//
//            // 🔹 Get payment id
//            int paymentId = ((Number) em.createNativeQuery(
//                    "SELECT student_fee_payments_id FROM student_fee_payments "
//                    + "WHERE enrollment_id = ? AND status = 1"
//            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
//
//            // 🔹 Get last installment number
//            Object lastNoObj = em.createNativeQuery(
//                    "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
//            ).setParameter(1, enrollmentId).getSingleResult();
//            int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;
//
//            int remainingAmount = payingFee;
//
//            // =====================================================
//            // 🔥 ZERO PAYMENT LOGIC
//            // =====================================================
//            if (payingFee == 0) {
//                // 1️⃣ Get last paid month with amount > 0 OR remarks
//                List<Object[]> lastPaidRows = em.createNativeQuery(
//                        "SELECT month_for, COALESCE(SUM(amount_paid),0) as paid_sum, "
//                        + "MAX(remarks) "
//                        + "FROM student_fee_installments "
//                        + "WHERE enrollment_id=? AND status=1 "
//                        + "GROUP BY month_for "
//                        + "ORDER BY month_for DESC"
//                ).setParameter(1, enrollmentId).getResultList();
//
//                String targetMonth = null;
//
//                if (!lastPaidRows.isEmpty()) {
//                    Object[] lastRow = lastPaidRows.get(0);
//                    String lastMonthDB = (String) lastRow[0]; // e.g., "2026-11"
//                    int lastPaid = ((Number) lastRow[1]).intValue();
//                    String lastRemarks = lastRow[2] != null ? lastRow[2].toString() : "";
//
//                    // Check if last month partially paid without remarks
//                    if (lastPaid < monthlyFee && lastRemarks.isEmpty()) {
//                        int confirm = JOptionPane.showConfirmDialog(
//                                null,
//                                "Your last month (" + lastMonthDB + ") has remaining balance: "
//                                + lastPaid + "/" + monthlyFee + ".\nDo you want to continue with 0 for next month?",
//                                "Confirm",
//                                JOptionPane.YES_NO_OPTION
//                        );
//                        if (confirm == JOptionPane.NO_OPTION) {
//                            em.getTransaction().rollback();
//                            return;
//                        }
//                    }
//
//                    // Always pay 0 for next month
//                    String[] parts = lastMonthDB.split("-"); // [year, month]
//                    int year = Integer.parseInt(parts[0]);
//                    int month = Integer.parseInt(parts[1]);
//
//                    month++;
//                    if (month > 12) {
//                        month = 1;
//                        year++;
//                    }
//
//                    targetMonth = String.format("%04d-%02d", year, month);
//
//                } else if (rowCount > 0) {
//                    // No prior payments, pick first month from table (numeric)
//                    targetMonth = model.getValueAt(0, 1).toString() + "-" + GeneralMethods.getMonthNumber(model.getValueAt(0, 2).toString());
//                } else {
//                    // fallback: current month
//                    java.time.LocalDate now = java.time.LocalDate.now();
//                    targetMonth = String.format("%04d-%02d", now.getYear(), now.getMonthValue());
//                }
//
//                installmentNo++;
//                Query insert = em.createNativeQuery(
//                        "INSERT INTO student_fee_installments ("
//                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
//                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
//                        + ") VALUES (?, ?, ?, 0, NOW(), ?, ?, ?, ?, 1)"
//                );
//                insert.setParameter(1, paymentId);
//                insert.setParameter(2, enrollmentId);
//                insert.setParameter(3, installmentNo);
//                insert.setParameter(4, paymentMethod);
//                insert.setParameter(5, "MONTHLY");
//                insert.setParameter(6, targetMonth);
//                insert.setParameter(7, note);
//                insert.executeUpdate();
//                em.getTransaction().commit();
//
//                JOptionPane.showMessageDialog(null, "Zero payment saved for month " + targetMonth + "!");
//                return;
//            }
//
//            // =====================================================
//            // 🔥 LESS THAN MONTHLY VALIDATION
//            // =====================================================
//            if (payingFee < monthlyFee && (note == null || note.trim().isEmpty())) {
//                JOptionPane.showMessageDialog(null, "Remarks required for payment less than monthly fee!");
//                return;
//            }
//
//            // =====================================================
//            // 🔹 Track months already filled
//            // =====================================================
//            Set<String> filledMonths = new HashSet<>();
//
//            // =====================================================
    //// 🔹 GET PARTIAL MONTHS + REMARK CHECK (STRICT)
//// =====================================================
//            List<Object[]> partialMonths = em.createNativeQuery(
//                    "SELECT s.month_for, "
//                    + "COALESCE(SUM(s.amount_paid),0) as total_paid, "
//                    + "SUM(CASE WHEN TRIM(COALESCE(s.remarks,'')) <> '' THEN 1 ELSE 0 END) as remark_count "
//                    + "FROM student_fee_installments s "
//                    + "WHERE s.enrollment_id=? AND s.status=1 "
//                    + "GROUP BY s.month_for "
//                    + "HAVING total_paid < ? "
//                    + "ORDER BY s.month_for ASC"
//            )
//                    .setParameter(1, enrollmentId)
//                    .setParameter(2, monthlyFee)
//                    .getResultList();
//
//// =====================================================
//// 🔹 FILL ONLY VALID MONTHS
//// =====================================================
//            for (Object[] row : partialMonths) {
//
//                if (remainingAmount <= 0) {
//                    break;
//                }
//
//                String monthFor = row[0].toString();
//                int paidSoFar = ((Number) row[1]).intValue();
//                int remarkCount = ((Number) row[2]).intValue();
//
//                // ❌ CRITICAL RULE
//                // If remarks exist → NEVER fill this month
//                if (remarkCount > 0) {
//                    continue;
//                }
//
//                // ❌ avoid duplicate fill
//                if (filledMonths.contains(monthFor)) {
//                    continue;
//                }
//
//                int balance = monthlyFee - paidSoFar;
//
//                // if already fully paid skip
//                if (balance <= 0) {
//                    continue;
//                }
//
//                int payNow = Math.min(balance, remainingAmount);
//
//                installmentNo++;
//
//                Query insert = em.createNativeQuery(
//                        "INSERT INTO student_fee_installments ("
//                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
//                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
//                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
//                );
//
//                insert.setParameter(1, paymentId);
//                insert.setParameter(2, enrollmentId);
//                insert.setParameter(3, installmentNo);
//                insert.setParameter(4, payNow);
//                insert.setParameter(5, paymentMethod);
//                insert.setParameter(6, "MONTHLY");
//                insert.setParameter(7, monthFor);
//                insert.setParameter(8, note);
//
//                insert.executeUpdate();
//
//                remainingAmount -= payNow;
//                filledMonths.add(monthFor);
//            }
////            List<Object[]> partialMonths = em.createNativeQuery(
////                    "SELECT month_for, COALESCE(SUM(amount_paid),0) as total_paid, "
////                    + "SUM(CASE WHEN remarks IS NOT NULL AND remarks<>'' THEN 1 ELSE 0 END) as remark_count "
////                    + "FROM student_fee_installments "
////                    + "WHERE enrollment_id=? AND status=1 "
////                    + "GROUP BY month_for "
////                    + "HAVING total_paid < ? "
////                    + "ORDER BY month_for ASC"
////            )
////                    .setParameter(1, enrollmentId)
////                    .setParameter(2, monthlyFee)
////                    .getResultList();
//
//            for (Object[] row : partialMonths) {
//
//                if (remainingAmount <= 0) {
//                    break;
//                }
//
//                String monthFor = row[0].toString();
//                int paidSoFar = ((Number) row[1]).intValue();
//                int remarkCount = ((Number) row[2]).intValue();
//
//                // ❌ IF remarks exist → SKIP this month
//                if (remarkCount > 0) {
//                    continue;
//                }
//
//                // ❌ avoid double fill
//                if (filledMonths.contains(monthFor)) {
//                    continue;
//                }
//
//                int balance = monthlyFee - paidSoFar;
//                int payNow = Math.min(balance, remainingAmount);
//
//                installmentNo++;
//
//                Query insert = em.createNativeQuery(
//                        "INSERT INTO student_fee_installments ("
//                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
//                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
//                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
//                );
//
//                insert.setParameter(1, paymentId);
//                insert.setParameter(2, enrollmentId);
//                insert.setParameter(3, installmentNo);
//                insert.setParameter(4, payNow);
//                insert.setParameter(5, paymentMethod);
//                insert.setParameter(6, "MONTHLY");
//                insert.setParameter(7, monthFor);
//                insert.setParameter(8, note);
//
//                insert.executeUpdate();
//
//                remainingAmount -= payNow;
//                filledMonths.add(monthFor);
//            }
////            List<Object[]> partialMonths = em.createNativeQuery(
////                    "SELECT month_for, COALESCE(SUM(amount_paid),0) as total_paid "
////                    + "FROM student_fee_installments "
////                    + "WHERE enrollment_id=? AND status=1 "
////                    + "GROUP BY month_for "
////                    + "HAVING total_paid < ? AND SUM(CASE WHEN remarks IS NOT NULL AND remarks<>'' THEN 1 ELSE 0 END)=0 "
////                    + "ORDER BY month_for ASC"
////            )
////                    .setParameter(1, enrollmentId)
////                    .setParameter(2, monthlyFee)
////                    .getResultList();
////
////            for (Object[] row : partialMonths) {
////                if (remainingAmount <= 0) {
////                    break;
////                }
////
////                String monthFor = row[0].toString();
////                if (filledMonths.contains(monthFor)) {
////                    continue;
////                }
////
////                int paidSoFar = ((Number) row[1]).intValue();
////                int balance = monthlyFee - paidSoFar;
////                int payNow = Math.min(balance, remainingAmount);
////
////                installmentNo++;
////                Query insert = em.createNativeQuery(
////                        "INSERT INTO student_fee_installments ("
////                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
////                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
////                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
////                );
////                insert.setParameter(1, paymentId);
////                insert.setParameter(2, enrollmentId);
////                insert.setParameter(3, installmentNo);
////                insert.setParameter(4, payNow);
////                insert.setParameter(5, paymentMethod);
////                insert.setParameter(6, "MONTHLY");
////                insert.setParameter(7, monthFor);
////                insert.setParameter(8, note);
////                insert.executeUpdate();
////
////                remainingAmount -= payNow;
////                filledMonths.add(monthFor);
////            }
//
//            // =====================================================
//            // 🔹 DISTRIBUTE REMAINING PAYMENT TO OTHER MONTHS
//            // =====================================================
//            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
//                int year = Integer.parseInt(model.getValueAt(i, 1).toString());
//                String monthName = model.getValueAt(i, 2).toString();
//                String yearMonth = String.format("%04d-%02d", year, GeneralMethods.getMonthNumber(monthName));
//
//                int alreadyPaid = GeneralMethods.parseCommaNumber(
//                        model.getValueAt(i, 3).toString().trim().isEmpty() ? "0" : model.getValueAt(i, 3).toString()
//                );
//                String remarksVal = model.getValueAt(i, 4) != null ? model.getValueAt(i, 4).toString().trim() : "";
//
//                // Skip month if fully paid, 0-paid-with-remarks, or already filled
//                if (alreadyPaid >= monthlyFee || (alreadyPaid == 0 && !remarksVal.isEmpty()) || filledMonths.contains(yearMonth)) {
//                    continue;
//                }
//
//                int payNow = Math.min(monthlyFee - alreadyPaid, remainingAmount);
//
//                installmentNo++;
//                Query insert = em.createNativeQuery(
//                        "INSERT INTO student_fee_installments ("
//                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
//                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
//                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
//                );
//                insert.setParameter(1, paymentId);
//                insert.setParameter(2, enrollmentId);
//                insert.setParameter(3, installmentNo);
//                insert.setParameter(4, payNow);
//                insert.setParameter(5, paymentMethod);
//                insert.setParameter(6, "MONTHLY");
//                insert.setParameter(7, yearMonth);
//                insert.setParameter(8, note);
//                insert.executeUpdate();
//
//                remainingAmount -= payNow;
//                filledMonths.add(yearMonth);
//            }
//
//            // =====================================================
//            // 🔹 UPDATE MASTER TABLE
//            // =====================================================
//            int totalPaid = ((Number) em.createNativeQuery(
//                    "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id=?"
//            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
//
//            int totalFee = rowCount * monthlyFee;
//            int balance = totalFee - totalPaid;
//
//            Query update = em.createNativeQuery(
//                    "UPDATE student_fee_payments SET total_paid=?, total_balance=?, payment_status=? WHERE enrollment_id=?"
//            );
//            update.setParameter(1, totalPaid);
//            update.setParameter(2, balance);
//            update.setParameter(3, balance == 0 ? "COMPLETED" : "ACTIVE");
//            update.setParameter(4, enrollmentId);
//            update.executeUpdate();
//
//            em.getTransaction().commit();
//            JOptionPane.showMessageDialog(null, "Monthly Payment Saved Successfully!");
//
//        } catch (Exception e) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }

//    public void saveMonthlyFullPayment(
//            int enrollmentId,
//            JTable table,
//            JTextField monthlyFeeField,
//            String paymentMethod
//    ) {
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            if (table.isEditing()) {
//                table.getCellEditor().stopCellEditing();
//            }
//
//            DefaultTableModel model = (DefaultTableModel) table.getModel();
//            int rowCount = model.getRowCount();
//
//            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
//            int payingFee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_paid_Textfield.getText().trim());
//            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();
//
//            // =====================================================
//            // 🔥 VALIDATION
//            // =====================================================
//            if (payingFee < 0) {
//                JOptionPane.showMessageDialog(null, "Invalid payment amount!");
//                return;
//            }
//
//            if (payingFee == 0 && (note == null || note.trim().isEmpty())) {
//                JOptionPane.showMessageDialog(null, "Zero payment requires remarks!");
//                return;
//            }
//
//            // =====================================================
//            // 🔥 FIND LAST MONTH FROM DB
//            // =====================================================
//            String lastMonth = (String) em.createNativeQuery(
//                    "SELECT MAX(month_for) FROM student_fee_installments "
//                    + "WHERE enrollment_id=? AND status=1 "
//                    + "AND (amount_paid > 0 OR (remarks IS NOT NULL AND remarks<>''))"
//            ).setParameter(1, enrollmentId).getSingleResult();
//
//            int lastMonthPaid = 0;
//            if (lastMonth != null) {
//                lastMonthPaid = ((Number) em.createNativeQuery(
//                        "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments "
//                        + "WHERE enrollment_id=? AND month_for=?"
//                ).setParameter(1, enrollmentId)
//                        .setParameter(2, lastMonth)
//                        .getSingleResult()).intValue();
//            }
//
//            boolean lastMonthNotFull = (lastMonthPaid < monthlyFee);
//
//            int remainingAmount = payingFee;
//
//            em.getTransaction().begin();
//
//            // 🔹 get payment id
//            int paymentId = ((Number) em.createNativeQuery(
//                    "SELECT student_fee_payments_id FROM student_fee_payments "
//                    + "WHERE enrollment_id = ? AND status = 1"
//            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
//
//            Object lastNoObj = em.createNativeQuery(
//                    "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
//            ).setParameter(1, enrollmentId).getSingleResult();
//
//            int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;
//
//            // =====================================================
    //// 🔥 ZERO PAYMENT LOGIC FIXED
//// =====================================================
//            if (payingFee == 0) {
//
//                // 1️⃣ Get last paid month with amount > 0 OR remarks
//                List<Object[]> lastPaidRows = em.createNativeQuery(
//                        "SELECT month_for, COALESCE(SUM(amount_paid),0) as paid_sum, "
//                        + "MAX(remarks) "
//                        + "FROM student_fee_installments "
//                        + "WHERE enrollment_id=? AND status=1 "
//                        + "GROUP BY month_for "
//                        + "ORDER BY month_for DESC"
//                ).setParameter(1, enrollmentId).getResultList();
//
//                String targetMonth = null;
//
//                if (!lastPaidRows.isEmpty()) {
//                    Object[] lastRow = lastPaidRows.get(0);
//                    String lastMonthDB = (String) lastRow[0]; // e.g., "2026-11"
//                    int lastPaid = ((Number) lastRow[1]).intValue();
//                    String lastRemarks = lastRow[2] != null ? lastRow[2].toString() : "";
//
//                    // check if last month partially paid
//                    if (lastPaid < monthlyFee && lastRemarks.isEmpty()) {
//                        int confirm = JOptionPane.showConfirmDialog(
//                                null,
//                                "Your last month (" + lastMonthDB + ") has remaining balance: "
//                                + lastPaid + "/" + monthlyFee + ".\nDo you want to continue with 0 for next month?",
//                                "Confirm",
//                                JOptionPane.YES_NO_OPTION
//                        );
//                        if (confirm == JOptionPane.NO_OPTION) {
//                            em.getTransaction().rollback();
//                            return;
//                        }
//                    }
//
//                    // always pay 0 for next month
//                    String[] parts = lastMonthDB.split("-"); // [year, month]
//                    int year = Integer.parseInt(parts[0]);
//                    int month = Integer.parseInt(parts[1]);
//
//                    month++; // next month
//                    if (month > 12) {
//                        month = 1;
//                        year++;
//                    }
//
//                    targetMonth = String.format("%04d-%02d", year, month);
//
//                } else if (rowCount > 0) {
//                    // No prior payments, pick first month from table (numeric)
//                    targetMonth = model.getValueAt(0, 1).toString() + "-" + model.getValueAt(0, 2).toString();
//                } else {
//                    // fallback: current month
//                    java.time.LocalDate now = java.time.LocalDate.now();
//                    targetMonth = String.format("%04d-%02d", now.getYear(), now.getMonthValue());
//                }
//
//                installmentNo++;
//
//                Query insert = em.createNativeQuery(
//                        "INSERT INTO student_fee_installments ("
//                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
//                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
//                        + ") VALUES (?, ?, ?, 0, NOW(), ?, ?, ?, ?, 1)"
//                );
//
//                insert.setParameter(1, paymentId);
//                insert.setParameter(2, enrollmentId);
//                insert.setParameter(3, installmentNo);
//                insert.setParameter(4, paymentMethod);
//                insert.setParameter(5, "MONTHLY");
//                insert.setParameter(6, targetMonth);
//                insert.setParameter(7, note);
//
//                insert.executeUpdate();
//                em.getTransaction().commit();
//
//                JOptionPane.showMessageDialog(null, "Zero payment saved for month " + targetMonth + "!");
//                return;
//            }
//
//            // =====================================================
//            // 🔥 LESS THAN MONTHLY VALIDATION
//            // =====================================================
//            if (payingFee < monthlyFee) {
//                if (lastMonthNotFull) {
//                    int confirm = JOptionPane.showConfirmDialog(
//                            null,
//                            "Last month has balance. Complete it?",
//                            "Confirm",
//                            JOptionPane.YES_NO_OPTION
//                    );
//                    if (confirm == JOptionPane.NO_OPTION && (note == null || note.trim().isEmpty())) {
//                        JOptionPane.showMessageDialog(null, "Please enter remarks!");
//                        return;
//                    }
//                } else if (note == null || note.trim().isEmpty()) {
//                    JOptionPane.showMessageDialog(null, "Remarks required!");
//                    return;
//                }
//            }
//
//            // =====================================================
//            // 🔹 FILL LAST MONTH IF PARTIALLY PAID
//            // =====================================================
//            if (lastMonth != null && remainingAmount > 0 && lastMonthPaid < monthlyFee) {
//
//                int remarksCount = ((Number) em.createNativeQuery(
//                        "SELECT COUNT(*) FROM student_fee_installments "
//                        + "WHERE enrollment_id=? AND month_for=? "
//                        + "AND remarks IS NOT NULL AND remarks<>''"
//                ).setParameter(1, enrollmentId)
//                        .setParameter(2, lastMonth)
//                        .getSingleResult()).intValue();
//
//                if (remarksCount == 0) {
//                    int balance = monthlyFee - lastMonthPaid;
//                    int payNow = Math.min(balance, remainingAmount);
//
//                    installmentNo++;
//
//                    Query insert = em.createNativeQuery(
//                            "INSERT INTO student_fee_installments ("
//                            + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
//                            + "payment_date, payment_method, payment_type, month_for, remarks, status"
//                            + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
//                    );
//
//                    insert.setParameter(1, paymentId);
//                    insert.setParameter(2, enrollmentId);
//                    insert.setParameter(3, installmentNo);
//                    insert.setParameter(4, payNow);
//                    insert.setParameter(5, paymentMethod);
//                    insert.setParameter(6, "MONTHLY");
//                    insert.setParameter(7, lastMonth);
//                    insert.setParameter(8, "");
//
//                    insert.executeUpdate();
//                    remainingAmount -= payNow;
//                }
//            }
//
//            // =====================================================
//            // 🔹 DISTRIBUTE REMAINING PAYMENT
//            // =====================================================
//            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
//
//                String yearMonth = model.getValueAt(i, 1).toString() + "-" + model.getValueAt(i, 2).toString();
//                int alreadyPaid = GeneralMethods.parseCommaNumber(model.getValueAt(i, 3).toString());
//
//                if (alreadyPaid >= monthlyFee) {
//                    continue;
//                }
//
//                int payNow = Math.min(monthlyFee - alreadyPaid, remainingAmount);
//
//                installmentNo++;
//
//                Query insert = em.createNativeQuery(
//                        "INSERT INTO student_fee_installments ("
//                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
//                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
//                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
//                );
//
//                insert.setParameter(1, paymentId);
//                insert.setParameter(2, enrollmentId);
//                insert.setParameter(3, installmentNo);
//                insert.setParameter(4, payNow);
//                insert.setParameter(5, paymentMethod);
//                insert.setParameter(6, "MONTHLY");
//                insert.setParameter(7, yearMonth);
//                insert.setParameter(8, note);
//
//                insert.executeUpdate();
//                remainingAmount -= payNow;
//            }
//
//            // =====================================================
//            // 🔹 UPDATE MASTER TABLE
//            // =====================================================
//            int totalPaid = ((Number) em.createNativeQuery(
//                    "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id=?"
//            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
//
//            int totalFee = rowCount * monthlyFee;
//            int balance = totalFee - totalPaid;
//
//            Query update = em.createNativeQuery(
//                    "UPDATE student_fee_payments SET total_paid=?, total_balance=?, payment_status=? WHERE enrollment_id=?"
//            );
//
//            update.setParameter(1, totalPaid);
//            update.setParameter(2, balance);
//            update.setParameter(3, balance == 0 ? "COMPLETED" : "ACTIVE");
//            update.setParameter(4, enrollmentId);
//
//            update.executeUpdate();
//            em.getTransaction().commit();
//
//            JOptionPane.showMessageDialog(null, "Monthly Payment Saved Successfully!");
//
//        } catch (Exception e) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }

    /**
     * Returns the next month string in format YYYY-MMM (e.g., 2026-FEB →
     * 2026-MAR)
     */
    public String getNextMonthString(String currentMonthStr) {
        // currentMonthStr format: "YYYY-MMM", e.g., "2026-FEB"
        try {
            String[] parts = currentMonthStr.split("-");
            if (parts.length != 2) {
                return null;
            }

            int year = Integer.parseInt(parts[0].trim());
            String monthPart = parts[1].trim().toUpperCase();

            Month monthEnum = Month.valueOf(monthPart); // e.g., "FEB" -> Month.FEBRUARY

            Month nextMonthEnum = monthEnum.plus(1); // increment month
            int nextYear = year;
            if (monthEnum == Month.DECEMBER) {
                nextYear += 1; // handle year change
            }

            // Format back to "YYYY-MMM"
            String nextMonthStr = nextYear + "-" + nextMonthEnum.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
            return nextMonthStr;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    //    public void saveMonthlyFullPayment(
    //            int enrollmentId,
    //            JTable table,
    //            JTextField monthlyFeeField,
    //            String paymentMethod
    //    ) {
    //        EntityManager em = HibernateConfig.getEntityManager();
    //
    //        try {
    //            if (table.isEditing()) {
    //                table.getCellEditor().stopCellEditing();
    //            }
    //
    //            DefaultTableModel model = (DefaultTableModel) table.getModel();
    //            int rowCount = model.getRowCount();
    //
    //            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
    //            int payingFee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_paid_Textfield.getText().trim());
    //            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();
    //
    //            // =====================================================
    //            // 🔥 VALIDATION
    //            // =====================================================
    //            // ❌ NEGATIVE BLOCK
    //            if (payingFee < 0) {
    //                JOptionPane.showMessageDialog(null, "Invalid payment amount!");
    //                return;
    //            }
    //
    //            // ============================
    //            // FIND LAST MONTH FROM DB
    //            // ============================
    //            String lastMonth = (String) em.createNativeQuery(
    //                    "SELECT MAX(month_for) FROM student_fee_installments WHERE enrollment_id=? AND status=1"
    //            ).setParameter(1, enrollmentId).getSingleResult();
    //
    //            int lastMonthPaid = 0;
    //
    //            if (lastMonth != null) {
    //                lastMonthPaid = ((Number) em.createNativeQuery(
    //                        "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id=? AND month_for=?"
    //                ).setParameter(1, enrollmentId)
    //                        .setParameter(2, lastMonth)
    //                        .getSingleResult()).intValue();
    //            }
    //
    //            boolean lastMonthNotFull = (lastMonthPaid > 0 && lastMonthPaid < monthlyFee);
    //
    //            // ============================
    //            // ZERO PAYMENT
    //            // ============================
    //            if (payingFee == 0) {
    //
    //                if (note == null || note.trim().isEmpty()) {
    //                    JOptionPane.showMessageDialog(null,
    //                            "Zero payment requires remarks (e.g., Holiday / Pandemic)");
    //                    return;
    //                }
    //
    //                em.getTransaction().begin();
    //
    //                int paymentId = ((Number) em.createNativeQuery(
    //                        "SELECT student_fee_payments_id FROM student_fee_payments WHERE enrollment_id = ? AND status = 1"
    //                ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //                Object lastNoObj = em.createNativeQuery(
    //                        "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
    //                ).setParameter(1, enrollmentId).getSingleResult();
    //
    //                int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;
    //
    //                // save zero entry to FIRST unpaid month
    //                for (int i = 0; i < rowCount; i++) {
    //
    //                    int alreadyPaid = GeneralMethods.parseCommaNumber(model.getValueAt(i, 3).toString());
    //
    //                    if (alreadyPaid == 0) {
    //
    //                        installmentNo++;
    //
    //                        String monthFor = model.getValueAt(i, 5).toString();
    //
    //                        Query insert = em.createNativeQuery(
    //                                "INSERT INTO student_fee_installments ("
    //                                + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                                + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                                + ") VALUES (?, ?, ?, 0, NOW(), ?, ?, ?, ?, 1)"
    //                        );
    //
    //                        insert.setParameter(1, paymentId);
    //                        insert.setParameter(2, enrollmentId);
    //                        insert.setParameter(3, installmentNo);
    //                        insert.setParameter(4, paymentMethod);
    //                        insert.setParameter(5, "MONTHLY");
    //                        insert.setParameter(6, monthFor);
    //                        insert.setParameter(7, note);
    //
    //                        insert.executeUpdate();
    //                        break;
    //                    }
    //                }
    //
    //                em.getTransaction().commit();
    //
    //                JOptionPane.showMessageDialog(null, "Zero payment saved!");
    //                return;
    //            }
    //
    //            // ============================
    //            // LESS THAN MONTHLY
    //            // ============================
    //            if (payingFee < monthlyFee) {
    //
    //                if (lastMonthNotFull) {
    //
    //                    int confirm = JOptionPane.showConfirmDialog(
    //                            null,
    //                            "Last month has remaining balance.\nDo you want to complete it?",
    //                            "Confirm",
    //                            JOptionPane.YES_NO_OPTION
    //                    );
    //
    //                    if (confirm == JOptionPane.NO_OPTION) {
    //                        if (note == null || note.trim().isEmpty()) {
    //                            JOptionPane.showMessageDialog(null,
    //                                    "Please enter remarks if not completing previous month.");
    //                            return;
    //                        }
    //                    }
    //
    //                } else {
    //                    if (note == null || note.trim().isEmpty()) {
    //                        JOptionPane.showMessageDialog(null,
    //                                "Remarks required for partial payment.");
    //                        return;
    //                    }
    //                }
    //            }
    //
    //            int remainingAmount = payingFee;
    //
    //            em.getTransaction().begin();
    //
    //            int paymentId = ((Number) em.createNativeQuery(
    //                    "SELECT student_fee_payments_id FROM student_fee_payments WHERE enrollment_id = ? AND status = 1"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            Object lastNoObj = em.createNativeQuery(
    //                    "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
    //            ).setParameter(1, enrollmentId).getSingleResult();
    //
    //            int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;
    //
    //            // =====================================================
    //            // 🔥 STEP 1: FILL LAST MONTH IF NEEDED
    //            // =====================================================
    //            if (lastMonth != null && remainingAmount > 0 && lastMonthPaid < monthlyFee) {
    //
    //                int remarksCount = ((Number) em.createNativeQuery(
    //                        "SELECT COUNT(*) FROM student_fee_installments "
    //                        + "WHERE enrollment_id=? AND month_for=? AND remarks IS NOT NULL AND remarks<>''"
    //                ).setParameter(1, enrollmentId)
    //                        .setParameter(2, lastMonth)
    //                        .getSingleResult()).intValue();
    //
    //                if (remarksCount == 0) {
    //
    //                    int balance = monthlyFee - lastMonthPaid;
    //                    int payNow = Math.min(balance, remainingAmount);
    //
    //                    installmentNo++;
    //
    //                    Query insert = em.createNativeQuery(
    //                            "INSERT INTO student_fee_installments ("
    //                            + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                            + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                            + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
    //                    );
    //
    //                    insert.setParameter(1, paymentId);
    //                    insert.setParameter(2, enrollmentId);
    //                    insert.setParameter(3, installmentNo);
    //                    insert.setParameter(4, payNow);
    //                    insert.setParameter(5, paymentMethod);
    //                    insert.setParameter(6, "MONTHLY");
    //                    insert.setParameter(7, lastMonth);
    //                    insert.setParameter(8, "BALANCE FILL");
    //
    //                    insert.executeUpdate();
    //                    em.flush();
    //
    //                    remainingAmount -= payNow;
    //
    //                    // JTable update
    //                    for (int i = 0; i < rowCount; i++) {
    //                        if (model.getValueAt(i, 5).toString().equals(lastMonth)) {
    //                            int old = GeneralMethods.parseCommaNumber(model.getValueAt(i, 3).toString());
    //                            int newVal = old + payNow;
    //                            model.setValueAt(newVal, i, 3);
    //                            model.setValueAt(newVal >= monthlyFee ? "Paid" : "Now Paid", i, 4);
    //                            break;
    //                        }
    //                    }
    //                }
    //            }
    //
    //            // =====================================================
    //            // 🔥 STEP 2: NORMAL DISTRIBUTION
    //            // =====================================================
    //            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
    //
    //                String monthFor = model.getValueAt(i, 5).toString();
    //                int alreadyPaid = GeneralMethods.parseCommaNumber(model.getValueAt(i, 3).toString());
    //
    //                if (alreadyPaid >= monthlyFee) {
    //                    continue;
    //                }
    //
    //                int toFill = monthlyFee - alreadyPaid;
    //                int payNow = Math.min(toFill, remainingAmount);
    //
    //                installmentNo++;
    //
    //                Query insert = em.createNativeQuery(
    //                        "INSERT INTO student_fee_installments ("
    //                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
    //                );
    //
    //                insert.setParameter(1, paymentId);
    //                insert.setParameter(2, enrollmentId);
    //                insert.setParameter(3, installmentNo);
    //                insert.setParameter(4, payNow);
    //                insert.setParameter(5, paymentMethod);
    //                insert.setParameter(6, "MONTHLY");
    //                insert.setParameter(7, monthFor);
    //                insert.setParameter(8, note.isEmpty() ? "MONTHLY FEE" : note);
    //
    //                insert.executeUpdate();
    //                em.flush();
    //
    //                int newPaid = alreadyPaid + payNow;
    //                model.setValueAt(newPaid, i, 3);
    //                model.setValueAt(newPaid >= monthlyFee ? "Paid" : "Now Paid", i, 4);
    //
    //                remainingAmount -= payNow;
    //            }
    //
    //            // =====================================================
    //            // UPDATE MASTER
    //            // =====================================================
    //            int totalPaid = ((Number) em.createNativeQuery(
    //                    "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id=?"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            int totalFee = rowCount * monthlyFee;
    //            int balance = totalFee - totalPaid;
    //
    //            Query update = em.createNativeQuery(
    //                    "UPDATE student_fee_payments SET total_paid=?, total_balance=?, payment_status=? WHERE enrollment_id=?"
    //            );
    //
    //            update.setParameter(1, totalPaid);
    //            update.setParameter(2, balance);
    //            update.setParameter(3, balance == 0 ? "COMPLETED" : "ACTIVE");
    //            update.setParameter(4, enrollmentId);
    //
    //            update.executeUpdate();
    //
    //            em.getTransaction().commit();
    //
    //            JOptionPane.showMessageDialog(null, "Monthly Payment Saved Successfully!");
    //
    //        } catch (Exception e) {
    //            if (em.getTransaction().isActive()) {
    //                em.getTransaction().rollback();
    //            }
    //            e.printStackTrace();
    //        } finally {
    //            em.close();
    //        }
    //    }
    // ********************************* FULL WORKING CODE **************************
    //    public void saveMonthlyFullPayment(
    //            int enrollmentId,
    //            JTable table,
    //            JTextField monthlyFeeField,
    //            String paymentMethod
    //    ) {
    //        EntityManager em = HibernateConfig.getEntityManager();
    //
    //        try {
    //            if (table.isEditing()) {
    //                table.getCellEditor().stopCellEditing();
    //            }
    //
    //            DefaultTableModel model = (DefaultTableModel) table.getModel();
    //            int rowCount = model.getRowCount();
    //
    //            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
    //            int payingFee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_paid_Textfield.getText().trim());
    //            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();
    //
    //            if (payingFee <= 0) {
    //                JOptionPane.showMessageDialog(null, "Enter valid payment amount!");
    //                return;
    //            }
    //
    //            int remainingAmount = payingFee;
    //
    //            em.getTransaction().begin();
    //
    //            int paymentId = ((Number) em.createNativeQuery(
    //                    "SELECT student_fee_payments_id FROM student_fee_payments WHERE enrollment_id = ? AND status = 1"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            Object lastNoObj = em.createNativeQuery(
    //                    "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
    //            ).setParameter(1, enrollmentId).getSingleResult();
    //
    //            int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;
    //
    //            // =====================================================
    //            // 🔥 STEP 1: FIND LAST PAID MONTH
    //            // =====================================================
    //            String lastMonth = (String) em.createNativeQuery(
    //                    "SELECT MAX(month_for) FROM student_fee_installments WHERE enrollment_id=? AND status=1"
    //            ).setParameter(1, enrollmentId).getSingleResult();
    //
    //            if (lastMonth != null && remainingAmount > 0) {
    //
    //                // total paid for last month
    //                int lastMonthPaid = ((Number) em.createNativeQuery(
    //                        "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id=? AND month_for=?"
    //                ).setParameter(1, enrollmentId)
    //                        .setParameter(2, lastMonth)
    //                        .getSingleResult()).intValue();
    //
    //                System.out.println("DEBUG LastMonth=" + lastMonth + " Paid=" + lastMonthPaid);
    //
    //                // 🔥 IF PARTIAL MONTH
    //                if (lastMonthPaid < monthlyFee) {
    //
    //                    // check remarks exists
    //                    int remarksCount = ((Number) em.createNativeQuery(
    //                            "SELECT COUNT(*) FROM student_fee_installments "
    //                            + "WHERE enrollment_id=? AND month_for=? AND remarks IS NOT NULL AND remarks<>''"
    //                    ).setParameter(1, enrollmentId)
    //                            .setParameter(2, lastMonth)
    //                            .getSingleResult()).intValue();
    //
    //                    System.out.println("DEBUG RemarksCount=" + remarksCount);
    //
    //                    // ✅ ONLY fill if NO remarks
    //                    if (remarksCount == 0) {
    //
    //                        int balance = monthlyFee - lastMonthPaid;
    //                        int payNow = Math.min(balance, remainingAmount);
    //
    //                        installmentNo++;
    //
    //                        Query insert = em.createNativeQuery(
    //                                "INSERT INTO student_fee_installments ("
    //                                + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                                + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                                + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
    //                        );
    //
    //                        insert.setParameter(1, paymentId);
    //                        insert.setParameter(2, enrollmentId);
    //                        insert.setParameter(3, installmentNo);
    //                        insert.setParameter(4, payNow);
    //                        insert.setParameter(5, paymentMethod);
    //                        insert.setParameter(6, "MONTHLY");
    //                        insert.setParameter(7, lastMonth);
    //                        insert.setParameter(8, "BALANCE FILL");
    //                        insert.executeUpdate();
    //                        em.flush();
    //
    //                        remainingAmount -= payNow;
    //
    //                        // 🔥 UPDATE JTable
    //                        for (int i = 0; i < rowCount; i++) {
    //                            String rowMonth = model.getValueAt(i, 5).toString();
    //                            if (rowMonth.equals(lastMonth)) {
    //                                int old = GeneralMethods.parseCommaNumber(model.getValueAt(i, 3).toString());
    //                                int newVal = old + payNow;
    //                                model.setValueAt(newVal, i, 3);
    //                                model.setValueAt(newVal >= monthlyFee ? "Paid" : "Now Paid", i, 4);
    //                                break;
    //                            }
    //                        }
    //
    //                        System.out.println("DEBUG Filled last month " + payNow + " Remaining=" + remainingAmount);
    //                    }
    //                }
    //            }
    //
    //            // =====================================================
    //            // 🔥 STEP 2: NORMAL DISTRIBUTION
    //            // =====================================================
    //            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
    //
    //                String monthFor = model.getValueAt(i, 5).toString();
    //                int alreadyPaid = GeneralMethods.parseCommaNumber(
    //                        model.getValueAt(i, 3).toString()
    //                );
    //
    //                if (alreadyPaid >= monthlyFee) {
    //                    continue;
    //                }
    //
    //                int toFill = monthlyFee - alreadyPaid;
    //                int payNow = Math.min(toFill, remainingAmount);
    //
    //                installmentNo++;
    //
    //                Query insert = em.createNativeQuery(
    //                        "INSERT INTO student_fee_installments ("
    //                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
    //                );
    //
    //                insert.setParameter(1, paymentId);
    //                insert.setParameter(2, enrollmentId);
    //                insert.setParameter(3, installmentNo);
    //                insert.setParameter(4, payNow);
    //                insert.setParameter(5, paymentMethod);
    //                insert.setParameter(6, "MONTHLY");
    //                insert.setParameter(7, monthFor);
    //                insert.setParameter(8, note.isEmpty() ? "MONTHLY FEE" : note);
    //                insert.executeUpdate();
    //                em.flush();
    //
    //                int newPaid = alreadyPaid + payNow;
    //                model.setValueAt(newPaid, i, 3);
    //                model.setValueAt(newPaid >= monthlyFee ? "Paid" : "Now Paid", i, 4);
    //
    //                remainingAmount -= payNow;
    //
    //                System.out.println("DEBUG Normal pay " + payNow + " Month=" + monthFor + " Remaining=" + remainingAmount);
    //            }
    //
    //            // =====================================================
    //            // UPDATE MASTER
    //            // =====================================================
    //            int totalPaid = ((Number) em.createNativeQuery(
    //                    "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id=?"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            int totalFee = rowCount * monthlyFee;
    //            int balance = totalFee - totalPaid;
    //
    //            Query update = em.createNativeQuery(
    //                    "UPDATE student_fee_payments SET total_paid=?, total_balance=?, payment_status=? WHERE enrollment_id=?"
    //            );
    //            update.setParameter(1, totalPaid);
    //            update.setParameter(2, balance);
    //            update.setParameter(3, balance == 0 ? "COMPLETED" : "ACTIVE");
    //            update.setParameter(4, enrollmentId);
    //            update.executeUpdate();
    //
    //            em.getTransaction().commit();
    //
    //            JOptionPane.showMessageDialog(null, "Monthly Payment Saved Successfully!");
    //
    //        } catch (Exception e) {
    //            em.getTransaction().rollback();
    //            e.printStackTrace();
    //        } finally {
    //            em.close();
    //        }
    //    }
    //    public void saveMonthlyFullPayment(
    //            int enrollmentId,
    //            JTable table,
    //            JTextField monthlyFeeField,
    //            String paymentMethod
    //    ) {
    //        EntityManager em = HibernateConfig.getEntityManager();
    //
    //        try {
    //            if (table.isEditing()) {
    //                table.getCellEditor().stopCellEditing();
    //            }
    //
    //            DefaultTableModel model = (DefaultTableModel) table.getModel();
    //            int rowCount = model.getRowCount();
    //            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
    //            int payingFee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_paid_Textfield.getText().trim());
    //            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();
    //
    //            // 🔥 VALIDATION
    //            if (payingFee <= 0) {
    //                JOptionPane.showMessageDialog(null, "Enter valid payment amount!");
    //                return;
    //            }
    //            if (payingFee < monthlyFee && (note == null || note.isEmpty())) {
    //                JOptionPane.showMessageDialog(null, "You are paying less than monthly fee. Please enter a note!");
    //                return;
    //            }
    //
    //            int remainingAmount = payingFee;
    //
    //            // ============================
    //            // TRANSACTION START
    //            // ============================
    //            em.getTransaction().begin();
    //
    //            // ============================
    //            // GET PAYMENT ID
    //            // ============================
    //            int paymentId = ((Number) em.createNativeQuery(
    //                    "SELECT student_fee_payments_id FROM student_fee_payments WHERE enrollment_id = ? AND status = 1"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            // ============================
    //            // GET LAST INSTALLMENT NO
    //            // ============================
    //            Object lastNoObj = em.createNativeQuery(
    //                    "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
    //            ).setParameter(1, enrollmentId).getSingleResult();
    //            int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;
    //
    //            // ============================
    //            // LOOP TABLE AND DISTRIBUTE PAYMENT
    //            // ============================
    //            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
    //
    //                int year = Integer.parseInt(model.getValueAt(i, 1).toString());
    //                String month = model.getValueAt(i, 2).toString();
    //
    //                Object paidObj = model.getValueAt(i, 3);
    //                int alreadyPaid = 0;
    //                if (paidObj != null && !paidObj.toString().trim().isEmpty()) {
    //                    alreadyPaid = GeneralMethods.parseCommaNumber(paidObj.toString());
    //                }
    //
    //                int toFill = monthlyFee - alreadyPaid;
    //                if (toFill <= 0) {
    //                    continue; // skip fully paid months
    //                }
    //                int payNow = Math.min(toFill, remainingAmount);
    //
    //                installmentNo++;
    //                int monthNum = GeneralMethods.getMonthNumber(month);
    //                String monthFor = year + "-" + String.format("%02d", monthNum);
    //
    //                // ============================
    //                // INSERT INTO DATABASE
    //                // ============================
    //                Query insert = em.createNativeQuery(
    //                        "INSERT INTO student_fee_installments ("
    //                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
    //                );
    //                insert.setParameter(1, paymentId);
    //                insert.setParameter(2, enrollmentId);
    //                insert.setParameter(3, installmentNo);
    //                insert.setParameter(4, payNow);
    //                insert.setParameter(5, paymentMethod);
    //                insert.setParameter(6, "MONTHLY");
    //                insert.setParameter(7, monthFor);
    //                insert.setParameter(8, note.isEmpty() ? "MONTHLY FEE" : note);
    //                insert.executeUpdate();
    //                em.flush();
    //
    //                // ============================
    //                // UPDATE JTable
    //                // ============================
    //                int newPaid = alreadyPaid + payNow;
    //                model.setValueAt(newPaid, i, 3);
    //                if (newPaid >= monthlyFee) {
    //                    model.setValueAt("Paid", i, 4);
    //                } else {
    //                    model.setValueAt("Now Paid", i, 4);
    //                }
    //
    //                remainingAmount -= payNow;
    //                System.out.println("DEBUG Inserted " + payNow + " for month " + monthFor + " | Remaining=" + remainingAmount);
    //            }
    //
    //            // ============================
    //            // IF REMAINING AMOUNT LEFT
    //            // Add to next available months
    //            // ============================
    //            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
    //                int year = Integer.parseInt(model.getValueAt(i, 1).toString());
    //                String month = model.getValueAt(i, 2).toString();
    //
    //                Object paidObj = model.getValueAt(i, 3);
    //                int alreadyPaid = 0;
    //                if (paidObj != null && !paidObj.toString().trim().isEmpty()) {
    //                    alreadyPaid = GeneralMethods.parseCommaNumber(paidObj.toString());
    //                }
    //
    //                if (alreadyPaid >= monthlyFee) {
    //                    continue;
    //                }
    //
    //                int toFill = monthlyFee - alreadyPaid;
    //                int payNow = Math.min(toFill, remainingAmount);
    //
    //                installmentNo++;
    //                int monthNum = GeneralMethods.getMonthNumber(month);
    //                String monthFor = year + "-" + String.format("%02d", monthNum);
    //
    //                Query insert = em.createNativeQuery(
    //                        "INSERT INTO student_fee_installments ("
    //                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
    //                );
    //                insert.setParameter(1, paymentId);
    //                insert.setParameter(2, enrollmentId);
    //                insert.setParameter(3, installmentNo);
    //                insert.setParameter(4, payNow);
    //                insert.setParameter(5, paymentMethod);
    //                insert.setParameter(6, "MONTHLY");
    //                insert.setParameter(7, monthFor);
    //                insert.setParameter(8, note.isEmpty() ? "MONTHLY FEE" : note);
    //                insert.executeUpdate();
    //                em.flush();
    //
    //                int newPaid = alreadyPaid + payNow;
    //                model.setValueAt(newPaid, i, 3);
    //                if (newPaid >= monthlyFee) {
    //                    model.setValueAt("Paid", i, 4);
    //                } else {
    //                    model.setValueAt("Now Paid", i, 4);
    //                }
    //
    //                remainingAmount -= payNow;
    //                System.out.println("DEBUG Extra Insert " + payNow + " for month " + monthFor + " | Remaining=" + remainingAmount);
    //            }
    //
    //            // ============================
    //            // UPDATE MASTER
    //            // ============================
    //            int totalPaid = ((Number) em.createNativeQuery(
    //                    "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id = ?"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            int totalFee = rowCount * monthlyFee;
    //            int balance = totalFee - totalPaid;
    //            String paymentStatus = (balance == 0) ? "COMPLETED" : "ACTIVE";
    //
    //            Query update = em.createNativeQuery(
    //                    "UPDATE student_fee_payments SET total_paid = ?, total_balance = ?, payment_type = ?, payment_status = ?, last_mofidied = NOW() "
    //                    + "WHERE enrollment_id = ? AND status = 1"
    //            );
    //            update.setParameter(1, totalPaid);
    //            update.setParameter(2, balance);
    //            update.setParameter(3, paymentMethod);
    //            update.setParameter(4, paymentStatus);
    //            update.setParameter(5, enrollmentId);
    //            update.executeUpdate();
    //
    //            em.getTransaction().commit();
    //            JOptionPane.showMessageDialog(null, "Monthly Payment Saved Successfully!");
    //
    //        } catch (Exception e) {
    //            em.getTransaction().rollback();
    //            e.printStackTrace();
    //            JOptionPane.showMessageDialog(null, "Error saving payment!");
    //        } finally {
    //            em.close();
    //        }
    //    }
    //    public void saveMonthlyFullPayment(
    //            int enrollmentId,
    //            JTable table,
    //            JTextField monthlyFeeField,
    //            String paymentMethod
    //    ) {
    //        EntityManager em = HibernateConfig.getEntityManager();
    //
    //        try {
    //            if (table.isEditing()) {
    //                table.getCellEditor().stopCellEditing();
    //            }
    //
    //            DefaultTableModel model = (DefaultTableModel) table.getModel();
    //            int rowCount = model.getRowCount();
    //            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
    //            int payingFee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_paid_Textfield.getText().trim());
    //            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();
    //
    //            // 🔥 VALIDATION
    //            if (payingFee <= 0) {
    //                JOptionPane.showMessageDialog(null, "Enter valid payment amount!");
    //                return;
    //            }
    //            if (payingFee < monthlyFee && (note == null || note.isEmpty())) {
    //                JOptionPane.showMessageDialog(null, "You are paying less than monthly fee. Please enter a note!");
    //                return;
    //            }
    //
    //            // ============================
    //            // DISTRIBUTE PAYMENT
    //            // ============================
    //            int remainingAmount = payingFee;
    //
    //            // ============================
    //            // TRANSACTION START
    //            // ============================
    //            em.getTransaction().begin();
    //
    //            // ============================
    //            // GET PAYMENT ID
    //            // ============================
    //            int paymentId = ((Number) em.createNativeQuery(
    //                    "SELECT student_fee_payments_id FROM student_fee_payments WHERE enrollment_id = ? AND status = 1"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            // ============================
    //            // GET LAST INSTALLMENT NO
    //            // ============================
    //            Object lastNoObj = em.createNativeQuery(
    //                    "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
    //            ).setParameter(1, enrollmentId).getSingleResult();
    //            int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;
    //
    //            // ============================
    //            // INSERT INSTALLMENTS
    //            // ============================
    //            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
    //
    //                int year = Integer.parseInt(model.getValueAt(i, 1).toString());
    //                String month = model.getValueAt(i, 2).toString();
    //
    //                Object paidObj = model.getValueAt(i, 3);
    //                int alreadyPaid = 0;
    //                if (paidObj != null && !paidObj.toString().trim().isEmpty()) {
    //                    alreadyPaid = GeneralMethods.parseCommaNumber(paidObj.toString());
    //                }
    //
    //                int toFill = monthlyFee - alreadyPaid;
    //                if (toFill <= 0) {
    //                    continue; // skip fully paid months
    //                }
    //                int payNow = Math.min(toFill, remainingAmount);
    //
    //                installmentNo++;
    //                int monthNum = GeneralMethods.getMonthNumber(month);
    //                String monthFor = year + "-" + String.format("%02d", monthNum);
    //
    //                Query insert = em.createNativeQuery(
    //                        "INSERT INTO student_fee_installments ("
    //                        + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                        + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                        + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
    //                );
    //
    //                insert.setParameter(1, paymentId);
    //                insert.setParameter(2, enrollmentId);
    //                insert.setParameter(3, installmentNo);
    //                insert.setParameter(4, payNow);
    //                insert.setParameter(5, paymentMethod);
    //                insert.setParameter(6, "MONTHLY");
    //                insert.setParameter(7, monthFor);
    //                insert.setParameter(8, note.isEmpty() ? "MONTHLY FEE" : note);
    //
    //                insert.executeUpdate();
    //                em.flush();
    //
    //                System.out.println("DEBUG Inserted " + payNow + " for month " + monthFor + " | Remaining=" + (remainingAmount - payNow));
    //
    //                remainingAmount -= payNow;
    //            }
    //
    //            // ============================
    //            // UPDATE MASTER
    //            // ============================
    //            int totalPaid = ((Number) em.createNativeQuery(
    //                    "SELECT COALESCE(SUM(amount_paid),0) FROM student_fee_installments WHERE enrollment_id = ?"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            int totalFee = rowCount * monthlyFee;
    //            int balance = totalFee - totalPaid;
    //            String paymentStatus = (balance == 0) ? "COMPLETED" : "ACTIVE";
    //
    //            Query update = em.createNativeQuery(
    //                    "UPDATE student_fee_payments SET total_paid = ?, total_balance = ?, payment_type = ?, payment_status = ?, last_mofidied = NOW() "
    //                    + "WHERE enrollment_id = ? AND status = 1"
    //            );
    //            update.setParameter(1, totalPaid);
    //            update.setParameter(2, balance);
    //            update.setParameter(3, paymentMethod);
    //            update.setParameter(4, paymentStatus);
    //            update.setParameter(5, enrollmentId);
    //            update.executeUpdate();
    //
    //            em.getTransaction().commit();
    //            JOptionPane.showMessageDialog(null, "Monthly Payment Saved Successfully!");
    //
    //        } catch (Exception e) {
    //            em.getTransaction().rollback();
    //            e.printStackTrace();
    //            JOptionPane.showMessageDialog(null, "Error saving payment!");
    //        } finally {
    //            em.close();
    //        }
    //    }
    // *************** FIRST WORKING METHOD ********************
    //    public void saveMonthlyFullPayment(
    //            int enrollmentId,
    //            JTable table,
    //            JTextField monthlyFeeField,
    //            String paymentMethod
    //    ) {
    //
    //        EntityManager em = HibernateConfig.getEntityManager();
    //
    //        try {
    //
    //            if (table.isEditing()) {
    //                table.getCellEditor().stopCellEditing();
    //            }
    //
    //            DefaultTableModel model = (DefaultTableModel) table.getModel();
    //
    //            int rowCount = model.getRowCount();
    //            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
    //            int payingFee = GeneralMethods.parseCommaNumber(
    //                    mm_fees_Monthly_total_paid_Textfield.getText().trim()
    //            );
    //
    //            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();
    //
    //            // ============================
    //            // 🔥 VALIDATION
    //            // ============================
    //            if (payingFee <= 0) {
    //                JOptionPane.showMessageDialog(null, "Enter valid payment amount!");
    //                return;
    //            }
    //
    //            // ✅ LESS THAN MONTHLY → REQUIRE NOTE
    //            if (payingFee < monthlyFee) {
    //
    //                if (note == null || note.isEmpty()) {
    //                    JOptionPane.showMessageDialog(
    //                            null,
    //                            "You are paying less than monthly fee.\nPlease enter a note!"
    //                    );
    //                    return;
    //                }
    //
    //            } // ❌ GREATER BUT NOT MULTIPLE → BLOCK
    //            else if (payingFee % monthlyFee != 0) {
    //
    //                JOptionPane.showMessageDialog(
    //                        null,
    //                        "Amount must be multiple of monthly fee!"
    //                );
    //                return;
    //            }
    //
    //            // ============================
    //            // DISTRIBUTE
    //            // ============================
    //            distributeMonthlyPayment(
    //                    table,
    //                    mm_fees_Monthly_total_paid_Textfield,
    //                    mm_fees_Monthly_total_fee_Textfield
    //            );
    //
    //            // ============================
    //            // CALCULATE TOTALS
    //            // ============================
    //            int totalFee = rowCount * monthlyFee;
    //
    //            int totalPaid = 0;
    //            for (int i = 0; i < rowCount; i++) {
    //                Object val = model.getValueAt(i, 3);
    //                if (val != null && !val.toString().trim().isEmpty()) {
    //                    totalPaid += GeneralMethods.parseCommaNumber(val.toString());
    //                }
    //            }
    //
    //            int balance = totalFee - totalPaid;
    //            String paymentStatus = (balance == 0) ? "COMPLETED" : "ACTIVE";
    //
    //            // ============================
    //            // TRANSACTION START
    //            // ============================
    //            em.getTransaction().begin();
    //
    //            // ============================
    //            // UPDATE MASTER
    //            // ============================
    //            Query update = em.createNativeQuery(
    //                    "UPDATE student_fee_payments SET "
    //                    + "total_paid = ?, "
    //                    + "total_balance = ?, "
    //                    + "payment_type = ?, "
    //                    + "payment_status = ?, "
    //                    + "last_mofidied = NOW() "
    //                    + "WHERE enrollment_id = ? AND status = 1"
    //            );
    //
    //            update.setParameter(1, totalPaid);
    //            update.setParameter(2, balance);
    //            update.setParameter(3, paymentMethod);
    //            update.setParameter(4, paymentStatus);
    //            update.setParameter(5, enrollmentId);
    //
    //            update.executeUpdate();
    //
    //            // ============================
    //            // GET PAYMENT ID
    //            // ============================
    //            int paymentId = ((Number) em.createNativeQuery(
    //                    "SELECT student_fee_payments_id "
    //                    + "FROM student_fee_payments "
    //                    + "WHERE enrollment_id = ? AND status = 1"
    //            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
    //
    //            // ============================
    //            // GET LAST INSTALLMENT NO
    //            // ============================
    //            Object lastNoObj = em.createNativeQuery(
    //                    "SELECT MAX(installment_no) FROM student_fee_installments WHERE enrollment_id = ?"
    //            ).setParameter(1, enrollmentId).getSingleResult();
    //
    //            int installmentNo = (lastNoObj != null) ? ((Number) lastNoObj).intValue() : 0;
    //
    //            // ============================
    //            // INSERT INSTALLMENTS
    //            // ============================
    //            for (int i = 0; i < rowCount; i++) {
    //
    //                int year = Integer.parseInt(model.getValueAt(i, 1).toString());
    //                String month = model.getValueAt(i, 2).toString();
    //
    //                Object paidObj = model.getValueAt(i, 3);
    //                Object statusObj = model.getValueAt(i, 4);
    //
    //                int paid = 0;
    //                if (paidObj != null && !paidObj.toString().trim().isEmpty()) {
    //                    paid = GeneralMethods.parseCommaNumber(paidObj.toString());
    //                }
    //
    //                String status = (statusObj != null) ? statusObj.toString() : "";
    //
    //                if (paid > 0 && status.equalsIgnoreCase("Now Paid")) {
    //
    //                    installmentNo++;
    //
    //                    int monthNum = GeneralMethods.getMonthNumber(month);
    //                    String monthFor = year + "-" + String.format("%02d", monthNum);
    //
    //                    Query insert = em.createNativeQuery(
    //                            "INSERT INTO student_fee_installments ("
    //                            + "student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
    //                            + "payment_date, payment_method, payment_type, month_for, remarks, status"
    //                            + ") VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, 1)"
    //                    );
    //
    //                    insert.setParameter(1, paymentId);
    //                    insert.setParameter(2, enrollmentId);
    //                    insert.setParameter(3, installmentNo);
    //                    insert.setParameter(4, paid);
    //                    insert.setParameter(5, paymentMethod);
    //                    insert.setParameter(6, "MONTHLY");
    //                    insert.setParameter(7, monthFor);
    //                    insert.setParameter(8, note.isEmpty() ? "MONTHLY FEE" : note);
    //
    //                    insert.executeUpdate();
    //                }
    //            }
    //
    //            em.getTransaction().commit();
    //
    //            JOptionPane.showMessageDialog(null, "Monthly Payment Saved Successfully!");
    //
    //        } catch (Exception e) {
    //            em.getTransaction().rollback();
    //            e.printStackTrace();
    //            JOptionPane.showMessageDialog(null, "Error saving payment!");
    //        } finally {
    //            em.close();
    //        }
    //    }

    public void saveMonthlyChequePayment(
            int enrollmentId,
            JTable table,
            JTextField monthlyFeeField
    ) {
        EntityManager em = HibernateConfig.getEntityManager();

        try {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int rowCount = model.getRowCount();

            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
            int chequeAmount = GeneralMethods.parseCommaNumber(mm_fees_cheq_cheque_amount.getText().trim());

            String chequeNo = mm_fees_cheq_cheque_number.getText();
            String bank = mm_fees_cheq_cheque_bank.getEditor().getItem().toString();
            String branch = mm_fees_cheq_cheque_branch.getText();
            Date chequeDate = mm_fees_cheq_cheque_date.getDate();
            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();

            // =====================================================
            // VALIDATION: ZERO AND PARTIAL PAYMENTS
            // =====================================================
            if (chequeAmount < 0) {
                JOptionPane.showMessageDialog(null, "Invalid cheque amount!");
                return;
            }

            if (chequeAmount == 0 && (note == null || note.trim().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Zero payment requires remarks!");
                return;
            }

            if (chequeAmount < monthlyFee && (note == null || note.trim().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Partial payment requires remarks!");
                return;
            }

            em.getTransaction().begin();

            int paymentId = ((Number) em.createNativeQuery(
                    "SELECT student_fee_payments_id FROM student_fee_payments WHERE enrollment_id=? AND status=1"
            ).setParameter(1, enrollmentId).getSingleResult()).intValue();

            int installmentNo = ((Number) em.createNativeQuery(
                    "SELECT COALESCE(MAX(installment_no),0) FROM student_fee_installments WHERE enrollment_id=?"
            ).setParameter(1, enrollmentId).getSingleResult()).intValue();

            int remainingAmount = chequeAmount;

            // =====================================================
            // SKIP MONTHS WITH REMARKS
            // =====================================================
            List<Object[]> debugList = em.createNativeQuery(
                    "SELECT month_for, COALESCE(SUM(amount_paid),0), GROUP_CONCAT(remarks) "
                    + "FROM student_fee_installments WHERE enrollment_id=? AND status=1 GROUP BY month_for"
            )
                    .setParameter(1, enrollmentId)
                    .getResultList();

            Set<String> skipMonths = new HashSet<>();
            for (Object[] row : debugList) {
                String m = row[0].toString();
                int paid = ((Number) row[1]).intValue();
                String remarks = row[2] != null ? row[2].toString() : "";
                if (!remarks.trim().isEmpty() && paid < monthlyFee) {
                    skipMonths.add(m);
                }
            }

            Set<String> filledMonths = new HashSet<>();

            // =====================================================
            // ZERO PAYMENT SCENARIO
            // =====================================================
            if (chequeAmount == 0) {
                // Determine next month
                String targetMonth = model.getValueAt(0, 1).toString() + "-"
                        + String.format("%02d", GeneralMethods.getMonthNumber(model.getValueAt(0, 2).toString()));

                installmentNo++;

                em.createNativeQuery(
                        "INSERT INTO student_fee_installments "
                        + "(student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
                        + "payment_date, payment_method, payment_type, month_for, remarks, status) "
                        + "VALUES (?, ?, ?, 0, NOW(), ?, ?, ?, ?, 1)"
                )
                        .setParameter(1, paymentId)
                        .setParameter(2, enrollmentId)
                        .setParameter(3, installmentNo)
                        .setParameter(4, "CHEQUE")
                        .setParameter(5, "MONTHLY")
                        .setParameter(6, targetMonth)
                        .setParameter(7, note)
                        .executeUpdate();

                int installmentId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).intValue();

//                // 🔹 LEDGER
//                LedgerHelper.saveLedger(em,
//                        0,
//                        "CREDIT",
//                        "Zero cheque entry for " + targetMonth + " | " + note,
//                        "STUDENT_PAYMENT",
//                        installmentId,
//                        "CHEQUE",
//                        "MONTHLY_FEE",
//                        username
//                );

                // 🔹 LOG ONLY (NO LEDGER, NO CHEQUE TABLE)
                LogHelper.saveLog(em,
                        "STUDENT_PAYMENT",
                        enrollmentId,
                        "INSERT",
                        0,
                        "CHEQUE",
                        "Zero cheque entry for " + targetMonth + " | " + note,
                        username
                );

                em.getTransaction().commit();
                JOptionPane.showMessageDialog(null, "Zero payment saved for " + targetMonth);
                return; // Skip cheque details
            }

            // =====================================================
            // FILL PARTIAL MONTHS WITHOUT REMARKS
            // =====================================================
            List<Object[]> partialMonths = em.createNativeQuery(
                    "SELECT month_for, COALESCE(SUM(amount_paid),0) "
                    + "FROM student_fee_installments WHERE enrollment_id=? AND status=1 "
                    + "GROUP BY month_for HAVING SUM(amount_paid) < ? ORDER BY month_for ASC"
            )
                    .setParameter(1, enrollmentId)
                    .setParameter(2, monthlyFee)
                    .getResultList();

            for (Object[] row : partialMonths) {
                if (remainingAmount <= 0) {
                    break;
                }

                String monthFor = row[0].toString();
                int paidSoFar = ((Number) row[1]).intValue();

                if (skipMonths.contains(monthFor) || filledMonths.contains(monthFor)) {
                    continue;
                }

                int payNow = Math.min(monthlyFee - paidSoFar, remainingAmount);
                installmentNo++;

                // Insert installment
                em.createNativeQuery(
                        "INSERT INTO student_fee_installments VALUES (NULL,?,?,?,?,NOW(),?,?,?, ?,1)"
                )
                        .setParameter(1, paymentId)
                        .setParameter(2, enrollmentId)
                        .setParameter(3, installmentNo)
                        .setParameter(4, payNow)
                        .setParameter(5, "CHEQUE")
                        .setParameter(6, "MONTHLY")
                        .setParameter(7, monthFor)
                        .setParameter(8, note)
                        .executeUpdate();

                int installmentId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).intValue();

                // Insert cheque details
                em.createNativeQuery(
                        "INSERT INTO student_fee_cheque_details "
                        + "(student_fee_installments_id, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', 1)"
                )
                        .setParameter(1, installmentId)
                        .setParameter(2, chequeNo)
                        .setParameter(3, bank)
                        .setParameter(4, branch)
                        .setParameter(5, chequeDate)
                        .setParameter(6, payNow)
                        .executeUpdate();

//                // 🔹 LEDGER
//                LedgerHelper.saveLedger(em,
//                        payNow,
//                        "CREDIT",
//                        "Cheque pending - " + monthFor,
//                        "STUDENT_PAYMENT",
//                        installmentId,
//                        "CHEQUE",
//                        "MONTHLY_FEE",
//                        username
//                );

                // 🔹 LOG
                LogHelper.saveLog(em,
                        "STUDENT_PAYMENT",
                        installmentId,
                        "INSERT",
                        payNow,
                        "CHEQUE",
                        "Cheque saved (partial) for " + monthFor,
                        username
                );

                remainingAmount -= payNow;
                filledMonths.add(monthFor);
            }

            // =====================================================
            // DISTRIBUTE TO NEW MONTHS IN TABLE
            // =====================================================
            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
                int year = Integer.parseInt(model.getValueAt(i, 1).toString());
                String monthName = model.getValueAt(i, 2).toString();
                String ym = String.format("%04d-%02d", year, GeneralMethods.getMonthNumber(monthName));

                int paid = GeneralMethods.parseCommaNumber(
                        model.getValueAt(i, 3).toString().isEmpty() ? "0" : model.getValueAt(i, 3).toString()
                );

                if (skipMonths.contains(ym) || filledMonths.contains(ym) || paid >= monthlyFee) {
                    continue;
                }

                int payNow = Math.min(monthlyFee - paid, remainingAmount);
                installmentNo++;

                // Insert installment
                em.createNativeQuery(
                        "INSERT INTO student_fee_installments VALUES (NULL,?,?,?,?,NOW(),?,?,?, ?,1)"
                )
                        .setParameter(1, paymentId)
                        .setParameter(2, enrollmentId)
                        .setParameter(3, installmentNo)
                        .setParameter(4, payNow)
                        .setParameter(5, "CHEQUE")
                        .setParameter(6, "MONTHLY")
                        .setParameter(7, ym)
                        .setParameter(8, note)
                        .executeUpdate();

                int installmentId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).intValue();

                // Insert cheque details
                em.createNativeQuery(
                        "INSERT INTO student_fee_cheque_details "
                        + "(student_fee_installments_id, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', 1)"
                )
                        .setParameter(1, installmentId)
                        .setParameter(2, chequeNo)
                        .setParameter(3, bank)
                        .setParameter(4, branch)
                        .setParameter(5, chequeDate)
                        .setParameter(6, payNow)
                        .executeUpdate();

//                // ledger
//                LedgerHelper.saveLedger(em,
//                        payNow,
//                        "CREDIT",
//                        "Cheque pending - " + ym,
//                        "STUDENT_PAYMENT",
//                        installmentId,
//                        "CHEQUE",
//                        "MONTHLY_FEE",
//                        username
//                );

                // log
                LogHelper.saveLog(em,
                        "STUDENT_PAYMENT",
                        installmentId,
                        "INSERT",
                        payNow,
                        "CHEQUE",
                        "Cheque saved for " + ym,
                        username
                );

                remainingAmount -= payNow;
                filledMonths.add(ym);
            }

            em.getTransaction().commit();
            JOptionPane.showMessageDialog(null, "Cheque saved as PENDING!");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

//    public void saveMonthlyChequePayment(
//            int enrollmentId,
//            JTable table,
//            JTextField monthlyFeeField
//    ) {
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            if (table.isEditing()) {
//                table.getCellEditor().stopCellEditing();
//            }
//
//            DefaultTableModel model = (DefaultTableModel) table.getModel();
//            int rowCount = model.getRowCount();
//
//            int monthlyFee = GeneralMethods.parseCommaNumber(monthlyFeeField.getText().trim());
//            int chequeAmount = GeneralMethods.parseCommaNumber(mm_fees_cheq_cheque_amount.getText().trim());
//
//            String chequeNo = mm_fees_cheq_cheque_number.getText();
//            String bank = mm_fees_cheq_cheque_bank.getEditor().getItem().toString();
//            String branch = mm_fees_cheq_cheque_branch.getText();
//            Date chequeDate = mm_fees_cheq_cheque_date.getDate();
//            String note = mm_fees_Monthly_fee_note_Textarea.getText().trim();
//
//            // =====================================================
//            // VALIDATION: ZERO AND PARTIAL PAYMENTS
//            // =====================================================
//            if (chequeAmount < 0) {
//                JOptionPane.showMessageDialog(null, "Invalid cheque amount!");
//                return;
//            }
//            
//            // Zero payment must have remarks
//            if (chequeAmount == 0 && (note == null || note.trim().isEmpty())) {
//                JOptionPane.showMessageDialog(null, "Zero payment requires remarks!");
//                return;
//            }
//
    //// ❗ Partial payment must have remarks
//            if (chequeAmount < monthlyFee && (note == null || note.trim().isEmpty())) {
//                JOptionPane.showMessageDialog(null, "Partial payment requires remarks!");
//                return;
//            }
//
//
//
//            em.getTransaction().begin();
//
//            int paymentId = ((Number) em.createNativeQuery(
//                    "SELECT student_fee_payments_id FROM student_fee_payments WHERE enrollment_id=? AND status=1"
//            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
//
//            int installmentNo = ((Number) em.createNativeQuery(
//                    "SELECT COALESCE(MAX(installment_no),0) FROM student_fee_installments WHERE enrollment_id=?"
//            ).setParameter(1, enrollmentId).getSingleResult()).intValue();
//
//            int remainingAmount = chequeAmount;
//
//            // =====================================================
//            // SKIP MONTHS WITH REMARKS
//            // =====================================================
//            List<Object[]> debugList = em.createNativeQuery(
//                    "SELECT month_for, COALESCE(SUM(amount_paid),0), GROUP_CONCAT(remarks) "
//                    + "FROM student_fee_installments WHERE enrollment_id=? AND status=1 GROUP BY month_for"
//            )
//                    .setParameter(1, enrollmentId)
//                    .getResultList();
//
//            Set<String> skipMonths = new HashSet<>();
//            for (Object[] row : debugList) {
//                String m = row[0].toString();
//                int paid = ((Number) row[1]).intValue();
//                String remarks = row[2] != null ? row[2].toString() : "";
//                if (remarks != null && !remarks.trim().isEmpty() && paid < monthlyFee) {
//                    skipMonths.add(m);
//                }
//            }
//
//            Set<String> filledMonths = new HashSet<>();
//
//            // =====================================================
//            // FILL PARTIAL MONTHS WITHOUT REMARKS
//            // =====================================================
//            List<Object[]> partialMonths = em.createNativeQuery(
//                    "SELECT month_for, COALESCE(SUM(amount_paid),0) "
//                    + "FROM student_fee_installments WHERE enrollment_id=? AND status=1 "
//                    + "GROUP BY month_for HAVING SUM(amount_paid) < ? ORDER BY month_for ASC"
//            )
//                    .setParameter(1, enrollmentId)
//                    .setParameter(2, monthlyFee)
//                    .getResultList();
//
//            for (Object[] row : partialMonths) {
//                if (remainingAmount <= 0) {
//                    break;
//                }
//
//                String monthFor = row[0].toString();
//                int paidSoFar = ((Number) row[1]).intValue();
//
//                if (skipMonths.contains(monthFor)) {
//                    continue;
//                }
//                if (filledMonths.contains(monthFor)) {
//                    continue;
//                }
//
//                int balance = monthlyFee - paidSoFar;
//                if (balance <= 0) {
//                    continue;
//                }
//
//                int payNow = Math.min(balance, remainingAmount);
//                installmentNo++;
//
//                // Insert installment
//                em.createNativeQuery(
//                        "INSERT INTO student_fee_installments VALUES (NULL,?,?,?,?,NOW(),?,?,?, ?,1)"
//                )
//                        .setParameter(1, paymentId)
//                        .setParameter(2, enrollmentId)
//                        .setParameter(3, installmentNo)
//                        .setParameter(4, payNow)
//                        .setParameter(5, "CHEQUE")
//                        .setParameter(6, "MONTHLY")
//                        .setParameter(7, monthFor)
//                        .setParameter(8, note)
//                        .executeUpdate();
//
//                int installmentId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).intValue();
//
//                // Insert cheque details
//                em.createNativeQuery(
//                        "INSERT INTO student_fee_cheque_details "
//                        + "(student_fee_installments_id, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
//                        + "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', 1)"
//                )
//                        .setParameter(1, installmentId)
//                        .setParameter(2, chequeNo)
//                        .setParameter(3, bank)
//                        .setParameter(4, branch)
//                        .setParameter(5, chequeDate)
//                        .setParameter(6, payNow)
//                        .executeUpdate();
//
//                remainingAmount -= payNow;
//                filledMonths.add(monthFor);
//            }
//
//            // =====================================================
//            // DISTRIBUTE TO NEW MONTHS IN TABLE
//            // =====================================================
//            for (int i = 0; i < rowCount && remainingAmount > 0; i++) {
//                int year = Integer.parseInt(model.getValueAt(i, 1).toString());
//                String monthName = model.getValueAt(i, 2).toString();
//                String ym = String.format("%04d-%02d", year, GeneralMethods.getMonthNumber(monthName));
//
//                int paid = GeneralMethods.parseCommaNumber(
//                        model.getValueAt(i, 3).toString().isEmpty() ? "0" : model.getValueAt(i, 3).toString()
//                );
//
//                if (skipMonths.contains(ym)) {
//                    continue;
//                }
//                if (filledMonths.contains(ym)) {
//                    continue;
//                }
//                if (paid >= monthlyFee) {
//                    continue;
//                }
//
//                int payNow = Math.min(monthlyFee - paid, remainingAmount);
//                installmentNo++;
//
//                // Insert installment
//                em.createNativeQuery(
//                        "INSERT INTO student_fee_installments VALUES (NULL,?,?,?,?,NOW(),?,?,?, ?,1)"
//                )
//                        .setParameter(1, paymentId)
//                        .setParameter(2, enrollmentId)
//                        .setParameter(3, installmentNo)
//                        .setParameter(4, payNow)
//                        .setParameter(5, "CHEQUE")
//                        .setParameter(6, "MONTHLY")
//                        .setParameter(7, ym)
//                        .setParameter(8, note)
//                        .executeUpdate();
//
//                int installmentId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).intValue();
//
//                // Insert cheque details
//                em.createNativeQuery(
//                        "INSERT INTO student_fee_cheque_details "
//                        + "(student_fee_installments_id, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
//                        + "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', 1)"
//                )
//                        .setParameter(1, installmentId)
//                        .setParameter(2, chequeNo)
//                        .setParameter(3, bank)
//                        .setParameter(4, branch)
//                        .setParameter(5, chequeDate)
//                        .setParameter(6, payNow)
//                        .executeUpdate();
//
//                remainingAmount -= payNow;
//                filledMonths.add(ym);
//            }
//
//            em.getTransaction().commit();
//            JOptionPane.showMessageDialog(null, "Cheque saved as PENDING!");
//
//        } catch (Exception e) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }


    public void updateMonthlySummaryFields(
            int enrollmentId,
            JTable table,
            JTextField paidMonthsField,
            JTextField paidAmountField,
            JTextField pendingMonthsField
    ) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        int paidMonths = 0;
        int pendingMonths = 0;
        int totalPaidAmount = 0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object statusObj = model.getValueAt(i, 4);
            Object paidObj = model.getValueAt(i, 3);

            String status = (statusObj != null) ? statusObj.toString().trim() : "";

            System.out.println("Row " + i
                    + " | Status: " + status
                    + " | Paid: " + paidObj);

            // ============================
            // PAID
            // ============================
            if (status.equalsIgnoreCase("PAID") || status.equalsIgnoreCase("Now Paid")) {

                paidMonths++;

                if (paidObj != null && !paidObj.toString().trim().isEmpty()) {
                    totalPaidAmount += GeneralMethods.parseCommaNumber(paidObj.toString());
                }

            } else {
                pendingMonths++;
            }
        }

        // ============================
        // 🔥 GET PENDING CHEQUE FROM DB
        // ============================
        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
        int pendingCheque = dao.getPendingChequeAmount(enrollmentId);

        // ============================
        // SET TEXTFIELDS
        // ============================
        paidMonthsField.setText(String.valueOf(paidMonths));
        paidAmountField.setText(GeneralMethods.formatWithComma(totalPaidAmount - pendingCheque));

        // 🔥 SET CHEQUE PENDING
        mm_fees_Monthly_tot_cheque_pending_Textfield
                .setText(GeneralMethods.formatWithComma(pendingCheque));

        // 🔥 TOTAL PAID (CASH + CHEQUE)
        mm_fees_Monthly_tot_totPaid_Textfield.setText(
                GeneralMethods.formatWithComma(totalPaidAmount)
        );

        pendingMonthsField.setText(String.valueOf(pendingMonths));

        int month_fee = GeneralMethods.parseCommaNumber(
                mm_fees_Monthly_total_fee_Textfield.getText()
        );

        mm_fees_Monthly_tot_pending_balancee_Textfield.setText(
                GeneralMethods.formatWithComma((pendingMonths + paidMonths) * month_fee - totalPaidAmount)
        );

        mm_fees_Monthly_total_balance_Textfield.setText(GeneralMethods.formatWithComma(pendingMonths * month_fee));
        System.out.println("pendingMonths * month_fee = " + pendingMonths * month_fee);
    }

//    public void updateMonthlySummaryFields(
//            JTable table,
//            JTextField paidMonthsField,
//            JTextField paidAmountField,
//            JTextField pendingMonthsField
//    ) {
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//        DefaultTableModel master_fee = (DefaultTableModel) Fees_Management.fm_fees_course_table.getModel();
//
//        int paidMonths = 0;
//        int pendingMonths = 0;
//        int totalPaidAmount = 0;
//
//        for (int i = 0; i < model.getRowCount(); i++) {
//
//            Object statusObj = model.getValueAt(i, 4); // status column
//            Object paidObj = model.getValueAt(i, 3);   // paid fee column
//
//            String status = (statusObj != null) ? statusObj.toString().trim() : "";
//            System.out.println("Row " + i
//                    + " | Status: " + status
//                    + " | Paid: " + paidObj);
//
//            // ============================
//            // PAID ROWS
//            // ============================
//            if (status.equalsIgnoreCase("PAID") || status.equalsIgnoreCase("Now Paid")) {
//
//                paidMonths++;
//
//                if (paidObj != null && !paidObj.toString().trim().isEmpty()) {
//                    totalPaidAmount += GeneralMethods.parseCommaNumber(paidObj.toString());
//                }
//
//            } // ============================
//            // PENDING ROWS
//            // ============================
//            else {
//                pendingMonths++;
//            }
//        }
//
//        // ============================
//        // SET TEXTFIELDS
//        // ============================
//        paidMonthsField.setText(String.valueOf(paidMonths));
//        paidAmountField.setText(GeneralMethods.formatWithComma(totalPaidAmount));
//        //  int tot_paid = GeneralMethods.parseCommaNumber(mm_fees_Monthly_tot_paid_amount_Textfield.getText());
//        int tot_cheq = GeneralMethods.parseCommaNumber(mm_fees_Monthly_tot_cheque_pending_Textfield.getText());
//        mm_fees_Monthly_tot_totPaid_Textfield.setText(GeneralMethods.formatWithComma(tot_cheq + totalPaidAmount));
//
//        pendingMonthsField.setText(String.valueOf(pendingMonths));
//        int month_fee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_fee_Textfield.getText());
//        mm_fees_Monthly_tot_pending_balancee_Textfield.setText(GeneralMethods.formatWithComma(pendingMonths * month_fee));
//    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        monthly_jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        mm_fees_Monthly_total_paid_Textfield = new javax.swing.JTextField();
        firstName_label7 = new javax.swing.JLabel();
        mm_fees_Monthly_total_balance_Textfield = new javax.swing.JTextField();
        firstName_label8 = new javax.swing.JLabel();
        sup_payment_cash_label = new javax.swing.JLabel();
        mm_fees_Monthly_total_fee_Textfield = new javax.swing.JTextField();
        buttonGradient2 = new Classes.ButtonGradient();
        jLabel14 = new javax.swing.JLabel();
        mm_fees_Monthly_payment_method_combo = new javax.swing.JComboBox<>();
        mm_fees_Monthly_fee_cal_Textfield = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        mm_fees_cheq_cheque_number = new javax.swing.JTextField();
        mm_fees_cheq_cheque_bank = new javax.swing.JComboBox<>();
        mm_fees_cheq_cheque_branch = new javax.swing.JTextField();
        mm_fees_cheq_cheque_amount = new javax.swing.JTextField();
        mm_fees_cheq_cheque_date = new com.toedter.calendar.JDateChooser();
        sup_payment_cheque_label = new javax.swing.JLabel();
        fm_fees_cheq_full_fees_Textfield = new javax.swing.JTextField();
        mm_fees_cheq_cheque_status = new javax.swing.JComboBox<>();
        mm_fees_cheq_cheque_remaining = new javax.swing.JTextField();
        buttonGradient3 = new Classes.ButtonGradient();
        sup_payment_cheque_label1 = new javax.swing.JLabel();
        fm_fees_cheq_full_fees_cal_Textfield = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mm_fees_monthly_table = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        mm_fees_Monthly_payment_date = new com.toedter.calendar.JDateChooser();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mm_fees_Monthly_fee_note_Textarea = new javax.swing.JEditorPane();
        jPanel6 = new javax.swing.JPanel();
        firstName_label9 = new javax.swing.JLabel();
        firstName_label10 = new javax.swing.JLabel();
        mm_fees_Monthly_tot_paid_months_Textfield = new javax.swing.JTextField();
        mm_fees_Monthly_tot_paid_amount_Textfield = new javax.swing.JTextField();
        firstName_label13 = new javax.swing.JLabel();
        mm_fees_Monthly_tot_cheque_pending_Textfield = new javax.swing.JTextField();
        mm_fees_Monthly_tot_totPaid_Textfield = new javax.swing.JTextField();
        firstName_label14 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        firstName_label15 = new javax.swing.JLabel();
        firstName_label16 = new javax.swing.JLabel();
        mm_fees_Monthly_tot_pending_months_Textfield = new javax.swing.JTextField();
        mm_fees_Monthly_tot_pending_balancee_Textfield = new javax.swing.JTextField();

        monthly_jTabbedPane2.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        mm_fees_Monthly_total_paid_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_total_paid_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_total_paid_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_total_paid_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_total_paid_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_total_paid_TextfieldKeyReleased(evt);
            }
        });

        firstName_label7.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label7.setText("Payment");

        mm_fees_Monthly_total_balance_Textfield.setEditable(false);
        mm_fees_Monthly_total_balance_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_total_balance_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_total_balance_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_total_balance_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_total_balance_TextfieldKeyReleased(evt);
            }
        });

        firstName_label8.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label8.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label8.setText("Total Balance");

        sup_payment_cash_label.setBackground(new java.awt.Color(33, 33, 33));
        sup_payment_cash_label.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        sup_payment_cash_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sup_payment_cash_label.setText("Monthly Fee");

        mm_fees_Monthly_total_fee_Textfield.setEditable(false);
        mm_fees_Monthly_total_fee_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_total_fee_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_total_fee_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_total_fee_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_total_fee_TextfieldKeyReleased(evt);
            }
        });

        buttonGradient2.setText("F1");
        buttonGradient2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient2ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel14.setText("Select Payment Method");

        mm_fees_Monthly_payment_method_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        mm_fees_Monthly_payment_method_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CASH", "CARD" }));

        mm_fees_Monthly_fee_cal_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_fee_cal_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_fee_cal_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_fee_cal_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_fee_cal_TextfieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(sup_payment_cash_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mm_fees_Monthly_total_fee_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mm_fees_Monthly_fee_cal_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(firstName_label7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mm_fees_Monthly_total_paid_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(mm_fees_Monthly_payment_method_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(firstName_label8, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mm_fees_Monthly_total_balance_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mm_fees_Monthly_payment_method_combo)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sup_payment_cash_label, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(mm_fees_Monthly_fee_cal_Textfield)
                    .addComponent(mm_fees_Monthly_total_fee_Textfield))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mm_fees_Monthly_total_paid_Textfield)
                    .addComponent(firstName_label7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mm_fees_Monthly_total_balance_Textfield)
                    .addComponent(firstName_label8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        monthly_jTabbedPane2.addTab("Cash / Card", jPanel7);

        mm_fees_cheq_cheque_number.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_cheq_cheque_number.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_cheq_cheque_numberActionPerformed(evt);
            }
        });
        mm_fees_cheq_cheque_number.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_cheq_cheque_numberKeyReleased(evt);
            }
        });

        mm_fees_cheq_cheque_bank.setEditable(true);
        mm_fees_cheq_cheque_bank.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_cheq_cheque_bank.setMinimumSize(new java.awt.Dimension(83, 30));
        mm_fees_cheq_cheque_bank.setPreferredSize(new java.awt.Dimension(72, 30));
        mm_fees_cheq_cheque_bank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_cheq_cheque_bankActionPerformed(evt);
            }
        });
        mm_fees_cheq_cheque_bank.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_cheq_cheque_bankKeyReleased(evt);
            }
        });

        mm_fees_cheq_cheque_branch.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_cheq_cheque_branch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_cheq_cheque_branchActionPerformed(evt);
            }
        });
        mm_fees_cheq_cheque_branch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_cheq_cheque_branchKeyReleased(evt);
            }
        });

        mm_fees_cheq_cheque_amount.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_cheq_cheque_amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_cheq_cheque_amountActionPerformed(evt);
            }
        });
        mm_fees_cheq_cheque_amount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_cheq_cheque_amountKeyReleased(evt);
            }
        });

        mm_fees_cheq_cheque_date.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N

        sup_payment_cheque_label.setBackground(new java.awt.Color(33, 33, 33));
        sup_payment_cheque_label.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        sup_payment_cheque_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sup_payment_cheque_label.setText("Monthly Fee");

        fm_fees_cheq_full_fees_Textfield.setEditable(false);
        fm_fees_cheq_full_fees_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_full_fees_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_full_fees_TextfieldActionPerformed(evt);
            }
        });
        fm_fees_cheq_full_fees_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_full_fees_TextfieldKeyReleased(evt);
            }
        });

        mm_fees_cheq_cheque_status.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_cheq_cheque_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending" }));
        mm_fees_cheq_cheque_status.setMinimumSize(new java.awt.Dimension(83, 30));
        mm_fees_cheq_cheque_status.setPreferredSize(new java.awt.Dimension(72, 30));
        mm_fees_cheq_cheque_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_cheq_cheque_statusActionPerformed(evt);
            }
        });
        mm_fees_cheq_cheque_status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_cheq_cheque_statusKeyReleased(evt);
            }
        });

        mm_fees_cheq_cheque_remaining.setEditable(false);
        mm_fees_cheq_cheque_remaining.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_cheq_cheque_remaining.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_cheq_cheque_remainingActionPerformed(evt);
            }
        });
        mm_fees_cheq_cheque_remaining.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_cheq_cheque_remainingKeyReleased(evt);
            }
        });

        buttonGradient3.setText("F1");
        buttonGradient3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient3ActionPerformed(evt);
            }
        });

        sup_payment_cheque_label1.setBackground(new java.awt.Color(33, 33, 33));
        sup_payment_cheque_label1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        sup_payment_cheque_label1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sup_payment_cheque_label1.setText("Total Balance");

        fm_fees_cheq_full_fees_cal_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_full_fees_cal_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_full_fees_cal_TextfieldActionPerformed(evt);
            }
        });
        fm_fees_cheq_full_fees_cal_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_full_fees_cal_TextfieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mm_fees_cheq_cheque_number)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(sup_payment_cheque_label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fm_fees_cheq_full_fees_Textfield)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fm_fees_cheq_full_fees_cal_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(mm_fees_cheq_cheque_bank, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mm_fees_cheq_cheque_branch, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(sup_payment_cheque_label1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(mm_fees_cheq_cheque_amount, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addGap(186, 186, 186)
                                .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mm_fees_cheq_cheque_remaining)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(mm_fees_cheq_cheque_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mm_fees_cheq_cheque_status, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sup_payment_cheque_label, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(fm_fees_cheq_full_fees_cal_Textfield)
                    .addComponent(fm_fees_cheq_full_fees_Textfield))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mm_fees_cheq_cheque_number, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mm_fees_cheq_cheque_bank, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mm_fees_cheq_cheque_branch, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mm_fees_cheq_cheque_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mm_fees_cheq_cheque_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mm_fees_cheq_cheque_status, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mm_fees_cheq_cheque_remaining, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sup_payment_cheque_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        monthly_jTabbedPane2.addTab("Cheque", jPanel10);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Payment Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        mm_fees_monthly_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Year", "Month", "Paid Fee", "Payment Method", "Cheque Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        mm_fees_monthly_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mm_fees_monthly_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(mm_fees_monthly_table);
        if (mm_fees_monthly_table.getColumnModel().getColumnCount() > 0) {
            mm_fees_monthly_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            mm_fees_monthly_table.getColumnModel().getColumn(1).setPreferredWidth(150);
            mm_fees_monthly_table.getColumnModel().getColumn(2).setPreferredWidth(150);
            mm_fees_monthly_table.getColumnModel().getColumn(3).setPreferredWidth(120);
        }

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel13.setText("Select Payment Date");

        mm_fees_Monthly_payment_date.setForeground(new java.awt.Color(204, 204, 204));
        mm_fees_Monthly_payment_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Fee Note", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jScrollPane2.setViewportView(mm_fees_Monthly_fee_note_Textarea);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Total Paid", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        firstName_label9.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label9.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label9.setText("Paid Months");

        firstName_label10.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label10.setText("Paid Amount");

        mm_fees_Monthly_tot_paid_months_Textfield.setEditable(false);
        mm_fees_Monthly_tot_paid_months_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_tot_paid_months_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_tot_paid_months_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_tot_paid_months_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_paid_months_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_paid_months_TextfieldKeyReleased(evt);
            }
        });

        mm_fees_Monthly_tot_paid_amount_Textfield.setEditable(false);
        mm_fees_Monthly_tot_paid_amount_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_tot_paid_amount_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_tot_paid_amount_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_tot_paid_amount_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_paid_amount_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_paid_amount_TextfieldKeyReleased(evt);
            }
        });

        firstName_label13.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label13.setText("Cheque (Pending)");

        mm_fees_Monthly_tot_cheque_pending_Textfield.setEditable(false);
        mm_fees_Monthly_tot_cheque_pending_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_tot_cheque_pending_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_tot_cheque_pending_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_tot_cheque_pending_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_cheque_pending_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_cheque_pending_TextfieldKeyReleased(evt);
            }
        });

        mm_fees_Monthly_tot_totPaid_Textfield.setEditable(false);
        mm_fees_Monthly_tot_totPaid_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_tot_totPaid_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_tot_totPaid_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_tot_totPaid_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_totPaid_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_totPaid_TextfieldKeyReleased(evt);
            }
        });

        firstName_label14.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label14.setText("Total Paid");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(firstName_label9, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(mm_fees_Monthly_tot_paid_months_Textfield))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mm_fees_Monthly_tot_paid_amount_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstName_label10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(firstName_label13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mm_fees_Monthly_tot_cheque_pending_Textfield))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mm_fees_Monthly_tot_totPaid_Textfield)
                    .addComponent(firstName_label14, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstName_label9)
                    .addComponent(firstName_label10)
                    .addComponent(firstName_label13)
                    .addComponent(firstName_label14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mm_fees_Monthly_tot_paid_months_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mm_fees_Monthly_tot_paid_amount_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mm_fees_Monthly_tot_cheque_pending_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mm_fees_Monthly_tot_totPaid_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Total Balance", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        firstName_label15.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label15.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label15.setText("Balance Months");

        firstName_label16.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label16.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label16.setText("Balance Amount");

        mm_fees_Monthly_tot_pending_months_Textfield.setEditable(false);
        mm_fees_Monthly_tot_pending_months_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_tot_pending_months_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_tot_pending_months_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_tot_pending_months_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_pending_months_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_pending_months_TextfieldKeyReleased(evt);
            }
        });

        mm_fees_Monthly_tot_pending_balancee_Textfield.setEditable(false);
        mm_fees_Monthly_tot_pending_balancee_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        mm_fees_Monthly_tot_pending_balancee_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mm_fees_Monthly_tot_pending_balancee_TextfieldActionPerformed(evt);
            }
        });
        mm_fees_Monthly_tot_pending_balancee_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_pending_balancee_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mm_fees_Monthly_tot_pending_balancee_TextfieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(firstName_label15, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(mm_fees_Monthly_tot_pending_months_Textfield))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mm_fees_Monthly_tot_pending_balancee_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstName_label16))
                .addContainerGap(233, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstName_label15)
                    .addComponent(firstName_label16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mm_fees_Monthly_tot_pending_months_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mm_fees_Monthly_tot_pending_balancee_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mm_fees_Monthly_payment_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(monthly_jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mm_fees_Monthly_payment_date, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(monthly_jTabbedPane2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mm_fees_monthly_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mm_fees_monthly_tableMouseClicked

    }//GEN-LAST:event_mm_fees_monthly_tableMouseClicked

    private void mm_fees_Monthly_total_paid_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_total_paid_TextfieldActionPerformed

    }//GEN-LAST:event_mm_fees_Monthly_total_paid_TextfieldActionPerformed

    private void mm_fees_Monthly_total_paid_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_total_paid_TextfieldKeyPressed

    }//GEN-LAST:event_mm_fees_Monthly_total_paid_TextfieldKeyPressed

    private void mm_fees_Monthly_total_paid_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_total_paid_TextfieldKeyReleased
        if (mm_fees_Monthly_total_paid_Textfield.getText().equals("")) {
            mm_fees_Monthly_tot_paid_months_Textfield.setText("");
            mm_fees_Monthly_tot_paid_amount_Textfield.setText("");
        }
    }//GEN-LAST:event_mm_fees_Monthly_total_paid_TextfieldKeyReleased

    private void mm_fees_Monthly_total_balance_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_total_balance_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_total_balance_TextfieldActionPerformed

    private void mm_fees_Monthly_total_balance_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_total_balance_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_total_balance_TextfieldKeyReleased

    private void mm_fees_Monthly_total_fee_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_total_fee_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_total_fee_TextfieldActionPerformed

    private void mm_fees_Monthly_total_fee_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_total_fee_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_total_fee_TextfieldKeyReleased

    private void mm_fees_cheq_cheque_numberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_numberActionPerformed
        mm_fees_cheq_cheque_amount.requestFocus();
    }//GEN-LAST:event_mm_fees_cheq_cheque_numberActionPerformed

    private void mm_fees_cheq_cheque_numberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_numberKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_numberKeyReleased

    private void mm_fees_cheq_cheque_bankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_bankActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_bankActionPerformed

    private void mm_fees_cheq_cheque_bankKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_bankKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_bankKeyReleased

    private void mm_fees_cheq_cheque_branchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_branchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_branchActionPerformed

    private void mm_fees_cheq_cheque_branchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_branchKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_branchKeyReleased

    private void mm_fees_cheq_cheque_amountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_amountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_amountActionPerformed

    private void mm_fees_cheq_cheque_amountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_amountKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_amountKeyReleased

    private void fm_fees_cheq_full_fees_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_full_fees_TextfieldActionPerformed
        mm_fees_cheq_cheque_number.requestFocus();
    }//GEN-LAST:event_fm_fees_cheq_full_fees_TextfieldActionPerformed

    private void fm_fees_cheq_full_fees_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_full_fees_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_full_fees_TextfieldKeyReleased

    private void mm_fees_cheq_cheque_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_statusActionPerformed

    private void mm_fees_cheq_cheque_statusKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_statusKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_statusKeyReleased

    private void mm_fees_cheq_cheque_remainingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_remainingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_remainingActionPerformed

    private void mm_fees_cheq_cheque_remainingKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_cheq_cheque_remainingKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_cheq_cheque_remainingKeyReleased

    private void buttonGradient2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient2ActionPerformed

        try {

            int st_id = Fees_Management.selectedStudentIds;
            int en_id = Fees_Management.selectedEnrollmentId;

            // Check if user entered amount
            if (mm_fees_Monthly_total_paid_Textfield.getText().equalsIgnoreCase("")) {
                JOptionPane.showMessageDialog(null, "Paying amount cannot be empty or 0", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();

            saveMonthlyFullPayment(
                    selectedEnrollmentId,
                    mm_fees_monthly_table,
                    mm_fees_Monthly_total_fee_Textfield,
                    mm_fees_Monthly_payment_method_combo.getSelectedItem().toString()
            );

            Fees_Management.updateMasterTableRows(st_id);
            Fees_Management.loadMonthlyTable(en_id, mm_fees_monthly_table);

            updateMonthlySummaryFields(
                    selectedEnrollmentId,
                    MonthlyFeePanel.mm_fees_monthly_table,
                    MonthlyFeePanel.mm_fees_Monthly_tot_paid_months_Textfield,
                    MonthlyFeePanel.mm_fees_Monthly_tot_paid_amount_Textfield,
                    MonthlyFeePanel.mm_fees_Monthly_tot_pending_months_Textfield
            );

            mm_fees_Monthly_fee_note_Textarea.setText("");

//            distributeMonthlyPayment(
//                    mm_fees_monthly_table,
//                    mm_fees_Monthly_total_paid_Textfield,
//                    mm_fees_Monthly_total_fee_Textfield
//            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_buttonGradient2ActionPerformed

    private void buttonGradient3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient3ActionPerformed

        DefaultTableModel instModel = (DefaultTableModel) mm_fees_monthly_table.getModel();

        int st_id = Fees_Management.selectedStudentIds;
        int en_id = Fees_Management.selectedEnrollmentId;

        // Check mandatory fields
//        if (fm_fees_cheq_full_fees_Textfield.getText().equals("")
//                || mm_fees_cheq_cheque_number.getText().equals("")
//                || mm_fees_cheq_cheque_bank.getEditor().getItem().toString().equals("")
//                || mm_fees_cheq_cheque_amount.getText().equalsIgnoreCase("")
//                || mm_fees_cheq_cheque_amount.getText().equalsIgnoreCase("0")) {
//            JOptionPane.showMessageDialog(null, "Fields cannot be empty or 0", "Not Found", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
        if (mm_fees_cheq_cheque_date.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Select cheque date", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int amount_paid = GeneralMethods.parseCommaNumber(mm_fees_cheq_cheque_amount.getText());

        saveMonthlyChequePayment(en_id, mm_fees_monthly_table, fm_fees_cheq_full_fees_Textfield);

        Fees_Management.updateMasterTableRows(st_id);
        Fees_Management.loadMonthlyTable(en_id, mm_fees_monthly_table);

        updateMonthlySummaryFields(
                selectedEnrollmentId,
                MonthlyFeePanel.mm_fees_monthly_table,
                MonthlyFeePanel.mm_fees_Monthly_tot_paid_months_Textfield,
                MonthlyFeePanel.mm_fees_Monthly_tot_paid_amount_Textfield,
                MonthlyFeePanel.mm_fees_Monthly_tot_pending_months_Textfield
        );

        mm_fees_Monthly_fee_note_Textarea.setText("");

//        saveMonthlyChequePayment(en_id, mm_fees_monthly_table, mm_fees_cheq_cheque_number.getText(), mm_fees_cheq_cheque_bank.getEditor().getItem().toString(),
//                mm_fees_cheq_cheque_branch.getText(), mm_fees_cheq_cheque_date.getDate(), amount_paid);

    }//GEN-LAST:event_buttonGradient3ActionPerformed

    private void mm_fees_Monthly_tot_paid_amount_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_paid_amount_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_paid_amount_TextfieldKeyReleased

    private void mm_fees_Monthly_tot_paid_amount_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_paid_amount_TextfieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_paid_amount_TextfieldKeyPressed

    private void mm_fees_Monthly_tot_paid_amount_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_paid_amount_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_paid_amount_TextfieldActionPerformed

    private void mm_fees_Monthly_tot_paid_months_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_paid_months_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_paid_months_TextfieldKeyReleased

    private void mm_fees_Monthly_tot_paid_months_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_paid_months_TextfieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_paid_months_TextfieldKeyPressed

    private void mm_fees_Monthly_tot_paid_months_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_paid_months_TextfieldActionPerformed

    }//GEN-LAST:event_mm_fees_Monthly_tot_paid_months_TextfieldActionPerformed

    private void mm_fees_Monthly_fee_cal_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_fee_cal_TextfieldActionPerformed

        try {

            if (mm_fees_Monthly_total_fee_Textfield.getText().equals("") || mm_fees_Monthly_fee_cal_Textfield.getText().equals("")) {
                return;
            }

            int month_fee = GeneralMethods.parseCommaNumber(mm_fees_Monthly_total_fee_Textfield.getText());
            int month_fee_cal = GeneralMethods.parseCommaNumber(mm_fees_Monthly_fee_cal_Textfield.getText());

            mm_fees_Monthly_total_paid_Textfield.setText(GeneralMethods.formatWithComma(month_fee * month_fee_cal));

            mm_fees_Monthly_fee_cal_Textfield.requestFocus();
            mm_fees_Monthly_fee_cal_Textfield.selectAll();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_mm_fees_Monthly_fee_cal_TextfieldActionPerformed

    private void mm_fees_Monthly_fee_cal_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_fee_cal_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_fee_cal_TextfieldKeyReleased

    private void mm_fees_Monthly_tot_cheque_pending_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_cheque_pending_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_cheque_pending_TextfieldActionPerformed

    private void mm_fees_Monthly_tot_cheque_pending_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_cheque_pending_TextfieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_cheque_pending_TextfieldKeyPressed

    private void mm_fees_Monthly_tot_cheque_pending_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_cheque_pending_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_cheque_pending_TextfieldKeyReleased

    private void mm_fees_Monthly_tot_totPaid_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_totPaid_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_totPaid_TextfieldActionPerformed

    private void mm_fees_Monthly_tot_totPaid_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_totPaid_TextfieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_totPaid_TextfieldKeyPressed

    private void mm_fees_Monthly_tot_totPaid_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_totPaid_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_totPaid_TextfieldKeyReleased

    private void mm_fees_Monthly_tot_pending_months_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_pending_months_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_pending_months_TextfieldActionPerformed

    private void mm_fees_Monthly_tot_pending_months_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_pending_months_TextfieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_pending_months_TextfieldKeyPressed

    private void mm_fees_Monthly_tot_pending_months_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_pending_months_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_pending_months_TextfieldKeyReleased

    private void mm_fees_Monthly_tot_pending_balancee_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_pending_balancee_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_pending_balancee_TextfieldActionPerformed

    private void mm_fees_Monthly_tot_pending_balancee_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_pending_balancee_TextfieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_pending_balancee_TextfieldKeyPressed

    private void mm_fees_Monthly_tot_pending_balancee_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mm_fees_Monthly_tot_pending_balancee_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_mm_fees_Monthly_tot_pending_balancee_TextfieldKeyReleased

    private void fm_fees_cheq_full_fees_cal_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_full_fees_cal_TextfieldActionPerformed
        try {

            if (fm_fees_cheq_full_fees_Textfield.getText().equals("") || fm_fees_cheq_full_fees_cal_Textfield.getText().equals("")) {
                return;
            }

            int month_fee = GeneralMethods.parseCommaNumber(fm_fees_cheq_full_fees_Textfield.getText());
            int month_fee_cal = GeneralMethods.parseCommaNumber(fm_fees_cheq_full_fees_cal_Textfield.getText());

            mm_fees_cheq_cheque_amount.setText(GeneralMethods.formatWithComma(month_fee * month_fee_cal));

            mm_fees_cheq_cheque_number.requestFocus();
            mm_fees_cheq_cheque_number.selectAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_fm_fees_cheq_full_fees_cal_TextfieldActionPerformed

    private void fm_fees_cheq_full_fees_cal_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_full_fees_cal_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_full_fees_cal_TextfieldKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradient buttonGradient2;
    private Classes.ButtonGradient buttonGradient3;
    private javax.swing.JLabel firstName_label10;
    private javax.swing.JLabel firstName_label13;
    private javax.swing.JLabel firstName_label14;
    private javax.swing.JLabel firstName_label15;
    private javax.swing.JLabel firstName_label16;
    private javax.swing.JLabel firstName_label7;
    private javax.swing.JLabel firstName_label8;
    private javax.swing.JLabel firstName_label9;
    public static javax.swing.JTextField fm_fees_cheq_full_fees_Textfield;
    public static javax.swing.JTextField fm_fees_cheq_full_fees_cal_Textfield;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTextField mm_fees_Monthly_fee_cal_Textfield;
    public static javax.swing.JEditorPane mm_fees_Monthly_fee_note_Textarea;
    public static com.toedter.calendar.JDateChooser mm_fees_Monthly_payment_date;
    public static javax.swing.JComboBox<String> mm_fees_Monthly_payment_method_combo;
    public static javax.swing.JTextField mm_fees_Monthly_tot_cheque_pending_Textfield;
    public static javax.swing.JTextField mm_fees_Monthly_tot_paid_amount_Textfield;
    public static javax.swing.JTextField mm_fees_Monthly_tot_paid_months_Textfield;
    public static javax.swing.JTextField mm_fees_Monthly_tot_pending_balancee_Textfield;
    public static javax.swing.JTextField mm_fees_Monthly_tot_pending_months_Textfield;
    public static javax.swing.JTextField mm_fees_Monthly_tot_totPaid_Textfield;
    public static javax.swing.JTextField mm_fees_Monthly_total_balance_Textfield;
    public static javax.swing.JTextField mm_fees_Monthly_total_fee_Textfield;
    public static javax.swing.JTextField mm_fees_Monthly_total_paid_Textfield;
    public static javax.swing.JTextField mm_fees_cheq_cheque_amount;
    public static javax.swing.JComboBox<String> mm_fees_cheq_cheque_bank;
    public static javax.swing.JTextField mm_fees_cheq_cheque_branch;
    public static com.toedter.calendar.JDateChooser mm_fees_cheq_cheque_date;
    public static javax.swing.JTextField mm_fees_cheq_cheque_number;
    public static javax.swing.JTextField mm_fees_cheq_cheque_remaining;
    public static javax.swing.JComboBox<String> mm_fees_cheq_cheque_status;
    public static javax.swing.JTable mm_fees_monthly_table;
    public static javax.swing.JTabbedPane monthly_jTabbedPane2;
    private javax.swing.JLabel sup_payment_cash_label;
    private javax.swing.JLabel sup_payment_cheque_label;
    private javax.swing.JLabel sup_payment_cheque_label1;
    // End of variables declaration//GEN-END:variables
private int showRoundPaymentDialog(int studentId, int enrollmentId, int overpaidAmount) {
        Window parent = SwingUtilities.getWindowAncestor(this);

        ModernDialog dialog = new ModernDialog((Frame) parent, 480, 300);
        JPanel panel = dialog.getContentPanel();

        panel.setLayout(null);
        panel.setBackground(new Color(45, 45, 50));

        // ===== TITLE =====
        JLabel title = new JLabel("Overpayment Detected");
        title.setBounds(30, 20, 420, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        panel.add(title);

        // ===== MESSAGE =====
        JLabel message = new JLabel("<html>Payment exceeds the balance by: <b>" + overpaidAmount + "</b><br>"
                + "Do you want to apply excess to other courses?</html>");
        message.setBounds(30, 60, 420, 50);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setForeground(Color.WHITE);
        panel.add(message);

        // ===== BUTTONS =====
        GradientButton roundBtn = new GradientButton(
                "Round Payment",
                new Color(0, 153, 102),
                new Color(0, 204, 153)
        );
        roundBtn.setBounds(30, 150, 140, 42);

        GradientButton singleBtn = new GradientButton(
                "Pay This Course Only",
                new Color(0, 102, 204),
                new Color(0, 180, 255)
        );
        singleBtn.setBounds(190, 150, 180, 42);

        GradientButton cancelBtn = new GradientButton(
                "Cancel",
                Color.decode("#F09819"),
                Color.decode("#FF512F")
        );
        cancelBtn.setBounds(380, 150, 80, 42);

        panel.add(roundBtn);
        panel.add(singleBtn);
        panel.add(cancelBtn);

        final int[] choice = {-1}; // -1 = cancel, 1 = round, 2 = single

        roundBtn.addActionListener(e -> {
            choice[0] = 1;
            dialog.dispose();
        });

        singleBtn.addActionListener(e -> {
            choice[0] = 2;
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> {
            choice[0] = -1;
            dialog.dispose();
        });

        // ===== SUM BALANCES IF MULTIPLE COURSES =====
        int courseCount = Fees_Management.fm_fees_course_table.getRowCount();
        if (courseCount > 1) {
            int totalBalance = 0;
            for (int i = 0; i < courseCount; i++) {
                totalBalance += GeneralMethods.parseCommaNumber(
                        Fees_Management.fm_fees_course_table.getValueAt(i, 9).toString()
                );
            }
            message.setText("<html>Payment exceeds the balance by: <b>" + overpaidAmount + "</b><br>"
                    + "Total balance across courses: <b>" + totalBalance + "</b><br>"
                    + "Do you want to apply excess to other courses?</html>");
        }

        dialog.setVisible(true);

        return choice[0];
    }
}
