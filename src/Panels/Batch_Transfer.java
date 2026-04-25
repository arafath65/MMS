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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Batch_Transfer extends javax.swing.JPanel {

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    String username;
    String role;

    public Batch_Transfer(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        bat_pay_date.setDate(new Date());
        styleDateChooser.applyDarkTheme(bat_pay_date);

        bat_pay_table.setDefaultRenderer(Object.class, new TableGradientCell());
        bat_pay_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        loadActiveMonthlyBatchCourses(bat_pay_batch_course_combo);

    }

    public void loadActiveMonthlyBatchCourses(JComboBox<String> comboBox) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            comboBox.removeAllItems();

            // =====================================================
            // FETCH DISTINCT ACTIVE COURSE IDS
            // course_enrollment.course_status = ACTIVE
            // only MONTHLY courses
            // =====================================================
            List<Object[]> list = em.createNativeQuery(
                    "SELECT DISTINCT c.course_id, c.batch, c.course_name "
                    + "FROM course_enrollment ce "
                    + "INNER JOIN course c ON ce.course_id = c.course_id "
                    + "WHERE ce.course_status = 'ACTIVE' "
                    + "AND c.payment_mode = 'MONTHLY' "
                    + "AND c.status = 1 AND ce.status = 1 "
                    + "ORDER BY c.batch ASC"
            ).getResultList();

            // =====================================================
            // LOAD COMBO
            // Format:
            // Batch + Course [course_id]
            // Example:
            // BATCH-01 - Spoken English [15]
            // =====================================================
            for (Object[] row : list) {

                int courseId = ((Number) row[0]).intValue();
                String batch = row[1] != null ? row[1].toString() : "";
                String courseName = row[2] != null ? row[2].toString() : "";

                String display = courseName + " [" + batch + "] - " + courseId;

                comboBox.addItem(display);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        bat_pay_date = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        bat_pay_table = new javax.swing.JTable();
        bat_pay_batch_course_combo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        buttonGradient4 = new Classes.ButtonGradient();
        jButton5 = new javax.swing.JButton();
        bat_pay_batch_course_combo1 = new javax.swing.JComboBox<>();
        bat_pay_total1 = new javax.swing.JTextField();
        bat_pay_batch_course_combo2 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        bat_pay_total = new javax.swing.JTextField();

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Batch Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Payment Date");

        bat_pay_date.setForeground(new java.awt.Color(204, 204, 204));
        bat_pay_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        bat_pay_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Admission", "Student Name", "Class", "Last Paid Month", "Paying Amount", "ids"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bat_pay_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bat_pay_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(bat_pay_table);
        if (bat_pay_table.getColumnModel().getColumnCount() > 0) {
            bat_pay_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            bat_pay_table.getColumnModel().getColumn(1).setPreferredWidth(120);
            bat_pay_table.getColumnModel().getColumn(2).setPreferredWidth(250);
            bat_pay_table.getColumnModel().getColumn(3).setPreferredWidth(80);
            bat_pay_table.getColumnModel().getColumn(4).setPreferredWidth(120);
            bat_pay_table.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        bat_pay_batch_course_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Batch / Course");

        jLabel12.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel12.setText("Transfer Batch");

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Course/Monthly Fee");

        buttonGradient4.setText("TRANSFER");
        buttonGradient4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(102, 102, 102));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search16.png"))); // NOI18N
        jButton5.setToolTipText("Course Enrolment");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        bat_pay_batch_course_combo1.setEditable(true);
        bat_pay_batch_course_combo1.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        bat_pay_total1.setEditable(false);
        bat_pay_total1.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        bat_pay_total1.setForeground(new java.awt.Color(251, 63, 63));
        bat_pay_total1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bat_pay_total1ActionPerformed(evt);
            }
        });
        bat_pay_total1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bat_pay_total1KeyTyped(evt);
            }
        });

        bat_pay_batch_course_combo2.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("Class");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_pay_date, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_pay_batch_course_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(bat_pay_batch_course_combo2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_pay_batch_course_combo1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(bat_pay_total1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel12)
                            .addComponent(jLabel3))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_pay_total1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bat_pay_batch_course_combo1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bat_pay_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bat_pay_batch_course_combo2)
                            .addComponent(bat_pay_batch_course_combo))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel10.setText("TotalStudents ");

        bat_pay_total.setEditable(false);
        bat_pay_total.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        bat_pay_total.setForeground(new java.awt.Color(251, 63, 63));
        bat_pay_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bat_pay_totalActionPerformed(evt);
            }
        });
        bat_pay_total.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bat_pay_totalKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bat_pay_total, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bat_pay_total, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
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

    private void bat_pay_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bat_pay_tableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_pay_tableMouseClicked

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

    }//GEN-LAST:event_buttonGradient4ActionPerformed

    private void bat_pay_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bat_pay_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_pay_totalActionPerformed

    private void bat_pay_totalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bat_pay_totalKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_pay_totalKeyTyped

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

       
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void bat_pay_total1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bat_pay_total1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_pay_total1ActionPerformed

    private void bat_pay_total1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bat_pay_total1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_bat_pay_total1KeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox<String> bat_pay_batch_course_combo;
    public static javax.swing.JComboBox<String> bat_pay_batch_course_combo1;
    public static javax.swing.JComboBox<String> bat_pay_batch_course_combo2;
    public static com.toedter.calendar.JDateChooser bat_pay_date;
    private javax.swing.JTable bat_pay_table;
    private javax.swing.JTextField bat_pay_total;
    private javax.swing.JTextField bat_pay_total1;
    private Classes.ButtonGradient buttonGradient4;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
