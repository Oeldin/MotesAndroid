package at.oeldin.motes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import at.oeldin.motes.MotesWrapper.MotesCallbackInterface;



public class LoginActivity extends ActionBarActivity implements MotesCallbackInterface {
	
    private SharedPreferences settings;
    private SharedPreferences.Editor settingsEditor;
    private MotesWrapper mrapper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getIntent().getBooleanExtra("Exit", false))
        {
            super.onBackPressed();
        }
        
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
        	mrapper = new MotesWrapper(this);
        	settings = this.getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        }
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	
    	if(settings.contains("key")){
    		mrapper.Login();
    	}
    }
    
    public void onLoginFinished(Boolean success){
    	
    	if(success){
        	Intent mainIntent = new Intent(this, MainActivity.class);
        	startActivity(mainIntent);
        }
        else{
        	Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginButtonClicked(View view){
    	
        settingsEditor = settings.edit();
        
    	EditText nameField = (EditText)findViewById(R.id.name_edit);
    	EditText passField = (EditText)findViewById(R.id.pass_edit);
    	
    	settingsEditor.putString("name", nameField.getText().toString());
    	settingsEditor.putString("pass", passField.getText().toString());
    	
    	if(settingsEditor.commit()){
    		mrapper.Login();
    	}
    	else{
        	Toast.makeText(this, "Failed to save password", Toast.LENGTH_SHORT).show();
        }
    	
    }

	@Override
	public void onModRequestFinished(Boolean success) {

		
	}

	@Override
	public void onRequestFinished(MotesObject result) {
		
	}

	@Override
	public void onRequestStatusUpdate(int progress) {

		
	}
}
