package it.unina.comics.platino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ConnectException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.*;


public class Log {
	 
	 public static void send2Orco(String endPoint, String key2send,String value2send) {
try {
          	Client client = ClientBuilder.newClient();
          	WebTarget target = client.target(endPoint + "/Platino/wsRest/LoggManagementService/CreateLog");
 
                String body="{[ \"key\": \""+key2send+"\" , \"value\": \""+value2send +"\" ]}";
System.err.println(body);
          	String dateTime=new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSS" ).format( Calendar.getInstance().getTime());
	  	target=target.queryParam("timeEvent", dateTime);
	  	target=target.queryParam("ors", "OR7");
	  	target=target.queryParam("prototypeName", "PdP/Pep");
		
                Invocation inv = target.request().buildPost(Entity.entity(body, MediaType.APPLICATION_JSON));
	
		String response=inv.invoke(String.class);

		System.err.println("Log server response: " + response);
} catch (Exception e ){
		System.err.println("Log error " + e.getMessage());
}
	}
}



