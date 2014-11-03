package it.unina.comics.platino;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;


// netconf related imports
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import net.juniper.netconf.Device;
import net.juniper.netconf.NetconfException;
import net.juniper.netconf.XML;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.List;


/**
 * Root resource (exposed at "ReserveBandwidth" path)
 */
@Path("ReserveBandwidth")
public class ReserveBandwidth {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt(
      @DefaultValue("") @QueryParam("srcIp") String srcIp,
      @DefaultValue("") @QueryParam("dstIp") String dstIp,
      @DefaultValue("") @QueryParam("srcPort") String srcPort,
      @DefaultValue("") @QueryParam("dstPort") String dstPort,
      @DefaultValue("") @QueryParam("protocol") String protocol,
      @DefaultValue("") @QueryParam("bandwidth") String bandwidth){
      
           
      try {
	// ottieni qua percorco...

	// poi fai un ciclo che, per ogni router, esegue le istruzioni seguenti... ovviamente la configurazione va preparata per ogni router in maniera specifica.
      
	String configuration = "<shaper xmlns=\"http://www.comics.unina.it/nc/shaper\">"+
							   "<qdisc><interface>eth0</interface>"+
							   "<bandwidth>10000</bandwidth><class><id>11</id><rate>3000</rate><ceil>10000</ceil><filter><id>16</id><protocol>0</protocol><sourcePort>1040</sourcePort><destinationPort>2000</destinationPort></filter></class>"+
							   "</qdisc></shaper>";	
      
	NetconfAdapter router = new NetconfAdapter();

	router.ConfigureRouter("127.0.0.1", "gennaro", "gennaro", configuration);
	 
	
	// alla fine restituisci il risultato in formato JSON specificando se tutto è ok o se ci sono stati problemi.
	return "Reserving: " + srcIp + " " + dstIp + " " + srcPort + " " + dstPort + " " + protocol + " " + bandwidth + "\n";

      } catch (NetconfException e){
	//TODO gestire errori
	System.err.println(e);
      } catch (ParserConfigurationException e){
	System.err.println(e);
      } catch (SAXException e){
	System.err.println(e);
      } catch (IOException e){
	System.err.println(e);
      } catch (InterruptedException e){
	System.err.println(e);
      } finally  {
	return "Error";
      }
      
	
    }
}
