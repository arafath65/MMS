/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Employee;

import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Entities.Employee.EmployeeCareerHistory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class EmployeeCareerHistoryDAO {

    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

    public boolean save(EmployeeCareerHistory history) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            em.persist(history);

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

    public void loadEmployeeCareerHistory(JTable table, int employeeId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            List<Object[]> list = em.createQuery(
                    "SELECT h.employeeCareerHistoryId, "
                    + "h.effectiveDate, "
                    + "h.designation, "
                    + "h.salary, "
                    + "h.changeType, "
                    + "h.remarks "
                    + "FROM EmployeeCareerHistory h "
                    + "WHERE h.employee.employeeId = :id "
                    + "AND h.status = 1 "
                    + "ORDER BY h.effectiveDate DESC",
                    Object[].class)
                    .setParameter("id", employeeId)
                    .getResultList();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int count = 1;

            for (Object[] row : list) {

                model.addRow(new Object[]{
                    count++,
                    sdf.format(row[1]), // Date
                    row[2], // Designation
                    GeneralMethods.formatWithComma(
                    GeneralMethods.parseCommaNumber(row[3].toString())
                    ),
                    row[4], // Change Type
                    row[5], // Remarks
                    row[0] // employee_career_history_id
                });
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            em.close();
        }
    }

    public boolean softDelete(int careerHistoryId) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            EmployeeCareerHistory history = em.find(
                    EmployeeCareerHistory.class,
                    careerHistoryId
            );

            if (history == null) {
                return false;
            }

            history.setStatus(0);

            em.merge(history);

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

}
