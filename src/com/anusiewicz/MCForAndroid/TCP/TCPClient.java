package com.anusiewicz.MCForAndroid.TCP;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 27.06.13
 * Time: 01:50
 * To change this template use File | Settings | File Templates.
 */
import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {

    public interface TcpMessageListener{

        public void onMessage(TCPClient client, String message);
    }

    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private Thread listenThread = null;
    private boolean listening = false;
    private TcpMessageListener listener;


    public String getRemoteHost(){
        if (isConnected()){
            return socket.getInetAddress().toString();
        } else {
            return null;
        }
    }

    public boolean isConnected(){
        return socket.isConnected();
    }


    public void setTcpListener(TcpMessageListener listener)
    {
            this.listener = listener;
    }

    public void removeTcpListener(TcpMessageListener listener)
    {
            this.listener = null;
    }

    public boolean connect(String serverIpOrHost, int port) {
        try {
            socket = new Socket(serverIpOrHost, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.listenThread = new Thread(new Runnable() {

                public void run() {
                    int charsRead = 0;
                    char[] buff = new char[4096];
                    while (listening && charsRead >= 0) {
                        try {
                            charsRead = in.read(buff);
                            if (charsRead > 0) {
                                Log.d("TCPClient", new String(buff).trim());
                                String input = new String(buff).trim();
                                synchronized (listener) {
                                    listener.onMessage(TCPClient.this, input);
                                }
                            }
                        } catch (IOException e) {
                            Log.e("TCPClient", "IOException while reading input stream");
                            listening = false;
                        }
                    }
                }

            });

            this.listening = true;
            this.listenThread.setDaemon(true);
            this.listenThread.start();

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            return false;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            return false;
        } catch (Exception e) {
            System.err.println(e.getMessage().toString());
            return false;
        }
        return true;
    }

    public void sendMessage(String msg) {
        if(out != null)
        {
            out.println(msg);
            out.flush();
        }
    }

    public void disconnect() {
        try {
            if(out != null){
                out.close();
                out = null;
            }
            if(in != null){
                in.close();
                in = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if(this.listenThread != null){
                this.listening = false;
                this.listenThread.interrupt();
            }
        } catch (IOException ioe) {
            System.err.println("I/O error in closing connection.");
        }
    }
}