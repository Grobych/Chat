package sample;

import java.io.IOException;
import java.net.*;

/**
 * Created by Alex on 03.02.2017.
 */
public class ChatThread{
    Member user;
    int port;
    public static boolean isWorking;
    DatagramSocket sendSocket, receiveSocket;
    byte inBuffer[] = new byte[1024];
    byte outBuffer[] = new byte[1024];

    public void setPort(int port){
        this.port=port;
    }

    public void setUser(Member user) {
        this.user = user;
    }

    public void run(){
        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(port);
            receiveSocket.setSoTimeout(10000);
            sendSocket.setBroadcast(true);
            isWorking = true;
            receive();

        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public synchronized void sendMessage(String message){
        try {
            DatagramPacket packet = new DatagramPacket(outBuffer,outBuffer.length, InetAddress.getByName("255.255.255.255"),port);
            message = new String(user.getName()+" > "+ message);
            packet.setData(message.getBytes());
            packet.setLength(message.length());
            sendSocket.send(packet);
            MessageList.list.add(message);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(){
        System.out.println("Start listen");
        new Thread() {
            public void run() {
                while (true) {

                    try {

                        synchronized (this){
                            byte[] buf = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(buf, buf.length);
                            receiveSocket.receive(packet);
                            String message = new String(buf);

                            System.out.println("Message: " + message);
                            String msg [] = message.trim().split("\\s+");
                            String name = msg[0];
                            if (!name.equals(user.getName()))
                                MessageList.list.add(message);
                        }

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    finally {
                        if (!MemberThread.isWorking){
                            System.out.println("Chat thread stop");
                            break;
                        }
                    }
                }
            }
        }.start();
    }
}
