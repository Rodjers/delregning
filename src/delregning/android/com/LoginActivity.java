package delregning.android.com;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;


public class LoginActivity extends Activity {
	
	private Button login_button;
	private Button regninger_button;
	private Button slettRegningButton;
	private DelregningConnection connection;
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


    		setContentView(R.layout.main);
            regninger_button = (Button)findViewById(R.id.regninger_button);
            regninger_button.setOnClickListener(regningerListener);
            slettRegningButton = (Button)findViewById(R.id.slett_regning_button);
            slettRegningButton.setOnClickListener(slettRegningerListener);
    		
    	}
    };
  
    private OnClickListener regningerListener = new OnClickListener(){
    	public void onClick(View v){
    		    		
    		Intent regningerIntent = new Intent(LoginActivity.this, BillsActivity.class);
    		regningerIntent.putExtra("username", username);
    		regningerIntent.putExtra("password", password);
    		startActivity(regningerIntent);
    		
    	}
    };
    private OnClickListener slettRegningerListener = new OnClickListener(){
    	public void onClick(View v){
    		
    		connection = new DelregningConnection(username, password);
    		
			connection.deleteBill("9nZ");
    		
    		
    		setContentView(R.layout.main);
            regninger_button = (Button)findViewById(R.id.regninger_button);
            regninger_button.setOnClickListener(regningerListener);
            slettRegningButton = (Button)findViewById(R.id.slett_regning_button);
            slettRegningButton.setOnClickListener(slettRegningerListener);
    		
    	}
    };
    
}