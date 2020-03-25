package Interface;
import java.rmi.*;
import java.util.HashMap;
import java.util.List;

public interface MON_ServerInterface extends Remote {
  
	public String addItem(String managerID, String itemID,String itemName, int quantity) throws RemoteException;
	public String removeItem(String managerID, String itemID,int quantity) throws RemoteException;
	public String borrowItem(String userID, String itemID, int numberOfDays) throws RemoteException;
	public String returnItem(String userID, String itemID) throws RemoteException;
	public String addToQueue(String userID, String itemID) throws RemoteException;
	public String findItem(String userID, String itemName) throws RemoteException;
	public String printLibrary(String managerID) throws RemoteException;
	public void initialize(String userID, String itemName) throws RemoteException;
	public void startServer() throws RemoteException;
	public String getStatus() throws RemoteException;
}
