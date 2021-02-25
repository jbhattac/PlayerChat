package com.chat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChatApplication {

	private static BlockingQueue<String> initiatorSends = new ArrayBlockingQueue<String>(1);
	private static BlockingQueue<String> initiatorRecives = new ArrayBlockingQueue<String>(1);
	static int counter;
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Start................");
		Thread thread1 = new Thread(() -> initiatorPlayer());
		Thread thread2 = new Thread(() -> otherPlayer());
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
		System.out.println("End.........");
	}

	private static void initiatorPlayer() {

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

	private static void otherPlayer() {
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

	private static void handleError(Exception e) {
		System.err.println("Thread interrupted unexpectedly ");
		throw new IllegalStateException("Program ended unexpectedly");
	}

}
