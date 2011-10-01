package delregning.android.gitlestad.net;

import delregning.android.gitlestad.net.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	
	private String username;
	private String password;
	private Button billsButton;
	private Button addBillButton;
	static final int ADD_BILL_DIALOG = 1;
	private AlertDialog.Builder addBillDialogBuilder;
	
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
		
		createDialog();

	}
	
	private void createDialog(){
		addBillDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.dialogTheme));
		addBillDialogBuilder.setTitle(R.string.new_bill);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.add_bill_dialog, (ViewGroup) findViewById(R.id.add_bill_dialog_id));
		addBillDialogBuilder.setInverseBackgroundForced(false);
		addBillDialogBuilder.setView(dialogView);
		addBillDialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//TODO Create add bill code
				createDialog();
			  }
			});
		addBillDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    createDialog();
			  }
			});	
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
    		
    		addBillDialogBuilder.show();
    		    		
    		/*Intent addBillIntent = new Intent(MainMenuActivity.this, AddBillActivity.class);
    		addBillIntent.putExtra("username", username);
    		addBillIntent.putExtra("password", password);
    		startActivity(addBillIntent);*/
    		
    	}
    };
	
}