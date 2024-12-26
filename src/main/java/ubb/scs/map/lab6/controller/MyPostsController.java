package ubb.scs.map.lab6.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.Post;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.service.ServicePost;
import ubb.scs.map.lab6.service.ServiceUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class MyPostsController {
    ServicePost servicePost;
    ServiceUser serviceUser;
    User user;
    Stage stage;
    Post postC;

    @FXML
    ListView PostsList;

    public void setService(ServicePost servicePost, ServiceUser serviceUser,Stage stage, User user) {
        this.servicePost = servicePost;
        this.serviceUser = serviceUser;
        this.user=user;
        this.stage=stage;
        initModel();
    }

    public VBox createPost(Post post)
    {
        VBox box = new VBox();
        box.setStyle("-fx-background-color: #fffef4; "+"-fx-border-color: #5a7561");
        Image image =new Image(new ByteArrayInputStream(post.getImage()));
        ImageView imageView=new ImageView(image);
        imageView.setFitHeight(330);
        imageView.setFitWidth(330);
        imageView.setStyle("-fx-border-color: #5a7561");
        HBox userB=new HBox();
        userB.setStyle("-fx-background-color: #fffef4; "+"-fx-border-color: #5a7561");
        Image image1=new Image((getClass().getResource("/images/ForkFriends(2).png")).toString());
        ImageView imageView1=new ImageView(image1);
        imageView1.setFitHeight(40);
        imageView1.setFitWidth(40);
        userB.getChildren().add(imageView1);
        Label label1=new Label(post.getAuthor());
        userB.getChildren().add(label1);
        label1.setStyle("-fx-background-color: #fffef4; " +
                "-fx-text-fill:  #b84c65; " +
                "-fx-padding: 10px; " +
                "-fx-border-color:  #b84c65;" +
                "-fx-border-radius: 15px;" +
                "-fx-background-radius: 15px;");
        box.getChildren().add(userB);
        box.getChildren().add(imageView);
        Label label2=new Label(post.getDescription());
        label2.setStyle("-fx-background-color: #5a7561; " +"-fx-text-fill: #fffef4");
        label2.setPrefWidth(347);
        HBox likeB=new HBox();
        likeB.setStyle("-fx-background-color: #fffef4; "+"-fx-border-color: #5a7561");
        Image image2=new Image((getClass().getResource("/images/ForkFriends(6).png")).toString());
        ImageView imageView2=new ImageView(image2);
        imageView2.setFitHeight(40);
        imageView2.setFitWidth(40);
        Button likeButton=new Button();
        likeButton.setStyle("-fx-background-color: transparent");
        likeButton.setGraphic(imageView2);
        likeButton.setOnAction(event->{
            this.postC=post;
            likeButtonHandle(event);
        });
        likeButton.setPrefHeight(40);
        likeButton.setPrefWidth(40);
        likeB.getChildren().add(likeButton);
        String likes= String.valueOf(post.getLikes().size());
        Label label3=new Label(likes);
        label3.setStyle("-fx-font-size: 20px;");
        label3.setPrefWidth(40);
        label3.setPrefHeight(40);
        likeB.getChildren().add(label3);
        label3.setStyle("-fx-background-color: #fffef4; " +"-fx-text-fill: #b84c65");
        box.getChildren().add(label2);
        box.getChildren().add(likeB);
        return box;


    }


    public void likeButtonHandle(javafx.event.ActionEvent event)
    {
        servicePost.addLike(postC,user.getId());
        initModel();
    }

    public void initModel(){
        PostsList.getItems().clear();
        List<Post> posts=servicePost.getPostsByUser(user);
        posts.forEach(p->{
            VBox box=createPost(p);
            PostsList.getItems().add(box);
        });

    }

    public void handleDeletePost(javafx.event.ActionEvent event)
    {

    }

    public void handleAddPost(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/addpost-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("AddPost");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setResizable(true);
            Scene scene = new Scene(root,538,400);
            dialogStage.setScene(scene);

            AddPostController addPostController = loader.getController();
            addPostController.setService(servicePost,serviceUser,dialogStage,user);

            dialogStage.show();
            initModel();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

