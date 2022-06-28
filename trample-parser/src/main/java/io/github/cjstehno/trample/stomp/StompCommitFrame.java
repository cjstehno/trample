package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static io.github.cjstehno.trample.stomp.StompBeginFrame.TRANSACTION_HEADER;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompCommitFrame extends StompFrame implements StompFrame.StompClientFrame {

    public static final String COMMAND = "COMMIT";

    public StompCommitFrame() {
        super(COMMAND);
    }

    public StompCommitFrame(final String transaction) {
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
