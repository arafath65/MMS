package Panels_SubDialogs;

import Classes.ChequeNumberFormatter;
import Classes.DecimalOnlyFilter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NumberOnlyFilter;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Student_Management.StudentEliminates;
import JPA_DAO.Inventory.ItemDAO;
import JPA_DAO.Settings.CourseDAO;
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
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.PlainDocument;

public class Eliminate_Student extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Eliminate_Student.class.getName());
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    private String studentName;
    private int selectedStudentIds;
    String username;
    String role;

    public Eliminate_Student(Window parent, int selectedStudentIds, String studentName, String username, String role) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.selectedStudentIds = selectedStudentIds;
        this.studentName = studentName;
        this.username = username;
        this.role = role;

        initComponents();

        // Example variables from your Table or ComboBox
        el_st_date.setDate(new Date());
        styleDateChooser.applyDarkTheme(el_st_date);
        el_st_note_textpane.requestFocus();

        jLabel1.setText("<html><center>Are you sure you want to update <b>" + studentName + "</b><br>"
                + " <b> student's enrollment </b> status?</center></html>");

    }

    public void saveStudentElimination(int studentId,
            String eliminateType,
            String note,
            String user) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // =====================================================
            // CHECK EXISTING RECORD
            // =====================================================
            List<Object> existing = em.createNativeQuery(
                    "SELECT student_eliminates_id "
                    + "FROM student_eliminates "
                    + "WHERE student_id = ? "
                    + "LIMIT 1"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            if (!existing.isEmpty()) {

                // =====================================================
                // UPDATE EXISTING
                // =====================================================
                int eliminateId = ((Number) existing.get(0)).intValue();

                em.createNativeQuery(
                        "UPDATE student_eliminates "
                        + "SET eliminate_type = ?, "
                        + "note = ?, "
                        + "eliminate_date = ?, "
                        + "user = ?, "
                        + "status = 1 "
                        + "WHERE student_eliminates_id = ?"
                )
                        .setParameter(1, eliminateType)
                        .setParameter(2, note)
                        .setParameter(3, new java.util.Date())
                        .setParameter(4, user)
                        .setParameter(5, eliminateId)
                        .executeUpdate();

                JOptionPane.showMessageDialog(
                        null,
                        "Student elimination updated successfully."
                );

            } else {

                // =====================================================
                // INSERT NEW
                // =====================================================
                StudentEliminates eliminate = new StudentEliminates();
                eliminate.setStudentId(studentId);
                eliminate.setEliminateType(eliminateType);
                eliminate.setEliminateDate(new java.util.Date());
                eliminate.setNote(note);
                eliminate.setUser(user);
                eliminate.setStatus(1);

                em.persist(eliminate);

                JOptionPane.showMessageDialog(
                        null,
                        "Student elimination saved successfully."
                );
            }

            // =====================================================
            // UPDATE STUDENT CURRENT STATUS
            // =====================================================
            em.createNativeQuery(
                    "UPDATE student "
                    + "SET current_status = ? "
                    + "WHERE student_id = ?"
            )
                    .setParameter(1, eliminateType)
                    .setParameter(2, studentId)
                    .executeUpdate();

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    public void deleteStudentElimination(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // =====================================================
            // CHECK EXISTING RECORD
            // =====================================================
            List<Object> existing = em.createNativeQuery(
                    "SELECT student_eliminates_id "
                    + "FROM student_eliminates "
                    + "WHERE student_id = ? "
                    + "LIMIT 1"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            if (existing.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No Student found!");
                return;
            }

            em.createNativeQuery(
                    "UPDATE student_eliminates "
                    + "SET status = 0 "
                    + "WHERE student_id = ?"
            )
                    .setParameter(1, studentId)
                    .executeUpdate();

            // =====================================================
            // UPDATE STUDENT STATUS BACK TO ACTIVE
            // =====================================================
            em.createNativeQuery(
                    "UPDATE student "
                    + "SET current_status = 'ACTIVE' "
                    + "WHERE student_id = ?"
            )
                    .setParameter(1, studentId)
                    .executeUpdate();

            tx.commit();

            JOptionPane.showMessageDialog(null,
                    "Student elimination deleted successfully.");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Integer getStudentEliminateId(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            Object result = em.createNativeQuery(
                    "SELECT student_eliminates_id "
                    + "FROM student_eliminates "
                    + "WHERE student_id = ? "
                    + "AND status = 1 "
                    + "ORDER BY student_eliminates_id DESC "
                    + "LIMIT 1"
            )
                    .setParameter(1, studentId)
                    .getSingleResult();

            if (result != null) {
                return ((Number) result).intValue();
            }

        } catch (NoResultException e) {
            return null;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        el_st_date = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        el_st_status_combo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        el_st_note_textpane = new javax.swing.JEditorPane();
        jLabel26 = new javax.swing.JLabel();
        buttonGradient3 = new Classes.ButtonGradient();
        buttonGradient4 = new Classes.ButtonGradient();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LABEL");

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Medical Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel25.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel25.setText("Date");

        el_st_date.setForeground(new java.awt.Color(204, 204, 204));
        el_st_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Change status");

        el_st_status_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        el_st_status_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DISCONTINUED", "TERMINATED", "INACTIVE", "TRANFERRED", "OTHER" }));

        jScrollPane1.setViewportView(el_st_note_textpane);

        jLabel26.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel26.setText("Note/Reason");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25)
                                    .addComponent(el_st_date, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(el_st_status_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel26))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(el_st_status_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(el_st_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                .addContainerGap())
        );

        buttonGradient3.setText("CANCEL");
        buttonGradient3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient3ActionPerformed(evt);
            }
        });

        buttonGradient4.setText("UPDATE");
        buttonGradient4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
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

    private void buttonGradient3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient3ActionPerformed

        deleteStudentElimination(selectedStudentIds);
        logHelper.log(
                "STUDENT_TERMINATIONT",
                selectedStudentIds, // The ID of the student being updated
                "STUDENT STATUS DELETE",
                "Delete record ",
                0.0,
                String.format("record deleted of " + studentName),
                username
        );

        this.dispose();

    }//GEN-LAST:event_buttonGradient3ActionPerformed

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        String selectedStatus = el_st_status_combo.getSelectedItem().toString();
        String note = el_st_note_textpane.getText().trim();

        if (note.isEmpty()) {
            JOptionPane.showMessageDialog(null, "A note or reason is required...", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        logHelper.log(
                "STUDENT_TERMINATIONT",
                selectedStudentIds, // The ID of the student being updated
                "STUDENT STATUS CHANGE",
                "New Status: " + selectedStatus,
                0.0,
                String.format("Status updated to %s. Reason/Note: %s",
                        selectedStatus, note),
                username
        );

        saveStudentElimination(selectedStudentIds, selectedStatus, note, username);

        this.dispose();

    }//GEN-LAST:event_buttonGradient4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Eliminate_Student dialog
                    = new Eliminate_Student(frame, 0, "", "", "");

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradient buttonGradient3;
    private Classes.ButtonGradient buttonGradient4;
    private com.toedter.calendar.JDateChooser el_st_date;
    private javax.swing.JEditorPane el_st_note_textpane;
    private javax.swing.JComboBox<String> el_st_status_combo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
