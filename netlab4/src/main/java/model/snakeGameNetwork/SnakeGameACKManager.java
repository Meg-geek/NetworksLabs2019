package model.snakeGameNetwork;

import model.networkUtils.ACKManager;
import model.networkUtils.Message;
import model.networkUtils.NetworkApp;
import model.networkUtils.NetworkUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SnakeGameACKManager implements ACKManager, Runnable {
    private Map<Message, List<NetworkUser>> messageUsersListMap = new ConcurrentHashMap<>();
    private Map<Long, Message> numberMessageMap = new ConcurrentHashMap<>();
    private AtomicInteger nodeTimeoutMS;
    private NetworkApp networkApp;

    SnakeGameACKManager(int nodeTimeoutMS, NetworkApp networkApp){
        this.nodeTimeoutMS = new AtomicInteger(nodeTimeoutMS);
        this.networkApp = networkApp;
    }

    @Override
    public void run() {
        if(messageUsersListMap.size() == 0){
            return;
        }
        List<Message> messagesRemoveList = new ArrayList<>();
        for(Map.Entry<Message, List<NetworkUser>> messageListEntry : messageUsersListMap.entrySet()){
            refreshList(messageListEntry.getValue());
            if(messageListEntry.getValue().size() == 0){
                messagesRemoveList.add(messageListEntry.getKey());
            } else {
                networkApp.sendMessage(messageListEntry.getKey(), messageListEntry.getValue());
            }
        }
        for(Message messageRemove : messagesRemoveList){
            messageUsersListMap.remove(messageRemove);
            numberMessageMap.remove(messageRemove.getNumber());
        }
    }

    private void refreshList(List<NetworkUser> usersList){
        int nodeTimeout = nodeTimeoutMS.get();
        long nowMS = new Date().getTime();
        List<NetworkUser> usersRemoveList = new ArrayList<>();
        for(NetworkUser user : usersList){
            if(nowMS - user.getLastActivity().getTime() < nodeTimeout){
                usersRemoveList.add(user);
            }
        }
        for(NetworkUser userRemove : usersRemoveList){
            usersList.remove(userRemove);
        }
    }

    @Override
    public void ackRecv(long messageNumber, NetworkUser user) {
        Message message = numberMessageMap.get(messageNumber);
        if(message != null){
            List<NetworkUser> networkUserList = messageUsersListMap.get(message);
            if(networkUserList != null){
                networkUserList.remove(user);
            }
        }
    }

    @Override
    public void addMessage(Message message, List<NetworkUser> usersList) {
        numberMessageMap.put(message.getNumber(), message);
        messageUsersListMap.put(message, usersList);
    }

    @Override
    public void changeTimeout(int newTimeout) {
        nodeTimeoutMS.set(newTimeout);
    }
}
