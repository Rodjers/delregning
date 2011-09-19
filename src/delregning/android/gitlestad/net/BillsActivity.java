package delregning.android.gitlestad.net;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ListActivity;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.View;
import android.content.Intent;

public class BillsActivity extends ListActivity {
	
	private String username;
	private String password;
	private DelregningConnection connection;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill);
		
		Bundle bundle = getIntent().getExtras();
		username = bundle.getString("username");
		password = bundle.getString("password");
		
		connection = new DelregningConnection(username, password);

		
		presentBills();
	}
	
    private void presentBills(){
		JSONArray bills = connection.getBills();

		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		try{
    		for (int i = 0; i < bills.length(); i++){
    			JSONObject bill = (JSONObject) bills.get(i);
    			HashMap<String,String> item = new HashMap<String,String>();
    			item.put("text1", bill.getString("title"));
    			item.put("text2", bill.getString("description"));
    			item.put("slug", bill.getString("slug"));
    			list.add(item);
    		}
    		}
    		catch (JSONException e){
    			e.printStackTrace();
    		}
    		
    		this.setListAdapter(new SimpleAdapter(
														this, 
														list,
														android.R.layout.two_line_list_item,
														new String[] {"text1","text2"},
														new int[] {android.R.id.text1, android.R.id.text2}));
    		final ListView lv = getListView();
    		lv.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                	
                	@SuppressWarnings("unchecked")
                	HashMap<String,String> item = (HashMap<String,String>) lv.getItemAtPosition(position);
                    Intent billIntent = new Intent(BillsActivity.this, BillActivity.class);
                    billIntent.putExtra("slug", item.get("slug"));
            		billIntent.putExtra("username", username);
            		billIntent.putExtra("password", password);
            		startActivity(billIntent);
                    //Do your logic and open up a new Activity.
                }
            });
    }
}