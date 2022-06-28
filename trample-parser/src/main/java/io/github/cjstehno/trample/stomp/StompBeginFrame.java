package io.github.cjstehno.trample.stomp;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class StompBeginFrame extends StompFrame implements StompFrame.StompClientFrame {

    public static final String COMMAND = "BEGIN";
    public static final String TRANSACTION_HEADER = "transaction";

    public StompBeginFrame(){
        super(COMMAND);
    }

    public StompBeginFrame(final String transaction){
        this();
        setTransaction(transaction);
    }

    public void setTransaction(final String transaction){
        setHeader(TRANSACTION_HEADER, transaction);
    }

    public String getTransaction(){
        return getHeader(TRANSACTION_HEADER);
    }
}
