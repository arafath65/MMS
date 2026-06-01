
package Dashboard_Design;

import Panels.Fees_Management;

public class AppNavigator {

    private static Dashboard.Dashboard dashboard;
    private static Fees_Management feesPanel;

    public static void init(Dashboard.Dashboard db) {
        dashboard = db;
    }
    
    public static void setFeesPanel(Fees_Management panel) {
        feesPanel = panel;
    }

    public static Fees_Management getFeesPanel() {
        return feesPanel;
    }

    public static void openFeesManagement() {
        if (dashboard != null) {
            dashboard.showPanel("FEES_MANAGEMENT");
            Dashboard.Dashboard.Main_Lable.setText("FEES MANAGEMENT");
        }
    }

    public static void openPanel(String name, String title) {
        if (dashboard != null) {
            dashboard.showPanel(name);
            Dashboard.Dashboard.Main_Lable.setText(title);
        }
    }
}