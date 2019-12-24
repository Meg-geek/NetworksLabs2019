package model.networkUtils;

public abstract class Message {
    private String ip;
    private int port;
    private BasicMessageInfo messageInfo;

    public Message(){}

    public Message(BasicMessageInfo messageInfo){
        this.messageInfo = messageInfo;
    }

    final public long getNumber(){
        return messageInfo.getNumber();
    }

    final public int getSenderID(){
        return messageInfo.getSenderID();
    }

    final public int getReceiverID(){
        return messageInfo.getReceiverID();
    }

    abstract public MessageType getType();

    final public void setRecieverID(int receiverID){
        messageInfo.setRecieverID(receiverID);
    }

    final public void setPort(int port) {
        this.port = port;
    }

    final public String getIp() {
        return ip;
    }

    final public int getPort() {
        return port;
    }

    final public void setIp(String ip) {
        this.ip = ip;
    }
}
