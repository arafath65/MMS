package Panels;

import Additional.PermissionManager;
import Additional.UserSession;
import Dashboard.Dashboard;
import JPA_DAO.Dashboard.LoginDAO;
import JPA_DAO.Settings.UserPermissionDAO;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import java.awt.Color;
import java.awt.Font;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Login extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Login.class.getName());

    public Login() {
        initComponents();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelRound2 = new Classes.PanelRound();
        Main_Lable = new javax.swing.JLabel();
        login_username = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        login_password = new javax.swing.JPasswordField();
        buttonGradient5 = new Classes.ButtonGradient();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelRound2.setBackground(new java.awt.Color(247, 178, 50));
        panelRound2.setRoundBottomLeft(10);
        panelRound2.setRoundBottomRight(10);
        panelRound2.setRoundTopLeft(10);
        panelRound2.setRoundTopRight(10);

        Main_Lable.setFont(new java.awt.Font("Roboto Black", 3, 14)); // NOI18N
        Main_Lable.setForeground(new java.awt.Color(255, 255, 255));
        Main_Lable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Main_Lable.setText("ACCOUNT ACCESS");

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Main_Lable, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Main_Lable, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addContainerGap())
        );

        login_username.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        login_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                login_usernameActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 14)); // NOI18N
        jLabel2.setText("Password");

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 14)); // NOI18N
        jLabel3.setText("Username");

        login_password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                login_passwordActionPerformed(evt);
            }
        });

        buttonGradient5.setText("LOG - IN");
        buttonGradient5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient5ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Onyx", 1, 36)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("LOGIN");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(buttonGradient5, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .addComponent(login_password, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .addComponent(login_username)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(83, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(login_username, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(login_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {login_password, login_username});

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
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void login_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_login_usernameActionPerformed
        login_password.requestFocus();
    }//GEN-LAST:event_login_usernameActionPerformed

    private void buttonGradient5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient5ActionPerformed

        try {

            String username = login_username.getText().trim();
            String password = String.valueOf(login_password.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter credentials to login.");
                return;
            }

            LoginDAO dao = new LoginDAO();

            Object[] user = dao.login(username, password);

            if (user != null) {

                int loginId = Integer.parseInt(user[0].toString());
                int employeeId = Integer.parseInt(user[1].toString());
                int roleId = Integer.parseInt(user[2].toString());

                String uname = user[3].toString();
                String roleName = user[4].toString();
                String employeeName = user[5].toString();

                UserPermissionDAO permissionDAO = new UserPermissionDAO();

                Set<String> permissions
                        = permissionDAO.getPermissionsByRole(roleId);

                UserSession.initialize(
                        loginId,
                        employeeId,
                        roleId,
                        uname,
                        roleName,
                        employeeName,
                        permissions
                );

                Dashboard dashboard = new Dashboard();
                dashboard.setVisible(true);
                this.dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Username or Password",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                login_password.setText("");
                login_password.requestFocus();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_buttonGradient5ActionPerformed

    private void login_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_login_passwordActionPerformed
        buttonGradient5.doClick();
    }//GEN-LAST:event_login_passwordActionPerformed

    /**
     * @param args the command line arguments
     */
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
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel Main_Lable;
    private Classes.ButtonGradient buttonGradient5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField login_password;
    private javax.swing.JTextField login_username;
    private Classes.PanelRound panelRound2;
    // End of variables declaration//GEN-END:variables
}
