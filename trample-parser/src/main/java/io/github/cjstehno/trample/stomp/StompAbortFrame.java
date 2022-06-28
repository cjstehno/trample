package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static io.github.cjstehno.trample.stomp.StompBeginFrame.TRANSACTION_HEADER;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompAbortFrame extends StompFrame implements StompFrame.Client {

    public static final String COMMAND = "ABORT";

    public StompAbortFrame() {
        super(COMMAND);
    }

    public StompAbortFrame(final String transaction) {
        this();
        setTransaction(transaction);
    }

    public void setTransaction(final String transaction) {
        setHeader(TRANSACTION_HEADER, transaction);
    }

    public String getTransaction() {
        return getHeader(TRANSACTION_HEADER);
    }
}
