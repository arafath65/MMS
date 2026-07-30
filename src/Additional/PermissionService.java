
package Additional;

import JPA_DAO.Settings.UserPermissionDAO;
import java.util.Set;

public class PermissionService {

    public static void reloadCurrentUserPermissions() {

        UserPermissionDAO dao = new UserPermissionDAO();

        Set<String> permissions =
                dao.getPermissionsByRole(
                        UserSession.getRoleId());

        UserSession.setPermissions(permissions);

    }

}