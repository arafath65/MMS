package Panels_SubDialogs;

import Classes.TableGradientCell;
import Entities.Settings.Course;
import JPA_DAO.Settings.CourseDAO;
import JPA_DAO.Student_Management.CourseEnrollmentDAO;
import JPA_DAO.Student_Management.StudentDAO;
import Panels.Student_Management;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

public class Siblings_Register extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Siblings_Register.class.getName());
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

    private Student_Management parentForm;

    CourseDAO dao = new CourseDAO();

    int courseID = 0;
    private Integer studentId;

    public Siblings_Register(Window parent, int studentId, Student_Management parentForm) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.studentId = studentId;
        this.parentForm = parentForm;
        initComponents();

//    public Siblings_Register(java.awt.Frame parent, boolean modal, int studentId) {
//        super(parent, modal);
//        this.studentId = studentId;
//        this.parentForm = parentForm;
//        initComponents();
        stm_si_table.setDefaultRenderer(Object.class, new TableGradientCell());
        stm_si_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        //  jComboPopulates();
        enableSiblingSearchLive();

    }

    private void jComboPopulates() {
        stm_si_option_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                String input = stm_si_option_combo.getEditor().getItem().toString().trim();

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

                stm_si_option_combo.setModel(model);

                // Restore typed text
                stm_si_option_combo.getEditor().setItem(typedText);

                stm_si_option_combo.showPopup();
            }
        });

        setupComboSelectionListeners1(stm_si_option_combo, stm_si_option_combo, "ADMISSION");
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

    private void enableSiblingSearchLive() {

        Component editor = stm_si_search_combo.getEditor().getEditorComponent();

        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                String searchText = ((JTextComponent) editor).getText().trim();

                if (searchText.length() >= 2) {   // prevent DB call for 1 letter
                    loadSiblingTable();
                } else {
                    DefaultTableModel model = (DefaultTableModel) stm_si_table.getModel();
                    model.setRowCount(0);
                }
            }
        });
    }

    private void loadSiblingTable() {

        String searchText = ((JTextComponent) stm_si_search_combo
                .getEditor().getEditorComponent()).getText().trim();

        String searchType = stm_si_option_combo.getSelectedItem().toString();

        if (searchText.isEmpty()) {
            return;   // no popup while typing
        }

        StudentDAO dao = new StudentDAO();
        List<Object[]> list = dao.searchSiblings(searchText, searchType);

        DefaultTableModel model = (DefaultTableModel) stm_si_table.getModel();
        model.setRowCount(0);

        int seq = 1;

        for (Object[] row : list) {
            model.addRow(new Object[]{
                seq++,
                row[0],
                row[1],
                row[2],
                row[3]
            });
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        stm_si_option_combo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        stm_si_table = new javax.swing.JTable();
        stm_si_search_combo = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Family Sibling Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(232, 232, 232));
        jLabel1.setText("Search By");

        stm_si_option_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_si_option_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mother's NIC", "Mother's Name", "Mother's Contact", "Father's NIC", "Father's Name", "Father's Contact", "Guardian's NIC", "Guardian's Name", "Guardian's Contact" }));

        stm_si_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Admission Number", "Student Name ", "Admission Date", "Course Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stm_si_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stm_si_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(stm_si_table);
        if (stm_si_table.getColumnModel().getColumnCount() > 0) {
            stm_si_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            stm_si_table.getColumnModel().getColumn(1).setPreferredWidth(120);
            stm_si_table.getColumnModel().getColumn(2).setPreferredWidth(250);
            stm_si_table.getColumnModel().getColumn(3).setPreferredWidth(120);
            stm_si_table.getColumnModel().getColumn(4).setPreferredWidth(100);
        }

        stm_si_search_combo.setEditable(true);
        stm_si_search_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(stm_si_option_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stm_si_search_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 246, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stm_si_option_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_si_search_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void stm_si_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stm_si_tableMouseClicked
        int row = stm_si_table.getSelectedRow();
        int column = stm_si_table.getSelectedColumn();

        if (row != -1 && column == 4) {

            String admission_no = stm_si_table.getModel()
                    .getValueAt(row, 1).toString();

            parentForm.loadParentsDao(admission_no);

            this.dispose();
        }
    }//GEN-LAST:event_stm_si_tableMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Siblings_Register dialog
                    = new Siblings_Register(frame, 0, null);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> stm_si_option_combo;
    private javax.swing.JComboBox<String> stm_si_search_combo;
    private javax.swing.JTable stm_si_table;
    // End of variables declaration//GEN-END:variables
private void showEnrollmentActionDialog(int enrollmentId, int rowIndex) {

        JDialog dialog = new JDialog(this);
        dialog.setUndecorated(true);
        dialog.setSize(460, 340);
        dialog.setLayout(null);
        dialog.setBackground(new Color(0, 0, 0, 0)); // transparent
        dialog.setLocationRelativeTo(this);

        // ===== ROUNDED PANEL WITH SHADOW =====
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(10, 10, getWidth() - 10, getHeight() - 10, 30, 30);

                // Main background
                g2.setColor(Color.decode("#2B2B2B"));
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setBounds(0, 0, 460, 340);

        JLabel title = new JLabel("Course Enrollment Action");
        title.setBounds(30, 25, 350, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        panel.add(title);

        // ===== CHECKBOXES (Single Select) =====
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

        // ===== BUTTONS WITH HOVER =====
        JButton deleteBtn = createAnimatedGradientButton(
                "DELETE",
                new Color(170, 0, 0),
                new Color(255, 70, 70)
        );
        deleteBtn.setBounds(30, 250, 120, 42);

        JButton changeBtn = createAnimatedGradientButton(
                "CHANGE STATUS",
                new Color(0, 102, 204),
                new Color(0, 180, 255)
        );
        changeBtn.setBounds(160, 250, 170, 42);

        JButton cancelBtn = createAnimatedGradientButton(
                "CANCEL",
                Color.decode("#F09819"),
                Color.decode("#FF512F")
        );
        cancelBtn.setBounds(340, 250, 100, 42);

        panel.add(deleteBtn);
        panel.add(changeBtn);
        panel.add(cancelBtn);

        dialog.add(panel);

        // ===== BUTTON ACTIONS =====
        deleteBtn.addActionListener(e -> {
            CourseEnrollmentDAO dao = new CourseEnrollmentDAO();
            dao.softDelete(enrollmentId);
            ((DefaultTableModel) stm_si_table.getModel()).removeRow(rowIndex);
            dialog.dispose();
        });

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
                JOptionPane.showMessageDialog(dialog, "Select one status");
                return;
            }

            CourseEnrollmentDAO dao = new CourseEnrollmentDAO();
            dao.updateStatus(enrollmentId, newStatus);
            stm_si_table.setValueAt(newStatus, rowIndex, 8);
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        // ===== SLIDE IN ANIMATION =====
        Point end = dialog.getLocation();
        dialog.setLocation(end.x, end.y + 80);

        Timer timer = new Timer(5, null);
        timer.addActionListener(ev -> {
            Point p = dialog.getLocation();
            if (p.y <= end.y) {
                dialog.setLocation(end);
                timer.stop();
            } else {
                dialog.setLocation(p.x, p.y - 8);
            }
        });
        timer.start();

        dialog.setVisible(true);
    }

    private JButton createAnimatedGradientButton(String text, Color c1, Color c2) {

        JButton btn = new JButton(text) {

            private float scale = 1f;

            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int width = (int) (getWidth() * scale);
                int height = (int) (getHeight() * scale);

                int x = (getWidth() - width) / 2;
                int y = (getHeight() - height) / 2;

                GradientPaint gp = new GradientPaint(
                        0, 0, c1,
                        getWidth(), getHeight(), c2
                );

                g2.setPaint(gp);
                g2.fillRoundRect(x, y, width, height, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Hover Animation
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setSize(btn.getWidth() + 2, btn.getHeight() + 2);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setSize(btn.getWidth() - 2, btn.getHeight() - 2);
            }
        });

        return btn;
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
