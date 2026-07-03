/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Employee;

import Classes.HibernateConfig;
import Entities.Employee.Employee;
import Entities.Employee.EmployeeLanguages;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class EmployeeLanguagesDAO {

    public void saveOrUpdateLanguage(Employee employee,
            String language,
            boolean selected,
            String username) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            EmployeeLanguages lang = null;

            try {

                lang = em.createQuery(
                        "SELECT l FROM EmployeeLanguages l "
                        + "WHERE l.employee.employeeId=:empId "
                        + "AND l.languages=:lang",
                        EmployeeLanguages.class)
                        .setParameter("empId", employee.getEmployeeId())
                        .setParameter("lang", language)
                        .getSingleResult();

            } catch (Exception ex) {
                lang = null;
            }

            if (selected) {

                if (lang == null) {

                    lang = new EmployeeLanguages();

                    lang.setEmployee(employee);
                    lang.setLanguages(language);
                    lang.setLastModified(new Date());
                    lang.setUser(username);
                    lang.setStatus(1);

                    em.persist(lang);

                } else {

                    lang.setStatus(1);
                    lang.setLastModified(new Date());
                    lang.setUser(username);

                    em.merge(lang);

                }

            } else {

                if (lang != null && lang.getStatus() == 1) {

                    lang.setStatus(0);
                    lang.setLastModified(new Date());
                    lang.setUser(username);

                    em.merge(lang);

                }

            }

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
        }

    }

    public List<String> getActiveLanguagesByEmployeeId(int employeeId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT l.languages "
                    + "FROM EmployeeLanguages l "
                    + "WHERE l.employee.employeeId = :id "
                    + "AND l.status = 1",
                    String.class)
                    .setParameter("id", employeeId)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

}
