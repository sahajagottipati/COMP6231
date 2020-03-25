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

public class MCG_Server {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		MCG_Implementation mcg_obj = new MCG_Implementation();
		
		LogManager.getLogManager().readConfiguration(new FileInputStream("properties/server_logging.properties"));
		
		Runnable libraryServerRunnable = () ->{
			mcg_obj.MCGserver();
		};
		
		Runnable mcgRequestServerRunnable = () ->{
			McgBind(mcg_obj,args);
		};
		
		new Thread(libraryServerRunnable).start();
		new Thread(mcgRequestServerRunnable).start();
	}
		private static void McgBind(MCG_Implementation mcg_obj, String[] args) {
			ORB orb = ORB.init(args, null);
			try {
				POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
				rootPoa.the_POAManager().activate();
				
				mcg_obj.setOrb(orb);
				

				DLMS href = DLMSHelper.narrow(rootPoa.servant_to_reference(mcg_obj));
				
				NamingContextExt namingContextReference = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
				NameComponent[] path = namingContextReference.to_name("MCG_SERVER");
				
				namingContextReference.rebind(path, href);
				System.out.println("MCG Server is ready");
				while(true) {
					orb.run();
				}
				
			} catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName | ServantNotActive | WrongPolicy | NotFound | CannotProceed e) {
				System.out.println("Something went wrong in MCG server: "+e.getMessage());
			}
			
		}

}
