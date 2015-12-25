
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface RemoteInterface extends Remote{
	public boolean join(int x, int y, String ipAddress, String peerName, int port) throws RemoteException;
	public void printZoneInfo() throws RemoteException;
	public void printNeighborList() throws RemoteException;
	public void removeNode(String peerName) throws RemoteException;
	public void addNeighborNode(NeighborNodes tempNodeOld) throws RemoteException;
	public boolean viewByPeerName(String peerName) throws RemoteException;
	public void displayNodeInfo(NeighborNodes node, List<NeighborNodes> list) throws RemoteException;
	public void viewAllNodesInfo() throws RemoteException;
	public void insertFileToNode(String fileName) throws RemoteException;
	public void sendMeFile(String fileName, List<NeighborNodes> list) throws RemoteException;
	public void serachInNextNode(int x, int y, String fileName, List<NeighborNodes> list) throws RemoteException;
	public boolean getData(String name, byte[] fileData, int fileLen) throws RemoteException;
	public void searchFileInCAN(String fileName) throws RemoteException;
	public void searchFileInNeighbor(int x, int y, String fileName, List<NeighborNodes> list) throws RemoteException;
	public void displayFileInformation(String fileName, List<NeighborNodes> list, boolean b) throws RemoteException;
	public void divideHashMap(ClientNode clientNode, RemoteInterface rInt) throws RemoteException;
	public boolean checkInZone(int x, int y, ZoneCoordinates zoneCoordinates) throws RemoteException;	
	public void addInHashMap(String key) throws RemoteException;
	public void leaveNetwork() throws RemoteException;
	public void mergeZone(String side, NeighborNodes node) throws RemoteException;
	public void transferNeighborList(NeighborNodes node, List<NeighborNodes> list) throws RemoteException;
	public String checkSideOfNode(NeighborNodes node) throws RemoteException;
	public void copyAllFilesToMerger(NeighborNodes node) throws RemoteException;
	public Map<String, Boolean> getFileMap() throws RemoteException;
	
	public ZoneCoordinates getZone() throws RemoteException;
	public void setZone(ZoneCoordinates zone) throws RemoteException;
	public List<NeighborNodes> getNeighbor() throws RemoteException;
	public void setNeighbor(List<NeighborNodes> neighbor) throws RemoteException;
	public int getxCoordinate() throws RemoteException;
	public void setxCoordinate(int xCoordinate) throws RemoteException;
	public int getyCoordinate() throws RemoteException;
	public void setyCoordinate(int yCoordinate) throws RemoteException;
	public String getIpAddress() throws RemoteException;
	public void setIpAddress(String ipAddress) throws RemoteException;
	public int getPort() throws RemoteException;
	public void setPort(int port) throws RemoteException;
	public String getPeerName() throws RemoteException;
	public void setPeerName(String peerName) throws RemoteException;
	public RemoteInterface getR() throws RemoteException;
	public void setR(RemoteInterface r) throws RemoteException;
	public void initializeZone() throws RemoteException;
}
