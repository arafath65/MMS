package Panels_SubDialogs;

import Classes.ChequeNumberFormatter;
import Classes.DecimalOnlyFilter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NumberOnlyFilter;
import Classes.TableCheckboxHandler;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Student_Management.StudentAdditionalFees;
import JPA_DAO.Inventory.ItemDAO;
import JPA_DAO.Settings.CourseDAO;
import JPA_DAO.Student_Management.StudentAdditionalFeesDAO;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
import Panels.Fees_Management;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.PlainDocument;

public class Round_Payment extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Round_Payment.class.getName());
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
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
        rp_student_name_combo.setSelectedItem(this.studentName);

        styleDateChooser.applyDarkTheme(rp_date);

        //JComboPopulatesBankInfo();
        rp_due_table.setDefaultRenderer(Object.class, new TableGradientCell());
        rp_due_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        loadCourseDuesToTable(this.selectedStudentIds);
        calculateTotal();

        new TableCheckboxHandler(rp_due_table, 6, rp_round_total_pay_cash_text, rp_round_cheque_amount);

//        reg_misc_amount_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        reg_misc_amount_text.putClientProperty("JComponent.focusWidth", 2);
//
//        reg_misc_qty_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        reg_misc_qty_text.putClientProperty("JComponent.focusWidth", 2);
//
//        reg_misc_discount_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        reg_misc_discount_text.putClientProperty("JComponent.focusWidth", 2);
    }

//    private void JComboPopulatesBankInfo() {
//        // Medicine brand combo
//        reg_misc_service_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
//            public void keyReleased(KeyEvent e) {
//                String input = reg_misc_service_combo.getEditor().getItem().toString();
//                generalMethods.loadMatchingComboItemswithID(reg_misc_service_combo, "item_id", "fee_name", "fee_types", input);
//            }
//
//        });
//        setupComboSelectionListener(reg_misc_service_combo, reg_misc_amount_text);
//
//    }
    private boolean itemSelectedByUser = false;

//    public void setupComboSelectionListener(JComboBox<String> comboBox, JComponent nextFocusComponent) {
//        comboBox.addPopupMenuListener(new PopupMenuListener() {
//            @Override
//            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//                itemSelectedByUser = false;
//            }
//
//            @Override
//            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//                if (itemSelectedByUser) {
//                    Object selected = comboBox.getSelectedItem();
//                    if (selected != null) {
//                        String selectedValue = selected.toString().trim();
//                        if (!selectedValue.isEmpty() && isValueFromList(comboBox, selectedValue)) {
//
//                            ItemDAO itemDAO = new ItemDAO();
//                            StudentAdditionalFeesDAO studentAdditionalFeesDAO = new StudentAdditionalFeesDAO();
//                            int itemId = generalMethods.extractIdFromCombo(reg_misc_service_combo.getEditor().getItem().toString());
//                            String fee_name = generalMethods.extractNameFromCombo(reg_misc_service_combo.getEditor().getItem().toString());
//                            feeID = studentAdditionalFeesDAO.getFeeTypeId(itemId, fee_name);
//
//                            Object[] data = itemDAO.getItemLatestPriceAndStock(itemId, fee_name);
//
//                            double price = (double) data[0];
//                            double stock = (double) data[1];
//
//                            reg_misc_stock_text.setText(GeneralMethods.formatWithComma(stock));
//                            reg_misc_amount_text.setText(GeneralMethods.formatWithComma(price));
//
//                            if (itemId == 0) {
//                                reg_misc_qty_text.setEnabled(false);
//                                reg_misc_discount_text.requestFocus();
//                            } else {
//                                reg_misc_qty_text.setEnabled(true);
//
//                            }
//                            nextFocusComponent.requestFocus();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void popupMenuCanceled(PopupMenuEvent e) {
//                itemSelectedByUser = false;
//            }
//        });
//
//        // Detect user selection from keyboard (Enter) or mouse (click)
//        comboBox.addActionListener(e -> {
//            if (comboBox.isPopupVisible()) {
//                itemSelectedByUser = true;
//            }
//        });
//
//    }
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
            double balance = Double.parseDouble(row[3].toString());

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
                name,
                qty,
                GeneralMethods.formatWithComma(balance),
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

            // ✅ STEP 1: GET SUMMED ISSUED AMOUNT PER fee_type_id
            List<Object[]> issuedList = em.createNativeQuery(
                    "SELECT fee_type_id, SUM(amount) "
                    + "FROM student_additional_fees "
                    + "WHERE student_id = ? AND status = 1 "
                    + "GROUP BY fee_type_id"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            for (Object[] row : issuedList) {

                int feeTypeId = Integer.parseInt(row[0].toString());
                double totalIssued = Double.parseDouble(row[1].toString());

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
                double balance = totalIssued - totalPaid;

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
                    feeName,
                    1,
                    GeneralMethods.formatWithComma(balance),
                    "",
                    false,
                    "ADD_" + feeTypeId // ✅ grouped by fee_type_id
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
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

        for (int i = 0; i < model.getRowCount(); i++) {

            Object value = GeneralMethods.parseCommaNumber(model.getValueAt(i, 4).toString()); // column index 10

            if (value != null && !value.toString().isEmpty()) {
                total += Double.parseDouble(String.valueOf(value));
            }
        }

        rp_total_due_text.setText(GeneralMethods.formatWithComma(total));
    }

    public void calculateRoundDistribution() {

        double enteredAmount = GeneralMethods.parseCommaNumber(rp_round_calculate_text.getText());
        double totalDue = GeneralMethods.parseCommaNumber(rp_total_due_text.getText());

        System.out.println("Entered: " + enteredAmount);
        System.out.println("Total Due: " + totalDue);

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
        System.out.println("----- ORIGINAL TABLE -----");

        for (int i = 0; i < rowCount; i++) {

            Object val = model.getValueAt(i, 4);

            double due = 0;

            if (val != null && !val.toString().trim().isEmpty()) {
                due = GeneralMethods.parseCommaNumber(val.toString());
            }

            System.out.println("Row " + i + " Due: " + due);

            list.add(new Object[]{i, due});
        }

        // =========================
        // SORT
        // =========================
        list.sort((a, b) -> Double.compare((double) a[1], (double) b[1]));

        System.out.println("----- AFTER SORT -----");

        for (Object[] r : list) {
            System.out.println("Row: " + r[0] + " Sorted Due: " + r[1]);
        }

        double remaining = enteredAmount;

        // =========================
        // CLEAR OLD VALUES
        // =========================
        for (int i = 0; i < rowCount; i++) {
            model.setValueAt("", i, 5);
            model.setValueAt(false, i, 6);
        }

        // =========================
        // DISTRIBUTION
        // =========================
        System.out.println("----- DISTRIBUTION -----");

        for (Object[] r : list) {

            int row = (int) r[0];
            double due = (double) r[1];

            if (remaining <= 0) {
                break;
            }

            double payAmount;

            if (remaining >= due) {
                payAmount = due;
            } else {
                payAmount = remaining;
            }

            System.out.println("Row: " + row
                    + " | Due: " + due
                    + " | Paying: " + payAmount
                    + " | Remaining BEFORE: " + remaining);

            model.setValueAt(GeneralMethods.formatWithComma(payAmount), row, 5);
            model.setValueAt(true, row, 6);

            remaining -= payAmount;

            System.out.println("Remaining AFTER: " + remaining);
        }

    calculateTotal();
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
        jLabel10 = new javax.swing.JLabel();
        buttonGradientRound1 = new Classes.ButtonGradientRound();
        rp_total_due_text = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        rp_round_calculate_text = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        rp_round_total_pay_cash_text = new javax.swing.JTextField();
        firstName_label7 = new javax.swing.JLabel();
        buttonGradient2 = new Classes.ButtonGradient();
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
        buttonGradient3 = new Classes.ButtonGradient();
        rp_round_total_pending_cheque_text = new javax.swing.JTextField();
        firstName_label9 = new javax.swing.JLabel();
        rp_round_remaining_bal_text = new javax.swing.JTextField();
        firstName_label8 = new javax.swing.JLabel();

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
                "#", "Category", "Service/Item", "Qty", "Due Amount", "Payable", "", "ids"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true, false
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
            rp_due_table.getColumnModel().getColumn(2).setPreferredWidth(200);
            rp_due_table.getColumnModel().getColumn(6).setMinWidth(50);
            rp_due_table.getColumnModel().getColumn(6).setPreferredWidth(50);
            rp_due_table.getColumnModel().getColumn(6).setMaxWidth(50);
            rp_due_table.getColumnModel().getColumn(7).setMinWidth(60);
            rp_due_table.getColumnModel().getColumn(7).setPreferredWidth(60);
            rp_due_table.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        jLabel10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel10.setText("Total Due");

        buttonGradientRound1.setText("X");
        buttonGradientRound1.setFont(new java.awt.Font("Roboto Black", 0, 17)); // NOI18N
        buttonGradientRound1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradientRound1ActionPerformed(evt);
            }
        });

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
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(rp_date, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rp_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 248, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rp_round_calculate_text, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18))))
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rp_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rp_round_calculate_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rp_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

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

        buttonGradient2.setText("DELETE");
        buttonGradient2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient2ActionPerformed(evt);
            }
        });

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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(firstName_label7, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_round_total_pay_cash_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(27, 27, 27)
                        .addComponent(rp_round_payement_method_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rp_round_payement_method_combo)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rp_round_total_pay_cash_text)
                    .addComponent(firstName_label7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        buttonGradient3.setText("SAVE");
        buttonGradient3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rp_round_cheque_number_text)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(rp_round_bank_name_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_round_cheque_branch, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(rp_round_cheque_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_round_cheque_date, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rp_round_cheque_status, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)))
                .addContainerGap())
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
                .addGap(121, 121, 121)
                .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(firstName_label8)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(firstName_label9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rp_round_remaining_bal_text)
                            .addComponent(rp_round_total_pending_cheque_text, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                .addGap(123, 123, 123)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rp_round_total_pending_cheque_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstName_label9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rp_round_remaining_bal_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstName_label8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {rp_round_remaining_bal_text, rp_round_total_pending_cheque_text});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void buttonGradientRound1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound1ActionPerformed


    }//GEN-LAST:event_buttonGradientRound1ActionPerformed

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

    private void buttonGradient2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient2ActionPerformed


    }//GEN-LAST:event_buttonGradient2ActionPerformed

    private void rp_round_remaining_bal_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_remaining_bal_textActionPerformed

    }//GEN-LAST:event_rp_round_remaining_bal_textActionPerformed

    private void rp_round_remaining_bal_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_remaining_bal_textKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_remaining_bal_textKeyPressed

    private void rp_round_remaining_bal_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_remaining_bal_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_remaining_bal_textKeyReleased

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed


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

    private void buttonGradient3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient3ActionPerformed

    }//GEN-LAST:event_buttonGradient3ActionPerformed

    private void rp_round_calculate_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rp_round_calculate_textActionPerformed
        calculateRoundDistribution();
    }//GEN-LAST:event_rp_round_calculate_textActionPerformed

    private void rp_round_calculate_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rp_round_calculate_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_rp_round_calculate_textKeyTyped

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
    private Classes.ButtonGradient buttonGradient2;
    private Classes.ButtonGradient buttonGradient3;
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradientRound buttonGradientRound1;
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
