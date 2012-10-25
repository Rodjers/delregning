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
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class BillActivity extends ListActivity {

	private String username;
	private String password;
	private String slug;
	private DelregningConnection connection;
	private JSONObject bill;
	private AlertDialog.Builder addParticipantDialogBuilder;
	private AlertDialog.Builder addExpenseDialogBuilder;
	private AlertDialog.Builder editExpenseDialogBuilder;
	private AlertDialog.Builder confirmDialogBuilder;
	private AlertDialog.Builder editBillDialogBuilder;
	private static final int NEW_PARTICIPANT_DIALOG = 1;
	private static final int ADD_PARTICIPANT_DIALOG = 2;
	private static final int ADD_EXPENSE_DIALOG = 3;
	private static final int EDIT_EXPENSE_DIALOG = 4;
	private static final int EDIT_BILL_DIALOG = 5;
	private static final int CONFIRM_DELETE_EXPENSE_DIALOG = 6;
	private static final int CONFIRM_DELETE_BILL_DIALOG = 7;	
	private ArrayList<String> mParticipantsName;
	private ArrayList<String> mParticipantsId;
	private String participantId;
	private JSONObject currentExpense;
	private JSONArray mExpenses;



	//	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill);

		Bundle bundle = getIntent().getExtras();

		username = bundle.getString("username");
		password = bundle.getString("password");
		slug = bundle.getString("slug");
		try {
			mExpenses = new JSONArray(bundle.getString("expenses"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
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
			presentExpenses(mExpenses);
		



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
			showDialog(CONFIRM_DELETE_BILL_DIALOG);
			return true;
		case R.id.button_new_participant:
			showDialog(NEW_PARTICIPANT_DIALOG);
			return true;
		case R.id.button_new_expense:
			showDialog(ADD_EXPENSE_DIALOG);
			return true;
		case R.id.button_edit_bill:
			showDialog(EDIT_BILL_DIALOG);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id){

		Dialog dialog;
		ArrayAdapter<String> paidBySpinnerAdapter;
		switch(id){
		case NEW_PARTICIPANT_DIALOG:
			addParticipantDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.dialogTheme));
			addParticipantDialogBuilder.setTitle(R.string.new_participant);
			final View newParticipantDialogView = LayoutInflater.from(this).inflate(R.layout.new_participant_dialog, (ViewGroup) findViewById(R.id.new_participant_layout));
			addParticipantDialogBuilder.setView(newParticipantDialogView);
			addParticipantDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String mName = ((EditText) newParticipantDialogView.findViewById(R.id.edit_name)).getText().toString();
					String mEmail = ((EditText) newParticipantDialogView.findViewById(R.id.edit_email)).getText().toString();
					if(mName != null && mEmail != null){
						connection.registerParticipant(slug, mName, mEmail);
					}
					dismissDialog(NEW_PARTICIPANT_DIALOG);
				}
			});
			addParticipantDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dismissDialog(NEW_PARTICIPANT_DIALOG);

				}
			});	

			dialog = (Dialog)addParticipantDialogBuilder.show();
			break;

		case ADD_PARTICIPANT_DIALOG:

			dialog = null;
			break;

		case ADD_EXPENSE_DIALOG:

			addExpenseDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialogTheme));
			addExpenseDialogBuilder.setTitle(R.string.new_expense);
			final View addExpenseDialogView = LayoutInflater.from(this).inflate(R.layout.expense_dialog, (ViewGroup) findViewById(R.id.expense_dialog_layout));
			addExpenseDialogBuilder.setView(addExpenseDialogView);
			Spinner paidBySpinner = (Spinner)addExpenseDialogView.findViewById(R.id.spinner_paid_by);
			paidBySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mParticipantsName);
			paidBySpinner.setAdapter(paidBySpinnerAdapter);
			paidBySpinner.setOnItemSelectedListener(paidBySpinnerListener);
			paidBySpinner.getContext().setTheme(R.style.dialogTheme);

			final TableLayout addExpenseParticipantsTable = (TableLayout)addExpenseDialogView.findViewById(R.id.table_split_between);
			for (int i = 0; i < mParticipantsName.size(); i++){
				CheckBox checkBox = new CheckBox(this);
				TableRow tableRow = new TableRow(this);
				checkBox.setText(mParticipantsName.get(i));
				checkBox.setTextColor(Color.BLACK);
				tableRow.addView(checkBox);
				addExpenseParticipantsTable.addView(tableRow);

			}

			addExpenseDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String description = ((EditText) addExpenseDialogView.findViewById(R.id.edit_description)).getText().toString();
					String amount = ((EditText) addExpenseDialogView.findViewById(R.id.edit_amount)).getText().toString();
					ArrayList<String> splitBetween = new ArrayList<String>();

					for (int i = 0; i < addExpenseParticipantsTable.getChildCount(); i++){
						if(((CheckBox)((TableRow)addExpenseParticipantsTable.getChildAt(i)).getChildAt(0)).isChecked()){
							splitBetween.add(mParticipantsId.get(i));

						}

					}

					connection.addExpense(slug, description, amount, participantId, splitBetween);
					dismissDialog(ADD_EXPENSE_DIALOG);
					try {
						presentExpenses(getExpenses(slug));
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
			addExpenseDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dismissDialog(ADD_EXPENSE_DIALOG);

				}
			});	

			dialog = (Dialog)addExpenseDialogBuilder.show();
			break;

		case EDIT_EXPENSE_DIALOG:
			editExpenseDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialogTheme));
			try {
				editExpenseDialogBuilder.setTitle(currentExpense.getString("description"));

				final View editExpenseDialogView = LayoutInflater.from(this).inflate(R.layout.expense_dialog, (ViewGroup) findViewById(R.id.expense_dialog_layout));
				EditText expenseName = (EditText)editExpenseDialogView.findViewById(R.id.edit_description);
				expenseName.setText(currentExpense.getString("description"));
				EditText expenseAmount = (EditText)editExpenseDialogView.findViewById(R.id.edit_amount);
				expenseAmount.setText(currentExpense.getString("amount"));
				paidBySpinner = (Spinner)editExpenseDialogView.findViewById(R.id.spinner_paid_by);
				paidBySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mParticipantsName);
				paidBySpinner.setAdapter(paidBySpinnerAdapter);
				paidBySpinner.setSelection(mParticipantsName.indexOf(((JSONObject)currentExpense.get("paid_by")).getString("name")));
				paidBySpinner.setOnItemSelectedListener(paidBySpinnerListener);
				paidBySpinner.getContext().setTheme(R.style.dialogTheme);
				Button deleteExpenseButton = new Button(this);
				deleteExpenseButton.setText(R.string.delete_expense);

				deleteExpenseButton.setOnClickListener(deleteExpenseListener);
				JSONArray splitBetweenJSON = currentExpense.getJSONArray("split_between");
				ArrayList<String> splitBetweenArray = new ArrayList<String>();
				for (int i = 0; i < splitBetweenJSON.length(); i++){
					splitBetweenArray.add(splitBetweenJSON.getJSONObject(i).getString("name"));
				}

				final TableLayout editExpenseParticipantsTable = (TableLayout)editExpenseDialogView.findViewById(R.id.table_split_between);
				for (int i = 0; i < mParticipantsName.size(); i++){
					CheckBox checkBox = new CheckBox(this);
					TableRow tableRow = new TableRow(this);
					checkBox.setText(mParticipantsName.get(i));
					if (splitBetweenArray.indexOf(mParticipantsName.get(i)) != -1){
						checkBox.setChecked(true);
					}
					else {
						checkBox.setChecked(false);
					}
					checkBox.setTextColor(Color.BLACK);
					tableRow.addView(checkBox);
					editExpenseParticipantsTable.addView(tableRow);

				}

				LinearLayout editExpenseLinearLayout = (LinearLayout)editExpenseDialogView.findViewById(R.id.expense_linear_layout);

				editExpenseLinearLayout.addView(deleteExpenseButton);

				editExpenseDialogBuilder.setView(editExpenseDialogView);

				editExpenseDialogBuilder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String description = ((EditText) editExpenseDialogView.findViewById(R.id.edit_description)).getText().toString();
						String amount = ((EditText) editExpenseDialogView.findViewById(R.id.edit_amount)).getText().toString();
						ArrayList<String> splitBetween = new ArrayList<String>();

						for (int i = 0; i < editExpenseParticipantsTable.getChildCount(); i++){
							if(((CheckBox)((TableRow)editExpenseParticipantsTable.getChildAt(i)).getChildAt(0)).isChecked()){
								splitBetween.add(mParticipantsId.get(i));

							}

						}
						try {
							connection.updateExpense(slug, new Integer(currentExpense.getInt("id")).toString(), description, amount, participantId, splitBetween);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						removeDialog(EDIT_EXPENSE_DIALOG);
						try {
							presentExpenses(getExpenses(slug));
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});
				editExpenseDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						removeDialog(EDIT_EXPENSE_DIALOG);
					}
				});	

			} catch (JSONException e) {
				e.printStackTrace();
			}
			dialog = (Dialog)editExpenseDialogBuilder.show();

			break;

		case CONFIRM_DELETE_EXPENSE_DIALOG:

			confirmDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialogTheme));
			confirmDialogBuilder.setTitle(R.string.are_you_sure);
			View confirmDeleteExpenseDialogView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, (ViewGroup) findViewById(R.id.confirm_dialog_layout));
			confirmDialogBuilder.setView(confirmDeleteExpenseDialogView);

			confirmDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					try {
						connection.removeExpense(slug, new Integer(currentExpense.getInt("id")).toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					try {
						presentExpenses(getExpenses(slug));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					removeDialog(CONFIRM_DELETE_EXPENSE_DIALOG);

				}
			});	
			confirmDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					removeDialog(CONFIRM_DELETE_EXPENSE_DIALOG);

				}
			});	
			dialog = (Dialog)confirmDialogBuilder.show();
			break;

		case CONFIRM_DELETE_BILL_DIALOG:

			confirmDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialogTheme));
			confirmDialogBuilder.setTitle(R.string.are_you_sure);
			View confirmDeleteBillDialogView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, (ViewGroup) findViewById(R.id.confirm_dialog_layout));
			confirmDialogBuilder.setView(confirmDeleteBillDialogView);

			confirmDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {					
					connection.deleteBill(slug);
					removeDialog(CONFIRM_DELETE_BILL_DIALOG);
					finish();

				}
			});	
			confirmDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					removeDialog(CONFIRM_DELETE_BILL_DIALOG);

				}
			});	
			dialog = (Dialog)confirmDialogBuilder.show();
			break;

		case EDIT_BILL_DIALOG:

			editBillDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialogTheme));
			editBillDialogBuilder.setTitle(R.string.edit_bill);
			final View editBillDialogView = LayoutInflater.from(this).inflate(R.layout.edit_bill_dialog, (ViewGroup) findViewById(R.id.edit_bill_layout));
			editBillDialogBuilder.setView(editBillDialogView);

			try {
				EditText title = (EditText)editBillDialogView.findViewById(R.id.edit_title);
				title.setText(bill.getString("title"));

				EditText description = (EditText)editBillDialogView.findViewById(R.id.edit_description);
				description.setText(bill.getString("description"));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			editBillDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {					
					connection.updateBill(slug, ((EditText) editBillDialogView.findViewById(R.id.edit_title)).getText().toString(), ((EditText) editBillDialogView.findViewById(R.id.edit_description)).getText().toString());				
					removeDialog(EDIT_BILL_DIALOG);

				}
			});	
			editBillDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					removeDialog(EDIT_BILL_DIALOG);

				}
			});	
			dialog = (Dialog)editBillDialogBuilder.show();
			break;

		default:
			dialog = null;
			break;
		}
		return dialog;
	}

	private OnClickListener deleteExpenseListener = new OnClickListener(){
		public void onClick(View v){

			removeDialog(EDIT_EXPENSE_DIALOG);
			showDialog(CONFIRM_DELETE_EXPENSE_DIALOG);

		}
	};

	private OnItemSelectedListener paidBySpinnerListener = new OnItemSelectedListener(){

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			participantId = mParticipantsId.get(pos);

		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing

		}

	};

	private JSONArray getExpenses(String slug) throws JSONException{
		
		JSONObject tBill = connection.getBill(slug);
		bill = tBill.getJSONObject("bill");
		final JSONArray expenses = bill.getJSONArray("expenses");
		
		return expenses;
	}

	private void presentExpenses(JSONArray pExpenses){
		
		final JSONArray expenses = pExpenses;
		try{
			ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

			for (int i = 0; i < expenses.length(); i++){
				JSONObject expense = (JSONObject) expenses.get(i);
				HashMap<String,String> item = new HashMap<String,String>();
				item.put("title", expense.getString("description"));
				int amount = expense.getInt("amount");
				item.put("amount", Integer.toString(amount));
				item.put("date", expense.getString("added_date").substring(0,10));
				JSONObject paid_by = expense.getJSONObject("paid_by");
				item.put("paid_by", getResources().getText(R.string.paid_by) + " " + paid_by.getString("name"));
				JSONArray split_between = expense.getJSONArray("split_between");
				if (split_between.length() != 0){
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
				}
				list.add(item);
			}

			if (list.isEmpty()){
				HashMap<String,String> lastItem = new HashMap<String,String>();
				lastItem.put("title", (String) getResources().getText(R.string.no_expenses));
				lastItem.put("paid_by", (String) getResources().getText(R.string.click_new_expense));
				list.add(lastItem);
			}
			else {
				HashMap<String,String> lastItem = new HashMap<String,String>();
				lastItem.put("title", (String) getResources().getText(R.string.add_expense));
				lastItem.put("paid_by", (String) getResources().getText(R.string.click_new_expense));
				list.add(lastItem);
			}

			final ListView lv = getListView();
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					if (position == lv.getCount( ) -1){
						showDialog(ADD_EXPENSE_DIALOG);
<<<<<<< HEAD
					}
					else {
						try {
							currentExpense = expenses.getJSONObject(position);
						} catch (JSONException e) {
							e.printStackTrace();
						}				
						showDialog(EDIT_EXPENSE_DIALOG);
					}
=======
					}
					else {
						try {
							currentExpense = expenses.getJSONObject(position);
						} catch (JSONException e) {
							e.printStackTrace();
						}				
						showDialog(EDIT_EXPENSE_DIALOG);
					}

				}
			});

>>>>>>> development

				}
			});
			setListAdapter(new SimpleAdapter(
					this, 
					list,
					R.layout.expense,
					new String[] {"title","amount","paid_by","split_between", "date"},
					new int[] {R.id.title, R.id.amount, R.id.paid_by, R.id.split_between, R.id.date}));
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
