/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Dashboard;

import Classes.HibernateConfig;
import java.util.List;
import javax.persistence.EntityManager;

public class LoginDAO {

    public Object[] login(String username, String password) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            List<Object[]> list = em.createNativeQuery(
                    "SELECT login_id, emp_id, role, username FROM login WHERE username=? AND password=? AND is_active=1 AND status=1 ")
                    .setParameter(1, username.trim())
                    .setParameter(2, password.trim())
                    .getResultList();

            if (!list.isEmpty()) {

                // Update last login
                em.getTransaction().begin();

                em.createNativeQuery(
                        "UPDATE login "
                        + "SET last_login=NOW() "
                        + "WHERE username=?"
                )
                        .setParameter(1, username.trim())
                        .executeUpdate();

                em.getTransaction().commit();

                return list.get(0);
            }

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
        }

        return null;
    }

}
