package com.chat;

import java.util.concurrent.BlockingQueue;

public class Player {

	static int  counter;

	
	public  void otherPlayer(BlockingQueue<String> initiatorSends, BlockingQueue<String> initiatorRecives) {
		String msg;
		try {
			while (counter < 9) {
				msg = initiatorSends.take();
				System.out.println("Second  Player : " + msg);
				initiatorRecives.put(msg);
			}
		} catch (InterruptedException e) {
			handleError(e);
		}
	}
	
	public void initiatorPlayer(BlockingQueue<String> initiatorSends, BlockingQueue<String> initiatorRecives) {

		try {
			String startMsg = String.format("Start  {%d} ", counter);
			System.out.println("Initial Player : " + startMsg);
			initiatorSends.put(startMsg);
			while (counter < 9) {
				String msg = initiatorRecives.take();
				msg = msg + String.format(" {%d} ", ++counter);
				System.out.println("Initial Player : " + msg);
				initiatorSends.put(msg);
			}
		} catch (InterruptedException e) {
			handleError(e);
		}
	}

	
	private  void handleError(Exception e) {
		System.err.println("Thread interrupted unexpectedly ");
		throw new IllegalStateException("Program ended unexpectedly");
	}
}
