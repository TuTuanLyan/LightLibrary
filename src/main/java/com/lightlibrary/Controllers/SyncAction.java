package com.lightlibrary.Controllers;

import java.util.Objects;

public interface SyncAction {
    void setTheme(boolean darkMode);
    void setParentController(CustomerDashboardController parentController);
}
