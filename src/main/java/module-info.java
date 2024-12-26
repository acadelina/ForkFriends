module ubb.scs.map.lab6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.httpserver;
    requires java.desktop;
    requires org.postgresql.jdbc;
    requires spring.security.crypto;


    opens ubb.scs.map.lab6 to javafx.fxml;
    exports ubb.scs.map.lab6;
    exports ubb.scs.map.lab6.controller to javafx.fxml;

    opens ubb.scs.map.lab6.controller to javafx.fxml;
    opens ubb.scs.map.lab6.views to javafx.fxml;
    opens ubb.scs.map.lab6.domain to javafx.base;
}