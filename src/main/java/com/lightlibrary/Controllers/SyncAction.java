package com.lightlibrary.Controllers;

public interface SyncAction {
    void setTheme(boolean darkMode);
    void setParentController(CustomerDashboardController parentController);
    void setParentController(AdminDashboardController parentController);
    void autoUpdate();
}
