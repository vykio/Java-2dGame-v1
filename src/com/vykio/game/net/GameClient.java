package com.vykio.game.net;

import com.vykio.game.Game;
import com.vykio.game.entities.PlayerMP;
import com.vykio.game.net.packets.Packet;
import com.vykio.game.net.packets.Packet00Login;
import com.vykio.game.net.packets.Packet01Disconnect;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Game game;

    public GameClient(Game game, String ipAddress) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
            //String msg = new String(packet.getData());
            //System.out.println("Server ["+ packet.getAddress().getHostAddress() +":"+ packet.getPort() +"] > " + msg);
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0,2));
        Packet packet;
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                System.out.println("["+ address.getHostAddress() +":"+ port +"] " + ((Packet00Login) packet).getUsername() + " has joined...");
                PlayerMP player = new PlayerMP(game.level, 100,100,  ((Packet00Login) packet).getUsername(), address, port);

                game.level.addEntity(player);

                break;
            case DISCONNECT:
                packet = new Packet01Disconnect(data);
                System.out.println("["+ address.getHostAddress() +":"+ port +"] " + ((Packet01Disconnect) packet).getUsername() + " has left the world...");

                game.level.removePlayerMP(((Packet01Disconnect) packet).getUsername());

                break;
        }
    }

    public void sendData(byte[] data){
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
