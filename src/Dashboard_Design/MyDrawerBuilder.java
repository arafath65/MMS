package Dashboard_Design;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import raven.drawer.component.DrawerPanel;
import raven.drawer.component.footer.SimpleFooterData;
import raven.drawer.component.footer.SimpleFooterStyle;
import raven.drawer.component.header.SimpleHeaderData;
import raven.drawer.component.header.SimpleHeaderStyle;
import raven.drawer.component.menu.*;
import raven.drawer.component.SimpleDrawerBuilder;
import raven.drawer.component.menu.data.Item;
import raven.drawer.component.menu.data.MenuItem;
import raven.swing.AvatarIcon;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class MyDrawerBuilder extends SimpleDrawerBuilder {

    private Dashboard.Dashboard dashboard;   // reference to main window

    public MyDrawerBuilder(Dashboard.Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        AvatarIcon icon = new AvatarIcon(getClass().getResource("/images/logo.jpeg"), 60, 60, 999);
        icon.setBorder(2);
        return new SimpleHeaderData()
                .setIcon(icon)
                .setTitle("INNOVEX WEB")
                .setDescription("Madhrasa Management System")
                .setHeaderStyle(new SimpleHeaderStyle() {

                    @Override
                    public void styleTitle(JLabel label) {
                        label.putClientProperty(FlatClientProperties.STYLE, ""
                                + "[light]foreground:#FAFAFA");
                    }

                    @Override
                    public void styleDescription(JLabel label) {
                        label.putClientProperty(FlatClientProperties.STYLE, ""
                                + "[light]foreground:#E1E1E1");
                    }
                });
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle("Java Swing Drawer")
                .setDescription("Version 1.1.0")
                .setFooterStyle(new SimpleFooterStyle() {

                    @Override
                    public void styleTitle(JLabel label) {
                        label.putClientProperty(FlatClientProperties.STYLE, ""
                                + "[light]foreground:#FAFAFA");
                    }

                    @Override
                    public void styleDescription(JLabel label) {
                        label.putClientProperty(FlatClientProperties.STYLE, ""
                                + "[light]foreground:#E1E1E1");
                    }
                });
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {

        MenuItem items[] = new MenuItem[]{
            //  new Item.Label("MAIN"),
            new Item("Dashboard", "dashboard.svg"),
            //   new Item.Label("STUDENT MANAGEMENT"),

            new Item("Student Management", "student.svg")
            .subMenu("New Admission")
            .subMenu("Fees Handling")
            .subMenu("Batch Transfer / Payment")
            .subMenu(new Item("Reports")
            .subMenu("Batch Student List")
            .subMenu("Contact Details: Batch-wise")
            .subMenu("Master Student Directory")
            .subMenu("Students Due Report")
            ),
            new Item("Donations (Funds)", "donation.svg")
            .subMenu("One-Time Donation")
            .subMenu("Recurring Donation"),
            
            new Item("Inventory", "invetory.svg")
            .subMenu("Add Inventory")
            .subMenu(new Item("Reports")
            .subMenu("Supplier List")
            .subMenu("Stock Movement")
            .subMenu("Low Stock")
            ),
            new Item("Employee Management", "employee.svg")
            .subMenu("New Employee"),
            new Item("Accounts", "accountsvg.svg")
            .subMenu("Cheque Handling"),
            new Item("Settings", "settingssvg.svg")
            .subMenu("Register Course")
            .subMenu("Additional Payments")
            .subMenu("User Permissions"),};

        SimpleMenuOption simpleMenuOption = new SimpleMenuOption() {
            @Override
            public Icon buildMenuIcon(String path, float scale) {
                FlatSVGIcon icon = new FlatSVGIcon(path, scale);
                FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter();
                colorFilter.add(Color.decode("#969696"), Color.decode("#FAFAFA"), Color.decode("#969696"));
                icon.setColorFilter(colorFilter);
                return icon;
            }
        };

        simpleMenuOption.addMenuEvent(new MenuEvent() {
            @Override
            public void selected(MenuAction action, int[] index) {

                if (dashboard == null) {
                    return;
                }

                // DASHBOARD
                if (index.length == 1 && index[0] == 0) {
                    dashboard.showPanel("DASHBOARD_PANEL");
                    return;
                }

                // ==========================
                // STUDENT MANAGEMENT
                // ==========================
                if (index.length == 2 && index[0] == 1 && index[1] == 0) {
                    dashboard.showPanel("STUDENT_ADMISSION");
                    Dashboard.Dashboard.Main_Lable.setText("NEW ADMISSION");
                    return;
                }

                if (index.length == 2 && index[0] == 1 && index[1] == 1) {
                    dashboard.showPanel("FEES_MANAGEMENT");
                    Dashboard.Dashboard.Main_Lable.setText("FEES MANAGEMENT");
                    return;
                }

                if (index.length == 2 && index[0] == 1 && index[1] == 2) {
                    dashboard.showPanel("BATCH_TRANSFER");
                    Dashboard.Dashboard.Main_Lable.setText("BATCH TRANSFER / PAYMENTS");
                    return;
                }

                if (index.length == 3 && index[0] == 1 && index[1] == 3 && index[2] == 0) {
                    dashboard.showPanel("BATCH/CLASS_STUDENT_REPORT");
                    Dashboard.Dashboard.Main_Lable.setText("BATCH STUDENT LIST");
                    return;
                }

                if (index.length == 3 && index[0] == 1 && index[1] == 3 && index[2] == 1) {
                    dashboard.showPanel("BATCH/CLASS_STUDENT_CONTACT");
                    Dashboard.Dashboard.Main_Lable.setText("CONTACT DETAILS");
                    return;
                }

                if (index.length == 3 && index[0] == 1 && index[1] == 3 && index[2] == 2) {
                    dashboard.showPanel("ENTIRE_STUDENTS_REPORT");
                    Dashboard.Dashboard.Main_Lable.setText("STUDENT DIRECTORY");
                    return;
                }

                if (index.length == 3 && index[0] == 1 && index[1] == 3 && index[2] == 3) {
                    dashboard.showPanel("STUDENT_WISE_DUE");
                    Dashboard.Dashboard.Main_Lable.setText("STUDENTS DUE REPORT");
                    return;
                }

                // ==========================
                // DONATIONS
                // ==========================
                if (index.length == 2 && index[0] == 2 && index[1] == 0) {
                    dashboard.showPanel("ONE-TIME_DONATION");
                    Dashboard.Dashboard.Main_Lable.setText("ONE-TIME DONATION");
                    return;
                }

                if (index.length == 2 && index[0] == 2 && index[1] == 1) {
                    dashboard.showPanel("RECURRING_DONATION");
                    Dashboard.Dashboard.Main_Lable.setText("RECURRING DONATION");
                    return;
                }

                // ==========================
                // INVENTORY
                // ==========================
                if (index.length == 2 && index[0] == 3 && index[1] == 0) {
                    dashboard.showPanel("INVENTORY");
                    Dashboard.Dashboard.Main_Lable.setText("ADD INVENTORY");
                    return;
                }

                if (index.length == 3 && index[0] == 3 && index[1] == 1 && index[2] == 0) {
                    dashboard.showPanel("SUPPLIER_LIST");
                    Dashboard.Dashboard.Main_Lable.setText("SUPPLIER LIST");
                    return;
                }

                if (index.length == 3 && index[0] == 3 && index[1] == 1 && index[2] == 1) {
                    dashboard.showPanel("STOCK_MOVEMENT");
                    Dashboard.Dashboard.Main_Lable.setText("STOCK MOVEMENT");
                    return;
                }

                if (index.length == 3 && index[0] == 3 && index[1] == 1 && index[2] == 2) {
                    dashboard.showPanel("LOW_STOCK");
                    Dashboard.Dashboard.Main_Lable.setText("LOW STOCK");
                    return;
                }

                // ==========================
                // EMPLOYEE MANAGEMENT
                // ==========================
                if (index.length == 2 && index[0] == 4 && index[1] == 0) {
                    dashboard.showPanel("REGISTER_EMPLOYEE");
                    Dashboard.Dashboard.Main_Lable.setText("NEW EMPLOYEE");
                    return;
                }

                // ==========================
                // ACCOUNTS
                // ==========================
                if (index.length == 2 && index[0] == 5 && index[1] == 0) {
                    dashboard.showPanel("CHEQUE_HANDLING");
                    Dashboard.Dashboard.Main_Lable.setText("CHEQUE HANDLING");
                    return;
                }

                // ==========================
                // SETTINGS
                // ==========================
                if (index.length == 2 && index[0] == 6 && index[1] == 0) {
                    dashboard.showPanel("REGISTER_COURSE");
                    Dashboard.Dashboard.Main_Lable.setText("REGISTER COURSE");
                    return;
                }

                if (index.length == 2 && index[0] == 6 && index[1] == 1) {
                    dashboard.showPanel("ADDITIONAL_PAYMENTS");
                    Dashboard.Dashboard.Main_Lable.setText("REGISTER ADDITIONAL PAYMENTS");
                    return;
                }

                if (index.length == 2 && index[0] == 6 && index[1] == 2) {
                    dashboard.showPanel("USER-PERMISSION");
                    Dashboard.Dashboard.Main_Lable.setText("USER PERMISSION");
                    return;
                }

                System.out.println("Drawer menu selected " + Arrays.toString(index));
                System.out.println("Menu Click: " + Arrays.toString(index));
            }
        });
//        simpleMenuOption.addMenuEvent(new MenuEvent() {
//            @Override
//            public void selected(MenuAction action, int[] index) {
//
//                if (dashboard == null) {
//                    return;
//                }
//
//                // DASHBOARD
//                if (index.length == 1 && index[0] == 0) {
//                    dashboard.showPanel("DASHBOARD_PANEL");
//                    return;
//                }
//
        //// STUDENT MANAGEMENT -> NEW ADMISSION
//                if (index.length == 2 && index[0] == 1 && index[1] == 0) {
//                    dashboard.showPanel("STUDENT_ADMISSION");
//                    Dashboard.Dashboard.Main_Lable.setText("NEW ADMISSION");
//                    return;
//                }
//
//// STUDENT MANAGEMENT -> Fees Handling
//                if (index.length == 2 && index[0] == 1 && index[1] == 1) {
//                    dashboard.showPanel("FEES_MANAGEMENT");
//                    Dashboard.Dashboard.Main_Lable.setText("FEES MANAGEMENT");
//                    return;
//                }
//
//// STUDENT MANAGEMENT -> Batch Transfer / Payment
//                if (index.length == 2 && index[0] == 1 && index[1] == 2) {
//                    dashboard.showPanel("BATCH_TRANSFER");
//                    Dashboard.Dashboard.Main_Lable.setText("BATCH TRANSFER / PAYMENTS");
//                    return;
//                }
//
//// STUDENT MANAGEMENT -> Reports -> Batch Student List
//                if (index.length == 3 && index[0] == 1 && index[1] == 3 && index[2] == 0) {
//                    dashboard.showPanel("BATCH/CLASS_STUDENT_REPORT");
//                    Dashboard.Dashboard.Main_Lable.setText("BATCH STUDENT LIST");
//                    return;
//                }
//
//// STUDENT MANAGEMENT -> Reports -> Contact Details
//                if (index.length == 3 && index[0] == 1 && index[1] == 3 && index[2] == 1) {
//                    dashboard.showPanel("BATCH/CLASS_STUDENT_CONTACT");
//                    Dashboard.Dashboard.Main_Lable.setText("CONTACT DETAILS");
//                    return;
//                }
//
//// STUDENT MANAGEMENT -> Reports -> Master Student Directory
//                if (index.length == 3 && index[0] == 1 && index[1] == 3 && index[2] == 2) {
//                    dashboard.showPanel("ENTIRE_STUDENTS_REPORT");
//                    Dashboard.Dashboard.Main_Lable.setText("STUDENT DIRECTORY");
//                    return;
//                }
//
//// STUDENT MANAGEMENT -> Reports -> Students Due Report
//                if (index.length == 3 && index[0] == 1 && index[1] == 3 && index[2] == 3) {
//                    dashboard.showPanel("STUDENT_WISE_DUE");
//                    Dashboard.Dashboard.Main_Lable.setText("STUDENTS DUE REPORT");
//                    return;
//                }
//
//// DONATIONS -> One-Time Donation
//                if (index.length == 2 && index[0] == 2 && index[1] == 0) {
//                    dashboard.showPanel("ONE-TIME_DONATION");
//                    Dashboard.Dashboard.Main_Lable.setText("ONE-TIME DONATION");
//                    return;
//                }
//
//// DONATIONS -> Recurring Donation
//                if (index.length == 2 && index[0] == 2 && index[1] == 1) {
//                    dashboard.showPanel("RECURRING_DONATION");
//                    Dashboard.Dashboard.Main_Lable.setText("RECURRING DONATION");
//                    return;
//                }
//
//// INVENTORY -> Add Inventory
//                if (index.length == 2 && index[0] == 3 && index[1] == 0) {
//                    dashboard.showPanel("INVENTORY");
//                    Dashboard.Dashboard.Main_Lable.setText("ADD INVENTORY");
//                    return;
//                }
//
//// INVENTORY -> Reports -> Supplier List
//                if (index.length == 3 && index[0] == 3 && index[1] == 1 && index[2] == 0) {
//                    dashboard.showPanel("SUPPLIER_LIST");
//                    Dashboard.Dashboard.Main_Lable.setText("SUPPLIER LIST");
//                    return;
//                }
//
//// INVENTORY -> Reports -> Stock Movement
//                if (index.length == 3 && index[0] == 3 && index[1] == 1 && index[2] == 1) {
//                    dashboard.showPanel("STOCK_MOVEMENT");
//                    Dashboard.Dashboard.Main_Lable.setText("STOCK MOVEMENT");
//                    return;
//                }
//
//// INVENTORY -> Reports -> Low Stock
//                if (index.length == 3 && index[0] == 3 && index[1] == 1 && index[2] == 2) {
//                    dashboard.showPanel("LOW_STOCK");
//                    Dashboard.Dashboard.Main_Lable.setText("LOW STOCK");
//                    return;
//                }
//
//// ACCOUNTS -> Cheque Handling
//                if (index.length == 2 && index[0] == 4 && index[1] == 0) {
//                    dashboard.showPanel("CHEQUE_HANDLING");
//                    Dashboard.Dashboard.Main_Lable.setText("CHEQUE HANDLING");
//                    return;
//                }
//
//// SETTINGS -> Register Course
//                if (index.length == 2 && index[0] == 5 && index[1] == 0) {
//                    dashboard.showPanel("REGISTER_COURSE");
//                    Dashboard.Dashboard.Main_Lable.setText("REGISTER COURSE");
//                    return;
//                }
//
//// SETTINGS -> Additional Payments
//                if (index.length == 2 && index[0] == 5 && index[1] == 1) {
//                    dashboard.showPanel("ADDITIONAL_PAYMENTS");
//                    Dashboard.Dashboard.Main_Lable.setText("REGISTER ADDITIONAL PAYMENTS");
//                    return;
//                }
//                
//                // SETTINGS -> User Permissions
//                if (index.length == 2 && index[0] == 5 && index[1] == 2) {
//                    dashboard.showPanel("USER-PERMISSION");
//                    Dashboard.Dashboard.Main_Lable.setText("USER PERMISSION");
//                    return;
//                }
//
//                System.out.println("Drawer menu selected " + Arrays.toString(index));
//                System.out.println("Menu Click: " + Arrays.toString(index));
//            }
//
//        });

        simpleMenuOption.setMenuStyle(new SimpleMenuStyle() {
            @Override
            public void styleMenu(JComponent component) {
                component.putClientProperty(FlatClientProperties.STYLE,
                        "background:$Drawer.background");
            }

            @Override
            public void styleMenuPanel(JPanel panel, int[] index) {
                panel.putClientProperty(FlatClientProperties.STYLE,
                        "background:$Drawer.background");
            }

            @Override
            public void styleMenuItem(JButton menu, int[] index) {
                menu.putClientProperty(FlatClientProperties.STYLE,
                        ""
                        + "foreground:#878d92;"
                        + "hoverBackground:#b5fc58;"
                        + "hoverForeground:#000000;"
                        + "selectedBackground:#2A7A6D;"
                        + "arc:10"
                );
            }

            @Override
            public void styleLabel(JLabel label) {
                label.putClientProperty(FlatClientProperties.STYLE,
                        "foreground:#CFE7E3");
            }
        });

        simpleMenuOption.setMenuValidation(new MenuValidation() {
            @Override
            public boolean menuValidation(int[] index) {
                System.out.println(Arrays.toString(index));
                return true;
            }
        });

//        simpleMenuOption.setMenuValidation(new MenuValidation() {
//            @Override
//            public boolean menuValidation(int[] index) {
//                if (index.length == 1) {
//                    // Hide Calendar
//                    if (index[0] == 5) {
//                        return false;
//                    }
//                } else if (index.length == 10) {
//                    //  Hide Read 4
//                    if (index[0] == 1 && index[1] == 1 && index[2] == 4) {
//                        return false;
//                    }
//                }
//                return true;
//            }
//        });
        simpleMenuOption.setMenus(items)
                .setBaseIconPath("images")
                .setIconScale(0.2f);
        return simpleMenuOption;
    }

    @Override
    public void build(DrawerPanel drawerPanel) {
        drawerPanel.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Drawer.background");
    }

    @Override
    public int getDrawerWidth() {
        return 275;
    }
}
