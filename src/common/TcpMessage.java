package common;

import java.io.Serializable;

public class TcpMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private Object payload;

    public TcpMessage() {
    }

    public TcpMessage(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public static TcpMessage of(String type, Object payload) {
        return new TcpMessage(type, payload);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}