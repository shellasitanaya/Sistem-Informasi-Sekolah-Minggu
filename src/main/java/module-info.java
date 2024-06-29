module com.example.pbo_sekolahminggu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kernel;
    requires layout;
    requires io;
    requires org.apache.poi.ooxml;


    opens com.example.pbo_sekolahminggu to javafx.fxml;


    exports com.example.pbo_sekolahminggu;
    exports com.example.pbo_sekolahminggu.controllers;
    opens com.example.pbo_sekolahminggu.controllers to javafx.fxml;
    exports com.example.pbo_sekolahminggu.controllers.master.data;
    opens com.example.pbo_sekolahminggu.controllers.master.data to javafx.fxml;
    exports com.example.pbo_sekolahminggu.controllers.transactional.data;
    opens com.example.pbo_sekolahminggu.controllers.transactional.data to javafx.fxml;
    exports com.example.pbo_sekolahminggu.beans.master.data;
    opens com.example.pbo_sekolahminggu.beans.master.data to javafx.base, javafx.fxml;
    exports com.example.pbo_sekolahminggu.beans.transactional.data;
    opens com.example.pbo_sekolahminggu.beans.transactional.data to javafx.base, javafx.fxml;
}