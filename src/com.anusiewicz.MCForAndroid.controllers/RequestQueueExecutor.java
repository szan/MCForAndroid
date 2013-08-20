package com.anusiewicz.MCForAndroid.controllers;

import android.util.Log;
import com.anusiewicz.MCForAndroid.TCP.TCPClient;
import com.anusiewicz.MCForAndroid.model.MCRequest;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 * Date: 01.08.13
 * Time: 20:54
 */
public class RequestQueueExecutor extends Thread {

    private LinkedList<String> requestQueue = new LinkedList<String>();
    private TCPClient client;

    public RequestQueueExecutor(TCPClient client) {
         this.client = client;
    }

    @Override
    public void run() {
        Log.i("RequestQueueExecutor", "Starting executor thread...") ;
            while ((client != null) && client.isConnected()){
                synchronized (requestQueue) {
                    if (requestQueue.peek()!= null){
                        Log.i("RequestQueueExecutor","Sending first from request queue: " + requestQueue.toString()) ;
                        client.sendMessage(requestQueue.peek());
                        try {
                            synchronized (this) {
                                Log.i("RequestQueueExecutor","Thread waiting...") ;
                                this.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
    }

    public void enqueueRequest(String request) {
            requestQueue.add(request);
    }

    public String peekQueue() {
            return requestQueue.peek();
    }

    public void pollQueue() {

            requestQueue.poll();
    }

    public void clearQueue() {
        requestQueue.clear();
    }
}
