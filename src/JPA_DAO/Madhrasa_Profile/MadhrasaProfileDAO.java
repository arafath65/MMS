/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Madhrasa_Profile;

import Classes.HibernateConfig;
import Entities.Madhrasa_Profile.MadhrasaProfile;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author UNKNOWN_UN
 */
public class MadhrasaProfileDAO {

    public void save(MadhrasaProfile profile) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            em.getTransaction().begin();

            em.persist(profile);

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public void update(MadhrasaProfile profile) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            em.getTransaction().begin();

            em.merge(profile);

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public MadhrasaProfile getProfile() {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            List<MadhrasaProfile> list = em.createQuery(
                    "FROM MadhrasaProfile m",
                    MadhrasaProfile.class
            ).setMaxResults(1).getResultList();

            if (!list.isEmpty()) {
                return list.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return null;
    }

}
