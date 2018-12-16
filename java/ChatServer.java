package chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
	ExecutorService executorService;
	ServerSocketChannel serverSocketChannel;
	int port = 1104;
	List<Client> connectList = new Vector<Client>();
	List<Timer> timers = new Vector<>();    
	String tokenID = "";
	
	public ChatServer() {
		executorService = Executors.newFixedThreadPool(20);

		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(true);
			serverSocketChannel.bind(new InetSocketAddress("192.168.0.20", port));
		} catch (Exception e) {
			if (serverSocketChannel.isOpen()) {
				stopServer();
			}
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						SocketChannel socketChannel = serverSocketChannel.accept();
						Client client = new Client(socketChannel);
						connectList.add(client);
						/*if (connectList.size() > 1) {
							//client리스트 관리
							connectList.get(0).socketChannel.close();
							connectList.remove(0);
							System.out.println("기존의 클라이언트 연결 종료");
						}*/
						System.out.println(client.socketChannel.getLocalAddress() + "가 연결되었습니다.");
						System.out.println("연결된 클라이언트 수 : " + connectList.size());
					} catch (Exception e) {
						if (serverSocketChannel.isOpen()) {
							System.out.print("서버종료");
							stopServer();
						}
						break;
					}
				}
			}
		};
		executorService.submit(runnable);
	}

	void stopServer() {
		try {
			Iterator<Client> iterator = connectList.iterator();
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socketChannel.close();
				iterator.remove();
			}
			if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
				serverSocketChannel.close();
			}
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
		} catch (Exception e) {
		}
	}

	public class MyTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						ProcessBuilder pbSend = new ProcessBuilder("python", "FarmPrint.py");
						Process processSend = pbSend.start();
						InputStream is = processSend.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));

						String data = reader.readLine();

						System.out.println("mytask갱신");

						Charset charset = Charset.forName("UTF-8");
						ByteBuffer byteBuffer = ByteBuffer.allocate(100);
						byteBuffer = charset.encode(data);
						for (Client client : connectList) {
							client.socketChannel.write(byteBuffer);
						}

					} catch (IOException e) {
						System.out.println("error호출");
						e.printStackTrace();
					}
				}
			};

			executorService.submit(runnable);
		}

	}

	public class MyTask1 extends TimerTask {
		private String temp;
		private String humidity;
		
		public MyTask1(String temp, String humidity) {
			this.temp = temp;
			this.humidity = humidity;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					System.out.println("mytask1갱신");
					ProcessBuilder pbSend = new ProcessBuilder("python", "automodeON.py", temp, humidity);
							
					try {
						pbSend.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};

			executorService.submit(runnable);
		}

	}

	public class Client {
		SocketChannel socketChannel;
		Timer timer1;
		
		Client(SocketChannel socketChannel) throws IOException, InterruptedException {
			this.socketChannel = socketChannel;
			
			MyTask mytask = new MyTask();
			timer1 = new Timer();
			timer1.scheduleAtFixedRate(mytask, 0, 3000);

			receive();
		}
		
		void timerCancel() {
			for(int i=0; i<timers.size(); i++) {
				System.out.println(timers.size());
				timers.get(i).cancel();
				timers.remove(i);
			}
		}

		void receive() {
				
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (true) {
						try {
							ByteBuffer byteBuffer = ByteBuffer.allocate(250);
							int readByteCount = socketChannel.read(byteBuffer);

							if (readByteCount == -1) {
								throw new IOException();
							}

							byteBuffer.flip();
							Charset charset = Charset.forName("UTF-8");
							String data = charset.decode(byteBuffer).toString();

							switch (data.split(" ")[0]) {
							case "clientOFF":
								connectList.get(0).timer1.cancel();
								connectList.get(0).socketChannel.close();
								connectList.clear();
								System.out.println("연결된 클라이언트 종료");
								break;
							case "securitymodeON":
								ProcessBuilder pbsecurityON = new ProcessBuilder("python", "security_3.py", data.split(" ")[1], "&");
								pbsecurityON.start();
								System.out.println("security ON");
								break;
							case "securitymodeOFF":
								ProcessBuilder pbsecurityOFF = new ProcessBuilder("killall", "-9", "python", "security_3.py", data.split(" ")[1], "&");
								pbsecurityOFF.start();
								System.out.println("security OFF");
								break;
							case "automodeON":
								MyTask1 mytask1 = new MyTask1(data.split(" ")[1],data.split(" ")[2]);
								Timer timer2 = new Timer();
								timers.add(timer2);
								if(timers.size()>1) {
									timers.get(0).cancel();
									timers.remove(0);
								}
								timers.get(0).schedule(mytask1, 100, 20000);
								
								
								System.out.println("autoMode ON");
								break;

							case "allOFF":
								timerCancel();
								ProcessBuilder pballOFF = new ProcessBuilder("python", "allOFF.py");
								pballOFF.start();
								System.out.println("Receive allOFF");
								break;

							case "ledON":
								timerCancel();
								ProcessBuilder pbledON = new ProcessBuilder("python", "ledON.py");
								pbledON.start();
								System.out.println("Receive ledON");
								break;

							case "ledOFF":
								timerCancel();
								ProcessBuilder pbledOFF = new ProcessBuilder("python", "ledOFF.py");
								pbledOFF.start();
								System.out.println("Receive ledOFF");
								break;

							case "fanON":
								timerCancel();
								ProcessBuilder pbfanON = new ProcessBuilder("python", "fanON.py");
								pbfanON.start();
								System.out.println("Receive fanON");
								break;

							case "fanOFF":
								timerCancel();
								ProcessBuilder pbfanOFF = new ProcessBuilder("python", "fanOFF.py");
								pbfanOFF.start();
								System.out.println("Receive fanOFF");
								break;

							case "motorON":
								timerCancel();
								ProcessBuilder pbmotorON = new ProcessBuilder("python", "motorON.py");
								pbmotorON.start();
								System.out.println("Receive motorON");
								break;

							case "motorOFF":
								timerCancel();
								ProcessBuilder pbmotorOFF = new ProcessBuilder("python", "motorOFF.py");
								pbmotorOFF.start();
								System.out.println("Receive motorOFF");
								break;

							case "waterON":
								timerCancel();
								ProcessBuilder pbwaterON = new ProcessBuilder("python", "waterON.py");
								pbwaterON.start();
								System.out.println("Receive waterON");
								break;

							case "waterOFF":
								timerCancel();
								ProcessBuilder pbwaterOFF = new ProcessBuilder("python", "waterOFF.py");
								pbwaterOFF.start();
								System.out.println("Receive waterOFF");
								break;
							}
						} catch (Exception e) {
							try {
								connectList.remove(Client.this);
								socketChannel.close();
							} catch (IOException e2) {
								break;
							}
						}
					}

				}
			};
			executorService.submit(runnable);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("서버 시작");
		new ChatServer();
	}

}
