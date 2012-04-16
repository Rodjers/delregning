package delregning.android.gitlestad.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.NameValuePair;

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
			HttpGet httpget = new HttpGet("http://splitabill.com/bills/all/");
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
			HttpGet httpget = new HttpGet("https://splitabill.com/bills/" + slug + "/");
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
		HttpPost httppost = new HttpPost("https://splitabill.com/bills/" + slug + "/delete/");
		
		postData(httppost, null);
	}
	
	public JSONObject updateBill(String slug, String title, String description, String notification, String reminder_interval, String background){
		
		HttpPost httppost = new HttpPost("https://splitabill.com/bills" + slug + "/settings/");
		List<NameValuePair> urldata = new ArrayList<NameValuePair>(2);  
		urldata.add(new BasicNameValuePair("title", title));
		urldata.add(new BasicNameValuePair("description", description));
		urldata.add(new BasicNameValuePair("notification", notification)); 
		urldata.add(new BasicNameValuePair("reminder_interval", reminder_interval)); 
		urldata.add(new BasicNameValuePair("background", background)); 
		
		return postData(httppost, urldata);
	}
	
	public JSONObject registerParticipant(String slug, String name, String email){
		
		HttpPost httppost = new HttpPost("https://splitabill.com/bills/" + slug + "/participants/add/");
		List<NameValuePair> urldata = new ArrayList<NameValuePair>(2);  
		urldata.add(new BasicNameValuePair("name", name));
		urldata.add(new BasicNameValuePair("email", email));
		urldata.add(new BasicNameValuePair("payment_info", ""));
		urldata.add(new BasicNameValuePair("new", "1"));
		
		return postData(httppost, urldata);
	}
	
	public JSONObject addParticipant(String slug, int participant, boolean send_invitation){
		
		HttpPost httppost = new HttpPost("https://splitabill.com/bills/" + slug + "/participants/add/");
		List<NameValuePair> urldata = new ArrayList<NameValuePair>(2);  
		urldata.add(new BasicNameValuePair("participant", Integer.toString(participant)));
		urldata.add(new BasicNameValuePair("existing", "1"));
		urldata.add(new BasicNameValuePair("send_invitation", Boolean.toString(send_invitation))); 
		
		return postData(httppost, urldata);
	}
	
	public JSONObject removeParticipant(String slug, int participant){
		
		HttpPost httppost = new HttpPost("http://splitabill.com/bills/" + slug + "/participants/" + Integer.toString(participant) + "/remove/");
				
		return postData(httppost, null);
	}
	
	public JSONObject addExpense(String slug, String description, String amount, String paid_by, ArrayList<String> split_between){
		HttpPost httppost = new HttpPost("https://splitabill.com/bills/" + slug + "/expenses/add/");
		List<NameValuePair> urldata = new ArrayList<NameValuePair>(2);
		urldata.add(new BasicNameValuePair("description", description));
		urldata.add(new BasicNameValuePair("amount", amount));  
		urldata.add(new BasicNameValuePair("paid_by", paid_by));
		
		if (split_between != null){
			Iterator<String> it = split_between.iterator();
			
			while(it.hasNext()){
				urldata.add(new BasicNameValuePair("split_between", it.next()));
			}
		}
		
		return postData(httppost, urldata);
	}
	
	public JSONObject removeExpense(String slug, String expense){
		
		HttpPost httppost = new HttpPost("https://splitabill.com/bills/" + slug + "/expense/" + expense + "/remove/");
		
		return postData(httppost, null);
	}
	
	public JSONObject addPayment(String slug, String amount, String paid_by, String paid_to){
		
		HttpPost httppost = new HttpPost("http://splitabill.com/bills/create/");
		List<NameValuePair> urldata = new ArrayList<NameValuePair>(2);  
		urldata.add(new BasicNameValuePair("amount", amount));  
		urldata.add(new BasicNameValuePair("paid_by", paid_by));
		urldata.add(new BasicNameValuePair("paid_by", paid_to));
		
		return postData(httppost, urldata);
	}
	
	public JSONObject removePayment(String slug, int payment_id){
		HttpPost httppost = new HttpPost("https://splitabill.com/bills/" + slug + "/payments/" + payment_id + "/remove/");
		
		return postData(httppost, null);
	}

	public JSONObject addBill(String title, String description){

		HttpPost httppost = new HttpPost("https://splitabill.com/bills/create/");
		List<NameValuePair> urldata = new ArrayList<NameValuePair>(2);  
		urldata.add(new BasicNameValuePair("title", title));  
		urldata.add(new BasicNameValuePair("description", description));
		
		return postData(httppost, urldata);
	}
	
	public ArrayList<JSONObject> getParticipants(){
		ArrayList<JSONObject> participants = new ArrayList<JSONObject>();
		
		try{
			HttpGet httpget = new HttpGet("https://splitabill.com/bills/participants/");
			httpget.setHeader("Accept", "application/json");
			httpget.setHeader("Authorization", "Basic "+Base64.encodeToString((username + ":" + password).getBytes(),2));
			HttpResponse response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			JSONObject participantsObject = new JSONObject(convertStreamToString(stream));
			JSONArray participantsArray = participantsObject.getJSONArray("participants");

			for(int i = 0; i > participantsArray.length(); i++){
				participants.add(participants.get(i));
			}

		}
		catch (Exception e){
			e.printStackTrace();
		}
		return participants;

	}
	
	public ArrayList<JSONObject> getParticipantsOfBill(String slug){
		ArrayList<JSONObject> participants = new ArrayList<JSONObject>();
		
		try{
			
			JSONObject participantsObject = getBill(slug);
			JSONObject mJSONObject = participantsObject.getJSONObject("bill");
			JSONArray participantsArray = mJSONObject.getJSONArray("participants");

			for(int i = 0; i < participantsArray.length(); i++){
				participants.add(participantsArray.getJSONObject(i));
			}

		}
		catch (Exception e){
			e.printStackTrace();
		}
		return participants;

	}
		
	private JSONObject postData(HttpPost httpPost, List<NameValuePair> urlData){

		HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
		JSONObject bill = null;

		try {
			if (urlData != null){
			httpPost.setEntity(new UrlEncodedFormEntity(urlData, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}


		httpPost.setHeader("Authorization", "Basic "+Base64.encodeToString((username + ":" + password).getBytes(),2));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		try{
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			bill = new JSONObject(convertStreamToString(stream));
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return bill;
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
