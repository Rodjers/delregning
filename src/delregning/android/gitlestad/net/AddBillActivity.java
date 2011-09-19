package delregning.android.gitlestad.net;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddBillActivity extends Activity {
	
	private Button addBillButton;
	private String username;
	private String password;
	private String title;
	private String description;
	private DelregningConnection connection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bill);

		Bundle bundle = getIntent().getExtras();
		username = bundle.getString("username");
		password = bundle.getString("password");

        addBillButton = (Button)findViewById(R.id.add_bill_button);
        addBillButton.setOnClickListener(addBillListener);
        
    }
    
    private OnClickListener addBillListener = new OnClickListener(){
    	public void onClick(View v){
    		
    		EditText editTitle = (EditText)findViewById(R.id.title);
    		EditText editDescription = (EditText)findViewById(R.id.description);
    		
    		title = editTitle.getText().toString();
    		description = editDescription.getText().toString();
    		
    		connection = new DelregningConnection(username, password);
    		
    		connection.addBill(title, description);

    		Intent billsIntent = new Intent(AddBillActivity.this, BillsActivity.class);
    		billsIntent.putExtra("username", username);
    		billsIntent.putExtra("password", password);
    		startActivity(billsIntent);

    		
    	}
    };
	
}