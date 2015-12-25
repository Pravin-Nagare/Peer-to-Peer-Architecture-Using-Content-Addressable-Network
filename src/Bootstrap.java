
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bootstrap extends UnicastRemoteObject implements BootstrapInt, Serializable {
	 
	public static List<CANNodesIP>  nodeIP = null;
	Random random = new Random();
	
	public Bootstrap() throws RemoteException {
		super();
		nodeIP = new ArrayList<CANNodesIP>();
	}
	
	public CANNodesIP getNodeIPAddresses() throws RemoteException{
		if(nodeIP.isEmpty()){
			//System.out.println("List empty");
			return null;
		}
		int index = random.nextInt(nodeIP.size());
		System.out.println("Random Node Peer: " + nodeIP.get(index).peerName);
		return nodeIP.get(index);
	}			
	
	public boolean setNodeInBootstrap(String ipAddress, int port, String peerName) throws RemoteException{
		CANNodesIP node  = new CANNodesIP();
		node.ipAddress = ipAddress;
		node.peerName = peerName;
		node.port=port;
		nodeIP.add(node); 		
		System.out.println("New node added: " + peerName + " " + ipAddress + " " + port);  
		return true;
	}
	
	public void removeFromPool(String peerName) throws RemoteException{
		for(int i=0; i<nodeIP.size(); i++){
			if(nodeIP.get(i).peerName.equals(peerName)){
				System.out.println("Node Removed: " + peerName);
				nodeIP.remove(i);break;
			}
		}
	}
}
