 package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainControl {
    
	private ServerSocket server=null;
	public static SynchronizedHashMap<String,SynchronizedPrintWriter> map=new SynchronizedHashMap<String,SynchronizedPrintWriter>();
	
	public MainControl(){
		/*
		 * 1.����ServerSocket 
		 */
		try {
			server = new ServerSocket(7000); 
			System.out.println("**********�������˽����ɹ�************");
		} catch (IOException e) {
			System.out.println("����������ʧ�ܣ��Ƿ�������Ӧ��ռ�ýӿڣ�7000��");
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		
		/*
		 * 2.ѭ������7000�˿�
		 */
		Socket socket=null;
		while(true){   //������������ʼ�տ���
			
			try {
				/*
				 * 3.����յ��ͻ��˵����������򴴽��߳�ChatThread������ÿͻ��˵�ͨ��
				 */
				socket=server.accept();
				if(socket!=null){
				   System.out.println("�µĿͻ��˽�������");
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
