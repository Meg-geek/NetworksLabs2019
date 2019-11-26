package model.snakeGameNetwork;

import model.networkUtils.BasicMessageInfo;
import model.networkUtils.NetworkApp;
import model.networkUtils.NetworkUser;
import model.networkUtils.PingSender;
import model.snakeGameNetwork.messages.PingMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class PingMessageSender implements Runnable, PingSender {
    private NetworkApp app;
    private List<NetworkUser> usersList = null;
    private AtomicInteger pingDelayMS;

    public PingMessageSender(NetworkApp app, int pingDelay){
        this.app = app;
        pingDelayMS = new AtomicInteger(pingDelay);
    }

    @Override
    public void run() {
        List<NetworkUser> userList = usersList;
        if(userList == null || userList.size() == 0){
            return;
        }
        int pingDelay = pingDelayMS.get();
        long nowMS = new Date().getTime();
        for(NetworkUser user : userList){
            if(nowMS - user.getLastActivity().getTime() > pingDelay){
                app.sendMessage(new PingMessage
                        (new BasicMessageInfo(app.getAndIncrementMsgSeq(),
                        app.getMyID(), user.getID()))
                        , new ArrayList<>(){{add(user);}});
            }
        }
    }

    @Override
    public void setNewPingList(List<NetworkUser> usersList) {
        this.usersList = usersList;
    }

    @Override
    public void setNewPingDelay(int newPingDelay) {
        pingDelayMS.set(newPingDelay);
    }
}
