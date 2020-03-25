package server.implementation;
import Interface.*;
import model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.*;

import org.omg.CORBA.ORB;

import DLMSIdl.DLMSPOA;

public class CON_Implementation extends DLMSPOA {
	
    public static String U="";
    public static String I="";
    public static String S="";
    private ORB orb;
	HashMap<String, ItemDetails> library= new HashMap<String, ItemDetails>();
    HashMap<String, List<String>> borrowItem= new HashMap<String, List<String>>();
    HashMap<String, List<String>> queueList= new HashMap<String, List<String>>();
    Map<String, Queue<String>> waitingList = new HashMap<>();
    Queue<String> queue = new LinkedList<String>();
    final Logger log1= Logger.getLogger("Server");
	
    public CON_Implementation() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub 		    
	}
    
    public void setOrb(ORB orbValue) {
		orb = orbValue;
	}
    
 
	public void shutdown() {
		orb.shutdown(false);
	}
	
    public void initialize(String userID, String itemName) {
    	U=userID;
    	I=itemName;
    }
    
    public String getStatus() {
    	return S;
    }
    
    public void startServer()
    {
    	S="";
    	Runnable task = () -> {
    		sendMessage(6789);
		};
		Runnable task2 = () -> {
			sendMessage(6788);
		};
		
		Runnable task3 = () -> {
			sendMessage(6787);
		};
		
		Thread thread = new Thread(task);
		Thread thread2 = new Thread(task2);
		Thread thread3 = new Thread(task3);	
		thread.start();
		thread2.start();
		thread3.start();
    }
    
    public void sendMessage(int serverPort) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			String data="Find Item-"+U+"-"+I+"-";
			byte[] message = data.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, data.length(), aHost, serverPort);
			aSocket.send(request);
			log1.info("Request message sent from the client to server with port number " + serverPort + " is: "+ new String(request.getData()));
			byte[] buffer = new byte[12];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			String status=new String(reply.getData());
			S=S+status;
			log1.info("Reply received from the server with port number " + serverPort + " is: "+ status);
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
    
    public void UDPserver() {
    	DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(6789);
			byte[] buffer = new byte[120];
			log1.info("Server Started............");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String data=new String(request.getData());
				List<String> datasplit=new ArrayList<String>(Arrays.asList(data.split("-")));
				String status="";
				if(datasplit.get(0).equals("Borrow Item")) {
					log1.info(datasplit.get(1)+datasplit.get(2)+datasplit.get(3));
					
					List<String> borrowList=new ArrayList<String>();
					borrowList= new ArrayList<String>(borrowItem.keySet());
					int check=0;
					for(int i=0;i<borrowList.size();i++) {
						if(borrowItem.get(borrowList.get(i)).contains(datasplit.get(1))) {
							check=1;
							break;
						}
					}
					if(check==0 || borrowItem.size()==0 ) {
						
							status=borrowItem(datasplit.get(1),datasplit.get(2),Integer.parseInt(datasplit.get(3)));
							List<String> queue=new ArrayList<String>();
							List<String> remove_queue=new ArrayList<String>();
							queue= new ArrayList<String>(queueList.keySet());
							for(int i=0;i<queue.size();i++) {
								if(queueList.get(queue.get(i)).contains(datasplit.get(1))) {
									remove_queue=queueList.get(queue.get(i));
									remove_queue.remove(datasplit.get(1));
									queueList.put(queue.get(i), remove_queue);
								}
							}
					}else {
							status="One Item was already borrowed from this Library";
						}
				      }else if(datasplit.get(0).equals("Return Item")) {
							log1.info(datasplit.get(1)+datasplit.get(2));
							
							List<String> borrowList=new ArrayList<String>();
							borrowList= new ArrayList<String>(borrowItem.keySet());
							int check=0;
							for(int i=0;i<borrowList.size();i++) {
								if(borrowItem.get(borrowList.get(i)).contains(datasplit.get(1))) {
									check=1;
									break;
								}
							}
							if(check==1) {
								
									status=returnItem(datasplit.get(1),datasplit.get(2));
								
							}else {
									status="Book was not borrowed";
								}
						      }
				      else if(datasplit.get(0).equals("Find Item")) {
							log1.info(datasplit.get(1)+datasplit.get(2));
							status=findItem(datasplit.get(1),datasplit.get(2));
							
					 }else if(datasplit.get(0).equals("Add Queue")) {
							
				    	  log1.info(datasplit.get(1)+datasplit.get(2));
						  status=addToQueue(datasplit.get(1),datasplit.get(2));
								
						}
					 else if(datasplit.get(0).equals("Check Availability")) {
							
				    	  log1.info(datasplit.get(1)+datasplit.get(2));
						  status=checkBookAvailability(datasplit.get(2));
						  if(status.equals("Book Available")) {
							  log1.info(datasplit.get(0)+datasplit.get(1)+datasplit.get(2));
								
								List<String> borrowList=new ArrayList<String>();
								borrowList= new ArrayList<String>(borrowItem.keySet());
								int check=0;
								for(int i=0;i<borrowList.size();i++) {
									if(borrowItem.get(borrowList.get(i)).contains(datasplit.get(1))) {
										check=1;
										break;
									}
								}
								if(check==0 || borrowItem.size()==0 || (datasplit.get(2).startsWith("CON")&&datasplit.get(3).startsWith("CON")) ) {
									
										status+=borrowItem(datasplit.get(1),datasplit.get(2),30);
										List<String> queue=new ArrayList<String>();
										List<String> remove_queue=new ArrayList<String>();
										queue= new ArrayList<String>(queueList.keySet());
										for(int i=0;i<queue.size();i++) {
											if(queueList.get(queue.get(i)).contains(datasplit.get(1))) {
												remove_queue=queueList.get(queue.get(i));
												remove_queue.remove(datasplit.get(1));
												queueList.put(queue.get(i), remove_queue);
											}
										}
										
								}else {
										status="One Item was already borrowed from this Library";
									}
						  }
						}
					    else if(datasplit.get(0).equals("Check Borrow List")) {
							
				    	  log1.info(datasplit.get(1)+datasplit.get(2));
						  status=checkBorrowList(datasplit.get(1),datasplit.get(2));	  		
						}
				log1.info(status);				
				byte [] message = status.getBytes();
				DatagramPacket reply = new DatagramPacket(message,message.length, request.getAddress(),request.getPort());
				aSocket.send(reply);// reply sent
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
    }

	@Override
	public String addItem(String managerID, String itemID, String itemName, int quantity) {
		// TODO Auto-generated method stub
		if(managerID.matches("(CONM)([0-9]{4})")){
				log1.info(managerID+" requested to add "+itemID+" for "+itemName+" of "+quantity);
		    	return addToLib(itemID, itemName, quantity, library,queueList);			
				}
		else {
			return "";
		}
		}
    
	@Override
	public String removeItem(String managerID, String itemID,int quantity) {
		// TODO Auto-generated method stub
		if(managerID.matches("(CONM)([0-9]{4})")){
				log1.info(managerID+" requested to remove "+itemID+" of quantity "+quantity);
		    	return removeFromLib(itemID, quantity, library,borrowItem);			
				}
		else {
			return "";
		}
		}

	@Override
	public String borrowItem(String userID, String itemID, int numberOfDays) {
		// TODO Auto-generated method stub
		
		if(itemID.matches("(CON)([0-9]{4})")) {
			log1.info(userID+" requested to borrow "+itemID);
			return borrowItemFromLib(userID, itemID, library,borrowItem);
			//logger.info("In borrow item method");
		}
		else {
			return "Sorry server unavailable";
		}
		
	}
     public String addToLib(String itemID, String itemName,int quantity, HashMap<String,ItemDetails> lib, HashMap<String, List<String>> queue)  {
    	 ItemDetails itemDetails;
    	 //if the library already has the book update the number of quantity    	 
    	 String status = null;
    	 List<String> user = new ArrayList<String>();
    	 if(lib.containsKey(itemID)) {
    		int old_quantity = lib.get(itemID).getQuantity();
    		int new_quantity= old_quantity + quantity;
    		itemDetails= new ItemDetails(itemName,new_quantity);
    		lib.put(itemID, itemDetails);
    		status="Book with item id : "+itemID+" is added to the library";
    		log1.info(status);
    		//logger.info("Item with itemID: "+itemID+" added to the library");
    	 }
    	 else {
    		 // if the library doesn't have the item then add it to the library
    		 itemDetails= new ItemDetails(itemName, quantity);
    		 lib.put(itemID, itemDetails);
    		 status="Book with item id : "+itemID+" is added to the library";
    		 log1.info(status);
    	 }
    	 if(queue.containsKey(itemID)&& queue.get(itemID).size()!=0) {
			 user= queue.get(itemID);
			 int queue_size=0;
			 if(lib.get(itemID).getQuantity()>=user.size()) {
				 queue_size=user.size();
			 }else {
				 queue_size=user.size()-lib.get(itemID).getQuantity();
			 }
			 for(int i=0;i<queue_size;i++) {
				 user= queue.get(itemID);
				 status+=borrowItem(user.get(0),itemID,30);
    			 user.remove(0);
    			 queue.put(itemID, user);
			 }
		 }
    	 return status;
    	 
     }
     
     public String removeFromLib(String itemID,int quantity, HashMap<String,ItemDetails> lib,HashMap<String, List<String>> borrow) {
    	 ItemDetails itemDetails;
    	 //if the library already has the book update the number of quantity    	 
    	 String status;
    	 if(lib.containsKey(itemID)) {
    		int old_quantity = lib.get(itemID).getQuantity();
    		if(quantity==-1) {
    			lib.remove(itemID);
    			status=itemID+" is  completely removed from library";
    			log1.info(status);
    			borrow.remove(itemID);
    			
    		}else {
    		if(old_quantity==quantity && !borrow.containsKey(itemID)) {
    			lib.remove(itemID);
    			status=itemID+" is  completely removed from library";
    			log1.info(status);
    		}else {
    			if(old_quantity==0) {
    				int borrow_qty=borrow.get(itemID).size();
    				if(quantity<=borrow_qty) {
    					status="";
    					for(int i=0;i<quantity;i++) {
    						int j=0;
    						status=status+" "+itemID+" book is removed from "+borrow.get(itemID).get(i)+" ";
    						//borrow.get(itemID).remove(j);
    					}
    					List<String> new_list=borrow.get(itemID).subList(quantity,borrow_qty);
    					borrow.put(itemID,new_list);
    					log1.info(status);
    				}else {
    					status="Exceeded the quantity maximum, old quantity is "+old_quantity+" and borrow quantity is "+borrow.get(itemID).size();
    					log1.info(status);
    				}
    			}else {
    			   if(quantity<=old_quantity) {
    				  int new_quantity= old_quantity - quantity;
    				  itemDetails= new ItemDetails(lib.get(itemID).getItemName(),new_quantity);
    				  lib.put(itemID, itemDetails);
    				  status="Book with item id : "+itemID+" is removed from the library and current quantity is "+new_quantity;
    				  log1.info(status);
    			     }else {
    			    	 status="Exceeded the maximum quantity, quantity is "+old_quantity;
     					log1.info(status);
    			    }
    			}
    		 }
    		}
    		//logger.info("Item with itemID: "+itemID+" added to the library");
    	 }
    	 else {
    		 // if the library doesn't have the item then add it to the library
    		 status=itemID+" does not exist in the library";
    		 log1.info(status);
    	 }
    	 
    	 return status;
    	 
     }
     
     public String borrowItemFromLib(String userID,String itemID, HashMap<String,ItemDetails> lib, HashMap<String,List<String>> borrowItem) {
    	 //if item available borrow the book from library
    	 String status="";
    	 if((lib.get(itemID) != null) && (lib.get(itemID).getQuantity()!=0)) 
    	 {
            int qty= lib.get(itemID).getQuantity();
            List<String> borrowList=new ArrayList<String>();
            if(borrowItem.containsKey(itemID)) {
            	borrowList=borrowItem.get(itemID);
            }
            borrowList.add(userID);
            borrowItem.put(itemID, borrowList);
            log1.info(""+borrowItem);
            int new_qty= qty-1;
            lib.get(itemID).setQuantity(new_qty);
            status=userID+" borrowed an item with item id: "+itemID+" Remaining Quantity is "+lib.get(itemID).getQuantity();
            log1.info(status);
            //log1.info("User borrowed an item with item id: "+itemID);
    	 } else {
    		 
    		 status="Book Not Available";
    		 //queue.add(userID);
    		 //logger.info("Item with item id: "+itemID+" is not available so the user is added to the waitlist");
    		 //status="Book with itemID: "+ itemID+" is not available so, you have been added to waiting list"+queue.stream();
    	 }
    	 return status;
    	     	 
     }
     
      @Override
      public String printLibrary(String managerID)
      {
    	  if(managerID.matches("(CONM)([0-9]{4})")) {
    		  log1.info(managerID+" requested to display list of Items");
    		  return libPrint(library);
  		}
  		else {
  			return "";
  		}
      }
      
      public String libPrint(HashMap<String,ItemDetails> lib)
      {
    	 String status= lib.toString();
    	 //status=status.replace('{', ' ');
    	 //status=status.replaceAll("=ItemDetails [itemName="," ");
    	 //status=status.replaceAll(", quantity="," ");
    	 //status=status.replaceAll("}","");
    	 log1.info(status);
    	 return status;
      }
	
      @Override
      public String addToQueue(String userID, String itemID)
      {
    	  if(itemID.matches("(CON)([0-9]{4})")) {
    		  log1.info(itemID+" requested to add "+userID+" to queue");
    		  return addQueue(userID,itemID,queueList);
  		}
  		else {
  			return "";
  		} 
      }
      
      public String addQueue(String userID, String itemID,HashMap<String,List<String>> queue)
      {
    	 
    	  List<String> user = new ArrayList<String>();
    	  if(queue.containsKey(itemID)) {
    	  user= queue.get(itemID);
    	  }
    	  user.add(userID);
    	  queue.put(itemID, user);
    	  String status="Book with itemID: "+ itemID+" is not available so, you have been added to waiting list"+queue.get(itemID);
    	  log1.info(status);
    	  return status;
      }
      
      @Override
      public String returnItem(String userID, String itemID)
      {
    	  if(itemID.matches("(CON)([0-9]{4})")) {
    		  log1.info(userID+" requested to return "+itemID);
    		  return returnItems(userID,itemID,library,borrowItem,queueList);
  		}
  		else {
  			return "";
  		} 
      }
      
      public String returnItems(String userID, String itemID,HashMap<String, ItemDetails> lib,HashMap<String,List<String>> borrow,HashMap<String,List<String>> queue) 
      {
    	  String status="";
    	  List<String> user = new ArrayList<String>();
    	 if(borrow.containsKey(itemID) && borrow.get(itemID).contains(userID)) {
    		 user=borrow.get(itemID);
    		 
    		 user.remove(userID);
    		 borrow.put(itemID, user);
    	     status="Book with id "+itemID+" Returned";
    		 int book_qty=lib.get(itemID).getQuantity();
    	     book_qty+=1;
    	     lib.get(itemID).setQuantity(book_qty);
    		 status+=" Book is returned to the library and present quantity is "+lib.get(itemID).getQuantity();
    		 if(queue.containsKey(itemID) && queue.get(itemID).size()!=0) {
    			 user= queue.get(itemID);
    			 status+=borrowItem(user.get(0),itemID,30);
    			 user.remove(0);
    			 queue.put(itemID, user);
    		 }
    		 log1.info(status);
    	 }else {
    		 status="Book was not borrowed";
    	 }
    		 return status;
      }
      
      @Override
      public String findItem(String userID, String itemName) {
    	  
    	      log1.info(userID+" requested to find items with "+itemName);
    		  return find(userID,itemName,library);
  		 
      }
      
      public String find(String userID, String itemName,HashMap<String, ItemDetails> lib) {
    	  String status="";
    	  List<String> findList=new ArrayList<String>();
		  findList= new ArrayList<String>(lib.keySet());
		  for(int i=0;i<findList.size();i++) {
			if(lib.get(findList.get(i)).getItemName().contains(itemName)) {
				int qty=lib.get(findList.get(i)).getQuantity();
				status=" "+findList.get(i)+" "+qty+" ";
				break;
			}
		  }
    	  log1.info(status);
    	  return status;
      }
      
      public String checkBorrowList(String userID,String itemID) {
    	  log1.info(userID+" requested to check for "+itemID);
    	  return checkList(userID,itemID,borrowItem);
      }
      
      public String checkList(String userID, String itemID, HashMap<String,List<String>> borrow) {
    	  String status="";
    	  if(borrow.containsKey(itemID) && borrow.get(itemID).contains(userID)) {
    		  status="Book Borrowed";
    	  }else {
    		  status="Book Not Borrowed, Please select a correct ItemID";
    	  }
    	  log1.info(status);
    	  return status;
      }
      
      public String checkBookAvailability(String itemID) {
    	  log1.info("Requested to check for "+itemID);
    	  return checkAvailability(itemID,library);
      }
      
      public String checkAvailability(String itemID,HashMap<String, ItemDetails> lib) {
    	  String status="";
    	  if(lib.containsKey(itemID)&& lib.get(itemID).getQuantity()>0) {
    		  status="Book Available";
    	  }else {
    		  status="Book Not Available";
    	  }
    	  return status;
      }
}
