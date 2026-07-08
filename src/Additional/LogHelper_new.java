package Additional;

import Classes.*;
import Additional.SystemLog;
import java.util.Date;
import javax.persistence.EntityManager;

public class LogHelper_new {

    public void log(String module, String subModule, int moduleId, String action,
            String reference, String userDesc, String description, String user) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            SystemLog_new log = new SystemLog_new();
            log.setModule(module);
            log.setSubModule(subModule);
            log.setModuleId(moduleId);
            log.setAction(action);
            log.setReferenceNo(reference);
            log.setUserViewDescription(userDesc);
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
