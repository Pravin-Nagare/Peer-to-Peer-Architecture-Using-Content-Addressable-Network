
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BootstrapInt extends Remote{
	public CANNodesIP getNodeIPAddresses() throws RemoteException;
	public boolean setNodeInBootstrap(String ipAddress, int port, String peerName) throws RemoteException;
	//public boolean join(int x, int y, ZoneCoordinates zoneClient) throws RemoteException;
	public void removeFromPool(String peerName) throws RemoteException;
}
