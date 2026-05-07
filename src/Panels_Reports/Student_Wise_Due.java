package Panels_Reports;

import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Pagination.EventPagination;
import Pagination.PaginationItemRenderStyle1;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Student_Wise_Due extends javax.swing.JPanel {

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String currentSelectedClass = "";
    String username;
    String role;

    public Student_Wise_Due(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        swd_st_table.setDefaultRenderer(Object.class, new TableGradientCell());
        swd_st_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");
        swd_st_table.setRowHeight(30);

        swd_st_table.getTableHeader().setPreferredSize(
                new Dimension(
                        swd_st_table.getTableHeader().getPreferredSize().width,
                        35
                )
        );

        loadClassCombo();
        JComboPopulates();

        pagination1.setPaginationItemRender(new PaginationItemRenderStyle1());
        pagination1.addEventPagination(new EventPagination() {
            @Override
            public void pageChanged(int page) {
                loadStudentsWithDue(swd_st_table, swd_st_con_batch_combo, swd_st_con_class_combo, page);
            }
        });

    }

    private void JComboPopulates() {
        // Medicine brand combo
        swd_st_con_batch_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = swd_st_con_batch_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(swd_st_con_batch_combo, "course_id", "batch", "course", input);
            }

        });
        setupComboSelectionListener(swd_st_con_batch_combo, swd_st_con_batch_combo);

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

    public void loadClassCombo() {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            if (swd_st_con_class_combo == null) {
                System.out.println("btc_st_class_combo is null");
                return;
            }

            swd_st_con_class_combo.removeAllItems();

            // Default item
            // btc_st_class_combo.addItem("Select Class");
            List<Object[]> list = em.createNativeQuery(
                    "SELECT class_name "
                    + "FROM student_class "
                    + "WHERE status = 1 "
                    + "ORDER BY class_name ASC"
            ).getResultList();

            System.out.println("Class Count = " + list.size());

            for (Object row : list) {

                String className = row != null ? row.toString() : "";

                if (!className.trim().isEmpty()) {
                    swd_st_con_class_combo.addItem(className);

                }
            }

            swd_st_con_class_combo.revalidate();
            swd_st_con_class_combo.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void loadStudentsWithDue(JTable table,
            JComboBox<String> swd_st_con_batch_combo,
            JComboBox<String> swd_st_con_class_combo,
            int page) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            // ================= COURSE ID =================
            String selectedBatch = swd_st_con_batch_combo.getSelectedItem() != null
                    ? swd_st_con_batch_combo.getSelectedItem().toString()
                    : "";

            Integer courseId = null;

            if (selectedBatch.contains("[") && selectedBatch.contains("]")) {
                courseId = Integer.parseInt(
                        selectedBatch.substring(
                                selectedBatch.lastIndexOf("[") + 1,
                                selectedBatch.lastIndexOf("]")
                        ).trim()
                );

                try {
                    Object courseNameObj = em.createNativeQuery(
                            "SELECT course_name FROM course WHERE course_id=? AND status=1"
                    ).setParameter(1, courseId).getSingleResult();

                    swd_st_con_course_text.setText(
                            courseNameObj != null ? courseNameObj.toString() : ""
                    );
                } catch (Exception ex) {
                    swd_st_con_course_text.setText("");
                }

            } else {
                swd_st_con_course_text.setText("");
            }

            // ================= CLASS FILTER =================
            String selectedClass = swd_st_con_class_combo.getSelectedItem() != null
                    ? swd_st_con_class_combo.getSelectedItem().toString()
                    : "";

            boolean filterClass = selectedClass != null
                    && !selectedClass.trim().isEmpty()
                    && !selectedClass.equalsIgnoreCase("Select Class");

            int limit = 14;
            int offset = (page - 1) * limit;

            // ================= COUNT QUERY =================
            String countSql = "SELECT COUNT(DISTINCT sfp.student_id) "
                    + "FROM student_fee_payments sfp "
                    + "INNER JOIN course_enrollment ce ON sfp.enrollment_id = ce.enrollment_id "
                    + "WHERE sfp.total_balance > 0 "
                    + "AND sfp.status = 1 "
                    + "AND ce.status = 1 ";

            if (courseId != null) {
                countSql += "AND ce.course_id = ? ";
            }

            if (filterClass) {
                countSql += "AND ce.class_name = ? ";
            }

            Query countQuery = em.createNativeQuery(countSql);

            int paramIndex = 1;

            if (courseId != null) {
                countQuery.setParameter(paramIndex++, courseId);
            }

            if (filterClass) {
                countQuery.setParameter(paramIndex++, selectedClass);
            }

            int totalRows = ((Number) countQuery.getSingleResult()).intValue();

            lbl_total_rows.setText("Total : " + totalRows + " Records");

            int totalPages = (int) Math.ceil((double) totalRows / limit);
            pagination1.setPagegination(page, totalPages);

            // ================= MAIN QUERY =================
            String sql = "SELECT "
                    + "s.student_id, "
                    + "MAX(s.admission_no), "
                    + "MAX(s.full_name), "
                    + "MAX(s.contact_no), "
                    + "MAX(sp.father_contact), "
                    // 🔥 TOTAL PAID (ALL ENROLLMENTS)
                    + "IFNULL(SUM(sfp.total_paid),0), "
                    // 🔥 TOTAL BALANCE (ALL ENROLLMENTS)
                    + "IFNULL(SUM(sfp.total_balance),0), "
                    // 🔥 CHEQUE (ALL ENROLLMENTS)
                    + "IFNULL((SELECT SUM(cd.cheque_amount) "
                    + "FROM student_fee_cheque_details cd "
                    + "INNER JOIN student_fee_round_payment_master m "
                    + "ON cd.reference_id = m.student_fee_round_payment_master_id "
                    + "WHERE m.student_id = s.student_id "
                    + "AND cd.cheque_status='PENDING' "
                    + "AND cd.category='STUDENT'),0), "
                    // 🔥 ADJUSTMENT + CREDIT
                    + "IFNULL((SELECT SUM(fa.adjustment_amount) "
                    + "FROM fee_adjustment fa "
                    + "WHERE fa.student_id = s.student_id AND fa.status=1),0), "
                    + "IFNULL((SELECT SUM(fa.credit_amount) "
                    + "FROM fee_adjustment fa "
                    + "WHERE fa.student_id = s.student_id AND fa.status=1),0), "
                    // 🔥 ADDITIONAL BALANCE
                    + "IFNULL((SELECT SUM(saf.amount) "
                    + "FROM student_additional_fees saf "
                    + "WHERE saf.student_id = s.student_id AND saf.status=1),0) "
                    + "- IFNULL((SELECT SUM(p.amount_paid) "
                    + "FROM student_additional_fee_payments p "
                    + "INNER JOIN student_additional_fees saf2 "
                    + "ON p.student_additional_fees_id = saf2.student_additional_fees_id "
                    + "WHERE saf2.student_id = s.student_id AND p.status=1),0) "
                    + "FROM student_fee_payments sfp "
                    + "INNER JOIN course_enrollment ce ON sfp.enrollment_id = ce.enrollment_id "
                    + "INNER JOIN student s ON sfp.student_id = s.student_id "
                    + "LEFT JOIN student_parents sp ON s.student_parents_id = sp.student_parents_id "
                    + "WHERE sfp.total_balance > 0 "
                    + "AND sfp.status = 1 "
                    + "AND ce.status = 1 ";

            if (courseId != null) {
                sql += "AND ce.course_id = ? ";
            }

            if (filterClass) {
                sql += "AND ce.class_name = ? ";
            }

            sql += "GROUP BY s.student_id ";
            sql += "ORDER BY MAX(s.admission_no) ASC LIMIT ? OFFSET ?";

            Query query = em.createNativeQuery(sql);

            paramIndex = 1;

            if (courseId != null) {
                query.setParameter(paramIndex++, courseId);
            }

            if (filterClass) {
                query.setParameter(paramIndex++, selectedClass);
            }

            query.setParameter(paramIndex++, limit);
            query.setParameter(paramIndex, offset);

            List<Object[]> list = query.getResultList();

            int rowNo = offset + 1;

            // ================= LOAD TABLE =================
            for (Object[] row : list) {

                int studentId = ((Number) row[0]).intValue();

                String admissionNo = row[1] != null ? row[1].toString() : "";
                String name = row[2] != null ? row[2].toString() : "";
                String contact = row[3] != null ? row[3].toString() : "";
                String fatherContact = row[4] != null ? row[4].toString() : "";

                double totalPaid = ((Number) row[5]).doubleValue();
                double totalBalance = ((Number) row[6]).doubleValue();
                double cheque = ((Number) row[7]).doubleValue();
                double adjustment = ((Number) row[8]).doubleValue();
                double credit = ((Number) row[9]).doubleValue();
                double additionalBalance = ((Number) row[10]).doubleValue();

                double totalPaidWithCheque = totalPaid + cheque;
                double finalDue = totalBalance + additionalBalance;

                model.addRow(new Object[]{
                    rowNo++,
                    admissionNo,
                    name,
                    GeneralMethods.formatWithComma(totalPaid),
                    GeneralMethods.formatWithComma(cheque),
                    GeneralMethods.formatWithComma(totalPaidWithCheque),
                    GeneralMethods.formatWithComma(adjustment),
                    GeneralMethods.formatWithComma(credit),
                    GeneralMethods.formatWithComma(additionalBalance),
                    GeneralMethods.formatWithComma(finalDue),
                    contact,
                    fatherContact
                });
            }

            calculateStudentDueTotals(table);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

//    public void loadStudentsWithDue(JTable table,
//            JComboBox<String> swd_st_con_batch_combo,
//            JComboBox<String> swd_st_con_class_combo,
//            int page) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            DefaultTableModel model = (DefaultTableModel) table.getModel();
//            model.setRowCount(0);
//
//            System.out.println("\n========== DEBUG START ==========");
//
//            // ===================== COURSE =====================
//            String selectedBatch = swd_st_con_batch_combo.getSelectedItem() != null
//                    ? swd_st_con_batch_combo.getSelectedItem().toString()
//                    : "";
//
//            Integer courseId = null;
//
//            if (selectedBatch.contains("[") && selectedBatch.contains("]")) {
//                courseId = Integer.parseInt(
//                        selectedBatch.substring(
//                                selectedBatch.lastIndexOf("[") + 1,
//                                selectedBatch.lastIndexOf("]")
//                        ).trim()
//                );
//            }
//
//            System.out.println("DEBUG → courseId = " + courseId);
//
//            // ===================== CLASS =====================
//            String selectedClass = swd_st_con_class_combo.getSelectedItem() != null
//                    ? swd_st_con_class_combo.getSelectedItem().toString()
//                    : "";
//
//            boolean filterClass = selectedClass != null
//                    && !selectedClass.trim().isEmpty()
//                    && !selectedClass.equalsIgnoreCase("Select Class");
//
//            System.out.println("DEBUG → class = " + selectedClass);
//
//            int limit = 14;
//            int offset = (page - 1) * limit;
//
//            // ===================== DATA QUERY =====================
//            String dataSql = "SELECT "
//                    + "sfp.student_id, "
//                    + "sfp.enrollment_id, "
//                    + "sfp.total_paid, "
//                    + "sfp.total_balance, "
//                    + "s.admission_no, "
//                    + "s.full_name, "
//                    + "s.contact_no, "
//                    + "sp.father_contact "
//                    + "FROM student_fee_payments sfp "
//                    + "INNER JOIN course_enrollment ce ON sfp.enrollment_id = ce.enrollment_id "
//                    + "INNER JOIN student s ON sfp.student_id = s.student_id "
//                    + "LEFT JOIN student_parents sp ON s.student_parents_id = sp.student_parents_id "
//                    + "WHERE sfp.total_balance > 0 "
//                    + "AND sfp.status = 1 "
//                    + "AND ce.status = 1 ";
//
//            if (courseId != null) {
//                dataSql += "AND ce.course_id = ? ";
//            }
//
//            if (filterClass) {
//                dataSql += "AND ce.class_name = ? ";
//            }
//
//            dataSql += "ORDER BY s.admission_no ASC LIMIT ? OFFSET ?";
//
//            System.out.println("DEBUG SQL → " + dataSql);
//
//            Query dataQuery = em.createNativeQuery(dataSql);
//
//            int paramIndex = 1;
//
//            if (courseId != null) {
//                dataQuery.setParameter(paramIndex++, courseId);
//            }
//
//            if (filterClass) {
//                dataQuery.setParameter(paramIndex++, selectedClass);
//            }
//
//            dataQuery.setParameter(paramIndex++, limit);
//            dataQuery.setParameter(paramIndex, offset);
//
//            List<Object[]> list = dataQuery.getResultList();
//
//            System.out.println("DEBUG → DB rows count = " + list.size());
//
//            int rowNo = offset + 1;
//
//            // ===================== LOOP =====================
//            for (Object[] row : list) {
//
//                int studentId = ((Number) row[0]).intValue();
//                int enrollmentId = ((Number) row[1]).intValue();
//
//                double totalPaid = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
//                double totalBalance = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
//
//                System.out.println("\n----- ROW START -----");
//                System.out.println("studentId = " + studentId);
//                System.out.println("enrollmentId = " + enrollmentId);
//                System.out.println("DB totalPaid = " + totalPaid);
//
//                // ================= CHEQUE =================
//                double chequeAmount = 0;
//
//                List<Integer> masterIds = em.createNativeQuery(
//                        "SELECT DISTINCT m.student_fee_round_payment_master_id "
//                        + "FROM student_fee_round_payment_master m "
//                        + "INNER JOIN student_fee_round_payment_master_details d "
//                        + "ON m.student_fee_round_payment_master_id = d.student_fee_round_payment_master_id "
//                        + "WHERE m.student_id=? "
//                        + "AND m.payment_mode='CHEQUE' "
//                        + "AND m.status=1 "
//                        + "AND d.enrollment_id=? "
//                        + "AND d.status=1"
//                )
//                        .setParameter(1, studentId)
//                        .setParameter(2, enrollmentId)
//                        .getResultList();
//
//                System.out.println("masterIds = " + masterIds);
//
//                if (masterIds != null && !masterIds.isEmpty()) {
//
//                    String inClause = masterIds.toString().replace("[", "").replace("]", "");
//
//                    Object result = em.createNativeQuery(
//                            "SELECT IFNULL(SUM(cheque_amount),0) "
//                            + "FROM student_fee_cheque_details "
//                            + "WHERE reference_type='ROUND' "
//                            + "AND category='STUDENT' "
//                            + "AND cheque_status='PENDING' "
//                            + "AND reference_id IN (" + inClause + ")"
//                    ).getSingleResult();
//
//                    chequeAmount = result != null ? ((Number) result).doubleValue() : 0;
//                }
//
//                System.out.println("chequeAmount = " + chequeAmount);
//
//                // ================= ADJUSTMENT =================
//                Object[] adjRow = (Object[]) em.createNativeQuery(
//                        "SELECT IFNULL(SUM(adjustment_amount),0), IFNULL(SUM(credit_amount),0) "
//                        + "FROM fee_adjustment WHERE student_id=? AND status=1"
//                )
//                        .setParameter(1, studentId)
//                        .getSingleResult();
//
//                double totalAdjustment = ((Number) adjRow[0]).doubleValue();
//                double totalCredit = ((Number) adjRow[1]).doubleValue();
//
//                System.out.println("adjustment = " + totalAdjustment);
//                System.out.println("credit = " + totalCredit);
//
//                // ================= ADDITIONAL =================
//                double totalAdditional = ((Number) em.createNativeQuery(
//                        "SELECT IFNULL(SUM(amount),0) FROM student_additional_fees WHERE student_id=? AND status=1"
//                ).setParameter(1, studentId).getSingleResult()).doubleValue();
//
//                double totalAdditionalPaid = ((Number) em.createNativeQuery(
//                        "SELECT IFNULL(SUM(safp.amount_paid),0) "
//                        + "FROM student_additional_fee_payments safp "
//                        + "INNER JOIN student_additional_fees saf "
//                        + "ON safp.student_additional_fees_id = saf.student_additional_fees_id "
//                        + "WHERE saf.student_id=? AND safp.status=1"
//                ).setParameter(1, studentId).getSingleResult()).doubleValue();
//
//                double additionalBalance = totalAdditional - totalAdditionalPaid;
//                if (additionalBalance < 0) {
//                    additionalBalance = 0;
//                }
//
//                System.out.println("additionalBalance = " + additionalBalance);
//
//                double totalPaidWithCheque = totalPaid + chequeAmount;
//
//                System.out.println("FINAL totalPaidWithCheque = " + totalPaidWithCheque);
//                System.out.println("FINAL totalBalance = " + (totalBalance + additionalBalance));
//
//                // ================= ADD ROW =================
//                model.addRow(new Object[]{
//                    rowNo++,
//                    row[4],
//                    row[5],
//                    totalPaid,
//                    chequeAmount,
//                    totalPaidWithCheque,
//                    totalAdjustment,
//                    totalCredit,
//                    additionalBalance,
//                    totalBalance + additionalBalance,
//                    row[6],
//                    row[7]
//                });
//
//                System.out.println("----- ROW END -----");
//            }
//
//            System.out.println("========== DEBUG END ==========\n");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }
    // ******************** NO ALL STUDENT SEARCH. ONLY BATCH WISE
//    public void loadStudentsWithDue(JTable table,
//            JComboBox<String> swd_st_con_batch_combo,
//            JComboBox<String> swd_st_con_class_combo,
//            int page) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            DefaultTableModel model = (DefaultTableModel) table.getModel();
//            model.setRowCount(0);
//
//            // =====================================================
//            // 🔥 GET COURSE ID FROM BATCH COMBO
//            // Format = BatchName [courseId]
//            // =====================================================
//            String selectedBatch = swd_st_con_batch_combo.getSelectedItem() != null
//                    ? swd_st_con_batch_combo.getSelectedItem().toString()
//                    : "";
//
//            if (!selectedBatch.contains("[") || !selectedBatch.contains("]")) {
//                return;
//            }
//
//            int courseId = Integer.parseInt(
//                    selectedBatch.substring(
//                            selectedBatch.lastIndexOf("[") + 1,
//                            selectedBatch.lastIndexOf("]")
//                    ).trim()
//            );
//
//            Object courseNameObj = em.createNativeQuery(
//                    "SELECT course_name FROM course WHERE course_id=? AND status=1"
//            )
//                    .setParameter(1, courseId)
//                    .getSingleResult();
//
//            String courseName = courseNameObj != null ? courseNameObj.toString() : "";
//            swd_st_con_course_text.setText(courseName);
//
//            // =====================================================
//            // 🔥 CLASS FILTER
//            // =====================================================
//            String selectedClass = swd_st_con_class_combo.getSelectedItem() != null
//                    ? swd_st_con_class_combo.getSelectedItem().toString()
//                    : "";
//
//            boolean filterClass = selectedClass != null
//                    && !selectedClass.trim().isEmpty()
//                    && !selectedClass.equalsIgnoreCase("Select Class");
//
//            int limit = 14;
//            int offset = (page - 1) * limit;
//
//            // =====================================================
//            // 🔥 COUNT QUERY
//            // =====================================================
//            String countSql = "SELECT COUNT(*) "
//                    + "FROM student_fee_payments sfp "
//                    + "INNER JOIN course_enrollment ce ON sfp.enrollment_id = ce.enrollment_id "
//                    + "WHERE ce.course_id = ? "
//                    + "AND sfp.total_balance > 0 "
//                    + "AND sfp.status = 1 "
//                    + "AND ce.status = 1 ";
//
//            if (filterClass) {
//                countSql += "AND ce.class_name = ? ";
//            }
//
//            Query countQuery = em.createNativeQuery(countSql);
//            countQuery.setParameter(1, courseId);
//
//            if (filterClass) {
//                countQuery.setParameter(2, selectedClass);
//            }
//
//            int totalRows = ((Number) countQuery.getSingleResult()).intValue();
//
//            lbl_total_rows.setText("Total : " + totalRows + " Records");
//
//            int totalPages = (int) Math.ceil((double) totalRows / limit);
//            pagination1.setPagegination(page, totalPages);
//
//            // =====================================================
//            // 🔥 DATA QUERY
//            // =====================================================
//            String dataSql = "SELECT "
//                    + "sfp.student_id, " // 0
//                    + "sfp.enrollment_id, " // 1
//                    + "sfp.total_paid, " // 2
//                    + "sfp.total_balance, " // 3
//                    + "s.admission_no, " // 4
//                    + "s.full_name, " // 5
//                    + "s.contact_no, " // 6
//                    + "sp.father_contact " // 7
//                    + "FROM student_fee_payments sfp "
//                    + "INNER JOIN course_enrollment ce ON sfp.enrollment_id = ce.enrollment_id "
//                    + "INNER JOIN student s ON sfp.student_id = s.student_id "
//                    + "LEFT JOIN student_parents sp ON s.student_parents_id = sp.student_parents_id "
//                    + "WHERE ce.course_id = ? "
//                    + "AND sfp.total_balance > 0 "
//                    + "AND sfp.status = 1 "
//                    + "AND ce.status = 1 ";
//
//            if (filterClass) {
//                dataSql += "AND ce.class_name = ? ";
//            }
//
//            dataSql += "ORDER BY s.admission_no ASC LIMIT ? OFFSET ?";
//
//            Query dataQuery = em.createNativeQuery(dataSql);
//            dataQuery.setParameter(1, courseId);
//
//            int paramIndex = 2;
//
//            if (filterClass) {
//                dataQuery.setParameter(paramIndex++, selectedClass);
//            }
//
//            dataQuery.setParameter(paramIndex++, limit);
//            dataQuery.setParameter(paramIndex, offset);
//
//            List<Object[]> list = dataQuery.getResultList();
//
//            int rowNo = offset + 1;
//
//            // =====================================================
//            // 🔥 LOAD TABLE
//            // =====================================================
//            for (Object[] row : list) {
//
//                int studentId = ((Number) row[0]).intValue();
//                int enrollmentId = ((Number) row[1]).intValue();
//
//                double totalPaid = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
//                double totalBalance = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
//
//                String admissionNo = row[4] != null ? row[4].toString() : "";
//                String name = row[5] != null ? row[5].toString() : "";
//                String contact = row[6] != null ? row[6].toString() : "";
//                String fatherContact = row[7] != null ? row[7].toString() : "";
//
//                // =====================================================
//                // 🔥 GET PENDING CHEQUE AMOUNT (PER STUDENT)
//                // =====================================================
//                double chequeAmount = 0;
//
//                List<Integer> masterIds = em.createNativeQuery(
//                        "SELECT student_fee_round_payment_master_id "
//                        + "FROM student_fee_round_payment_master "
//                        + "WHERE student_id=? AND payment_mode='CHEQUE' AND status=1"
//                )
//                        .setParameter(1, studentId)
//                        .getResultList();
//
//                if (masterIds != null && !masterIds.isEmpty()) {
//
//                    StringBuilder inClause = new StringBuilder();
//
//                    for (int i = 0; i < masterIds.size(); i++) {
//                        inClause.append(masterIds.get(i));
//                        if (i < masterIds.size() - 1) {
//                            inClause.append(",");
//                        }
//                    }
//
//                    Object result = em.createNativeQuery(
//                            "SELECT IFNULL(SUM(cheque_amount),0) "
//                            + "FROM student_fee_cheque_details "
//                            + "WHERE reference_type='ROUND' "
//                            + "AND category='STUDENT' "
//                            + "AND cheque_status='PENDING' "
//                            + "AND reference_id IN (" + inClause.toString() + ")"
//                    ).getSingleResult();
//
//                    chequeAmount = result != null
//                            ? ((Number) result).doubleValue()
//                            : 0;
//                }
//
//                // =====================================================
//                // 🔥 FETCH ADJUSTMENT + CREDIT (PER STUDENT)
    //// =====================================================
//                double totalAdjustment = 0;
//                double totalCredit = 0;
//
//                Object[] adjRow = (Object[]) em.createNativeQuery(
//                        "SELECT "
//                        + "IFNULL(SUM(adjustment_amount),0), "
//                        + "IFNULL(SUM(credit_amount),0) "
//                        + "FROM fee_adjustment "
//                        + "WHERE student_id=? AND status=1"
//                )
//                        .setParameter(1, studentId)
//                        .getSingleResult();
//
//                if (adjRow != null) {
//                    totalAdjustment = adjRow[0] != null ? ((Number) adjRow[0]).doubleValue() : 0;
//                    totalCredit = adjRow[1] != null ? ((Number) adjRow[1]).doubleValue() : 0;
//                }
//
//                // =====================================================
//// 🔥 ADDITIONAL FEES BALANCE (PER STUDENT)
//// =====================================================
//                double totalAdditionalAmount = 0;
//                double totalAdditionalPaid = 0;
//                double additionalBalance = 0;
//
//// 1. Total Additional Fees
//                Object totalAddObj = em.createNativeQuery(
//                        "SELECT IFNULL(SUM(amount),0) "
//                        + "FROM student_additional_fees "
//                        + "WHERE student_id=? AND status=1"
//                )
//                        .setParameter(1, studentId)
//                        .getSingleResult();
//
//                if (totalAddObj != null) {
//                    totalAdditionalAmount = ((Number) totalAddObj).doubleValue();
//                }
//
//// 2. Total Additional Paid
//                Object totalPaidObj = em.createNativeQuery(
//                        "SELECT IFNULL(SUM(safp.amount_paid),0) "
//                        + "FROM student_additional_fee_payments safp "
//                        + "INNER JOIN student_additional_fees saf "
//                        + "ON safp.student_additional_fees_id = saf.student_additional_fees_id "
//                        + "WHERE saf.student_id=? AND safp.status=1"
//                )
//                        .setParameter(1, studentId)
//                        .getSingleResult();
//
//                if (totalPaidObj != null) {
//                    totalAdditionalPaid = ((Number) totalPaidObj).doubleValue();
//                }
//
//// 3. Balance
//                additionalBalance = totalAdditionalAmount - totalAdditionalPaid;
//
//                if (additionalBalance < 0) {
//                    additionalBalance = 0;
//                }
//
//                // =====================================================
//                // 🔥 TOTAL PAID = PAID + CHEQUE
//                // =====================================================
//                double totalPaidWithCheque = totalPaid + chequeAmount;
//
//                // =====================================================
//                // 🔥 ADD ROW (UPDATED COLUMNS)
//                // =====================================================
//                model.addRow(new Object[]{
//                    rowNo++,
//                    admissionNo,
//                    name,
//                    GeneralMethods.formatWithComma(totalPaid), // Paid
//                    GeneralMethods.formatWithComma(chequeAmount), // Cheque
//                    GeneralMethods.formatWithComma(totalPaidWithCheque), // Total Paid
//
//                    GeneralMethods.formatWithComma(totalAdjustment), // 🔥 Adjustment
//                    GeneralMethods.formatWithComma(totalCredit), // 🔥 Credit
//                    GeneralMethods.formatWithComma(additionalBalance),
//                    GeneralMethods.formatWithComma(totalBalance + additionalBalance),
//                    contact,
//                    fatherContact
//                });
//            }
//
//            calculateStudentDueTotals(table);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }

    public void calculateStudentDueTotals(JTable table) {

        double totalPaid = 0;
        double totalCheque = 0;
        double grandTotal = 0;
        double totalAdjustment = 0;
        double totalCredit = 0;
        double totalAdditional = 0;
        double totalDue = 0;

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {

            // =========================
            // PAID (col 3)
            // =========================
            Object paidObj = model.getValueAt(i, 3);
            totalPaid += GeneralMethods.parseCommaNumber(paidObj.toString());

            // =========================
            // CHEQUE (col 4)
            // =========================
            Object chequeObj = model.getValueAt(i, 4);
            totalCheque += GeneralMethods.parseCommaNumber(chequeObj.toString());

            // =========================
            // GRAND TOTAL (col 5)
            // =========================
            Object grandObj = model.getValueAt(i, 5);
            grandTotal += GeneralMethods.parseCommaNumber(grandObj.toString());

            // =========================
            // ADJUSTMENT (col 6)
            // =========================
            Object adjObj = model.getValueAt(i, 6);
            totalAdjustment += GeneralMethods.parseCommaNumber(adjObj.toString());

            // =========================
            // CREDIT (col 7)
            // =========================
            Object creditObj = model.getValueAt(i, 7);
            totalCredit += GeneralMethods.parseCommaNumber(creditObj.toString());

            // =========================
            // ADDITIONAL (col 8)
            // =========================
            Object addObj = model.getValueAt(i, 8);
            totalAdditional += GeneralMethods.parseCommaNumber(addObj.toString());

            // =========================
            // DUE (col 9)
            // =========================
            Object dueObj = model.getValueAt(i, 9);
            totalDue += GeneralMethods.parseCommaNumber(dueObj.toString());
        }

        // =========================
        // SET VALUES TO TEXTFIELDS
        // =========================
        swd_st_con_total_paid_text.setText(GeneralMethods.formatWithComma(totalPaid));
        swd_st_con_total_cheque_text.setText(GeneralMethods.formatWithComma(totalCheque));
        swd_st_con_grand_total_text.setText(GeneralMethods.formatWithComma(grandTotal));
        swd_st_con_total_adjustment_text.setText(GeneralMethods.formatWithComma(totalAdjustment));
        swd_st_con_total_credit_text.setText(GeneralMethods.formatWithComma(totalCredit));
        swd_st_con_total_additional_text.setText(GeneralMethods.formatWithComma(totalAdditional));
        swd_st_con_total_due_text.setText(GeneralMethods.formatWithComma(totalDue));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        swd_st_table = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        swd_st_con_batch_combo = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        swd_st_con_course_text = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        swd_st_con_class_combo = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        swd_st_con_total_due_text = new javax.swing.JTextField();
        swd_st_con_grand_total_text = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        swd_st_con_total_adjustment_text = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        swd_st_con_total_credit_text = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        swd_st_con_total_cheque_text = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        swd_st_con_total_paid_text = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        swd_st_con_total_additional_text = new javax.swing.JTextField();
        pagination1 = new Pagination.Pagination();
        lbl_total_rows = new javax.swing.JLabel();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Student Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        swd_st_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Admission", "Student Name", "Paid", "Cheque", "Total Paid", "Adjustment", "Credit", "Additional", "Due", "Student Contact", "Father Contact"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        swd_st_table.setRowHeight(25);
        swd_st_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                swd_st_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(swd_st_table);
        if (swd_st_table.getColumnModel().getColumnCount() > 0) {
            swd_st_table.getColumnModel().getColumn(0).setPreferredWidth(20);
            swd_st_table.getColumnModel().getColumn(1).setPreferredWidth(100);
            swd_st_table.getColumnModel().getColumn(2).setPreferredWidth(150);
        }

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printblue32.png"))); // NOI18N
        jButton1.setToolTipText("Siblings");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/xlsx32.png"))); // NOI18N
        jButton2.setToolTipText("Siblings");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Batch");

        swd_st_con_batch_combo.setEditable(true);
        swd_st_con_batch_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jButton7.setBackground(new java.awt.Color(102, 102, 102));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/info24.png"))); // NOI18N
        jButton7.setToolTipText("Course Enrolment");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(102, 102, 102));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search16.png"))); // NOI18N
        jButton6.setToolTipText("Course Enrolment");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Course");

        swd_st_con_course_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        swd_st_con_course_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swd_st_con_course_textActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Class");

        swd_st_con_class_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jButton5.setBackground(new java.awt.Color(102, 102, 102));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search16.png"))); // NOI18N
        jButton5.setToolTipText("Course Enrolment");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Total Credit");

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("Total Due");

        swd_st_con_total_due_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        swd_st_con_total_due_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swd_st_con_total_due_textActionPerformed(evt);
            }
        });

        swd_st_con_grand_total_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        swd_st_con_grand_total_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swd_st_con_grand_total_textActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel7.setText("Grand Total Paid");

        swd_st_con_total_adjustment_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        swd_st_con_total_adjustment_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swd_st_con_total_adjustment_textActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel6.setText("Total Adjustment");

        swd_st_con_total_credit_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        swd_st_con_total_credit_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swd_st_con_total_credit_textActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel8.setText("Total Cheque");

        swd_st_con_total_cheque_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        swd_st_con_total_cheque_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swd_st_con_total_cheque_textActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel9.setText("Total Paid");

        swd_st_con_total_paid_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        swd_st_con_total_paid_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swd_st_con_total_paid_textActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel10.setText("Total Add.");

        swd_st_con_total_additional_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        swd_st_con_total_additional_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swd_st_con_total_additional_textActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(swd_st_con_batch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(swd_st_con_course_text, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(swd_st_con_class_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(swd_st_con_total_paid_text, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(swd_st_con_total_cheque_text, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(swd_st_con_grand_total_text, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(swd_st_con_total_adjustment_text, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(swd_st_con_total_credit_text, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(swd_st_con_total_additional_text, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(swd_st_con_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(swd_st_con_class_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(swd_st_con_course_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(swd_st_con_batch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(41, 41, 41))
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swd_st_con_total_paid_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swd_st_con_total_cheque_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swd_st_con_grand_total_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swd_st_con_total_adjustment_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swd_st_con_total_credit_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swd_st_con_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(swd_st_con_total_additional_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pagination1.setOpaque(false);

        lbl_total_rows.setFont(new java.awt.Font("Roboto Medium", 3, 14)); // NOI18N
        lbl_total_rows.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_total_rows.setText("Total : 0 Records");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(pagination1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(934, 934, 934)
                        .addComponent(lbl_total_rows, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_total_rows)
                    .addComponent(pagination1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed


    }//GEN-LAST:event_jButton1ActionPerformed

    private void swd_st_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_swd_st_tableMouseClicked

    }//GEN-LAST:event_swd_st_tableMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Batch_Class_Student_Dialog dialog = new Batch_Class_Student_Dialog(parentFrame);
        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        loadStudentsWithDue(swd_st_table, swd_st_con_batch_combo, swd_st_con_class_combo, 1);

    }//GEN-LAST:event_jButton6ActionPerformed

    private void swd_st_con_course_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swd_st_con_course_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swd_st_con_course_textActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        loadStudentsWithDue(swd_st_table, swd_st_con_batch_combo, swd_st_con_class_combo, 1);

    }//GEN-LAST:event_jButton5ActionPerformed

    private void swd_st_con_total_due_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swd_st_con_total_due_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swd_st_con_total_due_textActionPerformed

    private void swd_st_con_total_credit_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swd_st_con_total_credit_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swd_st_con_total_credit_textActionPerformed

    private void swd_st_con_total_adjustment_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swd_st_con_total_adjustment_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swd_st_con_total_adjustment_textActionPerformed

    private void swd_st_con_grand_total_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swd_st_con_grand_total_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swd_st_con_grand_total_textActionPerformed

    private void swd_st_con_total_cheque_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swd_st_con_total_cheque_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swd_st_con_total_cheque_textActionPerformed

    private void swd_st_con_total_paid_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swd_st_con_total_paid_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swd_st_con_total_paid_textActionPerformed

    private void swd_st_con_total_additional_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swd_st_con_total_additional_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_swd_st_con_total_additional_textActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_total_rows;
    private Pagination.Pagination pagination1;
    public static javax.swing.JComboBox<String> swd_st_con_batch_combo;
    private javax.swing.JComboBox<String> swd_st_con_class_combo;
    public static javax.swing.JTextField swd_st_con_course_text;
    public static javax.swing.JTextField swd_st_con_grand_total_text;
    public static javax.swing.JTextField swd_st_con_total_additional_text;
    public static javax.swing.JTextField swd_st_con_total_adjustment_text;
    public static javax.swing.JTextField swd_st_con_total_cheque_text;
    public static javax.swing.JTextField swd_st_con_total_credit_text;
    public static javax.swing.JTextField swd_st_con_total_due_text;
    public static javax.swing.JTextField swd_st_con_total_paid_text;
    public static javax.swing.JTable swd_st_table;
    // End of variables declaration//GEN-END:variables

}
