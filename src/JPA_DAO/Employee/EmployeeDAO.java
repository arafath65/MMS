
package JPA_DAO.Employee;

import Classes.HibernateConfig;
import Entities.Employee.Employee;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class EmployeeDAO {

    // =====================================================
    // SAVE
    // =====================================================
    public boolean save(Employee employee) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();
            em.persist(employee);
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

    // =====================================================
    // UPDATE
    // =====================================================
    public boolean update(Employee employee) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();
            em.merge(employee);
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

    // =====================================================
    // FIND BY ID
    // =====================================================
    public Employee findById(int employeeId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            return em.find(Employee.class, employeeId);

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {
            em.close();
        }
    }

    // =====================================================
    // FIND BY EMPLOYEE NO
    // =====================================================
    public Employee findByEmployeeNo(String employeeNo) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT e FROM Employee e "
                    + "WHERE e.employeeNo = :empNo "
                    + "AND e.status = 1",
                    Employee.class)
                    .setParameter("empNo", employeeNo)
                    .getSingleResult();

        } catch (Exception e) {

            return null;

        } finally {
            em.close();
        }
    }

    // =====================================================
    // GET FIRST (PROFILE SCREEN STYLE)
    // =====================================================
    public Employee getEmployeeById(int employeeId) {

    EntityManager em = HibernateConfig.getEntityManager();

    try {

        return em.createQuery(
                "SELECT e FROM Employee e "
                + "WHERE e.employeeId = :id "
                + "AND e.status = 1",
                Employee.class)
                .setParameter("id", employeeId)
                .getSingleResult();

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    } finally {
        em.close();
    }
}

    // =====================================================
    // SOFT DELETE
    // =====================================================
    public boolean softDelete(int employeeId) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            Employee employee = em.find(Employee.class, employeeId);

            if (employee == null) {
                return false;
            }

            employee.setStatus(0);

            em.merge(employee);

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
