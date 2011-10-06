package delregning.android.gitlestad.net;




import delregning.android.gitlestad.net.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;


public class LoginActivity extends Activity {

	private Button login_button;
	private String username;
	private String password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		login_button = (Button)findViewById(R.id.login_button);
		login_button.setOnClickListener(loginListener);

	}

	private OnClickListener loginListener = new OnClickListener(){
		public void onClick(View v){

			EditText editUsername = (EditText)findViewById(R.id.username);
			EditText editPassword = (EditText)findViewById(R.id.password);

			username = editUsername.getText().toString();
			password = editPassword.getText().toString();

			Intent billsIntent = new Intent(LoginActivity.this, BillsActivity.class);
			billsIntent.putExtra("username", username);
			billsIntent.putExtra("password", password);
			startActivity(billsIntent);


		}
	};




}