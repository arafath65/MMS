/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Panels;

import Additional.PermissionService;
import Classes.GeneralMethods;
import Classes.TableGradientCell;
import Classes.ButtonGradientRound;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Entities.Madhrasa_Profile.MadhrasaProfile;
import Entities.Settings.Course;
import Entities.Settings.StudentClass;
import Entities.Settings.UserPermission;
import Entities.Student_Management.FeeTypes;
import JPA_DAO.Madhrasa_Profile.MadhrasaProfileDAO;
import JPA_DAO.Settings.ClassDAO;
import JPA_DAO.Settings.CourseDAO;
import JPA_DAO.Settings.UserPermissionDAO;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class User_Permissions extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    private File selectedImageFile;

    int selectedRoleId = 0;
    String username;
    String role;

    private List<JCheckBox> permissionBoxes = new ArrayList<>();

    public User_Permissions(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        user_per_add_role_Table.setDefaultRenderer(Object.class, new TableGradientCell());
        user_per_add_role_Table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        initializePermissions();
        initializePermissionList();
        SelectandUnselectAllCheckboxes();

        loadUserRoles(user_per_add_role_Table);

        user_per_add_role_Table.getSelectionModel().addListSelectionListener(e -> {

            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = user_per_add_role_Table.getSelectedRow();

            if (row == -1) {
                return;
            }

            int roleId = Integer.parseInt(
                    user_per_add_role_Table.getValueAt(row, 2).toString());

            selectedRoleId = roleId;

            loadRolePermissions(roleId);

        });

    }

    private void SelectandUnselectAllCheckboxes() {

        // ************ DASHBOARD
        setupPermissionGroup(
                DASH_STU_ALL,
                DASH_STU_NEW_ADMISSION,
                DASH_STU_FEES_HANDLING,
                DASH_STU_BATCH_TRANSFER,
                DASH_STU_REPORTS
        );
        setupPermissionGroup(
                DASH_DON_ALL,
                DASH_DON_HANDLING,
                DASH_DON_REPORTS,
                DASH_INV_ADD_INVENTORY,
                DASH_INV_REPORTS
        );
        setupPermissionGroup(
                DASH_EMP_ALL,
                DASH_EMP_NEW_REGISTRATION
        );
        setupPermissionGroup(
                DASH_ACC_ALL,
                DASH_ACC_CHQ_HANDLING
        );
        setupPermissionGroup(
                DASH_SETT_ALL,
                DASH_SETT_REGISTER_COURSE,
                DASH_SETT_ADDI_PAYMENTS,
                DASH_SETT_USER_PERMISSIONS
        );

        // ************ STUDENT MANAGEMENT
        setupPermissionGroup(
                ST_NEW_ALL,
                ST_SAVE_RECORD,
                ST_UPDATE_RECORD,
                ST_DELETE_RECORD,
                ST_SIBBLINGS,
                ST_COURSE_ENROLMENT,
                ST_MISCELLANEOUS,
                ST_ELIMINATION
        );

        // ************ EMPLOYEE MANAGEMENT
        setupPermissionGroup(
                EMP_NEW_ALL,
                EMP_REG_SAVE,
                EMP_REG_DELETE,
                EMP_REG_CAREER_HISTORY,
                EMP_REG_BASIC_SALARY,
                EMP_REG_BANK_ACCOUNT
        );

    }

    private void JComboPopulates() {
        // Medicine brand combo
//        reg_add_paym_category_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
//            public void keyReleased(KeyEvent e) {
//                String input = reg_add_paym_category_combo.getEditor().getItem().toString();
//                generalMethods.loadMatchingComboItems(reg_add_paym_category_combo, "fee_category", "fee_types", input);
//            }
//
//        });
//        setupComboSelectionListener(reg_add_paym_category_combo, reg_add_paym_amount_text);
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

    public void loadUserRoles(JTable table) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            List<Object[]> list = em.createNativeQuery(
                    "SELECT user_roles_id, user_roles "
                    + "FROM user_roles "
                    + "WHERE status = 1 "
                    + "ORDER BY user_roles ASC")
                    .getResultList();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int count = 1;

            for (Object[] row : list) {

                model.addRow(new Object[]{
                    count++, // #
                    row[1], // Role Name
                    row[0] // Hidden ID
                });

            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    public boolean softDeleteRole(int roleId) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            em.createNativeQuery(
                    "UPDATE user_roles SET status = 0 WHERE user_roles_id = ?"
            )
                    .setParameter(1, roleId)
                    .executeUpdate();

            tx.commit();
            return true;

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            e.printStackTrace();
            return false;

        } finally {
            em.close();
        }
    }

    private void loadRolePermissions(int roleId) {

        try {

            UserPermissionDAO dao = new UserPermissionDAO();
            clearPermissionCheckboxes();

            List<String> permissions
                    = dao.getActivePermissionsByRole(selectedRoleId);
            System.out.println("Role ID : " + roleId);
            System.out.println("Permissions : " + permissions);

            for (String permission : permissions) {

                for (JCheckBox box : permissionBoxes) {

                    if (permission.equals(box.getName())) {

                        box.setSelected(true);
                        break;

                    }

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private void initializePermissions() {

        // ************ DASHBOARD
        DASH_STU_NEW_ADMISSION.setName("DASH_STU_NEW_ADMISSION");
        DASH_STU_FEES_HANDLING.setName("DASH_STU_FEES_HANDLING");
        DASH_STU_BATCH_TRANSFER.setName("DASH_STU_BATCH_TRANSFER");
        DASH_STU_REPORTS.setName("DASH_STU_REPORTS");

        DASH_DON_HANDLING.setName("DASH_DON_HANDLING");
        DASH_DON_REPORTS.setName("DASH_DON_REPORTS");
        DASH_INV_ADD_INVENTORY.setName("DASH_INV_ADD_INVENTORY");
        DASH_INV_REPORTS.setName("DASH_INV_REPORTS");

        DASH_EMP_NEW_REGISTRATION.setName("DASH_EMP_NEW_REGISTRATION");
        DASH_ACC_CHQ_HANDLING.setName("DASH_ACC_CHQ_HANDLING");

        DASH_SETT_REGISTER_COURSE.setName("DASH_SETT_REGISTER_COURSE");
        DASH_SETT_ADDI_PAYMENTS.setName("DASH_SETT_ADDI_PAYMENTS");
        DASH_SETT_USER_PERMISSIONS.setName("DASH_SETT_USER_PERMISSIONS");

        // ************ STUDENT MANAGEMENT
        ST_SAVE_RECORD.setName("ST_SAVE_RECORD");
        ST_UPDATE_RECORD.setName("ST_UPDATE_RECORD");
        ST_DELETE_RECORD.setName("ST_DELETE_RECORD");
        ST_SIBBLINGS.setName("ST_SIBBLINGS");
        ST_COURSE_ENROLMENT.setName("ST_COURSE_ENROLMENT");
        ST_MISCELLANEOUS.setName("ST_MISCELLANEOUS");
        ST_ELIMINATION.setName("ST_ELIMINATION");

        // ************ EMPLOYEE MANAGEMENT
        EMP_REG_SAVE.setName("EMP_REG_SAVE");
        EMP_REG_DELETE.setName("EMP_REG_DELETE");
        EMP_REG_CAREER_HISTORY.setName("EMP_REG_CAREER_HISTORY");
        EMP_REG_BASIC_SALARY.setName("EMP_REG_BASIC_SALARY");
        EMP_REG_BANK_ACCOUNT.setName("EMP_REG_BANK_ACCOUNT");

        // Continue for all remaining checkboxes...
    }

    private void initializePermissionList() {

        // ************ DASHBOARD
        permissionBoxes.add(DASH_STU_NEW_ADMISSION);
        permissionBoxes.add(DASH_STU_FEES_HANDLING);
        permissionBoxes.add(DASH_STU_BATCH_TRANSFER);
        permissionBoxes.add(DASH_STU_REPORTS);

        permissionBoxes.add(DASH_DON_HANDLING);
        permissionBoxes.add(DASH_DON_REPORTS);
        permissionBoxes.add(DASH_INV_ADD_INVENTORY);
        permissionBoxes.add(DASH_INV_REPORTS);

        permissionBoxes.add(DASH_EMP_NEW_REGISTRATION);
        permissionBoxes.add(DASH_ACC_CHQ_HANDLING);

        permissionBoxes.add(DASH_SETT_REGISTER_COURSE);
        permissionBoxes.add(DASH_SETT_ADDI_PAYMENTS);
        permissionBoxes.add(DASH_SETT_USER_PERMISSIONS);

        // ************ STUDENT MANAGEMENT
        permissionBoxes.add(ST_SAVE_RECORD);
        permissionBoxes.add(ST_UPDATE_RECORD);
        permissionBoxes.add(ST_DELETE_RECORD);
        permissionBoxes.add(ST_SIBBLINGS);
        permissionBoxes.add(ST_COURSE_ENROLMENT);
        permissionBoxes.add(ST_MISCELLANEOUS);
        permissionBoxes.add(ST_ELIMINATION);

        // ************ EMPLOYEE MANAGEMENT
        permissionBoxes.add(EMP_REG_SAVE);
        permissionBoxes.add(EMP_REG_DELETE);
        permissionBoxes.add(EMP_REG_CAREER_HISTORY);
        permissionBoxes.add(EMP_REG_BASIC_SALARY);
        permissionBoxes.add(EMP_REG_BANK_ACCOUNT);

        // Continue for every checkbox...
    }

    private void clearPermissionCheckboxes() {

        for (JCheckBox box : permissionBoxes) {
            box.setSelected(false);
        }

    }

    private void setupPermissionGroup(JCheckBox selectAll, JCheckBox... checkBoxes) {

        final boolean[] updating = {false};

        // ALL -> Children
        selectAll.addActionListener(e -> {

            if (updating[0]) {
                return;
            }

            updating[0] = true;

            boolean selected = selectAll.isSelected();

            for (JCheckBox box : checkBoxes) {
                box.setSelected(selected);
            }

            updating[0] = false;

        });

        // Children -> ALL
        ActionListener childListener = e -> {

            if (updating[0]) {
                return;
            }

            updating[0] = true;

            boolean allSelected = true;

            for (JCheckBox box : checkBoxes) {

                if (!box.isSelected()) {
                    allSelected = false;
                    break;
                }

            }

            selectAll.setSelected(allSelected);

            updating[0] = false;

        };

        for (JCheckBox box : checkBoxes) {
            box.addActionListener(childListener);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        user_per_add_role_text = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        user_per_add_role_Table = new javax.swing.JTable();
        buttonGradientRound1 = new Classes.ButtonGradientRound();
        reg_misc_student_name_combo = new javax.swing.JComboBox<>();
        jButton6 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        DASH_STU_FEES_HANDLING = new javax.swing.JCheckBox();
        DASH_STU_NEW_ADMISSION = new javax.swing.JCheckBox();
        DASH_STU_BATCH_TRANSFER = new javax.swing.JCheckBox();
        DASH_STU_ALL = new javax.swing.JCheckBox();
        jLabel29 = new javax.swing.JLabel();
        DASH_STU_REPORTS = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        DASH_DON_ALL = new javax.swing.JCheckBox();
        jLabel30 = new javax.swing.JLabel();
        DASH_DON_HANDLING = new javax.swing.JCheckBox();
        DASH_DON_REPORTS = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        DASH_EMP_ALL = new javax.swing.JCheckBox();
        jSeparator5 = new javax.swing.JSeparator();
        DASH_EMP_NEW_REGISTRATION = new javax.swing.JCheckBox();
        jLabel31 = new javax.swing.JLabel();
        DASH_INV_ADD_INVENTORY = new javax.swing.JCheckBox();
        DASH_INV_REPORTS = new javax.swing.JCheckBox();
        jLabel32 = new javax.swing.JLabel();
        DASH_ACC_CHQ_HANDLING = new javax.swing.JCheckBox();
        DASH_ACC_ALL = new javax.swing.JCheckBox();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        DASH_SETT_ALL = new javax.swing.JCheckBox();
        DASH_SETT_REGISTER_COURSE = new javax.swing.JCheckBox();
        jLabel33 = new javax.swing.JLabel();
        DASH_SETT_ADDI_PAYMENTS = new javax.swing.JCheckBox();
        DASH_SETT_USER_PERMISSIONS = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        ST_UPDATE_RECORD = new javax.swing.JCheckBox();
        ST_SAVE_RECORD = new javax.swing.JCheckBox();
        ST_DELETE_RECORD = new javax.swing.JCheckBox();
        ST_NEW_ALL = new javax.swing.JCheckBox();
        jLabel21 = new javax.swing.JLabel();
        ST_COURSE_ENROLMENT = new javax.swing.JCheckBox();
        ST_MISCELLANEOUS = new javax.swing.JCheckBox();
        ST_ELIMINATION = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jCheckBox9 = new javax.swing.JCheckBox();
        jLabel25 = new javax.swing.JLabel();
        jCheckBox10 = new javax.swing.JCheckBox();
        ST_SIBBLINGS = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        EMP_REG_DELETE = new javax.swing.JCheckBox();
        EMP_REG_SAVE = new javax.swing.JCheckBox();
        EMP_REG_CAREER_HISTORY = new javax.swing.JCheckBox();
        EMP_NEW_ALL = new javax.swing.JCheckBox();
        jLabel27 = new javax.swing.JLabel();
        EMP_REG_BASIC_SALARY = new javax.swing.JCheckBox();
        EMP_REG_BANK_ACCOUNT = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        jCheckBox18 = new javax.swing.JCheckBox();
        jLabel28 = new javax.swing.JLabel();
        jCheckBox19 = new javax.swing.JCheckBox();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jCheckBox8 = new javax.swing.JCheckBox();
        jPasswordField1 = new javax.swing.JPasswordField();
        buttonGradientRound3 = new Classes.ButtonGradientRound();
        jButton1 = new javax.swing.JButton();

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "User Roles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        user_per_add_role_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        user_per_add_role_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_per_add_role_textActionPerformed(evt);
            }
        });
        user_per_add_role_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                user_per_add_role_textKeyTyped(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(0, 0, 0));
        jLabel26.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel26.setText("Add Role");

        user_per_add_role_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "User Roles", "roles_id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        user_per_add_role_Table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                user_per_add_role_TableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(user_per_add_role_Table);
        if (user_per_add_role_Table.getColumnModel().getColumnCount() > 0) {
            user_per_add_role_Table.getColumnModel().getColumn(1).setPreferredWidth(250);
            user_per_add_role_Table.getColumnModel().getColumn(2).setMinWidth(0);
            user_per_add_role_Table.getColumnModel().getColumn(2).setPreferredWidth(0);
            user_per_add_role_Table.getColumnModel().getColumn(2).setMaxWidth(0);
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(user_per_add_role_text))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user_per_add_role_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        reg_misc_student_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh.png"))); // NOI18N
        jButton6.setToolTipText("Refreshing the invoice number");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jPanel13.setBackground(new java.awt.Color(46, 45, 45));
        jPanel13.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        DASH_STU_FEES_HANDLING.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_STU_FEES_HANDLING.setForeground(new java.awt.Color(255, 255, 255));
        DASH_STU_FEES_HANDLING.setText("Fees Handling");

        DASH_STU_NEW_ADMISSION.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_STU_NEW_ADMISSION.setForeground(new java.awt.Color(255, 255, 255));
        DASH_STU_NEW_ADMISSION.setText("New Admission");

        DASH_STU_BATCH_TRANSFER.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_STU_BATCH_TRANSFER.setForeground(new java.awt.Color(255, 255, 255));
        DASH_STU_BATCH_TRANSFER.setText("Batch Transfer / Payments");
        DASH_STU_BATCH_TRANSFER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DASH_STU_BATCH_TRANSFERActionPerformed(evt);
            }
        });

        DASH_STU_ALL.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_STU_ALL.setForeground(new java.awt.Color(255, 255, 255));
        DASH_STU_ALL.setText("ALL");

        jLabel29.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("STUDENT MANAGEMENT");

        DASH_STU_REPORTS.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_STU_REPORTS.setForeground(new java.awt.Color(255, 255, 255));
        DASH_STU_REPORTS.setText("Reports");
        DASH_STU_REPORTS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DASH_STU_REPORTSActionPerformed(evt);
            }
        });

        DASH_DON_ALL.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_DON_ALL.setForeground(new java.awt.Color(255, 255, 255));
        DASH_DON_ALL.setText("ALL");

        jLabel30.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("DONATIONS & INVENTORY");

        DASH_DON_HANDLING.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_DON_HANDLING.setForeground(new java.awt.Color(255, 255, 255));
        DASH_DON_HANDLING.setText("Donation Handling");

        DASH_DON_REPORTS.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_DON_REPORTS.setForeground(new java.awt.Color(255, 255, 255));
        DASH_DON_REPORTS.setText("Donation Reports");

        DASH_EMP_ALL.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_EMP_ALL.setForeground(new java.awt.Color(255, 255, 255));
        DASH_EMP_ALL.setText("ALL");

        DASH_EMP_NEW_REGISTRATION.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_EMP_NEW_REGISTRATION.setForeground(new java.awt.Color(255, 255, 255));
        DASH_EMP_NEW_REGISTRATION.setText("New Employee Registration");

        jLabel31.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("EMPLOYEE MANAGEMENT");

        DASH_INV_ADD_INVENTORY.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_INV_ADD_INVENTORY.setForeground(new java.awt.Color(255, 255, 255));
        DASH_INV_ADD_INVENTORY.setText("Add Inventory");

        DASH_INV_REPORTS.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_INV_REPORTS.setForeground(new java.awt.Color(255, 255, 255));
        DASH_INV_REPORTS.setText("Inventory Reports");

        jLabel32.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("ACCOUNTS");

        DASH_ACC_CHQ_HANDLING.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_ACC_CHQ_HANDLING.setForeground(new java.awt.Color(255, 255, 255));
        DASH_ACC_CHQ_HANDLING.setText("Cheque Handling");

        DASH_ACC_ALL.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_ACC_ALL.setForeground(new java.awt.Color(255, 255, 255));
        DASH_ACC_ALL.setText("ALL");

        DASH_SETT_ALL.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_SETT_ALL.setForeground(new java.awt.Color(255, 255, 255));
        DASH_SETT_ALL.setText("ALL");

        DASH_SETT_REGISTER_COURSE.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_SETT_REGISTER_COURSE.setForeground(new java.awt.Color(255, 255, 255));
        DASH_SETT_REGISTER_COURSE.setText("Register Course / Grade");

        jLabel33.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("SETTINGS");

        DASH_SETT_ADDI_PAYMENTS.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_SETT_ADDI_PAYMENTS.setForeground(new java.awt.Color(255, 255, 255));
        DASH_SETT_ADDI_PAYMENTS.setText("Additional Payments");

        DASH_SETT_USER_PERMISSIONS.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        DASH_SETT_USER_PERMISSIONS.setForeground(new java.awt.Color(255, 255, 255));
        DASH_SETT_USER_PERMISSIONS.setText("User Permissions");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DASH_STU_ALL))
                    .addComponent(jSeparator3)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DASH_DON_ALL))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DASH_EMP_ALL))
                    .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DASH_ACC_ALL))
                    .addComponent(jSeparator6)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(DASH_SETT_ALL))
                    .addComponent(jSeparator7)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(DASH_STU_NEW_ADMISSION)
                                .addGap(18, 18, 18)
                                .addComponent(DASH_STU_FEES_HANDLING)
                                .addGap(18, 18, 18)
                                .addComponent(DASH_STU_BATCH_TRANSFER)
                                .addGap(18, 18, 18)
                                .addComponent(DASH_STU_REPORTS))
                            .addComponent(DASH_EMP_NEW_REGISTRATION)
                            .addComponent(DASH_ACC_CHQ_HANDLING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(DASH_SETT_REGISTER_COURSE)
                                .addGap(18, 18, 18)
                                .addComponent(DASH_SETT_ADDI_PAYMENTS)
                                .addGap(18, 18, 18)
                                .addComponent(DASH_SETT_USER_PERMISSIONS))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(DASH_DON_HANDLING)
                                .addGap(18, 18, 18)
                                .addComponent(DASH_DON_REPORTS)
                                .addGap(18, 18, 18)
                                .addComponent(DASH_INV_ADD_INVENTORY)
                                .addGap(18, 18, 18)
                                .addComponent(DASH_INV_REPORTS)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(DASH_STU_ALL))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DASH_STU_NEW_ADMISSION)
                    .addComponent(DASH_STU_FEES_HANDLING)
                    .addComponent(DASH_STU_BATCH_TRANSFER)
                    .addComponent(DASH_STU_REPORTS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(DASH_DON_ALL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DASH_DON_HANDLING)
                    .addComponent(DASH_DON_REPORTS)
                    .addComponent(DASH_INV_ADD_INVENTORY)
                    .addComponent(DASH_INV_REPORTS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(DASH_EMP_ALL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(DASH_EMP_NEW_REGISTRATION)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(DASH_ACC_ALL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(DASH_ACC_CHQ_HANDLING)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(DASH_SETT_ALL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DASH_SETT_REGISTER_COURSE)
                    .addComponent(DASH_SETT_ADDI_PAYMENTS)
                    .addComponent(DASH_SETT_USER_PERMISSIONS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("     DASHBOARD     ", jPanel2);

        jPanel8.setBackground(new java.awt.Color(46, 45, 45));
        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        ST_UPDATE_RECORD.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ST_UPDATE_RECORD.setForeground(new java.awt.Color(255, 255, 255));
        ST_UPDATE_RECORD.setText("Update Record");

        ST_SAVE_RECORD.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ST_SAVE_RECORD.setForeground(new java.awt.Color(255, 255, 255));
        ST_SAVE_RECORD.setText("Save Record");

        ST_DELETE_RECORD.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ST_DELETE_RECORD.setForeground(new java.awt.Color(255, 255, 255));
        ST_DELETE_RECORD.setText("Delete Record");
        ST_DELETE_RECORD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ST_DELETE_RECORDActionPerformed(evt);
            }
        });

        ST_NEW_ALL.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ST_NEW_ALL.setForeground(new java.awt.Color(255, 255, 255));
        ST_NEW_ALL.setText("ALL");

        jLabel21.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("NEW ADMISSION");

        ST_COURSE_ENROLMENT.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ST_COURSE_ENROLMENT.setForeground(new java.awt.Color(255, 255, 255));
        ST_COURSE_ENROLMENT.setText("Course / Grade Enrollment");
        ST_COURSE_ENROLMENT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ST_COURSE_ENROLMENTActionPerformed(evt);
            }
        });

        ST_MISCELLANEOUS.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ST_MISCELLANEOUS.setForeground(new java.awt.Color(255, 255, 255));
        ST_MISCELLANEOUS.setText("Miscellaneous Items Issuing");
        ST_MISCELLANEOUS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ST_MISCELLANEOUSActionPerformed(evt);
            }
        });

        ST_ELIMINATION.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ST_ELIMINATION.setForeground(new java.awt.Color(255, 255, 255));
        ST_ELIMINATION.setText("Student Elimination");
        ST_ELIMINATION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ST_ELIMINATIONActionPerformed(evt);
            }
        });

        jCheckBox9.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jCheckBox9.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox9.setText("ALL");

        jLabel25.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("FEES HANDLING");

        jCheckBox10.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jCheckBox10.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox10.setText("Save Record");

        ST_SIBBLINGS.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ST_SIBBLINGS.setForeground(new java.awt.Color(255, 255, 255));
        ST_SIBBLINGS.setText("Student Sibblings ");
        ST_SIBBLINGS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ST_SIBBLINGSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ST_NEW_ALL))
                    .addComponent(jSeparator1)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBox9))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ST_ELIMINATION)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(ST_SAVE_RECORD)
                                .addGap(18, 18, 18)
                                .addComponent(ST_UPDATE_RECORD)
                                .addGap(18, 18, 18)
                                .addComponent(ST_DELETE_RECORD)
                                .addGap(18, 18, 18)
                                .addComponent(ST_SIBBLINGS)
                                .addGap(18, 18, 18)
                                .addComponent(ST_COURSE_ENROLMENT)
                                .addGap(18, 18, 18)
                                .addComponent(ST_MISCELLANEOUS))
                            .addComponent(jCheckBox10))
                        .addGap(0, 64, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(ST_NEW_ALL))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ST_SAVE_RECORD)
                    .addComponent(ST_UPDATE_RECORD)
                    .addComponent(ST_DELETE_RECORD)
                    .addComponent(ST_COURSE_ENROLMENT)
                    .addComponent(ST_MISCELLANEOUS)
                    .addComponent(ST_SIBBLINGS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ST_ELIMINATION)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jCheckBox9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox10)
                .addContainerGap(349, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("     STUDENT     ", jPanel10);

        jPanel12.setBackground(new java.awt.Color(46, 45, 45));
        jPanel12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        EMP_REG_DELETE.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        EMP_REG_DELETE.setForeground(new java.awt.Color(255, 255, 255));
        EMP_REG_DELETE.setText("Delete Employee");
        EMP_REG_DELETE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EMP_REG_DELETEActionPerformed(evt);
            }
        });

        EMP_REG_SAVE.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        EMP_REG_SAVE.setForeground(new java.awt.Color(255, 255, 255));
        EMP_REG_SAVE.setText("Save / Update Employee");
        EMP_REG_SAVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EMP_REG_SAVEActionPerformed(evt);
            }
        });

        EMP_REG_CAREER_HISTORY.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        EMP_REG_CAREER_HISTORY.setForeground(new java.awt.Color(255, 255, 255));
        EMP_REG_CAREER_HISTORY.setText("Career History");
        EMP_REG_CAREER_HISTORY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EMP_REG_CAREER_HISTORYActionPerformed(evt);
            }
        });

        EMP_NEW_ALL.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        EMP_NEW_ALL.setForeground(new java.awt.Color(255, 255, 255));
        EMP_NEW_ALL.setText("ALL");

        jLabel27.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("REGISTER NEW EMPLOYEE");

        EMP_REG_BASIC_SALARY.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        EMP_REG_BASIC_SALARY.setForeground(new java.awt.Color(255, 255, 255));
        EMP_REG_BASIC_SALARY.setText("Basic Salary");
        EMP_REG_BASIC_SALARY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EMP_REG_BASIC_SALARYActionPerformed(evt);
            }
        });

        EMP_REG_BANK_ACCOUNT.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        EMP_REG_BANK_ACCOUNT.setForeground(new java.awt.Color(255, 255, 255));
        EMP_REG_BANK_ACCOUNT.setText("Bank Account Number");
        EMP_REG_BANK_ACCOUNT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EMP_REG_BANK_ACCOUNTActionPerformed(evt);
            }
        });

        jCheckBox18.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jCheckBox18.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox18.setText("ALL");

        jLabel28.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("FEES HANDLING");

        jCheckBox19.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jCheckBox19.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox19.setText("Save Record");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(EMP_NEW_ALL))
                    .addComponent(jSeparator2)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBox18))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(EMP_REG_SAVE)
                                .addGap(18, 18, 18)
                                .addComponent(EMP_REG_DELETE)
                                .addGap(18, 18, 18)
                                .addComponent(EMP_REG_CAREER_HISTORY)
                                .addGap(18, 18, 18)
                                .addComponent(EMP_REG_BASIC_SALARY)
                                .addGap(18, 18, 18)
                                .addComponent(EMP_REG_BANK_ACCOUNT))
                            .addComponent(jCheckBox19))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(EMP_NEW_ALL))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EMP_REG_SAVE)
                    .addComponent(EMP_REG_DELETE)
                    .addComponent(EMP_REG_CAREER_HISTORY)
                    .addComponent(EMP_REG_BASIC_SALARY)
                    .addComponent(EMP_REG_BANK_ACCOUNT))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jCheckBox18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox19)
                .addContainerGap(380, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("     EMPLOYEE     ", jPanel11);

        jLabel22.setBackground(new java.awt.Color(0, 0, 0));
        jLabel22.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel22.setText("Username");

        jLabel23.setBackground(new java.awt.Color(0, 0, 0));
        jLabel23.setFont(new java.awt.Font("Roboto Medium", 1, 13)); // NOI18N
        jLabel23.setText("Password");

        jCheckBox8.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jCheckBox8.setText("Show Password");
        jCheckBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox8ActionPerformed(evt);
            }
        });

        jPasswordField1.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        buttonGradientRound3.setText("SAVE");
        buttonGradientRound3.setToolTipText("Save invoice");
        buttonGradientRound3.setFont(new java.awt.Font("Roboto Black", 0, 17)); // NOI18N
        buttonGradientRound3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradientRound3ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user_permission32.png"))); // NOI18N
        jButton1.setToolTipText("Assigning User Permission");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCheckBox8)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jPasswordField1))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(reg_misc_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradientRound3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jTabbedPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(reg_misc_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradientRound3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void buttonGradientRound4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound4ActionPerformed

    }//GEN-LAST:event_buttonGradientRound4ActionPerformed

    private void ST_DELETE_RECORDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ST_DELETE_RECORDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ST_DELETE_RECORDActionPerformed

    private void ST_COURSE_ENROLMENTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ST_COURSE_ENROLMENTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ST_COURSE_ENROLMENTActionPerformed

    private void ST_MISCELLANEOUSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ST_MISCELLANEOUSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ST_MISCELLANEOUSActionPerformed

    private void ST_ELIMINATIONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ST_ELIMINATIONActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ST_ELIMINATIONActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed


    }//GEN-LAST:event_jButton6ActionPerformed

    private void jCheckBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox8ActionPerformed

    private void user_per_add_role_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_per_add_role_textActionPerformed

        try {

            String roleName = user_per_add_role_text.getText().trim();

            if (roleName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a role name.");
                return;
            }

            EntityManager em = HibernateConfig.getEntityManager();
            EntityTransaction tx = em.getTransaction();

            try {

                tx.begin();

                // ============================
                // Duplicate Check
                // ============================
                Number count = (Number) em.createNativeQuery(
                        "SELECT COUNT(*) FROM user_roles "
                        + "WHERE UPPER(user_roles)=UPPER(?) "
                        + "AND status=1")
                        .setParameter(1, roleName)
                        .getSingleResult();

                if (count.intValue() > 0) {

                    tx.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "Role already exists."
                    );

                    return;
                }

                // ============================
                // Save
                // ============================
                em.createNativeQuery(
                        "INSERT INTO user_roles "
                        + "(user_roles, status) "
                        + "VALUES (?, ?)")
                        .setParameter(1, roleName)
                        .setParameter(2, 1)
                        .executeUpdate();

                tx.commit();

//                JOptionPane.showMessageDialog(
//                        this,
//                        "Role saved successfully."
//                );
                // ============================
                // Add to JTable
                // ============================
                DefaultTableModel model
                        = (DefaultTableModel) user_per_add_role_Table.getModel();

                model.addRow(new Object[]{
                    model.getRowCount() + 1,
                    roleName
                });

                user_per_add_role_text.setText("");
                user_per_add_role_text.requestFocus();

            } catch (Exception ex) {

                if (tx.isActive()) {
                    tx.rollback();
                }

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

            } finally {
                em.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_user_per_add_role_textActionPerformed

    private void user_per_add_role_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_user_per_add_role_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_user_per_add_role_textKeyTyped

    private void buttonGradientRound1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound1ActionPerformed

        int selectedRow = user_per_add_role_Table.getSelectedRow();

        if (selectedRow != -1) {

            int roleId = Integer.parseInt(
                    user_per_add_role_Table.getValueAt(selectedRow, 2).toString()
            );

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to delete this role?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                boolean success = softDeleteRole(roleId);

                if (success) {

                    DefaultTableModel model
                            = (DefaultTableModel) user_per_add_role_Table.getModel();

                    //  model.removeRow(selectedRow);
                    loadUserRoles(user_per_add_role_Table);

                    JOptionPane.showMessageDialog(null, "Role deleted successfully.");

                } else {
                    JOptionPane.showMessageDialog(null, "Delete failed.");
                }
            }
        }

    }//GEN-LAST:event_buttonGradientRound1ActionPerformed

    private void buttonGradientRound3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound3ActionPerformed

        try {

            if (selectedRoleId == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a user role.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            UserPermissionDAO dao = new UserPermissionDAO();

            dao.savePermissions(
                    selectedRoleId,
                    permissionBoxes,
                    username
            );
            PermissionService.reloadCurrentUserPermissions();
          //  dashboard.refreshCurrentPermissions();

            JOptionPane.showMessageDialog(
                    this,
                    "User permissions have been saved successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save permissions.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

//        try {
//
//            //    UserPermissionDAO dao = new UserPermissionDAO();
//            if (selectedRoleId == 0) {
//                JOptionPane.showMessageDialog(
//                        this,
//                        "Please select the user role to update.",
//                        "Warning",
//                        JOptionPane.ERROR_MESSAGE
//                );
//                return;
//            }
//
//            //  int roleId = Integer.parseInt(model.getValueAt(user_per_add_role_Table.getSelectedRow(), 2).toString());
//            int roleId = selectedRoleId;
//
//            UserPermissionDAO dao = new UserPermissionDAO();
//
//            for (JCheckBox box : permissionBoxes) {
//
//                dao.saveOrUpdatePermission(
//                        selectedRoleId,
//                        box.getName(),
//                        box.isSelected(),
//                        username
//                );
//
//            }
//
//            JOptionPane.showMessageDialog(
//                    this,
//                    "User permissions have been saved successfully.",
//                    "Success",
//                    JOptionPane.INFORMATION_MESSAGE
//            );
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }//GEN-LAST:event_buttonGradientRound3ActionPerformed

    private void EMP_REG_CAREER_HISTORYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EMP_REG_CAREER_HISTORYActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EMP_REG_CAREER_HISTORYActionPerformed

    private void EMP_REG_BASIC_SALARYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EMP_REG_BASIC_SALARYActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EMP_REG_BASIC_SALARYActionPerformed

    private void EMP_REG_BANK_ACCOUNTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EMP_REG_BANK_ACCOUNTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EMP_REG_BANK_ACCOUNTActionPerformed

    private void EMP_REG_DELETEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EMP_REG_DELETEActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EMP_REG_DELETEActionPerformed

    private void EMP_REG_SAVEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EMP_REG_SAVEActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EMP_REG_SAVEActionPerformed

    private void user_per_add_role_TableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_user_per_add_role_TableMouseClicked

//        int row = user_per_add_role_Table.getSelectedRow();
//
//        if (row >= 0) {
//
//            int roleId = Integer.parseInt(
//                    user_per_add_role_Table
//                            .getValueAt(row, 2)
//                            .toString()
//            );
//
//            loadRolePermissions(roleId);
//
//            selectedRoleId = roleId;
//
//        }

    }//GEN-LAST:event_user_per_add_role_TableMouseClicked

    private void DASH_STU_BATCH_TRANSFERActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DASH_STU_BATCH_TRANSFERActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DASH_STU_BATCH_TRANSFERActionPerformed

    private void DASH_STU_REPORTSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DASH_STU_REPORTSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DASH_STU_REPORTSActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed


    }//GEN-LAST:event_jButton1ActionPerformed

    private void ST_SIBBLINGSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ST_SIBBLINGSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ST_SIBBLINGSActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox DASH_ACC_ALL;
    private javax.swing.JCheckBox DASH_ACC_CHQ_HANDLING;
    private javax.swing.JCheckBox DASH_DON_ALL;
    private javax.swing.JCheckBox DASH_DON_HANDLING;
    private javax.swing.JCheckBox DASH_DON_REPORTS;
    private javax.swing.JCheckBox DASH_EMP_ALL;
    private javax.swing.JCheckBox DASH_EMP_NEW_REGISTRATION;
    private javax.swing.JCheckBox DASH_INV_ADD_INVENTORY;
    private javax.swing.JCheckBox DASH_INV_REPORTS;
    private javax.swing.JCheckBox DASH_SETT_ADDI_PAYMENTS;
    private javax.swing.JCheckBox DASH_SETT_ALL;
    private javax.swing.JCheckBox DASH_SETT_REGISTER_COURSE;
    private javax.swing.JCheckBox DASH_SETT_USER_PERMISSIONS;
    private javax.swing.JCheckBox DASH_STU_ALL;
    private javax.swing.JCheckBox DASH_STU_BATCH_TRANSFER;
    private javax.swing.JCheckBox DASH_STU_FEES_HANDLING;
    private javax.swing.JCheckBox DASH_STU_NEW_ADMISSION;
    private javax.swing.JCheckBox DASH_STU_REPORTS;
    private javax.swing.JCheckBox EMP_NEW_ALL;
    private javax.swing.JCheckBox EMP_REG_BANK_ACCOUNT;
    private javax.swing.JCheckBox EMP_REG_BASIC_SALARY;
    private javax.swing.JCheckBox EMP_REG_CAREER_HISTORY;
    private javax.swing.JCheckBox EMP_REG_DELETE;
    private javax.swing.JCheckBox EMP_REG_SAVE;
    private javax.swing.JCheckBox ST_COURSE_ENROLMENT;
    private javax.swing.JCheckBox ST_DELETE_RECORD;
    private javax.swing.JCheckBox ST_ELIMINATION;
    private javax.swing.JCheckBox ST_MISCELLANEOUS;
    private javax.swing.JCheckBox ST_NEW_ALL;
    private javax.swing.JCheckBox ST_SAVE_RECORD;
    private javax.swing.JCheckBox ST_SIBBLINGS;
    private javax.swing.JCheckBox ST_UPDATE_RECORD;
    private Classes.ButtonGradientRound buttonGradientRound1;
    private Classes.ButtonGradientRound buttonGradientRound3;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox18;
    private javax.swing.JCheckBox jCheckBox19;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> reg_misc_student_name_combo;
    private javax.swing.JTable user_per_add_role_Table;
    public static javax.swing.JTextField user_per_add_role_text;
    // End of variables declaration//GEN-END:variables
}
