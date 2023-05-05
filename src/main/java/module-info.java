module jkl.iecserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
            
                            
    opens jkl.iec.tc.application to javafx.fxml,javafx.graphics,javafx.base;
    opens jkl.iec.tc.fxml to javafx.fxml;
    opens jkl.iec.net.fxml to javafx.fxml;
    exports jkl.iec.tc.bean.type;
    exports jkl.iec.net.sockets;
    exports jkl.iec.net.fx.gui;
    exports jkl.iec.tc.bean.utils;
    exports jkl.iec.tc.fx.gui;
}