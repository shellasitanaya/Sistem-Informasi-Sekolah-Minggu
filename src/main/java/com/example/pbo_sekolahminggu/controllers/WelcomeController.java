package com.example.pbo_sekolahminggu.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Button masukBtn;

    @FXML
    ImageView myImageView;

    public void initialize() {

//        myImageView.setImage(new Image(getClass().getResource("/images/sekolahMingguLogo.png").toString()));
    }

    @FXML
    void SwitchToDashboard(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/com/example/PBO_sekolahMinggu/views/mainWindow.fxml"));
        stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - scene.getWidth()) / 5);
        stage.setY((screenBounds.getHeight() - scene.getHeight()) / 5);
        stage.setResizable(false);
        stage.setMaximized(false);
        stage.setTitle("Dasboard");
        stage.setScene(scene);
        stage.show();


    }
}
