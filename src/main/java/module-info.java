module com.example.pbo_sekolahminggu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io;
    requires kernel;
    requires layout;
    requires org.apache.poi.ooxml;


    opens com.example.pbo_sekolahminggu to javafx.fxml;
    opens com.example.pbo_sekolahminggu.beans to javafx.fxml, javafx.base;

    exports com.example.pbo_sekolahminggu;
    exports com.example.pbo_sekolahminggu.controllers;
    exports com.example.pbo_sekolahminggu.beans;
    opens com.example.pbo_sekolahminggu.controllers to javafx.fxml;
}