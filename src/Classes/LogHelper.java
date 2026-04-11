package Classes;

import Additional.SystemLog;
import java.util.Date;
import javax.persistence.EntityManager;

public class LogHelper {

    public void log(String module, int moduleId, String action,
            String reference, double amount,
            String description, String user) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            SystemLog log = new SystemLog();
            log.setModule(module);
            log.setModuleId(moduleId);
            log.setAction(action);
            log.setReferenceNo(reference);
            log.setAmount(amount);
            log.setDescription(description);
            log.setUser(user);
            log.setCreatedAt(new Date());
            log.setStatus(1);

            em.persist(log);

            em.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
