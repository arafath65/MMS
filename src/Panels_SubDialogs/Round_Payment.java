package Panels_SubDialogs;

import Classes.ChequeNumberFormatter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NumberOnlyFilter;
import Classes.TableCheckboxHandler;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Student_Management.StudentFeeRoundPaymentMaster;
import Entities.Student_Management.StudentFeeRoundPaymentMasterDetails;
import JPA_DAO.Settings.CourseDAO;
import JPA_DAO.Student_Management.StudentAdditionalFeesDAO;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
import JPA_DAO.Student_Management.StudentFeeRoundPaymentDAO;
import Panels.Fees_Management;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;

public class Round_Payment extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Round_Payment.class.getName());
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    private TableCheckboxHandler tableCheckboxHandler;
    LogHelper logHelper = new LogHelper();

    private Fees_Management parentForm;

    CourseDAO dao = new CourseDAO();

    int feeID = 0;
    private int selectedStudentIds;
    String studentName;
    String username;
    String role;

    public Round_Payment(Window parent, int selectedStudentIds, String studentName, String username, String role) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.parentForm = parentForm;
        this.selectedStudentIds = selectedStudentIds;
        this.studentName = studentName;
        this.username = username;
        this.role = role;
        initComponents();

        rp_date.setDate(new Date());
        styleDateChooser.applyDarkTheme(rp_date);
        styleDateChooser.applyDarkTheme(rp_round_cheque_date);
        rp_student_name_text.setText(this.studentName);

        //JComboPopulatesBankInfo();
        rp_due_table.setDefaultRenderer(Object.class, new TableGradientCell());
        rp_due_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        loadCourseDuesToTable(this.selectedStudentIds);
        calculateTotal();

        tableCheckboxHandler = new TableCheckboxHandler(rp_due_table, rp_round_total_pay_cash_text, rp_round_cheque_amount, rp_round_remaining_bal_text);

//        reg_misc_amount_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        reg_misc_amount_text.putClientProperty("JComponent.focusWidth", 2);
//
//        reg_misc_qty_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        reg_misc_qty_text.putClientProperty("JComponent.focusWidth", 2);
//
//        reg_misc_discount_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        reg_misc_discount_text.putClientProperty("JComponent.focusWidth", 2);
        JComboPopulatesBankInfo();
    }

    private void JComboPopulatesBankInfo() {
        // Medicine brand combo
        rp_round_bank_name_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = rp_round_bank_name_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(rp_round_bank_name_combo, "bank_names", "bank_names_srilanka", input);
            }

        });
        setupComboSelectionListener(rp_round_bank_name_combo, rp_round_cheque_amount);

        new ChequeNumberFormatter(rp_round_cheque_number_text, rp_round_bank_name_combo, rp_round_cheque_branch);
        PlainDocument doc = (PlainDocument) rp_round_cheque_number_text.getDocument();
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

    public void loadCourseDuesToTable(int studentId) {

        DefaultTableModel model = (DefaultTableModel) rp_due_table.getModel();
        model.setRowCount(0);

        int count = 1;

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            // =====================================================
            // 1. COURSE LOGIC
            // =====================================================
            List<Object[]> courseList = em.createNativeQuery(
                    "SELECT student_fee_payments_id, enrollment_id, total_fee, total_paid, total_balance, course_type, created_at "
                    + "FROM student_fee_payments "
                    + "WHERE student_id=? AND status=1"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            for (Object[] courseRow : courseList) {

                int enrollmentId = Integer.parseInt(courseRow[1].toString());

                double totalFee = courseRow[2] != null ? Double.parseDouble(courseRow[2].toString()) : 0;
                double totalPaid = courseRow[3] != null ? Double.parseDouble(courseRow[3].toString()) : 0;
                double balance = courseRow[4] != null ? Double.parseDouble(courseRow[4].toString()) : 0;

                String courseType = courseRow[5] != null ? courseRow[5].toString() : "";
                String date = courseRow[6] != null ? courseRow[6].toString().split(" ")[0] : "";

                // ✅ CHEQUE (PENDING ONLY)
                double chequePendingCourse = ((Number) em.createNativeQuery(
                        "SELECT COALESCE(SUM(d.paid_amount),0) "
                        + "FROM student_fee_round_payment_master_details d "
                        + "JOIN student_fee_cheque_details c "
                        + "  ON c.reference_id = d.student_fee_round_payment_master_id "
                        + "  AND c.reference_type='ROUND' "
                        + "  AND c.category='STUDENT' "
                        + "  AND c.status=1 "
                        + "  AND c.cheque_status = 'PENDING' " // ✅ STRICT FILTER HERE
                        + "WHERE d.reference_type='COURSE' "
                        + "AND d.enrollment_id=? "
                        + "AND d.status=1"
                )
                        .setParameter(1, enrollmentId)
                        .getSingleResult()).doubleValue();
//                double chequePendingCourse = ((Number) em.createNativeQuery(
//                        "SELECT COALESCE(SUM(d.paid_amount),0) "
//                        + "FROM student_fee_round_payment_master_details d "
//                        + "JOIN student_fee_cheque_details c "
//                        + "ON c.reference_id = d.student_fee_round_payment_master_id "
//                        + "AND c.reference_type='ROUND' "
//                        + "AND c.category='STUDENT' "
//                        + "WHERE d.reference_type='COURSE' "
//                        + "AND d.enrollment_id=? "
//                        + "AND d.status=1 "
//                        + "AND c.cheque_status='PENDING' "
//                        + "AND c.status=1"
//                )
//                        .setParameter(1, enrollmentId)
//                        .getSingleResult()).doubleValue();

                double finalDueCourse = Math.max(balance - chequePendingCourse, 0);

                // ✅ QTY (MONTHLY)
                int qty = 1;
                if ("MONTHLY".equalsIgnoreCase(courseType)) {
                    qty = getPendingMonthCount(enrollmentId);
                    if (qty <= 0) {
                        qty = 1;
                    }
                }

                //    System.out.println("COURSE ROW: " + enrollmentId);
                model.addRow(new Object[]{
                    count++,
                    "COURSE",
                    date,
                    "Course (" + courseType + ")",
                    qty,
                    GeneralMethods.formatWithComma(totalFee),
                    GeneralMethods.formatWithComma(totalPaid),
                    GeneralMethods.formatWithComma(chequePendingCourse),
                    GeneralMethods.formatWithComma(finalDueCourse),
                    "",
                    false,
                    "COURSE_" + enrollmentId
                });
            }

            // =====================================================
            // 2. ADDITIONAL + INVENTORY (FINAL FIXED CHEQUE LOGIC)
            // =====================================================
            List<Object[]> issuedList = em.createNativeQuery(
                    "SELECT fee_type_id, MIN(student_additional_fees_id), SUM(amount), MIN(issued_date) "
                    + "FROM student_additional_fees "
                    + "WHERE student_id=? AND status=1 "
                    + "GROUP BY fee_type_id"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            for (Object[] addRow : issuedList) {

                int feeTypeId = Integer.parseInt(addRow[0].toString());
                int additionalFeeId = Integer.parseInt(addRow[1].toString());
                double totalAmount = Double.parseDouble(addRow[2].toString());

                String issuedDate = addRow[3] != null ? addRow[3].toString().split(" ")[0] : "";

//                System.out.println("\n=============================");
//                System.out.println("ADD ID: " + additionalFeeId);
//                System.out.println("FEE TYPE: " + feeTypeId);
//                System.out.println("TOTAL AMOUNT: " + totalAmount);
                // =====================================================
                // CASH / CARD ONLY PAID
                // =====================================================
                Double totalPaidAdd = (Double) em.createNativeQuery(
                        "SELECT COALESCE(SUM(p.amount_paid),0) "
                        + "FROM student_additional_fee_payments p "
                        + "JOIN student_additional_fees saf "
                        + "ON p.student_additional_fees_id = saf.student_additional_fees_id "
                        + "WHERE saf.fee_type_id=? AND saf.student_id=? "
                        + "AND p.status=1 "
                        + "AND p.payment_method <> 'CHEQUE'"
                )
                        .setParameter(1, feeTypeId)
                        .setParameter(2, studentId)
                        .getSingleResult();

                if (totalPaidAdd == null) {
                    totalPaidAdd = 0.0;
                }

                //    System.out.println("TOTAL PAID (NON-CHEQUE): " + totalPaidAdd);
                // =====================================================
                // 🔥 CHEQUE LOGIC (FINAL FIX)
                // =====================================================
                double chequePendingAdd = 0;

                // STEP 1: GET ROUND MASTER IDS (CHEQUE ONLY)
                List<Integer> masterIds = em.createNativeQuery(
                        "SELECT student_fee_round_payment_master_id "
                        + "FROM student_fee_round_payment_master "
                        + "WHERE student_id=? AND payment_mode='CHEQUE' AND status=1"
                )
                        .setParameter(1, studentId)
                        .getResultList();

                //    System.out.println("ROUND MASTER IDS (CHEQUE): " + masterIds);
                // STEP 2: FOR EACH MASTER → CHECK ADDITIONAL PAYMENTS
                for (Integer masterId : masterIds) {

                    Object result = em.createNativeQuery(
                            "SELECT COALESCE(SUM(d.paid_amount),0) "
                            + "FROM student_fee_round_payment_master_details d "
                            + "JOIN student_fee_cheque_details c "
                            + "  ON c.reference_id = d.student_fee_round_payment_master_id "
                            + "  AND c.reference_type='ROUND' "
                            + "  AND c.category='STUDENT' "
                            + "  AND c.status=1 "
                            + "  AND c.cheque_status = 'PENDING' " // ✅ ONLY PENDING
                            + "WHERE d.student_fee_round_payment_master_id=? "
                            + "AND d.reference_type='ADDITIONAL' "
                            + "AND d.reference_id=? "
                            + "AND d.status=1"
                    )
                            .setParameter(1, masterId)
                            .setParameter(2, additionalFeeId)
                            .getSingleResult();
//                    Object result = em.createNativeQuery(
//                            "SELECT COALESCE(SUM(d.paid_amount),0) "
//                            + "FROM student_fee_round_payment_master_details d "
//                            + "JOIN student_fee_cheque_details c "
//                            + "ON c.reference_id = d.student_fee_round_payment_master_id "
//                            + "AND c.reference_type='ROUND' "
//                            + "AND c.category='STUDENT' "
//                            + "WHERE d.student_fee_round_payment_master_id=? "
//                            + "AND d.reference_type='ADDITIONAL' "
//                            + "AND d.reference_id=? "
//                            + "AND c.cheque_status='PENDING' "
//                            + "AND d.status=1 "
//                            + "AND c.status=1"
//                    )
//                            .setParameter(1, masterId)
//                            .setParameter(2, additionalFeeId)
//                            .getSingleResult();

                    double paidAmount = ((Number) result).doubleValue();

//                    System.out.println("MASTER: " + masterId
//                            + " | ADD_ID: " + additionalFeeId
//                            + " | CHEQUE_PAID: " + paidAmount);
                    chequePendingAdd += paidAmount;
                }

                //    System.out.println("TOTAL CHEQUE (ADDITIONAL): " + chequePendingAdd);
                // =====================================================
                // FINAL CALCULATION
                // =====================================================
                double balanceAdd = totalAmount - totalPaidAdd;
                double finalDueAdd = Math.max(balanceAdd - chequePendingAdd, 0);

//                System.out.println("BALANCE: " + balanceAdd);
//                System.out.println("FINAL CHEQUE: " + chequePendingAdd);
//                System.out.println("FINAL DUE: " + finalDueAdd);
                if (balanceAdd <= 0) {
                    continue;
                }

                Object[] feeData = (Object[]) em.createNativeQuery(
                        "SELECT fee_name, item_id FROM fee_types WHERE fee_type_id=?"
                )
                        .setParameter(1, feeTypeId)
                        .getSingleResult();

                String feeName = feeData[0].toString();
                int itemId = feeData[1] != null ? Integer.parseInt(feeData[1].toString()) : 0;

                String category = (itemId == 0) ? "SERVICE" : "INVENTORY";

                model.addRow(new Object[]{
                    count++,
                    category,
                    issuedDate,
                    feeName,
                    1,
                    GeneralMethods.formatWithComma(totalAmount),
                    GeneralMethods.formatWithComma(totalPaidAdd),
                    GeneralMethods.formatWithComma(chequePendingAdd),
                    GeneralMethods.formatWithComma(finalDueAdd),
                    "",
                    false,
                    "ADD_" + additionalFeeId
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public double getTotalPendingRoundCheque(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            Object result = em.createNativeQuery(
                    "SELECT COALESCE(SUM(c.cheque_amount),0) "
                    + "FROM student_fee_cheque_details c "
                    + "WHERE c.reference_type='ROUND' "
                    + "AND c.category='STUDENT' "
                    + "AND c.cheque_status='PENDING' "
                    + "AND c.status=1 "
                    + "AND c.reference_id IN ( "
                    + "   SELECT m.student_fee_round_payment_master_id "
                    + "   FROM student_fee_round_payment_master m "
                    + "   WHERE m.student_id=? AND m.status=1 "
                    + ")"
            )
                    .setParameter(1, studentId)
                    .getSingleResult();

            return ((Number) result).doubleValue();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public int getPendingMonthCount(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
            StudentFeeInstallmentsDAO.MonthDataDTO data = dao.getMonthData(enrollmentId);

            int pendingCount = 0;

            int y = data.startYear;
            int m = data.startMonth;

            while (true) {

                String monthStr = String.format("%02d", m);
                String full = y + "-" + monthStr;

                // =====================================================
                // 🔥 CHECK MONTH STATUS
                // =====================================================
                Object[] result = (Object[]) em.createNativeQuery(
                        "SELECT "
                        + "COALESCE(SUM(amount_paid),0), "
                        + "MAX(payment_type) "
                        + "FROM student_fee_installments "
                        + "WHERE enrollment_id=? "
                        + "AND month_for=? "
                        + "AND status=1"
                )
                        .setParameter(1, enrollmentId)
                        .setParameter(2, full)
                        .getSingleResult();

                double paid = ((Number) result[0]).doubleValue();
                String type = result[1] != null ? result[1].toString() : "";

                // =====================================================
                // 🔥 PENDING LOGIC (FINAL RULE)
                // =====================================================
                if (paid == 0) {

                    // ❌ ZERO → NOT pending
                    if (!"ZERO".equalsIgnoreCase(type)) {
                        pendingCount++;
                    }

                } else {

                    // ❌ DISCOUNT → NOT pending
                    if (!"DISCOUNT".equalsIgnoreCase(type)) {

                        // partial payment → pending
                        if (data.monthAmountMap.containsKey(full)) {
                            double monthlyFee = data.monthAmountMap.get(full);

                            if (paid < monthlyFee) {
                                pendingCount++;
                            }
                        } else {
                            pendingCount++;
                        }
                    }
                }

                // =====================================================
                // STOP CONDITION
                // =====================================================
                if (y == data.endYear && m == data.endMonth) {
                    break;
                }

                m++;
                if (m > 12) {
                    m = 1;
                    y++;
                }
            }

            return pendingCount;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

//    public double getTotalPendingRoundCheque(int studentId) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            Object result = em.createNativeQuery(
//                    "SELECT COALESCE(SUM(cheque_amount),0) "
//                    + "FROM student_fee_cheque_details "
//                    + "WHERE reference_type='ROUND' "
//                    + "AND cheque_status='PENDING' "
//                    + "AND status=1 "
//                    + "AND reference_id IN ("
//                    + "   SELECT student_fee_round_payment_master_id "
//                    + "   FROM student_fee_round_payment_master "
//                    + "   WHERE student_id=?"
//                    + ")"
//            )
//                    .setParameter(1, studentId)
//                    .getSingleResult();
//
//            return ((Number) result).doubleValue();
//
//        } finally {
//            em.close();
//        }
//    }
//    public int getPendingMonthCount(int enrollmentId) {
//
//        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
//        StudentFeeInstallmentsDAO.MonthDataDTO data = dao.getMonthData(enrollmentId);
//
//        int pendingCount = 0;
//
//        int y = data.startYear;
//        int m = data.startMonth;
//
//        while (true) {
//
//            String monthStr = String.format("%02d", m);
//            String full = y + "-" + monthStr;
//
//            // ❌ NOT PAID → NOT IN MAP
//            if (!data.monthAmountMap.containsKey(full)) {
//                pendingCount++;
//            }
//
//            // stop condition
//            if (y == data.endYear && m == data.endMonth) {
//                break;
//            }
//
//            m++;
//            if (m > 12) {
//                m = 1;
//                y++;
//            }
//        }
//
//        return pendingCount;
//    }
    private void calculateTotal() {

        DefaultTableModel model = (DefaultTableModel) rp_due_table.getModel();

        double total = 0.0;
        double total_paid = 0.0;
        double tot_chq = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object value = GeneralMethods.parseCommaNumber(model.getValueAt(i, 8).toString());
            Object value_paid = GeneralMethods.parseCommaNumber(model.getValueAt(i, 6).toString());
            Object value_chq = GeneralMethods.parseCommaNumber(model.getValueAt(i, 7).toString());

            if (value != null && !value.toString().isEmpty()) {
                total += Double.parseDouble(String.valueOf(value));
            }

            if (value_chq != null && !value_chq.toString().isEmpty()) {
                tot_chq += Double.parseDouble(String.valueOf(value_chq));
            }

            if (value_paid != null && !value_paid.toString().isEmpty()) {
                total_paid += Double.parseDouble(String.valueOf(value_paid));
            }
        }

        rp_total_due_text.setText(GeneralMethods.formatWithComma(total));
        rp_round_total_paid_text.setText(GeneralMethods.formatWithComma(total_paid));
        rp_round_total_pending_cheque_text.setText(GeneralMethods.formatWithComma(tot_chq));
        rp_round_remaining_bal_text.setText(GeneralMethods.formatWithComma(total - tot_chq));
    }

    public void calculateRoundDistribution() {

        double enteredAmount = GeneralMethods.parseCommaNumber(rp_round_calculate_text.getText());
        double totalDue = GeneralMethods.parseCommaNumber(rp_total_due_text.getText());

        if (enteredAmount <= 0) {
            JOptionPane.showMessageDialog(null, "Enter valid amount");
            return;
        }

        if (enteredAmount > totalDue) {
            JOptionPane.showMessageDialog(null, "Amount exceeds total due!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) rp_due_table.getModel();
        int rowCount = model.getRowCount();

        List<Object[]> list = new ArrayList<>();

        // =========================
        // READ ALL DUES
        // =========================
        for (int i = 0; i < rowCount; i++) {

            Object val = model.getValueAt(i, 8);

            double due = 0;

            if (val != null && !val.toString().trim().isEmpty()) {
                due = GeneralMethods.parseCommaNumber(val.toString());
            }

            list.add(new Object[]{i, due});
        }

        // =========================
        // SORT (LOW → HIGH)
        // =========================
        list.sort((a, b) -> Double.compare((double) a[1], (double) b[1]));

        double remaining = enteredAmount;

        // 🔥 IMPORTANT: disable handler logic
        tableCheckboxHandler.isProgrammaticUpdate = true;
        // calculateTotal();

        // =========================
        // CLEAR OLD VALUES
        // =========================
        for (int i = 0; i < rowCount; i++) {
            model.setValueAt("", i, 9);
            model.setValueAt(false, i, 10);
        }

        // =========================
        // DISTRIBUTION
        // =========================
        for (Object[] r : list) {

            int row = (int) r[0];
            double due = (double) r[1];

            if (remaining <= 0) {
                break;
            }

            double payAmount = Math.min(due, remaining);

            model.setValueAt(GeneralMethods.formatWithComma(payAmount), row, 9);
            model.setValueAt(true, row, 10);

            remaining -= payAmount;
        }

        // 🔥 enable back
        tableCheckboxHandler.isProgrammaticUpdate = false;
        SwingUtilities.invokeLater(() -> {
            tableCheckboxHandler.forceUpdateTotal();
        });
    }

    public void saveRoundPayment(int studentId, JTable table,
            String paymentMode, double totalPaid, double roundingAdj, String user) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        int COL_SERVICENAME = 3;
        int COL_PAYABLE = 9;
        int COL_IDS = 11;

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            // =========================
            // 1. MASTER
            // =========================
            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
            master.setStudentId(studentId);
            master.setPaymentDate(new java.util.Date());
            master.setPaymentMode("ROUND");
            master.setTotalPaid(totalPaid);
            master.setRoundingAdjustment(roundingAdj);
            master.setUser(user);
            master.setStatus(1);

            em.persist(master);
            em.flush();

            System.out.println("MASTER ID: " + master.getStudentFeeRoundPaymentMasterId());
            Integer roundMasterId = master.getStudentFeeRoundPaymentMasterId();

            System.out.println("ROUND MASTER ID: " + roundMasterId);

//            // =========================
//            // CHEQUE MASTER SAVE (ONLY ONCE)
//            // =========================
//            if ("CHEQUE".equalsIgnoreCase(paymentMode)) {
//
//                em.createNativeQuery(
//                        "INSERT INTO student_fee_cheque_details "
//                        + "(reference_id, reference_type, category, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
//                        + "VALUES (?, 'ROUND', ?, ?, ?, ?, ?, ?, 'PENDING', 1)"
//                )
//                        .setParameter(1, roundMasterId)
//                        .setParameter(2, "STUDENT")
//                        .setParameter(3, rp_round_cheque_number_text.getText())
//                        .setParameter(4, rp_round_bank_name_combo.getEditor().getItem().toString())
//                        .setParameter(5, rp_round_cheque_branch.getText())
//                        .setParameter(6, rp_round_cheque_date.getDate())
//                        .setParameter(7, totalPaid)
//                        .executeUpdate();
//
//                System.out.println("CHEQUE SAVED FOR ROUND MASTER ID: " + roundMasterId);
//            }
            System.out.println("CHEQUE SAVED FOR ROUND MASTER ID: " + roundMasterId);

            // =========================
            // 2. LOOP TABLE
            // =========================
            for (int i = 0; i < model.getRowCount(); i++) {

                Object payableObj = model.getValueAt(i, COL_PAYABLE);
                Object refObj = model.getValueAt(i, COL_IDS);

                if (payableObj == null || payableObj.toString().trim().isEmpty()) {
                    continue;
                }
                if (refObj == null) {
                    continue;
                }

                double payableAmount = GeneralMethods.parseCommaNumber(payableObj.toString());
                if (payableAmount <= 0) {
                    continue;
                }

                String ref = refObj.toString();

                // =====================================================
                // COURSE
                // =====================================================
                if (ref.startsWith("COURSE_")) {

                    int enrollmentId = Integer.parseInt(ref.replace("COURSE_", ""));
                    String serviceName = model.getValueAt(i, COL_SERVICENAME).toString();

                    // =====================================================
                    // ONE-TIME COURSE
                    // =====================================================
                    if (serviceName.toUpperCase().contains("ONE-TIME")) {

                        Object[] paymentRow = (Object[]) em.createNativeQuery(
                                "SELECT student_fee_payments_id, total_paid, total_balance "
                                + "FROM student_fee_payments "
                                + "WHERE enrollment_id = ? AND status = 1"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult();

                        int paymentId = Integer.parseInt(paymentRow[0].toString());
                        double currentPaid = Double.parseDouble(paymentRow[1].toString());
                        double currentBalance = Double.parseDouble(paymentRow[2].toString());

                        double newPaid = currentPaid + payableAmount;
                        double newBalance = Math.max(currentBalance - payableAmount, 0);

                        String status = (newBalance == 0) ? "COMPLETE" : "ACTIVE";

                        em.createNativeQuery(
                                "UPDATE student_fee_payments "
                                + "SET total_paid=?, total_balance=?, payment_status=?, last_mofidied=NOW() "
                                + "WHERE student_fee_payments_id=?"
                        )
                                .setParameter(1, newPaid)
                                .setParameter(2, newBalance)
                                .setParameter(3, status)
                                .setParameter(4, paymentId)
                                .executeUpdate();

                        Integer nextInstallmentNo = ((Number) em.createNativeQuery(
                                "SELECT COALESCE(MAX(installment_no),0)+1 "
                                + "FROM student_fee_installments WHERE student_fee_payments_id=?"
                        )
                                .setParameter(1, paymentId)
                                .getSingleResult()).intValue();

                        em.createNativeQuery(
                                "INSERT INTO student_fee_installments "
                                + "(student_fee_payments_id, enrollment_id, student_fee_round_payment_master_id, installment_no, amount_paid, "
                                + "payment_date, payment_method, payment_type, remarks, status) "
                                + "VALUES (?, ?, ?, ?, ?, NOW(), ?, 'ROUND', 'Round Payment', 1)"
                        )
                                .setParameter(1, paymentId)
                                .setParameter(2, enrollmentId)
                                .setParameter(3, roundMasterId)
                                .setParameter(4, nextInstallmentNo)
                                .setParameter(5, payableAmount)
                                .setParameter(6, paymentMode)
                                .executeUpdate();

                    } // =========================
                    // MONTHLY COURSE SUPPORT (IMPORTANT ADDITION)
                    // =========================
                    // =========================
                    // MONTHLY COURSE SUPPORT
                    // =========================
                    else if (serviceName.toUpperCase().contains("MONTHLY")) {

                        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
                        StudentFeeInstallmentsDAO.MonthDataDTO dto = dao.getMonthData(enrollmentId);

                        // =====================================================
                        // 1. GET TOTAL FEE
                        // =====================================================
                        double totalFee = ((Number) em.createNativeQuery(
                                "SELECT total_fee "
                                + "FROM student_fee_payments "
                                + "WHERE enrollment_id=? AND status=1"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult()).doubleValue();

                        // =====================================================
                        // 2. CALCULATE TOTAL MONTHS
                        // =====================================================
                        int totalMonths = 0;
                        int ty = dto.startYear;
                        int tm = dto.startMonth;

                        while (true) {
                            totalMonths++;

                            if (ty == dto.endYear && tm == dto.endMonth) {
                                break;
                            }

                            tm++;
                            if (tm > 12) {
                                tm = 1;
                                ty++;
                            }
                        }

                        double monthlyFee = totalMonths == 0 ? 0 : (totalFee / totalMonths);

                        if (monthlyFee <= 0) {
                            System.out.println("Invalid monthly fee");
                            return;
                        }

                        // =====================================================
                        // 3. PAYMENT ID
                        // =====================================================
                        int paymentId = ((Number) em.createNativeQuery(
                                "SELECT student_fee_payments_id "
                                + "FROM student_fee_payments "
                                + "WHERE enrollment_id=? AND status=1"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult()).intValue();

                        int installmentNo = ((Number) em.createNativeQuery(
                                "SELECT COALESCE(MAX(installment_no),0) "
                                + "FROM student_fee_installments "
                                + "WHERE enrollment_id=?"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult()).intValue();

                        // =====================================================
                        // 4. FETCH VALID MONTHLY PAYMENTS ONLY
                        // Ignore:
                        // - ZERO
                        // - DISCOUNT
                        // - status = 0 (returned cheque rows)
                        // =====================================================
                        Map<String, Double> paidMap = new HashMap<>();

                        List<Object[]> paidRows = em.createNativeQuery(
                                "SELECT month_for, COALESCE(SUM(amount_paid),0) "
                                + "FROM student_fee_installments "
                                + "WHERE enrollment_id=? "
                                + "AND status=1 "
                                + "AND month_for IS NOT NULL "
                                + "AND payment_type NOT IN ('ZERO','DISCOUNT') "
                                + "GROUP BY month_for"
                        )
                                .setParameter(1, enrollmentId)
                                .getResultList();

                        for (Object[] r : paidRows) {
                            String month = r[0].toString();
                            double amt = ((Number) r[1]).doubleValue();
                            paidMap.put(month, amt);
                        }

                        // =====================================================
                        // 5. FIFO PAYMENT ALLOCATION
                        // oldest unpaid first
                        // =====================================================
                        double remainingAmount = payableAmount;

                        int y = dto.startYear;
                        int m = dto.startMonth;

                        while (remainingAmount > 0) {

                            if (y > dto.endYear || (y == dto.endYear && m > dto.endMonth)) {
                                break;
                            }

                            String monthKey = String.format("%04d-%02d", y, m);

                            double alreadyPaid = paidMap.getOrDefault(monthKey, 0.0);
                            double balance = monthlyFee - alreadyPaid;

                            if (balance > 0) {

                                double payNow = Math.min(balance, remainingAmount);

                                installmentNo++;

                                em.createNativeQuery(
                                        "INSERT INTO student_fee_installments "
                                        + "(student_fee_payments_id, enrollment_id, "
                                        + "student_fee_round_payment_master_id, installment_no, "
                                        + "amount_paid, payment_date, payment_method, "
                                        + "payment_type, month_for, remarks, status) "
                                        + "VALUES (?, ?, ?, ?, ?, NOW(), ?, "
                                        + "'ROUND', ?, 'Round Monthly Payment', 1)"
                                )
                                        .setParameter(1, paymentId)
                                        .setParameter(2, enrollmentId)
                                        .setParameter(3, roundMasterId)
                                        .setParameter(4, installmentNo)
                                        .setParameter(5, payNow)
                                        .setParameter(6, paymentMode)
                                        .setParameter(7, monthKey)
                                        .executeUpdate();

                                paidMap.put(monthKey, alreadyPaid + payNow);

                                remainingAmount -= payNow;
                            }

                            m++;
                            if (m > 12) {
                                m = 1;
                                y++;
                            }
                        }

                        // =====================================================
                        // 6. FINAL UPDATE
                        // =====================================================
                        double finalPaid = ((Number) em.createNativeQuery(
                                "SELECT COALESCE(SUM(amount_paid),0) "
                                + "FROM student_fee_installments "
                                + "WHERE enrollment_id=? "
                                + "AND status=1 "
                                + "AND payment_type NOT IN ('ZERO','DISCOUNT')"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult()).doubleValue();

                        double finalBalance = Math.max(totalFee - finalPaid, 0);

                        em.createNativeQuery(
                                "UPDATE student_fee_payments "
                                + "SET total_paid=?, "
                                + "total_balance=?, "
                                + "payment_status=? "
                                + "WHERE enrollment_id=?"
                        )
                                .setParameter(1, finalPaid)
                                .setParameter(2, finalBalance)
                                .setParameter(3, finalBalance == 0 ? "COMPLETED" : "ACTIVE")
                                .setParameter(4, enrollmentId)
                                .executeUpdate();
                    }

                    // =========================
                    // ROUND MASTER DETAILS
                    // =========================
                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
                    d.setStudentFeeRoundPaymentMaster(master);
                    d.setEnrollmentId(enrollmentId);
                    d.setReferenceType("COURSE");
                    d.setPaidAmount(payableAmount);
                    d.setStatus(1);

                    em.persist(d);
                } // =====================================================
                // ADDITIONAL / INVENTORY
                // =====================================================
                else if (ref.startsWith("ADD_")) {

                    int safId = Integer.parseInt(ref.replace("ADD_", ""));

                    em.createNativeQuery(
                            "INSERT INTO student_additional_fee_payments "
                            + "(student_additional_fees_id, student_fee_round_payment_master_id, paid_date, amount_paid, payment_method, user, status) "
                            + "VALUES (?, ?, NOW(), ?, ?, ?, 1)"
                    )
                            .setParameter(1, safId)
                            .setParameter(2, roundMasterId)
                            .setParameter(3, payableAmount)
                            .setParameter(4, paymentMode)
                            .setParameter(5, user)
                            .executeUpdate();

                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
                    d.setStudentFeeRoundPaymentMaster(master);
                    d.setEnrollmentId(null);
                    d.setReferenceId(safId);
                    d.setReferenceType("ADDITIONAL");
                    d.setPaidAmount(payableAmount);
                    d.setStatus(1);

                    em.persist(d);
                }
            }

            // ✅ AUDIT LOG: Bulk Round Payment
            // Grab the name safely
            String studentName = (rp_student_name_text.getText() != null)
                    ? rp_student_name_text.getText() : "";

            String description = String.format(
                    "Round Payment: Student=%s, Mode=%s, Amount=%.2f, Student ID: %d",
                    studentName, paymentMode, totalPaid, studentId
            );

            logHelper.log(
                    "ROUND_PAYMENT",
                    studentId,
                    "PAYMENT", // Usually the 'Action' column looks better as a single verb like 'PAYMENT' or 'COLLECT'
                    paymentMode,
                    totalPaid,
                    description,
                    user
            );

            tx.commit();
            JOptionPane.showMessageDialog(this, "Payment saved successfully.", "Payment Processed", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

//    public void saveRoundPayment(int studentId, JTable table,
//            String paymentMode, double totalPaid, double roundingAdj, String user) {
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//        int COL_SERVICENAME = 3;
//        int COL_PAYABLE = 9;
//        int COL_IDS = 11;
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//
//            tx.begin();
//
//            // =========================
//            // 1. MASTER
//            // =========================
//            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
//            master.setStudentId(studentId);
//            master.setPaymentDate(new java.util.Date());
//            master.setPaymentMode("ROUND");
//            master.setTotalPaid(totalPaid);
//            master.setRoundingAdjustment(roundingAdj);
//            master.setUser(user);
//            master.setStatus(1);
//
//            em.persist(master);
//            em.flush();
//
//            System.out.println("MASTER ID: " + master.getStudentFeeRoundPaymentMasterId());
//            Integer roundMasterId = master.getStudentFeeRoundPaymentMasterId();
//
//            System.out.println("ROUND MASTER ID: " + roundMasterId);
//
    ////            // =========================
////            // CHEQUE MASTER SAVE (ONLY ONCE)
////            // =========================
////            if ("CHEQUE".equalsIgnoreCase(paymentMode)) {
////
////                em.createNativeQuery(
////                        "INSERT INTO student_fee_cheque_details "
////                        + "(reference_id, reference_type, category, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
////                        + "VALUES (?, 'ROUND', ?, ?, ?, ?, ?, ?, 'PENDING', 1)"
////                )
////                        .setParameter(1, roundMasterId)
////                        .setParameter(2, "STUDENT")
////                        .setParameter(3, rp_round_cheque_number_text.getText())
////                        .setParameter(4, rp_round_bank_name_combo.getEditor().getItem().toString())
////                        .setParameter(5, rp_round_cheque_branch.getText())
////                        .setParameter(6, rp_round_cheque_date.getDate())
////                        .setParameter(7, totalPaid)
////                        .executeUpdate();
////
////                System.out.println("CHEQUE SAVED FOR ROUND MASTER ID: " + roundMasterId);
////            }
//            System.out.println("CHEQUE SAVED FOR ROUND MASTER ID: " + roundMasterId);
//
//            // =========================
//            // 2. LOOP TABLE
//            // =========================
//            for (int i = 0; i < model.getRowCount(); i++) {
//
//                Object payableObj = model.getValueAt(i, COL_PAYABLE);
//                Object refObj = model.getValueAt(i, COL_IDS);
//
//                if (payableObj == null || payableObj.toString().trim().isEmpty()) {
//                    continue;
//                }
//                if (refObj == null) {
//                    continue;
//                }
//
//                double payableAmount = GeneralMethods.parseCommaNumber(payableObj.toString());
//                if (payableAmount <= 0) {
//                    continue;
//                }
//
//                String ref = refObj.toString();
//
//                // =====================================================
//                // COURSE
//                // =====================================================
//                if (ref.startsWith("COURSE_")) {
//
//                    int enrollmentId = Integer.parseInt(ref.replace("COURSE_", ""));
//                    String serviceName = model.getValueAt(i, COL_SERVICENAME).toString();
//
//                    // =====================================================
//                    // ONE-TIME COURSE
//                    // =====================================================
//                    if (serviceName.toUpperCase().contains("ONE-TIME")) {
//
//                        Object[] paymentRow = (Object[]) em.createNativeQuery(
//                                "SELECT student_fee_payments_id, total_paid, total_balance "
//                                + "FROM student_fee_payments "
//                                + "WHERE enrollment_id = ? AND status = 1"
//                        )
//                                .setParameter(1, enrollmentId)
//                                .getSingleResult();
//
//                        int paymentId = Integer.parseInt(paymentRow[0].toString());
//                        double currentPaid = Double.parseDouble(paymentRow[1].toString());
//                        double currentBalance = Double.parseDouble(paymentRow[2].toString());
//
//                        double newPaid = currentPaid + payableAmount;
//                        double newBalance = Math.max(currentBalance - payableAmount, 0);
//
//                        String status = (newBalance == 0) ? "COMPLETE" : "ACTIVE";
//
//                        em.createNativeQuery(
//                                "UPDATE student_fee_payments "
//                                + "SET total_paid=?, total_balance=?, payment_status=?, last_mofidied=NOW() "
//                                + "WHERE student_fee_payments_id=?"
//                        )
//                                .setParameter(1, newPaid)
//                                .setParameter(2, newBalance)
//                                .setParameter(3, status)
//                                .setParameter(4, paymentId)
//                                .executeUpdate();
//
//                        Integer nextInstallmentNo = ((Number) em.createNativeQuery(
//                                "SELECT COALESCE(MAX(installment_no),0)+1 "
//                                + "FROM student_fee_installments WHERE student_fee_payments_id=?"
//                        )
//                                .setParameter(1, paymentId)
//                                .getSingleResult()).intValue();
//
//                        em.createNativeQuery(
//                                "INSERT INTO student_fee_installments "
//                                + "(student_fee_payments_id, enrollment_id, student_fee_round_payment_master_id, installment_no, amount_paid, "
//                                + "payment_date, payment_method, payment_type, remarks, status) "
//                                + "VALUES (?, ?, ?, ?, ?, NOW(), ?, 'ROUND', 'Round Payment', 1)"
//                        )
//                                .setParameter(1, paymentId)
//                                .setParameter(2, enrollmentId)
//                                .setParameter(3, roundMasterId)
//                                .setParameter(4, nextInstallmentNo)
//                                .setParameter(5, payableAmount)
//                                .setParameter(6, paymentMode)
//                                .executeUpdate();
//
//                    } // =========================
//                    // MONTHLY COURSE SUPPORT (IMPORTANT ADDITION)
//                    // =========================
//                    else if (serviceName.toUpperCase().contains("MONTHLY")) {
//
//                        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
//                        StudentFeeInstallmentsDAO.MonthDataDTO dto = dao.getMonthData(enrollmentId);
//
//                        // =====================================================
//                        // FIX 1: GET TOTAL FEE
//                        // =====================================================
//                        double totalFee = ((Number) em.createNativeQuery(
//                                "SELECT total_fee FROM student_fee_payments WHERE enrollment_id=? AND status=1"
//                        )
//                                .setParameter(1, enrollmentId)
//                                .getSingleResult()).doubleValue();
//
//                        // =====================================================
//                        // FIX 2: CALCULATE MONTH COUNT
//                        // =====================================================
//                        int totalMonths = 0;
//                        int ty = dto.startYear;
//                        int tm = dto.startMonth;
//
//                        while (true) {
//                            totalMonths++;
//
//                            if (ty == dto.endYear && tm == dto.endMonth) {
//                                break;
//                            }
//
//                            tm++;
//                            if (tm > 12) {
//                                tm = 1;
//                                ty++;
//                            }
//                        }
//
//                        double monthlyFee = totalMonths == 0 ? 0 : (totalFee / totalMonths);
//
//                        if (monthlyFee <= 0) {
//                            System.out.println("❌ STOP: Monthly fee is 0 or invalid");
//                            return;
//                        }
//
//                        // LAST PAID MONTH
//                        String lastPaidMonth = dto.monthAmountMap.keySet()
//                                .stream()
//                                .max(String::compareTo)
//                                .orElse(null);
//
//                        int pendingMonths = getPendingMonthCount(enrollmentId);
//                        System.out.println("PENDING MONTH COUNT: " + pendingMonths);
//
//                        Number paymentRow = (Number) em.createNativeQuery(
//                                "SELECT student_fee_payments_id FROM student_fee_payments "
//                                + "WHERE enrollment_id=? AND status=1"
//                        )
//                                .setParameter(1, enrollmentId)
//                                .getSingleResult();
//
//                        int paymentId = paymentRow.intValue();
//
//                        int installmentNo = ((Number) em.createNativeQuery(
//                                "SELECT COALESCE(MAX(installment_no),0) "
//                                + "FROM student_fee_installments WHERE enrollment_id=?"
//                        )
//                                .setParameter(1, enrollmentId)
//                                .getSingleResult()).intValue();
//
//                        double remainingAmount = payableAmount;
//
//                        int y = dto.startYear;
//                        int m = dto.startMonth;
//
//                        while (remainingAmount > 0) {
//
//                            if (y > dto.endYear || (y == dto.endYear && m > dto.endMonth)) {
//                                break;
//                            }
//
//                            String monthKey = String.format("%04d-%02d", y, m);
//
//                            double alreadyPaid = dto.monthAmountMap.getOrDefault(monthKey, 0);
//                            double balance = monthlyFee - alreadyPaid;
//
//                            if (balance > 0) {
//
//                                double payNow = Math.min(balance, remainingAmount);
//
//                                installmentNo++;
//
//                                int inserted = em.createNativeQuery(
//                                        "INSERT INTO student_fee_installments "
//                                        + "(student_fee_payments_id, enrollment_id, student_fee_round_payment_master_id, installment_no, amount_paid, "
//                                        + "payment_date, payment_method, payment_type, month_for, remarks, status) "
//                                        + "VALUES (?, ?, ?, ?, ?, NOW(), ?, 'ROUND', ?, 'Round Monthly Payment', 1)"
//                                )
//                                        .setParameter(1, paymentId)
//                                        .setParameter(2, enrollmentId)
//                                        .setParameter(3, roundMasterId)
//                                        .setParameter(4, installmentNo)
//                                        .setParameter(5, payNow)
//                                        .setParameter(6, paymentMode)
//                                        .setParameter(7, monthKey)
//                                        .executeUpdate();
//
//                                // IMPORTANT: update map to avoid duplicate allocation in same run
////                                dto.monthAmountMap.put(monthKey, alreadyPaid + payNow);
//                                double sum = alreadyPaid + payNow;
//                                int updatedAmount = (int) sum;
//                                dto.monthAmountMap.put(monthKey, updatedAmount);
//
//                                remainingAmount -= payNow;
//                            }
//
//                            m++;
//                            if (m > 12) {
//                                m = 1;
//                                y++;
//                            }
//                        }
//
//                        // =====================================================
//                        // FINAL CALCULATION (FIXED)
//                        // =====================================================
//                        double totalPaids = ((Number) em.createNativeQuery(
//                                "SELECT COALESCE(SUM(amount_paid),0) "
//                                + "FROM student_fee_installments WHERE enrollment_id=?"
//                        )
//                                .setParameter(1, enrollmentId)
//                                .getSingleResult()).doubleValue();
//
//                        double finalTotalFee = totalFee; // IMPORTANT FIX
//
//                        em.createNativeQuery(
//                                "UPDATE student_fee_payments "
//                                + "SET total_paid=?, total_balance=?, payment_status=? "
//                                + "WHERE enrollment_id=?"
//                        )
//                                .setParameter(1, totalPaids)
//                                .setParameter(2, finalTotalFee - totalPaids)
//                                .setParameter(3, totalPaids >= finalTotalFee ? "COMPLETED" : "ACTIVE")
//                                .setParameter(4, enrollmentId)
//                                .executeUpdate();
//
//                    }
//
//                    // =========================
//                    // ROUND MASTER DETAILS
//                    // =========================
//                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
//                    d.setStudentFeeRoundPaymentMaster(master);
//                    d.setEnrollmentId(enrollmentId);
//                    d.setReferenceType("COURSE");
//                    d.setPaidAmount(payableAmount);
//                    d.setStatus(1);
//
//                    em.persist(d);
//                } // =====================================================
//                // ADDITIONAL / INVENTORY
//                // =====================================================
//                else if (ref.startsWith("ADD_")) {
//
//                    int safId = Integer.parseInt(ref.replace("ADD_", ""));
//
//                    em.createNativeQuery(
//                            "INSERT INTO student_additional_fee_payments "
//                            + "(student_additional_fees_id, student_fee_round_payment_master_id, paid_date, amount_paid, payment_method, user, status) "
//                            + "VALUES (?, ?, NOW(), ?, ?, ?, 1)"
//                    )
//                            .setParameter(1, safId)
//                            .setParameter(2, roundMasterId)
//                            .setParameter(3, payableAmount)
//                            .setParameter(4, paymentMode)
//                            .setParameter(5, user)
//                            .executeUpdate();
//
//                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
//                    d.setStudentFeeRoundPaymentMaster(master);
//                    d.setEnrollmentId(null);
//                    d.setReferenceId(safId);
//                    d.setReferenceType("ADDITIONAL");
//                    d.setPaidAmount(payableAmount);
//                    d.setStatus(1);
//
//                    em.persist(d);
//                }
//            }
//
//            // ✅ AUDIT LOG: Bulk Round Payment
//            // Grab the name safely
//            String studentName = (rp_student_name_text.getText() != null)
//                    ? rp_student_name_text.getText() : "";
//
//            String description = String.format(
//                    "Round Payment: Student=%s, Mode=%s, Amount=%.2f, Student ID: %d",
//                    studentName, paymentMode, totalPaid, studentId
//            );
//
//            logHelper.log(
//                    "ROUND_PAYMENT",
//                    studentId,
//                    "PAYMENT", // Usually the 'Action' column looks better as a single verb like 'PAYMENT' or 'COLLECT'
//                    paymentMode,
//                    totalPaid,
//                    description,
//                    user
//            );
//
//            tx.commit();
//            JOptionPane.showMessageDialog(this, "Payment saved successfully.", "Payment Processed", JOptionPane.INFORMATION_MESSAGE);
//
//        } catch (Exception e) {
//            tx.rollback();
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }

    public void saveRoundPaymentCheque(int studentId, JTable table,
            String paymentMode, double totalPaid, double roundingAdj, String user) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        int COL_SERVICENAME = 3;
        int COL_PAYABLE = 9;
        int COL_IDS = 11;

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        Map<Integer, Integer> paymentCache = new HashMap<>();

        try {
            tx.begin();

            // =========================
            // MASTER
            // =========================
            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
            master.setStudentId(studentId);
            master.setPaymentDate(new java.util.Date());
            master.setPaymentMode("CHEQUE");
            master.setTotalPaid(totalPaid);
            master.setRoundingAdjustment(roundingAdj);
            master.setUser(user);
            master.setStatus(1);

            em.persist(master);
            em.flush();

            int roundMasterId = master.getStudentFeeRoundPaymentMasterId();

            // =========================
            // CHEQUE HEADER (ONLY ONCE)
            // =========================
            em.createNativeQuery(
                    "INSERT INTO student_fee_cheque_details "
                    + "(reference_id, reference_type, category, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
                    + "VALUES (?, 'ROUND', 'STUDENT', ?, ?, ?, ?, ?, 'PENDING', 1)"
            )
                    .setParameter(1, roundMasterId)
                    .setParameter(2, rp_round_cheque_number_text.getText())
                    .setParameter(3, rp_round_bank_name_combo.getEditor().getItem().toString())
                    .setParameter(4, rp_round_cheque_branch.getText())
                    .setParameter(5, rp_round_cheque_date.getDate())
                    .setParameter(6, totalPaid)
                    .executeUpdate();

            // =========================
            // LOOP TABLE
            // =========================
            for (int i = 0; i < model.getRowCount(); i++) {

                Object refObj = model.getValueAt(i, COL_IDS);
                Object payObj = model.getValueAt(i, COL_PAYABLE);
                Object nameObj = model.getValueAt(i, COL_SERVICENAME);

                if (refObj == null || payObj == null) {
                    continue;
                }

                String ref = refObj.toString();
                double amount = GeneralMethods.parseCommaNumber(payObj.toString());
                String serviceName = nameObj != null ? nameObj.toString() : "";

                if (amount <= 0) {
                    continue;
                }

                // =====================================================
                // ONE TIME COURSE
                // =====================================================
                if (ref.startsWith("COURSE_") && serviceName.toUpperCase().contains("ONE-TIME")) {

                    int enrollmentId = Integer.parseInt(ref.replace("COURSE_", ""));

                    int paymentId = getPaymentId(em, paymentCache, enrollmentId);
                    int nextNo = getNextInstallmentNo(em, paymentId);

                    em.createNativeQuery(
                            "INSERT INTO student_fee_installments "
                            + "(student_fee_payments_id, enrollment_id, student_fee_round_payment_master_id, "
                            + "installment_no, amount_paid, payment_date, payment_method, payment_type, remarks, status) "
                            + "VALUES (?, ?, ?, ?, ?, NOW(), ?, 'ROUND', 'ROUND CHEQUE', 1)"
                    )
                            .setParameter(1, paymentId)
                            .setParameter(2, enrollmentId)
                            .setParameter(3, roundMasterId)
                            .setParameter(4, nextNo)
                            .setParameter(5, amount)
                            .setParameter(6, paymentMode)
                            .executeUpdate();
                } // =====================================================
                // MONTHLY COURSE (IMPORTANT FIXED LOGIC)
                // =====================================================
                // =====================================================
                // MONTHLY COURSE (FIFO + RETURNED CHEQUE FIX)
                // =====================================================
                else if (ref.startsWith("COURSE_") && serviceName.toUpperCase().contains("MONTHLY")) {

                    int enrollmentId = Integer.parseInt(ref.replace("COURSE_", ""));

                    StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
                    StudentFeeInstallmentsDAO.MonthDataDTO dto = dao.getMonthData(enrollmentId);

                    // =====================================================
                    // 1. GET TOTAL FEE
                    // =====================================================
                    double totalFee = ((Number) em.createNativeQuery(
                            "SELECT total_fee "
                            + "FROM student_fee_payments "
                            + "WHERE enrollment_id=? AND status=1"
                    )
                            .setParameter(1, enrollmentId)
                            .getSingleResult()).doubleValue();

                    // =====================================================
                    // 2. CALCULATE TOTAL MONTHS
                    // =====================================================
                    int totalMonths = 0;
                    int ty = dto.startYear;
                    int tm = dto.startMonth;

                    while (true) {
                        totalMonths++;

                        if (ty == dto.endYear && tm == dto.endMonth) {
                            break;
                        }

                        tm++;
                        if (tm > 12) {
                            tm = 1;
                            ty++;
                        }
                    }

                    double monthlyFee = totalMonths == 0 ? 0 : (totalFee / totalMonths);

                    if (monthlyFee <= 0) {
                        System.out.println("Invalid monthly fee");
                        return;
                    }

                    // =====================================================
                    // 3. PAYMENT ID + INSTALLMENT NO
                    // =====================================================
                    int paymentId = getPaymentId(em, paymentCache, enrollmentId);
                    int installmentNo = getNextInstallmentNo(em, paymentId);

                    // =====================================================
                    // 4. FETCH VALID PAID MONTHS ONLY
                    // Ignore:
                    // - ZERO
                    // - DISCOUNT
                    // - status = 0 (returned cheque rows)
                    // =====================================================
                    Map<String, Double> paidMap = new HashMap<>();

                    List<Object[]> paidRows = em.createNativeQuery(
                            "SELECT month_for, COALESCE(SUM(amount_paid),0) "
                            + "FROM student_fee_installments "
                            + "WHERE enrollment_id=? "
                            + "AND status=1 "
                            + "AND month_for IS NOT NULL "
                            + "AND payment_type NOT IN ('ZERO','DISCOUNT') "
                            + "GROUP BY month_for"
                    )
                            .setParameter(1, enrollmentId)
                            .getResultList();

                    for (Object[] r : paidRows) {
                        String month = r[0].toString();
                        double amt = ((Number) r[1]).doubleValue();
                        paidMap.put(month, amt);
                    }

                    // =====================================================
                    // 5. FIFO ALLOCATION
                    // oldest unpaid first
                    // =====================================================
                    double remaining = amount;

                    int y = dto.startYear;
                    int m = dto.startMonth;

                    while (remaining > 0) {

                        if (y > dto.endYear || (y == dto.endYear && m > dto.endMonth)) {
                            break;
                        }

                        String monthKey = String.format("%04d-%02d", y, m);

                        double alreadyPaid = paidMap.getOrDefault(monthKey, 0.0);
                        double balance = monthlyFee - alreadyPaid;

                        if (balance > 0) {

                            double payNow = Math.min(balance, remaining);

                            installmentNo++;

                            em.createNativeQuery(
                                    "INSERT INTO student_fee_installments "
                                    + "(student_fee_payments_id, enrollment_id, "
                                    + "student_fee_round_payment_master_id, installment_no, "
                                    + "amount_paid, payment_date, payment_method, "
                                    + "payment_type, month_for, remarks, status) "
                                    + "VALUES (?, ?, ?, ?, ?, NOW(), ?, "
                                    + "'ROUND', ?, 'ROUND CHEQUE', 1)"
                            )
                                    .setParameter(1, paymentId)
                                    .setParameter(2, enrollmentId)
                                    .setParameter(3, roundMasterId)
                                    .setParameter(4, installmentNo)
                                    .setParameter(5, payNow)
                                    .setParameter(6, paymentMode)
                                    .setParameter(7, monthKey)
                                    .executeUpdate();

                            paidMap.put(monthKey, alreadyPaid + payNow);

                            remaining -= payNow;
                        }

                        m++;
                        if (m > 12) {
                            m = 1;
                            y++;
                        }
                    }
                }

                // =========================
// ROUND DETAIL (COURSE + ADDITIONAL)
// =========================
                if (ref.startsWith("COURSE_")) {

                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
                    d.setStudentFeeRoundPaymentMaster(master);
                    d.setEnrollmentId(Integer.parseInt(ref.replace("COURSE_", "")));
                    d.setReferenceType("COURSE");
                    d.setPaidAmount(amount);
                    d.setStatus(1);

                    em.persist(d);

                } else if (ref.startsWith("ADD_")) {

                    int additionalId = Integer.parseInt(ref.replace("ADD_", ""));

                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
                    d.setStudentFeeRoundPaymentMaster(master);

                    d.setEnrollmentId(null); // important
                    d.setReferenceId(additionalId); // 🔥 MUST STORE THIS
                    d.setReferenceType("ADDITIONAL");

                    d.setPaidAmount(amount);
                    d.setStatus(1);

                    em.persist(d);
                }
            }

            // ✅ AUDIT LOG: Round Cheque Payment
            // 1. Capture details from the UI components
            String studentName = (rp_student_name_text.getText() != null) ? rp_student_name_text.getText() : "Unknown";
            String chqNo = rp_round_cheque_number_text.getText();
            String bank = rp_round_bank_name_combo.getEditor().getItem().toString();

            // 2. Format the description to include Cheque and Rounding details
            String description = String.format(
                    "Round Cheque Payment: Student=%s, Chq#=%s, Bank=%s, Amount=%.2f, Adj=%.2f, Student ID: %d",
                    studentName, chqNo, bank, totalPaid, roundingAdj, studentId
            );

            // 3. Call your logHelper (Same format as Cash/Round payment)
            logHelper.log(
                    "ROUND_PAYMENT", // action_type
                    studentId, // student_id
                    "ROUND PAYMENT", // action_performed
                    "CHEQUE", // payment_mode
                    totalPaid, // amount
                    description, // description
                    user // user
            );

            // Now commit everything
            tx.commit();

            tx.commit();

            JOptionPane.showMessageDialog(null, "Round Cheque Payment Saved Successfully!");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private int calculateMonths(StudentFeeInstallmentsDAO.MonthDataDTO dto) {

        int count = 0;
        int y = dto.startYear;
        int m = dto.startMonth;

        while (true) {

            count++;

            if (y == dto.endYear && m == dto.endMonth) {
                break;
            }

            m++;
            if (m > 12) {
                m = 1;
                y++;
            }
        }

        return count;
    }

    private int getNextInstallmentNo(EntityManager em, int paymentId) {

        return ((Number) em.createNativeQuery(
                "SELECT COALESCE(MAX(installment_no),0)+1 FROM student_fee_installments WHERE student_fee_payments_id=?"
        )
                .setParameter(1, paymentId)
                .getSingleResult()).intValue();
    }

    private int getPaymentId(EntityManager em, Map<Integer, Integer> cache, int enrollmentId) {

        if (cache.containsKey(enrollmentId)) {
            return cache.get(enrollmentId);
        }

        int paymentId = ((Number) em.createNativeQuery(
                "SELECT student_fee_payments_id FROM student_fee_payments WHERE enrollment_id=? AND status=1"
        )
                .setParameter(1, enrollmentId)
                .getSingleResult()).intValue();

        cache.put(enrollmentId, paymentId);
        return paymentId;
    }

//    public void saveRoundPaymentCheque(int studentId, JTable table,
//            String paymentMode, double totalPaid, double roundingAdj, String user) {
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//        int COL_SERVICENAME = 3;
//        int COL_PAYABLE = 9;
//        int COL_IDS = 11;
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//
//            tx.begin();
//
//            // =========================
//            // 1. MASTER (ROUND)
//            // =========================
//            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
//            master.setStudentId(studentId);
//            master.setPaymentDate(new java.util.Date());
//            master.setPaymentMode("CHEQUE"); // force cheque
//            master.setTotalPaid(totalPaid);
//            master.setRoundingAdjustment(roundingAdj);
//            master.setUser(user);
//            master.setStatus(1);
//
//            em.persist(master);
//            em.flush();
//
//            Integer roundMasterId = master.getStudentFeeRoundPaymentMasterId();
//
//            System.out.println("ROUND MASTER ID: " + roundMasterId);
//
//            // =========================
//            // 2. SAVE CHEQUE (ONLY ONCE)
//            // =========================
//            em.createNativeQuery(
//                    "INSERT INTO student_fee_cheque_details "
//                    + "(reference_id, reference_type, category, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
//                    + "VALUES (?, 'ROUND', ?, ?, ?, ?, ?, ?, 'PENDING', 1)"
//            )
//                    .setParameter(1, roundMasterId)
//                    .setParameter(2, "STUDENT")
//                    .setParameter(3, rp_round_cheque_number_text.getText())
//                    .setParameter(4, rp_round_bank_name_combo.getEditor().getItem().toString())
//                    .setParameter(5, rp_round_cheque_branch.getText())
//                    .setParameter(6, rp_round_cheque_date.getDate())
//                    .setParameter(7, totalPaid)
//                    .executeUpdate();
//
//            System.out.println("✅ CHEQUE SAVED (PENDING)");
//
//            // =========================
//            // 3. LOOP TABLE (DETAILS ONLY)
//            // =========================
//            for (int i = 0; i < model.getRowCount(); i++) {
//
//                Object payableObj = model.getValueAt(i, COL_PAYABLE);
//                Object refObj = model.getValueAt(i, COL_IDS);
//
//                if (payableObj == null || payableObj.toString().trim().isEmpty()) {
//                    continue;
//                }
//                if (refObj == null) {
//                    continue;
//                }
//
//                double payableAmount = GeneralMethods.parseCommaNumber(payableObj.toString());
//                if (payableAmount <= 0) {
//                    continue;
//                }
//
//                String ref = refObj.toString();
//
//                // =========================
//                // COURSE
//                // =========================
//                if (ref.startsWith("COURSE_")) {
//
//                    int enrollmentId = Integer.parseInt(ref.replace("COURSE_", ""));
//
//                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
//                    d.setStudentFeeRoundPaymentMaster(master);
//                    d.setEnrollmentId(enrollmentId);
//                    d.setReferenceType("COURSE");
//                    d.setPaidAmount(payableAmount);
//                    d.setStatus(1);
//
//                    em.persist(d);
//
//                    System.out.println("COURSE DETAIL SAVED: " + enrollmentId + " | AMOUNT: " + payableAmount);
//                } // =========================
//                // ADDITIONAL / INVENTORY
//                // =========================
//                else if (ref.startsWith("ADD_")) {
//
//                    int safId = Integer.parseInt(ref.replace("ADD_", ""));
//
//                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
//                    d.setStudentFeeRoundPaymentMaster(master);
//                    d.setEnrollmentId(null);
//                    d.setReferenceId(safId);
//                    d.setReferenceType("ADDITIONAL FEE");
//                    d.setPaidAmount(payableAmount);
//                    d.setStatus(1);
//
//                    em.persist(d);
//
//                    System.out.println("ADDITIONAL DETAIL SAVED: " + safId + " | AMOUNT: " + payableAmount);
//                }
//            }
//
//            // ✅ AUDIT LOG: Cheque Round Payment (Pending)
//            String studentName = (rp_student_name_text.getText() != null)
//                    ? rp_student_name_text.getText() : "";
//
//            String chequeNo = rp_round_cheque_number_text.getText();
//            String bank = rp_round_bank_name_combo.getEditor().getItem().toString();
//
//            String description = String.format(
//                    "Cheque Round Payment (PENDING): Student=%s, Cheque No=%s, Bank=%s, Amount=%.2f, Student ID: %d",
//                    studentName, chequeNo, bank, totalPaid, studentId
//            );
//
//            logHelper.log(
//                    "ROUND_PAYMENT",
//                    studentId,
//                    "CHEQUE_RECEIVED",
//                    "CHEQUE",
//                    totalPaid,
//                    description,
//                    user
//            );
//
//            tx.commit();
//
//            JOptionPane.showMessageDialog(this, "Payment saved successfully.", "Payment Processed", JOptionPane.INFORMATION_MESSAGE);
//
//        } catch (Exception e) {
//            tx.rollback();
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }
    public double getPendingChequeForCourse(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            // =========================================
            // 1. DIRECT CHEQUE (ONE-TIME SCREEN)
            // =========================================
            Double directCheque = (Double) em.createNativeQuery(
                    "SELECT COALESCE(SUM(cheque_amount),0) "
                    + "FROM student_fee_cheque_details "
                    + "WHERE reference_type='COURSE' "
                    + "AND reference_id=? "
                    + "AND cheque_status='PENDING' "
                    + "AND status=1 "
                    + "AND category='STUDENT'"
            )
                    .setParameter(1, enrollmentId)
                    .getSingleResult();

            if (directCheque == null) {
                directCheque = 0.0;
            }

            // =========================================
            // 2. ROUND CHEQUE
            // =========================================
            Double roundCheque = (Double) em.createNativeQuery(
                    "SELECT COALESCE(SUM(c.cheque_amount),0) "
                    + "FROM student_fee_cheque_details c "
                    + "JOIN student_fee_round_payment_master_details d "
                    + "ON c.reference_id = d.student_fee_round_payment_master_id "
                    + "WHERE c.reference_type='ROUND' "
                    + "AND d.enrollment_id=? "
                    + "AND c.cheque_status='PENDING' "
                    + "AND c.status=1 "
                    + "AND d.status=1 "
                    + "AND c.category='STUDENT'"
            )
                    .setParameter(1, enrollmentId)
                    .getSingleResult();

            if (roundCheque == null) {
                roundCheque = 0.0;
            }

            return directCheque + roundCheque;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

//    private double getPendingChequeForCourse(int enrollmentId) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//
//            Double amount = (Double) em.createNativeQuery(
//                    "SELECT COALESCE(SUM(d.paid_amount),0) "
//                    + "FROM student_fee_cheque_details c "
//                    + "JOIN student_fee_round_payment_master m "
//                    + "ON c.reference_id = m.student_fee_round_payment_master_id "
//                    + "JOIN student_fee_round_payment_master_details d "
//                    + "ON d.student_fee_round_payment_master_id = m.student_fee_round_payment_master_id "
//                    + "WHERE c.category = 'STUDENT' "
//                    + "AND c.cheque_status = 'PENDING' "
//                    + "AND c.status = 1 "
//                    + "AND d.reference_type = 'COURSE' "
//                    + "AND d.enrollment_id = ?"
//            )
//                    .setParameter(1, enrollmentId)
//                    .getSingleResult();
//
//            return amount == null ? 0 : amount;
//
//        } finally {
//            em.close();
//        }
//    }
    private double getPendingChequeForAdditional(int additionalFeeId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            Double amount = (Double) em.createNativeQuery(
                    "SELECT COALESCE(SUM(d.paid_amount),0) "
                    + "FROM student_fee_cheque_details c "
                    + "JOIN student_fee_round_payment_master m "
                    + "ON c.reference_id = m.student_fee_round_payment_master_id "
                    + "JOIN student_fee_round_payment_master_details d "
                    + "ON d.student_fee_round_payment_master_id = m.student_fee_round_payment_master_id "
                    + "WHERE c.category = 'STUDENT' "
                    + "AND c.cheque_status = 'PENDING' "
                    + "AND c.status = 1 "
                    + "AND d.reference_type = 'ADDITIONAL FEE' "
                    + "AND d.reference_id = ?"
            )
                    .setParameter(1, additionalFeeId)
                    .getSingleResult();

            return amount == null ? 0 : amount;

        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelRound2 = new Classes.PanelRound();
        Main_Lable = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        rp_date = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        rp_due_table = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        rp_round_calculate_text = new javax.swing.JTextField();
        rp_student_name_text = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        rp_round_total_pay_cash_text = new javax.swing.JTextField();
        firstName_label7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        rp_round_payement_method_combo = new javax.swing.JComboBox<>();
        buttonGradient4 = new Classes.ButtonGradient();
        jPanel10 = new javax.swing.JPanel();
        rp_round_cheque_number_text = new javax.swing.JTextField();
        rp_round_bank_name_combo = new javax.swing.JComboBox<>();
        rp_round_cheque_branch = new javax.swing.JTextField();
        rp_round_cheque_amount = new javax.swing.JTextField();
        rp_round_cheque_date = new com.toedter.calendar.JDateChooser();
        rp_round_cheque_status = new javax.swing.JComboBox<>();
        buttonGradient5 = new Classes.ButtonGradient();
        rp_round_total_pending_cheque_text = new javax.swing.JTextField();
        firstName_label9 = new javax.swing.JLabel();
        rp_round_remaining_bal_text = new javax.swing.JTextField();
        firstName_label8 = new javax.swing.JLabel();
        rp_total_due_text = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        rp_round_total_paid_text = new javax.swing.JTextField();
        firstName_label10 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelRound2.setBackground(new java.awt.Color(247, 178, 50));
        panelRound2.setRoundBottomLeft(10);
        panelRound2.setRoundBottomRight(10);
        panelRound2.setRoundTopLeft(10);
        panelRound2.setRoundTopRight(10);

        Main_Lable.setFont(new java.awt.Font("Roboto Black", 3, 14)); // NOI18N
        Main_Lable.setForeground(new java.awt.Color(255, 255, 255));
        Main_Lable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Main_Lable.setText("DUE PAYMENTS");

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Main_Lable, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Main_Lable, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Total Due Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Issue Date");

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText(" Student Name");

        rp_date.setForeground(new java.awt.Color(204, 204, 204));
        rp_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        rp_due_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Category", "Date", "Service/Item", "Qty", "Amount", "Paid", "Cheque", "Due Amount", "Payable", "", "ids"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        rp_due_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rp_due_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(rp_due_table);
        if (rp_due_table.getColumnModel().getColumnCount() > 0) {
            rp_due_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            rp_due_table.getColumnModel().getColumn(3).setPreferredWidth(200);
            rp_due_table.getColumnModel().getColumn(10).setMinWidth(50);
            rp_due_table.getColumnModel().getColumn(10).setPreferredWidth(50);
            rp_due_table.getColumnModel().getColumn(10).setMaxWidth(50);
            rp_due_table.getColumnModel().getColumn(11).setMinWidth(0);
            rp_due_table.getColumnModel().getColumn(11).setPreferredWidth(0);
            rp_due_table.getColumnModel().getColumn(11).setMaxWidth(0);
        }

        jLabel11.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel11.setText("Calculate Payment");

        rp_round_calculate_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        rp_round_calculate_text.setToolTipText("Enter total amount to auto-distribute across all selected dues");
        rp_round_calculate_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_calculate_textActionPerformed(evt);
            }
        });
        rp_round_calculate_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rp_round_calculate_textKeyTyped(evt);
            }
        });

        rp_student_name_text.setEditable(false);
        rp_student_name_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        rp_student_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_student_name_textActionPerformed(evt);
            }
        });
        rp_student_name_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rp_student_name_textKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(rp_date, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(rp_student_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rp_round_calculate_text, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1285, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rp_round_calculate_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_student_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        rp_round_total_pay_cash_text.setEditable(false);
        rp_round_total_pay_cash_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_total_pay_cash_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_total_pay_cash_textActionPerformed(evt);
            }
        });
        rp_round_total_pay_cash_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                rp_round_total_pay_cash_textKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_total_pay_cash_textKeyReleased(evt);
            }
        });

        firstName_label7.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label7.setText("Total Paid");

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel14.setText("Select Payment Method");

        rp_round_payement_method_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        rp_round_payement_method_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CASH", "CARD" }));

        buttonGradient4.setText("SAVE");
        buttonGradient4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient4ActionPerformed(evt);
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
                        .addComponent(firstName_label7, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rp_round_total_pay_cash_text, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_round_payement_method_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(rp_round_payement_method_combo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstName_label7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rp_round_total_pay_cash_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Cash / Card", jPanel7);

        rp_round_cheque_number_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_cheque_number_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_cheque_number_textActionPerformed(evt);
            }
        });
        rp_round_cheque_number_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_cheque_number_textKeyReleased(evt);
            }
        });

        rp_round_bank_name_combo.setEditable(true);
        rp_round_bank_name_combo.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_bank_name_combo.setMinimumSize(new java.awt.Dimension(83, 30));
        rp_round_bank_name_combo.setPreferredSize(new java.awt.Dimension(72, 30));
        rp_round_bank_name_combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_bank_name_comboActionPerformed(evt);
            }
        });
        rp_round_bank_name_combo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_bank_name_comboKeyReleased(evt);
            }
        });

        rp_round_cheque_branch.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_cheque_branch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_cheque_branchActionPerformed(evt);
            }
        });
        rp_round_cheque_branch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_cheque_branchKeyReleased(evt);
            }
        });

        rp_round_cheque_amount.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_cheque_amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_cheque_amountActionPerformed(evt);
            }
        });
        rp_round_cheque_amount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_cheque_amountKeyReleased(evt);
            }
        });

        rp_round_cheque_date.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N

        rp_round_cheque_status.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_cheque_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending" }));
        rp_round_cheque_status.setMinimumSize(new java.awt.Dimension(83, 30));
        rp_round_cheque_status.setPreferredSize(new java.awt.Dimension(72, 30));
        rp_round_cheque_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_cheque_statusActionPerformed(evt);
            }
        });
        rp_round_cheque_status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_cheque_statusKeyReleased(evt);
            }
        });

        buttonGradient5.setText("SAVE");
        buttonGradient5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(rp_round_cheque_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_round_cheque_date, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_round_cheque_status, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(rp_round_bank_name_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_round_cheque_branch, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rp_round_cheque_number_text))
                .addGap(17, 17, 17))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(rp_round_cheque_number_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rp_round_bank_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rp_round_cheque_branch, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rp_round_cheque_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rp_round_cheque_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rp_round_cheque_status, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(205, 205, 205))
        );

        jTabbedPane1.addTab("Cheque", jPanel10);

        rp_round_total_pending_cheque_text.setEditable(false);
        rp_round_total_pending_cheque_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_total_pending_cheque_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_total_pending_cheque_textActionPerformed(evt);
            }
        });
        rp_round_total_pending_cheque_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_total_pending_cheque_textKeyReleased(evt);
            }
        });

        firstName_label9.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label9.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label9.setText("Pending Cheques");

        rp_round_remaining_bal_text.setEditable(false);
        rp_round_remaining_bal_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_remaining_bal_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_remaining_bal_textActionPerformed(evt);
            }
        });
        rp_round_remaining_bal_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                rp_round_remaining_bal_textKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_remaining_bal_textKeyReleased(evt);
            }
        });

        firstName_label8.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label8.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label8.setText("Remaining Balance");

        rp_total_due_text.setEditable(false);
        rp_total_due_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        rp_total_due_text.setForeground(new java.awt.Color(251, 63, 63));
        rp_total_due_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_total_due_textActionPerformed(evt);
            }
        });
        rp_total_due_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rp_total_due_textKeyTyped(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel10.setText("Total Due");

        rp_round_total_paid_text.setEditable(false);
        rp_round_total_paid_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        rp_round_total_paid_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rp_round_total_paid_textActionPerformed(evt);
            }
        });
        rp_round_total_paid_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rp_round_total_paid_textKeyReleased(evt);
            }
        });

        firstName_label10.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label10.setText("Total Paid");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/revoke32.png"))); // NOI18N
        jButton3.setToolTipText("Search and cancel recent transactions (Last 3 days)");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rp_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(firstName_label10, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rp_round_total_paid_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rp_round_total_pending_cheque_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(firstName_label9, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rp_round_remaining_bal_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(firstName_label8))))
                        .addGap(186, 186, 186)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rp_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(firstName_label10, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(firstName_label9, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(firstName_label8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rp_round_total_paid_text)
                                    .addComponent(rp_round_total_pending_cheque_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rp_round_remaining_bal_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(165, 165, 165))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rp_round_remaining_bal_text, rp_round_total_pending_cheque_text});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rp_round_total_paid_text, rp_total_due_text});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rp_due_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rp_due_tableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_due_tableMouseClicked

    private void rp_total_due_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_total_due_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_total_due_textActionPerformed

    private void rp_total_due_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_total_due_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_total_due_textKeyTyped

    private void rp_round_total_pay_cash_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_total_pay_cash_textActionPerformed

    }//GEN-LAST:event_rp_round_total_pay_cash_textActionPerformed

    private void rp_round_total_pay_cash_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_total_pay_cash_textKeyPressed

    }//GEN-LAST:event_rp_round_total_pay_cash_textKeyPressed

    private void rp_round_total_pay_cash_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_total_pay_cash_textKeyReleased

    }//GEN-LAST:event_rp_round_total_pay_cash_textKeyReleased

    private void rp_round_total_pending_cheque_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_total_pending_cheque_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_total_pending_cheque_textActionPerformed

    private void rp_round_total_pending_cheque_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_total_pending_cheque_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_total_pending_cheque_textKeyReleased

    private void rp_round_remaining_bal_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_remaining_bal_textActionPerformed

    }//GEN-LAST:event_rp_round_remaining_bal_textActionPerformed

    private void rp_round_remaining_bal_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_remaining_bal_textKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_remaining_bal_textKeyPressed

    private void rp_round_remaining_bal_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_remaining_bal_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_remaining_bal_textKeyReleased

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        String paymentMode = rp_round_payement_method_combo.getSelectedItem().toString();
        double totPaid = GeneralMethods.parseCommaNumber(rp_round_total_pay_cash_text.getText());
        saveRoundPayment(selectedStudentIds, rp_due_table, paymentMode, totPaid, 0.00, username);

    }//GEN-LAST:event_buttonGradient4ActionPerformed

    private void rp_round_cheque_number_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_cheque_number_textActionPerformed
        rp_round_cheque_amount.requestFocus();
    }//GEN-LAST:event_rp_round_cheque_number_textActionPerformed

    private void rp_round_cheque_number_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_cheque_number_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_cheque_number_textKeyReleased

    private void rp_round_bank_name_comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_bank_name_comboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_bank_name_comboActionPerformed

    private void rp_round_bank_name_comboKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_bank_name_comboKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_bank_name_comboKeyReleased

    private void rp_round_cheque_branchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_cheque_branchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_cheque_branchActionPerformed

    private void rp_round_cheque_branchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_cheque_branchKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_cheque_branchKeyReleased

    private void rp_round_cheque_amountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_cheque_amountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_cheque_amountActionPerformed

    private void rp_round_cheque_amountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_cheque_amountKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_cheque_amountKeyReleased

    private void rp_round_cheque_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_cheque_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_cheque_statusActionPerformed

    private void rp_round_cheque_statusKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_cheque_statusKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_cheque_statusKeyReleased

    private void rp_round_calculate_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_calculate_textActionPerformed
        calculateRoundDistribution();
    }//GEN-LAST:event_rp_round_calculate_textActionPerformed

    private void rp_round_calculate_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_calculate_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_calculate_textKeyTyped

    private void buttonGradient5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient5ActionPerformed

        if (rp_round_cheque_date.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Select cheque date", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totPaid = GeneralMethods.parseCommaNumber(rp_round_total_pay_cash_text.getText());
        saveRoundPaymentCheque(selectedStudentIds, rp_due_table, "CHEQUE", totPaid, 0.00, username);
    }//GEN-LAST:event_buttonGradient5ActionPerformed

    private void rp_round_total_paid_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_total_paid_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_total_paid_textActionPerformed

    private void rp_round_total_paid_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_total_paid_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_total_paid_textKeyReleased

    private void rp_student_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_student_name_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_student_name_textActionPerformed

    private void rp_student_name_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_student_name_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_student_name_textKeyTyped

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        Cancel_Round_Payment dialog = new Cancel_Round_Payment(parentFrame, selectedStudentIds, studentName, username, role);
        GeneralMethods.openDialogOnDialog(this, dialog);

    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Round_Payment dialog
                    = new Round_Payment(frame, 0, "", "", "");

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel Main_Lable;
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradient buttonGradient5;
    private javax.swing.JLabel firstName_label10;
    private javax.swing.JLabel firstName_label7;
    private javax.swing.JLabel firstName_label8;
    private javax.swing.JLabel firstName_label9;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTabbedPane jTabbedPane1;
    private Classes.PanelRound panelRound2;
    public static com.toedter.calendar.JDateChooser rp_date;
    private javax.swing.JTable rp_due_table;
    private javax.swing.JComboBox<String> rp_round_bank_name_combo;
    private javax.swing.JTextField rp_round_calculate_text;
    private javax.swing.JTextField rp_round_cheque_amount;
    private javax.swing.JTextField rp_round_cheque_branch;
    private com.toedter.calendar.JDateChooser rp_round_cheque_date;
    private javax.swing.JTextField rp_round_cheque_number_text;
    private javax.swing.JComboBox<String> rp_round_cheque_status;
    private javax.swing.JComboBox<String> rp_round_payement_method_combo;
    public static javax.swing.JTextField rp_round_remaining_bal_text;
    public static javax.swing.JTextField rp_round_total_paid_text;
    private javax.swing.JTextField rp_round_total_pay_cash_text;
    public static javax.swing.JTextField rp_round_total_pending_cheque_text;
    private javax.swing.JTextField rp_student_name_text;
    private javax.swing.JTextField rp_total_due_text;
    // End of variables declaration//GEN-END:variables

}
