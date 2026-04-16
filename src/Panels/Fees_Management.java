package Panels;

import Classes.GeneralMethods;
import Classes.GeneralMethods.StudentSearchType;
import Classes.GradientButton;
import Classes.HibernateConfig;
import Classes.InstallmentIconRenderer;
import Classes.ModernDialog;
import Classes.MonthlyFeeIconRenderer;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Student_Management.Student;
import Entities.Student_Management.StudentFeeInstallments;
import Entities.Student_Management.StudentFeePayments;
import JPA_DAO.Student_Management.CourseEnrollmentDAO;
import JPA_DAO.Student_Management.StudentDAO;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
import Panels_SubDialogs.Admission_Fee_Payment;
import Panels_SubDialogs.MonthlyFeePanel;
import Panels_SubDialogs.OneTimeFeePanel;
import static Panels_SubDialogs.OneTimeFeePanel.fm_fees_oneTime_table;
import static Panels_SubDialogs.OneTimeFeePanel.fm_fees_oneTime_total_fee_Textfield;
import Panels_SubDialogs.Round_Payment;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Fees_Management extends javax.swing.JPanel {

    OneTimeFeePanel oneTimeFeePanel;
    MonthlyFeePanel monthlyFeePanel;

    public static int selectedStudentIds = 0;
    public static int selectedEnrollmentId = 0;
    public static double admis_Fees = 0.0;

    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private File selectedImageFile;

    private int selectedStudentId;
    String username;
    String role;

    public Fees_Management(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        oneTimeFeePanel = new OneTimeFeePanel(username, role);
        monthlyFeePanel = new MonthlyFeePanel(username, role);

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

        MonthlyFeePanel.mm_fees_monthly_table.setDefaultRenderer(Object.class, new TableGradientCell());
        MonthlyFeePanel.mm_fees_monthly_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        fm_fees_oneTime_table.getColumnModel().getColumn(2)
                .setCellRenderer(new InstallmentIconRenderer());

//        MonthlyFeePanel.mm_fees_monthly_table.getColumnModel().getColumn(3).setCellRenderer(
//                new MonthlyFeeIconRenderer(data.chequeStatusMap)
//        );
        mainPanel.setVisible(true);
        mainPanel.setLayout(new CardLayout());
        mainPanel.add(oneTimeFeePanel, "one_time_fees");
        mainPanel.add(monthlyFeePanel, "monthly_fee");

        // ✅ Default: show ONE-TIME FEE PANEL
        mainPanel.setVisible(true);
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "one_time_fees");

        fm_fees_course_table.getColumnModel().getColumn(10).setMinWidth(0);
        fm_fees_course_table.getColumnModel().getColumn(10).setMaxWidth(0);
        fm_fees_course_table.getColumnModel().getColumn(10).setWidth(0);

        fm_fees_course_table.getColumnModel().getColumn(11).setMinWidth(0);
        fm_fees_course_table.getColumnModel().getColumn(11).setMaxWidth(0);
        fm_fees_course_table.getColumnModel().getColumn(11).setWidth(0);
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

                DefaultTableModel model = (DefaultTableModel) OneTimeFeePanel.fm_fees_oneTime_table.getModel();
                model.setRowCount(0);

                DefaultTableModel model1 = (DefaultTableModel) MonthlyFeePanel.mm_fees_monthly_table.getModel();
                model1.setRowCount(0);

                // ONE TIME FRAME
                OneTimeFeePanel.fm_fees_oneTime_payment_date.setDate(new Date());
                OneTimeFeePanel.fm_fees_oneTime_payment_method_combo.setSelectedIndex(0);
                OneTimeFeePanel.fm_fees_oneTime_total_fee_Textfield.setText("");
                OneTimeFeePanel.fm_fees_oneTime_total_paid_Textfield.setText("");
                OneTimeFeePanel.fm_fees_oneTime_total_balance_Textfield.setText("");
                OneTimeFeePanel.fm_fees_oneTime_chq_sum_Textfield.setText("");
                OneTimeFeePanel.fm_fees_oneTime_chq_sum_bal_Textfield.setText("");

                OneTimeFeePanel.fm_fees_cheq_full_fees_Textfield.setText("");
                OneTimeFeePanel.fm_fees_cheq_cheque_number.setText("");
                OneTimeFeePanel.fm_fees_cheq_cheque_bank.removeAllItems();
                OneTimeFeePanel.fm_fees_cheq_cheque_branch.setText("");
                OneTimeFeePanel.fm_fees_cheq_cheque_amount.setText("");
                OneTimeFeePanel.fm_fees_cheq_cheque_date.setDate(null);
                OneTimeFeePanel.fm_fees_cheq_cheque_status.setSelectedIndex(0);
                OneTimeFeePanel.fm_fees_cheq_cheque_remaining.setText("");
                OneTimeFeePanel.fm_fees_cheq_cheque_sum_Textfield.setText("");
                OneTimeFeePanel.fm_fees_cheq_cheque_sum_bal_Textfield.setText("");

                // MONTHLY FRAME
                MonthlyFeePanel.mm_fees_Monthly_payment_date.setDate(new Date());
                MonthlyFeePanel.mm_fees_Monthly_payment_method_combo.setSelectedIndex(0);
                MonthlyFeePanel.mm_fees_Monthly_total_fee_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_fee_cal_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_total_paid_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_total_balance_Textfield.setText("");
                MonthlyFeePanel.fm_fees_cheq_full_fees_Textfield.setText("");
                MonthlyFeePanel.fm_fees_cheq_full_fees_cal_Textfield.setText("");
                MonthlyFeePanel.mm_fees_cheq_cheque_number.setText("");
                MonthlyFeePanel.mm_fees_cheq_cheque_bank.removeAllItems();
                MonthlyFeePanel.mm_fees_cheq_cheque_branch.setText("");
                MonthlyFeePanel.mm_fees_cheq_cheque_amount.setText("");
                MonthlyFeePanel.mm_fees_cheq_cheque_date.setDate(null);
                MonthlyFeePanel.mm_fees_cheq_cheque_status.setSelectedIndex(0);
                MonthlyFeePanel.mm_fees_cheq_cheque_remaining.setText("");

                MonthlyFeePanel.mm_fees_Monthly_tot_paid_months_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_tot_paid_amount_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_tot_cheque_pending_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_tot_totPaid_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_tot_pending_months_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_tot_pending_balancee_Textfield.setText("");
                MonthlyFeePanel.mm_fees_Monthly_fee_note_Textarea.setText("");

                admis_Fees = 0.0;
                selectedStudentIds = 0;
                selectedEnrollmentId = 0;

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
                GeneralMethods.formatWithComma((GeneralMethods.parseCommaNumber(row[7].toString()))), // Admission fee
                GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(row[8].toString())), // Total fee
                GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(row[9].toString())), // Total paid
                GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(row[10].toString())), // Balance
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

    public static void loadMonthlyTable(int enrollmentId, JTable table) {

        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
        StudentFeeInstallmentsDAO.MonthDataDTO data = dao.getMonthData(enrollmentId);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        int rowNo = 1;

        int y = data.startYear;
        int m = data.startMonth;

        while (true) {

            String monthStr = String.format("%02d", m);
            String full = y + "-" + monthStr;

            String monthName = java.time.Month.of(m).name().substring(0, 3);

            // ✅ GET EXACT AMOUNT FROM MAP
            int paidFee = data.monthAmountMap.getOrDefault(full, -1); // -1 → month not in DB

            String status = "";
            if (paidFee > 0) {
                status = "PAID"; // normal payment
            } else if (paidFee == 0) {
                // month exists in DB with 0 payment → still mark as PAID
                status = "PAID";
            } else {
                // month not in DB → leave blank
                paidFee = 0; // show empty in table
            }

            model.addRow(new Object[]{
                rowNo++,
                y,
                monthName,
                paidFee > 0 || paidFee == 0 ? paidFee : "", // show empty if not saved
                status,
                full
            });

            if (y == data.endYear && m == data.endMonth) {
                break;
            }

            m++;
            if (m > 12) {
                m = 1;
                y++;
            }
        }

        table.getColumnModel().getColumn(3).setCellRenderer(
                new MonthlyFeeIconRenderer(data.chequeStatusMap)
        );
    }

    // ************ Working code without summing same year+month *************************
//    public void loadMonthlyTable(int enrollmentId, JTable table) {
//
//        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
//        StudentFeeInstallmentsDAO.MonthDataDTO data = dao.getMonthData(enrollmentId);
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//        model.setRowCount(0);
//
//        int rowNo = 1;
//
//        int y = data.startYear;
//        int m = data.startMonth;
//
//        while (true) {
//
//            String monthStr = String.format("%02d", m);
//            String full = y + "-" + monthStr;
//
//            String monthName = java.time.Month.of(m).name().substring(0, 3);
//
//            // ✅ GET EXACT AMOUNT FROM MAP
//            int paidFee = data.monthAmountMap.getOrDefault(full, 0);
//
//            String status = paidFee > 0 ? "PAID" : "";
//
//            model.addRow(new Object[]{
//                rowNo++,
//                y,
//                monthName,
//                paidFee,
//                status,
//                full
//            });
    ////            model.addRow(new Object[]{
////                rowNo++, // 0
////                y, // 1
////                monthName, // 2
////                paidFee, // 3 (visible)
////                full // 4 (yyyy-MM → needed for renderer)
////            });
//
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
//        table.getColumnModel().getColumn(3).setCellRenderer(
//                new MonthlyFeeIconRenderer(data.chequeStatusMap)
//        );
//    }

    // ************************* VERY OLD ****************************************
//    public void loadMonthlyTable(int enrollmentId, JTable table) {
//
//        StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
//        StudentFeeInstallmentsDAO.MonthDataDTO data = dao.getMonthData(enrollmentId);
//
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//        model.setRowCount(0); // clear table
//
//        int rowNo = 1;
//
//        int y = data.startYear;
//        int m = data.startMonth;
//
//        while (true) {
//
//            // Format month (01,02...)
//            String monthStr = String.format("%02d", m);
//
//            // Format full value yyyy-MM
//            String full = y + "-" + monthStr;
//
//            // Month name (Jan, Feb...)
//            String monthName = java.time.Month.of(m).name().substring(0, 3);
//
//            // Check paid
//            boolean isPaid = data.paidMonths.contains(full);
//
//            int paidFee = isPaid ? data.monthlyFee : 0;
//            String status = isPaid ? "PAID" : "";
//
//            // Add row
//            model.addRow(new Object[]{
//                rowNo++,
//                y,
//                monthName,
//                paidFee,
//                status
//            });
//            
    ////            model.addRow(new Object[]{
////                rowNo++,
////                y,
////                monthName,
////                paidFee,
////                ""
////            });
//
//            // 🔥 BREAK CONDITION
//            if (y == data.endYear && m == data.endMonth) {
//                break;
//            }
//
//            // 🔥 MOVE TO NEXT MONTH
//            m++;
//
//            if (m > 12) {
//                m = 1;
//                y++;
//            }
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

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/admissionfee.png"))); // NOI18N
        jButton1.setToolTipText("Pay Admission Fee");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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

            double tot_fee = GeneralMethods.parseCommaNumber(model.getValueAt(fm_fees_course_table.getSelectedRow(), 7).toString());
            double tot_bal = GeneralMethods.parseCommaNumber(model.getValueAt(fm_fees_course_table.getSelectedRow(), 9).toString());

            StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();

            double pendingCheque = dao.getPendingChequeAmountForCourse(en_id);

            if (check.equalsIgnoreCase("ONE-TIME")) {

                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "one_time_fees");

                int tabIndex = OneTimeFeePanel.jTabbedPane1.getSelectedIndex();
                if (tabIndex == 0) {

                    fm_fees_oneTime_total_fee_Textfield.setText(GeneralMethods.formatWithComma(tot_fee));
                    OneTimeFeePanel.fm_fees_oneTime_total_balance_Textfield.setText(GeneralMethods.formatWithComma(tot_bal));

                    OneTimeFeePanel.fm_fees_oneTime_total_paid_Textfield.setText("");
                    OneTimeFeePanel.fm_fees_oneTime_chq_sum_Textfield.setText("");
                    OneTimeFeePanel.fm_fees_oneTime_chq_sum_bal_Textfield.setText("");

                    OneTimeFeePanel.fm_fees_oneTime_total_paid_Textfield.requestFocus();

                    // set pending cheque field
                    OneTimeFeePanel.fm_fees_oneTime_chq_sum_Textfield
                            .setText(GeneralMethods.formatWithComma(pendingCheque));
                    System.out.println("pendingCheque - " + pendingCheque);

                    // calculate remaining balance
                    double balanceAfterCheque = tot_bal - pendingCheque;

                    if (balanceAfterCheque < 0) {
                        balanceAfterCheque = 0;
                    }

                    // set remaining balance field
                    OneTimeFeePanel.fm_fees_oneTime_chq_sum_bal_Textfield
                            .setText(GeneralMethods.formatWithComma(balanceAfterCheque));

                } else if (tabIndex == 1) {
                    OneTimeFeePanel.fm_fees_cheq_full_fees_Textfield.setText(GeneralMethods.formatWithComma(tot_fee));
                    OneTimeFeePanel.fm_fees_cheq_cheque_remaining.setText(GeneralMethods.formatWithComma(tot_bal));
                    OneTimeFeePanel.fm_fees_cheq_cheque_amount.setText("");

                    OneTimeFeePanel.fm_fees_cheq_cheque_number.requestFocus();

                    // set pending cheque field
                    OneTimeFeePanel.fm_fees_cheq_cheque_sum_Textfield
                            .setText(GeneralMethods.formatWithComma(pendingCheque));
                    System.out.println("pendingCheque - " + pendingCheque);

                    // calculate remaining balance
                    double balanceAfterCheque = tot_bal - pendingCheque;

                    if (balanceAfterCheque < 0) {
                        balanceAfterCheque = 0;
                    }

                    // set remaining balance field
                    OneTimeFeePanel.fm_fees_cheq_cheque_sum_bal_Textfield
                            .setText(GeneralMethods.formatWithComma(balanceAfterCheque));
                }

//            StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
                List<Object[]> list = dao.getInstallments(en_id);

                DefaultTableModel model2 = (DefaultTableModel) fm_fees_oneTime_table.getModel();
                model2.setRowCount(0);

                for (Object[] row : list) {

                    String paymentMethod = row[3] != null ? row[3].toString() : "";
                    String chequeStatus = row[4] != null ? row[4].toString() : "";

                    model2.addRow(new Object[]{
                        row[0],
                        sdf.format(row[1]),
                        GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(row[2].toString())),
                        paymentMethod,
                        chequeStatus
                    });
                }

            } else if (check.equalsIgnoreCase("MONTHLY")) {

                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "monthly_fee");

//                MonthPicker monthPicker = new MonthPicker();
//                monthPicker.loadFromDatabase(en_id);
                loadMonthlyTable(en_id, MonthlyFeePanel.mm_fees_monthly_table);

                DefaultTableModel fee_install_table = (DefaultTableModel) MonthlyFeePanel.mm_fees_monthly_table.getModel();
                int tabIndex = MonthlyFeePanel.monthly_jTabbedPane2.getSelectedIndex();
                if (tabIndex == 0) {

                    int rowCount = fee_install_table.getRowCount();

                    double cal_sum = tot_fee / rowCount;
                    MonthlyFeePanel.mm_fees_Monthly_total_fee_Textfield.setText(GeneralMethods.formatWithComma(cal_sum));
                    MonthlyFeePanel.mm_fees_Monthly_total_balance_Textfield.setText(GeneralMethods.formatWithComma(tot_bal));

                    System.out.println("tot_fee - " + tot_fee + " = rowCount - " + rowCount);

                    MonthlyFeePanel.mm_fees_Monthly_fee_cal_Textfield.requestFocus();
                    MonthlyFeePanel.mm_fees_Monthly_fee_cal_Textfield.selectAll();

                    SwingUtilities.invokeLater(() -> {

                        monthlyFeePanel.updateMonthlySummaryFields(
                                selectedEnrollmentId,
                                MonthlyFeePanel.mm_fees_monthly_table,
                                MonthlyFeePanel.mm_fees_Monthly_tot_paid_months_Textfield,
                                MonthlyFeePanel.mm_fees_Monthly_tot_paid_amount_Textfield,
                                MonthlyFeePanel.mm_fees_Monthly_tot_pending_months_Textfield
                        );

                    });

                } else if (tabIndex == 1) {

                    int rowCount = fee_install_table.getRowCount();

                    double cal_sum = tot_fee / rowCount;
                    MonthlyFeePanel.fm_fees_cheq_full_fees_Textfield.setText(GeneralMethods.formatWithComma(cal_sum));
                    MonthlyFeePanel.mm_fees_cheq_cheque_remaining.setText(GeneralMethods.formatWithComma(tot_bal));

                    System.out.println("tot_fee - " + tot_fee + " = rowCount - " + rowCount);

                    MonthlyFeePanel.fm_fees_cheq_full_fees_cal_Textfield.requestFocus();
                    MonthlyFeePanel.fm_fees_cheq_full_fees_cal_Textfield.selectAll();

                    SwingUtilities.invokeLater(() -> {

                        monthlyFeePanel.updateMonthlySummaryFields(
                                selectedEnrollmentId,
                                MonthlyFeePanel.mm_fees_monthly_table,
                                MonthlyFeePanel.mm_fees_Monthly_tot_paid_months_Textfield,
                                MonthlyFeePanel.mm_fees_Monthly_tot_paid_amount_Textfield,
                                MonthlyFeePanel.mm_fees_Monthly_tot_pending_months_Textfield
                        );

                    });

                }

            }

            admis_Fees = GeneralMethods.parseCommaNumber(model.getValueAt(fm_fees_course_table.getSelectedRow(), 6).toString());
            selectedStudentIds = Integer.parseInt(model.getValueAt(fm_fees_course_table.getSelectedRow(), 11).toString());
            selectedEnrollmentId = Integer.parseInt(model.getValueAt(fm_fees_course_table.getSelectedRow(), 10).toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_fm_fees_course_tableMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed


    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        //  int admiFee = GeneralMethods.parseCommaNumber(fm_fees_course_table.getValueAt(fm_fees_course_table.getSelectedRow(), 6).toString());
        Admission_Fee_Payment dialog = new Admission_Fee_Payment(parentFrame, selectedStudentId, this, selectedStudentIds, selectedEnrollmentId, admis_Fees, username, role);
        System.out.println("CLICK ADMI - " + admis_Fees);

        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        String st_name = fm_fees_name_combo.getEditor().getItem().toString();
        if (st_name.equals("")) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) fm_fees_course_table.getModel();
        int st_id = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            st_id = Integer.parseInt(fm_fees_course_table.getValueAt(i, 11).toString());
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        //  int admiFee = GeneralMethods.parseCommaNumber(fm_fees_course_table.getValueAt(fm_fees_course_table.getSelectedRow(), 6).toString());
        Round_Payment dialog = new Round_Payment(parentFrame, st_id, st_name, username, role);
        System.out.println("stuuu - " + st_id);

        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
    }//GEN-LAST:event_jButton3ActionPerformed


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
