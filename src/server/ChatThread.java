package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import data.AccessDB;
import data.ChatProtocal;

public class ChatThread extends Thread {
	//���ӿͻ��˵�socket
	private Socket client = null; 
	//ͨ����·��������
	private BufferedReader in = null;
	//ͨ����·�������
	private SynchronizedPrintWriter out = null;
	//�ͻ��˵��˺�
	private String name = null;

	public ChatThread(Socket client) {
		this.client = client;
	}

	public void run() {

		try {
			/*
			 * 1.��ͨ����·�����������
			 */
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
            out = new SynchronizedPrintWriter(new PrintWriter(
					client.getOutputStream()));
			String line = null;
			/*
			 * 2.ѭ���������������յ�����������Ӧ����
			 */
			while ((line = in.readLine()) != null) {
				//���Ϊ��¼����
				if (line.startsWith(ChatProtocal.LOGIN)
						&& line.endsWith(ChatProtocal.LOGIN)) {

					try {
						returnLogin(line);
					} catch (SQLException e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
                   //���Ϊע������
				} else if (line.startsWith(ChatProtocal.REGISTER)
						&& line.endsWith(ChatProtocal.REGISTER)) {
					try {
						returnRegister(line);
					} catch (SQLException e) {

						e.printStackTrace();
					}
					//���ΪȺ����Ϣ
				} else if (line.startsWith(ChatProtocal.GR_MSG)
						&& line.endsWith(ChatProtocal.GR_MSG)) {
					passGRMsg(line, out);
					//���Ϊ˽����Ϣ
				} else if (line.startsWith(ChatProtocal.PR_MSG)
						&& line.endsWith(ChatProtocal.PR_MSG)) {
					passPRMsg(line);
					//������������û��б�
				} else if (line.startsWith(ChatProtocal.REQUESTFORLIST)
						&& line.endsWith(ChatProtocal.REQUESTFORLIST)) {
					returnList();
                     //����ǶϿ���������
				} else if (line.startsWith(ChatProtocal.DISCONNECT)
						&& line.endsWith(ChatProtocal.DISCONNECT)) {
					disconnect(line);

				} else {
					System.out.println("��������");
				}

			}
		} catch (IOException e) {
            /*
             * 3.����쳣����ر�������������ر�socket��
             */
			System.out.println("clientsocket ��������ʧ��");
			MainControl.map.remove(name);
			name = null;
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (client != null)
					client.close();
			} catch (IOException ex) {
				// TODO �Զ����ɵ� catch ��
				ex.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	// �����½����
	private void returnLogin(String line) throws SQLException {
		String realMsg = getRealMsg(line);
		System.out.println(realMsg);
		String[] msgs = realMsg.split(ChatProtocal.DELIMITER);
	
		
		/*
		 *�жϵ�½��Ϣ�Ƿ���ȷ
		 */
		if (MainControl.map.containsKey(msgs[0])) {
			/*
			 * 1.���˺��Ѿ����˵�¼
			 */
			out.println(ChatProtocal.LOGINED);
			System.out.println(msgs[0] + "�����˵�¼");

		} else {
			ResultSet set = AccessDB
					.run("SELECT passwd FROM account WHERE name='" + msgs[0]
							+ "'");
			if (!set.next()) {
				/*
				 * 2. ���˺Ų�����
				 */
				out.println(ChatProtocal.AC_NOTEX);
				System.out.println(msgs[0] + " �˺Ų����ڣ���ע��");
			} else {
			
				System.out.println(set.getString(1));
				if (set.getString(1).equals(msgs[1])) {
					/*
					 * 3.���˺ŵ�¼�ɹ�
					 */
					out.println(ChatProtocal.SUCCESS_LOGIN);
					System.out.println(msgs[0] + " ��¼�ɹ�");
					setName(msgs[0]);
					MainControl.map.put(msgs[0], out);
					name = msgs[0];
				} else {
					/*
					 * 4.�˺��������
					 */
					out.println(ChatProtocal.WR_PASSWD);

					System.out.println(msgs[0] + "�������");
				}
			}
		}
	}

	// ���� ע������
	private void returnRegister(String line) throws SQLException {
		String realMsg = getRealMsg(line);
		String[] msgs = realMsg.split(ChatProtocal.DELIMITER);
		
		ResultSet set = AccessDB.run("SELECT * FROM account WHERE name='"
				+ msgs[0] + "'");
		if (set.next()) {
			/*
			 * 1.�˺��Ѵ���
			 */
			out.println(ChatProtocal.AC_EXIST);

			System.out.println(msgs[0] + " �Ѵ��ڣ�");

		} else {

			try {
				/*
				 * 2.ע��ɹ�
				 */
				AccessDB.insert("insert into account(name,passwd,age,sex,telnum) values('"
						+ msgs[0]
						+ "','"
						+ msgs[1]
						+ "',"
						+ msgs[2]
						+ ",'"
						+ msgs[3] + "','" + msgs[4] + "')");
				out.println(ChatProtocal.REGISTER_SUCCESS);
				System.out.println(msgs[0] + "ע��ɹ���");
			} catch (SQLException e) {
				/*
				 * 3.ע��ʧ��
				 */
				out.println(ChatProtocal.REGISTER_FAILED);
				System.out.println(msgs[0] + "ע��ʧ�ܣ�");
				e.printStackTrace();
			}

		}

	}

	// ת��Ⱥ����Ϣ
	private void passGRMsg(String line, SynchronizedPrintWriter source) {

		Collection<SynchronizedPrintWriter> list = MainControl.map.values();
		for (SynchronizedPrintWriter writer : list) {
			if (source != writer)
				writer.println(line);
		}
		
		String realMsg = getRealMsg(line);
		System.out.println(realMsg);
		String[] msgs = realMsg.split(ChatProtocal.DELIMITER);
		SimpleDateFormat format1=new SimpleDateFormat("YYYY-MM-dd");
		SimpleDateFormat format2=new SimpleDateFormat("HH:mm:ss");
		String date=format1.format(new Date(Long.parseLong(msgs[2])));
		String timestamp=format2.format(new Date(Long.parseLong(msgs[2])));
		String sql="insert into message values('"+msgs[0]+"','group','"+msgs[1]+"','"+date+"','"+timestamp+"')";
		try {
			AccessDB.insert(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
			System.out.println("������Ϣ�־û�����");
		}
		
		

	}

	// ת��˽����Ϣ
	private void passPRMsg(String line) {
		String realMsg = getRealMsg(line);
		String[] msgs = realMsg.split(ChatProtocal.DELIMITER);
		SynchronizedPrintWriter writer = MainControl.map.get(msgs[0]);
		if (writer != null) {
			writer.println(line);

		} else {
			out.println(ChatProtocal.NOTONLINE);
			System.out.println(msgs[0] + "������");
        }
		SimpleDateFormat format1=new SimpleDateFormat("YYYY-MM-dd");
		SimpleDateFormat format2=new SimpleDateFormat("HH:mm:ss");
		String date=format1.format(new Date(Long.parseLong(msgs[3])));
		String timestamp=format2.format(new Date(Long.parseLong(msgs[3])));
		String sql="insert into message values('"+msgs[0]+"','"+msgs[1]+"','"+msgs[2]+"','"+date+"','"+timestamp+"')";
		try {
			AccessDB.insert(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
			System.out.println("˽����Ϣ�־û�����");
		}
		
	}

	// �ظ������û��б�
	private void returnList() {

		StringBuilder buffer = new StringBuilder();
		buffer.append(ChatProtocal.REQUESTFORLIST);
		if (MainControl.map.keySet() != null) {
			Set<String> names = MainControl.map.keySet();
			for (String name : names) {
				buffer.append(name + ChatProtocal.DELIMITER);
			}
			buffer.append(ChatProtocal.REQUESTFORLIST);
			out.println(buffer.toString());

		} else {

			out.println(ChatProtocal.REQUESTFORLIST);

		}
		System.out.println("���������û��б�");
	}

	
	private void disconnect(String line) {

		MainControl.map.remove(name);
		name = null;

		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (client != null)
				client.close();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}

	}

	// ��ȥ��Ϣ����Э��ͷ
	private String getRealMsg(String line) {

		return line.substring(ChatProtocal.PROTOCAL_LENGTH, line.length()
				- ChatProtocal.PROTOCAL_LENGTH);
	}

}
