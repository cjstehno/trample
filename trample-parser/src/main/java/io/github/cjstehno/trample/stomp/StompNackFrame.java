package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static io.github.cjstehno.trample.stomp.StompSubscribeFrame.ID_HEADER;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompNackFrame extends StompFrame implements StompFrame.StompClientFrame {

    public static final String COMMAND = "NACK";

    public StompNackFrame() {
        super(COMMAND);
    }

    public StompNackFrame(final String id) {
        this();
        setId(id);
    }

    public void setId(final String id) {
        setHeader(ID_HEADER, id);
    }

    public String getId() {
        return getHeader(ID_HEADER);
    }
}
