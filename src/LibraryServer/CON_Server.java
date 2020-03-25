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

public class CON_Server {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		CON_Implementation con_obj = new CON_Implementation();
		
		LogManager.getLogManager().readConfiguration(new FileInputStream("properties/server_logging.properties"));
		
		Runnable libraryServerRunnable = () ->{
			con_obj.UDPserver();
		};
		
		Runnable conRequestServerRunnable = () ->{
			ConBind(con_obj,args);
		};
		
		new Thread(libraryServerRunnable).start();
		new Thread(conRequestServerRunnable).start();
	}
		private static void ConBind(CON_Implementation con_obj, String[] args) {
			ORB orb = ORB.init(args, null);
			try {
				POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
				rootPoa.the_POAManager().activate();
				
				con_obj.setOrb(orb);
				

				DLMS href = DLMSHelper.narrow(rootPoa.servant_to_reference(con_obj));
				
				NamingContextExt namingContextReference = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
				NameComponent[] path = namingContextReference.to_name("CON_SERVER");
				
				namingContextReference.rebind(path, href);
				System.out.println("CON Server is ready");
				while(true) {
					orb.run();
				}
				
			} catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName | ServantNotActive | WrongPolicy | NotFound | CannotProceed e) {
				System.out.println("Something went wrong in CON server: "+e.getMessage());
			}
			
		}

}
