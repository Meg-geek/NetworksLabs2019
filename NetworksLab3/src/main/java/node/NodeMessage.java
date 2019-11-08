package node;

public interface NodeMessage {
    int CONNECTION = 1;
    int TEXT = 2;
    int ACK = 3;
    int ALTERNATIVE = 4;
    int PERIOD_CHECK = 5;
    int MAX_MESSAGE_LENGTH = 4096;

    String CHARSET_NAME = "UTF-8";
    //for all types
    byte[] toBytes();
    String getUUID();
    int getMessageType();
    //all messages
    int getPort();
    String getIP();
    //for ACK messages
    String getACKUUID();
    //for alternative messages
    String getParentIP();
    int getParentPort();
    //for text messages
    String getText();
    String getNodeName();
}
