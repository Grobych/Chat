package sample;

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



    byte buffer[] = new byte[1024];
    private MemberList memberList = new MemberList();
    private MemberThread memberThread;
    private ChatThread chatThread;

    DatagramSocket socket;
    DatagramPacket inPacket, outPacket, memberPacket;
    private int portChat = 19999;
    private int portMember = 20000;

    @FXML
    public synchronized void enterMessage(){
        chatThread.sendMessage(inputTextField.getText());
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
        chatTab.setDisable(false);
        addToLog("Start hello thread...");
        startHelloThread();
        addToLog("Start chat thread...");
        startChatThread();
    }

    @FXML
    public void addToLog(String msg){
        logArea.setText(logArea.getText()+"\n"+msg);
    }

    @FXML
    public synchronized void addToChat(String msg){
        textArea.setText(textArea.getText()+"\n"+msg);
    }



    public void tableInit(){
        contactTable.refresh();
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

    public void startHelloThread(){
        memberThread = new MemberThread();
        memberThread.setPort(portMember);
        memberThread.setMemberList(memberList);
        memberThread.run();
    }

    public void startChatThread(){
        chatThread = new ChatThread();
        chatThread.setPort(portChat);
        chatThread.setUser(memberList.get(0));
        chatThread.run();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableInit();
        chatInit();
    }

    @Override
    public void close() throws IOException {
        memberThread.isWorking = false;
        chatThread.isWorking = false;
    }
}
