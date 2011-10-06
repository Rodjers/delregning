package delregning.android.gitlestad.net;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import delregning.android.gitlestad.net.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class BillActivity extends ListActivity {

	private String username;
	private String password;
	private String slug;
	private DelregningConnection connection;
	private JSONObject bill;
	private AlertDialog.Builder addParticipantDialogBuilder;
	private AlertDialog.Builder newPaymentDialogBuilder;
	private static final int ADD_PARTICIPANT_DIALOG = 1;
	private static final int NEW_PAYMENT_DIALOG = 2;

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
		switch (item.getItemId()) {
		case R.id.button_delete:
			//connection.deleteBill(slug);
			finish();
			return true;
		case R.id.button_new_participant:
			showDialog(ADD_PARTICIPANT_DIALOG);
			return true;
		case R.id.button_new_payment:
			showDialog(NEW_PAYMENT_DIALOG);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id){

		Dialog dialog;
		switch(id){
		case ADD_PARTICIPANT_DIALOG:
			addParticipantDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.dialogTheme));
			addParticipantDialogBuilder.setTitle(R.string.new_participant);
			final View addParticipantDialogView = LayoutInflater.from(this).inflate(R.layout.add_participant_dialog, (ViewGroup) findViewById(R.id.add_participant_layout));
			addParticipantDialogBuilder.setView(addParticipantDialogView);
			addParticipantDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					//TODO Create add participant code

				}
			});
			addParticipantDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});	

			dialog = (Dialog)addParticipantDialogBuilder.show();
			break;
		case NEW_PAYMENT_DIALOG:
			newPaymentDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialogTheme));
			newPaymentDialogBuilder.setTitle(R.string.new_payment);
			View newPaymentDialogView = LayoutInflater.from(this).inflate(R.layout.new_payment_dialog, (ViewGroup) findViewById(R.id.new_payment_layout));
			newPaymentDialogBuilder.setView(newPaymentDialogView);
			newPaymentDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					//TODO Create add participant code

				}
			});
			newPaymentDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});	
			
			dialog = (Dialog)newPaymentDialogBuilder.show();
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
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
				item.put("paid_by", getResources().getText(R.string.paid_by) + " " + paid_by.getString("name"));
				JSONArray split_between = expense.getJSONArray("split_between");
				JSONObject splitObject =  split_between.getJSONObject(0);
				String splitString = getResources().getText(R.string.split_between) + " " + paid_by.getString("name") + ", " + splitObject.getString("name");
				for (int n = 1; n < split_between.length(); n++){
					splitObject = split_between.getJSONObject(n);
					splitString = splitObject.getString("name"); 				
				}
				item.put("split_between", splitString);
				list.add(item);
			}
			
			if (list.isEmpty()){
			HashMap<String,String> lastItem = new HashMap<String,String>();
			lastItem.put("title", (String) getResources().getText(R.string.no_expenses));
			lastItem.put("paid_by", (String) getResources().getText(R.string.click_new_expense));
			list.add(lastItem);

			final ListView lv = getListView();
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						//showDialog(ADD_EXPENSE_DIALOG);

					}
				});
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