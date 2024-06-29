package com.example.pbo_sekolahminggu;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/example/pbo_sekolahminggu/views/transactional_data/KelasPerTahun.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1100, 600);
        stage.setTitle("Main");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
