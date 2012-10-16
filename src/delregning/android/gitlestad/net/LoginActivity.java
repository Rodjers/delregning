package delregning.android.gitlestad.net;




import org.json.JSONArray;

import delregning.android.gitlestad.net.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;


public class LoginActivity extends Activity {

	private Button login_button;
	private String username;
	private String password;
	private JSONArray bills;
	private final static int LOADING = 1;
	private final static int AUTHENTICATION_ERROR_DIALOG = 2;
	private AlertDialog.Builder authenticationErrorDialogBuilder;
	private DelregningConnection connection;
	private ProgressDialog loadingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		login_button = (Button)findViewById(R.id.login_button);
		login_button.setOnClickListener(loginListener);
		EditText editUsername = (EditText)findViewById(R.id.username);
		EditText editPassword = (EditText)findViewById(R.id.password);
		
		editUsername.setOnEditorActionListener(editLoginListener);
		editPassword.setOnEditorActionListener(editLoginListener);
		

	}
	
	@Override
	public void onPause() {
		super.onPause();
		//loadingDialog.dismiss();
		
	}

	protected Dialog onCreateDialog(int id){
		Dialog dialog = null;

		switch(id){

		case LOADING:

			dialog = ProgressDialog.show(LoginActivity.this, "", 
					"Loading. Please wait...", true);
			

			break;

		case AUTHENTICATION_ERROR_DIALOG:

			authenticationErrorDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialogTheme));
			authenticationErrorDialogBuilder.setTitle(R.string.authentication_error);
			View authenticationErrorDialogView = LayoutInflater.from(this).inflate(R.layout.authentication_error_dialog, (ViewGroup) findViewById(R.id.authentication_error_dialog_layout));
			authenticationErrorDialogBuilder.setView(authenticationErrorDialogView);
			authenticationErrorDialogBuilder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dismissDialog(AUTHENTICATION_ERROR_DIALOG);

				}
			});	
			dialog = (Dialog)authenticationErrorDialogBuilder.show();
			break;
		}

		return dialog;
	}

	private OnClickListener loginListener = new OnClickListener(){
		public void onClick(View v){
			
			
			login();
		}
	};
	
	private TextView.OnEditorActionListener editLoginListener = new TextView.OnEditorActionListener(){

		public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
			login();
			return false;
		}
		
	};
	
	private void login(){
		
		
		loadingDialog = new ProgressDialog(LoginActivity.this);
		loadingDialog.getContext().setTheme(R.style.dialogTheme);
		loadingDialog.setMessage(getString(R.string.please_wait));
		loadingDialog.setTitle(R.string.logging_in);
		loadingDialog.show();
		//ProgressDialog.show(LoginActivity.this, "Loging in", "Please Wait...").getContext().setTheme(R.style.dialogTheme);
		EditText editUsername = (EditText)findViewById(R.id.username);
		EditText editPassword = (EditText)findViewById(R.id.password);

		username = editUsername.getText().toString();
		password = editPassword.getText().toString();
		connection = new DelregningConnection(username, password);
		try {
			bills = connection.getBills();
			Intent billsIntent = new Intent(LoginActivity.this, BillsActivity.class);
			billsIntent.putExtra("username", username);
			billsIntent.putExtra("password", password);
			billsIntent.putExtra("bills", bills.toString());
			loadingDialog.dismiss();
			startActivity(billsIntent);
		} catch (AuthenticationException e) {
			loadingDialog.dismiss();
			showDialog(AUTHENTICATION_ERROR_DIALOG);
		}
	}
}