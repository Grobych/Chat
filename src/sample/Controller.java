package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Controller implements Initializable, Closeable {
    @FXML
    TableView contactTable;
    @FXML
    TableColumn tableNameColumn, tableAddressColumn;
    @FXML
    TextArea textArea, logArea;
    @FXML
    TextField inputTextField, inputNameField;
    @FXML
    Tab logTab, chatTab;
    @FXML
    ChoiceBox choiceBox;



    byte buffer[] = new byte[1024];
    private MemberList memberList = new MemberList();
    private MemberThread memberThread;
    private ChatThread chatThread;
    private MulticastChatThread multicastChatThread;

    DatagramSocket socket;
    DatagramPacket inPacket, outPacket, memberPacket;
    private int portChat = 19999;
    private int portMember = 20000;

    @FXML
    public synchronized void enterMessage(){
        switch (choiceBox.getValue().toString()){
            case "255.255.255.255": chatThread.sendMessage(inputTextField.getText()); break;
            case "224.0.0.50" :      multicastChatThread.sendMessage(inputTextField.getText()); break;
        }
        inputTextField.clear();
    }

    public void initialisation(){
        String name = inputNameField.getText();
        Member user = new Member();
        if (name!=null){
            try {
                InetAddress address = InetAddress.getLocalHost();
                user.setName(name);
                user.setAddress(address);
                memberList.add(user);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        addToLog("Username: "+user.getName());
        addToLog("Address:"+user.getAddress().toString());

        try {
            addToLog("Start hello thread...");
            startHelloThread();
            addToLog("Start chat thread...");
            startChatThread();
            chatTab.setDisable(false);
            choiceBox.setDisable(true);
        }catch (BindException e){
            addToLog("Bind fail");
            System.out.println(e.getMessage());
        }

    }

    @FXML
    public void addToLog(String msg){
        logArea.setText(logArea.getText()+"\n"+msg);
    }

    @FXML
    public synchronized void addToChat(String msg){
        textArea.appendText("\n"+msg);
    }


    public void tableInit() {
        contactTable.refresh();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    contactTable.refresh();
                    if (!MemberThread.isWorking) break;
                }
            }
        }).start();

        tableNameColumn.setCellValueFactory(new PropertyValueFactory<Member,String>("Name"));
        tableAddressColumn.setCellValueFactory(new PropertyValueFactory<Member,String>("Address"));
        contactTable.setItems(memberList.getMembers());
    }

    public void chatInit(){
        MessageList.list.addListener(new ListChangeListener<String>() {
            @Override
            public synchronized void onChanged(Change<? extends String> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutate
                        }
                    } else if (c.wasUpdated()) {
                        //update item
                    } else {
                        for (String additem : c.getAddedSubList()) {
                            addToChat(additem);
                        }
                    }
                }
            }
        });
    }

    public void startHelloThread() throws BindException{
        memberThread = new MemberThread();
        memberThread.setPort(portMember);
        memberThread.setMemberList(memberList);
        memberThread.run();
    }

    public void startChatThread() throws BindException{
        switch (choiceBox.getValue().toString())
        {
            case "255.255.255.255":
            {
                addToLog("Broadcast mode");
                chatThread = new ChatThread();
                chatThread.setPort(portChat);
                chatThread.setUser(memberList.get(0));
                chatThread.run();
                break;
            }
            case "224.0.0.50":
            {
                addToLog("Multicast mode");
                multicastChatThread = new MulticastChatThread();
                multicastChatThread.setPort(portChat);
                multicastChatThread.setUser(memberList.get(0));
                multicastChatThread.run();
                break;
            }
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableInit();
        chatInit();
    }

//    @FXML
//    public void textAreaInit(){
//        textArea.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//
//            }
//        });
//     }


    @Override
    public void close() throws IOException {
        memberThread.isWorking = false;
        chatThread.isWorking = false;
    }
}
