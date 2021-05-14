package com.bham.bd.utils.messaging;

import com.bham.bd.entity.BaseGameEntity;

import java.util.TreeSet;

import static com.bham.bd.entity.EntityManager.entityManager;

import static com.bham.bd.utils.messaging.MessageTypes.MsgToStr;
import static com.bham.bd.utils.Timer.CLOCK;

/**
 * Message System to check and track the Game States
 */
public class MessageDispatcher {

    /**
     * Initialization
     */
    final public static double SEND_MSG_IMMEDIATELY = 0.0f;

    /**
     * null
     */
    final public static Object NO_ADDITIONAL_INFO = null;

    /**
     * Create only one Object of MessageDispatcher
     */
    final public static MessageDispatcher Dispatcher = new MessageDispatcher();

    /**
     * We use PriorityQ{@link TreeSet} As container for the delayed messages
     * PriorityQ: providing automatic sorting and avoidance of duplicates
     * Messages are sorted by dispatch time
     */
    private TreeSet<Telegram> PriorityQ = new TreeSet<Telegram>();

    /**
     * If when BaseGameEntity.HandleMessage(msg) returns false, means the telegram could not be handled
     * @param pReceiver {@link BaseGameEntity} the receuve entity
     * @param msg {@link Telegram} msg itself
     */
    private void Discharge(BaseGameEntity pReceiver, Telegram msg) {
        if (!pReceiver.handleMessage(msg)) {

            System.out.println("\nMessage not handled");
        }
    }

    private MessageDispatcher() {
    }

    /**
     * Copy Constructor for MessageDispatcher and it should be private
     * @param d {@link MessageDispatcher} get the messageDispather itself
     */
    private MessageDispatcher(MessageDispatcher d) {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    //this class is a singletonsd
    public static MessageDispatcher Instance() {
        return Dispatcher;
    }

    /**
     * A method to dispatch messages between Entities
     * @param delay when send the message
     * @param sender The index {@link Integer}  of sender
     * @param receiver The index {@link Integer}  of receiver
     * @param msg   The msg{@link MessageTypes} itself
     * @param ExtraInfo the info{@link com.bham.bd.entity.graph.ExtraInfo} itself
     */
    public void dispatchMessage(double delay,
                                int sender,
                                int receiver,
                                MessageTypes msg,
                                Object ExtraInfo) {


        /**
         * Get the reference to the sender and receiver
         */
        BaseGameEntity pSender = entityManager.getEntityFromID(sender);
        BaseGameEntity pReceiver = entityManager.getEntityFromID(receiver);

        /**
         * Make Sure Receiver is valid or it will printout warnings,and return
         */
        if (pReceiver == null) {
            System.out.println("\nWarning! No Receiver with ID of " + receiver + " found");

            return;
        }
        /**
         * Create telegram to be transmitted
         */
        Telegram telegram = new Telegram(0, sender, receiver, msg, ExtraInfo);

        /**
         * If there is no DELAY, we route messages immediately
         * Print out the message with its other info(CurrentTime, sender and receiver,and message content
         */
        if (delay <= 0.0f) {
            /**
             * send the telegram to the receiver
             */

            Discharge(pReceiver, telegram);
        } //else calculate the time when the telegram should be dispatched
        /**
         * If there is DELAY,
         * Calculate the dispatching time of message : Current Time + Delayed time
         * Put this message into the TreeSet Container
         */
        else {
            double CurrentTime = CLOCK.getCurrentTime();

            telegram.DispatchTime = CurrentTime + delay;

            PriorityQ.add(telegram);

            System.out.println("\nDelayed telegram from " + pSender.toString()
                    + " recorded at time " + CLOCK.getCurrentTime() + " for "
                    + pReceiver.toString() + ". Msg is " + MsgToStr(msg));
        }
    }

    /**
     * A method to dispatch the delayed Message
     *
     */

    void DispatchDelayedMessages() {



        double CurrentTime = CLOCK.getCurrentTime();

        /**
         * Peek the Queue to check if any telegrams need dispatching
         * FIFO order, reading the telegram from the front of the Queue
         * Retrieve the Receiver(BaseGameEntity Object) by Calling EntityManager 's GetID method
         * Send message to the Receiver by Discharge(pReceiver,telegram)
         * Finally, afer the message sent, remove from the Queue
         */
        while (!PriorityQ.isEmpty()
                && (PriorityQ.last().DispatchTime < CurrentTime)
                && (PriorityQ.last().DispatchTime > 0)) {

            final Telegram telegram = PriorityQ.last();

            BaseGameEntity pReceiver = entityManager.getEntityFromID(telegram.Receiver);

            System.out.println("\nQueued telegram ready for dispatch: Sent to "
                    + pReceiver.toString() + ". Msg is " + MsgToStr(telegram.Msg));

            Discharge(pReceiver, telegram);
            PriorityQ.remove(PriorityQ.last());
        }
    }

}
