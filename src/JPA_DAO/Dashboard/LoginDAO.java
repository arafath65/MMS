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
                    "SELECT "
                    + "l.login_id, "
                    + "l.employee_id, "
                    + "l.user_roles_id, "
                    + "l.username, "
                    + "r.user_roles, "
                    + "e.full_name "
                    + "FROM login l "
                    + "INNER JOIN employee e "
                    + "ON e.employee_id = l.employee_id "
                    + "INNER JOIN user_roles r "
                    + "ON r.user_roles_id = l.user_roles_id "
                    + "WHERE l.username = ? "
                    + "AND l.password = ? "
                    + "AND l.is_active = 1 "
                    + "AND l.status = 1"
            )
                    .setParameter(1, username.trim())
                    .setParameter(2, password.trim())
                    .getResultList();

            if (!list.isEmpty()) {

                // Update Last Login
                em.getTransaction().begin();

                em.createNativeQuery(
                        "UPDATE login "
                        + "SET last_login = NOW() "
                        + "WHERE login_id = ?"
                )
                        .setParameter(1, list.get(0)[0])
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
