package server;

import java.io.PrintWriter;

public class SynchronizedPrintWriter {
     private PrintWriter out=null;
     
     public SynchronizedPrintWriter(PrintWriter out ){
    	 
    	 this.out=out;
    	 
    	 
     }
     
     public synchronized void  println(String line){
    	 
    	  out.println(line);
    	  out.flush();
     }
     
    public synchronized void close(){
    	
    	out.close();
    }
}
