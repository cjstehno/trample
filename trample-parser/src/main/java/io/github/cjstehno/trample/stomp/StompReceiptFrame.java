package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompReceiptFrame extends StompFrame implements StompFrame.StompServerFrame {

    public static final String COMMAND = "RECEIPT";
    public static final String RECEIPT_ID_HEADER = "receipt-id";

    public StompReceiptFrame() {
        super(COMMAND);
    }

    public StompReceiptFrame(final String receiptId) {
        this();
        setReceiptId(receiptId);
    }

    public void setReceiptId(final String value) {
        setHeader(RECEIPT_ID_HEADER, value);
    }

    public String getReceiptId() {
        return getHeader(RECEIPT_ID_HEADER);
    }
}
