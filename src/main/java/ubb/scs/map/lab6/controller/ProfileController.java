package ubb.scs.map.lab6.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.lab6.domain.Post;
import ubb.scs.map.lab6.domain.User;
import ubb.scs.map.lab6.service.*;
import ubb.scs.map.lab6.utils.observer.Observer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class ProfileController implements Observer {
    private ServiceUser serviceUser;
    private ServiceRequest serviceRequest;
    private ServiceFriendship serviceFriendship;
    private ServiceMessage serviceMessage;
    private ServicePost servicePost;
    private Stage stage;
    private Stage oldStage;
    private User user;
    private Post postC;
    private Post selectedPostToDelete;
    @FXML
    private TextField email;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;

    @FXML
    private Button btnAddPhoto;
    @FXML
    private Button updateButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ImageView ProfilePhoto;

    @FXML
    ListView PostsList;

    @FXML
    private TextField username;

    public void setService(ServiceUser serviceUser,ServiceFriendship serviceFriendship, ServiceRequest serviceRequest,ServiceMessage serviceMessage,ServicePost servicePost, Stage stage,Stage oldStage,User user) {
        this.serviceUser = serviceUser;
        this.serviceFriendship = serviceFriendship;
        this.serviceRequest = serviceRequest;
        this.serviceMessage = serviceMessage;
        this.servicePost = servicePost;
        this.user = user;
        this.stage = stage;
        this.oldStage = oldStage;
        servicePost.addObserver(this);
        serviceUser.addObserver(this);
        email.setText(user.getEmail());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        username.setText(user.getUsername());
        username.setEditable(false);
        username.setStyle("-fx-font: Lucida Bright;"+"-fx-font-weight: bold;"+"-fx-text-fill: #5a7561;"+"-fx-background-color: transparent;"+"-fx-font-size: 20;"+"-fx-alignment: LEFT");
        initModel();

    }


    public VBox createPost(Post post)
    {
        VBox box = new VBox();
        box.setOnMouseClicked(event->{
            selectedPostToDelete = post;
        });
        box.setStyle("-fx-background-color: #fffef4; "+"-fx-border-color: #5a7561");
        Image image =new Image(new ByteArrayInputStream(post.getImage()));
        ImageView imageView=new ImageView(image);
        imageView.setFitHeight(330);
        imageView.setFitWidth(330);
        imageView.setStyle("-fx-border-color: #5a7561");
        HBox userB=new HBox();
        userB.setStyle("-fx-background-color: #fffef4; "+"-fx-border-color: #5a7561");
        Image image1;
        if(user.getPhoto()==null)
        {
            image1=new Image((getClass().getResource("/images/ForkFriends(2).png")).toString());
        }
        else
            image1=new Image(new ByteArrayInputStream(user.getPhoto()));

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

    public void handleUpdate(javafx.event.ActionEvent event) {
        try {
            String email = this.email.getText();
            String firstName = this.firstName.getText();
            String lastName = this.lastName.getText();
            serviceUser.updateUser(user.getId(), firstName, lastName, email);
            MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION,"Update","User updated successfully");
            stage.close();
        }catch(ServiceException e)
        {
            MessageAlert.showErrorMessage(stage, e.getMessage());
        }

    }

    public void handleLogout(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Login");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setResizable(true);
            Scene scene = new Scene(root,600,385);
            dialogStage.setScene(scene);
            dialogStage.sizeToScene();

            LogInController loginController = loader.getController();
            loginController.setService(serviceUser,serviceFriendship,serviceRequest,serviceMessage,servicePost,stage);
            stage.close();
            oldStage.close();
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initModel(){
        PostsList.getItems().clear();
        List<Post> posts=servicePost.getPostsByUser(user);
        posts.forEach(p->{
            VBox box=createPost(p);
            box.setOnMouseClicked(event->{
                selectedPostToDelete=p;
            });
            PostsList.getItems().add(box);
        });
        this.selectedPostToDelete=null;
        if(user.getPhoto()!=null) {
            btnAddPhoto.setStyle("-fx-background-color: transparent;");
            btnAddPhoto.setText("");
            Image image = new Image(new ByteArrayInputStream(user.getPhoto()));
            ProfilePhoto.setImage(image);
            ProfilePhoto.getStyleClass().add("profilePhoto");
        }


    }

    public void handleMyPosts(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/myPosts-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("My Posts");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setResizable(true);
            Scene scene = new Scene(root,488,416);
            dialogStage.setScene(scene);


            MyPostsController loginController = loader.getController();
            loginController.setService(servicePost,serviceUser,stage,user);
            stage.close();
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDeletePost(javafx.event.ActionEvent event)
    {
        if(selectedPostToDelete==null)
        {
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR,"Post delete error","No post selected");

        }
        else
        {
            servicePost.deletePost(selectedPostToDelete.getId());
            initModel();
        }


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
            Scene scene = new Scene(root,538,510);
            dialogStage.setScene(scene);

            AddPostController addPostController = loader.getController();
            addPostController.setService(servicePost,serviceUser,dialogStage,user);

            dialogStage.show();
            initModel();

        } catch (IOException e) {
            e.printStackTrace();
        }
        initModel();
    }

    public void handleAddPhoto(javafx.event.ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/add-photo-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("AddPhoto");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.setResizable(true);
            Scene scene = new Scene(root,510,465);
            dialogStage.setScene(scene);

            AddPhotoController addPostController = loader.getController();
            addPostController.setService(servicePost,serviceUser,dialogStage,user);

            dialogStage.setOnHidden(e->initModel());
            dialogStage.show();



        } catch (IOException e) {
            e.printStackTrace();
        }
        initModel();
    }
    @Override
    public void update() {
        initModel();
    }
}
