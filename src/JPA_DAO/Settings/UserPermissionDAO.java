package JPA_DAO.Settings;

import Classes.HibernateConfig;
import Entities.Settings.UserPermission;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.JCheckBox;

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

    public void savePermissions(
            int roleId,
            List<JCheckBox> permissionBoxes,
            String username) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            // Load all existing permissions for this role once
            List<UserPermission> existingPermissions = em.createQuery(
                    "SELECT p FROM UserPermission p WHERE p.userRolesId = :roleId",
                    UserPermission.class)
                    .setParameter("roleId", roleId)
                    .getResultList();

            // Convert to Map for fast searching
            Map<String, UserPermission> permissionMap = new HashMap<>();

            for (UserPermission p : existingPermissions) {
                permissionMap.put(p.getPermissions(), p);
            }

            Date now = new Date();

            for (JCheckBox box : permissionBoxes) {

                String permissionCode = box.getName();
                boolean selected = box.isSelected();

                UserPermission permission = permissionMap.get(permissionCode);

                if (permission != null) {

                    // Already exists -> update only
                    permission.setStatus(selected ? 1 : 0);
                    permission.setLastModified(now);
                    permission.setUser(username);

                    em.merge(permission);

                } else {

                    // Doesn't exist -> insert only if selected
                    if (selected) {

                        permission = new UserPermission();

                        permission.setUserRolesId(roleId);
                        permission.setEmployeeId(null);
                        permission.setPermissions(permissionCode);
                        permission.setStatus(1);
                        permission.setLastModified(now);
                        permission.setUser(username);

                        em.persist(permission);
                    }
                }
            }

            tx.commit();

        } catch (Exception ex) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw ex;

        } finally {
            em.close();
        }
    }

//    public void saveOrUpdatePermission(
//            int roleId,
//            String permissionCode,
//            boolean selected,
//            String username) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//
//            tx.begin();
//
//            List<UserPermission> list = em.createQuery(
//                    "SELECT p FROM UserPermission p "
//                    + "WHERE p.userRolesId = :roleId "
//                    + "AND p.permissions = :permission",
//                    UserPermission.class)
//                    .setParameter("roleId", roleId)
//                    .setParameter("permission", permissionCode)
//                    .getResultList();
//
//            UserPermission permission;
//
//            if (!list.isEmpty()) {
//
//                permission = list.get(0);
//
//                permission.setStatus(selected ? 1 : 0);
//                permission.setLastModified(new Date());
//                permission.setUser(username);
//
//                em.merge(permission);
//
//            } else {
//
//                if (selected) {
//
//                    permission = new UserPermission();
//
//                    permission.setUserRolesId(roleId);
//                    permission.setEmployeeId(null);
//                    permission.setPermissions(permissionCode);
//                    permission.setLastModified(new Date());
//                    permission.setUser(username);
//                    permission.setStatus(1);
//
//                    em.persist(permission);
//                }
//            }
//
//            tx.commit();
//
//        } catch (Exception e) {
//
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//
//            e.printStackTrace();
//
//        } finally {
//            em.close();
//        }
//    }
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

    public Set<String> getPermissionsByRole(int roleId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            List<String> list = em.createQuery(
                    "SELECT p.permissions "
                    + "FROM UserPermission p "
                    + "WHERE p.userRolesId = :roleId "
                    + "AND p.status = 1",
                    String.class)
                    .setParameter("roleId", roleId)
                    .getResultList();

            return new HashSet<>(list);

        } finally {
            em.close();
        }

    }
}
