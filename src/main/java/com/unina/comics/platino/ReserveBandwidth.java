package com.unina.comics.platino;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;


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
      @DefaultValue("TCP") @QueryParam("protocol") String protocol,
      @DefaultValue("100") @QueryParam("bandwidth") String bandwidth){
	
        return "Reserving: " + srcIp + " " + dstIp + " " + srcPort + " " + dstPort + " " + protocol + " " + bandwidth + "\n";
    }
}
