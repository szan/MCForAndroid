package com.anusiewicz.MCForAndroid.controllers;

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

    private LinkedList<MCRequest> requestQueue = new LinkedList<MCRequest>();
    private TCPClient client;

    public RequestQueueExecutor(TCPClient client) {
         this.client = client;
    }

    @Override
    public void run() {

        while (client.isConnected()){
            client.sendMessage(MCRequest.generateStringFromRequest(requestQueue.peek()));
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void enqueueRequest(MCRequest request) {
        requestQueue.add(request);
    }

    public MCRequest peekQueue() {
        return requestQueue.peek();
    }

    public void pollQueue() {
        requestQueue.poll();
    }

    public void clearQueue() {
        requestQueue.clear();
    }
}
