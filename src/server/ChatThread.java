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
	//连接客户端的socket
	private Socket client = null; 
	//通信链路的输入流
	private BufferedReader in = null;
	//通信链路的输出流
	private SynchronizedPrintWriter out = null;
	//客户端的账号
	private String name = null;

	public ChatThread(Socket client) {
		this.client = client;
	}

	public void run() {

		try {
			/*
			 * 1.打开通信链路的输入输出流
			 */
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
            out = new SynchronizedPrintWriter(new PrintWriter(
					client.getOutputStream()));
			String line = null;
			/*
			 * 2.循环监听输入流，收到请求，作出相应处理
			 */
			while ((line = in.readLine()) != null) {
				//如果为登录请求：
				if (line.startsWith(ChatProtocal.LOGIN)
						&& line.endsWith(ChatProtocal.LOGIN)) {

					try {
						returnLogin(line);
					} catch (SQLException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
                   //如果为注册请求：
				} else if (line.startsWith(ChatProtocal.REGISTER)
						&& line.endsWith(ChatProtocal.REGISTER)) {
					try {
						returnRegister(line);
					} catch (SQLException e) {

						e.printStackTrace();
					}
					//如果为群聊消息
				} else if (line.startsWith(ChatProtocal.GR_MSG)
						&& line.endsWith(ChatProtocal.GR_MSG)) {
					passGRMsg(line, out);
					//如果为私人消息
				} else if (line.startsWith(ChatProtocal.PR_MSG)
						&& line.endsWith(ChatProtocal.PR_MSG)) {
					passPRMsg(line);
					//如果请求在线用户列表
				} else if (line.startsWith(ChatProtocal.REQUESTFORLIST)
						&& line.endsWith(ChatProtocal.REQUESTFORLIST)) {
					returnList();
                     //如果是断开连接请求
				} else if (line.startsWith(ChatProtocal.DISCONNECT)
						&& line.endsWith(ChatProtocal.DISCONNECT)) {
					disconnect(line);

				} else {
					System.out.println("错误请求");
				}

			}
		} catch (IOException e) {
            /*
             * 3.如果异常，则关闭输入输出流，关闭socket。
             */
			System.out.println("clientsocket 打开数据流失败");
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
				// TODO 自动生成的 catch 块
				ex.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	// 处理登陆请求
	private void returnLogin(String line) throws SQLException {
		String realMsg = getRealMsg(line);
		System.out.println(realMsg);
		String[] msgs = realMsg.split(ChatProtocal.DELIMITER);
	
		
		/*
		 *判断登陆信息是否正确
		 */
		if (MainControl.map.containsKey(msgs[0])) {
			/*
			 * 1.该账号已经有人登录
			 */
			out.println(ChatProtocal.LOGINED);
			System.out.println(msgs[0] + "已有人登录");

		} else {
			ResultSet set = AccessDB
					.run("SELECT passwd FROM account WHERE name='" + msgs[0]
							+ "'");
			if (!set.next()) {
				/*
				 * 2. 该账号不存在
				 */
				out.println(ChatProtocal.AC_NOTEX);
				System.out.println(msgs[0] + " 账号不存在，请注册");
			} else {
			
				System.out.println(set.getString(1));
				if (set.getString(1).equals(msgs[1])) {
					/*
					 * 3.该账号登录成功
					 */
					out.println(ChatProtocal.SUCCESS_LOGIN);
					System.out.println(msgs[0] + " 登录成功");
					setName(msgs[0]);
					MainControl.map.put(msgs[0], out);
					name = msgs[0];
				} else {
					/*
					 * 4.账号密码错误
					 */
					out.println(ChatProtocal.WR_PASSWD);

					System.out.println(msgs[0] + "密码错误");
				}
			}
		}
	}

	// 处理 注册请求
	private void returnRegister(String line) throws SQLException {
		String realMsg = getRealMsg(line);
		String[] msgs = realMsg.split(ChatProtocal.DELIMITER);
		
		ResultSet set = AccessDB.run("SELECT * FROM account WHERE name='"
				+ msgs[0] + "'");
		if (set.next()) {
			/*
			 * 1.账号已存在
			 */
			out.println(ChatProtocal.AC_EXIST);

			System.out.println(msgs[0] + " 已存在！");

		} else {

			try {
				/*
				 * 2.注册成功
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
				System.out.println(msgs[0] + "注册成功！");
			} catch (SQLException e) {
				/*
				 * 3.注册失败
				 */
				out.println(ChatProtocal.REGISTER_FAILED);
				System.out.println(msgs[0] + "注册失败！");
				e.printStackTrace();
			}

		}

	}

	// 转发群聊消息
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
			System.out.println("公共消息持久化错误！");
		}
		
		

	}

	// 转发私人消息
	private void passPRMsg(String line) {
		String realMsg = getRealMsg(line);
		String[] msgs = realMsg.split(ChatProtocal.DELIMITER);
		SynchronizedPrintWriter writer = MainControl.map.get(msgs[0]);
		if (writer != null) {
			writer.println(line);

		} else {
			out.println(ChatProtocal.NOTONLINE);
			System.out.println(msgs[0] + "不在线");
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
			System.out.println("私人消息持久化错误！");
		}
		
	}

	// 回复在线用户列表
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
		System.out.println("返回在线用户列表");
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
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}

	// 除去消息类型协议头
	private String getRealMsg(String line) {

		return line.substring(ChatProtocal.PROTOCAL_LENGTH, line.length()
				- ChatProtocal.PROTOCAL_LENGTH);
	}

}
