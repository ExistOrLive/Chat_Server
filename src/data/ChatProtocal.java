package data;

public class ChatProtocal {
	
	//定义协议字符串长度
     public static int PROTOCAL_LENGTH=2;
     //数据分隔符
     public static String DELIMITER="\30";
     //注册数据头
     public static String REGISTER="^&";
     //登录数据头
     public static String LOGIN="$#";
     //私人信息数据头
     public static String PR_MSG="@#";
     //群信息
     public static String GR_MSG="'/";
     //登录时该账号已有人登录
     public static String LOGINED="$$";
     //注册时该账号已存在
     public static String AC_EXIST="%%";
     //注册成功
     public static String REGISTER_SUCCESS=".?";
     //注册失败
     public static String REGISTER_FAILED="@@";
     //登录时该账号不存在
     public static String AC_NOTEX="!!";
     //密码错误
     public static String WR_PASSWD="[]";
     //密码正确 
     public static String SUCCESS_LOGIN="<>";
     //账号不在线
     public static String NOTONLINE="`)";
     //请求在线账户
     public static String REQUESTFORLIST="*&";
     //断开连接
     public static String DISCONNECT="+%";
     
     
}
