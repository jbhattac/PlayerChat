package com.chat;
import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
public class ChatApp {



	    // Blocking queue looks superfluous for single message. But such a queue saves us from cumbersome
	    // synchronization of the threads.
	    private static final int MAX_MESSAGES_IN_QUEUE = 1;

	    public static void main(String[] args)
	    {
	        BlockingQueue<String> firstToSecond = new ArrayBlockingQueue<String>(MAX_MESSAGES_IN_QUEUE);
	        BlockingQueue<String> secondToFirst = new ArrayBlockingQueue<String>(MAX_MESSAGES_IN_QUEUE);

	        // Both players use the same queues symmetrically.
	        InitiatorPlayer firstPlayer = new InitiatorPlayer(firstToSecond, secondToFirst);
	        Player secondPlayer = new Player(secondToFirst, firstToSecond);

	        // Please note that we can start threads in reverse order. But thankfully to
	        // blocking queues the second player will wait for initialization message from
	        // the first player.
	        new Thread(secondPlayer).start();
	        new Thread(firstPlayer).start();
	    }


}




class InitiatorPlayer extends Player
{
    private static final String INIT_MESSAGE = "initiator player";

    public InitiatorPlayer(BlockingQueue<String> sent, BlockingQueue<String> received)
    {
        super(sent, received);
    }

    @Override
    public void run()
    {
        sendInitMessage();
        while (true)
        {
            String receivedMessage = receive();
            reply(receivedMessage);
        }
    }

    private void sendInitMessage()
    {
        try
        {
            sent.put(INIT_MESSAGE);
            System.out.printf("Player [%s] sent message [%s].%n", this, INIT_MESSAGE);
        }
        catch (InterruptedException interrupted)
        {
            String error = String.format(
                    "Player [%s] failed to sent message [%s].",
                    this, INIT_MESSAGE);
            throw new IllegalStateException(error, interrupted);
        }
    }
}





class Player implements Runnable
{
    protected final BlockingQueue<String> sent;
    protected final BlockingQueue<String> received;

    // Please aware that integer field may overflow during prolonged run
    // of the program. So after 2147483647 we'll get -2147483648. We can
    // either use BigInteger or compare the field with Integer.MAX_VALUE
    // before each increment.
    //
    // Let's choose BigInteger for simplicity.
    private BigInteger numberOfMessagesSent = new BigInteger("0");

    public Player(BlockingQueue<String> sent, BlockingQueue<String> received)
    {
        this.sent = sent;
        this.received = received;
    }

    public void run()
    {
        while (true)
        {
            String receivedMessage = receive();
            reply(receivedMessage);
        }
    }

    protected String receive()
    {
        String receivedMessage;
        try
        {
            // Take message from the queue if available or wait otherwise.
            receivedMessage = received.take();
        }
        catch (InterruptedException interrupted)
        {
            String error = String.format(
                    "Player [%s] failed to receive message on iteration [%d].",
                    this, numberOfMessagesSent);
            throw new IllegalStateException(error, interrupted);
        }
        return receivedMessage;
    }

    protected void reply(String receivedMessage)
    {
        String reply = receivedMessage + " " + numberOfMessagesSent;
        try
        {
            // Send message if the queue is not full or wait until one message
            // can fit.
            sent.put(reply);
            System.out.printf("Player [%s] sent message [%s].%n", this, reply);
            numberOfMessagesSent = numberOfMessagesSent.add(BigInteger.ONE);

            // All players will work fine without this delay. It placed here just
            // for slowing the console output down.
            Thread.sleep(1000);
        }
        catch (InterruptedException interrupted)
        {
            String error = String.format(
                    "Player [%s] failed to send message [%s] on iteration [%d].",
                    this, reply, numberOfMessagesSent);
            throw new IllegalStateException(error);
        }
    }
}


