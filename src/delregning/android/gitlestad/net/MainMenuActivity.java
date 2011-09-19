package delregning.android.gitlestad.net;

import delregning.android.gitlestad.net.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	
	private String username;
	private String password;
	private Button billsButton;
	private Button addBillButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
		Bundle bundle = getIntent().getExtras();
		
		username = bundle.getString("username");
		password = bundle.getString("password");
		setContentView(R.layout.main);
		billsButton = (Button)findViewById(R.id.bills_button);
		billsButton.setOnClickListener(billsListener);
		addBillButton = (Button)findViewById(R.id.add_bill_button);
		addBillButton.setOnClickListener(addBillListener);
	
	}
	
    private OnClickListener billsListener = new OnClickListener(){
    	public void onClick(View v){
    		    		
    		Intent billsIntent = new Intent(MainMenuActivity.this, BillsActivity.class);
    		billsIntent.putExtra("username", username);
    		billsIntent.putExtra("password", password);
    		startActivity(billsIntent);
    		
    	}
    };
    
    private OnClickListener addBillListener = new OnClickListener(){
    	public void onClick(View v){
    		    		
    		Intent addBillIntent = new Intent(MainMenuActivity.this, AddBillActivity.class);
    		addBillIntent.putExtra("username", username);
    		addBillIntent.putExtra("password", password);
    		startActivity(addBillIntent);
    		
    	}
    };
	
}