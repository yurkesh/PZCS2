package edu.kpi.nesteruk.pzcs.planning.parcel;

/**
 * Created by Yurii on 2016-04-20.
 */
public class Message extends AbstractParcel {

    public Message(String from, String to, String id) {
        super(from, to, id);
    }

    @Override
    public String toString() {
        return "Message{" + super.toString() + "}";
    }
}
