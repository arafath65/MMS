package Panels;

import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Student_Management.CourseEnrollment;
import Entities.Student_Management.OtherFeeAssignment;
import Entities.Student_Management.Student;
import Entities.Student_Management.StudentFeePayments;
import JPA_DAO.Student_Management.OtherFeeAssignmentDAO;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Batch_Transfer extends javax.swing.JPanel {

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    String username;
    String role;

    public Batch_Transfer(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        bat_tr_table.setDefaultRenderer(Object.class, new TableGradientCell());
        bat_tr_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");
        bat_pay_table.setDefaultRenderer(Object.class, new TableGradientCell());
        bat_pay_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");
        bat_other_pay_table.setDefaultRenderer(Object.class, new TableGradientCell());
        bat_other_pay_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        bat_pay_amount_text.setEnabled(true);

        loadActiveMonthlyBatchCourses(bat_tr_batch_course_combo);
        loadActiveMonthlyOnlyBatchCourses(bat_pay_batch_course_combo);
        loadActiveMonthlyBatchCourses(bat_other_pay_batch_course_combo);
        loadClassCombo(bat_tr_class_combo);
        loadClassCombo(bat_tr_new_class_combo);
        loadClassCombo(bat_pay_class_combo);
        loadClassCombo(bat_other_pay_class_combo);

        JComboPopulates();

    }

    private void JComboPopulates() {
        // TRANSFER TAB
        bat_tr_new_batch_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = bat_tr_new_batch_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingCourseComboItems(bat_tr_new_batch_combo, input);
            }

        });
        setupComboSelectionListener(bat_tr_new_batch_combo, bat_tr_new_batch_combo);

        bat_other_pay_payments_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = bat_other_pay_payments_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID0Only(bat_other_pay_payments_combo, "fee_type_id", "fee_name", "fee_types", input);
            }

        });
        setupComboSelectionListener2(bat_other_pay_payments_combo, bat_other_pay_amount_text);

    }

    private boolean itemSelectedByUser = false;
    private boolean itemSelectedByUser2 = false;

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

    public void setupComboSelectionListener2(JComboBox<String> comboBox, JComponent nextFocusComponent) {
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                itemSelectedByUser2 = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (itemSelectedByUser2) {
                    Object selected = comboBox.getSelectedItem();
                    if (selected != null) {
                        String selectedValue = selected.toString().trim();
                        if (!selectedValue.isEmpty() && isValueFromList(comboBox, selectedValue)) {

                            int feeId = generalMethods.extractIdFromCombo(bat_other_pay_payments_combo.getEditor().getItem().toString());
                            try {

                                EntityManager em = HibernateConfig.getEntityManager();

                                Object amount = em.createNativeQuery(
                                        "SELECT default_amount "
                                        + "FROM fee_types "
                                        + "WHERE fee_type_id = ? "
                                        + "AND status = 1")
                                        .setParameter(1, feeId) // your fee_type_id variable
                                        .getSingleResult();

                                if (amount != null) {
                                    bat_other_pay_amount_text.setText(
                                            GeneralMethods.formatWithComma(
                                                    GeneralMethods.parseCommaNumber(amount.toString())
                                            )
                                    );
                                } else {
                                    bat_other_pay_amount_text.setText("");
                                }

                                em.close();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                bat_other_pay_amount_text.setText("");
                            }

                            nextFocusComponent.requestFocus();
                        }
                    }
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                itemSelectedByUser2 = false;
            }
        });

        // Detect user selection from keyboard (Enter) or mouse (click)
        comboBox.addActionListener(e -> {
            if (comboBox.isPopupVisible()) {
                itemSelectedByUser2 = true;
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

    public void loadActiveMonthlyBatchCourses(JComboBox<String> comboBox) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            comboBox.removeAllItems();
            comboBox.addItem("Select the course");

            // =====================================================
            // FETCH DISTINCT ACTIVE MONTHLY COURSES
            // =====================================================
            List<Object[]> list = em.createNativeQuery(
                    "SELECT DISTINCT "
                    + "c.course_id, "
                    + "c.batch, "
                    + "c.course_name "
                    + "FROM course_enrollment ce "
                    + "INNER JOIN course c ON ce.course_id = c.course_id "
                    + "WHERE ce.course_status = 'ACTIVE' "
                    + "AND c.course_status = 'ACTIVE' "
                    + "AND ce.status = 1 "
                    + "AND c.status = 1 "
                    + "ORDER BY c.batch ASC"
            ).getResultList();

            System.out.println("Total Courses Found : " + list.size());

            // =====================================================
            // LOAD COMBO
            // Format:
            // Course Name [Batch] - CourseId
            // Example:
            // Spoken English [BATCH-01] - 15
            // =====================================================
            for (Object[] row : list) {

                int courseId = row[0] != null
                        ? ((Number) row[0]).intValue()
                        : 0;

                String batch = row[1] != null
                        ? row[1].toString()
                        : "";

                String courseName = row[2] != null
                        ? row[2].toString()
                        : "";

                String display = courseName + " [" + batch + "] - " + courseId;

                comboBox.addItem(display);

                System.out.println(display); // debug check
            }

            comboBox.revalidate();
            comboBox.repaint();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    public void loadActiveMonthlyOnlyBatchCourses(JComboBox<String> comboBox) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            comboBox.removeAllItems();
            comboBox.addItem("Select the course");

            // =====================================================
            // FETCH DISTINCT ACTIVE MONTHLY COURSES ONLY
            // ce.course_status = ACTIVE
            // c.course_status = ACTIVE
            // c.payment_mode = MONTHLY
            // =====================================================
            List<Object[]> list = em.createNativeQuery(
                    "SELECT DISTINCT "
                    + "c.course_id, "
                    + "c.batch, "
                    + "c.course_name "
                    + "FROM course_enrollment ce "
                    + "INNER JOIN course c "
                    + "ON ce.course_id = c.course_id "
                    + "WHERE ce.course_status = 'ACTIVE' "
                    + "AND c.course_status = 'ACTIVE' "
                    + "AND c.payment_mode = 'MONTHLY' "
                    + "AND ce.status = 1 "
                    + "AND c.status = 1 "
                    + "ORDER BY c.batch ASC"
            ).getResultList();

            System.out.println("Total Monthly Courses Found : " + list.size());

            // =====================================================
            // LOAD COMBO
            // Format:
            // Course Name [Batch] - CourseId
            // Example:
            // Spoken English [BATCH-01] - 15
            // =====================================================
            for (Object[] row : list) {

                int courseId = row[0] != null
                        ? ((Number) row[0]).intValue()
                        : 0;

                String batch = row[1] != null
                        ? row[1].toString()
                        : "";

                String courseName = row[2] != null
                        ? row[2].toString()
                        : "";

                String display = courseName + " [" + batch + "] - " + courseId;

                comboBox.addItem(display);

                System.out.println(display); // debug
            }

            comboBox.revalidate();
            comboBox.repaint();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    public void loadClassCombo(JComboBox combo1) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            if (combo1 == null) {
                System.out.println("btc_st_class_combo is null");
                return;
            }

            combo1.removeAllItems();
            combo1.addItem("Select class");

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
                    combo1.addItem(className);
                    System.out.println("Loaded Class = " + className);
                }
            }

            combo1.revalidate();
            combo1.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void loadBatchTransferStudents(JTable bat_tr_table,
            JComboBox<String> bat_tr_batch_course_combo,
            JComboBox<String> bat_tr_class_combo) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) bat_tr_table.getModel();
            model.setRowCount(0);

            // =====================================================
            // GET COURSE VALUE
            // =====================================================
            String selectedCourse = bat_tr_batch_course_combo.getSelectedItem() != null
                    ? bat_tr_batch_course_combo.getSelectedItem().toString()
                    : "";

            // If "Select the course" → stop loading
            if (selectedCourse.isEmpty()
                    || selectedCourse.equalsIgnoreCase("Select the course")
                    || !selectedCourse.contains("-")) {
                return;
            }

            // =====================================================
            // EXTRACT COURSE ID
            // Format:
            // Course Name [Batch] - CourseId
            // =====================================================
            int courseId = Integer.parseInt(
                    selectedCourse.substring(
                            selectedCourse.lastIndexOf("-") + 1
                    ).trim()
            );

            // =====================================================
            // GET CLASS FILTER
            // =====================================================
            String selectedClass = bat_tr_class_combo.getSelectedItem() != null
                    ? bat_tr_class_combo.getSelectedItem().toString()
                    : "";

            boolean filterClass = selectedClass != null
                    && !selectedClass.trim().isEmpty()
                    && !selectedClass.equalsIgnoreCase("Select class");

            // =====================================================
            // BUILD SQL
            // =====================================================
            String sql = "SELECT "
                    + "ce.enrollment_id, " // 0
                    + "s.student_id, " // 1
                    + "s.admission_no, " // 2
                    + "s.full_name " // 3
                    + "FROM course_enrollment ce "
                    + "INNER JOIN student s "
                    + "ON ce.student_id = s.student_id "
                    + "WHERE ce.course_id = ? "
                    + "AND ce.course_status = 'ACTIVE' "
                    + "AND ce.status = 1 "
                    + "AND s.status = 1 ";

            if (filterClass) {
                sql += "AND ce.class_name = ? ";
            }

            sql += "ORDER BY s.admission_no ASC";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, courseId);

            if (filterClass) {
                query.setParameter(2, selectedClass);
            }

            List<Object[]> list = query.getResultList();

            int rowNo = 1;

            for (Object[] row : list) {

                int enrollmentId = row[0] != null
                        ? ((Number) row[0]).intValue()
                        : 0;

                int studentId = row[1] != null
                        ? ((Number) row[1]).intValue()
                        : 0;

                String admissionNo = row[2] != null
                        ? row[2].toString()
                        : "";

                String studentName = row[3] != null
                        ? row[3].toString()
                        : "";

                model.addRow(new Object[]{
                    rowNo++, // index 0 -> #
                    admissionNo, // index 1 -> Admission No
                    studentName, // index 2 -> Student Name
                    "",
                    studentId, // index 3 -> Student ID
                });
            }

            bat_other_pay_total.setText(model.getRowCount() + "");

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    public void setNewBatchDetailsToTable(JTable bat_tr_table,
            JComboBox<String> bat_tr_new_batch_combo,
            JComboBox<String> bat_tr_new_class_combo) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) bat_tr_table.getModel();

            // =====================================================
            // GET NEW COURSE VALUE
            // Format:
            // Course Name [Batch] - CourseId
            // =====================================================
            String selectedCourse = bat_tr_new_batch_combo.getSelectedItem() != null
                    ? bat_tr_new_batch_combo.getSelectedItem().toString()
                    : "";

            if (selectedCourse.isEmpty()
                    || selectedCourse.equalsIgnoreCase("Select the course")
                    || !selectedCourse.contains("-")) {
                return;
            }

            // =====================================================
            // GET CLASS VALUE
            // =====================================================
            String selectedClass = bat_tr_new_class_combo.getSelectedItem() != null
                    ? bat_tr_new_class_combo.getSelectedItem().toString()
                    : "";

            if (selectedClass.isEmpty()
                    || selectedClass.equalsIgnoreCase("Select class")) {
                return;
            }

            // =====================================================
            // EXTRACT COURSE ID
            // =====================================================
            int courseId = Integer.parseInt(
                    selectedCourse.substring(
                            selectedCourse.lastIndexOf("-") + 1
                    ).trim()
            );

            // =====================================================
            // REMOVE COURSE ID FROM DISPLAY
            // Example:
            // Hifz Course [BATCH-01] - 15
            // becomes
            // Hifz Course [BATCH-01]
            // =====================================================
            String displayCourse = selectedCourse.substring(
                    0,
                    selectedCourse.lastIndexOf("-")
            ).trim();

            // =====================================================
            // FETCH FROM COURSE TABLE
            // payment_mode
            // admission_fee
            // fee
            // =====================================================
            String paymentMode = "";
            String admissionFee = "";
            String monthlyFee = "";

            List<Object[]> courseList = em.createNativeQuery(
                    "SELECT payment_mode, admission_fee, fee "
                    + "FROM course "
                    + "WHERE course_id = ? "
                    + "AND status = 1 "
                    + "LIMIT 1"
            )
                    .setParameter(1, courseId)
                    .getResultList();

            if (!courseList.isEmpty()) {
                Object[] row = courseList.get(0);

                paymentMode = row[0] != null ? row[0].toString() : "";
                admissionFee = row[1] != null ? row[1].toString() : "";
                monthlyFee = row[2] != null ? row[2].toString() : "";
            }

            for (int i = 0; i < model.getRowCount(); i++) {

                model.setValueAt(displayCourse, i, 3);   // batch + course
                model.setValueAt(selectedClass, i, 4);   // class
                model.setValueAt(paymentMode, i, 5);     // payment mode
                model.setValueAt(GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(admissionFee)), i, 6);    // admission fee
                model.setValueAt(GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(monthlyFee)), i, 7);      // fee
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    public void saveBatchTransfer() {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            DefaultTableModel model = (DefaultTableModel) bat_tr_table.getModel();

            String oldCourseText = bat_tr_batch_course_combo.getSelectedItem() != null
                    ? bat_tr_batch_course_combo.getSelectedItem().toString()
                    : "";

            String newCourseText = bat_tr_new_batch_combo.getSelectedItem() != null
                    ? bat_tr_new_batch_combo.getSelectedItem().toString()
                    : "";

            String newClass = bat_tr_new_class_combo.getSelectedItem() != null
                    ? bat_tr_new_class_combo.getSelectedItem().toString()
                    : "";

            String note = "";

            if (oldCourseText.equalsIgnoreCase("Select the course")
                    || oldCourseText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select old course");
                return;
            }

            if (newCourseText.equalsIgnoreCase("Select the course")
                    || newCourseText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select new course");
                return;
            }

            if (newClass.equalsIgnoreCase("Select class")
                    || newClass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select new class");
                return;
            }

            // =====================================================
            // GET OLD COURSE ID
            // Format: Course [Batch] - courseId
            // =====================================================
            int oldCourseId = Integer.parseInt(
                    oldCourseText.substring(
                            oldCourseText.lastIndexOf("-") + 1
                    ).trim()
            );

            // =====================================================
            // GET NEW COURSE ID
            // =====================================================
            int newCourseId = Integer.parseInt(
                    newCourseText.substring(
                            newCourseText.lastIndexOf("-") + 1
                    ).trim()
            );

            // =====================================================
            // FETCH NEW COURSE DETAILS
            // =====================================================
            Object[] courseData = (Object[]) em.createNativeQuery(
                    "SELECT payment_mode, admission_fee, fee "
                    + "FROM course "
                    + "WHERE course_id = ? "
                    + "AND status = 1"
            )
                    .setParameter(1, newCourseId)
                    .getSingleResult();

            String paymentMode = courseData[0] != null ? courseData[0].toString() : "";
            double admissionFee = courseData[1] != null
                    ? Double.parseDouble(courseData[1].toString())
                    : 0;
            double fee = courseData[2] != null
                    ? Double.parseDouble(courseData[2].toString())
                    : 0;

// ADD THESE VALIDATIONS + CONFIRMATION
// =====================================================
            //     DefaultTableModel model = (DefaultTableModel) bat_tr_table.getModel();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No students found to transfer.");
                return;
            }

// CHECK FEE COLUMNS (INDEX 6 + 7)
// =====================================================
            double totalAdmissionFee = 0;
            double totalFee = 0;

            for (int i = 0; i < model.getRowCount(); i++) {

                Object admissionFeeObj = model.getValueAt(i, 6);
                Object feeObj = model.getValueAt(i, 7);

                String admissionFeeText = admissionFeeObj != null
                        ? admissionFeeObj.toString().trim()
                        : "";

                String feeText = feeObj != null
                        ? feeObj.toString().trim()
                        : "";

                if (admissionFeeText.isEmpty() || feeText.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Fees cannot be empty.\nPlease check row : " + (i + 1)
                    );
                    return;
                }

                totalAdmissionFee += Double.parseDouble(
                        admissionFeeText.replace(",", "")
                );

                totalFee += Double.parseDouble(
                        feeText.replace(",", "")
                );
            }

// CHECK ALREADY REGISTERED IN NEW COURSE
// =====================================================
            for (int i = 0; i < model.getRowCount(); i++) {

                Object studentIdObj = model.getValueAt(i, 8);

                if (studentIdObj == null) {
                    continue;
                }

                int studentId = Integer.parseInt(studentIdObj.toString());

                List<Object[]> duplicateList = em.createNativeQuery(
                        "SELECT s.admission_no, s.full_name "
                        + "FROM course_enrollment ce "
                        + "INNER JOIN student s "
                        + "ON ce.student_id = s.student_id "
                        + "WHERE ce.student_id = ? "
                        + "AND ce.course_id = ? "
                        + "AND ce.status = 1 "
                        + "LIMIT 1"
                )
                        .setParameter(1, studentId)
                        .setParameter(2, newCourseId)
                        .getResultList();

                if (!duplicateList.isEmpty()) {

                    Object[] row = duplicateList.get(0);

                    String admissionNo = row[0] != null
                            ? row[0].toString()
                            : "";

                    String studentName = row[1] != null
                            ? row[1].toString()
                            : "";

                    JOptionPane.showMessageDialog(
                            null,
                            "Student already registered with this enrollment.\n\n"
                            + "Admission No : " + admissionNo + "\n"
                            + "Student Name : " + studentName + "\n"
                            + "Course Batch : " + newCourseText
                    );
                    return;
                }
            }

// CONFIRMATION POPUP
// =====================================================
            String message
                    = "Do you want to transfer these students?\n\n"
                    + "New Batch & Course : " + newCourseText + "\n"
                    + "No of Students : " + model.getRowCount() + "\n"
                    + "Total Admission Fee : "
                    + GeneralMethods.formatWithComma(totalAdmissionFee) + "\n"
                    + "Total Fee : "
                    + GeneralMethods.formatWithComma(totalFee);

            String[] options = {"Transfer", "Cancel"};

            int confirm = JOptionPane.showOptionDialog(
                    null,
                    message,
                    "Confirm Batch Transfer",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (confirm != 0) {
                return;
            }

            tx.begin();

            // =====================================================
            // SAVE ONE BY ONE STUDENT
            // =====================================================
            for (int i = 0; i < model.getRowCount(); i++) {

                Object studentIdObj = model.getValueAt(i, 8);

                if (studentIdObj == null) {
                    continue;
                }

                int studentId = Integer.parseInt(studentIdObj.toString());

                // =====================================================
                // FETCH STUDENT ENTITY
                // =====================================================
                Student student = em.find(Student.class, studentId);

                if (student == null) {
                    continue;
                }

                // =====================================================
                // FETCH OLD ENROLLMENT ID
                // =====================================================
                List<Object> oldEnrollList = em.createNativeQuery(
                        "SELECT enrollment_id "
                        + "FROM course_enrollment "
                        + "WHERE student_id = ? "
                        + "AND course_id = ? "
                        + "AND status = 1 "
                        + "ORDER BY enrollment_id DESC "
                        + "LIMIT 1"
                )
                        .setParameter(1, studentId)
                        .setParameter(2, oldCourseId)
                        .getResultList();

                int oldEnrollmentId = 0;

                if (!oldEnrollList.isEmpty()) {
                    oldEnrollmentId = ((Number) oldEnrollList.get(0)).intValue();
                }

                // =====================================================
                // INSERT NEW COURSE ENROLLMENT
                // =====================================================
                CourseEnrollment ce = new CourseEnrollment();

                ce.setStudent(student);
                ce.setCourseId(newCourseId);
                ce.setClassName(newClass);
                ce.setAdmissionFee(admissionFee);
                ce.setFee(fee);
                ce.setCourseStatus("ACTIVE");
                ce.setStatus(1);

                // =====================================================
                // STUDENT FEE PAYMENT
                // =====================================================
                StudentFeePayments fp = new StudentFeePayments();

                fp.setStudent(student);
                fp.setEnrollment(ce);

                fp.setTotalFee(fee);
                fp.setTotalPaid(0);
                fp.setTotalBalance(fee);

                fp.setPaymentType("");
                fp.setCourseType(paymentMode);
                fp.setPaymentStatus("ACTIVE");
                fp.setRemarks("BATCH_TRANSFER");

                fp.setCreatedAt(new Date());
                fp.setLastMofidied(new Date());
                fp.setUser(username);
                fp.setStatus(true);

                ce.setFeePayments(fp);

                em.persist(ce);
                em.flush(); // important for generated ID

                int newEnrollmentId = ce.getEnrollmentId();

                // =====================================================
                // INSERT BATCH TRANSFER RECORD
                // =====================================================
                em.createNativeQuery(
                        "INSERT INTO student_batch_transfer ("
                        + "student_id, "
                        + "old_course_id, "
                        + "old_enrollment_id, "
                        + "new_course_id, "
                        + "new_enrollment_id, "
                        + "transfer_date, "
                        + "note, "
                        + "user, "
                        + "status"
                        + ") VALUES (?,?,?,?,?,?,?,?,?)"
                )
                        .setParameter(1, studentId)
                        .setParameter(2, oldCourseId)
                        .setParameter(3, oldEnrollmentId)
                        .setParameter(4, newCourseId)
                        .setParameter(5, newEnrollmentId)
                        .setParameter(6, new Date())
                        .setParameter(7, note)
                        .setParameter(8, username)
                        .setParameter(9, 1)
                        .executeUpdate();

                em.createNativeQuery(
                        "UPDATE course_enrollment "
                        + "SET course_status = 'COMPLETED' "
                        + "WHERE enrollment_id = ?"
                )
                        .setParameter(1, oldEnrollmentId)
                        .executeUpdate();

                em.createNativeQuery(
                        "UPDATE course "
                        + "SET course_status = 'COMPLETED' "
                        + "WHERE course_id = ? "
                        + "AND status = 1"
                )
                        .setParameter(1, oldCourseId)
                        .executeUpdate();
            }

            tx.commit();

            JOptionPane.showMessageDialog(
                    null,
                    "Batch transfer completed successfully."
            );

        } catch (Exception e) {
            e.printStackTrace();

            if (tx.isActive()) {
                tx.rollback();
            }

        } finally {
            em.close();
        }
    }

    // ************** DISCOUNT TAB *******************
    public void loadDiscountWaiveStudents(JTable table,
            JComboBox<String> batchCourseCombo,
            JComboBox<String> classCombo) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            // ============================
            // GET COURSE VALUE
            // ============================
            String selectedCourse = batchCourseCombo.getSelectedItem() != null
                    ? batchCourseCombo.getSelectedItem().toString()
                    : "";

            if (selectedCourse.isEmpty()
                    || selectedCourse.equalsIgnoreCase("Select the course")
                    || !selectedCourse.contains("-")) {
                return;
            }

            int courseId = Integer.parseInt(
                    selectedCourse.substring(
                            selectedCourse.lastIndexOf("-") + 1
                    ).trim()
            );

            // ============================
            // FETCH COURSE MONTH RANGE ONLY
            // ============================
            Object[] courseData = (Object[]) em.createNativeQuery(
                    "SELECT enrol_year, enrol_month, comp_year, comp_month "
                    + "FROM course WHERE course_id = ? AND status = 1"
            )
                    .setParameter(1, courseId)
                    .getSingleResult();

            int enrolYear = courseData[0] != null ? ((Number) courseData[0]).intValue() : 0;
            int compYear = courseData[2] != null ? ((Number) courseData[2]).intValue() : 0;

            String enrolMonthValue = courseData[1] != null ? courseData[1].toString().trim() : "";
            String compMonthValue = courseData[3] != null ? courseData[3].toString().trim() : "";

            int startMonth = 1;
            int endMonth = 12;

            // START MONTH
            if (!enrolMonthValue.isEmpty()) {
                if (enrolMonthValue.contains("-")) {
                    String[] parts = enrolMonthValue.split("-");
                    startMonth = parts.length > 1 ? Integer.parseInt(parts[1]) : Integer.parseInt(parts[0]);
                } else if (enrolMonthValue.matches("\\d+")) {
                    startMonth = Integer.parseInt(enrolMonthValue);
                } else {
                    startMonth = generalMethods.getMonthNumber(enrolMonthValue);
                }
            }

            // END MONTH
            if (!compMonthValue.isEmpty()) {
                if (compMonthValue.contains("-")) {
                    String[] parts = compMonthValue.split("-");
                    endMonth = parts.length > 1 ? Integer.parseInt(parts[1]) : Integer.parseInt(parts[0]);
                } else if (compMonthValue.matches("\\d+")) {
                    endMonth = Integer.parseInt(compMonthValue);
                } else {
                    endMonth = generalMethods.getMonthNumber(compMonthValue);
                }
            }

            // ============================
            // GENERATE MONTH LIST
            // ============================
            DefaultComboBoxModel<String> monthModel = new DefaultComboBoxModel<>();

            int totalMonthCount = 0;

            for (int year = enrolYear; year <= compYear; year++) {

                int fromMonth = (year == enrolYear) ? startMonth : 1;
                int toMonth = (year == compYear) ? endMonth : 12;

                for (int m = fromMonth; m <= toMonth; m++) {

                    String value = year + " - " + GeneralMethods.getMonthName(m);
                    monthModel.addElement(value);
                    totalMonthCount++;
                }
            }

            bat_pay_month_for_combo.setModel(monthModel);

            // ============================
            // CLASS FILTER
            // ============================
            String selectedClass = classCombo.getSelectedItem() != null
                    ? classCombo.getSelectedItem().toString()
                    : "";

            boolean filterClass = selectedClass != null
                    && !selectedClass.trim().isEmpty()
                    && !selectedClass.equalsIgnoreCase("Select class");

            // ============================
            // MAIN QUERY (IMPORTANT CHANGE)
            // ============================
            String sql = "SELECT "
                    + "ce.enrollment_id, "
                    + "ce.class_name, "
                    + "ce.fee, " // ✅ student specific fee
                    + "s.student_id, "
                    + "s.admission_no, "
                    + "s.full_name "
                    + "FROM course_enrollment ce "
                    + "INNER JOIN student s ON ce.student_id = s.student_id "
                    + "WHERE ce.course_id = ? "
                    + "AND ce.course_status = 'ACTIVE' "
                    + "AND ce.status = 1 "
                    + "AND s.status = 1 ";

            if (filterClass) {
                sql += "AND ce.class_name = ? ";
            }

            sql += "ORDER BY s.admission_no ASC";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, courseId);

            if (filterClass) {
                query.setParameter(2, selectedClass);
            }

            List<Object[]> list = query.getResultList();

            int rowNo = 1;

            for (Object[] row : list) {

                int enrollmentId = ((Number) row[0]).intValue();
                String className = row[1] != null ? row[1].toString() : "";
                double studentTotalFee = row[2] != null ? Double.parseDouble(row[2].toString()) : 0;

                int studentId = ((Number) row[3]).intValue();
                String admissionNo = row[4] != null ? row[4].toString() : "";
                String studentName = row[5] != null ? row[5].toString() : "";

                // ============================
                // CALCULATE MONTHLY FEE PER STUDENT
                // ============================
                double studentMonthlyFee = totalMonthCount > 0
                        ? studentTotalFee / totalMonthCount
                        : 0;

                // ============================
                // LAST PAID MONTH
                // ============================
                String lastPaidMonth = "";

                List<Object> monthList = em.createNativeQuery(
                        "SELECT month_for FROM student_fee_installments "
                        + "WHERE enrollment_id = ? AND status = 1 "
                        + "ORDER BY student_fee_installments_id DESC LIMIT 1"
                )
                        .setParameter(1, enrollmentId)
                        .getResultList();

                if (!monthList.isEmpty() && monthList.get(0) != null) {

                    String value = monthList.get(0).toString();

                    if (value.contains("-")) {
                        String[] parts = value.split("-");
                        int m = Integer.parseInt(parts[1]);
                        String y = parts[0];

                        lastPaidMonth = y + " - " + GeneralMethods.getMonthName(m);
                    }
                }

                // ============================
                // ADD ROW (IMPORTANT CHANGE)
                // ============================
                model.addRow(new Object[]{
                    rowNo++,
                    admissionNo,
                    studentName,
                    className,
                    GeneralMethods.formatWithComma(studentMonthlyFee), // ✅ NEW COLUMN
                    lastPaidMonth,
                    "", // discount input
                    "",
                    "",
                    studentId,
                    enrollmentId
                });
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }
//    public void loadDiscountWaiveStudents(JTable table,
//            JComboBox<String> batchCourseCombo,
//            JComboBox<String> classCombo) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            DefaultTableModel model = (DefaultTableModel) table.getModel();
//            model.setRowCount(0);
//
//            // =====================================================
//            // GET COURSE VALUE
//            // =====================================================
//            String selectedCourse = batchCourseCombo.getSelectedItem() != null
//                    ? batchCourseCombo.getSelectedItem().toString()
//                    : "";
//
//            if (selectedCourse.isEmpty()
//                    || selectedCourse.equalsIgnoreCase("Select the course")
//                    || !selectedCourse.contains("-")) {
//                return;
//            }
//
//            // =====================================================
//            // EXTRACT COURSE ID
//            // =====================================================
//            int courseId = Integer.parseInt(
//                    selectedCourse.substring(
//                            selectedCourse.lastIndexOf("-") + 1
//                    ).trim()
//            );
//
//            // =====================================================
//            // FETCH COURSE DETAILS
//            // enrol_year
//            // enrol_month
//            // comp_year
//            // comp_month
//            // fee
//            // =====================================================
//            Object[] courseData = (Object[]) em.createNativeQuery(
//                    "SELECT enrol_year, enrol_month, comp_year, comp_month, fee "
//                    + "FROM course "
//                    + "WHERE course_id = ? "
//                    + "AND status = 1"
//            )
//                    .setParameter(1, courseId)
//                    .getSingleResult();
//
//            int enrolYear = courseData[0] != null
//                    ? ((Number) courseData[0]).intValue()
//                    : 0;
//
//            int compYear = courseData[2] != null
//                    ? ((Number) courseData[2]).intValue()
//                    : 0;
//
//            double totalCourseFee = courseData[4] != null
//                    ? Double.parseDouble(courseData[4].toString())
//                    : 0;
//
//            DefaultComboBoxModel<String> monthModel = new DefaultComboBoxModel<>();
//
//            String enrolMonthValue = courseData[1] != null
//                    ? courseData[1].toString().trim()
//                    : "";
//
//            String compMonthValue = courseData[3] != null
//                    ? courseData[3].toString().trim()
//                    : "";
//
//            int startMonth = 1;
//            int endMonth = 12;
//
//            // =====================================================
//            // START MONTH
//            // =====================================================
//            if (!enrolMonthValue.isEmpty()) {
//
//                if (enrolMonthValue.contains("-")) {
//                    startMonth = Integer.parseInt(
//                            enrolMonthValue.split("-")[1]
//                    );
//
//                } else if (enrolMonthValue.matches("\\d+")) {
//                    startMonth = Integer.parseInt(enrolMonthValue);
//
//                } else {
//                    startMonth = generalMethods.getMonthNumber(enrolMonthValue);
//                }
//            }
//
//            // =====================================================
//            // END MONTH
//            // =====================================================
//            if (!compMonthValue.isEmpty()) {
//
//                if (compMonthValue.contains("-")) {
//                    endMonth = Integer.parseInt(
//                            compMonthValue.split("-")[1]
//                    );
//
//                } else if (compMonthValue.matches("\\d+")) {
//                    endMonth = Integer.parseInt(compMonthValue);
//
//                } else {
//                    endMonth = generalMethods.getMonthNumber(compMonthValue);
//                }
//            }
//
//            // =====================================================
//            // GENERATE MONTH RANGE + COUNT TOTAL MONTHS
//            // =====================================================
//            int totalMonthCount = 0;
//
//            for (int year = enrolYear; year <= compYear; year++) {
//
//                int fromMonth = (year == enrolYear)
//                        ? startMonth
//                        : 1;
//
//                int toMonth = (year == compYear)
//                        ? endMonth
//                        : 12;
//
//                for (int m = fromMonth; m <= toMonth; m++) {
//
//                    String value = year + " - "
//                            + GeneralMethods.getMonthName(m);
//
//                    monthModel.addElement(value);
//                    totalMonthCount++;
//                }
//            }
//
//            bat_pay_month_for_combo.setModel(monthModel);
//
//            // =====================================================
//            // MONTHLY FEE CALCULATION
//            // total fee / total month count
//            // store in global variable: monthlyFee
//            // =====================================================
//            if (totalMonthCount > 0) {
//                monthlyFee = totalCourseFee / totalMonthCount;
//            } else {
//                monthlyFee = 0.0;
//            }
//
//            System.out.println("Total Course Fee = " + totalCourseFee);
//            System.out.println("Total Month Count = " + totalMonthCount);
//            System.out.println("Monthly Fee = " + monthlyFee);
//
//            // =====================================================
//            // CLASS FILTER
//            // =====================================================
//            String selectedClass = classCombo.getSelectedItem() != null
//                    ? classCombo.getSelectedItem().toString()
//                    : "";
//
//            boolean filterClass = selectedClass != null
//                    && !selectedClass.trim().isEmpty()
//                    && !selectedClass.equalsIgnoreCase("Select class");
//
//            // =====================================================
//            // MAIN QUERY
//            // =====================================================
//            String sql = "SELECT "
//                    + "ce.enrollment_id, "
//                    + "ce.class_name, "
//                    + "s.student_id, "
//                    + "s.admission_no, "
//                    + "s.full_name "
//                    + "FROM course_enrollment ce "
//                    + "INNER JOIN student s "
//                    + "ON ce.student_id = s.student_id "
//                    + "WHERE ce.course_id = ? "
//                    + "AND ce.course_status = 'ACTIVE' "
//                    + "AND ce.status = 1 "
//                    + "AND s.status = 1 ";
//
//            if (filterClass) {
//                sql += "AND ce.class_name = ? ";
//            }
//
//            sql += "ORDER BY s.admission_no ASC";
//
//            Query query = em.createNativeQuery(sql);
//            query.setParameter(1, courseId);
//
//            if (filterClass) {
//                query.setParameter(2, selectedClass);
//            }
//
//            List<Object[]> list = query.getResultList();
//
//            int rowNo = 1;
//
//            for (Object[] row : list) {
//
//                int enrollmentId = row[0] != null
//                        ? ((Number) row[0]).intValue()
//                        : 0;
//
//                String className = row[1] != null
//                        ? row[1].toString()
//                        : "";
//
//                int studentId = row[2] != null
//                        ? ((Number) row[2]).intValue()
//                        : 0;
//
//                String admissionNo = row[3] != null
//                        ? row[3].toString()
//                        : "";
//
//                String studentName = row[4] != null
//                        ? row[4].toString()
//                        : "";
//
//                String lastPaidMonth = "";
//
//                List<Object> monthList = em.createNativeQuery(
//                        "SELECT month_for "
//                        + "FROM student_fee_installments "
//                        + "WHERE enrollment_id = ? "
//                        + "AND status = 1 "
//                        + "ORDER BY student_fee_installments_id DESC "
//                        + "LIMIT 1"
//                )
//                        .setParameter(1, enrollmentId)
//                        .getResultList();
//
//                if (!monthList.isEmpty() && monthList.get(0) != null) {
//
//                    String value = monthList.get(0).toString();
//
//                    int monthNo = 0;
//                    String yearNo = "";
//
//                    if (value.contains("-")) {
//                        String yearPart = value.split("-")[0];
//                        String monthPart = value.split("-")[1];
//
//                        monthNo = Integer.parseInt(monthPart);
//                        yearNo = yearPart;
//                    }
//
//                    lastPaidMonth = yearNo + " - "
//                            + GeneralMethods.getMonthName(monthNo);
//                }
//
//                model.addRow(new Object[]{
//                    rowNo++,
//                    admissionNo,
//                    studentName,
//                    className,
//                    lastPaidMonth,
//                    "",
//                    "",
//                    "",
//                    studentId,
//                    enrollmentId
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        } finally {
//            em.close();
//        }
//    }

    public void saveDiscountWaive() {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            DefaultTableModel model = (DefaultTableModel) bat_pay_table.getModel();

            // ============================
            // VALIDATIONS
            // ============================
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No students found.");
                return;
            }

            String paymentOption = bat_pay_option_combo.getSelectedItem() != null
                    ? bat_pay_option_combo.getSelectedItem().toString()
                    : "";

            if (paymentOption.equalsIgnoreCase("Select Option") || paymentOption.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select payment option.");
                return;
            }

            String note = bat_pay_note_text.getText().trim();

            if (note.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Note cannot be empty.");
                return;
            }

            String selectedMonth = bat_pay_month_for_combo.getSelectedItem() != null
                    ? bat_pay_month_for_combo.getSelectedItem().toString()
                    : "";

            if (selectedMonth.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select month.");
                return;
            }

            // ============================
            // CONVERT MONTH
            // ============================
            String saveMonth = "";

            if (selectedMonth.contains(" - ")) {
                String year = selectedMonth.split(" - ")[0].trim();
                String monthName = selectedMonth.split(" - ")[1].trim();

                int monthNo = generalMethods.getMonthNumber(monthName);
                saveMonth = year + "-" + String.format("%02d", monthNo);
            }

            System.out.println("DEBUG → saveMonth: " + saveMonth);

            String paymentTypeValue = paymentOption.equalsIgnoreCase("DISCOUNT")
                    ? "DISCOUNT"
                    : "ZERO";

            tx.begin();

            // ============================
            // LOOP STUDENTS
            // ============================
            for (int i = 0; i < model.getRowCount(); i++) {

                Object studentIdObj = model.getValueAt(i, 9);
                Object enrollmentIdObj = model.getValueAt(i, 10);
                Object rowAmountObj = model.getValueAt(i, 6);
                Object monthlyFeeObj = model.getValueAt(i, 4);

                if (enrollmentIdObj == null) {
                    continue;
                }

                int studentId = studentIdObj != null
                        ? Integer.parseInt(studentIdObj.toString())
                        : 0;

                int enrollmentId = Integer.parseInt(enrollmentIdObj.toString());

                double monthlyFee = monthlyFeeObj != null
                        ? GeneralMethods.parseCommaNumber(monthlyFeeObj.toString())
                        : 0;

                // ============================
                // SKIP EMPTY
                // ============================
                if (rowAmountObj == null || rowAmountObj.toString().trim().isEmpty()) {
                    continue;
                }

                double discountAmount = 0;

                if (paymentOption.equalsIgnoreCase("DISCOUNT")) {

                    discountAmount = GeneralMethods.parseCommaNumber(rowAmountObj.toString());

                    if (discountAmount <= 0) {
                        continue;
                    }

                    if (discountAmount > monthlyFee) {
                        JOptionPane.showMessageDialog(null,
                                "Amount cannot exceed monthly fee.\nRow: " + (i + 1));
                        tx.rollback();
                        return;
                    }
                }

                // ============================
                // 🔥 CREATE ROUND MASTER (PER STUDENT)
                // ============================
                em.createNativeQuery(
                        "INSERT INTO student_fee_round_payment_master ("
                        + "student_id, payment_date, payment_mode, total_paid, "
                        + "rounding_adjustment, remarks, user, status"
                        + ") VALUES (?,?,?,?,?,?,?,?)"
                )
                        .setParameter(1, studentId)
                        .setParameter(2, new Date())
                        .setParameter(3, paymentTypeValue)
                        .setParameter(4, 0)
                        .setParameter(5, 0)
                        .setParameter(6, "FEE_ADJUSTMENT")
                        .setParameter(7, username)
                        .setParameter(8, 1)
                        .executeUpdate();

                int roundMasterId = ((Number) em.createNativeQuery("SELECT LAST_INSERT_ID()")
                        .getSingleResult()).intValue();

                // ============================
                // CHECK INSTALLMENT
                // ============================
                List<Object[]> list = em.createNativeQuery(
                        "SELECT student_fee_installments_id, IFNULL(amount_paid,0) "
                        + "FROM student_fee_installments "
                        + "WHERE enrollment_id = ? AND month_for = ? AND status = 1 "
                        + "ORDER BY student_fee_installments_id DESC LIMIT 1"
                )
                        .setParameter(1, enrollmentId)
                        .setParameter(2, saveMonth)
                        .getResultList();

                double paidAmount = 0;

                if (!list.isEmpty()) {

                    Object[] row = list.get(0);

                    int installmentId = ((Number) row[0]).intValue();
                    paidAmount = ((Number) row[1]).doubleValue();

                    // UPDATE TYPE + ROUND LINK
                    em.createNativeQuery(
                            "UPDATE student_fee_installments "
                            + "SET payment_type = ?, student_fee_round_payment_master_id = ? "
                            + "WHERE student_fee_installments_id = ?"
                    )
                            .setParameter(1, paymentTypeValue)
                            .setParameter(2, roundMasterId)
                            .setParameter(3, installmentId)
                            .executeUpdate();

                } else {

                    List<Object> paymentMasterList = em.createNativeQuery(
                            "SELECT student_fee_payments_id "
                            + "FROM student_fee_payments "
                            + "WHERE enrollment_id = ? AND status = 1 LIMIT 1"
                    )
                            .setParameter(1, enrollmentId)
                            .getResultList();

                    int paymentId = paymentMasterList.isEmpty()
                            ? 0
                            : ((Number) paymentMasterList.get(0)).intValue();

                    em.createNativeQuery(
                            "INSERT INTO student_fee_installments ("
                            + "student_fee_payments_id, enrollment_id, installment_no, "
                            + "amount_paid, payment_date, payment_method, payment_type, "
                            + "month_for, remarks, status, student_fee_round_payment_master_id"
                            + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)"
                    )
                            .setParameter(1, paymentId)
                            .setParameter(2, enrollmentId)
                            .setParameter(3, 0)
                            .setParameter(4, 0) // 🔥 NOT PAID
                            .setParameter(5, new Date())
                            .setParameter(6, "")
                            .setParameter(7, paymentTypeValue)
                            .setParameter(8, saveMonth)
                            .setParameter(9, "FEE_ADJUSTMENT")
                            .setParameter(10, 1)
                            .setParameter(11, roundMasterId)
                            .executeUpdate();

                    paidAmount = 0;
                }

                // ============================
                // INSERT ROUND DETAILS
                // ============================
                em.createNativeQuery(
                        "INSERT INTO student_fee_round_payment_master_details ("
                        + "student_fee_round_payment_master_id, enrollment_id, "
                        + "reference_id, reference_type, paid_amount, status"
                        + ") VALUES (?,?,?,?,?,?)"
                )
                        .setParameter(1, roundMasterId)
                        .setParameter(2, enrollmentId)
                        .setParameter(3, 0)
                        .setParameter(4, "FEE_ADJUSTMENT")
                        .setParameter(5, 0)
                        .setParameter(6, 1)
                        .executeUpdate();

                // ============================
                // CALCULATION
                // ============================
                double adjustmentAmount = paymentOption.equalsIgnoreCase("WAIVED")
                        ? monthlyFee
                        : discountAmount;

                double creditAmount = (paidAmount + adjustmentAmount) - monthlyFee;

                if (creditAmount < 0) {
                    creditAmount = 0;
                }

                String adjustmentStatus = creditAmount > 0 ? "PENDING" : "NA";

                // ============================
                // INSERT fee_adjustment (FIXED ORDER)
                // ============================
                em.createNativeQuery(
                        "INSERT INTO fee_adjustment ("
                        + "student_id, enrollment_id, category, month_for, monthly_fee, "
                        + "paid_amount, adjustment_amount, credit_amount, adjustment_status, "
                        + "note, user, status"
                        + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)"
                )
                        .setParameter(1, studentId)
                        .setParameter(2, enrollmentId)
                        .setParameter(3, paymentTypeValue)
                        .setParameter(4, saveMonth)
                        .setParameter(5, monthlyFee)
                        .setParameter(6, paidAmount)
                        .setParameter(7, adjustmentAmount)
                        .setParameter(8, creditAmount)
                        .setParameter(9, adjustmentStatus)
                        .setParameter(10, note)
                        .setParameter(11, username)
                        .setParameter(12, 1)
                        .executeUpdate();

// 🔥 UPDATE PAYMENT MASTER (FIXED)
// get payment id
                int paymentId = ((Number) em.createNativeQuery(
                        "SELECT student_fee_payments_id "
                        + "FROM student_fee_payments "
                        + "WHERE enrollment_id=? AND status=1"
                ).setParameter(1, enrollmentId).getSingleResult()).intValue();

// get current balance
                double currentBalance = ((Number) em.createNativeQuery(
                        "SELECT total_balance "
                        + "FROM student_fee_payments "
                        + "WHERE student_fee_payments_id=?"
                ).setParameter(1, paymentId).getSingleResult()).doubleValue();

// 🔥 deduction (DISCOUNT or WAIVED)
                double deduction = adjustmentAmount;

// prevent negative balance
                double newBalance = Math.max(currentBalance - deduction, 0);

// update ONLY balance (NOT total_paid)
                em.createNativeQuery(
                        "UPDATE student_fee_payments "
                        + "SET total_balance=?, payment_status=? "
                        + "WHERE student_fee_payments_id=?"
                )
                        .setParameter(1, newBalance)
                        .setParameter(2, newBalance == 0 ? "COMPLETED" : "ACTIVE")
                        .setParameter(3, paymentId)
                        .executeUpdate();
            }

            tx.commit();
            JOptionPane.showMessageDialog(null, "Saved successfully.");

        } catch (Exception e) {
            e.printStackTrace();

            if (tx.isActive()) {
                tx.rollback();
            }

        } finally {
            em.close();
        }
    }

//    public void saveDiscountWaive() {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//            DefaultTableModel model = (DefaultTableModel) bat_pay_table.getModel();
//
//            // ============================
//            // VALIDATIONS
//            // ============================
//            if (model.getRowCount() == 0) {
//                JOptionPane.showMessageDialog(null, "No students found.");
//                return;
//            }
//
//            String paymentOption = bat_pay_option_combo.getSelectedItem() != null
//                    ? bat_pay_option_combo.getSelectedItem().toString()
//                    : "";
//
//            if (paymentOption.equalsIgnoreCase("Select Option") || paymentOption.isEmpty()) {
//                JOptionPane.showMessageDialog(null, "Please select payment option.");
//                return;
//            }
//
//            String note = bat_pay_note_text.getText().trim();
//
//            if (note.isEmpty()) {
//                JOptionPane.showMessageDialog(null, "Note cannot be empty.");
//                return;
//            }
//
//            String selectedMonth = bat_pay_month_for_combo.getSelectedItem() != null
//                    ? bat_pay_month_for_combo.getSelectedItem().toString()
//                    : "";
//
//            if (selectedMonth.isEmpty()) {
//                JOptionPane.showMessageDialog(null, "Please select month.");
//                return;
//            }
//
//            // ============================
//            // CONVERT MONTH
//            // ============================
//            String saveMonth = "";
//
//            if (selectedMonth.contains(" - ")) {
//                String year = selectedMonth.split(" - ")[0].trim();
//                String monthName = selectedMonth.split(" - ")[1].trim();
//
//                int monthNo = generalMethods.getMonthNumber(monthName);
//                saveMonth = year + "-" + String.format("%02d", monthNo);
//            }
//
//            System.out.println("DEBUG → saveMonth: " + saveMonth);
//
//            String paymentTypeValue = paymentOption.equalsIgnoreCase("DISCOUNT")
//                    ? "DISCOUNT"
//                    : "ZERO";
//
//            tx.begin();
//
//            // ============================
//            // LOOP
//            // ============================
//            for (int i = 0; i < model.getRowCount(); i++) {
//
//                // ✅ FIXED INDEXES
//                Object studentIdObj = model.getValueAt(i, 9);
//                Object enrollmentIdObj = model.getValueAt(i, 10);
//                Object rowAmountObj = model.getValueAt(i, 6);
//                Object monthlyFeeObj = model.getValueAt(i, 4);
//
//                if (enrollmentIdObj == null) {
//                    continue;
//                }
//
//                int studentId = studentIdObj != null
//                        ? Integer.parseInt(studentIdObj.toString())
//                        : 0;
//
//                int enrollmentId = Integer.parseInt(enrollmentIdObj.toString());
//
//                // ✅ USE TABLE VALUE (NOT DB)
//                double monthlyFee = monthlyFeeObj != null
//                        ? GeneralMethods.parseCommaNumber(monthlyFeeObj.toString())
//                        : 0;
//
//                System.out.println("\n--- ROW " + (i + 1) + " ---");
//                System.out.println("Enrollment ID: " + enrollmentId);
//                System.out.println("Monthly Fee (FROM TABLE): " + monthlyFee);
//
//                // ============================
//                // SKIP EMPTY ROWS (BOTH TYPES)
//                // ============================
//                if (rowAmountObj == null || rowAmountObj.toString().trim().isEmpty()) {
//                    continue;
//                }
//
//                double discountAmount = 0;
//
//                if (paymentOption.equalsIgnoreCase("DISCOUNT")) {
//
//                    discountAmount = GeneralMethods.parseCommaNumber(rowAmountObj.toString());
//
//                    if (discountAmount <= 0) {
//                        continue;
//                    }
//
//                    if (discountAmount > monthlyFee) {
//                        JOptionPane.showMessageDialog(null,
//                                "Amount cannot exceed monthly fee.\nRow: " + (i + 1));
//                        tx.rollback();
//                        return;
//                    }
//
//                } else {
//                    // WAIVED
//                    discountAmount = 0;
//                }
//
//                // ============================
//                // CHECK INSTALLMENT
//                // ============================
//                List<Object[]> list = em.createNativeQuery(
//                        "SELECT student_fee_installments_id, IFNULL(amount_paid,0) "
//                        + "FROM student_fee_installments "
//                        + "WHERE enrollment_id = ? AND month_for = ? AND status = 1 "
//                        + "ORDER BY student_fee_installments_id DESC LIMIT 1"
//                )
//                        .setParameter(1, enrollmentId)
//                        .setParameter(2, saveMonth)
//                        .getResultList();
//
//                double paidAmount = 0;
//
//                if (!list.isEmpty()) {
//
//                    Object[] row = list.get(0);
//
//                    int installmentId = ((Number) row[0]).intValue();
//                    paidAmount = ((Number) row[1]).doubleValue();
//
//                    // ONLY UPDATE TYPE
//                    em.createNativeQuery(
//                            "UPDATE student_fee_installments "
//                            + "SET payment_type = ? "
//                            + "WHERE student_fee_installments_id = ?"
//                    )
//                            .setParameter(1, paymentTypeValue)
//                            .setParameter(2, installmentId)
//                            .executeUpdate();
//
//                } else {
//
//                    List<Object> paymentMasterList = em.createNativeQuery(
//                            "SELECT student_fee_payments_id "
//                            + "FROM student_fee_payments "
//                            + "WHERE enrollment_id = ? AND status = 1 LIMIT 1"
//                    )
//                            .setParameter(1, enrollmentId)
//                            .getResultList();
//
//                    int paymentId = paymentMasterList.isEmpty()
//                            ? 0
//                            : ((Number) paymentMasterList.get(0)).intValue();
//
//                    double insertAmount = paymentOption.equalsIgnoreCase("DISCOUNT")
//                            ? 0
//                            : 0;
//
//                    em.createNativeQuery(
//                            "INSERT INTO student_fee_installments ("
//                            + "student_fee_payments_id, enrollment_id, installment_no, "
//                            + "amount_paid, payment_date, payment_method, payment_type, "
//                            + "month_for, remarks, status"
//                            + ") VALUES (?,?,?,?,?,?,?,?,?,?)"
//                    )
//                            .setParameter(1, paymentId)
//                            .setParameter(2, enrollmentId)
//                            .setParameter(3, 0)
//                            .setParameter(4, insertAmount)
//                            .setParameter(5, new Date())
//                            .setParameter(6, "")
//                            .setParameter(7, paymentTypeValue)
//                            .setParameter(8, saveMonth)
//                            .setParameter(9, "FEE_ADJUSTMENT")
//                            .setParameter(10, 1)
//                            .executeUpdate();
//
//                    paidAmount = insertAmount;
//                }
//
//                // ============================
//                // CALCULATION
//                // ============================
//                double adjustmentAmount = paymentOption.equalsIgnoreCase("WAIVED")
//                        ? monthlyFee
//                        : discountAmount;
//
//                double creditAmount = (paidAmount + adjustmentAmount) - monthlyFee;
//
//                if (creditAmount < 0) {
//                    creditAmount = 0;
//                }
//
//                String adjustmentStatus = creditAmount > 0 ? "PENDING" : "NA";
//
//                // ============================
//                // INSERT fee_adjustment
//                // ============================
//                em.createNativeQuery(
//                        "INSERT INTO fee_adjustment ("
//                        + "student_id, enrollment_id, month_for, monthly_fee, "
//                        + "paid_amount, adjustment_amount, credit_amount, adjustment_status, "
//                        + "note, user, status"
//                        + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)"
//                )
//                        .setParameter(1, studentId)
//                        .setParameter(2, enrollmentId)
//                        .setParameter(3, paymentOption)
//                        .setParameter(4, saveMonth)
//                        .setParameter(5, monthlyFee)
//                        .setParameter(6, paidAmount)
//                        .setParameter(7, adjustmentAmount)
//                        .setParameter(8, creditAmount)
//                        .setParameter(9, adjustmentStatus)
//                        .setParameter(10, note)
//                        .setParameter(11, username)
//                        .setParameter(12, 1)
//                        .executeUpdate();
//            }
//
//            tx.commit();
//            JOptionPane.showMessageDialog(null, "Saved successfully.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//
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
        jLabel10 = new javax.swing.JLabel();
        bat_other_pay_total = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        bat_tr_table = new javax.swing.JTable();
        bat_tr_batch_course_combo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        buttonGradient4 = new Classes.ButtonGradient();
        jButton5 = new javax.swing.JButton();
        bat_tr_class_combo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        bat_tr_new_batch_combo = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        bat_tr_new_class_combo = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        bat_pay_table = new javax.swing.JTable();
        bat_pay_batch_course_combo = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        buttonGradient5 = new Classes.ButtonGradient();
        jButton6 = new javax.swing.JButton();
        bat_pay_month_for_combo = new javax.swing.JComboBox<>();
        bat_pay_amount_text = new javax.swing.JTextField();
        bat_pay_class_combo = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        bat_pay_option_combo = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        bat_pay_note_text = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        bat_other_pay_table = new javax.swing.JTable();
        bat_other_pay_batch_course_combo = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        buttonGradient6 = new Classes.ButtonGradient();
        jButton9 = new javax.swing.JButton();
        bat_other_pay_amount_text = new javax.swing.JTextField();
        bat_other_pay_class_combo = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        bat_other_pay_payments_combo = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();

        jLabel10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel10.setText("TotalStudents ");

        bat_other_pay_total.setEditable(false);
        bat_other_pay_total.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        bat_other_pay_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bat_other_pay_totalActionPerformed(evt);
            }
        });
        bat_other_pay_total.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bat_other_pay_totalKeyTyped(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        bat_tr_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Admission", "Student Name", "New Batch / Course", "New Class", "Payment Mode", "New Admission Fee", "New Monthly Fee", "student_id", "enrollment_id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bat_tr_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bat_tr_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(bat_tr_table);
        if (bat_tr_table.getColumnModel().getColumnCount() > 0) {
            bat_tr_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            bat_tr_table.getColumnModel().getColumn(1).setPreferredWidth(120);
            bat_tr_table.getColumnModel().getColumn(2).setPreferredWidth(200);
            bat_tr_table.getColumnModel().getColumn(3).setPreferredWidth(200);
            bat_tr_table.getColumnModel().getColumn(4).setPreferredWidth(80);
        }

        bat_tr_batch_course_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Current Batch");

        buttonGradient4.setText("TRANSFER");
        buttonGradient4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient4ActionPerformed(evt);
            }
        });

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

        bat_tr_class_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("Class");

        jLabel11.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel11.setText("Transfer Batch");

        bat_tr_new_batch_combo.setEditable(true);
        bat_tr_new_batch_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel14.setText("Class");

        bat_tr_new_class_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jButton7.setBackground(new java.awt.Color(102, 102, 102));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/enter24.png"))); // NOI18N
        jButton7.setToolTipText("Set fees to the table");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(bat_tr_batch_course_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(bat_tr_class_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 244, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(bat_tr_new_batch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(bat_tr_new_class_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(bat_tr_class_combo, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                                .addComponent(bat_tr_batch_course_combo)))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel11)
                                .addComponent(jLabel14))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(bat_tr_new_class_combo)
                                .addComponent(bat_tr_new_batch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("     Transfer     ", jPanel2);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Batch Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        bat_pay_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Admission", "Student Name", "Class", "Monthly Fee", "Last Paid Month", "Discount / Waive", "Month For", "Note", "st_id", "en_id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bat_pay_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bat_pay_tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(bat_pay_table);
        if (bat_pay_table.getColumnModel().getColumnCount() > 0) {
            bat_pay_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            bat_pay_table.getColumnModel().getColumn(1).setPreferredWidth(120);
            bat_pay_table.getColumnModel().getColumn(2).setPreferredWidth(200);
            bat_pay_table.getColumnModel().getColumn(3).setPreferredWidth(80);
            bat_pay_table.getColumnModel().getColumn(8).setPreferredWidth(150);
        }

        bat_pay_batch_course_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel6.setText("Batch / Course");

        jLabel13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel13.setText("Month For");

        jLabel7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel7.setText("Amount");

        buttonGradient5.setText("PAY");
        buttonGradient5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient5ActionPerformed(evt);
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

        bat_pay_month_for_combo.setEditable(true);
        bat_pay_month_for_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        bat_pay_amount_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        bat_pay_amount_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bat_pay_amount_textActionPerformed(evt);
            }
        });
        bat_pay_amount_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bat_pay_amount_textKeyTyped(evt);
            }
        });

        bat_pay_class_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel8.setText("Class");

        bat_pay_option_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        bat_pay_option_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DISCOUNT", "WAIVED" }));
        bat_pay_option_combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bat_pay_option_comboActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel9.setText("Option");

        jButton8.setBackground(new java.awt.Color(102, 102, 102));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/enter24.png"))); // NOI18N
        jButton8.setToolTipText("Set fees to the table");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        bat_pay_note_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        bat_pay_note_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bat_pay_note_textActionPerformed(evt);
            }
        });
        bat_pay_note_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bat_pay_note_textKeyTyped(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel12.setText("Note");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(bat_pay_batch_course_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(bat_pay_class_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_pay_option_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(bat_pay_month_for_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_pay_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(bat_pay_note_text, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel8))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(bat_pay_class_combo, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                                .addComponent(bat_pay_batch_course_combo)))
                        .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(jLabel13)
                                .addComponent(jLabel12))
                            .addGap(6, 6, 6)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(bat_pay_option_combo, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(bat_pay_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(bat_pay_note_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(bat_pay_month_for_combo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("     Waiver / Discount     ", jPanel3);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Batch Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        bat_other_pay_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Admission", "Student Name", "Amount", "st_id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bat_other_pay_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bat_other_pay_tableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(bat_other_pay_table);
        if (bat_other_pay_table.getColumnModel().getColumnCount() > 0) {
            bat_other_pay_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            bat_other_pay_table.getColumnModel().getColumn(1).setPreferredWidth(120);
            bat_other_pay_table.getColumnModel().getColumn(2).setPreferredWidth(350);
            bat_other_pay_table.getColumnModel().getColumn(4).setMinWidth(0);
            bat_other_pay_table.getColumnModel().getColumn(4).setPreferredWidth(0);
            bat_other_pay_table.getColumnModel().getColumn(4).setMaxWidth(0);
        }

        bat_other_pay_batch_course_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel15.setText("Batch / Course");

        jLabel17.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel17.setText("Amount");

        buttonGradient6.setText("PAY");
        buttonGradient6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient6ActionPerformed(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(102, 102, 102));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search16.png"))); // NOI18N
        jButton9.setToolTipText("Course Enrolment");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        bat_other_pay_amount_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        bat_other_pay_amount_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bat_other_pay_amount_textActionPerformed(evt);
            }
        });
        bat_other_pay_amount_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bat_other_pay_amount_textKeyTyped(evt);
            }
        });

        bat_other_pay_class_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel18.setText("Class");

        bat_other_pay_payments_combo.setEditable(true);
        bat_other_pay_payments_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        bat_other_pay_payments_combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bat_other_pay_payments_comboActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel19.setText("Other Payments");

        jButton10.setBackground(new java.awt.Color(102, 102, 102));
        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/enter24.png"))); // NOI18N
        jButton10.setToolTipText("Set fees to the table");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(bat_other_pay_batch_course_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(bat_other_pay_class_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 273, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_other_pay_payments_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_other_pay_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addGap(18, 18, 18)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(jLabel18))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(bat_other_pay_class_combo, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                                .addComponent(bat_other_pay_batch_course_combo)))
                        .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(buttonGradient6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel19))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bat_other_pay_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bat_other_pay_payments_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("     Other Payments     ", jPanel5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bat_other_pay_total, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bat_other_pay_total, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
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

    private void bat_other_pay_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bat_other_pay_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_other_pay_totalActionPerformed

    private void bat_other_pay_totalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bat_other_pay_totalKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_other_pay_totalKeyTyped

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        loadBatchTransferStudents(bat_tr_table, bat_tr_batch_course_combo, bat_tr_class_combo);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        saveBatchTransfer();
    }//GEN-LAST:event_buttonGradient4ActionPerformed

    private void bat_tr_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bat_tr_tableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_tr_tableMouseClicked

    private void bat_pay_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bat_pay_tableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_pay_tableMouseClicked

    private void buttonGradient5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient5ActionPerformed
        saveDiscountWaive();
    }//GEN-LAST:event_buttonGradient5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        loadDiscountWaiveStudents(bat_pay_table, bat_pay_batch_course_combo, bat_pay_class_combo);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void bat_pay_amount_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bat_pay_amount_textActionPerformed
        bat_pay_note_text.requestFocus();
    }//GEN-LAST:event_bat_pay_amount_textActionPerformed

    private void bat_pay_amount_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bat_pay_amount_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_pay_amount_textKeyTyped

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        setNewBatchDetailsToTable(bat_tr_table, bat_tr_new_batch_combo, bat_tr_new_class_combo);

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed

        try {
            DefaultTableModel model = (DefaultTableModel) bat_pay_table.getModel();

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "No students found."
                );
                return;
            }

            // =====================================================
            // GET VALUES
            // =====================================================
            String amountText = bat_pay_amount_text.getText() != null
                    ? bat_pay_amount_text.getText().trim()
                    : "";

            String monthFor = bat_pay_month_for_combo.getSelectedItem() != null
                    ? bat_pay_month_for_combo.getSelectedItem().toString()
                    : "";

            String note = bat_pay_note_text.getText() != null
                    ? bat_pay_note_text.getText().trim()
                    : "";

            // =====================================================
            // VALIDATION
            // =====================================================
            // Amount empty → set 0
            if (amountText.isEmpty()) {
                amountText = "0";
            }

            // Note empty → prevent save
            if (note.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Note cannot be empty."
                );
                return;
            }

            // Month validation
            if (monthFor.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Please select month."
                );
                return;
            }

            // =====================================================
            // SET ALL ROWS
            // index 5 -> Amount
            // index 6 -> Month For
            // index 7 -> Note
            // =====================================================
            for (int i = 0; i < model.getRowCount(); i++) {

                model.setValueAt(amountText, i, 6);
                model.setValueAt(monthFor, i, 7);
                model.setValueAt(note, i, 8);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton8ActionPerformed

    private void bat_pay_note_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bat_pay_note_textActionPerformed
        jButton8.doClick();
    }//GEN-LAST:event_bat_pay_note_textActionPerformed

    private void bat_pay_note_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bat_pay_note_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_pay_note_textKeyTyped

    private void bat_pay_option_comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bat_pay_option_comboActionPerformed

        if (bat_pay_option_combo.getSelectedItem().toString().equalsIgnoreCase("DISCOUNT")) {
            bat_pay_amount_text.setEnabled(true);
            bat_pay_amount_text.setText("");
            bat_pay_amount_text.requestFocus();
        } else if (bat_pay_option_combo.getSelectedItem().toString().equalsIgnoreCase("WAIVED")) {
            bat_pay_amount_text.setEnabled(false);
            bat_pay_amount_text.setText("0");
        }

    }//GEN-LAST:event_bat_pay_option_comboActionPerformed

    private void bat_other_pay_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bat_other_pay_tableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_other_pay_tableMouseClicked

    private void buttonGradient6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient6ActionPerformed

        try {

            EntityManager em = HibernateConfig.getEntityManager();

            List<OtherFeeAssignment> assignments = new ArrayList<>();

            int feeTypesId = generalMethods.extractIdFromCombo(
                    bat_other_pay_payments_combo.getEditor().getItem().toString());

            double amount = GeneralMethods.parseCommaNumber(
                    bat_other_pay_amount_text.getText());

            // Loop ALL rows
            for (int row = 0; row < bat_other_pay_table.getRowCount(); row++) {

                int studentId = Integer.parseInt(
                        bat_other_pay_table.getValueAt(row, 4).toString());

                Student student = em.find(Student.class, studentId);

                if (student == null) {
                    continue;
                }

                OtherFeeAssignment assignment = new OtherFeeAssignment();
                assignment.setStudent(student);
                assignment.setFeeType(feeTypesId);
                assignment.setAmount(amount);
                assignment.setAssignedDate(new Date());
                assignment.setFeeStatus("PENDING");
                assignment.setUser(username);
                assignment.setStatus(1);

                assignments.add(assignment);
            }

            new OtherFeeAssignmentDAO().saveAll(assignments);

            em.close();
            JOptionPane.showMessageDialog(null, "Fees assigned successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_buttonGradient6ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        loadBatchTransferStudents(bat_other_pay_table, bat_other_pay_batch_course_combo, bat_other_pay_class_combo);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void bat_other_pay_amount_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bat_other_pay_amount_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_other_pay_amount_textActionPerformed

    private void bat_other_pay_amount_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bat_other_pay_amount_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_other_pay_amount_textKeyTyped

    private void bat_other_pay_payments_comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bat_other_pay_payments_comboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_other_pay_payments_comboActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

        try {
            DefaultTableModel model = (DefaultTableModel) bat_other_pay_table.getModel();

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "No students found."
                );
                return;
            }

            // =====================================================
            // GET VALUES
            // =====================================================
            String amountText = bat_other_pay_amount_text.getText();
            String payments = bat_other_pay_payments_combo.getEditor().getItem().toString();

            if (amountText.isEmpty() || payments.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select the payments");
                return;
            }
            for (int i = 0; i < model.getRowCount(); i++) {

                model.setValueAt(amountText, i, 3);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton10ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bat_other_pay_amount_text;
    public static javax.swing.JComboBox<String> bat_other_pay_batch_course_combo;
    public static javax.swing.JComboBox<String> bat_other_pay_class_combo;
    public static javax.swing.JComboBox<String> bat_other_pay_payments_combo;
    private javax.swing.JTable bat_other_pay_table;
    private javax.swing.JTextField bat_other_pay_total;
    private javax.swing.JTextField bat_pay_amount_text;
    public static javax.swing.JComboBox<String> bat_pay_batch_course_combo;
    public static javax.swing.JComboBox<String> bat_pay_class_combo;
    public static javax.swing.JComboBox<String> bat_pay_month_for_combo;
    private javax.swing.JTextField bat_pay_note_text;
    public static javax.swing.JComboBox<String> bat_pay_option_combo;
    private javax.swing.JTable bat_pay_table;
    public static javax.swing.JComboBox<String> bat_tr_batch_course_combo;
    public static javax.swing.JComboBox<String> bat_tr_class_combo;
    public static javax.swing.JComboBox<String> bat_tr_new_batch_combo;
    public static javax.swing.JComboBox<String> bat_tr_new_class_combo;
    private javax.swing.JTable bat_tr_table;
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradient buttonGradient5;
    private Classes.ButtonGradient buttonGradient6;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

}
