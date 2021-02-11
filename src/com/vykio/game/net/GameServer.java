package com.vykio.game.net;

import com.vykio.game.Game;
import com.vykio.game.entities.PlayerMP;
import com.vykio.game.net.packets.Packet;
import com.vykio.game.net.packets.Packet00Login;
import com.vykio.game.net.packets.Packet01Disconnect;
import com.vykio.game.net.packets.Packet02Move;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameServer extends Thread {

    private DatagramSocket socket;
    private Game game;
    private List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();

    public GameServer(Game game) {
        this.game = game;
        try {
            this.socket = new DatagramSocket(1331);
        } catch (SocketException e) {
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
            /*String msg = new String(packet.getData());
            System.out.println("Client ["+ packet.getAddress().getHostAddress() +":"+ packet.getPort() +"] > " + msg);

            if (msg.trim().equalsIgnoreCase("ping")) {
                sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
            }*/

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
                System.out.println("["+ address.getHostAddress() +":"+ port +"] " + ((Packet00Login) packet).getUsername() + " has connected...");
                PlayerMP player = new PlayerMP(game.level, 100,100,  ((Packet00Login) packet).getUsername(), address, port);

                this.addConnection(player,  (Packet00Login) packet);

                break;
            case DISCONNECT:
                packet = new Packet01Disconnect(data);
                System.out.println("["+ address.getHostAddress() +":"+ port +"] " + ((Packet01Disconnect) packet).getUsername() + " has left...");

                this.removeConnection((Packet01Disconnect) packet);

                break;
            case MOVE:
                System.out.println("move");
                packet = new Packet02Move(data);
                this.handleMove(((Packet02Move) packet));
        }
    }

    private void handleMove(Packet02Move packet) {
        if (getPlayerMP(packet.getUsername()) != null) {
            int index = getPlayerMPIndex(packet.getUsername());
            PlayerMP player = this.connectedPlayers.get(index);
            player.x = packet.getX();
            player.y = packet.getY();
            player.setMoving(packet.isMoving());
            player.setMovingDir(packet.getMovingDir());
            player.setNumSteps(packet.getNumSteps());
            packet.writeData(this);
        }
    }


    public void addConnection(PlayerMP player, Packet00Login packet) {
        boolean alreadyConnected = false;
        Packet00Login packetLogin = packet;
        for (PlayerMP p : this.connectedPlayers) {
            packetLogin = packet;
            if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
                if (p.ipAddress == null) {
                    p.ipAddress = player.ipAddress;
                }
                if (p.port == -1) {
                    p.port = player.port;
                }
                alreadyConnected = true;

            } else {
                sendData(packetLogin.getData(), p.ipAddress, p.port);
                packetLogin = new Packet00Login(p.getUsername());
                sendData(packetLogin.getData(), player.ipAddress, player.port);
            }
        }
        if (!alreadyConnected) {
            this.connectedPlayers.add(player);
            //packet.writeData(this);
        }
    }

    public void removeConnection(Packet01Disconnect packet) {
        this.connectedPlayers.remove(getPlayerMPIndex(packet.getUsername()));
        packet.writeData(this);
    }

    public PlayerMP getPlayerMP(String username) {
        for (PlayerMP player : this.connectedPlayers){
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public int getPlayerMPIndex(String username) {
        int index = 0;
        for (PlayerMP player : this.connectedPlayers){
            if (player.getUsername().equals(username)) {
                break;
            }
            index++;
        }
        return index;
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port){
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers) {
            sendData(data, p.ipAddress, p.port);
        }
    }
}
