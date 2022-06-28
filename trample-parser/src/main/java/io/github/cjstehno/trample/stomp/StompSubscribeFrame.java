package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompSubscribeFrame extends StompFrame implements StompFrame.StompClientFrame {

    public static final String COMMAND = "SUBSCRIBE";
    public static final String DESTINATION_HEADER = "destination";
    public static final String ID_HEADER = "id";

    public StompSubscribeFrame() {
        super(COMMAND);
    }

    public StompSubscribeFrame(final String destination, final String id) {
        this();
        setDestination(destination);
        setId(id);
    }

    public void setDestination(final String destination) {
        setHeader(DESTINATION_HEADER, destination);
    }

    public String getDestination() {
        return getHeader(DESTINATION_HEADER);
    }

    public void setId(final String id){
        setHeader(ID_HEADER, id);
    }

    public String getId(){
        return getHeader(ID_HEADER);
    }
}
