package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompDisconnectFrame extends StompFrame implements StompFrame.Client {

    public static final String COMMAND = "DISCONNECT";
    public static final String RECEIPT_HEADER = "receipt";

    public StompDisconnectFrame() {
        super(COMMAND);
    }

    public StompDisconnectFrame(final String receiptId) {
        this();
        setReceipt(receiptId);
    }

    public void setReceipt(final String id) {
        setHeader(RECEIPT_HEADER, id);
    }

    public String getReceipt() {
        return getHeader(RECEIPT_HEADER);
    }
}
