/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Panels;

import Classes.GeneralMethods;
import Classes.TableGradientCell;
import Classes.ButtonGradientRound;
import Entities.Settings.Course;
import Entities.Settings.StudentClass;
import JPA_DAO.Settings.ClassDAO;
import JPA_DAO.Settings.CourseDAO;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author UNKNOWN_UN
 */
public class Register_Course extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();

    public Register_Course() {
        initComponents();

//        reg_course_CourseTable.setShowGrid(false);
//        reg_course_CourseTable.setIntercellSpacing(new Dimension(0, 0));
//        reg_course_CourseTable.setFillsViewportHeight(true);
        reg_course_CourseTable.setDefaultRenderer(Object.class, new TableGradientCell());
        reg_course_CourseTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        reg_course_ClassTable.setDefaultRenderer(Object.class, new TableGradientCell());
        reg_course_ClassTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        loadCoursesToTable(reg_course_CourseTable);
        loadClassToTable(reg_course_ClassTable);

    }

    public void loadCoursesToTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Clear existing rows first
        model.setRowCount(0);

        CourseDAO dao = new CourseDAO();
        List<Course> courses = dao.findAll();

        int seq = 1;
        for (Course c : courses) {
            String enrolMonthName = generalMethods.getMonthName(c.getEnrolMonth());
            String compMonthName = generalMethods.getMonthName(c.getCompMonth());

            model.addRow(new Object[]{
                seq++, // Sequence number
                c.getBatch(), // Batch
                c.getCourseName(), // Course Name
                c.getEnrolYear(), // Enrol Year
                enrolMonthName, // Enrol Month as String
                c.getCompYear(), // Completion Year
                compMonthName, // Completion Month as String
                c.getPaymentMode(), // Payment Mode
                c.getAdmissionFee(),
                c.getFee(), // Fee
                c.getStatus() // Status
            });
        }
    }

    public void loadClassToTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Clear existing rows first
        model.setRowCount(0);

        ClassDAO dao = new ClassDAO();
        List<StudentClass> courses = dao.findAll();

        int seq = 1;
        for (StudentClass c : courses) {

            model.addRow(new Object[]{
                seq++, // Sequence number
                c.getClassName(), // Class Name
            });
        }
    }

    private void rearrangeTableSeq(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0); // SEQ column index = 0
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        reg_course_batch_combo = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        reg_course_course_name_combo = new javax.swing.JComboBox<>();
        reg_course_enrol_year_combo = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        reg_course_enrol_month_combo = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        reg_course_comp_month_combo = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        reg_course_comp_year_combo = new javax.swing.JComboBox<>();
        reg_course_fees_textfield = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        reg_course_payment_mode_combo = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        reg_course_admission_fees_textfield = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        reg_course_CourseTable = new javax.swing.JTable();
        buttonGradientRound1 = new Classes.ButtonGradientRound();
        jPanel9 = new javax.swing.JPanel();
        reg_course_class_textfield = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        reg_course_ClassTable = new javax.swing.JTable();
        buttonGradientRound2 = new Classes.ButtonGradientRound();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Course Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(232, 232, 232));
        jLabel10.setText("Batch");

        jLabel12.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(232, 232, 232));
        jLabel12.setText("Course");

        reg_course_batch_combo.setEditable(true);
        reg_course_batch_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(232, 232, 232));
        jLabel16.setText("Enrollment Year");

        reg_course_course_name_combo.setEditable(true);
        reg_course_course_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        reg_course_enrol_year_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_course_enrol_year_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2026", "2027" }));

        jLabel18.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(232, 232, 232));
        jLabel18.setText("Enrollment Month");

        reg_course_enrol_month_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_course_enrol_month_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jLabel19.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(232, 232, 232));
        jLabel19.setText("Completion Month");

        reg_course_comp_month_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_course_comp_month_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jLabel20.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(232, 232, 232));
        jLabel20.setText("Completion Year");

        reg_course_comp_year_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_course_comp_year_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2026", "2027" }));

        reg_course_fees_textfield.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_course_fees_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_course_fees_textfieldActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(232, 232, 232));
        jLabel21.setText("Fee");

        reg_course_payment_mode_combo.setEditable(true);
        reg_course_payment_mode_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(232, 232, 232));
        jLabel22.setText("Payment Option");

        jLabel23.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(232, 232, 232));
        jLabel23.setText("Admission Fee");

        reg_course_admission_fees_textfield.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_course_admission_fees_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_course_admission_fees_textfieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reg_course_batch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(reg_course_course_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(reg_course_enrol_year_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(reg_course_enrol_month_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reg_course_comp_year_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(reg_course_comp_month_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reg_course_payment_mode_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reg_course_admission_fees_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reg_course_fees_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reg_course_batch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(reg_course_course_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(25, 25, 25)
                                    .addComponent(reg_course_comp_month_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(41, 41, 41)))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(reg_course_enrol_year_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(reg_course_comp_year_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(reg_course_enrol_month_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(reg_course_fees_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(reg_course_admission_fees_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(25, 25, 25)
                                    .addComponent(reg_course_payment_mode_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(41, 41, 41))))))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Course Table", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        reg_course_CourseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Batch", "Course Name", "Enrol. Year", "Enrol. Month", "Comp. Year", "Comp. Month", "Payment Mode", "Admission Fee", "Fee"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(reg_course_CourseTable);
        if (reg_course_CourseTable.getColumnModel().getColumnCount() > 0) {
            reg_course_CourseTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            reg_course_CourseTable.getColumnModel().getColumn(1).setPreferredWidth(120);
            reg_course_CourseTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            reg_course_CourseTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        }

        buttonGradientRound1.setText("X");
        buttonGradientRound1.setFont(new java.awt.Font("Roboto Black", 0, 17)); // NOI18N
        buttonGradientRound1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradientRound1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Add Class", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        reg_course_class_textfield.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_course_class_textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_course_class_textfieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reg_course_class_textfield, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(reg_course_class_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Class Table", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        reg_course_ClassTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Class"
            }
        ));
        jScrollPane2.setViewportView(reg_course_ClassTable);
        if (reg_course_ClassTable.getColumnModel().getColumnCount() > 0) {
            reg_course_ClassTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        }

        buttonGradientRound2.setText("X");
        buttonGradientRound2.setFont(new java.awt.Font("Roboto Black", 0, 17)); // NOI18N
        buttonGradientRound2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradientRound2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradientRound2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradientRound2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void reg_course_fees_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_course_fees_textfieldActionPerformed
        try {

            DefaultTableModel model = (DefaultTableModel) reg_course_CourseTable.getModel();
            String batch = reg_course_batch_combo.getEditor().getItem().toString();
            String course = reg_course_course_name_combo.getEditor().getItem().toString();
            int enrol_year = Integer.parseInt(reg_course_enrol_year_combo.getSelectedItem().toString());
            String enrol_month = reg_course_enrol_month_combo.getSelectedItem().toString();
            int en_monthNumber = generalMethods.getMonthNumber(enrol_month);

            int comp_year = Integer.parseInt(reg_course_comp_year_combo.getSelectedItem().toString());
            String comp_month = reg_course_comp_month_combo.getSelectedItem().toString();
            int co_monthNumber = generalMethods.getMonthNumber(comp_month);
            System.out.println("COMP MONTH-" + co_monthNumber + "Mon :" + comp_month);

            String payment_mode = reg_course_payment_mode_combo.getEditor().getItem().toString();
            String admissionFee = reg_course_admission_fees_textfield.getText();
            String fee = reg_course_fees_textfield.getText();

            // 🔴 DUPLICATE CHECK (RAW)
            boolean duplicate = false;

            for (int i = 0; i < model.getRowCount(); i++) {

                String tBatch = model.getValueAt(i, 1).toString();
                String tCourse = model.getValueAt(i, 2).toString();
                int tEnrolYear = Integer.parseInt(model.getValueAt(i, 3).toString());
                String tEnrolMonth = model.getValueAt(i, 4).toString();
                int tCompYear = Integer.parseInt(model.getValueAt(i, 5).toString());
                String tCompMonth = model.getValueAt(i, 6).toString();
                String tPaymentMode = model.getValueAt(i, 7).toString();
                String aFee = model.getValueAt(i, 8).toString();
                String tFee = model.getValueAt(i, 9).toString();

                if (tBatch.equalsIgnoreCase(batch)
                        && tCourse.equalsIgnoreCase(course)
                        && tEnrolYear == enrol_year
                        && tEnrolMonth.equalsIgnoreCase(enrol_month)
                        && tCompYear == comp_year
                        && tCompMonth.equalsIgnoreCase(comp_month)
                        && tPaymentMode.equalsIgnoreCase(payment_mode)
                        && aFee.equals(admissionFee)
                        && tFee.equals(fee)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                JOptionPane.showMessageDialog(
                        null,
                        "This course entry already exists!",
                        "Duplicate Entry",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Course c = new Course();
            c.setBatch(batch);
            c.setCourseName(course);
            c.setEnrolYear(enrol_year);
            c.setEnrolMonth(en_monthNumber);
            c.setCompYear(comp_year);
            c.setCompMonth(co_monthNumber);
            c.setPaymentMode(payment_mode);
            c.setAdmissionFee(Integer.parseInt(admissionFee));
            c.setFee(Integer.parseInt(fee));
            c.setStatus(1);

            new CourseDAO().save(c);

            model.addRow(new Object[]{model.getRowCount() + 1, batch, course, enrol_year, enrol_month, comp_year, comp_month, payment_mode, admissionFee, fee});
            
            reg_course_batch_combo.removeAllItems();
            reg_course_course_name_combo.removeAllItems();
            reg_course_enrol_year_combo.removeAllItems();
            reg_course_enrol_month_combo.removeAllItems();
            reg_course_comp_year_combo.removeAllItems();
            reg_course_comp_month_combo.removeAllItems();
            reg_course_payment_mode_combo.removeAllItems();
            reg_course_admission_fees_textfield.setText("");
            reg_course_fees_textfield.setText("");
            reg_course_batch_combo.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_reg_course_fees_textfieldActionPerformed

    private void reg_course_class_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_course_class_textfieldActionPerformed
        try {

            DefaultTableModel model = (DefaultTableModel) reg_course_ClassTable.getModel();
            String text_class = reg_course_class_textfield.getText();

            // 🔴 DUPLICATE CHECK (RAW)
            boolean duplicate = false;

            for (int i = 0; i < model.getRowCount(); i++) {

                String cName = model.getValueAt(i, 1).toString();

                if (cName.equalsIgnoreCase(text_class)) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                JOptionPane.showMessageDialog(
                        null,
                        "This class entry already exists!",
                        "Duplicate Entry",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            StudentClass c = new StudentClass();
            c.setClassName(text_class);

            c.setStatus(true);

            new ClassDAO().save(c);

            model.addRow(new Object[]{model.getRowCount() + 1, text_class});
            reg_course_class_textfield.setText("");
            reg_course_class_textfield.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_reg_course_class_textfieldActionPerformed

    private void buttonGradientRound4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound4ActionPerformed
        try {

            int selectedRow = reg_course_CourseTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a row to delete",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this course?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            DefaultTableModel model
                    = (DefaultTableModel) reg_course_CourseTable.getModel();

            String batch = model.getValueAt(selectedRow, 1).toString();
            String courseName = model.getValueAt(selectedRow, 2).toString();
            int enrolYear = Integer.parseInt(model.getValueAt(selectedRow, 3).toString());
            int enrolMonth = generalMethods.getMonthNumber(model.getValueAt(selectedRow, 4).toString());
            int compYear = Integer.parseInt(model.getValueAt(selectedRow, 5).toString());
            int compMonth = generalMethods.getMonthNumber(model.getValueAt(selectedRow, 6).toString());
            String paymentMode = model.getValueAt(selectedRow, 7).toString();
            int admissionFee = Integer.parseInt(model.getValueAt(selectedRow, 8).toString());
            int fee = Integer.parseInt(model.getValueAt(selectedRow, 9).toString());

            CourseDAO dao = new CourseDAO();
            boolean deleted = dao.deleteCourse(batch, courseName, enrolYear, enrolMonth,
                    compYear, compMonth, paymentMode, admissionFee, fee);

// remove row from table
            model.removeRow(selectedRow);

            rearrangeTableSeq(reg_course_CourseTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_buttonGradientRound4ActionPerformed

    private void buttonGradientRound1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound1ActionPerformed
        try {

            int selectedRow = reg_course_CourseTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a row to delete",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this course?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            DefaultTableModel model
                    = (DefaultTableModel) reg_course_CourseTable.getModel();

            String batch = model.getValueAt(selectedRow, 1).toString();
            String courseName = model.getValueAt(selectedRow, 2).toString();
            int enrolYear = Integer.parseInt(model.getValueAt(selectedRow, 3).toString());
            int enrolMonth = generalMethods.getMonthNumber(model.getValueAt(selectedRow, 4).toString());
            int compYear = Integer.parseInt(model.getValueAt(selectedRow, 5).toString());
            int compMonth = generalMethods.getMonthNumber(model.getValueAt(selectedRow, 6).toString());
            String paymentMode = model.getValueAt(selectedRow, 7).toString();
            int admissionFee = Integer.parseInt(model.getValueAt(selectedRow, 8).toString());
            int fee = Integer.parseInt(model.getValueAt(selectedRow, 9).toString());

            CourseDAO dao = new CourseDAO();
            boolean deleted = dao.deleteCourse(batch, courseName, enrolYear, enrolMonth,
                    compYear, compMonth, paymentMode, admissionFee, fee);

// remove row from table
            model.removeRow(selectedRow);

            rearrangeTableSeq(reg_course_CourseTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_buttonGradientRound1ActionPerformed

    private void buttonGradientRound2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound2ActionPerformed
        try {

            int selectedRow = reg_course_ClassTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a row to delete",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this class?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            DefaultTableModel model
                    = (DefaultTableModel) reg_course_ClassTable.getModel();

            String class_name = model.getValueAt(selectedRow, 1).toString();

            ClassDAO dao = new ClassDAO();
            boolean deleted = dao.deleteCourse(class_name);

// remove row from table
            model.removeRow(selectedRow);

            rearrangeTableSeq(reg_course_CourseTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_buttonGradientRound2ActionPerformed

    private void reg_course_admission_fees_textfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_course_admission_fees_textfieldActionPerformed
        reg_course_fees_textfield.requestFocus();
    }//GEN-LAST:event_reg_course_admission_fees_textfieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradientRound buttonGradientRound1;
    private Classes.ButtonGradientRound buttonGradientRound2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable reg_course_ClassTable;
    private javax.swing.JTable reg_course_CourseTable;
    private javax.swing.JTextField reg_course_admission_fees_textfield;
    private javax.swing.JComboBox<String> reg_course_batch_combo;
    private javax.swing.JTextField reg_course_class_textfield;
    private javax.swing.JComboBox<String> reg_course_comp_month_combo;
    private javax.swing.JComboBox<String> reg_course_comp_year_combo;
    private javax.swing.JComboBox<String> reg_course_course_name_combo;
    private javax.swing.JComboBox<String> reg_course_enrol_month_combo;
    private javax.swing.JComboBox<String> reg_course_enrol_year_combo;
    private javax.swing.JTextField reg_course_fees_textfield;
    private javax.swing.JComboBox<String> reg_course_payment_mode_combo;
    // End of variables declaration//GEN-END:variables
}
