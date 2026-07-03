/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Student_Management;

import Classes.HibernateConfig;
import Entities.Student_Management.OtherFeeAssignment;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;

/**
 *
 * @author UNKNOWN_UN
 */
public class OtherFeeAssignmentDAO {

    public void saveAll(List<OtherFeeAssignment> assignments) {
        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            for (OtherFeeAssignment assignment : assignments) {
                em.persist(assignment);
            }

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

}
