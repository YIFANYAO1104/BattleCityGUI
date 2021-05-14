package com.bham.bd.utils.messaging;
/**
 * enum for Message Types
 */
public enum MessageTypes {
    /**
     * Test info_1
     */
    Msg_IfindYou(0),
    /**
     * Test info_2
     */
    Msg_Gotta(1),
    /**
     * interact with obstacle
     */
    Msg_interact(2),
    /**
     * interact with removable obstacle
     */
    Msg_interactWithSoft(3),
    /**
     * remove the obstacle
     */
    Msg_removeSoft(4);

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
            case 2:
                return "interactWith";
            case 3:
                return "direction";
            default:
                return "Not recognized!";
        }
    }
}
