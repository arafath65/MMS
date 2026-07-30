
package Additional;

import java.util.HashSet;
import java.util.Set;

public class PermissionManager {

    private static final Set<String> permissions = new HashSet<>();

    public static void setPermissions(Set<String> list) {
        permissions.clear();
        permissions.addAll(list);
    }

    public static boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public static void clear() {
        permissions.clear();
    }

}
