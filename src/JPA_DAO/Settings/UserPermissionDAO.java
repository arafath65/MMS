package JPA_DAO.Settings;

import Classes.HibernateConfig;
import Entities.Settings.UserPermission;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class UserPermissionDAO {

    public boolean save(UserPermission permission) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            em.persist(permission);

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

    public boolean update(UserPermission permission) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            em.merge(permission);

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

    public void saveOrUpdatePermission(
            int roleId,
            String permissionCode,
            boolean selected,
            String username) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            List<UserPermission> list = em.createQuery(
                    "SELECT p FROM UserPermission p "
                    + "WHERE p.userRolesId = :roleId "
                    + "AND p.permissions = :permission",
                    UserPermission.class)
                    .setParameter("roleId", roleId)
                    .setParameter("permission", permissionCode)
                    .getResultList();

            UserPermission permission;

            if (!list.isEmpty()) {

                permission = list.get(0);

                permission.setStatus(selected ? 1 : 0);
                permission.setLastModified(new Date());
                permission.setUser(username);

                em.merge(permission);

            } else {

                if (selected) {

                    permission = new UserPermission();

                    permission.setUserRolesId(roleId);
                    permission.setEmployeeId(null);
                    permission.setPermissions(permissionCode);
                    permission.setLastModified(new Date());
                    permission.setUser(username);
                    permission.setStatus(1);

                    em.persist(permission);
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

    public List<String> getActivePermissionsByRole(int roleId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT p.permissions "
                    + "FROM UserPermission p "
                    + "WHERE p.userRolesId = :roleId "
                    + "AND p.status = 1",
                    String.class)
                    .setParameter("roleId", roleId)
                    .getResultList();

        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();

        } finally {

            em.close();

        }
    }

    public boolean delete(UserPermission permission) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            UserPermission obj = em.find(UserPermission.class,
                    permission.getUserPermissionId());

            if (obj != null) {
                em.remove(obj);
            }

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
