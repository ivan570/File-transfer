package fileTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class SendData implements Runnable {

	static int current = 0, size = 65000;
	private InputStream in;
	private OutputStream out;
	private FileInputStream fin;

	private String files[]; // queue to store sending file ...
	static Thread th;
	byte byt[];

	private int Size, brc;
	private int st_p = 0, end_p = -1;

	SendData(Socket s) throws Exception {

		in = s.getInputStream();
		out = s.getOutputStream();
		files = new String[100];
		th = new Thread(this);
		th.start();
		byt = new byte[size * 50];

	}

	@SuppressWarnings("deprecation")
	public void run() {

		try {
			while (true) {

				if (st_p - end_p == 1) {
					System.out.println("send data thread suspended ");
					th.suspend();
				} else {
					RecieveData.th.suspend();

					out.write("><".getBytes());

					while (in.read() != 47) {
						System.out.println("waiting");
					}

					String st = files[++end_p];
					fin = new FileInputStream(st);
					Size = fin.available();
					brc = (Size / size) - 50;
					System.out.println("brc is " + brc);

					File f1 = new File(st);
					st = "/" + f1.getName() + "/" + Size + "/";
					out.write(st.getBytes());

					while (in.read() != 47) {
						System.out.println(" waiting for /");
					}
					long start = System.currentTimeMillis();
					int i = 0;
					while (current <= brc) {

						fin.read(byt); // System.out.println("Data readed at "+ (System.currentTimeMillis()-start));
						for (i = 0; i < 50; i++) {

							out.write(byt, i * size, size);
							current++;
							while (in.read() != 47) {
								System.out.println("waiting for /" + current);
							}
						}
						// System.out.println("Data sended at "+ (System.currentTimeMillis()-start));
					}

					i = 0;
					fin.read(byt, 0, Size - current * size);
					System.out.println("last data readed ");

					while (Size - current * size >= size) {

						out.write(byt, i * size, size);
						current++;
						i++;
						while (in.read() != 47) {
							System.out.println("waiting for /" + current);
						}
					}

					out.write(byt, i * size, Size - current * size);
					fin.close();
					double l = System.currentTimeMillis() - start;

					System.out.println("data sended in  " + l / 1000);

					double ll = Size / (1024 * 1024);
					ll = ll / l * 1000;
					System.out.println(" average speed is " + ll + " MB/s");
					if (end_p == 99)
						end_p = -1;
					RecieveData.th.resume();
				}

			}
		} catch (Exception e) {
		}

	}

	@SuppressWarnings("deprecation")
	int InsertFile(String st) {

		if (end_p - st_p == 1 || (st_p == 99 && end_p == -1))
			return -1;

		else {
			files[st_p++] = st;
			System.out.println(" file added in que  " + st);

			if (st_p == 100)
				st_p = 0;
			if (st_p - end_p == 2) {
				System.out.println("send data thread resumes ");
				th.resume();
			}
		}
		return 1;
	}
}
