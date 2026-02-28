package JPA_DAO.Student_Management;

import Classes.GeneralMethods.StudentSearchType;
import Classes.HibernateConfig;
import Entities.Student_Management.CourseEnrollment;
import Entities.Student_Management.Student;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class StudentDAO {

    // --------------------------------------------------
    // Save
    // --------------------------------------------------
    public void save(Student student) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(student);
        em.getTransaction().commit();
        em.close();
    }

    // --------------------------------------------------
    // Update
    // --------------------------------------------------
    public void update(Student student) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.merge(student);
        em.getTransaction().commit();
        em.close();
    }

    // --------------------------------------------------
    // Delete by ID
    // --------------------------------------------------
    public void deleteById(int studentId) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        Student s = em.find(Student.class, studentId);
        if (s != null) {
            em.remove(s);
        }

        em.getTransaction().commit();
        em.close();
    }

    // --------------------------------------------------
// Soft Delete (Set INACTIVE)
// --------------------------------------------------
    public void softDeleteByAdmissionNo(String admissionNo) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            TypedQuery<Student> q = em.createQuery(
                    "SELECT s FROM Student s WHERE s.admissionNo = :admissionNo",
                    Student.class
            );
            q.setParameter("admissionNo", admissionNo.trim());

            List<Student> list = q.getResultList();

            if (list.isEmpty()) {
                throw new RuntimeException("Student not found.");
            }

            Student student = list.get(0);

            // Soft delete fields
            student.setCurrentStatus("INACTIVE");
            student.setStatus(false);   // since you are using boolean status also

            em.merge(student);

            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // --------------------------------------------------
    // Find by ID (NO parents)
    // --------------------------------------------------
    public Student findById(int studentId) {
        EntityManager em = HibernateConfig.getEntityManager();
        Student s = em.find(Student.class, studentId);
        em.close();
        return s;
    }

    // --------------------------------------------------
    // Find by ID WITH parents (IMPORTANT)
    // --------------------------------------------------
    public Student findByIdWithParents(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<Student> q = em.createQuery(
                "SELECT s FROM Student s "
                + "LEFT JOIN FETCH s.studentParents "
                + "WHERE s.studentId = :id",
                Student.class
        );

        q.setParameter("id", studentId);

        Student student = q.getSingleResult();
        em.close();
        return student;
    }

    // --------------------------------------------------
    // Find All (NO parents)
    // --------------------------------------------------
    public List<Student> findAll() {
        EntityManager em = HibernateConfig.getEntityManager();
        List<Student> list = em
                .createNamedQuery("Student.findAll", Student.class)
                .getResultList();
        em.close();
        return list;
    }

    // --------------------------------------------------
    // Find All WITH parents
    // --------------------------------------------------
    public List<Student> findAllWithParents() {
        EntityManager em = HibernateConfig.getEntityManager();

        List<Student> list = em.createQuery(
                "SELECT DISTINCT s FROM Student s "
                + "LEFT JOIN FETCH s.studentParentsList",
                Student.class
        ).getResultList();

        em.close();
        return list;
    }

    // --------------------------------------------------
    // Find by Admission No
    // --------------------------------------------------
    public Student findByAdmissionNo(String admissionNo) {
        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<Student> q = em.createNamedQuery(
                "Student.findByAdmissionNo",
                Student.class
        );
        q.setParameter("admissionNo", admissionNo.trim());
        q.setParameter("currentStatus", "ACTIVE");

        List<Student> list = q.getResultList();
        em.close();

        return list.isEmpty() ? null : list.get(0);
    }

    public Student findByAdmissionNos(String admissionNo) {

        EntityManager em = HibernateConfig.getEntityManager();
        Student student = null;

        try {

            TypedQuery<Student> query = em.createQuery(
                    "SELECT s FROM Student s "
                    + "LEFT JOIN FETCH s.studentParents "
                    + "WHERE s.admissionNo = :admissionNo",
                    Student.class
            );

            query.setParameter("admissionNo", admissionNo);
            student = query.getSingleResult();

        } catch (NoResultException e) {
            student = null;
        } finally {
            em.close();
        }

        return student;
    }

    // --------------------------------------------------
    // Find Active Students
    // --------------------------------------------------
    public List<Student> findActive() {
        EntityManager em = HibernateConfig.getEntityManager();

        List<Student> list = em.createNamedQuery(
                "Student.findByStatus",
                Student.class
        ).setParameter("status", true)
                .getResultList();

        em.close();
        return list;
    }

    // --------------------------------------------------
    // Find by Current Status
    // --------------------------------------------------
    public List<Student> findByCurrentStatus(String currentStatus) {
        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<Student> q = em.createNamedQuery(
                "Student.findByCurrentStatus",
                Student.class
        );
        q.setParameter("currentStatus", currentStatus);

        List<Student> list = q.getResultList();
        em.close();

        return list;
    }

    public List<Student> searchStudents(String text, String type) {

        EntityManager em = HibernateConfig.getEntityManager();

        String field;

        switch (type) {
            case "ADMISSION":
                field = "s.admissionNo";
                break;
            case "FORM":
                field = "s.formNo";
                break;
            case "NIC":
                field = "s.nic";
                break;
            case "NAME":
                field = "s.fullName";
                break;
            default:
                field = "s.admissionNo";
        }

        List<Student> list = em.createQuery(
                "SELECT s FROM Student s "
                + "WHERE s.currentStatus = 'ACTIVE' "
                + "AND " + field + " LIKE :text",
                Student.class
        )
                .setParameter("text", text + "%")
                .getResultList();

        em.close();
        return list;
    }

    public List<Object[]> searchSiblings(String searchText, String searchType) {
        EntityManager em = HibernateConfig.getEntityManager();
        List<Object[]> resultList = new ArrayList<>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

        try {
            String field;
            switch (searchType) {
                case "Mother's NIC":
                    field = "sp.motherNic";
                    break;
                case "Mother's Name":
                    field = "sp.motherName";
                    break;
                case "Mother's Contact":
                    field = "sp.motherContact";
                    break;
                case "Father's NIC":
                    field = "sp.fatherNic";
                    break;
                case "Father's Name":
                    field = "sp.fatherName";
                    break;
                case "Father's Contact":
                    field = "sp.fatherContact";
                    break;
                case "Guardian's NIC":
                    field = "sp.guardianNic";
                    break;
                case "Guardian's Name":
                    field = "sp.guardianName";
                    break;
                case "Guardian's Contact":
                    field = "sp.guardianContact";
                    break;
                default:
                    field = "sp.motherNic";
            }

            // JPQL to fetch students + their parents
            String jpql = "SELECT s FROM Student s JOIN s.studentParents sp "
                    + "WHERE s.currentStatus = :status AND " + field + " LIKE :searchText "
                    + "ORDER BY s.admissionDate DESC";

            Query query = em.createQuery(jpql);
            query.setParameter("status", "ACTIVE");
            query.setParameter("searchText", searchText + "%");
            query.setFirstResult(0);
            query.setMaxResults(10);

            List<Student> students = query.getResultList();

            // Now for each student, pick the latest CourseEnrollment
            for (Student s : students) {
                String latestCourseStatus = "N/A";
                if (s.getCourseEnrollments() != null && !s.getCourseEnrollments().isEmpty()) {
                    latestCourseStatus = s.getCourseEnrollments().stream()
                            .filter(ce -> ce.getStatus() == 1) // active only
                            .max(Comparator.comparingInt(CourseEnrollment::getEnrollmentId))
                            .map(CourseEnrollment::getCourseStatus)
                            .orElse("N/A");
                }

                resultList.add(new Object[]{
                    s.getAdmissionNo(),
                    s.getFullName(),
                    sdf.format(s.getAdmissionDate()),
                    latestCourseStatus
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return resultList;
    }

}
