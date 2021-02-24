package com.chat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatApplication {

	private static BlockingQueue<String> initiatorSends = new ArrayBlockingQueue<String>(1);
	private static BlockingQueue<String> initiatorRecives = new ArrayBlockingQueue<String>(1);
	static AtomicInteger counter = new AtomicInteger();

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
			String startMsg = String.format("Start  {%d} ", counter.get());
			System.out.println("Initial Player : "+startMsg);
			initiatorSends.put(startMsg);
			while (counter.get() < 19) {
				String msg = initiatorRecives.take();
				// System.out.println("Initiator player msg "+msg);
				msg = msg + String.format(" {%d} ", counter.incrementAndGet());
				System.out.println("Initial Player : "+msg);
				initiatorSends.put(msg);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void otherPlayer() {
		String msg;
		try {
			while (counter.get() < 19) {
				msg = initiatorSends.take();
				//System.out.println("Player1  msg recive " + msg);
				msg = msg + String.format(" {%d} ", counter.incrementAndGet());
				System.out.println("Second Player : "+msg);
				initiatorRecives.put(msg);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
