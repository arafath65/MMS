package Dashboard;

import Additional.UserSession;
import Dashboard_Design.AppNavigator;
import Dashboard_Design.MyDrawerBuilder;
import Panels.Additional_Payments;
import Panels.Batch_Transfer;
import Panels.Cheque_Handling;
import Panels.Dashboard_Panel;
import Panels.Fees_Management;
import Panels.Inventory;
import Panels.OneTime_Donation;
import Panels.Register_Course;
import Panels.Register_Employee;
import Panels.Student_Management;
import Panels.User_Permissions;
import Panels_Reports.Batch_Class_Student_Contact;
import Panels_Reports.Batch_Class_Student_report;
import Panels_Reports.Entire_Students_Report;
import Panels_Reports.Low_Stock;
import Panels_Reports.Stock_Movement;
import Panels_Reports.Student_Wise_Due;
import Panels_Reports.Suppliers_List;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import raven.drawer.Drawer;
import raven.popup.GlassPanePopup;

public class Dashboard extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());

    CardLayout cardLayout;
    Dashboard_Panel dashboard_Panel;
    Student_Management studentManagement;
    Register_Course register_Courses;
    Fees_Management fees_Management;
    Inventory inventory;
    Cheque_Handling cheque_Handling;
    Additional_Payments additional_Payments;
    Batch_Transfer batch_Transfer;
    OneTime_Donation oneTime_Donation;
    User_Permissions user_Permissions;
    Register_Employee register_Employee;
    // Course_enrolment course_enrolment;

    // REPORTS
    Batch_Class_Student_report batch_Class_Student_report;
    Batch_Class_Student_Contact batch_Class_Student_Contact;
    Entire_Students_Report entire_Students_Report;
    Student_Wise_Due student_Wise_Due;
    Suppliers_List supplier_List;
    Stock_Movement stock_Movement;
    Low_Stock low_Stock;

    String username;
    String role;

    public Dashboard() {

        GlassPanePopup.install(this);
        MyDrawerBuilder myDrawerBuilder = new MyDrawerBuilder(this);
        Drawer.getInstance().setDrawerBuilder(myDrawerBuilder);
        initComponents();

        main_username.setText("Username: " + UserSession.getUsername() + "  Role: " + UserSession.getRoleName());

        // main_panels MUST already exist from NetBeans designer
        cardLayout = new CardLayout();
        main_panels.setLayout(cardLayout);

        AppNavigator.init(this);

        dashboard_Panel = new Dashboard_Panel();
        studentManagement = new Student_Management(username, role);
        register_Courses = new Register_Course(username, role);
        fees_Management = new Fees_Management(username, role);
        AppNavigator.setFeesPanel(fees_Management);
        inventory = new Inventory(username, role);
        cheque_Handling = new Cheque_Handling(username, role);
        additional_Payments = new Additional_Payments(username, role);
        batch_Transfer = new Batch_Transfer(username, role);
        oneTime_Donation = new OneTime_Donation(username, role);
        user_Permissions = new User_Permissions(username, role);
        register_Employee = new Register_Employee(username, role);
        // course_enrolment = new Course_enrolment();

        batch_Class_Student_report = new Batch_Class_Student_report(username, role);
        batch_Class_Student_Contact = new Batch_Class_Student_Contact(username, role);
        entire_Students_Report = new Entire_Students_Report(username, role);
        student_Wise_Due = new Student_Wise_Due(username, role);
        supplier_List = new Suppliers_List(username, role);
        stock_Movement = new Stock_Movement(username, role);
        low_Stock = new Low_Stock(username, role);

        // ADD PANEL TO CARDLAYOUT (ONLY ONCE)
        main_panels.add(dashboard_Panel, "DASHBOARD_PANEL");
        main_panels.add(studentManagement, "STUDENT_ADMISSION");
        main_panels.add(register_Courses, "REGISTER_COURSE");
        main_panels.add(fees_Management, "FEES_MANAGEMENT");
        main_panels.add(inventory, "INVENTORY");
        main_panels.add(cheque_Handling, "CHEQUE_HANDLING");
        main_panels.add(additional_Payments, "ADDITIONAL_PAYMENTS");
        main_panels.add(batch_Transfer, "BATCH_TRANSFER");
        main_panels.add(oneTime_Donation, "ONE-TIME_DONATION");
        main_panels.add(user_Permissions, "USER-PERMISSION");
        main_panels.add(register_Employee, "REGISTER_EMPLOYEE");
        // main_panels.add(course_enrolment, "COURSE_ENROLMENT");

        main_panels.add(batch_Class_Student_report, "BATCH/CLASS_STUDENT_REPORT");
        main_panels.add(batch_Class_Student_Contact, "BATCH/CLASS_STUDENT_CONTACT");
        main_panels.add(entire_Students_Report, "ENTIRE_STUDENTS_REPORT");
        main_panels.add(student_Wise_Due, "STUDENT_WISE_DUE");
        main_panels.add(supplier_List, "SUPPLIER_LIST");
        main_panels.add(stock_Movement, "STOCK_MOVEMENT");
        main_panels.add(low_Stock, "LOW_STOCK");

        showPanel("DASHBOARD_PANEL"); // default

        //   Drawer.getInstance().showDrawer();
        // new HibernateConfig();
    }

    public void showPanel(String name) {
        if (name == null || name.isEmpty()) {
            System.err.println("Invalid panel name");
            return;
        }

        refreshCurrentPermissions();

        cardLayout.show(main_panels, name);
        main_panels.revalidate();
        main_panels.repaint();
        Drawer.getInstance().closeDrawer();
    }

    public void refreshCurrentPermissions() {

        studentManagement.refreshAllPermissions();

//        fees_Management.refreshAllPermissions();
//
//        inventory.refreshAllPermissions();
//
//        cheque_Handling.refreshAllPermissions();
//
//        register_Employee.refreshAllPermissions();

        // Add the rest later
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sub_main_panel = new javax.swing.JPanel();
        main_panels = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        main_time = new javax.swing.JLabel();
        main_username = new javax.swing.JLabel();
        panelRound2 = new Classes.PanelRound();
        Main_Lable = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sub_main_panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout main_panelsLayout = new javax.swing.GroupLayout(main_panels);
        main_panels.setLayout(main_panelsLayout);
        main_panelsLayout.setHorizontalGroup(
            main_panelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1366, Short.MAX_VALUE)
        );
        main_panelsLayout.setVerticalGroup(
            main_panelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 657, Short.MAX_VALUE)
        );

        sub_main_panel.add(main_panels, java.awt.BorderLayout.CENTER);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/drawer.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        main_time.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        main_time.setForeground(new java.awt.Color(232, 232, 232));
        main_time.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        main_time.setText("MMM Arafath");

        main_username.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        main_username.setForeground(new java.awt.Color(232, 232, 232));
        main_username.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        main_username.setText("2025-01-18 05:50 PM");

        panelRound2.setBackground(new java.awt.Color(247, 178, 50));
        panelRound2.setRoundBottomLeft(10);
        panelRound2.setRoundBottomRight(10);
        panelRound2.setRoundTopLeft(10);
        panelRound2.setRoundTopRight(10);

        Main_Lable.setFont(new java.awt.Font("Roboto Black", 3, 14)); // NOI18N
        Main_Lable.setForeground(new java.awt.Color(255, 255, 255));
        Main_Lable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Main_Lable.setText("ADMISSION");

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Main_Lable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sub_main_panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(main_username, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(main_time, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(main_username, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(main_time, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelRound2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sub_main_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Drawer.getInstance().showDrawer();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {

        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");

        UIManager.put("defaultFont",
                new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));

        // 🔴 THIS FIXES PINK EVERYWHERE (MOST IMPORTANT)
        UIManager.put("accentColor", new Color(15, 74, 48));

        UIManager.put("Drawer.background", new Color(30, 30, 30));

//        UIManager.put("Component.focusColor", new Color(15, 74, 48));
//        UIManager.put("Component.focusWidth", 1);
//        UIManager.put("TextComponent.background", new Color(45, 45, 45));
        // 🔹 TEXT COMPONENTS (THIS IS WHAT YOU MISSED)
        UIManager.put("TextComponent.background", new Color(247, 200, 96));
        UIManager.put("TextComponent.foreground", Color.WHITE);
        UIManager.put("TextComponent.caretForeground", Color.WHITE);

        UIManager.put("TextComponent.selectionBackground", new Color(247, 200, 96));
        UIManager.put("TextComponent.selectionForeground", Color.WHITE);

        UIManager.put("TextComponent.arc", 10);

        // 🔹 TEXT FIELD SPECIFIC
        UIManager.put("TextField.background", new Color(45, 45, 45));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextField.borderColor", Color.WHITE);

        // 🔹 TEXT AREA
        UIManager.put("TextArea.background", new Color(72, 72, 72));
        UIManager.put("TextArea.foreground", Color.WHITE);

        // 🔹 EDITOR PANE
        UIManager.put("EditorPane.background", new Color(72, 72, 72));
        UIManager.put("EditorPane.foreground", Color.WHITE);

        // 🔹 TEXT SELECTION
        UIManager.put("TextComponent.selectionBackground", new Color(247, 200, 96));
        UIManager.put("TextComponent.selectionForeground", Color.WHITE);

        // 🔹 COMBOBOX ARROW BUTTON
        UIManager.put("ComboBox.buttonBackground", new Color(45, 45, 45));
        UIManager.put("ComboBox.buttonHoverBackground", new Color(60, 60, 60));
        UIManager.put("ComboBox.buttonPressedBackground", new Color(42, 122, 109));
        UIManager.put("ComboBox.buttonArrowColor", Color.WHITE);

        // 🔹 COMBOBOX LIST
        UIManager.put("ComboBox.selectionBackground", new Color(247, 200, 96));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);

        UIManager.put("Component.focusWidth", 0);
        UIManager.put("Component.borderColor", new Color(102, 102, 102));

        FlatLaf.registerCustomDefaultsSource("raven.table");

        FlatMacDarkLaf.setup();
        //  FlatMacLightLaf.setup();

        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel Main_Lable;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel main_panels;
    public static javax.swing.JLabel main_time;
    private javax.swing.JLabel main_username;
    private Classes.PanelRound panelRound2;
    private javax.swing.JPanel sub_main_panel;
    // End of variables declaration//GEN-END:variables
}
