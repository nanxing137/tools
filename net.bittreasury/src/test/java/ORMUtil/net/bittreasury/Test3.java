package ORMUtil.net.bittreasury;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mysql.cj.protocol.StandardSocketFactory;

public class Test3 {

	private static int i;
	
	public static void main(String[] args) {
		Runnable tarsk = new Runnable() {
			public void run() {
				for (; i < 5; i++) {
					System.out.println(i);
				}
				
			}
		};
		new Thread(tarsk).start();
		new Thread(tarsk).start();
		new Thread(tarsk).start();
		System.out.println(Integer.MAX_VALUE);
		
		Queue<String> queue = new ConcurrentLinkedQueue<>();
		
	}

}
