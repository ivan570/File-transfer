package fileTransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class RecieveData implements Runnable {
	
	private InputStream in;  // read the data from socket 
	private OutputStream out; // Ack. send the data from socket
	static Thread th;  
	private int c;
	private int size = 65000; // wait for 65000 Byte data and then store
	private FileOutputStream fout; // create file and it's path 
	static int current = 0; // count how many time 65000 byte are come.
	static int 	brc; // size / 65000;
	static int  Size; // file size;
	String path = "D:/java"; // path
	byte byt[]; // store 65000 byte and send to fout.
	
	RecieveData(Socket socket) {
		try
		{
			 in = socket.getInputStream(); // get stream
			 out = socket.getOutputStream();
			 byt = new byte[size * 50];	// temp. queue
			 
			 th = new Thread(this); // Thread
			 th.start();
			 File ff = new File(path); // create folder
			 ff.mkdir();
		}
		catch(Exception e)
		{
			System.out.println("ReciveData error" + e);
		}
	}

	@SuppressWarnings("deprecation")
	public void run() {
	
		while (true) {
			
			try {
				// every sender first send '><'.
				// "><" comes suspend the SendData thread
				if (in.read() == 62)   {
					if (in.read() == 60)  {
						
						SendData.th.suspend(); // block send thread 
						out.write("<>/".getBytes()); // ready to tack data
						
						fileInfo(); 
						
						while (current <= brc) {
							int i = 0;
							for(i = 0; i < 50; i++) {   
								while (in.available() < size) {}

								out.write("/".getBytes());
								in.read(byt, i * size, size); 
								current++;
							}
							fout.write(byt);
							System.out.println(" data writed "+current);
						}
						
						int i = 0;	
						while (Size - current * size >= size) {
							while(in.available() < size) {}
							
							out.write("g/".getBytes());
							in.read(byt, i * size, size);
							i++; 
							current++; 
						}
						
						while(in.available() < Size - current * size) {}
						
						in.read(byt, i*size, Size-current*size);
						
						fout.write(byt, 0, i * size + Size - current * size );
						
						System.out.println("  Data  recieved ");
						fout.close();
					}
				}
				
				Thread.sleep(1000);
				System.out.println("sleeps RecieveData");
			} 
			catch (Exception e)  {
				System.out.println("RecieveData run error ");
			}  
		}
	}
	
	void fileInfo() throws Exception {
		// in is InputStream of RecieveData data class
		c = in.read(); 
		// if input char is '/' than
		if (c == 47) {
			
			String st = "";
			c = in.read();
			while (c != 47) {	
		 		st += (char)c;
		 		c = in.read();  
	 		}
			
			fout = new FileOutputStream("" + path + "\\" + st);
			// create file 
			
			st = ""; // read size
		    c = in.read(); // '/filename/size/g/'
		    
			while (c != 47) {
				st += (char)c;
				c = in.read();
			} 
			
			Size = Integer.parseInt(st);
			brc = (Size / size) - 50; // 
		    out.write("g/".getBytes()); // start to recive data 
		}
	}

}	
