package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompStompFrame extends StompFrame implements StompFrame.StompClientFrame {

    public static final String COMMAND = "STOMP";
    public static final String ACCEPT_VERSION_HEADER = "accept-version";
    public static final String HOST_HEADER = "host";

    public StompStompFrame() {
        super(COMMAND);
    }

    public StompStompFrame(final String host, final String acceptVersion) {
        this();
        setHost(host);
        setAcceptVersion(acceptVersion);
    }

    public void setHost(final String value) {
        setHeader(HOST_HEADER, value);
    }

    public String getHost() {
        return getHeader(HOST_HEADER);
    }

    public void setAcceptVersion(final String value) {
        setHeader(ACCEPT_VERSION_HEADER, value);
    }

    public String getAcceptVersion() {
        return getHeader(ACCEPT_VERSION_HEADER);
    }
}
