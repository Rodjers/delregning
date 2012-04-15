package delregning.android.gitlestad.net;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
//import org.json.JSONException;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
//import android.widget.Checkable;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class BillActivity extends ListActivity {

	private String username;
	private String password;
	private String slug;
	private DelregningConnection connection;
	private JSONObject bill;
	private AlertDialog.Builder addParticipantDialogBuilder;
	private AlertDialog.Builder newPaymentDialogBuilder;
	private AlertDialog.Builder addExpenseDialogBuilder;
	private static final int NEW_PARTICIPANT_DIALOG = 1;
	private static final int NEW_PAYMENT_DIALOG = 2;
	private static final int ADD_PARTICIPANT_DIALOG = 3;
	private static final int ADD_EXPENSE_DIALOG = 4;
	private ArrayList<String> mParticipantsName;
	private ArrayList<String> mParticipantsId;
	private String participantId;


//	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill);

		Bundle bundle = getIntent().getExtras();

		username = bundle.getString("username");
		password = bundle.getString("password");
		slug = bundle.getString("slug");
		mParticipantsName = new ArrayList<String>();
		mParticipantsId = new ArrayList<String>();



		connection = new DelregningConnection(username, password);
		ArrayList<JSONObject> mJSONParticipants = connection.getParticipantsOfBill(slug);
		Iterator<JSONObject> it = mJSONParticipants.iterator();
		while (it.hasNext()){
			try {
				JSONObject tempParticipant = it.next();
				mParticipantsName.add(tempParticipant.getString("name"));
				mParticipantsId.add(new Integer(tempParticipant.getInt("id")).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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
			connection.deleteBill(slug);
			finish();
			return true;
		case R.id.button_new_participant:
			showDialog(NEW_PARTICIPANT_DIALOG);
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
		case NEW_PARTICIPANT_DIALOG:
			addParticipantDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.dialogTheme));
			addParticipantDialogBuilder.setTitle(R.string.new_participant);
			final View newParticipantDialogView = LayoutInflater.from(this).inflate(R.layout.new_participant_dialog, (ViewGroup) findViewById(R.id.new_participant_layout));
			addParticipantDialogBuilder.setView(newParticipantDialogView);
			Button addExistingButton = (Button)newParticipantDialogView.findViewById(R.id.button_add_existing);
			addExistingButton.setOnClickListener(this.addExistingListener);
			addParticipantDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
						String mName = ((EditText) newParticipantDialogView.findViewById(R.id.edit_name)).getText().toString();
						String mEmail = ((EditText) newParticipantDialogView.findViewById(R.id.edit_email)).getText().toString();
						if(mName != null && mEmail != null){
							connection.registerParticipant(slug, mName, mEmail);
							//connection.addParticipant(slug, mParticipants.getInt("id"), ((Checkable) newParticipantDialogView.findViewById(R.id.checkbox_send_invitation)).isChecked());
						}
					

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
			final View newPaymentDialogView = LayoutInflater.from(this).inflate(R.layout.new_payment_dialog, (ViewGroup) findViewById(R.id.new_payment_layout));
			newPaymentDialogBuilder.setView(newPaymentDialogView);
			newPaymentDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {



				}
			});
			newPaymentDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});	

			dialog = (Dialog)newPaymentDialogBuilder.show();
			break;
			
		case ADD_PARTICIPANT_DIALOG:
			
			dialog = null;
			break;
			
		case ADD_EXPENSE_DIALOG:
			
			addExpenseDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialogTheme));
			addExpenseDialogBuilder.setTitle(R.string.new_expense);
			final View addExpenseDialogView = LayoutInflater.from(this).inflate(R.layout.add_expense_dialog, (ViewGroup) findViewById(R.id.add_expense_layout));
			addExpenseDialogBuilder.setView(addExpenseDialogView);
			Spinner paidBySpinner = (Spinner)addExpenseDialogView.findViewById(R.id.spinner_paid_by);
			ArrayAdapter<String> paidBySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mParticipantsName);
			paidBySpinner.setAdapter(paidBySpinnerAdapter);
			paidBySpinner.setOnItemSelectedListener(addExpenseListener);
			addExpenseDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String description = ((EditText) addExpenseDialogView.findViewById(R.id.edit_description)).getText().toString();
					String amount = ((EditText) addExpenseDialogView.findViewById(R.id.edit_amount)).getText().toString();
					connection.addExpense(slug, description, amount, participantId, null);

				}
			});
			addExpenseDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			});	

			dialog = (Dialog)addExpenseDialogBuilder.show();
			break;
			
		default:
			dialog = null;
			break;
		}
		return dialog;
	}
	
	private OnClickListener addExistingListener = new OnClickListener(){
		public void onClick(View v){
			dismissDialog(NEW_PARTICIPANT_DIALOG);
			showDialog(ADD_PARTICIPANT_DIALOG);

		}
	};
	
	private OnItemSelectedListener addExpenseListener = new OnItemSelectedListener(){

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			participantId = mParticipantsId.get(pos);
			
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing
			
		}
		
	};



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
				String splitString = (String) getResources().getText(R.string.split_between);
				for (int n = 0; n < split_between.length(); n++){
					splitObject = split_between.getJSONObject(n);
					if (n != 0){
						splitString = splitString + ", ";
					}
					splitString = splitString + splitObject.getString("name"); 				
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

						if (position == lv.getCount( ) -1){
							showDialog(ADD_EXPENSE_DIALOG);
						}

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