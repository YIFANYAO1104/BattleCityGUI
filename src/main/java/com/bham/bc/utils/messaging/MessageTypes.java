package com.bham.bc.utils.messaging;
/**
 * enum for Message Types
 */
public enum MessageTypes {


    Msg_IfindYou(0),
    Msg_Gotta(1);

    final public int id;

    /**
     * Constructor of MessageTypes
     * @param id
     */
    MessageTypes(int id) {
        this.id = id;
    }

    /**
     *
     * @return message Type
     */
    @Override
    public String toString() {
        return MsgToStr(this.id);
    }
    public static String MsgToStr(MessageTypes msg) {
        return MsgToStr(msg.id);

    }

    /**
     * Return different messages used for Dispatching decided by msg.id
     * @param msg
     * @return
     */
    public static String MsgToStr(int msg) {
        switch (msg) {
            case 0:
                return "I am shooting you!!";
            case 1:
                return "Gotta";
            default:
                return "Not recognized!";
        }
    }
}
