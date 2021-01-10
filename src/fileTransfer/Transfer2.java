package fileTransfer;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Transfer2 {
	private static SendData sd1;
	private static Socket skt;
	static ServerSocket ss;

	static void send() throws Exception {

		ss = new ServerSocket(1327);
		System.out.println("connect to " + InetAddress.getLocalHost().getHostName());
		// show ip address
		// wait for client
		skt = ss.accept();
		sd1 = new SendData(skt); // send data via socket
		new RecieveData(skt); // both are thread
		// we want to send data both the ways
		ss.close();
	}

	static void recieve() throws Exception {

		// skt.connect(endpoint, 2);
		skt = new Socket("127.0.0.1", 1327);
		sd1 = new SendData(skt);
		new RecieveData(skt);

	}

	public static void main(String args[]) {

		Scanner sc1 = new Scanner(System.in);
		String st = " ";
		System.out.println("Enter 1 for Send or 2 for Connect ");

		try {
			if (sc1.nextInt() == 1)
				send(); // like wifi and hotsport

			else
				recieve();
		} catch (Exception e) {
		}

		while (true) {

			st = sc1.next(); // take input either "end" or "<"
			if (st.equals("end"))
				break;

			if (st.equals("<")) {
				System.out.println(" enter the path of file ");
				st = sc1.next(); // take path
				System.out.println(" inserting file "); //
				sd1.InsertFile(st);
			}
		}
		sc1.close();
	}
}
