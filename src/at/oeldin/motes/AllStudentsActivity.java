package at.oeldin.motes;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Build;
import at.oeldin.motes.MotesObject.StudentAdapter;
import at.oeldin.motes.MotesObject.StudentObject;
import at.oeldin.motes.MotesWrapper.MotesCallbackInterface;

public class AllStudentsActivity extends ActionBarActivity implements MotesCallbackInterface {

	private SharedPreferences settings;
	private SharedPreferences.Editor settingsEditor;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_students);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new StudentListFragment()).commit();
			
			settings = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all_students, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent intent;
		switch(item.getItemId()){
	    	case R.id.menu_logout:
	    		logout();
	    		return true;
	    		
        	case R.id.menu_notes:
        		intent = new Intent(getApplicationContext(), NotesActivity.class);
    	    	startActivity(intent);
    	    	return true;
    	    	
        	case R.id.menu_students:
    	    	return true;
    	    	
	        default:
	    		return super.onOptionsItemSelected(item);
        
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class StudentListFragment extends ListFragment {
		
		MotesWrapper mWrapper;

		public StudentListFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_all_students,
					container, false);
			return rootView;
		}
		
		 @Override
		    public void onActivityCreated(Bundle savedInstanceState) {
		        super.onActivityCreated(savedInstanceState);

				mWrapper = new MotesWrapper(getActivity());
				mWrapper.GetStudents();

		    }


		    @Override
		    public void onListItemClick(ListView l, View v, int position, long id) {
		        int studentid =  Integer.parseInt(v.getTag().toString());
		        
		    	Intent intent = new Intent(getActivity().getApplicationContext(), StudentA.class);
		    	intent.putExtra("id", studentid);
		    	startActivity(intent);
		        
		    }

			
			
		public void setMyListAdapter(MotesObject result){
			
			StudentAdapter myAdapter = result.new StudentAdapter(getActivity(), 0, result.students);
		       	setListAdapter(myAdapter);
		}
	}
	
	@Override
	public void onLoginFinished(Boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModRequestFinished(Boolean success) {
		// TODO Auto-generated method stub
				
	}

	@Override
	public void onRequestFinished(MotesObject result) {
		
		StudentListFragment fragment = (StudentListFragment) getFragmentManager().findFragmentById(R.id.fragment);		
				
		fragment.setMyListAdapter(result);		
	}

	@Override
	public void onRequestStatusUpdate(int progress) {

	}
	
	private void logout() {
		
		settingsEditor = settings.edit();
		settingsEditor.remove("key");
		settingsEditor.commit();
		
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent);
		
	}
}
