package it.unina.comics.platino;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


// netconf related imports
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import net.juniper.netconf.Device;
import net.juniper.netconf.NetconfException;
import net.juniper.netconf.XML;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String; 

/**
 * Root resource (exposed at "ReserveBandwidth" path)
 */
@Path("ReserveBandwidth")
public class ReserveBandwidth  {

	private class Router {

		String  _ip;
		String  _interface;
		Integer _bandwitdh;

		public Router(String myIp, String myInterface, Integer myBand) {
			this._ip = myIp;
			this._interface = myInterface;
			this._bandwitdh = myBand;
		}

		public String getMyIP() 	{ return this._ip; 		}
		public String getMyInterface() 	{ return this._interface; 	}
		public Integer getMybandwitdh() { return this._bandwitdh; 	}
	}
	
	private ArrayList<Router> myRouters = new ArrayList<>();	

	private ArrayList<Router> getPath ( boolean serviceAvailable,
					    String ip, 
					    String srcIP, 
					    String dstIP, 
					    String SrcPort, 
					    String DstPort, 
					    String Bandwidth ) {
		String responseMsg;
		String URI="http://" + ip + "/NM/";
				
		//quale IP devo mettere?		
		if ( serviceAvailable ) {
			Client c = ClientBuilder.newClient();			
			WebTarget target = c.target(URI);			
			responseMsg = target.path("?function=PT_Traceroute&srcip="+ dstIP +"&dstip="+ srcIP +"").request().get(String.class);
		
		} else {
			responseMsg = "{0,\"hops\": [{1,\"IP\":\"localhost\",2}]}";
		}
	
		Object obj=JSONValue.parse(responseMsg);
		JSONObject jobj=(JSONObject)obj;
		JSONArray array=(JSONArray)jobj.get("hops");
		int len=array.size();

		for (int i=0; i<len;i++){
			String routerIP=((JSONObject)array.get(i)).get("IP").toString();
			myRouters.add( new Router(routerIP, routerIP, 10000));
		}			
	
		
		return myRouters;
	}

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    //@Produces(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt(
      @DefaultValue("") @QueryParam("srcip") String srcIp,
      @DefaultValue("") @QueryParam("dstip") String dstIp,
      @DefaultValue("") @QueryParam("srcport") String srcPort,
      @DefaultValue("") @QueryParam("dstport") String dstPort,
      @DefaultValue("TCP") @QueryParam("protocol") String protocol,
      @DefaultValue("") @QueryParam("bandwidth") String bandwidth)
      {      
	String user= "gennaro";
	String password= "gennaro";	
	boolean error=false;	
	String errMsg= " ";	
	boolean serviceAvailable=false;
	//NetconfAdapter netconf = new NetconfAdapter();	
	String routerIP="invalid";
	String NMIP="x.x.x.x";
	System.err.println("Serving request...");
	return "ok";

	try 
	{	
		myRouters = getPath(serviceAvailable, NMIP, srcIp, dstIp, srcPort, dstPort, bandwidth);
		
		for ( int i = 0; i < myRouters.size(); i++ )
			{

			int proto;
			if (protocol.toUpperCase()=="TCP")
				proto=6;
			else if (protocol.toUpperCase()=="UDP")
				proto=17;
			else if (protocol.toUpperCase()=="ICMP")
				proto=1;
			else {
				return "Err: Wrong protocol specification";	
			}			

			Router x = myRouters.get(i);
			routerIP = x.getMyIP();

			String configuration =
				"<shaper xmlns=\"http://www.comics.unina.it/nc/shaper\">"+
					"<qdisc>" +
						"<interface>" + x.getMyIP() + "</interface>"+
						"<bandwidth>" + x.getMybandwitdh() + "</bandwidth>" +
						"<class>"+
							"<id>11</id>"+
							"<rate>" + bandwidth + "</rate>"+
							"<ceil>" + x.getMybandwitdh() + "</ceil>"+
							"<filter>"+
								"<id>16</id>"+
								"<protocol>" + proto + "</protocol>"+
								"<sourcePort>" + srcPort + "</sourcePort>"+
								"<destinationPort>" + dstPort + "</destinationPort>"+
								"<source>" + srcIp +  "</source>" +
								"<destination>" + dstIp + "</destination>" +
							"</filter>"+
						"</class>"+
					"</qdisc>"+
				"</shaper>";	

			//NetconfAdapter router = new NetconfAdapter();

			netconf.ConfigureRouter(x.getMyIP(), user, password, configuration);
		        netconf.CloseConnection();

			}
		} catch (NetconfException e)
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();			
			System.err.println(e);      
			} 
		catch (ParserConfigurationException e)
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();			
			System.err.println(e);      			
			} 
		catch (SAXException e)	
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();			
			System.err.println(e);
			} 
		catch (IOException e)
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();
			System.err.println(e);
		      	}
 		catch (InterruptedException e)
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();
			System.err.println(e);
		      	}
		finally {
			netconf.CloseConnection();
		}

	if ( error == false )
		{errMsg = "ok";}
	//errMsg += "\n\n";
	return errMsg;
      }
}
