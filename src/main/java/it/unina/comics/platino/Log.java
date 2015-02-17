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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Log {
	 
	 public static void send2Orco(String endPoint, String _key2send,String _value2send) throws Exception{

			
			JsonObject tag =new JsonObject();
			tag.addProperty("key",_key2send);
			tag.addProperty("value",_value2send);
			
			JsonArray tags= new JsonArray();
			tags.add(tag);
			
			JsonObject request = new JsonObject();
			String dateTime=new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSS" ).format( Calendar.getInstance().getTime());
			request.addProperty("timeEvent", dateTime);
			request.addProperty("ors", "OR7");
			request.addProperty("prototypeName", "PdP/Pep");
			//request.addProperty("userId", "1");
			//request.addProperty("sessionId", "1");
			//request.addProperty("fruitionId", "1");

			request.add("tags",tags);
			JsonObject createLog=new JsonObject();
			createLog.add("CreateLog", request);
			Gson gson = new Gson();
			
			System.out.println("####### json="+gson.toJson(createLog));
			sendPostRequest(endPoint,gson.toJson(createLog));
		}
	 public static String sendPostRequest(String URI, String inputJsonString) throws Exception {
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
}
