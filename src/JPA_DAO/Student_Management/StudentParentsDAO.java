package JPA_DAO.Student_Management;

import Classes.HibernateConfig;
import Entities.Student_Management.Student;
import Entities.Student_Management.StudentParents;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class StudentParentsDAO {

    // --------------------------------------------------
    // Save
    // --------------------------------------------------
    public void save(StudentParents parents) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(parents);
        em.getTransaction().commit();
        em.close();
    }

    // --------------------------------------------------
    // Update
    // --------------------------------------------------
    public void update(StudentParents parents) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.merge(parents);
        em.getTransaction().commit();
        em.close();
    }

    // --------------------------------------------------
    // Delete by ID
    // --------------------------------------------------
    public void deleteById(int studentParentsId) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        StudentParents sp = em.find(StudentParents.class, studentParentsId);
        if (sp != null) {
            em.remove(sp);
        }

        em.getTransaction().commit();
        em.close();
    }

    // --------------------------------------------------
    // Find by ID
    // --------------------------------------------------
    public StudentParents findById(int studentParentsId) {
        EntityManager em = HibernateConfig.getEntityManager();
        StudentParents sp = em.find(StudentParents.class, studentParentsId);
        em.close();
        return sp;
    }

    // --------------------------------------------------
    // Find by Student ID (single record)
    // --------------------------------------------------
    public StudentParents findByStudentId(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<StudentParents> q = em.createQuery(
                "SELECT s.studentParents FROM Student s WHERE s.studentId = :studentId",
                StudentParents.class
        );

        q.setParameter("studentId", studentId);

        List<StudentParents> list = q.getResultList();
        em.close();

        return list.isEmpty() ? null : list.get(0);
    }
//    public StudentParents findByStudentId(int studentId) {
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        TypedQuery<StudentParents> q = em.createQuery(
//            "SELECT sp FROM StudentParents sp WHERE sp.student.studentId = :studentId",
//            StudentParents.class
//        );
//        q.setParameter("studentId", studentId);
//
//        List<StudentParents> list = q.getResultList();
//        em.close();
//
//        return list.isEmpty() ? null : list.get(0);
//    }

    // --------------------------------------------------
    // Find All
    // --------------------------------------------------
    public List<StudentParents> findAll() {
        EntityManager em = HibernateConfig.getEntityManager();

        List<StudentParents> list = em.createNamedQuery(
                "StudentParents.findAll",
                StudentParents.class
        ).getResultList();

        em.close();
        return list;
    }

    // --------------------------------------------------
    // Find Active Records
    // --------------------------------------------------
    public List<StudentParents> findActive() {
        EntityManager em = HibernateConfig.getEntityManager();

        List<StudentParents> list = em.createNamedQuery(
                "StudentParents.findByStatus",
                StudentParents.class
        ).setParameter("status", true)
                .getResultList();

        em.close();
        return list;
    }

    // --------------------------------------------------
    // Soft Delete by Student ID
    // --------------------------------------------------
    public void deactivateByStudentId(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        TypedQuery<Student> q = em.createQuery(
                "SELECT s FROM Student s WHERE s.studentId = :studentId",
                Student.class
        );

        q.setParameter("studentId", studentId);

        List<Student> list = q.getResultList();

        if (!list.isEmpty()) {
            Student student = list.get(0);
            StudentParents parents = student.getStudentParents();

            if (parents != null) {
                parents.setStatus(false);
            }
        }

        em.getTransaction().commit();
        em.close();
    }
//    public void deactivateByStudentId(int studentId) {
//        EntityManager em = HibernateConfig.getEntityManager();
//        em.getTransaction().begin();
//
//        TypedQuery<StudentParents> q = em.createQuery(
//                "SELECT sp FROM StudentParents sp WHERE sp.student.studentId = :studentId",
//                StudentParents.class
//        );
//        q.setParameter("studentId", studentId);
//
//        List<StudentParents> list = q.getResultList();
//        for (StudentParents sp : list) {
//            sp.setStatus(false);
//        }
//
//        em.getTransaction().commit();
//        em.close();
//    }

    public StudentParents findExistingParent(StudentParents input) {

        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<StudentParents> q = em.createQuery(
                "SELECT sp FROM StudentParents sp WHERE "
                + "(sp.motherNic = :motherNic AND sp.motherNic IS NOT NULL) OR "
                + "(sp.motherContact = :motherContact AND sp.motherContact IS NOT NULL) OR "
                + "(sp.fatherNic = :fatherNic AND sp.fatherNic IS NOT NULL) OR "
                + "(sp.fatherContact = :fatherContact AND sp.fatherContact IS NOT NULL) OR "
                + "(sp.guardianNic = :guardianNic AND sp.guardianNic IS NOT NULL) OR "
                + "(sp.guardianContact = :guardianContact AND sp.guardianContact IS NOT NULL)",
                StudentParents.class
        );

        q.setParameter("motherNic", input.getMotherNic());
        q.setParameter("motherContact", input.getMotherContact());
        q.setParameter("fatherNic", input.getFatherNic());
        q.setParameter("fatherContact", input.getFatherContact());
        q.setParameter("guardianNic", input.getGuardianNic());
        q.setParameter("guardianContact", input.getGuardianContact());

        List<StudentParents> list = q.getResultList();
        em.close();

        return list.isEmpty() ? null : list.get(0);
    }
}
