/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Settings;

import Classes.HibernateConfig;
import Entities.Settings.Course;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class CourseDAO {

    public void save(Course course) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(course);
        em.getTransaction().commit();
        em.close();
    }

    public void deleteById(int courseId) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        Course c = em.find(Course.class, courseId);
        if (c != null) {
            em.remove(c);
        }

        em.getTransaction().commit();
        em.close();
    }

    // Fetch course_id by batch
    // Delete course by all main attributes
    public boolean deleteCourse(String batch, String courseName,
            int enrolYear, int enrolMonth,
            int compYear, int compMonth,
            String paymentMode, int admissionFee, int fee) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        TypedQuery<Course> query = em.createQuery(
                "SELECT c FROM Course c WHERE "
                + "c.batch = :batch AND "
                + "c.courseName = :courseName AND "
                + "c.enrolYear = :enrolYear AND "
                + "c.enrolMonth = :enrolMonth AND "
                + "c.compYear = :compYear AND "
                + "c.compMonth = :compMonth AND "
                + "c.paymentMode = :paymentMode AND "
                + "c.admissionFee = :admissionFee AND "
                + "c.fee = :fee", Course.class
        );

        query.setParameter("batch", batch);
        query.setParameter("courseName", courseName);
        query.setParameter("enrolYear", enrolYear);
        query.setParameter("enrolMonth", enrolMonth);
        query.setParameter("compYear", compYear);
        query.setParameter("compMonth", compMonth);
        query.setParameter("paymentMode", paymentMode);
        query.setParameter("admissionFee", admissionFee);
        query.setParameter("fee", fee);

        List<Course> result = query.getResultList();

        if (!result.isEmpty()) {
            for (Course c : result) {
                em.remove(em.contains(c) ? c : em.merge(c));
            }
            em.getTransaction().commit();
            em.close();
            return true;
        }

        em.getTransaction().commit();
        em.close();
        return false; // nothing deleted
    }

    public List<Course> findAll() {
        EntityManager em = HibernateConfig.getEntityManager();
        List<Course> list = em
                .createNamedQuery("Course.findAll", Course.class)
                .getResultList();
        em.close();
        return list;
    }

    public List<Course> searchActiveCourses(String text) {

        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<Course> q = em.createQuery(
                "SELECT c FROM Course c "
                + "WHERE c.status = 1 AND "
                + "(LOWER(c.courseName) LIKE :text OR LOWER(c.batch) LIKE :text) "
                + "ORDER BY c.courseName",
                Course.class
        );

        q.setParameter("text", text.toLowerCase() + "%");

        q.setMaxResults(5);   // 🔥 LIMIT 5

        List<Course> list = q.getResultList();
        em.close();

        return list;
    }
    
    public Course findByCourseNameAndBatch(String courseName, String batch) {

    EntityManager em = HibernateConfig.getEntityManager();

    TypedQuery<Course> q = em.createQuery(
            "SELECT c FROM Course c " +
            "WHERE c.status = 1 " +
            "AND c.courseName = :name " +
            "AND c.batch = :batch",
            Course.class
    );

    q.setParameter("name", courseName);
    q.setParameter("batch", batch);

    List<Course> list = q.getResultList();
    em.close();

    return list.isEmpty() ? null : list.get(0);
}
    
    public Course findById(int id) {

    EntityManager em = HibernateConfig.getEntityManager();
    Course course = em.find(Course.class, id);
    em.close();
    return course;
}



}
