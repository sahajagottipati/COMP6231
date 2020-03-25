package LibraryServer;

import java.io.FileInputStream;
import java.util.logging.LogManager;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import DLMSIdl.DLMS;
import DLMSIdl.DLMSHelper;
import server.implementation.*;

public class MON_Server {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
MON_Implementation mon_obj = new MON_Implementation();
		
		LogManager.getLogManager().readConfiguration(new FileInputStream("properties/server_logging.properties"));
						
		Runnable libraryServerRunnable = () ->{
			mon_obj.MONserver();
		};
		
		Runnable monRequestServerRunnable = () ->{
			MonBind(mon_obj,args);
		};
		
		new Thread(libraryServerRunnable).start();
		new Thread(monRequestServerRunnable).start();
	}
		private static void MonBind(MON_Implementation mon_obj, String[] args) {
			ORB orb = ORB.init(args, null);
			try {
				POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
				rootPoa.the_POAManager().activate();
				
				mon_obj.setOrb(orb);
				

				DLMS href = DLMSHelper.narrow(rootPoa.servant_to_reference(mon_obj));
				
				NamingContextExt namingContextReference = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
				NameComponent[] path = namingContextReference.to_name("MON_SERVER");
				
				namingContextReference.rebind(path, href);
				System.out.println("MON Server is ready");
				while(true) {
					orb.run();
				}
				
			} catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName | ServantNotActive | WrongPolicy | NotFound | CannotProceed e) {
				System.out.println("Something went wrong in MON server: "+e.getMessage());
			}
			
		}

}
