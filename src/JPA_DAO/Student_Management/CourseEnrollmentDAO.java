package JPA_DAO.Student_Management;

import Classes.HibernateConfig;
import Entities.Student_Management.CourseEnrollment;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class CourseEnrollmentDAO {

    // Save new enrollment
    public void save(CourseEnrollment enrollment) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(enrollment);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Update enrollment
    public void update(CourseEnrollment enrollment) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(enrollment);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Find all active enrollments
//    public List<CourseEnrollment> findAll() {
//        EntityManager em = HibernateConfig.getEntityManager();
//        TypedQuery<CourseEnrollment> q = em.createNamedQuery("CourseEnrollment.findAll", CourseEnrollment.class);
//        List<CourseEnrollment> list = q.getResultList();
//        em.close();
//        return list;
//    }
    public List<CourseEnrollment> findAll() {

        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<CourseEnrollment> q = em.createQuery(
                "SELECT ce FROM CourseEnrollment ce WHERE ce.status = 1",
                CourseEnrollment.class
        );

        List<CourseEnrollment> list = q.getResultList();
        em.close();

        return list;
    }

    // Find by ID
//    public CourseEnrollment findById(int enrollmentId) {
//        EntityManager em = HibernateConfig.getEntityManager();
//        TypedQuery<CourseEnrollment> q = em.createNamedQuery("CourseEnrollment.findById", CourseEnrollment.class);
//        q.setParameter("enrollmentId", enrollmentId);
//        List<CourseEnrollment> list = q.getResultList();
//        em.close();
//        return list.isEmpty() ? null : list.get(0);
//    }
    public CourseEnrollment findById(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();
        CourseEnrollment enrollment = null;

        try {
            enrollment = em.find(CourseEnrollment.class, enrollmentId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return enrollment;
    }

//    public List<CourseEnrollment> findByStudentId(int studentId) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        List<CourseEnrollment> list;
//
//        try {
//
//            TypedQuery<CourseEnrollment> q = em.createQuery(
//                    "SELECT ce FROM CourseEnrollment ce "
//                    + "WHERE ce.student.studentId = :studentId AND ce.status = true",
//                    CourseEnrollment.class
//            );
//
//            q.setParameter("studentId", studentId);
//            list = q.getResultList();
//
//        } finally {
//            em.close();
//        }
//
//        return list;
//    }
    public List<CourseEnrollment> findByStudentId(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<CourseEnrollment> q = em.createQuery(
                "SELECT ce FROM CourseEnrollment ce "
                + "WHERE ce.student.studentId = :studentId "
                + "AND ce.status = 1",
                CourseEnrollment.class
        );

        q.setParameter("studentId", studentId);

        List<CourseEnrollment> list = q.getResultList();
        em.close();

        return list;
    }

    // Find by Course
//    public List<CourseEnrollment> findByCourseId(int courseId) {
//        EntityManager em = HibernateConfig.getEntityManager();
//        TypedQuery<CourseEnrollment> q = em.createNamedQuery("CourseEnrollment.findByCourseId", CourseEnrollment.class);
//        q.setParameter("courseId", courseId);
//        List<CourseEnrollment> list = q.getResultList();
//        em.close();
//        return list;
//    }
    public List<CourseEnrollment> findByCourseId(int courseId) {

        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<CourseEnrollment> q = em.createQuery(
                "SELECT ce FROM CourseEnrollment ce "
                + "WHERE ce.courseId = :courseId "
                + "AND ce.status = 1",
                CourseEnrollment.class
        );

        q.setParameter("courseId", courseId);

        List<CourseEnrollment> list = q.getResultList();
        em.close();

        return list;
    }

    public void softDelete(int id) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            CourseEnrollment ce = em.find(CourseEnrollment.class, id);

            if (ce != null) {
                ce.setStatus(0); // ✅ set inactive
                em.merge(ce);
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void updateStatus(int id, String newStatus) {

        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        CourseEnrollment ce = em.find(CourseEnrollment.class, id);

        if (ce != null) {
            ce.setCourseStatus(newStatus);
            em.merge(ce);
        }

        em.getTransaction().commit();
        em.close();
    }

}
