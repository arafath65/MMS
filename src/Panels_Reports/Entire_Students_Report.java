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

public class Entire_Students_Report extends javax.swing.JPanel {

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String currentSelectedClass = "";
    String username;
    String role;

    public Entire_Students_Report(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        ent_st_table.setDefaultRenderer(Object.class, new TableGradientCell());
        ent_st_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");
        ent_st_table.setRowHeight(30);

        ent_st_table.getTableHeader().setPreferredSize(
                new Dimension(
                        ent_st_table.getTableHeader().getPreferredSize().width,
                        35
                )
        );
        
        loadStudentStatusCombo();

        pagination1.setPaginationItemRender(new PaginationItemRenderStyle1());
        pagination1.addEventPagination(new EventPagination() {
            @Override
            public void pageChanged(int page) {
                loadAllStudents(
                        ent_st_table,
                        ent_st_status_combo.getSelectedItem().toString(),
                        ent_st_gender_combo.getSelectedItem().toString(),
                        page
                );
            }
        });

    }
    
    public void loadStudentStatusCombo() {

    EntityManager em = HibernateConfig.getEntityManager();

    try {
        // Clear old items
        ent_st_status_combo.removeAllItems();

        // First default value
        ent_st_status_combo.addItem("Select Status");

        // Optional ALL option
        ent_st_status_combo.addItem("ALL");

        // ============================================
        // FETCH DISTINCT CURRENT STATUS
        // ============================================
        List<Object> list = em.createNativeQuery(
                "SELECT DISTINCT current_status "
                + "FROM student "
                + "WHERE status = 1 "
                + "AND current_status IS NOT NULL "
                + "AND current_status <> '' "
                + "ORDER BY current_status ASC"
        ).getResultList();

        for (Object obj : list) {

            String status = obj != null ? obj.toString() : "";

            // Avoid duplicate ALL if already exists
            if (!status.equalsIgnoreCase("ALL")) {
                ent_st_status_combo.addItem(status);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();

    } finally {
        em.close();
    }
}

    public void loadAllStudents(JTable ent_st_table,
            String selectedStatus,
            String selectedGender,
            int page) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) ent_st_table.getModel();
            model.setRowCount(0);

            int limit = 16;
            int offset = (page - 1) * limit;

            // =====================================================
            // STATUS FILTER CHECK
            // =====================================================
            boolean filterAllStatus = selectedStatus != null
                    && selectedStatus.equalsIgnoreCase("ALL");

            boolean filterSelectStatus = selectedStatus != null
                    && selectedStatus.equalsIgnoreCase("Select Status");

            // =====================================================
            // GENDER FILTER CHECK
            // =====================================================
            boolean filterMale = selectedGender != null
                    && selectedGender.equalsIgnoreCase("Male");

            boolean filterFemale = selectedGender != null
                    && selectedGender.equalsIgnoreCase("Female");

            boolean filterSelectGender = selectedGender != null
                    && selectedGender.equalsIgnoreCase("Select Gender");

            // =====================================================
            // IF STATUS = Select Status → NO RECORDS
            // =====================================================
            if (filterSelectStatus) {
                pagination1.setPagegination(1, 1);
                return;
            }

            // =====================================================
            // COUNT QUERY
            // =====================================================
            String countSql = "SELECT COUNT(*) "
                    + "FROM student s "
                    + "WHERE s.status = 1 ";

            // STATUS FILTER
            if (!filterAllStatus) {
                countSql += "AND s.current_status = ? ";
            }

            // GENDER FILTER
            if (filterMale) {
                countSql += "AND s.Gender = 'Male' ";
            } else if (filterFemale) {
                countSql += "AND s.Gender = 'Female' ";
            }
            // Select Gender = no gender filter

            Query countQuery = em.createNativeQuery(countSql);

            if (!filterAllStatus) {
                countQuery.setParameter(1, selectedStatus);
            }

            Number totalCount = (Number) countQuery.getSingleResult();

            int count = totalCount.intValue();
            lbl_total_rows.setText("Total : " + count + " Records");
            int totalPage = (int) Math.ceil((double) count / limit);

            if (totalPage <= 0) {
                totalPage = 1;
            }

            pagination1.setPagegination(page, totalPage);

            // =====================================================
            // MAIN DATA QUERY
            // =====================================================
            String sql = "SELECT "
                    + "s.student_id, " // 0
                    + "s.current_status, " // 1
                    + "s.admission_no, " // 2
                    + "s.full_name, " // 3
                    + "s.Gender, " // 4
                    + "s.admission_date, " // 5
                    + "s.contact_no, " // 6
                    + "sp.father_contact " // 7
                    + "FROM student s "
                    + "LEFT JOIN student_parents sp "
                    + "ON s.student_parents_id = sp.student_parents_id "
                    + "AND sp.status = 1 "
                    + "WHERE s.status = 1 ";

            // STATUS FILTER
            if (!filterAllStatus) {
                sql += "AND s.current_status = ? ";
            }

            // GENDER FILTER
            if (filterMale) {
                sql += "AND s.Gender = 'Male' ";
            } else if (filterFemale) {
                sql += "AND s.Gender = 'Female' ";
            }

            sql += "ORDER BY s.admission_no ASC "
                    + "LIMIT ? OFFSET ?";

            Query dataQuery = em.createNativeQuery(sql);

            int paramIndex = 1;

            if (!filterAllStatus) {
                dataQuery.setParameter(paramIndex++, selectedStatus);
            }

            dataQuery.setParameter(paramIndex++, limit);
            dataQuery.setParameter(paramIndex, offset);

            List<Object[]> list = dataQuery.getResultList();

            int rowNo = offset + 1;

            for (Object[] row : list) {

                int studentId = row[0] != null
                        ? ((Number) row[0]).intValue()
                        : 0;

                String currentStatus = row[1] != null ? row[1].toString() : "";
                String admissionNo = row[2] != null ? row[2].toString() : "";
                String studentName = row[3] != null ? row[3].toString() : "";
                String gender = row[4] != null ? row[4].toString() : "";
                String joinedDate = row[5] != null ? row[5].toString() : "";
                String studentContact = row[6] != null ? row[6].toString() : "";
                String fatherContact = row[7] != null ? row[7].toString() : "";

                // =====================================================
                // FETCH LATEST COURSE
                // =====================================================
                String batchCourse = "";

                List<Object[]> courseList = em.createNativeQuery(
                        "SELECT c.batch, c.course_name "
                        + "FROM course_enrollment ce "
                        + "INNER JOIN course c ON ce.course_id = c.course_id "
                        + "WHERE ce.student_id = ? "
                        + "AND ce.status = 1 "
                        + "AND c.status = 1 "
                        + "ORDER BY ce.enrollment_id DESC "
                        + "LIMIT 1"
                )
                        .setParameter(1, studentId)
                        .getResultList();

                if (!courseList.isEmpty()) {
                    Object[] courseRow = courseList.get(0);

                    String batch = courseRow[0] != null
                            ? courseRow[0].toString()
                            : "";

                    String courseName = courseRow[1] != null
                            ? courseRow[1].toString()
                            : "";

                    batchCourse = batch + " - " + courseName;
                }

                model.addRow(new Object[]{
                    rowNo++, // #
                    currentStatus, // Current Status
                    admissionNo, // Admission No
                    studentName, // Student Name
                    gender, // Gender
                    joinedDate, // Joined Date
                    batchCourse, // Latest Batch + Course
                    studentContact, // Student Contact
                    fatherContact // Father Contact
                });
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
        }
    }

//    public void loadAllStudents(JTable ent_st_table,
//            String selectedStatus,
//            String selectedGender,
//            int page) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            DefaultTableModel model = (DefaultTableModel) ent_st_table.getModel();
//            model.setRowCount(0);
//
//            int limit = 16;
//            int offset = (page - 1) * limit;
//
//            // =====================================================
//            // STATUS FILTER CHECK
//            // =====================================================
//            boolean filterActive = selectedStatus != null
//                    && selectedStatus.equalsIgnoreCase("Active");
//
//            boolean filterInactive = selectedStatus != null
//                    && selectedStatus.equalsIgnoreCase("InActive");
//
//            boolean filterAllStatus = selectedStatus != null
//                    && selectedStatus.equalsIgnoreCase("ALL");
//
//            boolean filterSelectStatus = selectedStatus != null
//                    && selectedStatus.equalsIgnoreCase("Select Status");
//
//            // =====================================================
//            // GENDER FILTER CHECK
//            // =====================================================
//            boolean filterMale = selectedGender != null
//                    && selectedGender.equalsIgnoreCase("Male");
//
//            boolean filterFemale = selectedGender != null
//                    && selectedGender.equalsIgnoreCase("Female");
//
//            boolean filterSelectGender = selectedGender != null
//                    && selectedGender.equalsIgnoreCase("Select Gender");
//
//            // =====================================================
//            // IF STATUS = Select Status → NO RECORDS
//            // =====================================================
//            if (filterSelectStatus) {
//                pagination1.setPagegination(1, 1);
//                return;
//            }
//
//            // =====================================================
//            // COUNT QUERY
//            // =====================================================
//            String countSql = "SELECT COUNT(*) "
//                    + "FROM student s "
//                    + "WHERE s.status = 1 ";
//
//            // STATUS FILTER (mandatory)
//            if (filterActive) {
//                countSql += "AND s.current_status = 'ACTIVE' ";
//            } else if (filterInactive) {
//                countSql += "AND s.current_status <> 'ACTIVE' ";
//            }
//            // ALL = no extra condition
//
//            // GENDER FILTER (optional)
//            if (filterMale) {
//                countSql += "AND s.Gender = 'Male' ";
//            } else if (filterFemale) {
//                countSql += "AND s.Gender = 'Female' ";
//            }
//            // Select Gender = no gender filter
//
//            Number totalCount = (Number) em.createNativeQuery(countSql)
//                    .getSingleResult();
//
//            int count = totalCount.intValue();
//            int totalPage = (int) Math.ceil((double) count / limit);
//
//            if (totalPage <= 0) {
//                totalPage = 1;
//            }
//
//            pagination1.setPagegination(page, totalPage);
//
//            // =====================================================
//            // MAIN DATA QUERY
//            // =====================================================
//            String sql = "SELECT "
//                    + "s.student_id, " // 0
//                    + "s.current_status, " // 1
//                    + "s.admission_no, " // 2
//                    + "s.full_name, " // 3
//                    + "s.Gender, " // 4
//                    + "s.admission_date, " // 5
//                    + "s.contact_no, " // 6
//                    + "sp.father_contact " // 7
//                    + "FROM student s "
//                    + "LEFT JOIN student_parents sp "
//                    + "ON s.student_parents_id = sp.student_parents_id "
//                    + "AND sp.status = 1 "
//                    + "WHERE s.status = 1 ";
//
//            // STATUS FILTER
//            if (filterActive) {
//                sql += "AND s.current_status = 'ACTIVE' ";
//            } else if (filterInactive) {
//                sql += "AND s.current_status <> 'ACTIVE' ";
//            }
//
//            // GENDER FILTER
//            if (filterMale) {
//                sql += "AND s.Gender = 'Male' ";
//            } else if (filterFemale) {
//                sql += "AND s.Gender = 'Female' ";
//            }
//
//            sql += "ORDER BY s.admission_no ASC "
//                    + "LIMIT ? OFFSET ?";
//
//            List<Object[]> list = em.createNativeQuery(sql)
//                    .setParameter(1, limit)
//                    .setParameter(2, offset)
//                    .getResultList();
//
//            int rowNo = offset + 1;
//
//            for (Object[] row : list) {
//
//                int studentId = row[0] != null
//                        ? ((Number) row[0]).intValue()
//                        : 0;
//
//                String currentStatus = row[1] != null ? row[1].toString() : "";
//                String admissionNo = row[2] != null ? row[2].toString() : "";
//                String studentName = row[3] != null ? row[3].toString() : "";
//                String gender = row[4] != null ? row[4].toString() : "";
//                String joinedDate = row[5] != null ? row[5].toString() : "";
//                String studentContact = row[6] != null ? row[6].toString() : "";
//                String fatherContact = row[7] != null ? row[7].toString() : "";
//
//                // =====================================================
//                // FETCH LATEST COURSE
//                // =====================================================
//                String batchCourse = "";
//
//                List<Object[]> courseList = em.createNativeQuery(
//                        "SELECT c.batch, c.course_name "
//                        + "FROM course_enrollment ce "
//                        + "INNER JOIN course c ON ce.course_id = c.course_id "
//                        + "WHERE ce.student_id = ? "
//                        + "AND ce.status = 1 "
//                        + "AND c.status = 1 "
//                        + "ORDER BY ce.enrollment_id DESC "
//                        + "LIMIT 1"
//                )
//                        .setParameter(1, studentId)
//                        .getResultList();
//
//                if (!courseList.isEmpty()) {
//                    Object[] courseRow = courseList.get(0);
//
//                    String batch = courseRow[0] != null
//                            ? courseRow[0].toString()
//                            : "";
//
//                    String courseName = courseRow[1] != null
//                            ? courseRow[1].toString()
//                            : "";
//
//                    batchCourse = batch + " - " + courseName;
//                }
//
//                model.addRow(new Object[]{
//                    rowNo++, // #
//                    currentStatus, // Current Status
//                    admissionNo, // Admission No
//                    studentName, // Student Name
//                    gender, // Gender
//                    joinedDate, // Joined Date
//                    batchCourse, // Latest Batch + Course
//                    studentContact, // Student Contact
//                    fatherContact // Father Contact
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        } finally {
//            em.close();
//        }
//    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ent_st_table = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        ent_st_gender_combo = new javax.swing.JComboBox<>();
        ent_st_status_combo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        pagination1 = new Pagination.Pagination();
        lbl_total_rows = new javax.swing.JLabel();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Student Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        ent_st_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Current Status", "Admission", "Student Name", "Gender", "Joined Date", "Current/Last Course", "Student Contact", "Father Contact"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ent_st_table.setRowHeight(25);
        ent_st_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ent_st_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(ent_st_table);
        if (ent_st_table.getColumnModel().getColumnCount() > 0) {
            ent_st_table.getColumnModel().getColumn(0).setPreferredWidth(20);
            ent_st_table.getColumnModel().getColumn(1).setPreferredWidth(80);
            ent_st_table.getColumnModel().getColumn(2).setPreferredWidth(100);
            ent_st_table.getColumnModel().getColumn(3).setPreferredWidth(200);
            ent_st_table.getColumnModel().getColumn(6).setPreferredWidth(200);
        }

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Gender");

        ent_st_gender_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        ent_st_gender_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Gender", "Male", "Female" }));
        ent_st_gender_combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ent_st_gender_comboActionPerformed(evt);
            }
        });

        ent_st_status_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Status");

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printblue32.png"))); // NOI18N
        jButton1.setToolTipText("Siblings");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/xlsx32.png"))); // NOI18N
        jButton2.setToolTipText("Siblings");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

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
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search16.png"))); // NOI18N
        jButton7.setToolTipText("Course Enrolment");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
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
                                .addComponent(ent_st_status_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(ent_st_gender_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ent_st_status_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ent_st_gender_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addContainerGap())
        );

        pagination1.setOpaque(false);

        lbl_total_rows.setFont(new java.awt.Font("Roboto Medium", 3, 14)); // NOI18N
        lbl_total_rows.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_total_rows.setText("Total : 0 Records");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void ent_st_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ent_st_tableMouseClicked

    }//GEN-LAST:event_ent_st_tableMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        loadAllStudents(
                ent_st_table,
                ent_st_status_combo.getSelectedItem().toString(),
                ent_st_gender_combo.getSelectedItem().toString(),
                1
        );
    }//GEN-LAST:event_jButton6ActionPerformed

    private void ent_st_gender_comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ent_st_gender_comboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ent_st_gender_comboActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        loadAllStudents(
                ent_st_table,
                ent_st_status_combo.getSelectedItem().toString(),
                ent_st_gender_combo.getSelectedItem().toString(),
                1
        );
    }//GEN-LAST:event_jButton7ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> ent_st_gender_combo;
    public static javax.swing.JComboBox<String> ent_st_status_combo;
    public static javax.swing.JTable ent_st_table;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_total_rows;
    private Pagination.Pagination pagination1;
    // End of variables declaration//GEN-END:variables

}
