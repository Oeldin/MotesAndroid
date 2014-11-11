package at.oeldin.motes;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity{
	
	private SharedPreferences settings;
    	private SharedPreferences.Editor settingsEditor;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        if (savedInstanceState == null) {
			//settings = PreferenceManager.getDefaultSharedPreferences(this);
			settings = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
	        }
	    }


	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.std_m, menu);
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


	private void logout() {
		
		settingsEditor = settings.edit();
		settingsEditor.remove("key");
		settingsEditor.commit();
		
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
	    	startActivity(intent);
		
	}
}
