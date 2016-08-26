 package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainControl {
    
	private ServerSocket server=null;
	public static SynchronizedHashMap<String,SynchronizedPrintWriter> map=new SynchronizedHashMap<String,SynchronizedPrintWriter>();
	
	public MainControl(){
		/*
		 * 1.建立ServerSocket 
		 */
		try {
			server = new ServerSocket(7000); 
			System.out.println("**********服务器端建立成功************");
		} catch (IOException e) {
			System.out.println("服务器启动失败，是否有其他应用占用接口：7000！");
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		
		/*
		 * 2.循环监听7000端口
		 */
		Socket socket=null;
		while(true){   //服务器监听，始终开启
			
			try {
				/*
				 * 3.如果收到客户端的连接请求，则创建线程ChatThread负责与该客户端的通信
				 */
				socket=server.accept();
				if(socket!=null){
				   System.out.println("新的客户端建立连接");
				   new ChatThread(socket).start();
		        }
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args){
		
		new MainControl().run();
		
	}
	
	
	
}
