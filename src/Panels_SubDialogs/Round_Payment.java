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
import java.util.List;
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
        rp_student_name_combo.setSelectedItem(this.studentName);

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

        // =========================================
        // 1. COURSE DUES
        // =========================================
        StudentAdditionalFeesDAO dao = new StudentAdditionalFeesDAO();
        List<Object[]> courseList = dao.getStudentCourseDues(studentId);

        for (Object[] row : courseList) {

            int enrollmentId = Integer.parseInt(row[0].toString());
            String courseName = row[1].toString();
            String courseType = row[2].toString();
            double balance = GeneralMethods.parseCommaNumber(row[3].toString());
            String p_date = row[4] != null ? row[4].toString().split(" ")[0] : "";
            double tot_amount = GeneralMethods.parseCommaNumber(row[5].toString());
            double tot_paid = GeneralMethods.parseCommaNumber(row[6].toString());

            double chequePending = getPendingChequeForCourse(enrollmentId);

            double finalDue = balance - chequePending;
            if (finalDue < 0) {
                finalDue = 0;
            }

            String name = courseName + " (" + courseType + ")";

            int qty = 1;

            if ("MONTHLY".equalsIgnoreCase(courseType)) {
                qty = getPendingMonthCount(enrollmentId);
                if (qty == 0) {
                    qty = 1;
                }
            }

            model.addRow(new Object[]{
                count++,
                "COURSE",
                p_date,
                name,
                qty,
                GeneralMethods.formatWithComma(tot_amount),
                GeneralMethods.formatWithComma(tot_paid),
                GeneralMethods.formatWithComma(chequePending), // ✅ CHEQUE COLUMN
                GeneralMethods.formatWithComma(finalDue), // ✅ UPDATED DUE
                "",
                false,
                "COURSE_" + enrollmentId
            });
        }

        // =========================================
// 2. ADDITIONAL + INVENTORY DUES (FINAL LOGIC)
// =========================================
        EntityManager em = HibernateConfig.getEntityManager();

        try {

            List<Object[]> issuedList = em.createNativeQuery(
                    "SELECT fee_type_id, "
                    + "MIN(student_additional_fees_id), "
                    + "SUM(amount), "
                    + "MIN(issued_date) "
                    + "FROM student_additional_fees "
                    + "WHERE student_id = ? AND status = 1 "
                    + "GROUP BY fee_type_id"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            for (Object[] row : issuedList) {

                int feeTypeId = Integer.parseInt(row[0].toString());
                int additionalFeeId = Integer.parseInt(row[1].toString());
                double totalAmount = Double.parseDouble(row[2].toString());

                // ✅ DATE FIX (DATETIME → DATE)
                String issuedDate = row[3] != null
                        ? row[3].toString().split(" ")[0]
                        : "";

                // ================================
                // STEP 2: GET PAID AMOUNT
                // ================================
                Double totalPaid = (Double) em.createNativeQuery(
                        "SELECT COALESCE(SUM(p.amount_paid),0) "
                        + "FROM student_additional_fee_payments p "
                        + "JOIN student_additional_fees saf "
                        + "ON p.student_additional_fees_id = saf.student_additional_fees_id "
                        + "WHERE saf.fee_type_id = ? "
                        + "AND saf.student_id = ? "
                        + "AND p.status = 1"
                )
                        .setParameter(1, feeTypeId)
                        .setParameter(2, studentId)
                        .getSingleResult();

                if (totalPaid == null) {
                    totalPaid = 0.0;
                }

                // ================================
                // STEP 3: GET FEE DETAILS
                // ================================
                Object[] feeData = (Object[]) em.createNativeQuery(
                        "SELECT fee_name, item_id "
                        + "FROM fee_types "
                        + "WHERE fee_type_id = ?"
                )
                        .setParameter(1, feeTypeId)
                        .getSingleResult();

                String feeName = feeData[0].toString();

                int itemId = 0;
                if (feeData[1] != null) {
                    try {
                        itemId = Integer.parseInt(feeData[1].toString());
                    } catch (Exception e) {
                        itemId = 0;
                    }
                }

                // ================================
                // STEP 4: CALCULATE BALANCE
                // ================================
                double balance = totalAmount - totalPaid;
                double chequePending = getPendingChequeForAdditional(additionalFeeId);

                double finalDue = balance - chequePending;
                if (finalDue < 0) {
                    finalDue = 0;
                }

                if (balance <= 0) {
                    continue;
                }

                // ================================
                // STEP 5: CATEGORY
                // ================================
                String category = (itemId == 0) ? "SERVICE" : "INVENTORY";

                // ================================
                // STEP 6: ADD TO TABLE
                // ================================
                model.addRow(new Object[]{
                    count++,
                    category,
                    issuedDate,
                    feeName,
                    1,
                    GeneralMethods.formatWithComma(totalAmount),
                    GeneralMethods.formatWithComma(totalPaid),
                    GeneralMethods.formatWithComma(chequePending), // ✅ CHEQUE
                    GeneralMethods.formatWithComma(finalDue), // ✅ DUE
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
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//
//            // ✅ STEP 1: GET SUMMED ISSUED AMOUNT PER fee_type_id
//            List<Object[]> issuedList = em.createNativeQuery(
//                    "SELECT fee_type_id, "
//                    + "MIN(student_additional_fees_id), "
//                    + "SUM(amount) "
//                    + "FROM student_additional_fees "
//                    + "WHERE student_id = ? AND status = 1 "
//                    + "GROUP BY fee_type_id"
//            )
//                    .setParameter(1, studentId)
//                    .getResultList();
        ////            List<Object[]> issuedList = em.createNativeQuery(
////                    "SELECT fee_type_id, student_additional_fees_id, SUM(amount) "
////                    + "FROM student_additional_fees "
////                    + "WHERE student_id = ? AND status = 1 "
////                    + "GROUP BY fee_type_id"
////            )
////                    .setParameter(1, studentId)
////                    .getResultList();
//
//            for (Object[] row : issuedList) {
//
//                int feeTypeId = Integer.parseInt(row[0].toString());
//                int additionalFeeId = Integer.parseInt(row[1].toString());
//                double totalIssued = Double.parseDouble(row[2].toString());
//
//                // ================================
//                // STEP 2: GET PAID AMOUNT
//                // ================================
//                Double totalPaid = (Double) em.createNativeQuery(
//                        "SELECT COALESCE(SUM(p.amount_paid),0) "
//                        + "FROM student_additional_fee_payments p "
//                        + "JOIN student_additional_fees saf "
//                        + "ON p.student_additional_fees_id = saf.student_additional_fees_id "
//                        + "WHERE saf.fee_type_id = ? "
//                        + "AND saf.student_id = ? "
//                        + "AND p.status = 1"
//                )
//                        .setParameter(1, feeTypeId)
//                        .setParameter(2, studentId)
//                        .getSingleResult();
//
//                if (totalPaid == null) {
//                    totalPaid = 0.0;
//                }
//
//                // ================================
//                // STEP 3: GET FEE DETAILS
//                // ================================
//                Object[] feeData = (Object[]) em.createNativeQuery(
//                        "SELECT fee_name, item_id "
//                        + "FROM fee_types "
//                        + "WHERE fee_type_id = ?"
//                )
//                        .setParameter(1, feeTypeId)
//                        .getSingleResult();
//
//                String feeName = feeData[0].toString();
//
//                int itemId = 0;
//                if (feeData[1] != null) {
//                    try {
//                        itemId = Integer.parseInt(feeData[1].toString());
//                    } catch (Exception e) {
//                        itemId = 0;
//                    }
//                }
//
//                // ================================
//                // STEP 4: CALCULATE BALANCE
//                // ================================
//                double balance = totalIssued - totalPaid;
//
//                if (balance <= 0) {
//                    continue;
//                }
//
//                // ================================
//                // STEP 5: CATEGORY
//                // ================================
//                String category = (itemId == 0) ? "SERVICE" : "INVENTORY";
//
//                // ================================
//                // STEP 6: ADD TO TABLE
//                // ================================
//                model.addRow(new Object[]{
//                    count++,
//                    category,
//                    feeName,
//                    1,
//                    GeneralMethods.formatWithComma(balance),
//                    "",
//                    false,
//                    //                    "ADD_" + feeTypeId + "F" + additionalFeeId // ✅ grouped by fee_type_id
//                    "ADD_" + additionalFeeId
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }

        sortTableByDueAmount(model);
    }

//    public void loadCourseDuesToTable(int studentId) {
//
//        DefaultTableModel model = (DefaultTableModel) rp_due_table.getModel();
//        model.setRowCount(0);
//
//        int count = 1;
//
//        // =========================================
//        // 1. COURSE DUES
//        // =========================================
//        StudentAdditionalFeesDAO dao = new StudentAdditionalFeesDAO();
//        List<Object[]> courseList = dao.getStudentCourseDues(studentId);
//
//        for (Object[] row : courseList) {
//
//            int enrollmentId = Integer.parseInt(row[0].toString());
//            String courseName = row[1].toString();
//            String courseType = row[2].toString();
//            double balance = GeneralMethods.parseCommaNumber(row[3].toString());
//            String p_date = row[4] != null ? row[4].toString().split(" ")[0] : "";
//            double tot_amount = GeneralMethods.parseCommaNumber(row[5].toString());
//            double tot_paid = GeneralMethods.parseCommaNumber(row[6].toString());
//
//            String name = courseName + " (" + courseType + ")";
//
//            int qty = 1;
//
//            if ("MONTHLY".equalsIgnoreCase(courseType)) {
//                qty = getPendingMonthCount(enrollmentId);
//                if (qty == 0) {
//                    qty = 1;
//                }
//            }
//
//            model.addRow(new Object[]{
//                count++,
//                "COURSE",
//                p_date,
//                name,
//                qty,
//                GeneralMethods.formatWithComma(tot_amount),
//                GeneralMethods.formatWithComma(tot_paid),
//                "",
//                GeneralMethods.formatWithComma(balance),
//                "",
//                false,
//                "COURSE_" + enrollmentId
//            });
//        }
//
//        // =========================================
    //// 2. ADDITIONAL + INVENTORY DUES (FINAL LOGIC)
//// =========================================
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//
//            List<Object[]> issuedList = em.createNativeQuery(
//                    "SELECT fee_type_id, "
//                    + "MIN(student_additional_fees_id), "
//                    + "SUM(amount), "
//                    + "MIN(issued_date) "
//                    + "FROM student_additional_fees "
//                    + "WHERE student_id = ? AND status = 1 "
//                    + "GROUP BY fee_type_id"
//            )
//                    .setParameter(1, studentId)
//                    .getResultList();
//
//            for (Object[] row : issuedList) {
//
//                int feeTypeId = Integer.parseInt(row[0].toString());
//                int additionalFeeId = Integer.parseInt(row[1].toString());
//                double totalAmount = Double.parseDouble(row[2].toString());
//
//                // ✅ DATE FIX (DATETIME → DATE)
//                String issuedDate = row[3] != null
//                        ? row[3].toString().split(" ")[0]
//                        : "";
//
//                // ================================
//                // STEP 2: GET PAID AMOUNT
//                // ================================
//                Double totalPaid = (Double) em.createNativeQuery(
//                        "SELECT COALESCE(SUM(p.amount_paid),0) "
//                        + "FROM student_additional_fee_payments p "
//                        + "JOIN student_additional_fees saf "
//                        + "ON p.student_additional_fees_id = saf.student_additional_fees_id "
//                        + "WHERE saf.fee_type_id = ? "
//                        + "AND saf.student_id = ? "
//                        + "AND p.status = 1"
//                )
//                        .setParameter(1, feeTypeId)
//                        .setParameter(2, studentId)
//                        .getSingleResult();
//
//                if (totalPaid == null) {
//                    totalPaid = 0.0;
//                }
//
//                // ================================
//                // STEP 3: GET FEE DETAILS
//                // ================================
//                Object[] feeData = (Object[]) em.createNativeQuery(
//                        "SELECT fee_name, item_id "
//                        + "FROM fee_types "
//                        + "WHERE fee_type_id = ?"
//                )
//                        .setParameter(1, feeTypeId)
//                        .getSingleResult();
//
//                String feeName = feeData[0].toString();
//
//                int itemId = 0;
//                if (feeData[1] != null) {
//                    try {
//                        itemId = Integer.parseInt(feeData[1].toString());
//                    } catch (Exception e) {
//                        itemId = 0;
//                    }
//                }
//
//                // ================================
//                // STEP 4: CALCULATE BALANCE
//                // ================================
//                double balance = totalAmount - totalPaid;
//
//                if (balance <= 0) {
//                    continue;
//                }
//
//                // ================================
//                // STEP 5: CATEGORY
//                // ================================
//                String category = (itemId == 0) ? "SERVICE" : "INVENTORY";
//
//                // ================================
//                // STEP 6: ADD TO TABLE
//                // ================================
//                model.addRow(new Object[]{
//                    count++,
//                    category,
//                    issuedDate, // ✅ DATE COLUMN
//                    feeName, // SERVICE NAME
//                    1, // QTY
//                    GeneralMethods.formatWithComma(totalAmount), // AMOUNT
//                    GeneralMethods.formatWithComma(totalPaid), // PAID
//                    "", // CHEQUE
//                    GeneralMethods.formatWithComma(balance), // DUE
//                    "", // PAYABLE
//                    false,
//                    "ADD_" + additionalFeeId
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
////        EntityManager em = HibernateConfig.getEntityManager();
////
////        try {
////
////            // ✅ STEP 1: GET SUMMED ISSUED AMOUNT PER fee_type_id
////            List<Object[]> issuedList = em.createNativeQuery(
////                    "SELECT fee_type_id, "
////                    + "MIN(student_additional_fees_id), "
////                    + "SUM(amount) "
////                    + "FROM student_additional_fees "
////                    + "WHERE student_id = ? AND status = 1 "
////                    + "GROUP BY fee_type_id"
////            )
////                    .setParameter(1, studentId)
////                    .getResultList();
//        ////            List<Object[]> issuedList = em.createNativeQuery(
//////                    "SELECT fee_type_id, student_additional_fees_id, SUM(amount) "
//////                    + "FROM student_additional_fees "
//////                    + "WHERE student_id = ? AND status = 1 "
//////                    + "GROUP BY fee_type_id"
//////            )
//////                    .setParameter(1, studentId)
//////                    .getResultList();
////
////            for (Object[] row : issuedList) {
////
////                int feeTypeId = Integer.parseInt(row[0].toString());
////                int additionalFeeId = Integer.parseInt(row[1].toString());
////                double totalIssued = Double.parseDouble(row[2].toString());
////
////                // ================================
////                // STEP 2: GET PAID AMOUNT
////                // ================================
////                Double totalPaid = (Double) em.createNativeQuery(
////                        "SELECT COALESCE(SUM(p.amount_paid),0) "
////                        + "FROM student_additional_fee_payments p "
////                        + "JOIN student_additional_fees saf "
////                        + "ON p.student_additional_fees_id = saf.student_additional_fees_id "
////                        + "WHERE saf.fee_type_id = ? "
////                        + "AND saf.student_id = ? "
////                        + "AND p.status = 1"
////                )
////                        .setParameter(1, feeTypeId)
////                        .setParameter(2, studentId)
////                        .getSingleResult();
////
////                if (totalPaid == null) {
////                    totalPaid = 0.0;
////                }
////
////                // ================================
////                // STEP 3: GET FEE DETAILS
////                // ================================
////                Object[] feeData = (Object[]) em.createNativeQuery(
////                        "SELECT fee_name, item_id "
////                        + "FROM fee_types "
////                        + "WHERE fee_type_id = ?"
////                )
////                        .setParameter(1, feeTypeId)
////                        .getSingleResult();
////
////                String feeName = feeData[0].toString();
////
////                int itemId = 0;
////                if (feeData[1] != null) {
////                    try {
////                        itemId = Integer.parseInt(feeData[1].toString());
////                    } catch (Exception e) {
////                        itemId = 0;
////                    }
////                }
////
////                // ================================
////                // STEP 4: CALCULATE BALANCE
////                // ================================
////                double balance = totalIssued - totalPaid;
////
////                if (balance <= 0) {
////                    continue;
////                }
////
////                // ================================
////                // STEP 5: CATEGORY
////                // ================================
////                String category = (itemId == 0) ? "SERVICE" : "INVENTORY";
////
////                // ================================
////                // STEP 6: ADD TO TABLE
////                // ================================
////                model.addRow(new Object[]{
////                    count++,
////                    category,
////                    feeName,
////                    1,
////                    GeneralMethods.formatWithComma(balance),
////                    "",
////                    false,
////                    //                    "ADD_" + feeTypeId + "F" + additionalFeeId // ✅ grouped by fee_type_id
////                    "ADD_" + additionalFeeId
////                });
////            }
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        } finally {
////            em.close();
////        }
//
//        sortTableByDueAmount(model);
//    }

    private void sortTableByDueAmount(DefaultTableModel model) {

        java.util.List<Object[]> rows = new java.util.ArrayList<>();

        // Collect rows
        for (int i = 0; i < model.getRowCount(); i++) {

            Object[] row = new Object[model.getColumnCount()];

            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j);
            }

            rows.add(row);
        }

        // Sort by Due Amount (column 4)
        rows.sort((a, b) -> {
            double valA = GeneralMethods.parseCommaNumber(a[8].toString());
            double valB = GeneralMethods.parseCommaNumber(b[8].toString());
            return Double.compare(valA, valB);
        });

        // Clear table
        model.setRowCount(0);

        // Re-add sorted rows with new numbering
        int count = 1;

        for (Object[] row : rows) {
            row[0] = count++; // reset serial no
            model.addRow(row);
        }
    }

    public int getPendingMonthCount(int enrollmentId) {

        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
        StudentFeeInstallmentsDAO.MonthDataDTO data = dao.getMonthData(enrollmentId);

        int pendingCount = 0;

        int y = data.startYear;
        int m = data.startMonth;

        while (true) {

            String monthStr = String.format("%02d", m);
            String full = y + "-" + monthStr;

            // ❌ NOT PAID → NOT IN MAP
            if (!data.monthAmountMap.containsKey(full)) {
                pendingCount++;
            }

            // stop condition
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
    }

    private void calculateTotal() {

        DefaultTableModel model = (DefaultTableModel) rp_due_table.getModel();

        double total = 0.0;
        double tot_chq = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object value = GeneralMethods.parseCommaNumber(model.getValueAt(i, 8).toString());
            Object value_chq = GeneralMethods.parseCommaNumber(model.getValueAt(i, 7).toString());

            if (value != null && !value.toString().isEmpty()) {
                total += Double.parseDouble(String.valueOf(value));
            }
            
            if (value_chq != null && !value_chq.toString().isEmpty()) {
                tot_chq += Double.parseDouble(String.valueOf(value_chq));
            }
        }

        rp_total_due_text.setText(GeneralMethods.formatWithComma(total));
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
            master.setPaymentMode(paymentMode);
            master.setTotalPaid(totalPaid);
            master.setRoundingAdjustment(roundingAdj);
            master.setUser(user);
            master.setStatus(1);

            em.persist(master);
            em.flush();

            System.out.println("MASTER ID: " + master.getStudentFeeRoundPaymentMasterId());
            Integer roundMasterId = master.getStudentFeeRoundPaymentMasterId();

            System.out.println("ROUND MASTER ID: " + roundMasterId);

            // =========================
            // CHEQUE MASTER SAVE (ONLY ONCE)
            // =========================
            if ("CHEQUE".equalsIgnoreCase(paymentMode)) {

                em.createNativeQuery(
                        "INSERT INTO student_fee_cheque_details "
                        + "(reference_id, reference_type, category, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
                        + "VALUES (?, 'ROUND', ?, ?, ?, ?, ?, ?, 'PENDING', 1)"
                )
                        .setParameter(1, roundMasterId)
                        .setParameter(2, "STUDENT")
                        .setParameter(3, rp_round_cheque_number_text.getText())
                        .setParameter(4, rp_round_bank_name_combo.getEditor().getItem().toString())
                        .setParameter(5, rp_round_cheque_branch.getText())
                        .setParameter(6, rp_round_cheque_date.getDate())
                        .setParameter(7, totalPaid)
                        .executeUpdate();

                System.out.println("CHEQUE SAVED FOR ROUND MASTER ID: " + roundMasterId);
            }

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
                                + "(student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
                                + "payment_date, payment_method, payment_type, remarks, status) "
                                + "VALUES (?, ?, ?, ?, NOW(), ?, 'ROUND', 'Round Payment', 1)"
                        )
                                .setParameter(1, paymentId)
                                .setParameter(2, enrollmentId)
                                .setParameter(3, nextInstallmentNo)
                                .setParameter(4, payableAmount)
                                .setParameter(5, paymentMode)
                                .executeUpdate();

                    } // =========================
                    // MONTHLY COURSE SUPPORT (IMPORTANT ADDITION)
                    // =========================
                    else if (serviceName.toUpperCase().contains("MONTHLY")) {

                        System.out.println("\n========== MONTHLY ROUND PAYMENT DEBUG ==========");

                        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
                        StudentFeeInstallmentsDAO.MonthDataDTO dto = dao.getMonthData(enrollmentId);

                        System.out.println("START YEAR   : " + dto.startYear);
                        System.out.println("START MONTH  : " + dto.startMonth);
                        System.out.println("END YEAR     : " + dto.endYear);
                        System.out.println("END MONTH    : " + dto.endMonth);

                        // =====================================================
                        // FIX 1: GET TOTAL FEE
                        // =====================================================
                        double totalFee = ((Number) em.createNativeQuery(
                                "SELECT total_fee FROM student_fee_payments WHERE enrollment_id=? AND status=1"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult()).doubleValue();

                        // =====================================================
                        // FIX 2: CALCULATE MONTH COUNT
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

                        System.out.println("TOTAL FEE     : " + totalFee);
                        System.out.println("TOTAL MONTHS  : " + totalMonths);
                        System.out.println("MONTHLY FEE   : " + monthlyFee);

                        if (monthlyFee <= 0) {
                            System.out.println("❌ STOP: Monthly fee is 0 or invalid");
                            return;
                        }

                        // =====================================================
                        // DTO DEBUG
                        // =====================================================
                        System.out.println("MONTH MAP SIZE: " + dto.monthAmountMap.size());
                        System.out.println("MONTH MAP     : " + dto.monthAmountMap);

                        // LAST PAID MONTH
                        String lastPaidMonth = dto.monthAmountMap.keySet()
                                .stream()
                                .max(String::compareTo)
                                .orElse(null);

                        System.out.println("LAST PAID MONTH: " + lastPaidMonth);

                        int pendingMonths = getPendingMonthCount(enrollmentId);
                        System.out.println("PENDING MONTH COUNT: " + pendingMonths);

                        Number paymentRow = (Number) em.createNativeQuery(
                                "SELECT student_fee_payments_id FROM student_fee_payments "
                                + "WHERE enrollment_id=? AND status=1"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult();

                        int paymentId = paymentRow.intValue();

                        int installmentNo = ((Number) em.createNativeQuery(
                                "SELECT COALESCE(MAX(installment_no),0) "
                                + "FROM student_fee_installments WHERE enrollment_id=?"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult()).intValue();

                        double remainingAmount = payableAmount;

                        int y = dto.startYear;
                        int m = dto.startMonth;

                        System.out.println("\n========== DISTRIBUTION START ==========");

                        while (remainingAmount > 0) {

                            if (y > dto.endYear || (y == dto.endYear && m > dto.endMonth)) {
                                System.out.println("STOP: reached end of course range");
                                break;
                            }

                            String monthKey = String.format("%04d-%02d", y, m);

                            double alreadyPaid = dto.monthAmountMap.getOrDefault(monthKey, 0);
                            double balance = monthlyFee - alreadyPaid;

                            System.out.println("MONTH: " + monthKey);
                            System.out.println("   Already Paid : " + alreadyPaid);
                            System.out.println("   Monthly Fee   : " + monthlyFee);
                            System.out.println("   Balance       : " + balance);
                            System.out.println("   Remaining     : " + remainingAmount);

                            if (balance > 0) {

                                double payNow = Math.min(balance, remainingAmount);

                                System.out.println(">>> PAYING : " + payNow);

                                installmentNo++;

                                int inserted = em.createNativeQuery(
                                        "INSERT INTO student_fee_installments "
                                        + "(student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
                                        + "payment_date, payment_method, payment_type, month_for, remarks, status) "
                                        + "VALUES (?, ?, ?, ?, NOW(), ?, 'ROUND', ?, 'Round Monthly Payment', 1)"
                                )
                                        .setParameter(1, paymentId)
                                        .setParameter(2, enrollmentId)
                                        .setParameter(3, installmentNo)
                                        .setParameter(4, payNow)
                                        .setParameter(5, paymentMode)
                                        .setParameter(6, monthKey)
                                        .executeUpdate();

                                System.out.println("INSERT RESULT : " + inserted);

                                // IMPORTANT: update map to avoid duplicate allocation in same run
//                                dto.monthAmountMap.put(monthKey, alreadyPaid + payNow);
                                double sum = alreadyPaid + payNow;
                                int updatedAmount = (int) sum;
                                dto.monthAmountMap.put(monthKey, updatedAmount);

                                remainingAmount -= payNow;
                            }

                            m++;
                            if (m > 12) {
                                m = 1;
                                y++;
                            }
                        }

                        // =====================================================
                        // FINAL CALCULATION (FIXED)
                        // =====================================================
                        double totalPaids = ((Number) em.createNativeQuery(
                                "SELECT COALESCE(SUM(amount_paid),0) "
                                + "FROM student_fee_installments WHERE enrollment_id=?"
                        )
                                .setParameter(1, enrollmentId)
                                .getSingleResult()).doubleValue();

                        double finalTotalFee = totalFee; // IMPORTANT FIX

                        System.out.println("\n========== FINAL UPDATE ==========");
                        System.out.println("TOTAL PAID  : " + totalPaids);
                        System.out.println("TOTAL FEE   : " + finalTotalFee);

                        em.createNativeQuery(
                                "UPDATE student_fee_payments "
                                + "SET total_paid=?, total_balance=?, payment_status=? "
                                + "WHERE enrollment_id=?"
                        )
                                .setParameter(1, totalPaids)
                                .setParameter(2, finalTotalFee - totalPaids)
                                .setParameter(3, totalPaids >= finalTotalFee ? "COMPLETED" : "ACTIVE")
                                .setParameter(4, enrollmentId)
                                .executeUpdate();

                        System.out.println("REMAINING AFTER LOOP: " + remainingAmount);
                        System.out.println("========== MONTHLY ROUND END ==========\n");
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
                            + "(student_additional_fees_id, paid_date, amount_paid, payment_method, user, status) "
                            + "VALUES (?, NOW(), ?, ?, ?, 1)"
                    )
                            .setParameter(1, safId)
                            .setParameter(2, payableAmount)
                            .setParameter(3, paymentMode)
                            .setParameter(4, user)
                            .executeUpdate();

                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
                    d.setStudentFeeRoundPaymentMaster(master);
                    d.setEnrollmentId(null);
                    d.setReferenceId(safId);
                    d.setReferenceType("ADDITIONAL FEE");
                    d.setPaidAmount(payableAmount);
                    d.setStatus(1);

                    em.persist(d);
                }
            }

            tx.commit();
            System.out.println("✅ ROUND PAYMENT FULLY SAVED");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void saveRoundPaymentCheque(int studentId, JTable table,
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
            // 1. MASTER (ROUND)
            // =========================
            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
            master.setStudentId(studentId);
            master.setPaymentDate(new java.util.Date());
            master.setPaymentMode("CHEQUE"); // force cheque
            master.setTotalPaid(totalPaid);
            master.setRoundingAdjustment(roundingAdj);
            master.setUser(user);
            master.setStatus(1);

            em.persist(master);
            em.flush();

            Integer roundMasterId = master.getStudentFeeRoundPaymentMasterId();

            System.out.println("ROUND MASTER ID: " + roundMasterId);

            // =========================
            // 2. SAVE CHEQUE (ONLY ONCE)
            // =========================
            em.createNativeQuery(
                    "INSERT INTO student_fee_cheque_details "
                    + "(reference_id, reference_type, category, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
                    + "VALUES (?, 'ROUND', ?, ?, ?, ?, ?, ?, 'PENDING', 1)"
            )
                    .setParameter(1, roundMasterId)
                    .setParameter(2, "STUDENT")
                    .setParameter(3, rp_round_cheque_number_text.getText())
                    .setParameter(4, rp_round_bank_name_combo.getEditor().getItem().toString())
                    .setParameter(5, rp_round_cheque_branch.getText())
                    .setParameter(6, rp_round_cheque_date.getDate())
                    .setParameter(7, totalPaid)
                    .executeUpdate();

            System.out.println("✅ CHEQUE SAVED (PENDING)");

            // =========================
            // 3. LOOP TABLE (DETAILS ONLY)
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

                // =========================
                // COURSE
                // =========================
                if (ref.startsWith("COURSE_")) {

                    int enrollmentId = Integer.parseInt(ref.replace("COURSE_", ""));

                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
                    d.setStudentFeeRoundPaymentMaster(master);
                    d.setEnrollmentId(enrollmentId);
                    d.setReferenceType("COURSE");
                    d.setPaidAmount(payableAmount);
                    d.setStatus(1);

                    em.persist(d);

                    System.out.println("COURSE DETAIL SAVED: " + enrollmentId + " | AMOUNT: " + payableAmount);
                } // =========================
                // ADDITIONAL / INVENTORY
                // =========================
                else if (ref.startsWith("ADD_")) {

                    int safId = Integer.parseInt(ref.replace("ADD_", ""));

                    StudentFeeRoundPaymentMasterDetails d = new StudentFeeRoundPaymentMasterDetails();
                    d.setStudentFeeRoundPaymentMaster(master);
                    d.setEnrollmentId(null);
                    d.setReferenceId(safId);
                    d.setReferenceType("ADDITIONAL FEE");
                    d.setPaidAmount(payableAmount);
                    d.setStatus(1);

                    em.persist(d);

                    System.out.println("ADDITIONAL DETAIL SAVED: " + safId + " | AMOUNT: " + payableAmount);
                }
            }

            tx.commit();

            System.out.println("✅ CHEQUE ROUND PAYMENT SAVED (NO FINANCIAL IMPACT)");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private double getPendingChequeForCourse(int enrollmentId) {

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
                    + "AND d.reference_type = 'COURSE' "
                    + "AND d.enrollment_id = ?"
            )
                    .setParameter(1, enrollmentId)
                    .getSingleResult();

            return amount == null ? 0 : amount;

        } finally {
            em.close();
        }
    }

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
        rp_student_name_combo = new javax.swing.JComboBox<>();
        rp_date = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        rp_due_table = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        rp_round_calculate_text = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelRound2.setBackground(new java.awt.Color(247, 178, 50));
        panelRound2.setRoundBottomLeft(10);
        panelRound2.setRoundBottomRight(10);
        panelRound2.setRoundTopLeft(10);
        panelRound2.setRoundTopRight(10);

        Main_Lable.setFont(new java.awt.Font("Roboto Black", 3, 14)); // NOI18N
        Main_Lable.setForeground(new java.awt.Color(255, 255, 255));
        Main_Lable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Main_Lable.setText("ROUND PAYMENT");

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

        rp_student_name_combo.setEditable(true);
        rp_student_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

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
            rp_due_table.getColumnModel().getColumn(11).setMinWidth(120);
            rp_due_table.getColumnModel().getColumn(11).setPreferredWidth(120);
            rp_due_table.getColumnModel().getColumn(11).setMaxWidth(120);
        }

        jLabel11.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel11.setText("Calculate Payment");

        rp_round_calculate_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
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
                            .addComponent(rp_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rp_round_calculate_text, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2)))
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
                            .addComponent(rp_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_round_calculate_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                                .addGap(18, 18, 18)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rp_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(firstName_label9, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rp_round_total_pending_cheque_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(firstName_label8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rp_round_remaining_bal_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
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
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rp_round_remaining_bal_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(firstName_label8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_round_total_pending_cheque_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(firstName_label9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(41, 41, 41))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rp_round_remaining_bal_text, rp_round_total_pending_cheque_text});

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
        String paymentMode = rp_round_payement_method_combo.getSelectedItem().toString();
        double totPaid = GeneralMethods.parseCommaNumber(rp_round_total_pay_cash_text.getText());
        saveRoundPaymentCheque(selectedStudentIds, rp_due_table, paymentMode, totPaid, 0.00, username);
    }//GEN-LAST:event_buttonGradient5ActionPerformed

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
    private javax.swing.JTextField rp_round_total_pay_cash_text;
    public static javax.swing.JTextField rp_round_total_pending_cheque_text;
    private javax.swing.JComboBox<String> rp_student_name_combo;
    private javax.swing.JTextField rp_total_due_text;
    // End of variables declaration//GEN-END:variables

}
