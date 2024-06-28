module com.example.pbo_sekolahminggu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kernel;
    requires layout;
    requires io;
    requires org.apache.poi.ooxml;


    opens com.example.pbo_sekolahminggu to javafx.fxml;
    opens com.example.pbo_sekolahminggu.beans to javafx.base;

    exports com.example.pbo_sekolahminggu;
    exports com.example.pbo_sekolahminggu.controllers;
    opens com.example.pbo_sekolahminggu.controllers to javafx.fxml;
//    exports com.example.pbo_sekolahminggu.controllers.master_data;
//    opens com.example.pbo_sekolahminggu.controllers.master_data to javafx.fxml;
//    exports com.example.pbo_sekolahminggu.controllers.transactional_data;
//    opens com.example.pbo_sekolahminggu.controllers.transactional_data to javafx.fxml;
//    exports com.example.pbo_sekolahminggu.beans.master_data;
//    opens com.example.pbo_sekolahminggu.beans.master_data to javafx.base, javafx.fxml;
//    exports com.example.pbo_sekolahminggu.beans.transactional_data;
//    opens com.example.pbo_sekolahminggu.beans.transactional_data to javafx.base, javafx.fxml;
}