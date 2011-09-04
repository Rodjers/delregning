package delregning.android.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Base64;


public class DelregningConnection {

	private DefaultHttpClient httpClient;
	private String username;
	private String password;

	public DelregningConnection(String brukernavn, String passord) {
		httpClient = new DefaultHttpClient();
		username = brukernavn;
		password = passord;
		
	}
	
	public JSONArray getBills(){
		try{
			HttpGet httpget = new HttpGet("http://delregning.no/bills/all/");
			httpget.setHeader("Accept", "application/json");
			httpget.setHeader("Authorization", "Basic "+Base64.encodeToString((username + ":" + password).getBytes(),2));
			HttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			JSONObject billObject = new JSONObject(convertStreamToString(stream));
			JSONArray bills = billObject.getJSONArray("bills");
			
			return bills;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getBill(String slug){
		
		try{
		HttpGet httpget = new HttpGet("http://delregning.no/bills/" + slug + "/");
		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Authorization", "Basic "+Base64.encodeToString((username + ":" + password).getBytes(),2));
		HttpResponse response = httpClient.execute(httpget);
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		JSONObject bill = new JSONObject(convertStreamToString(stream));
		return bill;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
		
	}
	
	public void deleteBill(String slug){
		HttpPost httppost = new HttpPost("http://delregning.no/bills/" + slug + "/remove/");
		httppost.setHeader("Authorization", "Basic "+Base64.encodeToString((username + ":" + password).getBytes(),0));
		try{
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			String temp = convertStreamToString(stream);
			System.err.print(temp);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
