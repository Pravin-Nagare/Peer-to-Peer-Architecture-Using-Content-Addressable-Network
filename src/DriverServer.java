
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DriverServer {
		
	public static void main(String[] args){
		
		try {
			System.out.println("IP of Bootsrap: " + InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try{			
			Registry reg = LocateRegistry.createRegistry(1099);
			BootstrapInt boot = new Bootstrap();
			reg.bind("myboot", boot);
			System.out.println("Bootstrap Server is ready on port 1099");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
