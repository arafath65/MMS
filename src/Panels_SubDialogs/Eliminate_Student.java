package Panels_SubDialogs;

import Classes.ChequeNumberFormatter;
import Classes.DecimalOnlyFilter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NumberOnlyFilter;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Dashboard_Design.AppNavigator;
import Entities.Student_Management.StudentEliminates;
import JPA_DAO.Inventory.ItemDAO;
import JPA_DAO.Settings.CourseDAO;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
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

        el_st_due_table.setDefaultRenderer(Object.class, new TableGradientCell());
        el_st_due_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        jLabel1.setText("<html><center>Are you sure you want to update <b>" + studentName + "</b><br>"
                + " <b> student's enrollment </b> status?</center></html>");

        loadCourseDuesToTable(selectedStudentIds);

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

    public void loadCourseDuesToTable(int studentId) {

        DefaultTableModel model = (DefaultTableModel) el_st_due_table.getModel();
        model.setRowCount(0);

        int count = 1;

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            // =====================================================
            // 1. COURSE LOGIC
            // =====================================================
            List<Object[]> courseList = em.createNativeQuery(
                    "SELECT student_fee_payments_id, enrollment_id, total_fee, total_paid, total_balance, course_type, created_at "
                    + "FROM student_fee_payments "
                    + "WHERE student_id=? AND status=1"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            for (Object[] courseRow : courseList) {

                int enrollmentId = Integer.parseInt(courseRow[1].toString());

                double totalFee = courseRow[2] != null ? Double.parseDouble(courseRow[2].toString()) : 0;
                double totalPaid = courseRow[3] != null ? Double.parseDouble(courseRow[3].toString()) : 0;
                double balance = courseRow[4] != null ? Double.parseDouble(courseRow[4].toString()) : 0;

                String courseType = courseRow[5] != null ? courseRow[5].toString() : "";
                String date = courseRow[6] != null ? courseRow[6].toString().split(" ")[0] : "";

                // ✅ CHEQUE (PENDING ONLY)
                double chequePendingCourse = ((Number) em.createNativeQuery(
                        "SELECT COALESCE(SUM(d.paid_amount),0) "
                        + "FROM student_fee_round_payment_master_details d "
                        + "JOIN student_fee_cheque_details c "
                        + "  ON c.reference_id = d.student_fee_round_payment_master_id "
                        + "  AND c.reference_type='ROUND' "
                        + "  AND c.category='STUDENT' "
                        + "  AND c.status=1 "
                        + "  AND c.cheque_status = 'PENDING' " // ✅ STRICT FILTER HERE
                        + "WHERE d.reference_type='COURSE' "
                        + "AND d.enrollment_id=? "
                        + "AND d.status=1"
                )
                        .setParameter(1, enrollmentId)
                        .getSingleResult()).doubleValue();
//                double chequePendingCourse = ((Number) em.createNativeQuery(
//                        "SELECT COALESCE(SUM(d.paid_amount),0) "
//                        + "FROM student_fee_round_payment_master_details d "
//                        + "JOIN student_fee_cheque_details c "
//                        + "ON c.reference_id = d.student_fee_round_payment_master_id "
//                        + "AND c.reference_type='ROUND' "
//                        + "AND c.category='STUDENT' "
//                        + "WHERE d.reference_type='COURSE' "
//                        + "AND d.enrollment_id=? "
//                        + "AND d.status=1 "
//                        + "AND c.cheque_status='PENDING' "
//                        + "AND c.status=1"
//                )
//                        .setParameter(1, enrollmentId)
//                        .getSingleResult()).doubleValue();

                double finalDueCourse = Math.max(balance - chequePendingCourse, 0);

                // ✅ QTY (MONTHLY)
                int qty = 1;
                if ("MONTHLY".equalsIgnoreCase(courseType)) {
                    qty = getPendingMonthCount(enrollmentId);
                    if (qty <= 0) {
                        qty = 1;
                    }
                }

                //    System.out.println("COURSE ROW: " + enrollmentId);
                model.addRow(new Object[]{
                    count++,
                    "COURSE",
                    "Course (" + courseType + ")",
                    qty,
                    //                    GeneralMethods.formatWithComma(totalFee),
                    //                    GeneralMethods.formatWithComma(totalPaid),
                    //                    GeneralMethods.formatWithComma(chequePendingCourse),
                    GeneralMethods.formatWithComma(finalDueCourse), //                    "",
                //                    false,
                //                    "COURSE_" + enrollmentId
                });
            }

            // =====================================================
            // 2. ADDITIONAL + INVENTORY (FINAL FIXED CHEQUE LOGIC)
            // =====================================================
            List<Object[]> issuedList = em.createNativeQuery(
                    "SELECT fee_type_id, MIN(student_additional_fees_id), SUM(amount), MIN(issued_date) "
                    + "FROM student_additional_fees "
                    + "WHERE student_id=? AND status=1 "
                    + "GROUP BY fee_type_id"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            for (Object[] addRow : issuedList) {

                int feeTypeId = Integer.parseInt(addRow[0].toString());
                int additionalFeeId = Integer.parseInt(addRow[1].toString());
                double totalAmount = Double.parseDouble(addRow[2].toString());

                String issuedDate = addRow[3] != null ? addRow[3].toString().split(" ")[0] : "";

//                System.out.println("\n=============================");
//                System.out.println("ADD ID: " + additionalFeeId);
//                System.out.println("FEE TYPE: " + feeTypeId);
//                System.out.println("TOTAL AMOUNT: " + totalAmount);
                // =====================================================
                // CASH / CARD ONLY PAID
                // =====================================================
                Double totalPaidAdd = (Double) em.createNativeQuery(
                        "SELECT COALESCE(SUM(p.amount_paid),0) "
                        + "FROM student_additional_fee_payments p "
                        + "JOIN student_additional_fees saf "
                        + "ON p.student_additional_fees_id = saf.student_additional_fees_id "
                        + "WHERE saf.fee_type_id=? AND saf.student_id=? "
                        + "AND p.status=1 "
                        + "AND p.payment_method <> 'CHEQUE'"
                )
                        .setParameter(1, feeTypeId)
                        .setParameter(2, studentId)
                        .getSingleResult();

                if (totalPaidAdd == null) {
                    totalPaidAdd = 0.0;
                }

                //    System.out.println("TOTAL PAID (NON-CHEQUE): " + totalPaidAdd);
                // =====================================================
                // 🔥 CHEQUE LOGIC (FINAL FIX)
                // =====================================================
                double chequePendingAdd = 0;

                // STEP 1: GET ROUND MASTER IDS (CHEQUE ONLY)
                List<Integer> masterIds = em.createNativeQuery(
                        "SELECT student_fee_round_payment_master_id "
                        + "FROM student_fee_round_payment_master "
                        + "WHERE student_id=? AND payment_mode='CHEQUE' AND status=1"
                )
                        .setParameter(1, studentId)
                        .getResultList();

                //    System.out.println("ROUND MASTER IDS (CHEQUE): " + masterIds);
                // STEP 2: FOR EACH MASTER → CHECK ADDITIONAL PAYMENTS
                for (Integer masterId : masterIds) {

                    Object result = em.createNativeQuery(
                            "SELECT COALESCE(SUM(d.paid_amount),0) "
                            + "FROM student_fee_round_payment_master_details d "
                            + "JOIN student_fee_cheque_details c "
                            + "  ON c.reference_id = d.student_fee_round_payment_master_id "
                            + "  AND c.reference_type='ROUND' "
                            + "  AND c.category='STUDENT' "
                            + "  AND c.status=1 "
                            + "  AND c.cheque_status = 'PENDING' " // ✅ ONLY PENDING
                            + "WHERE d.student_fee_round_payment_master_id=? "
                            + "AND d.reference_type='ADDITIONAL' "
                            + "AND d.reference_id=? "
                            + "AND d.status=1"
                    )
                            .setParameter(1, masterId)
                            .setParameter(2, additionalFeeId)
                            .getSingleResult();
//                    Object result = em.createNativeQuery(
//                            "SELECT COALESCE(SUM(d.paid_amount),0) "
//                            + "FROM student_fee_round_payment_master_details d "
//                            + "JOIN student_fee_cheque_details c "
//                            + "ON c.reference_id = d.student_fee_round_payment_master_id "
//                            + "AND c.reference_type='ROUND' "
//                            + "AND c.category='STUDENT' "
//                            + "WHERE d.student_fee_round_payment_master_id=? "
//                            + "AND d.reference_type='ADDITIONAL' "
//                            + "AND d.reference_id=? "
//                            + "AND c.cheque_status='PENDING' "
//                            + "AND d.status=1 "
//                            + "AND c.status=1"
//                    )
//                            .setParameter(1, masterId)
//                            .setParameter(2, additionalFeeId)
//                            .getSingleResult();

                    double paidAmount = ((Number) result).doubleValue();

//                    System.out.println("MASTER: " + masterId
//                            + " | ADD_ID: " + additionalFeeId
//                            + " | CHEQUE_PAID: " + paidAmount);
                    chequePendingAdd += paidAmount;
                }

                //    System.out.println("TOTAL CHEQUE (ADDITIONAL): " + chequePendingAdd);
                // =====================================================
                // FINAL CALCULATION
                // =====================================================
                double balanceAdd = totalAmount - totalPaidAdd;
                double finalDueAdd = Math.max(balanceAdd - chequePendingAdd, 0);

//                System.out.println("BALANCE: " + balanceAdd);
//                System.out.println("FINAL CHEQUE: " + chequePendingAdd);
//                System.out.println("FINAL DUE: " + finalDueAdd);
                if (balanceAdd <= 0) {
                    continue;
                }

                Object[] feeData = (Object[]) em.createNativeQuery(
                        "SELECT fee_name, item_id FROM fee_types WHERE fee_type_id=?"
                )
                        .setParameter(1, feeTypeId)
                        .getSingleResult();

                String feeName = feeData[0].toString();
                int itemId = feeData[1] != null ? Integer.parseInt(feeData[1].toString()) : 0;

                String category = (itemId == 0) ? "SERVICE" : "INVENTORY";

                model.addRow(new Object[]{
                    count++,
                    category,
                    feeName,
                    1,
                    //                    GeneralMethods.formatWithComma(totalAmount),
                    //                    GeneralMethods.formatWithComma(totalPaidAdd),
                    //                    GeneralMethods.formatWithComma(chequePendingAdd),
                    GeneralMethods.formatWithComma(finalDueAdd), //                    "",
                //                    false,
                //                    "ADD_" + additionalFeeId
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public int getPendingMonthCount(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
            StudentFeeInstallmentsDAO.MonthDataDTO data = dao.getMonthData(enrollmentId);

            int pendingCount = 0;

            int y = data.startYear;
            int m = data.startMonth;

            while (true) {

                String monthStr = String.format("%02d", m);
                String full = y + "-" + monthStr;

                // ============================
                // 🔥 USE NEW DATA MODEL
                // ============================
                double monthlyFee = data.monthlyFeeMap.containsKey(full)
                        ? data.monthlyFeeMap.get(full)
                        : data.baseMonthlyFee;

                double adjustment = data.adjustmentMap.getOrDefault(full, 0.0);
                double paid = data.paidMap.getOrDefault(full, 0.0);

                double finalFee = monthlyFee - adjustment;

                boolean isPending = false;

                // ============================
                // 🔥 FINAL PENDING LOGIC
                // ============================
                if (adjustment > 0) {

                    // WAIVED → NOT pending
                    if (finalFee == 0) {
                        isPending = false;
                    } // DISCOUNT → check remaining
                    else if (paid < finalFee) {
                        isPending = true;
                    }

                } else {

                    // NO ADJUSTMENT → normal check
                    if (paid < monthlyFee) {
                        isPending = true;
                    }
                }

                if (isPending) {
                    pendingCount++;
                }

                // ============================
                // STOP CONDITION
                // ============================
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

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    public String getAdmissionNoByStudentId(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            Object result = em.createNativeQuery(
                    "SELECT admission_no FROM student WHERE student_id = ? AND status = 1"
            )
                    .setParameter(1, studentId)
                    .getSingleResult();

            return result != null ? result.toString() : "";

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            em.close();
        }
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
        jPanel10 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        el_st_due_table = new javax.swing.JTable();
        buttonGradient5 = new Classes.ButtonGradient();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LABEL");

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Elimination", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

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
                    .addComponent(jScrollPane1)
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

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Due Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        el_st_due_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Category", "Course Name / Description", "Qty / Month", "Total Due"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        el_st_due_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                el_st_due_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(el_st_due_table);
        if (el_st_due_table.getColumnModel().getColumnCount() > 0) {
            el_st_due_table.getColumnModel().getColumn(0).setPreferredWidth(20);
            el_st_due_table.getColumnModel().getColumn(2).setPreferredWidth(150);
        }

        buttonGradient5.setText("PAY");
        buttonGradient5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(159, 159, 159)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void el_st_due_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_el_st_due_tableMouseClicked

    }//GEN-LAST:event_el_st_due_tableMouseClicked

    private void buttonGradient5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient5ActionPerformed

        DefaultTableModel model = (DefaultTableModel) el_st_due_table.getModel();
        if (el_st_due_table.getRowCount() == -1) {
            return;
        }

        AppNavigator.openFeesManagement();

        Fees_Management fees_Management = AppNavigator.getFeesPanel();

        if (fees_Management != null) {

            String admissionNo = getAdmissionNoByStudentId(4);
            fees_Management.fm_fees_admission_no_combo.setSelectedItem(admissionNo);
            fees_Management.fm_fees_name_combo.setSelectedItem(studentName);

            fees_Management.jButton2.doClick();
        }

        this.dispose();

    }//GEN-LAST:event_buttonGradient5ActionPerformed

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
    private Classes.ButtonGradient buttonGradient5;
    private com.toedter.calendar.JDateChooser el_st_date;
    public static javax.swing.JTable el_st_due_table;
    private javax.swing.JEditorPane el_st_note_textpane;
    private javax.swing.JComboBox<String> el_st_status_combo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
