package ru.atom.lecture09.serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by gammaker on 22.04.2017.
 */
public class ObjectClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int portNum = 12345;
        //Socket
        Socket client = new Socket("wtfis.ru", portNum);
        //client.connect(new InetSocketAddress());
        //PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
        out.writeObject(new Packet("Вольнов Александр"));
        out.close();
        client.close();
        //client.send
    }
}
