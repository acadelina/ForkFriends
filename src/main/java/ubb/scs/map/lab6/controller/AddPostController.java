package ubb.scs.map.lab6.controller;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.Post;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.service.ServicePost;
import ubb.scs.map.lab6.service.ServiceUser;

import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AddPostController {
    private ServicePost servicePost;
    private ServiceUser serviceUser;
    private Stage stage;
    private User user;
    public void setService(ServicePost servicePost, ServiceUser serviceUser, Stage dialogStage,User user) {
        this.servicePost = servicePost;
        this.serviceUser = serviceUser;
        this.stage = dialogStage;
        this.user = user;
    }

    @FXML
    ImageView IMG;
    @FXML
    TextArea description;

    private FileChooser fileChooser = new FileChooser();
    private byte[] data;

    public void initialize() {
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    public void chooseAction(javafx.event.ActionEvent actionEvent) {
        try{
            File file= fileChooser.showOpenDialog(null);
            data= Files.readAllBytes(file.toPath());
            Image image =new Image(new ByteArrayInputStream(data));
            IMG.setImage(image);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void postAction(javafx.event.ActionEvent actionEvent) {

            if(data.length!=0)
            {
                String postDescription = description.getText();
                Post post=new Post(data,postDescription, user.getUsername(), user.getId());
                servicePost.createPost(post);
                stage.close();
            }

    }
}
