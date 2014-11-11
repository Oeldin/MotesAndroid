package at.oeldin.motes;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity implements OnItemSelectedListener{
	
		private SharedPreferences settings;
    	private SharedPreferences.Editor settingsEditor;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        if (savedInstanceState == null) {
			//settings = PreferenceManager.getDefaultSharedPreferences(this);
			settings = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
			
			
			Spinner spinner = (Spinner) findViewById(R.id.year_spinner);
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.spinner_array, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
	        }
	    }


	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        switch(item.getItemId()){
	        	case R.id.menu_logout:
	        		logout();
	        		return true;
		        default:
	        		return super.onOptionsItemSelected(item);
		        
	        }
	    }
	    
	    @Override
	    public void onBackPressed() {
	    	
	    	//call login activity which should exit because of the extra
	    	Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	intent.putExtra("Exit", true);
	    	startActivity(intent);

	    }

	    public void onItemSelected(AdapterView<?> parent, View view, 
	        int pos, long id) {
	        	
		    settingsEditor = settings.edit();
		    
		    switch(pos){
		    	case 0:
		    		settingsEditor.putString("year", "14");
		    		break;
		    	case 1:
		    		settingsEditor.putString("year", "15");
		    		break;
		    	default:
		    		break;
		    }
		    
		    settingsEditor.commit();
	    }
	
	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }
	
	private void logout() {
		
		settingsEditor = settings.edit();
		settingsEditor.remove("key");
		settingsEditor.commit();
		
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
	    	startActivity(intent);
		
	}
}
