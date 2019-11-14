package model.snakeGameNetwork;

import model.networkUtils.ACKManager;
import model.networkUtils.Message;
import model.networkUtils.NetworkUser;

import java.util.List;
import java.util.Map;

public class SnakeGameACKManager implements ACKManager, Runnable {
    private Map<Message, List<NetworkUser>> messageUsersListMap;
    private Map<Long, Message> numberMessageMap;

    @Override
    public void run() {

    }

    @Override
    public void askRecv(long messageNumber, NetworkUser user) {

    }

    @Override
    public void addMessage(Message message, List<NetworkUser> usersList) {

    }
}
