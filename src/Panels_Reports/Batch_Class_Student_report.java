package Panels_Reports;

import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Pagination.EventPagination;
import Pagination.PaginationItemRenderStyle1;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Batch_Class_Student_report extends javax.swing.JPanel {

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String currentSelectedClass = "";
    String username;
    String role;

    public Batch_Class_Student_report(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        loadClassCombo();

        btc_st_table.setDefaultRenderer(Object.class, new TableGradientCell());
        btc_st_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");
        btc_st_table.setRowHeight(30);

        btc_st_table.getTableHeader().setPreferredSize(
                new Dimension(
                        btc_st_table.getTableHeader().getPreferredSize().width,
                        35
                )
        );

        JComboPopulates();

        pagination1.setPaginationItemRender(new PaginationItemRenderStyle1());
        pagination1.addEventPagination(new EventPagination() {
            @Override
            public void pageChanged(int page) {
                loadBatchStudents(btc_st_table, btc_st_batch_combo, currentSelectedClass, page);
            }
        });

    }

    private void JComboPopulates() {
        // Medicine brand combo
        btc_st_batch_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = btc_st_batch_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(btc_st_batch_combo, "course_id", "batch", "course", input);
            }

        });
        setupComboSelectionListener(btc_st_batch_combo, btc_st_course_text);

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

    public void loadBatchStudents(JTable table, JComboBox<String> btc_st_batch_combo,
            String selectedClassName, int page) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            // =====================================================
            // GET COURSE ID FROM COMBO
            // Format = BatchName [courseId]
            // =====================================================
            String selected = btc_st_batch_combo.getSelectedItem() != null
                    ? btc_st_batch_combo.getSelectedItem().toString()
                    : "";

            if (selected.isEmpty() || !selected.contains("[") || !selected.contains("]")) {
                btc_st_course_text.setText("");
                return;
            }

            int courseId = Integer.parseInt(
                    selected.substring(
                            selected.lastIndexOf("[") + 1,
                            selected.lastIndexOf("]")
                    ).trim()
            );

            // =====================================================
            // FETCH COURSE NAME
            // =====================================================
            Object courseNameObj = em.createNativeQuery(
                    "SELECT course_name "
                    + "FROM course "
                    + "WHERE course_id = ? "
                    + "AND status = 1"
            )
                    .setParameter(1, courseId)
                    .getSingleResult();

            String courseName = courseNameObj != null ? courseNameObj.toString() : "";
            btc_st_course_text.setText(courseName);

            int limit = 14;
            int offset = (page - 1) * limit;

            boolean filterClass = selectedClassName != null
                    && !selectedClassName.trim().isEmpty()
                    && !selectedClassName.equalsIgnoreCase("Select Class");

            // =====================================================
            // COUNT TOTAL STUDENTS
            // =====================================================
            String countSql
                    = "SELECT COUNT(*) "
                    + "FROM course_enrollment ce "
                    + "INNER JOIN student s ON ce.student_id = s.student_id "
                    + "WHERE ce.course_id = ? "
                    + "AND ce.course_status = 'ACTIVE' "
                    + "AND ce.status = 1 "
                    + "AND s.status = 1 ";

            if (filterClass) {
                countSql += "AND ce.class_name = ? ";
            }

            Query countQuery = em.createNativeQuery(countSql);
            countQuery.setParameter(1, courseId);

            if (filterClass) {
                countQuery.setParameter(2, selectedClassName);
            }

            Number totalCount = (Number) countQuery.getSingleResult();

            int count = totalCount.intValue();
            lbl_total_rows.setText("Total : " + count + " Records");
            int totalPage = (int) Math.ceil((double) count / limit);

            pagination1.setPagegination(page, totalPage);

            // =====================================================
            // FETCH STUDENTS WITH PAGINATION
            // =====================================================
            String dataSql
                    = "SELECT "
                    + "s.admission_no, "
                    + "s.full_name, "
                    + "ce.class_name, "
                    + "s.dob, "
                    + "s.nic, "
                    + "s.admission_date, "
                    + "s.contact_no "
                    + "FROM course_enrollment ce "
                    + "INNER JOIN student s ON ce.student_id = s.student_id "
                    + "WHERE ce.course_id = ? "
                    + "AND ce.course_status = 'ACTIVE' "
                    + "AND ce.status = 1 "
                    + "AND s.status = 1 ";

            if (filterClass) {
                dataSql += "AND ce.class_name = ? ";
            }

            dataSql += "ORDER BY s.admission_no ASC "
                    + "LIMIT ? OFFSET ?";

            Query dataQuery = em.createNativeQuery(dataSql);
            dataQuery.setParameter(1, courseId);

            int paramIndex = 2;

            if (filterClass) {
                dataQuery.setParameter(paramIndex++, selectedClassName);
            }

            dataQuery.setParameter(paramIndex++, limit);
            dataQuery.setParameter(paramIndex, offset);

            List<Object[]> list = dataQuery.getResultList();

            int rowNo = offset + 1;

            for (Object[] row : list) {

                String admissionNo = row[0] != null ? row[0].toString() : "";
                String studentName = row[1] != null ? row[1].toString() : "";
                String studentClass = row[2] != null ? row[2].toString() : "";
                String dob = row[3] != null ? row[3].toString() : "";
                String nic = row[4] != null ? row[4].toString() : "";
                String joinedDate = row[5] != null ? row[5].toString() : "";
                String contact = row[6] != null ? row[6].toString() : "";

                model.addRow(new Object[]{
                    rowNo++,
                    admissionNo,
                    studentName,
                    studentClass,
                    dob,
                    nic,
                    joinedDate,
                    contact
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    public void loadClassCombo() {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            if (btc_st_class_combo == null) {
                System.out.println("btc_st_class_combo is null");
                return;
            }

            btc_st_class_combo.removeAllItems();

            // Default item
            // btc_st_class_combo.addItem("Select Class");
            List<Object[]> list = em.createNativeQuery(
                    "SELECT class_name "
                    + "FROM student_class "
                    + "WHERE status = 1 "
                    + "ORDER BY class_name ASC"
            ).getResultList();

            System.out.println("Class Count = " + list.size());

            for (Object row : list) {

                String className = row != null ? row.toString() : "";

                if (!className.trim().isEmpty()) {
                    btc_st_class_combo.addItem(className);
                    System.out.println("Loaded Class = " + className);
                }
            }

            btc_st_class_combo.revalidate();
            btc_st_class_combo.repaint();

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
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        btc_st_table = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btc_st_class_combo = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        btc_st_batch_combo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        btc_st_course_text = new javax.swing.JTextField();
        pagination1 = new Pagination.Pagination();
        lbl_total_rows = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printblue32.png"))); // NOI18N
        jButton1.setToolTipText("Siblings");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Payment Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        btc_st_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Admission", "Student Name", "Class", "Date of Birth", "NIC", "Joined Date", "Contact"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        btc_st_table.setRowHeight(25);
        btc_st_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btc_st_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(btc_st_table);
        if (btc_st_table.getColumnModel().getColumnCount() > 0) {
            btc_st_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            btc_st_table.getColumnModel().getColumn(1).setPreferredWidth(120);
            btc_st_table.getColumnModel().getColumn(2).setPreferredWidth(200);
        }

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Course");

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Class");

        btc_st_class_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

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

        btc_st_batch_combo.setEditable(true);
        btc_st_batch_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Batch");

        jButton6.setBackground(new java.awt.Color(102, 102, 102));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search16.png"))); // NOI18N
        jButton6.setToolTipText("Course Enrolment");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(102, 102, 102));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/info24.png"))); // NOI18N
        jButton7.setToolTipText("Course Enrolment");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        btc_st_course_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        btc_st_course_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btc_st_course_textActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1330, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(btc_st_batch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(btc_st_course_text, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(btc_st_class_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(55, 539, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btc_st_class_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btc_st_course_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btc_st_batch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(41, 41, 41))
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addContainerGap())
        );

        pagination1.setOpaque(false);

        lbl_total_rows.setFont(new java.awt.Font("Roboto Medium", 3, 14)); // NOI18N
        lbl_total_rows.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_total_rows.setText("Total : 0 Records");

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/xlsx32.png"))); // NOI18N
        jButton2.setToolTipText("Siblings");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(pagination1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(440, 440, 440)
                        .addComponent(lbl_total_rows, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_total_rows, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pagination1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed


    }//GEN-LAST:event_jButton1ActionPerformed

    private void btc_st_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btc_st_tableMouseClicked

    }//GEN-LAST:event_btc_st_tableMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        currentSelectedClass = btc_st_class_combo.getSelectedItem() != null
                ? btc_st_class_combo.getSelectedItem().toString()
                : "";

        loadBatchStudents(
                btc_st_table,
                btc_st_batch_combo,
                currentSelectedClass,
                1
        );

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        currentSelectedClass = "";

        loadBatchStudents(
                btc_st_table,
                btc_st_batch_combo,
                currentSelectedClass,
                1
        );

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Batch_Class_Student_Dialog dialog = new Batch_Class_Student_Dialog(parentFrame);
        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btc_st_course_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btc_st_course_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btc_st_course_textActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox<String> btc_st_batch_combo;
    private javax.swing.JComboBox<String> btc_st_class_combo;
    public static javax.swing.JTextField btc_st_course_text;
    public static javax.swing.JTable btc_st_table;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_total_rows;
    private Pagination.Pagination pagination1;
    // End of variables declaration//GEN-END:variables

}
