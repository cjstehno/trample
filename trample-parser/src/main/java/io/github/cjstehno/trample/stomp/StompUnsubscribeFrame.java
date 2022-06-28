package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static io.github.cjstehno.trample.stomp.StompSubscribeFrame.ID_HEADER;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompUnsubscribeFrame extends StompFrame implements StompFrame.StompClientFrame {

    public static final String COMMAND = "UNSUBSCRIBE";

    public StompUnsubscribeFrame() {
        super(COMMAND);
    }

    public StompUnsubscribeFrame(final String id) {
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
