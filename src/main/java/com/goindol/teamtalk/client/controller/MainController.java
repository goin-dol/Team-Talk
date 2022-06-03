package com.goindol.teamtalk.client.controller;

import com.goindol.teamtalk.HelloApplication;
import com.goindol.teamtalk.client.model.*;
import com.goindol.teamtalk.client.service.*;
import com.goindol.teamtalk.client.model.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML public StackPane stackPane;
    @FXML public AnchorPane chatAnchor;
    @FXML public AnchorPane friendAnchor;
    @FXML public TabPane tabContainer;
    @FXML public Tab chatTab;
    @FXML public Tab friendTab;
    @FXML public Tab logoutTab;
    @FXML public ListView chatRoomList;
    @FXML public ListView friendList;
    @FXML public TextField searchFriend;
    @FXML public Button addFriendButton;
    @FXML public ImageView makeChatRoomButton;
    @FXML public ImageView chatRoomListTabImage;
    @FXML public ImageView friendListTabImage;
    @FXML public ImageView logoutTabImage;
    DropShadow dropShadow = new DropShadow();

    chatRoomListDAO chatRoomListDAO = com.goindol.teamtalk.client.service.chatRoomListDAO.getInstance();
    friendDAO friendDAO = com.goindol.teamtalk.client.service.friendDAO.getInstance();
    userDAO userDAO = com.goindol.teamtalk.client.service.userDAO.getInstance();
    public UserDTO userDTO;


    public void showFriendList(){
        List<String> strings = new ArrayList<>();
        ArrayList<FriendDTO> friends = userDAO.getFriendList(userDTO.getNickName());
        if(friends == null) {
            strings.add("");
        }else {

            for(int i = 0; i < friends.size(); i++) {
                strings.add(friends.get(i).getFriendNickName());
            }
        }

        ObservableList<String> fr = FXCollections.observableList(strings);
        friendList.setItems(fr);
        friendList.setCellFactory(param -> new colorListCell());
//
    }

    public void showChatRoomList(){
        List<ChatRoomListDTO> strings = new ArrayList<>();
        if(userDTO != null) {
            ArrayList<ChatRoomListDTO> chatRoom = chatRoomListDAO.getChatRoomName(userDTO.getNickName());
            if(chatRoom != null) {
                for(int i = 0; i < chatRoom.size(); i++) {
                    strings.add(chatRoom.get(i));
                }
            }
            strings.add(new ChatRoomListDTO(0, ""));
        }

        ObservableList<ChatRoomListDTO> chatRoomObservableList = FXCollections.observableArrayList();

        chatRoomObservableList.addAll(strings);

        chatRoomList.setItems(chatRoomObservableList);
    }

    public void openChatRoom(){
        ChatRoomListDTO cr = (ChatRoomListDTO) chatRoomList.getSelectionModel().getSelectedItem();
        if(cr==null)
            return;
        try {

            Stage stage = (Stage) stackPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelloApplication.class.getResource("views/ChatView.fxml"));
            Parent root = (Parent) loader.load();
            ChatController chatController = loader.getController();
            chatController.setuserDTO(userDTO);
            chatController.setChatRoomId(cr.getChatRoom_id());
            chatController.setChatRoomTitle();
            chatController.initialChat();
            stage.setScene(new Scene(root, 400, 600));
            stage.setTitle("Team Talk");
            stage.setOnCloseRequest(event -> chatController.stopClient());
            stage.setOnCloseRequest(event -> {System.exit(0);});
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeChatroom(){
        //TODO : DB에 채팅방 저장
        try {
            Stage stage = (Stage) stackPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/MakeChatRoomView.fxml"));
            Parent root = (Parent) loader.load();
            MakeChatRoomController chatRoomTitleController = loader.getController();
            chatRoomTitleController.setuserDTO(userDTO);
            stage.setScene(new Scene(root, 400, 600));
            stage.setTitle("Team Talk");
            stage.setOnCloseRequest(event -> {System.exit(0);});
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFriend(){

        /*TODO : searchFriend.getText() = 사용자가 입력한 친구 닉네임

        이 부분에 디비랑 비교해서 친구 추가 가능한지 확인하고
        가능하면 추가하고 true 불가능하면 false

        */
        int status =friendDAO.addFriend(userDTO.getNickName(), searchFriend.getText());
        if(status == 1) {
            searchFriend.setText("");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("warning");
            alert.setHeaderText("Friend Add Error");
            alert.setContentText("Already friend");
            alert.show();
        }else if(status == 2){
            searchFriend.setText("");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("warning");
            alert.setHeaderText("Friend Add Error");
            alert.setContentText("Wrong NickName");
            alert.show();
        }else {
            ObservableList<String> friendListItems = friendList.getItems();
            friendListItems.add(searchFriend.getText());
            friendList.setItems(friendListItems);
            searchFriend.setText("");
        }


    }

    public void logOut(){
        //TODO DB에서 현재 사용자 상태 오프라인으로 바꾸고 나의 친구의 친구리스트에서 나를 오프라인으로 변경
        try {
            Stage stage = (Stage) stackPane.getScene().getWindow();
            Parent root = FXMLLoader.load(HelloApplication.class.getResource("views/InitialView.fxml"));
            stage.setScene(new Scene(root, 400, 600));
            stage.setTitle("Team Talk");
            stage.setOnCloseRequest(event -> {System.exit(0);});
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logoutTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                logOut();
            }
        });

       /* searchFriend.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                searchFriend.setText(keyEvent.getText());
            }
        });

        searchFriend.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    addFriend();
                }
            }
        });*/
        makeChatRoomButton.setOnMouseEntered(mouseEvent -> makeChatRoomButton.setEffect(dropShadow));
        makeChatRoomButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                makeChatRoomButton.setEffect(null);
            }
        });


        addFriendButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                addFriend();
            }
        });

        makeChatRoomButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                makeChatroom();
            }
        });
        chatRoomList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                openChatRoom();
            }
        });
    }
    private class colorListCell extends ListCell<String> {
        @Override
        public void updateItem(String obj, boolean empty) {
            super.updateItem(obj, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                Label label = new Label(obj);
                //#TODO 친구 온라인일시 Color.GREEN, 오프라인이면 Color.BLACK
                label.setTextFill(Color.GREEN);
                setGraphic(label);
            }
        }
    }
    public void setuserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}