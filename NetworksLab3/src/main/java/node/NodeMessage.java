package node;

public interface NodeMessage {
    /*
    int CONNECTION = 1;
    int TEXT = 2;
    int ACK = 3;
    int ALTERNATIVE = 4;
    int PERIOD_CHECK = 5;
*/
    String CHARSET_NAME = "UTF-8";
    byte[] toBytes();
    String getUUID();
}
