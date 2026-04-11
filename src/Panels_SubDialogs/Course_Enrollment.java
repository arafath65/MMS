package Panels_SubDialogs;

import Classes.GeneralMethods;
import Classes.GradientButton;
import Classes.LogHelper;
import Classes.ModernDialog;
import Classes.ModernMessage;
import Classes.TableGradientCell;
import Dashboard.Dashboard;
import Entities.Settings.Course;
import Entities.Settings.StudentClass;
import Entities.Student_Management.CourseEnrollment;
import Entities.Student_Management.Student;
import Entities.Student_Management.StudentFeePayments;
import JPA_DAO.Settings.ClassDAO;
import JPA_DAO.Settings.CourseDAO;
import JPA_DAO.Student_Management.CourseEnrollmentDAO;
import JPA_DAO.Student_Management.StudentDAO;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Course_Enrollment extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Course_Enrollment.class.getName());

    CourseDAO dao = new CourseDAO();
    LogHelper logHelper = new LogHelper();

    int courseID = 0;
    private Integer studentId;
    String username;
    String role;

    public Course_Enrollment(java.awt.Frame parent, boolean modal, int studentId, String username, String role) {
        super(parent, modal);
        this.studentId = studentId;
        this.username = username;
        this.role = role;
        initComponents();

        stm_ce_table.setDefaultRenderer(Object.class, new TableGradientCell());
        stm_ce_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        loadCourseEnrollmentTable(studentId);
        loadClassesToCombo();

        jComboPopulates();

        int lastColumnIndex = stm_ce_table.getColumnCount() - 1;

        stm_ce_table.getColumnModel().getColumn(lastColumnIndex).setMinWidth(0);
        stm_ce_table.getColumnModel().getColumn(lastColumnIndex).setMaxWidth(0);
        stm_ce_table.getColumnModel().getColumn(lastColumnIndex).setWidth(0);

    }

    private void jComboPopulates() {
        stm_ce_course_name_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                String input = stm_ce_course_name_combo.getEditor().getItem().toString().trim();

                if (input.isEmpty()) {
                    return;
                }

                List<Course> list = dao.searchActiveCourses(input);

                // Save current text
                String typedText = input;

                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

                for (Course c : list) {
                    String display = c.getCourseName() + " [" + c.getBatch() + "]";
                    model.addElement(display);
                }

                stm_ce_course_name_combo.setModel(model);

                // Restore typed text
                stm_ce_course_name_combo.getEditor().setItem(typedText);

                stm_ce_course_name_combo.showPopup();
            }
        });

        setupComboSelectionListeners1(stm_ce_course_name_combo, stm_ce_admission_fee_textfield, "ADMISSION");
    }

    private boolean itemSelectedByUser1 = false;

    public void setupComboSelectionListeners1(JComboBox<String> comboBox,
            JComponent nextFocusComponent,
            String type) {

        comboBox.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                itemSelectedByUser1 = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

                if (!itemSelectedByUser1) {
                    return;
                }

                Object selected = comboBox.getSelectedItem();
                if (selected == null) {
                    return;
                }

                String selectedValue = selected.toString().trim();
                if (selectedValue.isEmpty()) {
                    return;
                }

                // ✅ Extract course name & batch
                String courseName = selectedValue.substring(0, selectedValue.indexOf("[")).trim();
                String batch = selectedValue.substring(
                        selectedValue.indexOf("[") + 1,
                        selectedValue.indexOf("]")
                ).trim();

                // ✅ Fetch from DB
                CourseDAO dao = new CourseDAO();
                Course course = dao.findByCourseNameAndBatch(courseName, batch);

                if (course != null) {

                    // ✅ Set Fields
                    stm_ce_payment_mode_textfield.setText(course.getPaymentMode());
                    stm_ce_admission_fee_textfield.setText(GeneralMethods.formatWithComma(course.getAdmissionFee()));
                    stm_ce_fee_textfield.setText(GeneralMethods.formatWithComma(course.getFee()));
                    stm_ce_course_start_textfield.setText(GeneralMethods.getMonthName(course.getEnrolMonth()) + " - " + course.getEnrolYear());
                    stm_ce_course_end_textfield.setText(GeneralMethods.getMonthName(course.getCompMonth()) + " - " + course.getCompYear());
                    courseID = course.getCourseId();

                }

                nextFocusComponent.requestFocus();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                itemSelectedByUser1 = false;
            }
        });

        comboBox.addActionListener(e -> {
            if (comboBox.isPopupVisible()) {
                itemSelectedByUser1 = true;
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

    private void loadCourseEnrollmentTable(int studentId) {

        DefaultTableModel model = (DefaultTableModel) stm_ce_table.getModel();
        model.setRowCount(0);

        CourseEnrollmentDAO enrollmentDAO = new CourseEnrollmentDAO();
        CourseDAO courseDAO = new CourseDAO();

        List<CourseEnrollment> enrollments = enrollmentDAO.findByStudentId(studentId);

        int seq = 1;

        for (CourseEnrollment ce : enrollments) {

            Course course = courseDAO.findById(ce.getCourseId());

            if (course != null) {

                String con_sMonth = GeneralMethods.getMonthName(course.getEnrolMonth());
                String con_eMonth = GeneralMethods.getMonthName(course.getCompMonth());
                String start = con_sMonth + " - " + course.getEnrolYear();
                String end = con_eMonth + " - " + course.getCompYear();

                model.addRow(new Object[]{
                    seq++,
                    course.getBatch(),
                    course.getCourseName(),
                    ce.getClassName(),
                    start,
                    end,
                    course.getPaymentMode(),
                    GeneralMethods.formatWithComma(ce.getAdmissionFee()),
                    GeneralMethods.formatWithComma(ce.getFee()),
                    ce.getCourseStatus(),
                    ce.getEnrollmentId()
                });

                stm_ce_table.getColumnModel().getColumn(9).setMinWidth(0);
                stm_ce_table.getColumnModel().getColumn(9).setMaxWidth(0);
                stm_ce_table.getColumnModel().getColumn(9).setWidth(0);

            }
        }
    }

    private void loadClassesToCombo() {

        ClassDAO dao = new ClassDAO();
        List<StudentClass> classList = dao.findAll();

        stm_ce_class_name_combo.removeAllItems();

        // Optional: Add default item
        // stm_ce_class_name_combo.addItem(null);
        for (StudentClass sc : classList) {
            stm_ce_class_name_combo.addItem(sc.getClassName());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        stm_ce_course_name_combo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        stm_ce_payment_mode_textfield = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        stm_ce_admission_fee_textfield = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        stm_ce_table = new javax.swing.JTable();
        stm_ce_fee_textfield = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        stm_ce_course_start_textfield = new javax.swing.JTextField();
        stm_ce_course_end_textfield = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        stm_ce_class_name_combo = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Course Enrolment Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(232, 232, 232));
        jLabel1.setText("Course Name [Batch]");

        stm_ce_course_name_combo.setEditable(true);
        stm_ce_course_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(232, 232, 232));
        jLabel2.setText("Payment Mode");

        stm_ce_payment_mode_textfield.setEditable(false);
        stm_ce_payment_mode_textfield.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ce_payment_mode_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ce_payment_mode_textfieldActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(232, 232, 232));
        jLabel3.setText("Admission Fee");

        stm_ce_admission_fee_textfield.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ce_admission_fee_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ce_admission_fee_textfieldActionPerformed(evt);
            }
        });

        stm_ce_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Batch", "Course Name", "Class", "Course Start", "Course End", "Payment Mode", "Admission Fee", "Fees", "Status", "EnrollmentId"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stm_ce_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stm_ce_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(stm_ce_table);
        if (stm_ce_table.getColumnModel().getColumnCount() > 0) {
            stm_ce_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            stm_ce_table.getColumnModel().getColumn(1).setPreferredWidth(120);
            stm_ce_table.getColumnModel().getColumn(2).setPreferredWidth(250);
            stm_ce_table.getColumnModel().getColumn(4).setPreferredWidth(120);
            stm_ce_table.getColumnModel().getColumn(5).setPreferredWidth(120);
            stm_ce_table.getColumnModel().getColumn(6).setPreferredWidth(120);
            stm_ce_table.getColumnModel().getColumn(7).setPreferredWidth(100);
            stm_ce_table.getColumnModel().getColumn(8).setPreferredWidth(100);
            stm_ce_table.getColumnModel().getColumn(9).setPreferredWidth(100);
        }

        stm_ce_fee_textfield.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ce_fee_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ce_fee_textfieldActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(232, 232, 232));
        jLabel4.setText("Fees Amount");

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(232, 232, 232));
        jLabel5.setText("Course Start");

        stm_ce_course_start_textfield.setEditable(false);
        stm_ce_course_start_textfield.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ce_course_start_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ce_course_start_textfieldActionPerformed(evt);
            }
        });

        stm_ce_course_end_textfield.setEditable(false);
        stm_ce_course_end_textfield.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ce_course_end_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ce_course_end_textfieldActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(232, 232, 232));
        jLabel6.setText("Course End");

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        stm_ce_class_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(stm_ce_course_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stm_ce_class_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(stm_ce_payment_mode_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(stm_ce_admission_fee_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(stm_ce_fee_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ce_course_start_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ce_course_end_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(stm_ce_admission_fee_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(stm_ce_course_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(stm_ce_payment_mode_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(stm_ce_class_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(stm_ce_fee_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(stm_ce_course_end_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stm_ce_course_start_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stm_ce_payment_mode_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ce_payment_mode_textfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ce_payment_mode_textfieldActionPerformed

    private void stm_ce_admission_fee_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ce_admission_fee_textfieldActionPerformed
        stm_ce_fee_textfield.requestFocus();
    }//GEN-LAST:event_stm_ce_admission_fee_textfieldActionPerformed

    private void stm_ce_fee_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ce_fee_textfieldActionPerformed
        jButton2.doClick();
    }//GEN-LAST:event_stm_ce_fee_textfieldActionPerformed

    private void stm_ce_course_start_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ce_course_start_textfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ce_course_start_textfieldActionPerformed

    private void stm_ce_course_end_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ce_course_end_textfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ce_course_end_textfieldActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        try {
            DefaultTableModel model = (DefaultTableModel) stm_ce_table.getModel();

            String course = stm_ce_course_name_combo.getEditor().getItem().toString();

            if (course.equalsIgnoreCase("")) {
                JOptionPane.showMessageDialog(null, "Course name can not be empty...", "Warning!", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String courseName = course.substring(0, course.indexOf("[")).trim();
            String batch = course.substring(course.indexOf("[") + 1, course.indexOf("]")).trim();
            String class_name = stm_ce_class_name_combo.getSelectedItem().toString();

            String payment_mode = stm_ce_payment_mode_textfield.getText();
            String admissionFee = stm_ce_admission_fee_textfield.getText();
            String fee = stm_ce_fee_textfield.getText();

            int enrol_year = Integer.parseInt(stm_ce_course_start_textfield.getText().split(" - ")[1]);
            String enrol_month = stm_ce_course_start_textfield.getText().split(" - ")[0];

            int comp_year = Integer.parseInt(stm_ce_course_end_textfield.getText().split(" - ")[1]);
            String comp_month = stm_ce_course_end_textfield.getText().split(" - ")[0];

            // 🔴 DUPLICATE CHECK
            boolean duplicate = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                String tBatch = model.getValueAt(i, 1).toString();
                String tCourse = model.getValueAt(i, 2).toString();
                String tClass = model.getValueAt(i, 3).toString();
                String tEnrolYear = model.getValueAt(i, 4).toString();
                String tCompYear = model.getValueAt(i, 5).toString();
                String tPaymentMode = model.getValueAt(i, 6).toString();
                String aFee = model.getValueAt(i, 7).toString();
                String tFee = model.getValueAt(i, 8).toString();
                String stat = model.getValueAt(i, 9).toString();

                if (tBatch.equalsIgnoreCase(batch)
                        && tCourse.equalsIgnoreCase(course)
                        && tClass.equalsIgnoreCase(class_name)
                        && tEnrolYear.equalsIgnoreCase(stm_ce_course_start_textfield.getText())
                        && tCompYear.equalsIgnoreCase(stm_ce_course_end_textfield.getText())
                        && tPaymentMode.equalsIgnoreCase(payment_mode)
                        && aFee.equals(admissionFee)
                        && tFee.equals(fee)
                        && stat.equals("ACTIVE")) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                JOptionPane.showMessageDialog(null, "This course entry already exists!", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ Create CourseEnrollment properly
            CourseEnrollment ce = new CourseEnrollment();

            // Fetch the Student entity from DB (you need a DAO)
            Student s = new StudentDAO().findById(studentId); // <-- implement findById
            ce.setStudent(s);

            ce.setCourseId(courseID); // Integer courseID
            ce.setClassName(class_name);
            ce.setAdmissionFee(GeneralMethods.parseCommaNumber(admissionFee));
            ce.setFee(GeneralMethods.parseCommaNumber(fee));
            ce.setCourseStatus("ACTIVE");
            ce.setStatus(1); // Boolean, not int

            StudentFeePayments fp = new StudentFeePayments();

            double totalFee = GeneralMethods.parseCommaNumber(fee);

            fp.setStudent(s);
            fp.setEnrollment(ce);

            fp.setTotalFee(totalFee);
            fp.setTotalPaid(0);
            fp.setTotalBalance(totalFee);

            fp.setPaymentType("");
            fp.setCourseType(payment_mode);
            fp.setPaymentStatus("ACTIVE");
            fp.setRemarks("COURSE_ENROLLMENT");

            fp.setCreatedAt(new Date());
            fp.setLastMofidied(new Date());
            fp.setUser(Dashboard.main_username.getText());
            fp.setStatus(true);

            ce.setFeePayments(fp);
            new CourseEnrollmentDAO().save(ce);

            // ✅ LOG: Course Enrollment
            logHelper.log(
                    "COURSE_ENROLLMENT",
                    ce.getEnrollmentId(), // Assuming ID is generated after save
                    "COURSE ENROLLMENT CREATE",
                    "Admission: " + s.getAdmissionNo(),
                    ce.getFee(),
                    String.format("Enrolled student: %s into %s [%s]. Class: %s",
                            s.getFullName(), courseName, batch, class_name),
                    username
            );

            model.addRow(new Object[]{
                model.getRowCount() + 1,
                batch,
                course,
                class_name,
                enrol_month + " - " + enrol_year,
                comp_month + " - " + comp_year,
                payment_mode,
                GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(admissionFee)),
                GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(fee)),
                ce.getEnrollmentId(),});

            stm_ce_course_name_combo.removeAllItems();
            stm_ce_class_name_combo.setSelectedIndex(0);
            stm_ce_payment_mode_textfield.setText("");
            stm_ce_admission_fee_textfield.setText("");
            stm_ce_fee_textfield.setText("");
            stm_ce_course_start_textfield.setText("");
            stm_ce_course_end_textfield.setText("");
            stm_ce_course_name_combo.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton2ActionPerformed

    private void stm_ce_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stm_ce_tableMouseClicked
        int row = stm_ce_table.getSelectedRow();
        int column = stm_ce_table.getSelectedColumn();

        if (row != -1 && column == 8) { // Status column

//            int enrollmentId = Integer.parseInt(stm_ce_table.getModel().getValueAt(row, 10).toString());
//            String batch = stm_ce_table.getModel().getValueAt(row, 1).toString();
//            String courseName = stm_ce_table.getModel().getValueAt(row, 2).toString();
            showEnrollmentActionDialog(row);

        }
    }//GEN-LAST:event_stm_ce_tableMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Course_Enrollment dialog = new Course_Enrollment(new javax.swing.JFrame(), true, 0, "", "");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField stm_ce_admission_fee_textfield;
    private javax.swing.JComboBox<String> stm_ce_class_name_combo;
    private javax.swing.JTextField stm_ce_course_end_textfield;
    private javax.swing.JComboBox<String> stm_ce_course_name_combo;
    private javax.swing.JTextField stm_ce_course_start_textfield;
    private javax.swing.JTextField stm_ce_fee_textfield;
    private javax.swing.JTextField stm_ce_payment_mode_textfield;
    private javax.swing.JTable stm_ce_table;
    // End of variables declaration//GEN-END:variables
private void rearrangeTableSeq(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0); // SEQ column index = 0
        }
    }

//    private void showEnrollmentActionDialog(int rowIndex) {
//
//        // ✅ Get enrollment ID from hidden last column
//        int lastColumnIndex = stm_ce_table.getColumnModel().getColumnCount() - 1;
//        int enrollmentId = Integer.parseInt(
//                stm_ce_table.getModel().getValueAt(rowIndex, lastColumnIndex).toString()
//        );
//
//        // ✅ Load from DB using em.find()
//        CourseEnrollmentDAO dao = new CourseEnrollmentDAO();
//        CourseEnrollment enrollment = dao.findById(enrollmentId);
//
//        JDialog dialog = new JDialog(this);
//        dialog.setUndecorated(true);
//        dialog.setSize(460, 340);
//        dialog.setLayout(null);
//        dialog.setBackground(new Color(0, 0, 0, 0));
//        dialog.setLocationRelativeTo(this);
//
//        JPanel panel = new JPanel(null) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                        RenderingHints.VALUE_ANTIALIAS_ON);
//
//                g2.setColor(new Color(0, 0, 0, 120));
//                g2.fillRoundRect(10, 10, getWidth() - 10, getHeight() - 10, 30, 30);
//
//                g2.setColor(Color.decode("#2B2B2B"));
//                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
//
//                g2.dispose();
//            }
//        };
//
//        panel.setOpaque(false);
//        panel.setBounds(0, 0, 460, 340);
//
//        JLabel title = new JLabel("Course Enrollment Action");
//        title.setBounds(30, 25, 350, 30);
//        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        title.setForeground(Color.WHITE);
//        panel.add(title);
//
//        JCheckBox active = createWhiteCheckbox("ACTIVE");
//        JCheckBox completed = createWhiteCheckbox("COMPLETED");
//        JCheckBox suspended = createWhiteCheckbox("SUSPENDED");
//        JCheckBox dropped = createWhiteCheckbox("DROPPED OUT");
//
//        active.setBounds(40, 80, 200, 30);
//        completed.setBounds(40, 115, 200, 30);
//        suspended.setBounds(40, 150, 200, 30);
//        dropped.setBounds(40, 185, 200, 30);
//
//        ButtonGroup group = new ButtonGroup();
//        group.add(active);
//        group.add(completed);
//        group.add(suspended);
//        group.add(dropped);
//
//        panel.add(active);
//        panel.add(completed);
//        panel.add(suspended);
//        panel.add(dropped);
//
//        // ✅ Auto select checkbox from DB value
//        if (enrollment != null && enrollment.getCourseStatus() != null) {
//
//            switch (enrollment.getCourseStatus()) {
//                case "ACTIVE":
//                    active.setSelected(true);
//                    break;
//                case "COMPLETED":
//                    completed.setSelected(true);
//                    break;
//                case "SUSPENDED":
//                    suspended.setSelected(true);
//                    break;
//                case "DROPPED OUT":
//                    dropped.setSelected(true);
//                    break;
//            }
//        }
//
//        JButton deleteBtn = createAnimatedGradientButton(
//                "DELETE",
//                new Color(170, 0, 0),
//                new Color(255, 70, 70)
//        );
//        deleteBtn.setBounds(30, 250, 120, 42);
//
//        JButton changeBtn = createAnimatedGradientButton(
//                "CHANGE STATUS",
//                new Color(0, 102, 204),
//                new Color(0, 180, 255)
//        );
//        changeBtn.setBounds(160, 250, 170, 42);
//
//        JButton cancelBtn = createAnimatedGradientButton(
//                "CANCEL",
//                Color.decode("#F09819"),
//                Color.decode("#FF512F")
//        );
//        cancelBtn.setBounds(340, 250, 100, 42);
//
//        panel.add(deleteBtn);
//        panel.add(changeBtn);
//        panel.add(cancelBtn);
//
//        dialog.add(panel);
//
//        // ✅ DELETE (Soft Delete)
//        deleteBtn.addActionListener(e -> {
//            dao.softDelete(enrollmentId);
//            ((DefaultTableModel) stm_ce_table.getModel()).removeRow(rowIndex);
//            dialog.dispose();
//        });
//
//        // ✅ CHANGE STATUS
//        changeBtn.addActionListener(e -> {
//
//            String newStatus = null;
//
//            if (active.isSelected()) {
//                newStatus = "ACTIVE";
//            } else if (completed.isSelected()) {
//                newStatus = "COMPLETED";
//            } else if (suspended.isSelected()) {
//                newStatus = "SUSPENDED";
//            } else if (dropped.isSelected()) {
//                newStatus = "DROPPED OUT";
//            }
//
//            if (newStatus == null) {
//                JOptionPane.showMessageDialog(dialog, "Select one status");
//                return;
//            }
//
//            dao.updateStatus(enrollmentId, newStatus);
//
//            // update table column (adjust status column index if needed)
//          //  stm_ce_table.setValueAt(newStatus, rowIndex, 8);
//
//            dialog.dispose();
//        });
//
//        cancelBtn.addActionListener(e -> dialog.dispose());
//
//        dialog.setVisible(true);
//    }
    private void showEnrollmentActionDialog(int rowIndex) {

        // ✅ Get enrollment ID from hidden last column
        int lastColumnIndex = stm_ce_table.getColumnModel().getColumnCount() - 1;
        int enrollmentId = Integer.parseInt(
                stm_ce_table.getModel().getValueAt(rowIndex, lastColumnIndex).toString()
        );
        Window parent = SwingUtilities.getWindowAncestor(this);

        CourseEnrollmentDAO dao = new CourseEnrollmentDAO();
        CourseEnrollment enrollment = dao.findById(enrollmentId);

        // ✅ Use ModernDialog instead of JDialog
        ModernDialog dialog = new ModernDialog((Frame) parent, 460, 340);
        JPanel panel = dialog.getContentPanel();

        // ===== TITLE =====
        JLabel title = new JLabel("Course Enrollment Action");
        title.setBounds(30, 25, 350, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        panel.add(title);

        // ===== CHECKBOXES =====
        JCheckBox active = createWhiteCheckbox("ACTIVE");
        JCheckBox completed = createWhiteCheckbox("COMPLETED");
        JCheckBox suspended = createWhiteCheckbox("SUSPENDED");
        JCheckBox dropped = createWhiteCheckbox("DROPPED OUT");

        active.setBounds(40, 80, 200, 30);
        completed.setBounds(40, 115, 200, 30);
        suspended.setBounds(40, 150, 200, 30);
        dropped.setBounds(40, 185, 200, 30);

        ButtonGroup group = new ButtonGroup();
        group.add(active);
        group.add(completed);
        group.add(suspended);
        group.add(dropped);

        panel.add(active);
        panel.add(completed);
        panel.add(suspended);
        panel.add(dropped);

        // ===== AUTO SELECT FROM DB =====
        if (enrollment != null && enrollment.getCourseStatus() != null) {

            switch (enrollment.getCourseStatus()) {
                case "ACTIVE":
                    active.setSelected(true);
                    break;
                case "COMPLETED":
                    completed.setSelected(true);
                    break;
                case "SUSPENDED":
                    suspended.setSelected(true);
                    break;
                case "DROPPED OUT":
                    dropped.setSelected(true);
                    break;
            }
        }

        // ===== BUTTONS (Using GradientButton Class) =====
        GradientButton deleteBtn = new GradientButton(
                "DELETE",
                new Color(170, 0, 0),
                new Color(255, 70, 70)
        );
        deleteBtn.setBounds(30, 250, 120, 42);

        GradientButton changeBtn = new GradientButton(
                "CHANGE STATUS",
                new Color(0, 102, 204),
                new Color(0, 180, 255)
        );
        changeBtn.setBounds(160, 250, 170, 42);

        GradientButton cancelBtn = new GradientButton(
                "CANCEL",
                Color.decode("#F09819"),
                Color.decode("#FF512F")
        );
        cancelBtn.setBounds(340, 250, 100, 42);

        panel.add(deleteBtn);
        panel.add(changeBtn);
        panel.add(cancelBtn);

        // ===== DELETE ACTION =====
        deleteBtn.addActionListener(e -> {
            dao.softDelete(enrollmentId);

            // ✅ LOG: Enrollment Deletion
            logHelper.log(
                    "COURSE_ENROLLMENT",
                    enrollmentId,
                    "COURSE ENROLLMENT DELETE",
                    "Enrollment ID: " + enrollmentId,
                    0.0,
                    "Student removed from course enrollment (Soft Delete).",
                    username
            );

            ((DefaultTableModel) stm_ce_table.getModel()).removeRow(rowIndex);
            dialog.dispose();
        });

        // ===== CHANGE STATUS ACTION =====
        changeBtn.addActionListener(e -> {

            String newStatus = null;

            if (active.isSelected()) {
                newStatus = "ACTIVE";
            } else if (completed.isSelected()) {
                newStatus = "COMPLETED";
            } else if (suspended.isSelected()) {
                newStatus = "SUSPENDED";
            } else if (dropped.isSelected()) {
                newStatus = "DROPPED OUT";
            }

            if (newStatus == null) {
                ModernMessage.showMessage((Frame) parent, "Select one status");
                return;
            }

            dao.updateStatus(enrollmentId, newStatus);

            // ✅ LOG: Status Change
            logHelper.log(
                    "COURSE_ENROLLMENT",
                    enrollmentId,
                    "COURSE ENROLLMENT STATUS_CHANGE",
                    "New Status: " + newStatus,
                    0.0,
                    String.format("Enrollment status updated to: %s", newStatus),
                    username
            );

            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JCheckBox createWhiteCheckbox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setForeground(Color.WHITE);
        cb.setBackground(Color.decode("#2B2B2B"));
        cb.setFocusPainted(false);
        return cb;
    }

}
