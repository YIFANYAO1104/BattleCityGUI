/**
 * @author Petr (http://www.sallyx.org/)
 */
package com.bham.bc.common.Messaging;



/**
 * Class for building a telegram
 * The Telegram implements comparable Interface
 */
public class Telegram implements Comparable {

    /**
     * GameEntity sends this telegram
     */
    public int Sender;
    /**
     * GameEntity receive this telegram
     */
    public int Receiver;
    /**The message itself.
     * These are all enumerated in the file "MessageTypes.h"
     */
    public MessageTypes Msg;
    //messages can be dispatched immediately or delayed for a specified amount
    //of time. If a delay is necessary this field is stamped with the time
    //the message should be dispatched.
    /**
     * The DispatchTime of Message (Immediately Or Delayed)
     * If message is delayed, then this attribute is the time message should be dispatched.
     */
    public double DispatchTime;
    /**
     * Any Additional information that can be with message
     */
    public Object ExtraInfo;

    /**
     * Constructor of Telegram.
     * Initialize all attributes of Telegram to default values
     */
    public Telegram() {
        DispatchTime = -1;
        Sender = -1;
        Receiver = -1;
        Msg = null;
    }

    /**
     * Constructor of Telegram with 4 parameters.
     * Set all attributes except Object Info
     * @param time
     * @param sender
     * @param receiver
     * @param msg
     */
    public Telegram(double time,
                    int sender,
                    int receiver,
                    MessageTypes msg) {
        this(time, sender, receiver, msg, null);
    }

    /**
     * Constructor of Telegram with 5 parameters.
     * Set All attributes to values of parameters
     * @param time
     * @param sender
     * @param receiver
     * @param msg
     * @param info
     */
    public Telegram(double time,
                    int sender,
                    int receiver,
                    MessageTypes msg,
                    Object info) {
        DispatchTime = time;
        Sender = sender;
        Receiver = receiver;
        Msg = msg;
        ExtraInfo = info;
    }

    public final static double SmallestDelay = 0.25;


    /**
     * overloads "=="
     * Telegrams will be sorted in a priority queue.
     * So we need to overload the operator so that, the Priority Queue can sort telegrams by time priority
     * If Object is not Telegram type, return false
     * When we process two telegrams, if the difference in times is smaller than SmallestDelay
     * And two telegrams have all the same attributes(sender, receiver, message)
     * Then return true ,which can seen as equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Telegram)) {
            return false;
        }
        Telegram t1 = this;
        Telegram t2 = (Telegram) o;
        return (Math.abs(t1.DispatchTime - t2.DispatchTime) < SmallestDelay)
                && (t1.Sender == t2.Sender)
                && (t1.Receiver == t2.Receiver)
                && (t1.Msg == t2.Msg);
    }


    /**
     * Override hash function
     * Necessary to override hashcode method since 'equals' method has been overridden
     * In this way, we maintain the general contract : Equal Objects must have Equal Hashcodes
     * @return hashcode Of object
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.Sender;
        hash = 89 * hash + this.Receiver;
        hash = 89 * hash + this.Msg.id;
        return hash;
    }
    /**
     * overloads "<" and ">" operators
     * Return -1 when t1 will be dispatched earlier than t2
     * Return 0 if equals
     * Return 1 if t1 will be dispatched later than t2
     */
    @Override
    public int compareTo(Object o2) {
        Telegram t1 = this;
        Telegram t2 = (Telegram) o2;
        if (t1 == t2) {
            return 0;
        } else {
            return (t1.DispatchTime < t2.DispatchTime) ? -1 : 1;
        }
    }

    /**
     *
     * @return String info of Full message with time and transmitter
     */
    @Override
    public String toString() {
        return "time: " + DispatchTime + "  Sender: " + Sender
                + "   Receiver: " + Receiver + "   Msg: " + Msg;
    }

    //handy helper function for dereferencing the ExtraInfo field of the Telegram
//to the required type.
    public static <T> T DereferenceToType(Object p) {
        return (T) (p);
    }
}