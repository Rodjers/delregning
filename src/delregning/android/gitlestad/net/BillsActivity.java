package delregning.android.gitlestad.net;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import delregning.android.gitlestad.net.R;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.DialogInterface;
import android.content.Intent;

public class BillsActivity extends ListActivity {

	private String username;
	private String password;
	private DelregningConnection connection;
	private final static int ADD_BILL_DIALOG = 1;
	private final static int LOADING = 2;
	private AlertDialog.Builder addBillDialogBuilder;
	private JSONArray mBills;
	private ArrayList<JSONObject> mParticipants;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		username = bundle.getString("username");
		password = bundle.getString("password");
		connection = new DelregningConnection(username, password);

		try {
			mBills = new JSONArray(bundle.getString("bills"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		setContentView(R.layout.bill);
		presentBills(mBills);
		mParticipants = connection.getParticipants();
	}

	@Override
	public void onResume(){
		super.onResume();
		try {
			mBills = connection.getBills();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
		presentBills(mBills);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bills_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_button_add_bill:
			showDialog(ADD_BILL_DIALOG);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id){

		Dialog dialog;
		switch(id){
		case LOADING:

			dialog = ProgressDialog.show(BillsActivity.this, "", 
					"Loading. Please wait...", true);

			break;
		case ADD_BILL_DIALOG:
		addBillDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.dialogTheme));
			addBillDialogBuilder.setTitle(R.string.new_bill);
			final View dialogView = LayoutInflater.from(this).inflate(R.layout.add_bill_dialog, (ViewGroup) findViewById(R.id.add_bill_dialog_id));
			addBillDialogBuilder.setInverseBackgroundForced(false);
			addBillDialogBuilder.setView(dialogView);
			addBillDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {	
					connection.addBill(((EditText) dialogView.findViewById(R.id.edit_title)).getText().toString(),
							((EditText) dialogView.findViewById(R.id.edit_description)).getText().toString());
					try {
						presentBills(connection.getBills());
					} catch (AuthenticationException e) {
						e.printStackTrace();
					}
					dismissDialog(ADD_BILL_DIALOG);
				}
			});
			addBillDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dismissDialog(ADD_BILL_DIALOG);
				}
			});	
			dialog = (Dialog)addBillDialogBuilder.show();
			break;

		default:
			dialog = null;
			break;
		}
		return dialog;
	}

	private void presentBills(JSONArray bills){

		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

		try{
			for (int i = 0; i < bills.length(); i++){
				HashMap<String,String> item = new HashMap<String,String>();
				JSONObject bill = (JSONObject) bills.get(i);	
				item.put("text1", bill.getString("title"));
				item.put("text2", bill.getString("description"));
				item.put("slug", bill.getString("slug"));
				list.add(item);
			}

		}

		catch (JSONException e){
			e.printStackTrace();
		}
		HashMap<String,String> lastItem = new HashMap<String,String>();
		lastItem.put("text1", (String) getResources().getText(R.string.add_bill));
		lastItem.put("text2", (String) getResources().getText(R.string.click_add_bill));
		list.add(lastItem);

		this.setListAdapter(new SimpleAdapter(
				this, 
				list,
				android.R.layout.two_line_list_item,
				new String[] {"text1","text2"},
				new int[] {android.R.id.text1, android.R.id.text2}));
		final ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


				if (position == lv.getCount( ) -1){
					showDialog(ADD_BILL_DIALOG);
				}
				else {
					@SuppressWarnings("unchecked")
					
					ProgressDialog loadingDialog = new ProgressDialog(BillsActivity.this);
					loadingDialog.getContext().setTheme(R.style.dialogTheme);
					loadingDialog.setMessage(getString(R.string.please_wait));
					loadingDialog.setTitle(R.string.logging_in);
					loadingDialog.show();
					
					HashMap<String,String> item = (HashMap<String,String>) lv.getItemAtPosition(position);
					Intent billIntent = new Intent(BillsActivity.this, BillActivity.class);
					billIntent.putExtra("slug", item.get("slug"));
					billIntent.putExtra("username", username);
					billIntent.putExtra("password", password);
					billIntent.putExtra("participants", mParticipants);
					JSONObject tBill = connection.getBill(item.get("slug"));
					JSONObject bill;
					try {
						bill = tBill.getJSONObject("bill");
					
					final JSONArray expenses = bill.getJSONArray("expenses");
					billIntent.putExtra("expenses", expenses.toString());
					} catch (JSONException e) {
						loadingDialog.dismiss();
						e.printStackTrace();
					}
					loadingDialog.dismiss();
					startActivity(billIntent);
				}
			}
		});
	}
}