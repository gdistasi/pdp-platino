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
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ConnectException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;


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

	public ReserveBandwidth(){
	    try {
	      loadPdPConfig();
	    } catch (IOException e){
		System.err.println(e);
		System.exit(1);
	    }
	}

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
	private String nm_ip;
	private String pep_user;
	private String pep_password;

	private ArrayList<Router> getPath ( boolean serviceAvailable,
					    String ip, 
					    String srcIP, 
					    String dstIP, 
					    String SrcPort, 
					    String DstPort, 
					    String Bandwidth ) throws ParseException,ConnectException {
		String responseMsg;
		String URI="http://" + nm_ip + "/NM/";
	
	
		if ( serviceAvailable ) {
			Client c = ClientBuilder.newClient();			
			WebTarget target = c.target(URI);			
			responseMsg = target.path("?function=PT_Traceroute&srcip="+ dstIP +"&dstip="+ srcIP +"").request().get(String.class);
		
		} else {
			responseMsg = "{\"timestamp\": 0,\"hops\": [ {\"hop\": 1,\"ip\":\"localhost\",\"rtt\": 2} ]}";
		}
	
		System.err.println("Response from NM: ");
		System.err.println(responseMsg);
		
		JSONParser parser=new JSONParser();
	
		Object obj=parser.parse(responseMsg);
		JSONObject jobj=(JSONObject)obj;
		JSONArray array=(JSONArray)jobj.get("hops");
		int len=array.size();

		System.err.println("Path is made of " + len + " hop(s).");
		
		System.err.println("Hops: ");
		for (int i=0; i<len;i++){
			String routerIP=((JSONObject)array.get(i)).get("ip").toString();
			System.err.println(routerIP);
			myRouters.add( new Router(routerIP, routerIP, 10000));
		}			
	
		
		return myRouters;
	}

	
    private void loadPdPConfig() throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader("/etc/pdp.conf"));
	String line;
	
	nm_ip="";
	pep_user="";
	pep_password="";
	
	while ((line = reader.readLine()) != null){
	    String[] result = line.split("=");
	    if (result[0].trim().toUpperCase().equals("NM_IP")){
	      nm_ip=result[1].trim();
	    } else if (result[0].trim().toUpperCase().equals("PEP_USER")){
	      pep_user=result[1].trim();
	    } else if (result[0].trim().toUpperCase().equals("PEP_PASSWORD")){
	      pep_password=result[1].trim();
	    } else if (result[0].trim().length()==0 || result[0].trim().charAt(0)=='#'){ 
	      continue;
	    } else {
	      throw new IOException("Error in configuration file (/etc/pdp.conf): " + result[0].trim() + " is unknokwn.");
	    }
	}
	
	reader.close();
    }
	
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.TEXT_PLAIN)
    public String getIt(
      @DefaultValue("") @QueryParam("srcip") String srcIp,
      @DefaultValue("") @QueryParam("dstip") String dstIp,
      @DefaultValue("") @QueryParam("srcport") String srcPort,
      @DefaultValue("") @QueryParam("dstport") String dstPort,
      @DefaultValue("TCP") @QueryParam("protocol") String protocol,
      @DefaultValue("") @QueryParam("bandwidth") String bandwidth)
      {      
	boolean error=false;	
	String errMsg= " ";	
	boolean serviceAvailable;
	
	NetconfAdapter netconf = new NetconfAdapter();	
	String routerIP="invalid";
	System.err.println("Serving request...");

	try 
	
	
	{	
		if (nm_ip.equals("")){
		  System.err.println("NM is not available.");
		  serviceAvailable=false;
		} else {
		  System.err.println("NM available.");
		  serviceAvailable=true;
		}
	
		myRouters = getPath(serviceAvailable, nm_ip, srcIp, dstIp, srcPort, dstPort, bandwidth);
		
		for ( int i = 0; i < myRouters.size(); i++ )
			{
			
			int proto=-1;
			if (protocol.toUpperCase().equals("TCP"))
				proto=6;
			else if (protocol.toUpperCase().equals("UDP"))
				proto=17;
			else if (protocol.toUpperCase().equals("ICMP"))
				proto=1;
			else {
				errMsg="Err: Wrong protocol specification";
				error=true;
				System.err.println(errMsg);
				break;
			}			
			
			if (proto!=-1){
			
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
								"<protocol>" + proto + "</protocol>";
								
			   if (!srcPort.equals("")){
			      configuration = configuration + "<sourcePort>" + srcPort + "</sourcePort>";				
			   }
			   if (!dstPort.equals("")){
			      configuration = configuration + "<destinationPort>" + dstPort + "</destinationPort>";
			   }
			   if (!srcIp.equals("")){
			      configuration = configuration + "<source>" + srcIp +  "</source>";
			   }
			   if (!dstIp.equals("")){
			      configuration = configuration + "<destination>" + dstIp + "</destination>";
			   }
			   
			   configuration = configuration + "</filter></class></qdisc></shaper>";	

			   netconf.ConfigureRouter(x.getMyIP(), pep_user, pep_password, configuration);
			   netconf.CloseConnection();

			}
		}
	} catch (NetconfException e)
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();			
			System.err.println(errMsg);
			} 
	catch (ParserConfigurationException e)
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();			
			System.err.println(errMsg);
			} 
	catch (SAXException e)	
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();			
			System.err.println(errMsg);
			} 
	catch (IOException e)
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();
			System.err.println(errMsg);
		      	}
 	catch (InterruptedException e)
			{
			error=true;
			errMsg= "Error in configuring router " + routerIP + " : " + e.getMessage();
			System.err.println(errMsg);
		      	}
	catch (ParseException e)
			{
			error=true;
			errMsg="Error in getting the path from NM: " + e.getMessage();
			System.err.println(errMsg);
			} 
	catch (Exception e) 
			{
			error=true;
			errMsg="Error: " + e.getMessage();
			System.err.println(errMsg);
	} finally {
	      try {
			netconf.CloseConnection();
	      } catch (Exception e){}	
	}

	if ( error == false )
		{errMsg = "ok";}

	return errMsg;
      }
      

}
