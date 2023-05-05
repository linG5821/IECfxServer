package jkl.iec.tc.application;


import javafx.application.Platform;

/**
 * @author lsj
 * @date 2023-05-05 15:09
 */
public class Main {
    public static void main(String[] args) {
        Platform.startup(() -> {
            //do nothing
        });
        Server.main(args);
    }
}
