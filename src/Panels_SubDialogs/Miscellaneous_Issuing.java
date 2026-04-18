package Panels_SubDialogs;

import Classes.ChequeNumberFormatter;
import Classes.DecimalOnlyFilter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NumberOnlyFilter;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Student_Management.StudentAdditionalFees;
import JPA_DAO.Inventory.ItemDAO;
import JPA_DAO.Settings.CourseDAO;
import JPA_DAO.Student_Management.StudentAdditionalFeesDAO;
import Panels.Fees_Management;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

public class Miscellaneous_Issuing extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Miscellaneous_Issuing.class.getName());
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

    public Miscellaneous_Issuing(Window parent, int selectedStudentIds, String studentName, String username, String role) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.parentForm = parentForm;
        this.selectedStudentIds = selectedStudentIds;
        this.studentName = studentName;
        this.username = username;
        this.role = role;
        initComponents();

        reg_misc_date.setDate(new Date());
        reg_misc_student_name_combo.setSelectedItem(this.studentName);

        styleDateChooser.applyDarkTheme(reg_misc_date);

        JComboPopulatesBankInfo();

        reg_misc_issued_table.setDefaultRenderer(Object.class, new TableGradientCell());
        reg_misc_issued_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        reg_misc_amount_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        reg_misc_amount_text.putClientProperty("JComponent.focusWidth", 2);

        reg_misc_qty_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        reg_misc_qty_text.putClientProperty("JComponent.focusWidth", 2);

        reg_misc_discount_text.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        reg_misc_discount_text.putClientProperty("JComponent.focusWidth", 2);
    }

    private void JComboPopulatesBankInfo() {
        // Medicine brand combo
        reg_misc_service_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = reg_misc_service_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(reg_misc_service_combo, "item_id", "fee_name", "fee_types", input);
            }

        });
        setupComboSelectionListener(reg_misc_service_combo, reg_misc_amount_text);

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

                            ItemDAO itemDAO = new ItemDAO();
                            StudentAdditionalFeesDAO studentAdditionalFeesDAO = new StudentAdditionalFeesDAO();
                            int itemId = generalMethods.extractIdFromCombo(reg_misc_service_combo.getEditor().getItem().toString());
                            String fee_name = generalMethods.extractNameFromCombo(reg_misc_service_combo.getEditor().getItem().toString());
                            feeID = studentAdditionalFeesDAO.getFeeTypeId(itemId, fee_name);

                            Object[] data = itemDAO.getItemLatestPriceAndStock(itemId, fee_name);

                            double price = (double) data[0];
                            double stock = (double) data[1];

                            reg_misc_stock_text.setText(GeneralMethods.formatWithComma(stock));
                            reg_misc_amount_text.setText(GeneralMethods.formatWithComma(price));

                            if (itemId == 0) {
                                reg_misc_qty_text.setEnabled(false);
                                reg_misc_discount_text.requestFocus();
                            } else {
                                reg_misc_qty_text.setEnabled(true);

                            }
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

    private void calculateServiceTotal() {

        DefaultTableModel model = (DefaultTableModel) reg_misc_issued_table.getModel();

        double total = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object value = GeneralMethods.parseCommaNumber(model.getValueAt(i, 5).toString()); // column index 10

            if (value != null && !value.toString().isEmpty()) {
                total += Double.parseDouble(String.valueOf(value));
            }
        }

        reg_misc_total_due_text.setText(GeneralMethods.formatWithComma(total));
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
        reg_misc_student_name_combo = new javax.swing.JComboBox<>();
        reg_misc_date = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        reg_misc_service_combo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        reg_misc_stock_text = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        reg_misc_discount_text = new javax.swing.JTextField();
        reg_misc_amount_text = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        reg_misc_total_text = new javax.swing.JTextField();
        reg_misc_qty_text = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        reg_misc_issued_table = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        reg_misc_total_due_text = new javax.swing.JTextField();
        buttonGradientRound1 = new Classes.ButtonGradientRound();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelRound2.setBackground(new java.awt.Color(247, 178, 50));
        panelRound2.setRoundBottomLeft(10);
        panelRound2.setRoundBottomRight(10);
        panelRound2.setRoundTopLeft(10);
        panelRound2.setRoundTopRight(10);

        Main_Lable.setFont(new java.awt.Font("Roboto Black", 3, 14)); // NOI18N
        Main_Lable.setForeground(new java.awt.Color(255, 255, 255));
        Main_Lable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Main_Lable.setText("Issue Miscellaneous Items");

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

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Student Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Issue Date");

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText(" Student Name");

        reg_misc_student_name_combo.setEditable(true);
        reg_misc_student_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        reg_misc_date.setForeground(new java.awt.Color(204, 204, 204));
        reg_misc_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel9.setText("Total");

        reg_misc_service_combo.setEditable(true);
        reg_misc_service_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel7.setText("Stock");

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("Service / Item");

        reg_misc_stock_text.setEditable(false);
        reg_misc_stock_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jButton5.setBackground(new java.awt.Color(102, 102, 102));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/info24.png"))); // NOI18N
        jButton5.setToolTipText("Course Enrolment");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Amount");

        reg_misc_discount_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_misc_discount_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_misc_discount_textActionPerformed(evt);
            }
        });
        reg_misc_discount_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                reg_misc_discount_textKeyTyped(evt);
            }
        });

        reg_misc_amount_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_misc_amount_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_misc_amount_textActionPerformed(evt);
            }
        });
        reg_misc_amount_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                reg_misc_amount_textKeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Qty");

        jLabel6.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel6.setText("Discount Amount");

        reg_misc_total_text.setEditable(false);
        reg_misc_total_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        reg_misc_qty_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_misc_qty_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_misc_qty_textActionPerformed(evt);
            }
        });
        reg_misc_qty_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                reg_misc_qty_textKeyTyped(evt);
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
                            .addComponent(reg_misc_date, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(reg_misc_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(reg_misc_service_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reg_misc_stock_text, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(reg_misc_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reg_misc_qty_text, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(reg_misc_total_text, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(reg_misc_discount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reg_misc_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reg_misc_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reg_misc_discount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(reg_misc_total_text)
                            .addComponent(reg_misc_qty_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reg_misc_service_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(reg_misc_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(reg_misc_stock_text)))
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Issued Miscellaneous", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        reg_misc_issued_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Service/Item", "Amount", "Qty", "Discount", "Total", "additional_fee_id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        reg_misc_issued_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reg_misc_issued_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(reg_misc_issued_table);
        if (reg_misc_issued_table.getColumnModel().getColumnCount() > 0) {
            reg_misc_issued_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            reg_misc_issued_table.getColumnModel().getColumn(1).setPreferredWidth(200);
            reg_misc_issued_table.getColumnModel().getColumn(6).setMinWidth(0);
            reg_misc_issued_table.getColumnModel().getColumn(6).setPreferredWidth(0);
            reg_misc_issued_table.getColumnModel().getColumn(6).setMaxWidth(0);
        }

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel10.setText("Total Due");

        reg_misc_total_due_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        reg_misc_total_due_text.setForeground(new java.awt.Color(255, 0, 0));
        reg_misc_total_due_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_misc_total_due_textActionPerformed(evt);
            }
        });
        reg_misc_total_due_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                reg_misc_total_due_textKeyTyped(evt);
            }
        });

        buttonGradientRound1.setText("X");
        buttonGradientRound1.setFont(new java.awt.Font("Roboto Black", 0, 17)); // NOI18N
        buttonGradientRound1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradientRound1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reg_misc_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reg_misc_total_due_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

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

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Miscellaneous_Item_Loading dialog = new Miscellaneous_Item_Loading(parentFrame);
        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);

    }//GEN-LAST:event_jButton5ActionPerformed

    private void reg_misc_amount_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_misc_amount_textActionPerformed

        try {

            String ser_name = reg_misc_service_combo.getEditor().getItem().toString();
            int item_id = generalMethods.extractIdFromCombo(reg_misc_service_combo.getEditor().getItem().toString());
            if (item_id == 0) {

                StudentAdditionalFeesDAO dao = new StudentAdditionalFeesDAO();
                StudentAdditionalFees fee = new StudentAdditionalFees();

                double amount = GeneralMethods.parseCommaNumber(reg_misc_amount_text.getText());
                double qty = 0.00;
                double discount = 0.00;
                fee.setStudentId(selectedStudentIds);
                fee.setEnrollmentId(null);
                fee.setFeeTypeId(feeID);
                fee.setAmount(GeneralMethods.parseCommaNumber(reg_misc_amount_text.getText()));
                fee.setIssuedDate(reg_misc_date.getDate());
                fee.setUser(username);
                fee.setStatus(1);

                dao.save(fee);

                logHelper.log(
                        "MISC_PAYMENTS",
                        selectedStudentIds,
                        "SERVICE ISSUED",
                        ser_name,
                        amount,
                        String.format("Issued %s: Amt %.2f",
                                ser_name, amount),
                        username
                );

                // ✅ LOG: Miscellaneous Fee Issuance
                double finalTotal = 0.00;

                DefaultTableModel model = (DefaultTableModel) reg_misc_issued_table.getModel();
                int count = model.getRowCount() + 1;
                Object[] data = {
                    count,
                    ser_name,
                    GeneralMethods.formatWithComma(amount),
                    GeneralMethods.formatWithComma(qty),
                    GeneralMethods.formatWithComma(discount),
                    GeneralMethods.formatWithComma(amount)
                };

                model.addRow(data);
                reg_misc_service_combo.removeAllItems();
                reg_misc_stock_text.setText("");
                reg_misc_amount_text.setText("");
                reg_misc_qty_text.setText("");
                reg_misc_total_text.setText("");
                reg_misc_discount_text.setText("");
                reg_misc_service_combo.requestFocus();

            }

            reg_misc_qty_text.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_reg_misc_amount_textActionPerformed

    private void reg_misc_amount_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reg_misc_amount_textKeyTyped
        ((AbstractDocument) reg_misc_amount_text.getDocument())
                .setDocumentFilter(new DecimalOnlyFilter());
    }//GEN-LAST:event_reg_misc_amount_textKeyTyped

    private void reg_misc_qty_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_misc_qty_textActionPerformed

        if (reg_misc_amount_text.getText().equals("") || reg_misc_service_combo.getEditor().getItem().toString().equals("") || reg_misc_qty_text.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double stock = GeneralMethods.parseCommaNumber(reg_misc_stock_text.getText());
        double amount = GeneralMethods.parseCommaNumber(reg_misc_amount_text.getText());
        double qty = GeneralMethods.parseCommaNumber(reg_misc_qty_text.getText());

        if (qty > stock) {
            JOptionPane.showMessageDialog(null, "No enough stock available!", "NO STOCK", JOptionPane.WARNING_MESSAGE);
            reg_misc_qty_text.selectAll();
            reg_misc_qty_text.requestFocus();
            return;
        }

        reg_misc_total_text.setText(GeneralMethods.formatWithComma(amount * qty));

        reg_misc_discount_text.requestFocus();
    }//GEN-LAST:event_reg_misc_qty_textActionPerformed

    private void reg_misc_qty_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reg_misc_qty_textKeyTyped
        ((AbstractDocument) reg_misc_qty_text.getDocument())
                .setDocumentFilter(new DecimalOnlyFilter());
    }//GEN-LAST:event_reg_misc_qty_textKeyTyped

    private void reg_misc_discount_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_misc_discount_textActionPerformed

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {

            String ser_name = reg_misc_service_combo.getEditor().getItem().toString();

            if (ser_name.equals("")
                    || reg_misc_amount_text.getText().equals("") || reg_misc_qty_text.getText().equals("")
                    || reg_misc_service_combo.getEditor().getItem().toString().equals("")
                    || feeID == 0 || selectedStudentIds == 0) {
                JOptionPane.showMessageDialog(null, "Fields cannot be empty", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            tx.begin();

            StudentAdditionalFeesDAO dao = new StudentAdditionalFeesDAO();
            StudentAdditionalFees fee = new StudentAdditionalFees();

            double stock = GeneralMethods.parseCommaNumber(reg_misc_stock_text.getText());
            double amount = GeneralMethods.parseCommaNumber(reg_misc_amount_text.getText());
            double qty = GeneralMethods.parseCommaNumber(reg_misc_qty_text.getText());
            double discount = GeneralMethods.parseCommaNumber(reg_misc_discount_text.getText());
            double finalTotal = (amount * qty) - discount;

            if (qty > stock) {
                JOptionPane.showMessageDialog(null, "No enough stock available!", "NO STOCK", JOptionPane.WARNING_MESSAGE);
                reg_misc_qty_text.selectAll();
                reg_misc_qty_text.requestFocus();
                return;
            }

            fee.setStudentId(selectedStudentIds);
            fee.setEnrollmentId(null);
            fee.setFeeTypeId(feeID);
            fee.setQty(qty);
            fee.setAmount(finalTotal);
            fee.setIssuedDate(reg_misc_date.getDate());
            fee.setUser(username);
            fee.setStatus(1);
            int generatedId = dao.save(fee);
            
            int item_id = generalMethods.extractIdFromCombo(reg_misc_service_combo.getEditor().getItem().toString());

            em.createNativeQuery(
                    "INSERT INTO stock_transactions "
                    + "(item_id, student_id, quantity, transaction_type, transaction_date, remarks, user, status) "
                    + "VALUES (?, ?, ?, 'OUT', NOW(), 'Student Purchased', ?, 1)"
            )
                    .setParameter(1, item_id)
                    .setParameter(2, selectedStudentIds)
                    .setParameter(3, finalTotal)
                    .setParameter(4, username)
                    .executeUpdate();

            tx.commit();

            // ✅ LOG: Miscellaneous Fee Issuance
            logHelper.log(
                    "MISC_PAYMENTS",
                    selectedStudentIds,
                    "ITEM ISSUED",
                    ser_name,
                    finalTotal,
                    String.format("Issued %s: Qty %.0f, Amt %.2f, Disc %.2f. Total: %.2f",
                            ser_name, qty, amount, discount, finalTotal),
                    username
            );

            DefaultTableModel model = (DefaultTableModel) reg_misc_issued_table.getModel();
            int count = model.getRowCount() + 1;
            Object[] data = {
                count,
                ser_name,
                GeneralMethods.formatWithComma(amount),
                GeneralMethods.formatWithComma(qty),
                GeneralMethods.formatWithComma(discount),
                GeneralMethods.formatWithComma(finalTotal),
                generatedId
            };

            model.addRow(data);
            calculateServiceTotal();

            reg_misc_service_combo.removeAllItems();
            reg_misc_stock_text.setText("");
            reg_misc_amount_text.setText("");
            reg_misc_qty_text.setText("");
            reg_misc_total_text.setText("");
            reg_misc_discount_text.setText("");
            reg_misc_service_combo.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }//GEN-LAST:event_reg_misc_discount_textActionPerformed

    private void reg_misc_discount_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reg_misc_discount_textKeyTyped
        ((AbstractDocument) reg_misc_discount_text.getDocument())
                .setDocumentFilter(new DecimalOnlyFilter());
    }//GEN-LAST:event_reg_misc_discount_textKeyTyped

    private void reg_misc_issued_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reg_misc_issued_tableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_reg_misc_issued_tableMouseClicked

    private void reg_misc_total_due_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_misc_total_due_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reg_misc_total_due_textActionPerformed

    private void reg_misc_total_due_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reg_misc_total_due_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_reg_misc_total_due_textKeyTyped

    private void buttonGradientRound1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound1ActionPerformed

        int selectedRow = reg_misc_issued_table.getSelectedRow();

        // =========================
        // NO ROW SELECTED
        // =========================
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to delete");
            return;
        }

        // =========================
        // CONFIRMATION
        // =========================
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Do you want to delete selected record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) reg_misc_issued_table.getModel();

        try {

            // GET ID FROM COLUMN INDEX 6
            int id = Integer.parseInt(model.getValueAt(selectedRow, 6).toString());
            String serviceName = model.getValueAt(selectedRow, 1).toString();
            double deletedAmount = GeneralMethods.parseCommaNumber(model.getValueAt(selectedRow, 5).toString());

            // CALL DAO
            StudentAdditionalFeesDAO dao = new StudentAdditionalFeesDAO();
            dao.softDelete(id);

            // ✅ AUDIT LOG
            logHelper.log(
                    "MISC_PAYMENTS",
                    selectedStudentIds,
                    "SERVICE DELETE",
                    serviceName,
                    deletedAmount,
                    "Removed " + serviceName + " record from student account.",
                    username
            );

            // REMOVE FROM TABLE
            model.removeRow(selectedRow);

            // =========================
            // OPTIONAL: REORDER #
            // =========================
            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(i + 1, i, 0);
            }
            calculateServiceTotal();

            JOptionPane.showMessageDialog(null, "Deleted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting record!");
        }

    }//GEN-LAST:event_buttonGradientRound1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Miscellaneous_Issuing dialog
                    = new Miscellaneous_Issuing(frame, 0, "", "", "");

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel Main_Lable;
    private Classes.ButtonGradientRound buttonGradientRound1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private Classes.PanelRound panelRound2;
    public static javax.swing.JTextField reg_misc_amount_text;
    public static com.toedter.calendar.JDateChooser reg_misc_date;
    private javax.swing.JTextField reg_misc_discount_text;
    private javax.swing.JTable reg_misc_issued_table;
    private javax.swing.JTextField reg_misc_qty_text;
    public static javax.swing.JComboBox<String> reg_misc_service_combo;
    public static javax.swing.JTextField reg_misc_stock_text;
    private javax.swing.JComboBox<String> reg_misc_student_name_combo;
    private javax.swing.JTextField reg_misc_total_due_text;
    private javax.swing.JTextField reg_misc_total_text;
    // End of variables declaration//GEN-END:variables

}
