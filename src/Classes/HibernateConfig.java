package Classes;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author UNKNOWN_UN
 */
@Setter
@Getter
public class HibernateConfig {

    private static EntityManagerFactory emf;

    public static EntityManager getEntityManager() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("madhrasajpa");
        }
        return emf.createEntityManager();
    }
}
