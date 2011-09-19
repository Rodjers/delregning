package delregning.android.gitlestad.net;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import delregning.android.gitlestad.net.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleAdapter;

public class BillActivity extends ListActivity {
	
	private String username;
	private String password;
	private String slug;
	private DelregningConnection connection;
	private JSONObject bill;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill);
		
		Bundle bundle = getIntent().getExtras();
		
		username = bundle.getString("username");
		password = bundle.getString("password");
		slug = bundle.getString("slug");
		
		connection = new DelregningConnection(username, password);
		presentExpenses(slug);


		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.bill_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.delete:
	    //TODO Implement deleteBill(String slug) correctly   
	    // connection.deleteBill(slug);
	        finish();
	        return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void presentExpenses(String tSlug){
		try{
		JSONObject tBill = connection.getBill(slug);
		bill = tBill.getJSONObject("bill");
		JSONArray expenses = bill.getJSONArray("expenses");
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

    		for (int i = 0; i < expenses.length(); i++){
    			JSONObject expense = (JSONObject) expenses.get(i);
    			HashMap<String,String> item = new HashMap<String,String>();
    			item.put("title", expense.getString("description"));
    			int amount = expense.getInt("amount");
    			item.put("amount", Integer.toString(amount));
    			JSONObject paid_by = expense.getJSONObject("paid_by");
    			item.put("paid_by", "Paid by: " + paid_by.getString("name"));
    			JSONArray split_between = expense.getJSONArray("split_between");
    			JSONObject splitObject =  split_between.getJSONObject(0);
    			String splitString = "Split between: " + paid_by.getString("name") + ", " + splitObject.getString("name");
    			for (int n = 1; n < split_between.length(); n++){
        			splitObject = split_between.getJSONObject(n);
        			splitString = splitObject.getString("name"); 				
    			}
    			item.put("split_between", splitString);
    			list.add(item);
    		}
    		
    		setListAdapter(new SimpleAdapter(
														this, 
														list,
														R.layout.expense,
														new String[] {"title","amount","paid_by","split_between"},
														new int[] {R.id.title, R.id.amount, R.id.paid_by, R.id.split_between}));
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}