
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NeighborNodes implements Serializable{
	public ZoneCoordinates zone = null;
	String ipAddress;
	String peerName;
	int port;
	List<NeighborNodes> listOfNeighbors = null;
	//Add map for file storing
	public NeighborNodes(){
		zone = new ZoneCoordinates();
		listOfNeighbors = new ArrayList<NeighborNodes>();
		ipAddress = null;
		String peerName=null;
		int port=0;
	}
	public ZoneCoordinates getZone() {
		return zone;
	}
	public void setZone(ZoneCoordinates zone) {
		this.zone = zone;
	}
	public List<NeighborNodes> getListOfNeighbors() {
		return listOfNeighbors;
	}
	public void setListOfNeighbors(List<NeighborNodes> listOfNeighbors) {
		this.listOfNeighbors = listOfNeighbors;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getPeerName() {
		return peerName;
	}
	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}	
}
