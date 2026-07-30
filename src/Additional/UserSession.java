package Additional;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

public class UserSession {

    private static int loginId;
    private static int employeeId;
    private static int roleId;

    private static String username;
    private static String roleName;
    private static String employeeName;

    private static Set<String> permissions = new HashSet<>();

    public static void initialize(
            int login,
            int employee,
            int role,
            String user,
            String roleText,
            String empName,
            Set<String> permissionList) {

        loginId = login;
        employeeId = employee;
        roleId = role;

        username = user;
        roleName = roleText;
        employeeName = empName;

        permissions.clear();
        permissions.addAll(permissionList);
    }

    

    public static boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public static boolean checkPermission(String permission) {

        if (permissions.contains(permission)) {
            return true;
        }

        JOptionPane.showMessageDialog(
                null,
                "Access Denied!\n\nYou don't have permission to access this module.",
                "Permission Denied",
                JOptionPane.WARNING_MESSAGE
        );

        return false;
    }
    
    public static void setPermissions(Set<String> permissionList) {

        permissions.clear();
        permissions.addAll(permissionList);

    }

    public static int getLoginId() {
        return loginId;
    }

    public static int getEmployeeId() {
        return employeeId;
    }

    public static int getRoleId() {
        return roleId;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRoleName() {
        return roleName;
    }

    public static String getEmployeeName() {
        return employeeName;
    }

    public static void clear() {

        permissions.clear();

        loginId = 0;
        employeeId = 0;
        roleId = 0;

        username = null;
        roleName = null;
        employeeName = null;

    }

}
