package com.chat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChatApplication {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Start................");
		BlockingQueue<String> initiatorSends = new ArrayBlockingQueue<String>(1);
		BlockingQueue<String> initiatorRecives = new ArrayBlockingQueue<String>(1);
		Player initiatorplayer = new Player();
		Player player2 = new Player();
		Thread thread1 = new Thread(() -> initiatorplayer.initiatorPlayer(initiatorSends, initiatorRecives));
		Thread thread2 = new Thread(() -> player2.otherPlayer(initiatorSends, initiatorRecives));
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
		System.out.println("End.........");
	}

}
