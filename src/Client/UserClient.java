package client;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import DLMSIdl.DLMS;
import DLMSIdl.DLMSHelper;
import Interface.CON_ServerInterface;
import Interface.MCG_ServerInterface;
import Interface.MON_ServerInterface;
import server.implementation.*;

public class UserClient {

	
	static Logger log= Logger.getLogger("Client");
	static FileHandler fh;
	public static void main(String [] args) throws Exception {
		
		LogManager.getLogManager().readConfiguration(new FileInputStream("properties/user_logging.properties"));
		DatagramSocket aSocket = null;
   	 	Scanner sc= new Scanner(System.in);
   	 	String status="";
   	 	//String userID[]= new String[] {"CONU1001","CONU1002","CONU1003","MCGU2001","MCGU2002","MCGU2003","MONU3001","MONU3002","MONU3003"};
   	 	System.out.println("Enter userID: ");
   	 	String id= sc.next();
   	 	if(id.matches("(CONU)([0-9]{4})")) {
   		log.info("Log Created for user "+id);
   		 while(true) {
   		 System.out.println("Select an operation:"+"\n"+"1.Borrow Item"+"\n"+"2.Return Item"+"\n"+"3.Find Item"+"\n"+"4.Exchange Item"+"\n"+"5.Logout");
   		 int n=sc.nextInt();
   		 if(n==1) {
   			 log.info(id+" requested for Borrowing an Item");
   			 
   			 System.out.println("Enter itemID:");
   			 String item_id= sc.next();
   			 if(item_id.matches("(CON)([0-9]{4})")) {
   				 
      			 DLMS obj= connectToServer(id,args);
   				 status=obj.borrowItem(id, item_id, 30);
   				 if(status.equals("Book Not Available")) {
   					 System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
   					 int choice=sc.nextInt();
   					 if(choice==1) {
   						 log.info(id+" requested for adding into the queue");
   						 status=obj.addToQueue(id,item_id);
   						 //System.out.println(status);
   						 log.info(status);
   					 }else {
   						 log.info(id+" requesting not to add in the queue");
   					 }
   				 }
   				 log.info(id+" needs to borrow "+item_id+" for a duration of 30 days");
   				 System.out.println(status);
   				 log.info(status);
   			  } else if(item_id.matches("(MCG)([0-9]{4})")) {
   				  
   				try{
   					log.info("UDP Datagram Socket Started");
   					aSocket = new DatagramSocket();
   					String info="Borrow Item"+"-"+id+"-"+item_id+"-"+30+"-";
   					byte [] message = info.getBytes(); 
   					InetAddress aHost = InetAddress.getByName("localhost");
   					int serverPort = 6788;	
   					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
   					aSocket.send(request);   					
   					byte [] buffer = new byte[100];
   					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
   					aSocket.receive(reply);
   					status=new String(reply.getData());
   					if(status.contains("Book Not Available")) {
   						System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
      					 int choice=sc.nextInt();
      					 if(choice==1) {
      						 log.info(id+" requested for adding into the queue");
      						aSocket = new DatagramSocket();
      	   					String info1="Add Queue"+"-"+id+"-"+item_id+"-";
      	   					byte [] message1 = info1.getBytes(); 
      	   					InetAddress aHost1 = InetAddress.getByName("localhost");
      	   					int serverPort1 = 6788;	
      	   					DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
      	   					aSocket.send(request1);   					
      	   					byte [] buffer1 = new byte[100];
      	   					DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
      	   					aSocket.receive(reply1);
      	   					status=new String(reply1.getData());
      						 //System.out.println(status);
      						 log.info(status);
      					 }else {
      						 log.info(id+" requesting not to add in the queue");
      					 }
   					}
   					log.info("Reply from the server is: "+ status);
   					System.out.println("Reply from the server is: "+status);
   				}
   				catch(SocketException e){
   					System.out.println("Socket: "+e.getMessage());
   				}
   				catch(IOException e){
   					e.printStackTrace();
   					System.out.println("IO: "+e.getMessage());
   				}
   				finally{
   					if(aSocket != null) aSocket.close();  
   					}
   			  }
   			else if(item_id.matches("(MON)([0-9]{4})")) {
 				  
   				try{
   					log.info("UDP Datagram Socket Started");
   					aSocket = new DatagramSocket();
   					String info="Borrow Item"+"-"+id+"-"+item_id+"-"+30+"-";
   					byte [] message = info.getBytes(); 
   					InetAddress aHost = InetAddress.getByName("localhost");
   					int serverPort = 6787;	
   					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
   					aSocket.send(request);   					
   					byte [] buffer = new byte[100];
   					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
   					aSocket.receive(reply);
   					status=new String(reply.getData());
   					if(status.contains("Book Not Available")) {
   						System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
      					 int choice=sc.nextInt();
      					 if(choice==1) {
      						 log.info(id+" requested for adding into the queue");
      						aSocket = new DatagramSocket();
      	   					String info1="Add Queue"+"-"+id+"-"+item_id+"-";
      	   					byte [] message1 = info1.getBytes(); 
      	   					InetAddress aHost1 = InetAddress.getByName("localhost");
      	   					int serverPort1 = 6787;	
      	   					DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
      	   					aSocket.send(request1);   					
      	   					byte [] buffer1 = new byte[100];
      	   					DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
      	   					aSocket.receive(reply1);
      	   					status=new String(reply1.getData());
      						 //System.out.println(status);
      						 log.info(status);
      					 }else {
      						 log.info(id+" requesting not to add in the queue");
      					 }
   					 }
   					log.info("Reply received from the server is: "+ status);
   					System.out.println("Reply received from the server is: "+ status);
   				}
   				catch(SocketException e){
   					System.out.println("Socket: "+e.getMessage());
   				}
   				catch(IOException e){
   					e.printStackTrace();
   					System.out.println("IO: "+e.getMessage());
   				}
   				finally{
   					if(aSocket != null) aSocket.close();  
   					}
   			  }
   			 }
   		
   		 else if(n==2){
   			 DLMS obj= connectToServer(id,args);
   			 System.out.println("Enter itemID:");
  			 String item_id= sc.next();
   			 if(item_id.matches("(CON)([0-9]{4})")) {
   				 status=obj.returnItem(id,item_id);
   				 log.info(status);
   				 System.out.println(status);
   			 } else if(item_id.matches("(MCG)([0-9]{4})")) {
  				  
  				try{
  					log.info("UDP Datagram Socket Started");
  					aSocket = new DatagramSocket();
  					String info="Return Item"+"-"+id+"-"+item_id+"-"+30+"-";
  					byte [] message = info.getBytes(); 
  					InetAddress aHost = InetAddress.getByName("localhost");
  					int serverPort = 6788;	
  					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
  					aSocket.send(request);   					
  					byte [] buffer = new byte[100];
  					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
  					aSocket.receive(reply);
  					status=new String(reply.getData());
  					log.info("Reply received from the server is: "+ status);
  					System.out.println("Reply received from the server is: "+ status);
  				}
  				catch(SocketException e){
  					System.out.println("Socket: "+e.getMessage());
  				}
  				catch(IOException e){
  					e.printStackTrace();
  					System.out.println("IO: "+e.getMessage());
  				}
  				finally{
  					if(aSocket != null) aSocket.close();  
  					}
  			  }
  			else if(item_id.matches("(MON)([0-9]{4})")) {
				  
  				try{
  					log.info("UDP Datagram Socket Started");
  					aSocket = new DatagramSocket();
  					String info="Return Item"+"-"+id+"-"+item_id+"-"+30+"-";
  					byte [] message = info.getBytes(); 
  					InetAddress aHost = InetAddress.getByName("localhost");
  					int serverPort = 6787;	
  					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
  					aSocket.send(request);   					
  					byte [] buffer = new byte[100];
  					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
  					aSocket.receive(reply);
  					status=new String(reply.getData());
  					log.info("Reply received from the server is: "+ status);
  					System.out.println("Reply received from the server is: "+ status);
  				}
  				catch(SocketException e){
  					System.out.println("Socket: "+e.getMessage());
  				}
  				catch(IOException e){
  					e.printStackTrace();
  					System.out.println("IO: "+e.getMessage());
  				}
  				finally{
  					if(aSocket != null) aSocket.close();  
  					}
  			  }
   		 }
   		 
   		 else if(n==3){
    		 DLMS obj= connectToServer(id,args);
   			 String item_name; 
			 System.out.println("Enter itemName:"); 
		     item_name= sc.next();
   			 obj.initialize(id,item_name);
   			 obj.startServer();
		     TimeUnit.SECONDS.sleep(5);
   			 status=obj.getStatus();
   			 status=status.replace("","");
   			 log.info(status);
   			 System.out.println(status);
   		 }
   		 else if(n==4) {
  			 log.info(id+" requested for Exchanging an Item");
  			 
  			 System.out.println("Enter borrowed itemID:");
  			 String old_item_id= sc.next();
  			 System.out.println("Enter new itemID:");
 			 String new_item_id= sc.next();
 			 DLMS obj= connectToServer(id,args);
 			 if(old_item_id.matches("(CON)([0-9]{4})")) {
     			 
  				 status=obj.checkBorrowList(id, old_item_id);
  				 log.info(status);
  				 if(status.equals("Book Borrowed")) {
  					if(new_item_id.matches("(CON)([0-9]{4})")) {
  					   status=obj.checkBookAvailability(new_item_id);
  					   log.info(status);
  					   if(status.contains("Book Available")) {
  						 status+="-"+obj.borrowItem(id,new_item_id,30);
  						 status+="-"+obj.returnItem(id,old_item_id);
  					   }else {
  						   log.info(status);
  					   }
  					 System.out.println("Reply from the server is: "+status);
  					 } else if(new_item_id.matches("(MCG)([0-9]{4})")) {				  
  						 try{
  							 log.info("UDP Datagram Socket Started");
  							 aSocket = new DatagramSocket();
  							 String info="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
  							 byte [] message = info.getBytes(); 
  							 InetAddress aHost = InetAddress.getByName("localhost");
  							 int serverPort = 6788;	
  							 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
  							 aSocket.send(request);   					
  							 byte [] buffer = new byte[100];
  							 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
  							 aSocket.receive(reply);
  							 status=new String(reply.getData());
  							 if(status.contains("Book Available")) {
  							  log.info(status);
  							  status+="-"+obj.returnItem(id,old_item_id);
  							 }else {
  									log.info(status);
  								}
  							 log.info("Reply from the server is: "+ status);
  							 System.out.println("Reply from the server is: "+status);
  						 }
  						 catch(SocketException e){
  							 System.out.println("Socket: "+e.getMessage());
  						 }
  						 catch(IOException e){
  							 e.printStackTrace();
  							 System.out.println("IO: "+e.getMessage());
  						 }
  						 finally{
  							 if(aSocket != null) aSocket.close();  
  						 }
  					 }
  			         else if(new_item_id.matches("(MON)([0-9]{4})")) {
				  
  			        	 try{
  			        		 log.info("UDP Datagram Socket Started");
  			        		 aSocket = new DatagramSocket();
  			        		 String info="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
  			        		 byte [] message = info.getBytes(); 
  			        		 InetAddress aHost = InetAddress.getByName("localhost");
  			        		 int serverPort = 6787;	
  			        		 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
  			        		 aSocket.send(request);   					
  			        		 byte [] buffer = new byte[100];
  			        		 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
  			        		 aSocket.receive(reply);
  			        		 status=new String(reply.getData());
  			        		if(status.contains("Book Available")) {
    							  log.info(status);
    							  status+="-"+obj.returnItem(id,old_item_id);
    							 }else {
    									log.info(status);
    								}
  			        		 log.info("Reply received from the server is: "+ status);
  			        		 System.out.println("Reply received from the server is: "+ status);
  			        	 }
  			        	 catch(SocketException e){
  			        		 System.out.println("Socket: "+e.getMessage());
  			        	 }
  			        	 catch(IOException e){
  			        		 e.printStackTrace();
  			        		 System.out.println("IO: "+e.getMessage());
  			        	 }
  			        	 finally{
  			        		 if(aSocket != null) aSocket.close();  
  			        	 }
  			         }
  			 }else {
  				 log.info(status);
  				 System.out.println(status);
  			 }
  			}
  			else if(old_item_id.matches("(MCG)([0-9]{4})")) {				  
					 try{
						 log.info("UDP Datagram Socket Started");
						 aSocket = new DatagramSocket();
						 String info="Check Borrow List"+"-"+id+"-"+old_item_id+"-"+30+"-";
						 byte [] message = info.getBytes(); 
						 InetAddress aHost = InetAddress.getByName("localhost");
						 int serverPort = 6788;	
						 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
						 aSocket.send(request);   					
						 byte [] buffer = new byte[100];
						 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
						 aSocket.receive(reply);
						 status=new String(reply.getData());
						 if(status.contains("Book Borrowed")) {
							 
							 if(new_item_id.matches("(CON)([0-9]{4})")) {
			  					   status=obj.checkBookAvailability(new_item_id);
			  					   log.info(status);
			  					   if(status.contains("Book Available")) {
			  						  status+="-"+obj.borrowItem(id,new_item_id,30);
			  						aSocket = new DatagramSocket();
				  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
				  					byte [] message2 = info2.getBytes(); 
				  					InetAddress aHost2 = InetAddress.getByName("localhost");
				  					int serverPort2 = 6788;	
				  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
				  					aSocket.send(request2);   					
				  					byte [] buffer2 = new byte[100];
				  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
				  					aSocket.receive(reply2);
				  					status+="-"+new String(reply2.getData());
			  						  
			  					   }else {
			  						   log.info(status);
			  					   }
			  					 } else if(new_item_id.matches("(MCG)([0-9]{4})")) {				  
			  						 try{
			  							 log.info("UDP Datagram Socket Started");
			  							 aSocket = new DatagramSocket();
			  							 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+old_item_id+"-";
			  							 byte [] message1 = info1.getBytes(); 
			  							 InetAddress aHost1 = InetAddress.getByName("localhost");
			  							 int serverPort1 = 6788;	
			  							 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
			  							 aSocket.send(request1);   					
			  							 byte [] buffer1 = new byte[100];
			  							 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
			  							 aSocket.receive(reply1);
			  							 status=new String(reply1.getData());
			  							 if(status.contains("Book Available")) {
			  							  log.info(status);
										    aSocket = new DatagramSocket();
						  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
						  					byte [] message2 = info2.getBytes(); 
						  					InetAddress aHost2 = InetAddress.getByName("localhost");
						  					int serverPort2 = 6788;	
						  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
						  					aSocket.send(request2);   					
						  					byte [] buffer2 = new byte[100];
						  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
						  					aSocket.receive(reply2);
						  					status+="-"+new String(reply2.getData());
			  							 }else {
			  									log.info(status);
			  								}
			  							 log.info("Reply from the server is: "+ status);
			  						 }
			  						 catch(SocketException e){
			  							 System.out.println("Socket: "+e.getMessage());
			  						 }
			  						 catch(IOException e){
			  							 e.printStackTrace();
			  							 System.out.println("IO: "+e.getMessage());
			  						 }
			  						 finally{
			  							 if(aSocket != null) aSocket.close();  
			  						 }
			  					 }
			  			         else if(new_item_id.matches("(MON)([0-9]{4})")) {
							  
			  			        	 try{
			  			        		 log.info("UDP Datagram Socket Started");
			  			        		 aSocket = new DatagramSocket();
			  			        		 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
			  			        		 byte [] message1 = info1.getBytes(); 
			  			        		 InetAddress aHost1 = InetAddress.getByName("localhost");
			  			        		 int serverPort1 = 6787;	
			  			        		 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
			  			        		 aSocket.send(request1);   					
			  			        		 byte [] buffer1 = new byte[100];
			  			        		 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
			  			        		 aSocket.receive(reply1);
			  			        		 status=new String(reply1.getData());
			  			        		if(status.contains("Book Available")) {
			    							  log.info(status);
			  							    aSocket = new DatagramSocket();
						  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
						  					byte [] message2 = info2.getBytes(); 
						  					InetAddress aHost2 = InetAddress.getByName("localhost");
						  					int serverPort2 = 6788;	
						  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
						  					aSocket.send(request2);   					
						  					byte [] buffer2 = new byte[100];
						  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
						  					aSocket.receive(reply2);
						  					status+="-"+new String(reply2.getData());
			  			        		}else {
			    									log.info(status);
			    								}
			  			        		 log.info("Reply received from the server is: "+ status);
			  			        	 }
			  			        	 catch(SocketException e){
			  			        		 System.out.println("Socket: "+e.getMessage());
			  			        	 }
			  			        	 catch(IOException e){
			  			        		 e.printStackTrace();
			  			        		 System.out.println("IO: "+e.getMessage());
			  			        	 }
			  			        	 finally{
			  			        		 if(aSocket != null) aSocket.close();  
			  			        	 }
			  			         }
							 

			  					
						  }else {
								log.info(status);
							}
						 log.info("Reply from the server is: "+ status);
						 System.out.println("Reply from the server is: "+status);
					 }
					 catch(SocketException e){
						 System.out.println("Socket: "+e.getMessage());
					 }
					 catch(IOException e){
						 e.printStackTrace();
						 System.out.println("IO: "+e.getMessage());
					 }
					 finally{
						 if(aSocket != null) aSocket.close();  
					 }
				 }
 			 
  			else if(old_item_id.matches("(MON)([0-9]{4})")) {				  
				 try{
					 log.info("UDP Datagram Socket Started");
					 aSocket = new DatagramSocket();
					 String info="Check Borrow List"+"-"+id+"-"+old_item_id+"-"+30+"-";
					 byte [] message = info.getBytes(); 
					 InetAddress aHost = InetAddress.getByName("localhost");
					 int serverPort = 6787;	
					 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
					 aSocket.send(request);   					
					 byte [] buffer = new byte[100];
					 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					 aSocket.receive(reply);
					 status=new String(reply.getData());
					 if(status.contains("Book Borrowed")) {
						 
						 if(new_item_id.matches("(CON)([0-9]{4})")) {
		  					   status=obj.checkBookAvailability(new_item_id);
		  					   log.info(status);
		  					   if(status.contains("Book Available")) {
		  						 status+="-"+obj.borrowItem(id,new_item_id,30);
		  						aSocket = new DatagramSocket();
			  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
			  					byte [] message2 = info2.getBytes(); 
			  					InetAddress aHost2 = InetAddress.getByName("localhost");
			  					int serverPort2 = 6787;	
			  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
			  					aSocket.send(request2);   					
			  					byte [] buffer2 = new byte[100];
			  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
			  					aSocket.receive(reply2);
			  					status+="-"+new String(reply2.getData());
		  					   }else {
		  						   log.info(status);
		  					   }
		  					 } else if(new_item_id.matches("(MCG)([0-9]{4})")) {				  
		  						 try{
		  							 log.info("UDP Datagram Socket Started");
		  							 aSocket = new DatagramSocket();
		  							 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
		  							 byte [] message1 = info1.getBytes(); 
		  							 InetAddress aHost1 = InetAddress.getByName("localhost");
		  							 int serverPort1 = 6788;	
		  							 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
		  							 aSocket.send(request1);   					
		  							 byte [] buffer1 = new byte[100];
		  							 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
		  							 aSocket.receive(reply1);
		  							 status=new String(reply1.getData());
		  							 if(status.contains("Book Available")) {
		  							  log.info(status);
		  						    aSocket = new DatagramSocket();
				  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
				  					byte [] message2 = info2.getBytes(); 
				  					InetAddress aHost2 = InetAddress.getByName("localhost");
				  					int serverPort2 = 6787;	
				  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
				  					aSocket.send(request2);   					
				  					byte [] buffer2 = new byte[100];
				  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
				  					aSocket.receive(reply2);
				  					status+="-"+new String(reply2.getData());
		  							 }else {
		  									log.info(status);
		  								}
		  							 log.info("Reply from the server is: "+ status);
		  							}
		  						 catch(SocketException e){
		  							 System.out.println("Socket: "+e.getMessage());
		  						 }
		  						 catch(IOException e){
		  							 e.printStackTrace();
		  							 System.out.println("IO: "+e.getMessage());
		  						 }
		  						 finally{
		  							 if(aSocket != null) aSocket.close();  
		  						 }
		  					 }
		  			         else if(new_item_id.matches("(MON)([0-9]{4})")) {
						  
		  			        	 try{
		  			        		 log.info("UDP Datagram Socket Started");
		  			        		 aSocket = new DatagramSocket();
		  			        		 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+old_item_id+"-";
		  			        		 byte [] message1 = info1.getBytes(); 
		  			        		 InetAddress aHost1 = InetAddress.getByName("localhost");
		  			        		 int serverPort1 = 6787;	
		  			        		 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
		  			        		 aSocket.send(request1);   					
		  			        		 byte [] buffer1 = new byte[100];
		  			        		 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
		  			        		 aSocket.receive(reply1);
		  			        		 status=new String(reply1.getData());
		  			        		if(status.contains("Book Available")) {
		    							  log.info(status);
		    							    aSocket = new DatagramSocket();
		    			  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
		    			  					byte [] message2 = info2.getBytes(); 
		    			  					InetAddress aHost2 = InetAddress.getByName("localhost");
		    			  					int serverPort2 = 6787;	
		    			  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
		    			  					aSocket.send(request2);   					
		    			  					byte [] buffer2 = new byte[100];
		    			  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
		    			  					aSocket.receive(reply2);
		    			  					status+="-"+new String(reply2.getData());
		    							 }else {
		    									log.info(status);
		    								}
		  			        		 log.info("Reply received from the server is: "+ status);
		  			        		}
		  			        	 catch(SocketException e){
		  			        		 System.out.println("Socket: "+e.getMessage());
		  			        	 }
		  			        	 catch(IOException e){
		  			        		 e.printStackTrace();
		  			        		 System.out.println("IO: "+e.getMessage());
		  			        	 }
		  			        	 finally{
		  			        		 if(aSocket != null) aSocket.close();  
		  			        	 }
		  			         } 

						 
					  }else {
							log.info(status);
						}
					 log.info("Reply from the server is: "+ status);
					 System.out.println("Reply from the server is: "+status);
				 }
				 catch(SocketException e){
					 System.out.println("Socket: "+e.getMessage());
				 }
				 catch(IOException e){
					 e.printStackTrace();
					 System.out.println("IO: "+e.getMessage());
				 }
				 finally{
					 if(aSocket != null) aSocket.close();  
				 }
			 }
   		}
   		 else {
   			log.info(id+" logged out ");
   			break;
   		 }
   		}   		 
   	}
   	 	else if(id.matches("(MCGU)([0-9]{4})")) {
   	   		log.info("Log Created for user "+id);
      		 while(true) {
      		 System.out.println("Select an operation:"+"\n"+"1.Borrow Item"+"\n"+"2.Return Item"+"\n"+"3.Find Item"+"\n"+"4.Exchange Item"+"\n"+"5.Logout");
      		 int n=sc.nextInt();
      		 if(n==1) {
      			 log.info(id+" requested for Borrowing an Item");
      			 
      			 System.out.println("Enter itemID:");
      			 String item_id= sc.next();
      			 if(item_id.matches("(MCG)([0-9]{4})")) {
      				 DLMS obj= connectToServer(id,args);
      				 status=obj.borrowItem(id, item_id, 30);
      				 if(status.equals("Book Not Available")) {
      					 System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
      					 int choice=sc.nextInt();
      					 if(choice==1) {
      						 log.info(id+" requested for adding into the queue");
      						 status=obj.addToQueue(id,item_id);
      						 //System.out.println(status);
      						 log.info(status);
      					 }else {
      						 log.info(id+" requesting not to add in the queue");
      					 }
      				 }
      				 log.info(id+" needs to borrow "+item_id+" for a duration of 30 days");
      				 System.out.println(status);
      				 log.info(status);
      			  } else if(item_id.matches("(CON)([0-9]{4})")) {
      				  
      				try{
      					log.info("UDP Datagram Socket Started");
      					aSocket = new DatagramSocket();
      					String info="Borrow Item"+"-"+id+"-"+item_id+"-"+30+"-";
      					byte [] message = info.getBytes(); 
      					InetAddress aHost = InetAddress.getByName("localhost");
      					int serverPort = 6789;	
      					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
      					aSocket.send(request);   					
      					byte [] buffer = new byte[100];
      					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
      					aSocket.receive(reply);
      					status=new String(reply.getData());
      					if(status.contains("Book Not Available")) {
       						System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
          					 int choice=sc.nextInt();
          					 if(choice==1) {
          						log.info(id+" requested for adding into the queue");
          						aSocket = new DatagramSocket();
          	   					String info1="Add Queue"+"-"+id+"-"+item_id+"-";
          	   					byte [] message1 = info1.getBytes(); 
          	   					InetAddress aHost1 = InetAddress.getByName("localhost");
          	   					int serverPort1 = 6789;	
          	   					DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
          	   					aSocket.send(request1);   					
          	   					byte [] buffer1 = new byte[100];
          	   					DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
          	   					aSocket.receive(reply1);
          	   					status=new String(reply1.getData());
          						 //System.out.println(status);
          						 log.info(status);
          					 }else {
          						 log.info(id+" requesting not to add in the queue");
          					 }
       					}
      					log.info("Reply received from the server is: "+ status);
      					System.out.println("Reply received from the server is: "+ status);
      				}
      				catch(SocketException e){
      					System.out.println("Socket: "+e.getMessage());
      				}
      				catch(IOException e){
      					e.printStackTrace();
      					System.out.println("IO: "+e.getMessage());
      				}
      				finally{
      					if(aSocket != null) aSocket.close();  
      					}
      			  }
      			else if(item_id.matches("(MON)([0-9]{4})")) {
    				  
      				try{
      					log.info("UDP Datagram Socket Started");
      					aSocket = new DatagramSocket();
      					String info="Borrow Item"+"-"+id+"-"+item_id+"-"+30+"-";
      					byte [] message = info.getBytes(); 
      					InetAddress aHost = InetAddress.getByName("localhost");
      					int serverPort = 6787;	
      					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
      					aSocket.send(request);   					
      					byte [] buffer = new byte[100];
      					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
      					aSocket.receive(reply);
      					status=new String(reply.getData());
      					if(status.contains("Book Not Available")) {
       						System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
          					 int choice=sc.nextInt();
          					 if(choice==1) {
          						 log.info(id+" requested for adding into the queue");
          						aSocket = new DatagramSocket();
          	   					String info1="Add Queue"+"-"+id+"-"+item_id+"-";
          	   					byte [] message1 = info1.getBytes(); 
          	   					InetAddress aHost1 = InetAddress.getByName("localhost");
          	   					int serverPort1 = 6787;	
          	   					DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
          	   					aSocket.send(request1);   					
          	   					byte [] buffer1 = new byte[100];
          	   					DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
          	   					aSocket.receive(reply1);
          	   					status=new String(reply1.getData());
          						 //System.out.println(status);
          						 log.info(status);
          					 }else {
          						 log.info(id+" requesting not to add in the queue");
          					 }
       					}
      					log.info("Reply received from the server is: "+ status);
      					System.out.println("Reply received from the server is: "+ status);
      				}
      				catch(SocketException e){
      					System.out.println("Socket: "+e.getMessage());
      				}
      				catch(IOException e){
      					e.printStackTrace();
      					System.out.println("IO: "+e.getMessage());
      				}
      				finally{
      					if(aSocket != null) aSocket.close();  
      					}
      			  }
      			 }
      		
      		 else if(n==2){
      			 DLMS obj= connectToServer(id,args);
      			 System.out.println("Enter itemID:");
     			 String item_id= sc.next();
      			 if(item_id.matches("(MCG)([0-9]{4})")) {
      				 status=obj.returnItem(id,item_id);
      				 log.info(status);
      				 System.out.println(status);
      			 } else if(item_id.matches("(CON)([0-9]{4})")) {
     				  
     				try{
     					log.info("UDP Datagram Socket Started");
     					aSocket = new DatagramSocket();
     					String info="Return Item"+"-"+id+"-"+item_id+"-"+30+"-";
     					byte [] message = info.getBytes(); 
     					InetAddress aHost = InetAddress.getByName("localhost");
     					int serverPort = 6789;	
     					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
     					aSocket.send(request);   					
     					byte [] buffer = new byte[100];
     					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
     					aSocket.receive(reply);
     					status=new String(reply.getData());
     					log.info("Reply received from the server is: "+ status);
     					System.out.println("Reply received from the server is: "+ status);
     				}
     				catch(SocketException e){
     					System.out.println("Socket: "+e.getMessage());
     				}
     				catch(IOException e){
     					e.printStackTrace();
     					System.out.println("IO: "+e.getMessage());
     				}
     				finally{
     					if(aSocket != null) aSocket.close();  
     					}
     			  }
     			else if(item_id.matches("(MON)([0-9]{4})")) {
   				  
     				try{
     					log.info("UDP Datagram Socket Started");
     					aSocket = new DatagramSocket();
     					String info="Return Item"+"-"+id+"-"+item_id+"-"+30+"-";
     					byte [] message = info.getBytes(); 
     					InetAddress aHost = InetAddress.getByName("localhost");
     					int serverPort = 6787;	
     					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
     					aSocket.send(request);   					
     					byte [] buffer = new byte[100];
     					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
     					aSocket.receive(reply);
     					status=new String(reply.getData());
     					log.info("Reply received from the server is: "+ status);
     					System.out.println("Reply received from the server is: "+ status);
     				}
     				catch(SocketException e){
     					System.out.println("Socket: "+e.getMessage());
     				}
     				catch(IOException e){
     					e.printStackTrace();
     					System.out.println("IO: "+e.getMessage());
     				}
     				finally{
     					if(aSocket != null) aSocket.close();  
     					}
     			  }
      		 }
      		
      		 else if(n==3){
      			 DLMS obj= connectToServer(id,args);
      			 String item_name; 
   			     System.out.println("Enter itemName:"); 
   		         item_name= sc.next();
      			 obj.initialize(id,item_name);
   		         obj.startServer();
   		         TimeUnit.SECONDS.sleep(5);
      			 status=obj.getStatus();
      			 log.info(status);
      			 System.out.println(status);
      		 }
      		 
      		else if(n==4) {
     			 log.info(id+" requested for Exchanging an Item");
     			 
     			 System.out.println("Enter borrowed itemID:");
     			 String old_item_id= sc.next();
     			 System.out.println("Enter new itemID:");
    			 String new_item_id= sc.next();
    			 DLMS obj= connectToServer(id,args);
    			 if(old_item_id.matches("(MCG)([0-9]{4})")) {
        			 
     				 status=obj.checkBorrowList(id, old_item_id);
     				 log.info(status);
     				 if(status.equals("Book Borrowed")) {
     					if(new_item_id.matches("(MCG)([0-9]{4})")) {
     					   status=obj.checkBookAvailability(new_item_id);
     					   log.info(status);
     					   if(status.contains("Book Available")) {
     						 status+="-"+obj.borrowItem(id,new_item_id,30);
     						 status+="-"+obj.returnItem(id,old_item_id);
     					   }else {
     						   log.info(status);
     					   }
     					 System.out.println("Reply from the server is: "+status);
     					 } else if(new_item_id.matches("(CON)([0-9]{4})")) {				  
     						 try{
     							 log.info("UDP Datagram Socket Started");
     							 aSocket = new DatagramSocket();
     							 String info="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
     							 byte [] message = info.getBytes(); 
     							 InetAddress aHost = InetAddress.getByName("localhost");
     							 int serverPort = 6789;	
     							 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
     							 aSocket.send(request);   					
     							 byte [] buffer = new byte[100];
     							 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
     							 aSocket.receive(reply);
     							 status=new String(reply.getData());
     							 if(status.contains("Book Available")) {
     							  log.info(status);
     							  status+="-"+obj.returnItem(id,old_item_id);
     							 }else {
     									log.info(status);
     								}
     							 log.info("Reply from the server is: "+ status);
     							 System.out.println("Reply from the server is: "+status);
     						 }
     						 catch(SocketException e){
     							 System.out.println("Socket: "+e.getMessage());
     						 }
     						 catch(IOException e){
     							 e.printStackTrace();
     							 System.out.println("IO: "+e.getMessage());
     						 }
     						 finally{
     							 if(aSocket != null) aSocket.close();  
     						 }
     					 }
     			         else if(new_item_id.matches("(MON)([0-9]{4})")) {
   				  
     			        	 try{
     			        		 log.info("UDP Datagram Socket Started");
     			        		 aSocket = new DatagramSocket();
     			        		 String info="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
     			        		 byte [] message = info.getBytes(); 
     			        		 InetAddress aHost = InetAddress.getByName("localhost");
     			        		 int serverPort = 6787;	
     			        		 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
     			        		 aSocket.send(request);   					
     			        		 byte [] buffer = new byte[100];
     			        		 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
     			        		 aSocket.receive(reply);
     			        		 status=new String(reply.getData());
     			        		if(status.contains("Book Available")) {
       							  log.info(status);
       							  status+="-"+obj.returnItem(id,old_item_id);
       							 }else {
       									log.info(status);
       								}
     			        		 log.info("Reply received from the server is: "+ status);
     			        		 System.out.println("Reply received from the server is: "+ status);
     			        	 }
     			        	 catch(SocketException e){
     			        		 System.out.println("Socket: "+e.getMessage());
     			        	 }
     			        	 catch(IOException e){
     			        		 e.printStackTrace();
     			        		 System.out.println("IO: "+e.getMessage());
     			        	 }
     			        	 finally{
     			        		 if(aSocket != null) aSocket.close();  
     			        	 }
     			         }
     			 }else {
     				 log.info(status);
     				 System.out.println(status);
     			 }
     			}
     			else if(old_item_id.matches("(CON)([0-9]{4})")) {				  
   					 try{
   						 log.info("UDP Datagram Socket Started");
   						 aSocket = new DatagramSocket();
   						 String info="Check Borrow List"+"-"+id+"-"+old_item_id+"-"+30+"-";
   						 byte [] message = info.getBytes(); 
   						 InetAddress aHost = InetAddress.getByName("localhost");
   						 int serverPort = 6789;	
   						 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
   						 aSocket.send(request);   					
   						 byte [] buffer = new byte[100];
   						 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
   						 aSocket.receive(reply);
   						 status=new String(reply.getData());
   						 if(status.contains("Book Borrowed")) {
   							 
   							 if(new_item_id.matches("(MCG)([0-9]{4})")) {
   			  					   status=obj.checkBookAvailability(new_item_id);
   			  					   log.info(status);
   			  					   if(status.contains("Book Available")) {
   			  						  status+="-"+obj.borrowItem(id,new_item_id,30);
   			  						aSocket = new DatagramSocket();
   				  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
   				  					byte [] message2 = info2.getBytes(); 
   				  					InetAddress aHost2 = InetAddress.getByName("localhost");
   				  					int serverPort2 = 6789;	
   				  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
   				  					aSocket.send(request2);   					
   				  					byte [] buffer2 = new byte[100];
   				  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
   				  					aSocket.receive(reply2);
   				  					status+="-"+new String(reply2.getData());
   			  						  
   			  					   }else {
   			  						   log.info(status);
   			  					   }
   			  					 } else if(new_item_id.matches("(CON)([0-9]{4})")) {				  
   			  						 try{
   			  							 log.info("UDP Datagram Socket Started");
   			  							 aSocket = new DatagramSocket();
   			  							 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+old_item_id+"-";
   			  							 byte [] message1 = info1.getBytes(); 
   			  							 InetAddress aHost1 = InetAddress.getByName("localhost");
   			  							 int serverPort1 = 6789;	
   			  							 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
   			  							 aSocket.send(request1);   					
   			  							 byte [] buffer1 = new byte[100];
   			  							 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
   			  							 aSocket.receive(reply1);
   			  							 status=new String(reply1.getData());
   			  							 if(status.contains("Book Available")) {
   			  							  log.info(status);
   										    aSocket = new DatagramSocket();
   						  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
   						  					byte [] message2 = info2.getBytes(); 
   						  					InetAddress aHost2 = InetAddress.getByName("localhost");
   						  					int serverPort2 = 6789;	
   						  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
   						  					aSocket.send(request2);   					
   						  					byte [] buffer2 = new byte[100];
   						  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
   						  					aSocket.receive(reply2);
   						  					status+="-"+new String(reply2.getData());
   			  							 }else {
   			  									log.info(status);
   			  								}
   			  							 log.info("Reply from the server is: "+ status);
   			  						 }
   			  						 catch(SocketException e){
   			  							 System.out.println("Socket: "+e.getMessage());
   			  						 }
   			  						 catch(IOException e){
   			  							 e.printStackTrace();
   			  							 System.out.println("IO: "+e.getMessage());
   			  						 }
   			  						 finally{
   			  							 if(aSocket != null) aSocket.close();  
   			  						 }
   			  					 }
   			  			         else if(new_item_id.matches("(MON)([0-9]{4})")) {
   							  
   			  			        	 try{
   			  			        		 log.info("UDP Datagram Socket Started");
   			  			        		 aSocket = new DatagramSocket();
   			  			        		 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
   			  			        		 byte [] message1 = info1.getBytes(); 
   			  			        		 InetAddress aHost1 = InetAddress.getByName("localhost");
   			  			        		 int serverPort1 = 6787;	
   			  			        		 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
   			  			        		 aSocket.send(request1);   					
   			  			        		 byte [] buffer1 = new byte[100];
   			  			        		 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
   			  			        		 aSocket.receive(reply1);
   			  			        		 status=new String(reply1.getData());
   			  			        		if(status.contains("Book Available")) {
   			    							  log.info(status);
   			  							    aSocket = new DatagramSocket();
   						  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
   						  					byte [] message2 = info2.getBytes(); 
   						  					InetAddress aHost2 = InetAddress.getByName("localhost");
   						  					int serverPort2 = 6789;	
   						  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
   						  					aSocket.send(request2);   					
   						  					byte [] buffer2 = new byte[100];
   						  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
   						  					aSocket.receive(reply2);
   						  					status+="-"+new String(reply2.getData());
   			  			        		}else {
   			    									log.info(status);
   			    								}
   			  			        		 log.info("Reply received from the server is: "+ status);
   			  			        	 }
   			  			        	 catch(SocketException e){
   			  			        		 System.out.println("Socket: "+e.getMessage());
   			  			        	 }
   			  			        	 catch(IOException e){
   			  			        		 e.printStackTrace();
   			  			        		 System.out.println("IO: "+e.getMessage());
   			  			        	 }
   			  			        	 finally{
   			  			        		 if(aSocket != null) aSocket.close();  
   			  			        	 }
   			  			         }
   							 

   			  					
   						  }else {
   								log.info(status);
   							}
   						 log.info("Reply from the server is: "+ status);
   						 System.out.println("Reply from the server is: "+status);
   					 }
   					 catch(SocketException e){
   						 System.out.println("Socket: "+e.getMessage());
   					 }
   					 catch(IOException e){
   						 e.printStackTrace();
   						 System.out.println("IO: "+e.getMessage());
   					 }
   					 finally{
   						 if(aSocket != null) aSocket.close();  
   					 }
   				 }
    			 
     			else if(old_item_id.matches("(MON)([0-9]{4})")) {				  
   				 try{
   					 log.info("UDP Datagram Socket Started");
   					 aSocket = new DatagramSocket();
   					 String info="Check Borrow List"+"-"+id+"-"+old_item_id+"-"+30+"-";
   					 byte [] message = info.getBytes(); 
   					 InetAddress aHost = InetAddress.getByName("localhost");
   					 int serverPort = 6787;	
   					 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
   					 aSocket.send(request);   					
   					 byte [] buffer = new byte[100];
   					 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
   					 aSocket.receive(reply);
   					 status=new String(reply.getData());
   					 if(status.contains("Book Borrowed")) {
   						 
   						 if(new_item_id.matches("(MCG)([0-9]{4})")) {
   		  					   status=obj.checkBookAvailability(new_item_id);
   		  					   log.info(status);
   		  					   if(status.contains("Book Available")) {
   		  						 status+="-"+obj.borrowItem(id,new_item_id,30);
   		  						aSocket = new DatagramSocket();
   			  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
   			  					byte [] message2 = info2.getBytes(); 
   			  					InetAddress aHost2 = InetAddress.getByName("localhost");
   			  					int serverPort2 = 6787;	
   			  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
   			  					aSocket.send(request2);   					
   			  					byte [] buffer2 = new byte[100];
   			  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
   			  					aSocket.receive(reply2);
   			  					status+="-"+new String(reply2.getData());
   		  					   }else {
   		  						   log.info(status);
   		  					   }
   		  					 } else if(new_item_id.matches("(CON)([0-9]{4})")) {				  
   		  						 try{
   		  							 log.info("UDP Datagram Socket Started");
   		  							 aSocket = new DatagramSocket();
   		  							 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
   		  							 byte [] message1 = info1.getBytes(); 
   		  							 InetAddress aHost1 = InetAddress.getByName("localhost");
   		  							 int serverPort1 = 6789;	
   		  							 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
   		  							 aSocket.send(request1);   					
   		  							 byte [] buffer1 = new byte[100];
   		  							 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
   		  							 aSocket.receive(reply1);
   		  							 status=new String(reply1.getData());
   		  							 if(status.contains("Book Available")) {
   		  							  log.info(status);
   		  						    aSocket = new DatagramSocket();
   				  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
   				  					byte [] message2 = info2.getBytes(); 
   				  					InetAddress aHost2 = InetAddress.getByName("localhost");
   				  					int serverPort2 = 6787;	
   				  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
   				  					aSocket.send(request2);   					
   				  					byte [] buffer2 = new byte[100];
   				  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
   				  					aSocket.receive(reply2);
   				  					status+="-"+new String(reply2.getData());
   		  							 }else {
   		  									log.info(status);
   		  								}
   		  							 log.info("Reply from the server is: "+ status);
   		  							}
   		  						 catch(SocketException e){
   		  							 System.out.println("Socket: "+e.getMessage());
   		  						 }
   		  						 catch(IOException e){
   		  							 e.printStackTrace();
   		  							 System.out.println("IO: "+e.getMessage());
   		  						 }
   		  						 finally{
   		  							 if(aSocket != null) aSocket.close();  
   		  						 }
   		  					 }
   		  			         else if(new_item_id.matches("(MON)([0-9]{4})")) {
   						  
   		  			        	 try{
   		  			        		 log.info("UDP Datagram Socket Started");
   		  			        		 aSocket = new DatagramSocket();
   		  			        		 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+old_item_id+"-";
   		  			        		 byte [] message1 = info1.getBytes(); 
   		  			        		 InetAddress aHost1 = InetAddress.getByName("localhost");
   		  			        		 int serverPort1 = 6787;	
   		  			        		 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
   		  			        		 aSocket.send(request1);   					
   		  			        		 byte [] buffer1 = new byte[100];
   		  			        		 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
   		  			        		 aSocket.receive(reply1);
   		  			        		 status=new String(reply1.getData());
   		  			        		if(status.contains("Book Available")) {
   		    							  log.info(status);
   		    							    aSocket = new DatagramSocket();
   		    			  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
   		    			  					byte [] message2 = info2.getBytes(); 
   		    			  					InetAddress aHost2 = InetAddress.getByName("localhost");
   		    			  					int serverPort2 = 6787;	
   		    			  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
   		    			  					aSocket.send(request2);   					
   		    			  					byte [] buffer2 = new byte[100];
   		    			  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
   		    			  					aSocket.receive(reply2);
   		    			  					status+="-"+new String(reply2.getData());
   		    							 }else {
   		    									log.info(status);
   		    								}
   		  			        		 log.info("Reply received from the server is: "+ status);
   		  			        		}
   		  			        	 catch(SocketException e){
   		  			        		 System.out.println("Socket: "+e.getMessage());
   		  			        	 }
   		  			        	 catch(IOException e){
   		  			        		 e.printStackTrace();
   		  			        		 System.out.println("IO: "+e.getMessage());
   		  			        	 }
   		  			        	 finally{
   		  			        		 if(aSocket != null) aSocket.close();  
   		  			        	 }
   		  			         } 

   						 
   					  }else {
   							log.info(status);
   						}
   					 log.info("Reply from the server is: "+ status);
   					 System.out.println("Reply from the server is: "+status);
   				 }
   				 catch(SocketException e){
   					 System.out.println("Socket: "+e.getMessage());
   				 }
   				 catch(IOException e){
   					 e.printStackTrace();
   					 System.out.println("IO: "+e.getMessage());
   				 }
   				 finally{
   					 if(aSocket != null) aSocket.close();  
   				 }
   			 }
      		}
      		 
      		 else {
      			log.info(id+" logged out ");
      			break;
      		 }
      		}   		 
      	 } 
   	 	else if(id.matches("(MONU)([0-9]{4})")) {
        		log.info("Log Created for user "+id);
          		 while(true) {
          		 System.out.println("Select an operation:"+"\n"+"1.Borrow Item"+"\n"+"2.Return Item"+"\n"+"3.Find Item"+"\n"+"4.Exchange Item"+"\n"+"5.Logout");
          		 int n=sc.nextInt();
          		 if(n==1) {
          			 log.info(id+" requested for Borrowing an Item");
          			 
          			 System.out.println("Enter itemID:");
          			 String item_id= sc.next();
          			 if(item_id.matches("(MON)([0-9]{4})")) {
          				 DLMS obj= connectToServer(id,args);
          				 status=obj.borrowItem(id, item_id, 30);
          				 if(status.equals("Book Not Available")) {
          					 System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
          					 int choice=sc.nextInt();
          					 if(choice==1) {
          						 log.info(id+" requested for adding into the queue");
          						 status=obj.addToQueue(id,item_id);
          						 //System.out.println(status);
          						 log.info(status);
          					 }else {
          						 log.info(id+" requesting not to add in the queue");
          					 }
          				 }
          				 log.info(id+" needs to borrow "+item_id+" for a duration of 30 days");
          				 log.info(status);
          				 System.out.println(status);
          			  } else if(item_id.matches("(MCG)([0-9]{4})")) {
          				  
          				try{
          					log.info("UDP Datagram Socket Started");
          					aSocket = new DatagramSocket();
          					String info="Borrow Item"+"-"+id+"-"+item_id+"-"+30+"-";
          					byte [] message = info.getBytes(); 
          					InetAddress aHost = InetAddress.getByName("localhost");
          					int serverPort = 6788;	
          					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
          					aSocket.send(request);   					
          					byte [] buffer = new byte[100];
          					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
          					aSocket.receive(reply);
          					status=new String(reply.getData());
          					if(status.contains("Book Not Available")) {
           						System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
              					 int choice=sc.nextInt();
              					 if(choice==1) {
              						 log.info(id+" requested for adding into the queue");
              						aSocket = new DatagramSocket();
              	   					String info1="Add Queue"+"-"+id+"-"+item_id+"-";
              	   					byte [] message1 = info1.getBytes(); 
              	   					InetAddress aHost1 = InetAddress.getByName("localhost");
              	   					int serverPort1 = 6788;	
              	   					DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
              	   					aSocket.send(request1);   					
              	   					byte [] buffer1 = new byte[100];
              	   					DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
              	   					aSocket.receive(reply1);
              	   					status=new String(reply1.getData());
              						 //System.out.println(status);
              						 log.info(status);
              					 }else {
              						 log.info(id+" requesting not to add in the queue");
              					 }
           					}
          					log.info("Reply received from the server is: "+ status);
          					System.out.println("Reply received from the server is: "+ status);
          				}
          				catch(SocketException e){
          					System.out.println("Socket: "+e.getMessage());
          				}
          				catch(IOException e){
          					e.printStackTrace();
          					System.out.println("IO: "+e.getMessage());
          				}
          				finally{
          					if(aSocket != null) aSocket.close();  
          					}
          			  }
          			else if(item_id.matches("(CON)([0-9]{4})")) {
        				  
          				try{
          					log.info("UDP Datagram Socket Started");
          					aSocket = new DatagramSocket();
          					String info="Borrow Item"+"-"+id+"-"+item_id+"-"+30+"-";
          					byte [] message = info.getBytes(); 
          					InetAddress aHost = InetAddress.getByName("localhost");
          					int serverPort = 6789;	
          					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
          					aSocket.send(request);   					
          					byte [] buffer = new byte[100];
          					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
          					aSocket.receive(reply);
          					status=new String(reply.getData());
          					if(status.contains("Book Not Available")) {
           						System.out.println("Book Not Available Please choose an option to get added to queue 1. Yes 2.No"); 
              					 int choice=sc.nextInt();
              					 if(choice==1) {
              						 log.info(id+" requested for adding into the queue");
              						aSocket = new DatagramSocket();
              	   					String info1="Add Queue"+"-"+id+"-"+item_id+"-";
              	   					byte [] message1 = info1.getBytes(); 
              	   					InetAddress aHost1 = InetAddress.getByName("localhost");
              	   					int serverPort1 = 6789;	
              	   					DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
              	   					aSocket.send(request1);   					
              	   					byte [] buffer1 = new byte[100];
              	   					DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
              	   					aSocket.receive(reply1);
              	   					status=new String(reply1.getData());
              						 //System.out.println(status);
              						 log.info(status);
              					 }else {
              						 log.info(id+" requesting not to add in the queue");
              					 }
           					}
          					log.info("Reply received from the server is: "+ status);
          					System.out.println("Reply received from the server is: "+ status);
          				}
          				catch(SocketException e){
          					System.out.println("Socket: "+e.getMessage());
          				}
          				catch(IOException e){
          					e.printStackTrace();
          					System.out.println("IO: "+e.getMessage());
          				}
          				finally{
          					if(aSocket != null) aSocket.close();  
          					}
          			  }
          			 }
          		 
          		 else if(n==2){
          			 DLMS obj= connectToServer(id,args);
          			 System.out.println("Enter itemID:");
         			 String item_id= sc.next();
          			 if(item_id.matches("(MON)([0-9]{4})")) {
          				 status=obj.returnItem(id,item_id);
          				 log.info(status);
          				 System.out.println(status);
          			 } else if(item_id.matches("(MCG)([0-9]{4})")) {
         				  
         				try{
         					log.info("UDP Datagram Socket Started");
         					aSocket = new DatagramSocket();
         					String info="Return Item"+"-"+id+"-"+item_id+"-"+30+"-";
         					byte [] message = info.getBytes(); 
         					InetAddress aHost = InetAddress.getByName("localhost");
         					int serverPort = 6788;	
         					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
         					aSocket.send(request);   					
         					byte [] buffer = new byte[100];
         					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
         					aSocket.receive(reply);
         					status=new String(reply.getData());
         					log.info("Reply received from the server is: "+ status);
         					System.out.println("Reply received from the server is: "+ status);
         				}
         				catch(SocketException e){
         					System.out.println("Socket: "+e.getMessage());
         				}
         				catch(IOException e){
         					e.printStackTrace();
         					System.out.println("IO: "+e.getMessage());
         				}
         				finally{
         					if(aSocket != null) aSocket.close();  
         					}
         			  }
         			else if(item_id.matches("(CON)([0-9]{4})")) {
       				  
         				try{
         					log.info("UDP Datagram Socket Started");
         					aSocket = new DatagramSocket();
         					String info="Return Item"+"-"+id+"-"+item_id+"-"+30+"-";
         					byte [] message = info.getBytes(); 
         					InetAddress aHost = InetAddress.getByName("localhost");
         					int serverPort = 6789;	
         					DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
         					aSocket.send(request);   					
         					byte [] buffer = new byte[100];
         					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
         					aSocket.receive(reply);
         					status=new String(reply.getData());
         					log.info("Reply received from the server is: "+ status);
         					System.out.println("Reply received from the server is: "+ status);
         				}
         				catch(SocketException e){
         					System.out.println("Socket: "+e.getMessage());
         				}
         				catch(IOException e){
         					e.printStackTrace();
         					System.out.println("IO: "+e.getMessage());
         				}
         				finally{
         					if(aSocket != null) aSocket.close();  
         					}
         			  }
          		 }
          		
          		 else if(n==3){
          			 DLMS obj= connectToServer(id,args);
          			 String item_name; 
       			     System.out.println("Enter itemName:"); 
       		         item_name= sc.next();
          			 obj.initialize(id,item_name);
          			 obj.startServer();
      		         TimeUnit.SECONDS.sleep(5);
          			 status=obj.getStatus();
          			 log.info(status);
          			 System.out.println(status);
          		 }
          		 
          		else if(n==4) {
         			 log.info(id+" requested for Exchanging an Item");
         			 
         			 System.out.println("Enter borrowed itemID:");
         			 String old_item_id= sc.next();
         			 System.out.println("Enter new itemID:");
        			 String new_item_id= sc.next();
        			 DLMS obj= connectToServer(id,args);
        			 if(old_item_id.matches("(MON)([0-9]{4})")) {
            			 
         				 status=obj.checkBorrowList(id, old_item_id);
         				 log.info(status);
         				 if(status.equals("Book Borrowed")) {
         					if(new_item_id.matches("(MON)([0-9]{4})")) {
         					   status=obj.checkBookAvailability(new_item_id);
         					   log.info(status);
         					   if(status.contains("Book Available")) {
         						 status+="-"+obj.borrowItem(id,new_item_id,30);
         						 status+="-"+obj.returnItem(id,old_item_id);
         					   }else {
         						   log.info(status);
         					   }
         					 System.out.println("Reply from the server is: "+status);
         					 } else if(new_item_id.matches("(MCG)([0-9]{4})")) {				  
         						 try{
         							 log.info("UDP Datagram Socket Started");
         							 aSocket = new DatagramSocket();
         							 String info="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
         							 byte [] message = info.getBytes(); 
         							 InetAddress aHost = InetAddress.getByName("localhost");
         							 int serverPort = 6788;	
         							 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
         							 aSocket.send(request);   					
         							 byte [] buffer = new byte[100];
         							 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
         							 aSocket.receive(reply);
         							 status=new String(reply.getData());
         							 if(status.contains("Book Available")) {
         							  log.info(status);
         							  status+="-"+obj.returnItem(id,old_item_id);
         							 }else {
         									log.info(status);
         								}
         							 log.info("Reply from the server is: "+ status);
         							 System.out.println("Reply from the server is: "+status);
         						 }
         						 catch(SocketException e){
         							 System.out.println("Socket: "+e.getMessage());
         						 }
         						 catch(IOException e){
         							 e.printStackTrace();
         							 System.out.println("IO: "+e.getMessage());
         						 }
         						 finally{
         							 if(aSocket != null) aSocket.close();  
         						 }
         					 }
         			         else if(new_item_id.matches("(CON)([0-9]{4})")) {
       				  
         			        	 try{
         			        		 log.info("UDP Datagram Socket Started");
         			        		 aSocket = new DatagramSocket();
         			        		 String info="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
         			        		 byte [] message = info.getBytes(); 
         			        		 InetAddress aHost = InetAddress.getByName("localhost");
         			        		 int serverPort = 6789;	
         			        		 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
         			        		 aSocket.send(request);   					
         			        		 byte [] buffer = new byte[100];
         			        		 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
         			        		 aSocket.receive(reply);
         			        		 status=new String(reply.getData());
         			        		if(status.contains("Book Available")) {
           							  log.info(status);
           							  status+="-"+obj.returnItem(id,old_item_id);
           							 }else {
           									log.info(status);
           								}
         			        		 log.info("Reply received from the server is: "+ status);
         			        		 System.out.println("Reply received from the server is: "+ status);
         			        	 }
         			        	 catch(SocketException e){
         			        		 System.out.println("Socket: "+e.getMessage());
         			        	 }
         			        	 catch(IOException e){
         			        		 e.printStackTrace();
         			        		 System.out.println("IO: "+e.getMessage());
         			        	 }
         			        	 finally{
         			        		 if(aSocket != null) aSocket.close();  
         			        	 }
         			         }
         			 }else {
         				 log.info(status);
         				 System.out.println(status);
         			 }
         			}
         			else if(old_item_id.matches("(MCG)([0-9]{4})")) {				  
       					 try{
       						 log.info("UDP Datagram Socket Started");
       						 aSocket = new DatagramSocket();
       						 String info="Check Borrow List"+"-"+id+"-"+old_item_id+"-"+30+"-";
       						 byte [] message = info.getBytes(); 
       						 InetAddress aHost = InetAddress.getByName("localhost");
       						 int serverPort = 6788;	
       						 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
       						 aSocket.send(request);   					
       						 byte [] buffer = new byte[100];
       						 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
       						 aSocket.receive(reply);
       						 status=new String(reply.getData());
       						 if(status.contains("Book Borrowed")) {
       							 
       							 if(new_item_id.matches("(MON)([0-9]{4})")) {
       			  					   status=obj.checkBookAvailability(new_item_id);
       			  					   log.info(status);
       			  					   if(status.contains("Book Available")) {
       			  						  status+="-"+obj.borrowItem(id,new_item_id,30);
       			  						aSocket = new DatagramSocket();
       				  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
       				  					byte [] message2 = info2.getBytes(); 
       				  					InetAddress aHost2 = InetAddress.getByName("localhost");
       				  					int serverPort2 = 6788;	
       				  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
       				  					aSocket.send(request2);   					
       				  					byte [] buffer2 = new byte[100];
       				  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
       				  					aSocket.receive(reply2);
       				  					status+="-"+new String(reply2.getData());
       			  						  
       			  					   }else {
       			  						   log.info(status);
       			  					   }
       			  					 } else if(new_item_id.matches("(MCG)([0-9]{4})")) {				  
       			  						 try{
       			  							 log.info("UDP Datagram Socket Started");
       			  							 aSocket = new DatagramSocket();
       			  							 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+old_item_id+"-";
       			  							 byte [] message1 = info1.getBytes(); 
       			  							 InetAddress aHost1 = InetAddress.getByName("localhost");
       			  							 int serverPort1 = 6788;	
       			  							 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
       			  							 aSocket.send(request1);   					
       			  							 byte [] buffer1 = new byte[100];
       			  							 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
       			  							 aSocket.receive(reply1);
       			  							 status=new String(reply1.getData());
       			  							 if(status.contains("Book Available")) {
       			  							  log.info(status);
       										    aSocket = new DatagramSocket();
       						  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
       						  					byte [] message2 = info2.getBytes(); 
       						  					InetAddress aHost2 = InetAddress.getByName("localhost");
       						  					int serverPort2 = 6788;	
       						  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
       						  					aSocket.send(request2);   					
       						  					byte [] buffer2 = new byte[100];
       						  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
       						  					aSocket.receive(reply2);
       						  					status+="-"+new String(reply2.getData());
       			  							 }else {
       			  									log.info(status);
       			  								}
       			  							 log.info("Reply from the server is: "+ status);
       			  						 }
       			  						 catch(SocketException e){
       			  							 System.out.println("Socket: "+e.getMessage());
       			  						 }
       			  						 catch(IOException e){
       			  							 e.printStackTrace();
       			  							 System.out.println("IO: "+e.getMessage());
       			  						 }
       			  						 finally{
       			  							 if(aSocket != null) aSocket.close();  
       			  						 }
       			  					 }
       			  			         else if(new_item_id.matches("(CON)([0-9]{4})")) {
       							  
       			  			        	 try{
       			  			        		 log.info("UDP Datagram Socket Started");
       			  			        		 aSocket = new DatagramSocket();
       			  			        		 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
       			  			        		 byte [] message1 = info1.getBytes(); 
       			  			        		 InetAddress aHost1 = InetAddress.getByName("localhost");
       			  			        		 int serverPort1 = 6789;	
       			  			        		 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
       			  			        		 aSocket.send(request1);   					
       			  			        		 byte [] buffer1 = new byte[100];
       			  			        		 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
       			  			        		 aSocket.receive(reply1);
       			  			        		 status=new String(reply1.getData());
       			  			        		if(status.contains("Book Available")) {
       			    							  log.info(status);
       			  							    aSocket = new DatagramSocket();
       						  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
       						  					byte [] message2 = info2.getBytes(); 
       						  					InetAddress aHost2 = InetAddress.getByName("localhost");
       						  					int serverPort2 = 6788;	
       						  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
       						  					aSocket.send(request2);   					
       						  					byte [] buffer2 = new byte[100];
       						  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
       						  					aSocket.receive(reply2);
       						  					status+="-"+new String(reply2.getData());
       			  			        		}else {
       			    									log.info(status);
       			    								}
       			  			        		 log.info("Reply received from the server is: "+ status);
       			  			        	 }
       			  			        	 catch(SocketException e){
       			  			        		 System.out.println("Socket: "+e.getMessage());
       			  			        	 }
       			  			        	 catch(IOException e){
       			  			        		 e.printStackTrace();
       			  			        		 System.out.println("IO: "+e.getMessage());
       			  			        	 }
       			  			        	 finally{
       			  			        		 if(aSocket != null) aSocket.close();  
       			  			        	 }
       			  			         }
       							        			  					
       						  }else {
       								log.info(status);
       							}
       						 log.info("Reply from the server is: "+ status);
       						 System.out.println("Reply from the server is: "+status);
       					 }
       					 catch(SocketException e){
       						 System.out.println("Socket: "+e.getMessage());
       					 }
       					 catch(IOException e){
       						 e.printStackTrace();
       						 System.out.println("IO: "+e.getMessage());
       					 }
       					 finally{
       						 if(aSocket != null) aSocket.close();  
       					 }
       				 }
        			 
         			else if(old_item_id.matches("(CON)([0-9]{4})")) {				  
       				 try{
       					 log.info("UDP Datagram Socket Started");
       					 aSocket = new DatagramSocket();
       					 String info="Check Borrow List"+"-"+id+"-"+old_item_id+"-"+30+"-";
       					 byte [] message = info.getBytes(); 
       					 InetAddress aHost = InetAddress.getByName("localhost");
       					 int serverPort = 6789;	
       					 DatagramPacket request = new DatagramPacket(message, info.length(), aHost, serverPort);
       					 aSocket.send(request);   					
       					 byte [] buffer = new byte[100];
       					 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
       					 aSocket.receive(reply);
       					 status=new String(reply.getData());
       					 if(status.contains("Book Borrowed")) {
       						 
       						 if(new_item_id.matches("(MON)([0-9]{4})")) {
       		  					   status=obj.checkBookAvailability(new_item_id);
       		  					   log.info(status);
       		  					   if(status.contains("Book Available")) {
       		  						 status+="-"+obj.borrowItem(id,new_item_id,30);
       		  						aSocket = new DatagramSocket();
       			  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
       			  					byte [] message2 = info2.getBytes(); 
       			  					InetAddress aHost2 = InetAddress.getByName("localhost");
       			  					int serverPort2 = 6789;	
       			  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
       			  					aSocket.send(request2);   					
       			  					byte [] buffer2 = new byte[100];
       			  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
       			  					aSocket.receive(reply2);
       			  					status+="-"+new String(reply2.getData());
       		  					   }else {
       		  						   log.info(status);
       		  					   }
       		  					 } else if(new_item_id.matches("(MCG)([0-9]{4})")) {				  
       		  						 try{
       		  							 log.info("UDP Datagram Socket Started");
       		  							 aSocket = new DatagramSocket();
       		  							 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+30+"-";
       		  							 byte [] message1 = info1.getBytes(); 
       		  							 InetAddress aHost1 = InetAddress.getByName("localhost");
       		  							 int serverPort1 = 6788;	
       		  							 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
       		  							 aSocket.send(request1);   					
       		  							 byte [] buffer1 = new byte[100];
       		  							 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
       		  							 aSocket.receive(reply1);
       		  							 status=new String(reply1.getData());
       		  							 if(status.contains("Book Available")) {
       		  							  log.info(status);
       		  						    aSocket = new DatagramSocket();
       				  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
       				  					byte [] message2 = info2.getBytes(); 
       				  					InetAddress aHost2 = InetAddress.getByName("localhost");
       				  					int serverPort2 = 6789;	
       				  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
       				  					aSocket.send(request2);   					
       				  					byte [] buffer2 = new byte[100];
       				  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
       				  					aSocket.receive(reply2);
       				  					status+="-"+new String(reply2.getData());
       		  							 }else {
       		  									log.info(status);
       		  								}
       		  							 log.info("Reply from the server is: "+ status);
       		  							}
       		  						 catch(SocketException e){
       		  							 System.out.println("Socket: "+e.getMessage());
       		  						 }
       		  						 catch(IOException e){
       		  							 e.printStackTrace();
       		  							 System.out.println("IO: "+e.getMessage());
       		  						 }
       		  						 finally{
       		  							 if(aSocket != null) aSocket.close();  
       		  						 }
       		  					 }
       		  			         else if(new_item_id.matches("(CON)([0-9]{4})")) {
       						  
       		  			        	 try{
       		  			        		 log.info("UDP Datagram Socket Started");
       		  			        		 aSocket = new DatagramSocket();
       		  			        		 String info1="Check Availability"+"-"+id+"-"+new_item_id+"-"+old_item_id+"-";
       		  			        		 byte [] message1 = info1.getBytes(); 
       		  			        		 InetAddress aHost1 = InetAddress.getByName("localhost");
       		  			        		 int serverPort1 = 6789;	
       		  			        		 DatagramPacket request1 = new DatagramPacket(message1, info1.length(), aHost1, serverPort1);
       		  			        		 aSocket.send(request1);   					
       		  			        		 byte [] buffer1 = new byte[100];
       		  			        		 DatagramPacket reply1 = new DatagramPacket(buffer1, buffer1.length);
       		  			        		 aSocket.receive(reply1);
       		  			        		 status=new String(reply1.getData());
       		  			        		if(status.contains("Book Available")) {
       		    							  log.info(status);
       		    							    aSocket = new DatagramSocket();
       		    			  					String info2="Return Item"+"-"+id+"-"+old_item_id+"-"+30+"-";
       		    			  					byte [] message2 = info2.getBytes(); 
       		    			  					InetAddress aHost2 = InetAddress.getByName("localhost");
       		    			  					int serverPort2 = 6789;	
       		    			  					DatagramPacket request2 = new DatagramPacket(message2, info2.length(), aHost2, serverPort2);
       		    			  					aSocket.send(request2);   					
       		    			  					byte [] buffer2 = new byte[100];
       		    			  					DatagramPacket reply2 = new DatagramPacket(buffer2, buffer2.length);
       		    			  					aSocket.receive(reply2);
       		    			  					status+="-"+new String(reply2.getData());
       		    							 }else {
       		    									log.info(status);
       		    								}
       		  			        		 log.info("Reply received from the server is: "+ status);
       		  			        		}
       		  			        	 catch(SocketException e){
       		  			        		 System.out.println("Socket: "+e.getMessage());
       		  			        	 }
       		  			        	 catch(IOException e){
       		  			        		 e.printStackTrace();
       		  			        		 System.out.println("IO: "+e.getMessage());
       		  			        	 }
       		  			        	 finally{
       		  			        		 if(aSocket != null) aSocket.close();  
       		  			        	 }
       		  			         } 

       						 
       					  }else {
       							log.info(status);
       						}
       					 log.info("Reply from the server is: "+ status);
       					 System.out.println("Reply from the server is: "+status);
       				 }
       				 catch(SocketException e){
       					 System.out.println("Socket: "+e.getMessage());
       				 }
       				 catch(IOException e){
       					 e.printStackTrace();
       					 System.out.println("IO: "+e.getMessage());
       				 }
       				 finally{
       					 if(aSocket != null) aSocket.close();  
       				 }
       			 }
          		}
          		 else {
          			log.info(id+" logged out ");
          			break;
          		 }
          		}   		 
          	 }
   	 	else
   	 	{
   	 		System.out.println("Invalid user id");
   	 	}
  }
	
	public static DLMS connectToServer(String userId, String[] args) {
		ORB orb = ORB.init(args, null);
		try {
			NamingContextExt namingContext = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
			return DLMSHelper.narrow(namingContext.resolve_str(getServerName(userId.substring(0, 3))));
		} catch (InvalidName | NotFound | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getServerName(String userID) {
		switch (userID) {
		case "CON": return "CON_SERVER";
		case "MCG":return "MCG_SERVER";
		case "MON":return "MON_SERVER";
		}
		return userID;
	}
}