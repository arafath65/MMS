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
            .subMenu("Reports"),
            new Item("Settings", "chat.svg")
            .subMenu("Register Course"),};

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

                // STUDENT MANAGEMENT -> NEW ADMISSION
                if (index.length == 2 && index[0] == 1 && index[1] == 0) {
                    dashboard.showPanel("STUDENT_ADMISSION");
                    Dashboard.Dashboard.Main_Lable.setText("NEW ADMISSION");
                    return;
                }

                // STUDENT MANAGEMENT -> Fees Handling
                if (index.length == 2 && index[0] == 1 && index[1] == 1) {
                    dashboard.showPanel("FEES_MANAGEMENT");
                    Dashboard.Dashboard.Main_Lable.setText("FEES MANAGEMENT");
                    return;
                }

                // STUDENT MANAGEMENT -> Reports
                if (index.length == 2 && index[0] == 1 && index[1] == 2) {
                    dashboard.showPanel("");
                    return;
                }

                // SETTINGS -> REGISTER COURSE
                if (index.length == 2 && index[0] == 2 && index[1] == 0) {
                    dashboard.showPanel("REGISTER_COURSE");
                    Dashboard.Dashboard.Main_Lable.setText("REGISTER COURSE");
                    return;
                }

                System.out.println("Drawer menu selected " + Arrays.toString(index));
            }

        });

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
                if (index.length == 1) {
                    // Hide Calendar
                    if (index[0] == 3) {
                        return false;
                    }
                } else if (index.length == 3) {
                    //  Hide Read 4
                    if (index[0] == 1 && index[1] == 1 && index[2] == 4) {
                        return false;
                    }
                }
                return true;
            }
        });

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
