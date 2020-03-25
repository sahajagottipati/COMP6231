package client;

import java.io.FileInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

public class ManagerClient {

	static Logger log= Logger.getLogger("Client");
	static FileHandler fh;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		LogManager.getLogManager().readConfiguration(new FileInputStream("properties/manager_logging.properties"));
   	 	Scanner sc= new Scanner(System.in);
   	 	String status="";
   	 	System.out.println("Enter ManagerID:");
   	    String id= sc.next();
   	    
	 	if(id.matches("(CONM)([0-9]{4})")) {

	   		log.info("Log Created for user "+id);
	   		 while(true) {
	   		 System.out.println("Select an operation:"+"\n"+"1.Add Item"+"\n"+"2.Remove Item"+"\n"+"3.List of Items"+"\n"+"4.Logout");
	   		 int n=sc.nextInt();
	   		 
	   		  if(n==1) {
	   			 try {
	   			 log.info(id+" requested for Adding an Item");
	   			 DLMS obj= connectToServer(id,args);
						
						  String item_id,item_name; 
						  int item_quantity;
						  System.out.println("Enter itemID:");
						  item_id= sc.next();
						  
				if(item_id.matches("(CON)([0-9]{4})")) {
				
					System.out.println("Enter itemName:"); 
		  		    item_name= sc.next();
				    System.out.println("Enter quantity:"); 
			    	item_quantity= sc.nextInt();
	   			    log.info(id+" is going to add "+item_id+" "+item_name+" Book of quantity "+item_quantity);
	  			    status=obj.addItem(id, item_id,item_name, item_quantity);
	   			    //log.info(status);
						  }
				else{
					 status="Invalid item id";
					 //log.info(status);
						  }
				 //System.out.println(obj.printLibrary());
				System.out.println(status);	
				log.info(status);	 
	   			 }
	   			 catch(InputMismatchException e)
	   			 {
	   				 e.printStackTrace();
	   				 System.out.println(e.getMessage());
	   			 }
	   		 }
	   		
	   		 else if(n==2) {
	   			try {
	      			 log.info(id+" requested for Removing an Item");
	      			 DLMS obj= connectToServer(id,args);
	   					
	   					  String item_id; 
	   					  int item_quantity;
	   					  System.out.println("Enter itemID:"); 
	   					  item_id= sc.next();
	   					  
	   				if(item_id.matches("(CON)([0-9]{4})")) { 
	   					  System.out.println("Enter quantity:");
	   					  item_quantity= sc.nextInt();
	   					 
	      			 log.info(id+" is going to REMOVE "+item_id+"  Book of quantity "+item_quantity);
	     			 status=obj.removeItem(id,item_id,item_quantity);
	   				}
	   				else {
	   					status= "Invalid item id";
	   				}
	   				System.out.println(status);
	   				log.info(status);
	   			 //System.out.println(obj.printLibrary());
	   					 
	      			 }
	      			 catch(InputMismatchException e)
	      			 {
	      				 e.printStackTrace();
	      				 System.out.println(e.getMessage());
	      			 } 
	   		 }
	   		 else if(n==3){
	   			 DLMS obj= connectToServer(id,args);
	   			 status=obj.printLibrary(id);
	   			 log.info(status);
	   			 System.out.println(status);
	   		 }
	   		 else {
	   			log.info(id+" logged out ");
	   			break;
	   		 }
	   		}   		 
	   	 
	 	}
	 	
	 	else if(id.matches("(MCGM)([0-9]{4})")) {

   	   		log.info("Log Created for user "+id);
      		 while(true) {
      		 System.out.println("Select an operation:"+"\n"+"1.Add Item"+"\n"+"2.Remove Item"+"\n"+"3.List of Items"+"\n"+"4.Logout");
      		 int n=sc.nextInt();
    
      		 if(n==1) {
      			 try {
      			 log.info(id+" requested for Adding an Item");
      			 DLMS obj= connectToServer(id,args);
   					
   					  String item_id,item_name; 
   					  int item_quantity;
   					  System.out.println("Enter itemID:");
   					  item_id= sc.next();
   					  
   				if(item_id.matches("(MCG)([0-9]{4})")) {
   					  System.out.println("Enter itemName:"); 
   					  item_name= sc.next();
   					  System.out.println("Enter quantity:"); 
   					  item_quantity= sc.nextInt();
   					 
      			 log.info(id+" is going to add "+item_id+" "+item_name+" Book of quantity "+item_quantity);
     			 status=obj.addItem(id, item_id,item_name, item_quantity);
   				} 
   				else {
   					status="Invalid item id";
   				}
   				System.out.println(status);
      			log.info(status);
   			 //System.out.println(obj.printLibrary());
   					 
      			 }
      			 catch(InputMismatchException e)
      			 {
      				 e.printStackTrace();
      				 System.out.println(e.getMessage());
      			 }
      		 }
      		 
      		 else if(n==2) {
      			try {
         			 log.info(id+" requested for Removing an Item");
         			 DLMS obj= connectToServer(id,args);
      					
      					  String item_id,item_name; 
      					  int item_quantity;
      					  System.out.println("Enter itemID:"); 
      					  item_id= sc.next();
      					  
      				if(item_id.matches("(MCG)([0-9]{4})")) {
      		
      					  System.out.println("Enter quantity:");
      					  item_quantity= sc.nextInt();
      					 
         			 log.info(id+" is going to REMOVE "+item_id+" with quantity "+item_quantity);
        			 status=obj.removeItem(id, item_id,item_quantity);
      				} else {
      					status="Invalid item id";
      				}
      				System.out.println(status);
        			 log.info(status);
      			 //System.out.println(obj.printLibrary());
      					 
         			 }
         			 catch(InputMismatchException e)
         			 {
         				 e.printStackTrace();
         				 System.out.println(e.getMessage());
         			 } 
      		 }
      		 else if(n==3){
      			 DLMS obj= connectToServer(id,args);
      			 status=obj.printLibrary(id);
      			 log.info(status);
      			 System.out.println(status);
      		 }
      		 else {
      			log.info(id+" logged out ");
      			break;
      		 }
      		}   		 
      	 
	 	}
	 	
	 	else if(id.matches("(MONM)([0-9]{4})")) {	 		

    		 log.info("Log Created for user "+id);
      		 while(true) {
      		 System.out.println("Select an operation:"+"\n"+"1.Add Item"+"\n"+"2.Remove Item"+"\n"+"3.List of Items"+"\n"+"4.Logout");
      		 int n=sc.nextInt();
      		 if(n==1) {
      			 try {
      			 log.info(id+" requested for Adding an Item");
      			 DLMS obj= connectToServer(id,args);
   					
   					  String item_id,item_name; 
   					  int item_quantity;
   					  System.out.println("Enter itemID:");
   					  item_id= sc.next();
   					  
   				if(item_id.matches("(MON)([0-9]{4})")) {
   					  System.out.println("Enter itemName:"); 
   					  item_name= sc.next();
   					  System.out.println("Enter quantity:"); 
   					  item_quantity= sc.nextInt();
   					 
      			 log.info(id+" is going to add "+item_id+" "+item_name+" Book of quantity "+item_quantity);
     			 status=obj.addItem(id, item_id,item_name, item_quantity);
   				}else {
   					status="Invalid item id";
   				}
   				System.out.println(status);
     			 log.info(status);
   			 //System.out.println(obj.printLibrary());
   					 
      			 }
      			 catch(InputMismatchException e)
      			 {
      				 e.printStackTrace();
      				 System.out.println(e.getMessage());
      			 }
      		 }

      		 else if(n==2) {
      			try {
         			 log.info(id+" requested for Removing an Item");
         			 DLMS obj= connectToServer(id,args);
      					
      					  String item_id,item_name; 
      					  int item_quantity;
      					  System.out.println("Enter itemID:"); 
      					  item_id= sc.next();
      					  
      				if(item_id.matches("(MON)([0-9]{4})")) {
      				
      					  System.out.println("Enter quantity:");
      					  item_quantity= sc.nextInt();
      					 
         			 log.info(id+" is going to REMOVE "+item_id+" with quantity "+item_quantity);
        			 status=obj.removeItem(id, item_id,item_quantity);
      				} else {
      					status="Invalid item id";
      				}
      				System.out.println(status);
        			 log.info(status);
      			 //System.out.println(obj.printLibrary());
      					 
         			 }
         			 catch(InputMismatchException e)
         			 {
         				 e.printStackTrace();
         				 System.out.println(e.getMessage());
         			 } 
      		 }
      		 else if(n==3){
      			 DLMS obj= connectToServer(id,args);
      			 status=obj.printLibrary(id);
      			 log.info(status);
      			 System.out.println(status);
      		 }
      		 else {
      			log.info(id+" logged out ");
      			break;
      		 }
      		}   		 
      	 
	 	}
	 	else
	 	{
	 		System.out.println("Invalid Manager id");
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
