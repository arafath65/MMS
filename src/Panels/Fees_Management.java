package Panels;

import Classes.GeneralMethods;
import Classes.GeneralMethods.StudentSearchType;
import Classes.GradientButton;
import Classes.HibernateConfig;
import Classes.ModernDialog;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Student_Management.Student;
import Entities.Student_Management.StudentFeeInstallments;
import Entities.Student_Management.StudentFeePayments;
import JPA_DAO.Student_Management.CourseEnrollmentDAO;
import JPA_DAO.Student_Management.StudentDAO;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
import Panels_SubDialogs.OneTimeFeePanel;
import static Panels_SubDialogs.OneTimeFeePanel.fm_fees_oneTime_table;
import static Panels_SubDialogs.OneTimeFeePanel.fm_fees_oneTime_total_fee_Textfield;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Fees_Management extends javax.swing.JPanel {

    OneTimeFeePanel oneTimeFeePanel;

    public static int selectedStudentIds = 0;
    public static int selectedEnrollmentId = 0;

    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private File selectedImageFile;

    private int selectedStudentId;

    public Fees_Management() {
        initComponents();

        oneTimeFeePanel = new OneTimeFeePanel();

        fm_fees_course_table.setDefaultRenderer(Object.class, new TableGradientCell());
        fm_fees_course_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");
        fm_fees_oneTime_table.setDefaultRenderer(Object.class, new TableGradientCell());
        fm_fees_oneTime_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        mainPanel.setVisible(true);
        mainPanel.setLayout(new CardLayout());
        mainPanel.add(oneTimeFeePanel, "one_time_fees");

        // ✅ Default: show Wali panel and select checkbox
        mainPanel.setVisible(true);
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "one_time_fees");

//        fm_fees_course_table.getColumnModel().getColumn(10).setMinWidth(0);
//        fm_fees_course_table.getColumnModel().getColumn(10).setMaxWidth(0);
//        fm_fees_course_table.getColumnModel().getColumn(10).setWidth(0);
//
//        fm_fees_course_table.getColumnModel().getColumn(11).setMinWidth(0);
//        fm_fees_course_table.getColumnModel().getColumn(11).setMaxWidth(0);
//        fm_fees_course_table.getColumnModel().getColumn(11).setWidth(0);
        jComboPopulates();
    }

    private void jComboPopulates() {

        fm_fees_admission_no_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {

                String input = fm_fees_admission_no_combo.getEditor().getItem().toString();
                List<Student> list
                        = new StudentDAO().searchStudents(input, "ADMISSION");

                generalMethods.loadStudentCombo(fm_fees_admission_no_combo, list, input, StudentSearchType.ADMISSION);
            }
        });
        setupComboSelectionListeners1(fm_fees_admission_no_combo, fm_fees_admission_no_combo);

        fm_fees_name_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {

                String input = fm_fees_name_combo.getEditor().getItem().toString();

                List<Student> list
                        = new StudentDAO().searchStudents(input, "NAME");

                generalMethods.loadStudentCombo(fm_fees_name_combo, list, input, StudentSearchType.NAME);
            }
        });
        setupComboSelectionListeners1(fm_fees_name_combo, fm_fees_name_combo);

    }

    private boolean itemSelectedByUser1 = false;
    private boolean itemSelectedByUser2 = false;

    public void setupComboSelectionListeners1(JComboBox<String> comboBox, JComponent nextFocusComponent) {
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
                if (selectedValue.isEmpty() || !isValueFromList(comboBox, selectedValue)) {
                    return;
                }

                String admis_combo = selectedValue.split(" - ")[0];
                String name_combo = selectedValue.split(" - ")[1];

                fm_fees_admission_no_combo.setSelectedItem(admis_combo);
                fm_fees_name_combo.setSelectedItem(name_combo);

                loadStudentCoursesToTable(admis_combo);

                // comboBox.setSelectedItem(admis_combo);
                nextFocusComponent.requestFocus();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                itemSelectedByUser1 = false;
            }
        });

        // Detect user selection via Enter or mouse click
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

    public static void loadStudentCoursesToTable(String admissionNo) {

        StudentDAO studentDAO = new StudentDAO();
        CourseEnrollmentDAO ceDAO = new CourseEnrollmentDAO();

        Student student = studentDAO.findByAdmissionNo(admissionNo);

        if (student == null) {
            return;
        }

        List<Object[]> list = ceDAO.findCoursesForFeeTable(student.getStudentId());

        DefaultTableModel model = (DefaultTableModel) fm_fees_course_table.getModel();
        model.setRowCount(0);

        int seq = 1;

        for (Object[] row : list) {

            int row_st_month = Integer.parseInt(row[3].toString());
            int row_en_month = Integer.parseInt(row[5].toString());

            String st_month = GeneralMethods.getMonthName(row_st_month);
            String en_month = GeneralMethods.getMonthName(row_en_month);

            String start = st_month + " - " + row[2];
            String complete = en_month + " - " + row[4];

            model.addRow(new Object[]{
                seq++,
                row[0], // Batch
                row[1], // Course name
                start, // Course start
                complete, // Complete
                row[6], // Payment mode
                GeneralMethods.formatWithComma(Integer.parseInt(row[7].toString())), // Admission fee
                GeneralMethods.formatWithComma(Integer.parseInt(row[8].toString())), // Total fee
                GeneralMethods.formatWithComma(Integer.parseInt(row[9].toString())), // Total paid
                GeneralMethods.formatWithComma(Integer.parseInt(row[10].toString())), // Balance
                row[11], // Enrollment id
                row[12] // Student id
            });
        }
    }

    public static void updateMasterTableRows(int studentId) {
        try {
            DefaultTableModel model = (DefaultTableModel) fm_fees_course_table.getModel();
            int rowCount = model.getRowCount();

            // Fetch all active payments for this student from DB
            EntityManager em = HibernateConfig.getEntityManager();
            List<StudentFeePayments> payments = em.createQuery(
                    "SELECT p FROM StudentFeePayments p "
                    + "WHERE p.student.studentId = :studentId "
                    + "AND p.status = true",
                    StudentFeePayments.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
            em.close();

            // Loop through the table rows
            for (int i = 0; i < rowCount; i++) {
                int tableStudentId = Integer.parseInt(model.getValueAt(i, 11).toString());
                int tableEnrollmentId = Integer.parseInt(model.getValueAt(i, 10).toString());

                if (tableStudentId == studentId) {
                    // Find the corresponding payment for this enrollment
                    for (StudentFeePayments payment : payments) {
                        if (payment.getEnrollment().getEnrollmentId() == tableEnrollmentId) {
                            // Update the master table row
                            model.setValueAt(GeneralMethods.formatWithComma(payment.getTotalPaid()), i, 8);    // Total Paid
                            model.setValueAt(GeneralMethods.formatWithComma(payment.getTotalBalance()), i, 9); // Total Balance
                          //  model.setValueAt(payment.getPaymentType(), i, 4);                                  // Payment Type
                         //   model.setValueAt(payment.getTotalBalance() <= 0 ? "COMPLETE" : payment.getPaymentStatus(), i, 5); // Status
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void updateMasterTableRow(int studentId, int enrollmentId) {
//        try {
//            // Find the row in the master table corresponding to this student + enrollment
//            DefaultTableModel model = (DefaultTableModel) fm_fees_course_table.getModel();
//            int rowCount = model.getRowCount();
//
//            for (int i = 0; i < rowCount; i++) {
//                // Assuming column 0 = studentId and column 1 = enrollmentId in master table
//                int tableStudentId = Integer.parseInt(model.getValueAt(i, 11).toString());
//                int tableEnrollmentId = Integer.parseInt(model.getValueAt(i, 10).toString());
//
//                if (tableStudentId == studentId && tableEnrollmentId == enrollmentId) {
//                    // Fetch the latest totals from database
//                    EntityManager em = HibernateConfig.getEntityManager();
//                    StudentFeePayments payment = em.createQuery(
//                            "SELECT p FROM StudentFeePayments p "
//                            + "WHERE p.student.studentId = :studentId "
//                            + "AND p.enrollment.enrollmentId = :enrollmentId "
//                            + "AND p.status = true", StudentFeePayments.class)
//                            .setParameter("studentId", studentId)
//                            .setParameter("enrollmentId", enrollmentId)
//                            .getSingleResult();
//                    em.close();
//
//                    // Update the master table row (adjust columns as per your table)
//                    model.setValueAt(GeneralMethods.formatWithComma(payment.getTotalPaid()), i, 8);      // Total Paid column
//                    model.setValueAt(GeneralMethods.formatWithComma(payment.getTotalBalance()), i, 9);   // Total Balance column
//                    //  model.setValueAt(payment.getPaymentType(), i, 4);    // Payment Type column
//                    //    model.setValueAt(payment.getTotalBalance() <= 0 ? "COMPLETE" : payment.getPaymentStatus(), i, 5); // Payment Status
//
//                    break; // row updated, exit loop
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        fm_fees_admission_no_combo = new javax.swing.JComboBox<>();
        fm_fees_name_combo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        fm_fees_course_table = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Student Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(232, 232, 232));
        jLabel13.setText("Admission Number");

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(232, 232, 232));
        jLabel14.setText("Full Name");

        fm_fees_admission_no_combo.setEditable(true);
        fm_fees_admission_no_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        fm_fees_name_combo.setEditable(true);
        fm_fees_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        fm_fees_course_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Batch", "Course Name", "Start", "End", "Payment Mode", "Admission Fee", "Total Fees", "Total Paid", "Balance", "Enrolment Id", "Student Id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fm_fees_course_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fm_fees_course_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(fm_fees_course_table);
        if (fm_fees_course_table.getColumnModel().getColumnCount() > 0) {
            fm_fees_course_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            fm_fees_course_table.getColumnModel().getColumn(1).setPreferredWidth(100);
            fm_fees_course_table.getColumnModel().getColumn(2).setPreferredWidth(150);
            fm_fees_course_table.getColumnModel().getColumn(3).setPreferredWidth(120);
            fm_fees_course_table.getColumnModel().getColumn(4).setPreferredWidth(120);
            fm_fees_course_table.getColumnModel().getColumn(5).setPreferredWidth(100);
            fm_fees_course_table.getColumnModel().getColumn(6).setPreferredWidth(100);
            fm_fees_course_table.getColumnModel().getColumn(7).setPreferredWidth(100);
            fm_fees_course_table.getColumnModel().getColumn(10).setPreferredWidth(20);
        }

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
                        .addComponent(jLabel13)
                        .addGap(7, 7, 7)
                        .addComponent(fm_fees_admission_no_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fm_fees_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 517, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fm_fees_admission_no_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fm_fees_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(jLabel13))
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 407, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void fm_fees_course_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fm_fees_course_tableMouseClicked

        try {

            mainPanel.setVisible(true);
            DefaultTableModel model = (DefaultTableModel) fm_fees_course_table.getModel();
            String check = model.getValueAt(fm_fees_course_table.getSelectedRow(), 5).toString();

            int st_id = Integer.parseInt(model.getValueAt(fm_fees_course_table.getSelectedRow(), 11).toString());
            int en_id = Integer.parseInt(model.getValueAt(fm_fees_course_table.getSelectedRow(), 10).toString());

            int tot_fee = GeneralMethods.parseCommaNumber(model.getValueAt(fm_fees_course_table.getSelectedRow(), 7).toString());
            int tot_bal = GeneralMethods.parseCommaNumber(model.getValueAt(fm_fees_course_table.getSelectedRow(), 9).toString());

            if (check.equalsIgnoreCase("ONE-TIME")) {

                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "one_time_fees");

            } else {
            }

            int tabIndex = OneTimeFeePanel.jTabbedPane1.getSelectedIndex();
            if (tabIndex == 0) {

                StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();

                List<Object[]> list = dao.getInstallments(en_id);

                DefaultTableModel model2 = (DefaultTableModel) fm_fees_oneTime_table.getModel();
                model2.setRowCount(0);

                for (Object[] row : list) {

                    model2.addRow(new Object[]{
                        row[0],
                        sdf.format(row[1]),
                        GeneralMethods.formatWithComma(Integer.parseInt(row[2].toString()))
                    });

                }

                fm_fees_oneTime_total_fee_Textfield.setText(GeneralMethods.formatWithComma(tot_fee));
                OneTimeFeePanel.fm_fees_oneTime_total_balance_Textfield.setText(GeneralMethods.formatWithComma(tot_bal));
                OneTimeFeePanel.fm_fees_cheq_full_fees_Textfield.setText(GeneralMethods.formatWithComma(tot_fee));

                OneTimeFeePanel.fm_fees_oneTime_total_paid_Textfield.setText("");
                OneTimeFeePanel.fm_fees_oneTime_enter_amount_Textfield.setText("");
                OneTimeFeePanel.fm_fees_oneTime_show_bal_Textfield.setText("");

                OneTimeFeePanel.fm_fees_oneTime_total_paid_Textfield.requestFocus();

                selectedStudentIds = Integer.parseInt(model.getValueAt(fm_fees_course_table.getSelectedRow(), 11).toString());
                selectedEnrollmentId = Integer.parseInt(model.getValueAt(fm_fees_course_table.getSelectedRow(), 10).toString());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_fm_fees_course_tableMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed


    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    public static javax.swing.JComboBox<String> fm_fees_admission_no_combo;
    public static javax.swing.JTable fm_fees_course_table;
    private javax.swing.JComboBox<String> fm_fees_name_combo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables

}
