package JPA_DAO.Settings;

import Classes.HibernateConfig;
import Entities.Settings.StudentClass;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author UNKNOWN_UN
 */
public class ClassDAO {

    public void save(StudentClass classes) {

        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(classes);
        em.getTransaction().commit();
        em.close();
    }

    public void deleteById(int classId) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        StudentClass c = em.find(StudentClass.class, classId);
        if (c != null) {
            em.remove(c);
        }

        em.getTransaction().commit();
        em.close();
    }

    // Fetch course_id by batch
    // Delete course by all main attributes
    public boolean deleteCourse(String className) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        TypedQuery<StudentClass> query = em.createQuery(
                "SELECT c FROM StudentClass c WHERE c.className = :className",
                StudentClass.class
        );

        query.setParameter("className", className);

        List<StudentClass> result = query.getResultList();

        for (StudentClass c : result) {
            em.remove(c);
        }

        em.getTransaction().commit();
        em.close();

        return !result.isEmpty();
    }

    public List<StudentClass> findAll() {
        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<StudentClass> query = em.createQuery(
                "SELECT s FROM StudentClass s WHERE s.status = 1",
                StudentClass.class
        );

        List<StudentClass> list = query.getResultList();
        em.close();
        return list;
    }

//    public List<StudentClass> findAll() {
//        EntityManager em = HibernateConfig.getEntityManager();
//        List<StudentClass> list = em
//                .createNamedQuery("StudentClass.findAll", StudentClass.class)
//                .getResultList();
//        em.close();
//        return list;
//    }
}
