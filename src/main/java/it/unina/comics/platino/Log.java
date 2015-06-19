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
	 
   
	   
	public static String sendPostRequest(String URI, String inputJsonString) {
			String  output = "";
			
			try {
				String charset = "UTF-8";
				URL url = new URL(URI);
				HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setDoOutput(true);
				httpConnection.setRequestMethod("POST");
				httpConnection.setRequestProperty("Accept-Charset", charset);
				httpConnection.setRequestProperty("Accept", "application/json");
				httpConnection.setRequestProperty("Content-Type", "application/json");
				httpConnection.setRequestProperty("Request-Type", "application/json");
				OutputStream os = httpConnection.getOutputStream();
				os.write(inputJsonString.getBytes());
				os.flush();
				 
				//if ((httpConnection.getResponseCode() != HttpURLConnection.HTTP_CREATED) && (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)) {
				//	throw new RuntimeException("Failed : HTTP error code : "+ httpConnection.getResponseCode());
			//	}

				BufferedReader responseBuffer = new BufferedReader(new InputStreamReader( (httpConnection.getInputStream())));
				
				String currentLine = "";
		
				while ((currentLine = responseBuffer.readLine()) != null) {
					output += currentLine;
				}
				System.out.println("Server reply (output): [" + output + "]");
				httpConnection.disconnect();			
				

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 

	   return output;
	 }

	
   
	 public static void send2Orco(String endPoint, String key2send,String value2send) {
	    try {
          	Client client = ClientBuilder.newClient();
	        
          	WebTarget target = client.target(endPoint + "/Platino/wsRest/LoggManagementService/");
 
	        JSONObject tag=new JSONObject();
	        
	        tag.put("key", key2send);
	        tag.put("value", value2send);
	       
	        JSONArray tags = new JSONArray();
	        tags.add(tag);
	       
	        JSONObject request=new JSONObject();

 	        String dateTime=new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSS" ).format( Calendar.getInstance().getTime());
	        request.put("timeEvent", dateTime);
	        request.put("OR", "OR7");
	        request.put("prototypeName", "PolicyDecisionPoint");
	        
	        request.put("tags", tags);
	       
	        JSONObject createLog= new JSONObject();
	       
	        createLog.put("CreateLog", request);
	       
//	        System.err.println("JSON: " + createLog.toString());
  //              Invocation inv = target.request().buildPost(Entity<JSONObject>.json(createLog), "APPLICATION_JSON);
	
//		String response=inv.invoke();

	        sendPostRequest(endPoint + "/Platino/wsRest/LoggManagementService/", createLog.toString());
	       
//		System.err.println("Log server response: " + response);
	       
	    
	    } catch (Exception e ){
	        e.printStackTrace();
		System.err.println("Log error " + e.getMessage());
	    }
	}
   

   public static void main(String[] args)
     {
	
	Log.send2Orco("http://192.168.100.104:8080", "info", "Prova PDP/Pep");
	
     }
   


}



