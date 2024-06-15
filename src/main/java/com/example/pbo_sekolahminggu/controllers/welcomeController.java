package com.example.pbo_sekolahminggu.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class welcomeController {
    @FXML
    ImageView myImageView;

    public void initialize() {
        myImageView.setImage(new Image("images\\sekolahMingguLogo.png"));
    }


}
