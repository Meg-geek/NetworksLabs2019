package chatNode;

import node.NodeMessage;

import java.io.*;
import java.nio.charset.Charset;
import java.util.UUID;

/*
сначала в любом сообщении uuid сообщения в строковом формате
затем  int messageType,
далее в зависимости от вида сообщения
CONNECTION: ничего
TEXT: длина сообщения(int) + сообщение + длина имени узла (int) + имя узла
ACK: UUID полученного сообщения
ALTERNATIVE: длина (int) ip + ip + порт (int)
PERIOD CHECK: ничего
 */

public class ChatNodeMessage implements NodeMessage {
    private UUID uuid;
    private int messageType;
    private String text;
    private String ip, parentIP;
    private int port, parentPORT;
    private String nodeName;
    private ByteArrayOutputStream messageByteArrayStream = new ByteArrayOutputStream();
    private DataOutputStream messageDataStream = new DataOutputStream(messageByteArrayStream);
    private static int UUID_SIZE = UUID.randomUUID().toString().getBytes(Charset.forName(CHARSET_NAME)).length;

    public ChatNodeMessage(int messageType) throws IOException {
        uuid = UUID.randomUUID();
        this.messageType = messageType;
        messageDataStream.write(uuid.toString().getBytes(Charset.forName(CHARSET_NAME)));
        messageDataStream.writeInt(messageType);
        messageDataStream.flush();
    }

    //for ACK messages
    ChatNodeMessage(int messageType, String ackUUID) throws IOException{
        this(messageType);
        messageDataStream.write(ackUUID.getBytes(Charset.forName(CHARSET_NAME)));
        text = ackUUID;
        messageDataStream.flush();
    }

    //for TEXT message
    ChatNodeMessage(int messageType, String text, String nodeName) throws IOException{
        this(messageType);
        byte[] textByteArray = text.getBytes(Charset.forName(CHARSET_NAME));
        messageDataStream.writeInt(textByteArray.length);
        messageDataStream.write(textByteArray);
        textByteArray = nodeName.getBytes(Charset.forName(CHARSET_NAME));
        messageDataStream.writeInt(textByteArray.length);
        messageDataStream.write(textByteArray);
        this.nodeName = nodeName;
        this.text = text;
        messageDataStream.flush();
    }

    //for alternative messages
    ChatNodeMessage(int messageType, String parentIP, int parentPort) throws IOException{
        this(messageType);
        byte[] ipByteArray = parentIP.getBytes(Charset.forName(CHARSET_NAME));
        messageDataStream.writeInt(ipByteArray.length);
        messageDataStream.write(ipByteArray);
        messageDataStream.writeInt(parentPort);
        messageDataStream.flush();
        this.parentIP = parentIP;
        this.parentPORT = parentPort;
        messageDataStream.flush();
    }

    //for recv messages
    public ChatNodeMessage(byte[] recvMessage, String ip, int port) throws IOException {
        messageDataStream.write(recvMessage);
        messageDataStream.flush();
        this.ip = ip;
        this.port = port;
        DataInputStream recvMessageStream = new DataInputStream(new ByteArrayInputStream(recvMessage));
        byte[] uuidByteArray = new byte[UUID_SIZE];
        recvMessageStream.readFully(uuidByteArray, 0, UUID_SIZE);
        uuid = UUID.fromString(new String(uuidByteArray, CHARSET_NAME));
        messageType = recvMessageStream.readInt();
        switch(messageType){
            case CONNECTION :
                break;
            case PERIOD_CHECK:
                break;
            case ACK:
                recvMessageStream.readFully(uuidByteArray, 0, UUID_SIZE);
                text = new String(uuidByteArray, CHARSET_NAME);
                break;
            case TEXT:
                int length = recvMessageStream.readInt();
                byte[] textByteArray = new byte[length];
                recvMessageStream.readFully(textByteArray, 0, length);
                text = new String(textByteArray, CHARSET_NAME);
                length = recvMessageStream.readInt();
                textByteArray = new byte[length];
                recvMessageStream.readFully(textByteArray, 0, length);
                nodeName = new String(textByteArray, CHARSET_NAME);
                break;
            case ALTERNATIVE:
                int ipLength = recvMessageStream.readInt();
                byte[] parentIPbyteArray = new byte[ipLength];
                recvMessageStream.readFully(parentIPbyteArray, 0, ipLength);
                parentIP = new String(parentIPbyteArray, CHARSET_NAME);
                parentPORT = recvMessageStream.readInt();
                break;
        }
    }

    @Override
    public byte[] toBytes() {
        return messageByteArrayStream.toByteArray();
    }

    @Override
    public String getUUID() {
        return uuid.toString();
    }

    @Override
    public int getMessageType(){
        return messageType;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public String getACKUUID() {
        if(messageType == ACK){
            return text;
        }
        return null;
    }

    @Override
    public String getParentIP() {
        if(messageType == ALTERNATIVE){
            return parentIP;
        }
        return null;
    }

    @Override
    public int getParentPort() {
        if(messageType == ALTERNATIVE){
            return parentPORT;
        }
        return 0;
    }

    @Override
    public String getText(){
        if(messageType == TEXT){
            return text;
        }
        return null;
    }

    @Override
    public String getNodeName() {
        if(messageType == TEXT){
            return nodeName;
        }
        return null;
    }
}
