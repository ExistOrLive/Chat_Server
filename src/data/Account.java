package data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Account implements Serializable{
    
	
	private String name;
	private String passwd;
	private int age;
	private char sex;
	private String telNum;
	
	public Account(String name,String passwd,int age,char sex,String telNum)
	{
		this.name=name;
		this.passwd=passwd;
	    this.age=age;
	    this.sex=sex;
	    this.telNum=telNum;
		
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	
	
	
	
	
	
	
	
}
