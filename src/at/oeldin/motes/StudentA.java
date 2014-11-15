package at.oeldin.motes;

import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.oeldin.motes.MotesObject.StuffAdapter;
import at.oeldin.motes.MotesWrapper.MotesCallbackInterface;

public class StudentA extends ActionBarActivity implements MotesCallbackInterface {

	
	private SharedPreferences settings;
	private SharedPreferences.Editor settingsEditor;
	private int StudentID;
	private ProgressDialog pDia;
	private String newActivity;
	private StuffAdapter[] mAdapters;
	private int adapterStatus;
	private int stateBeforeReload;
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StudentID = getIntent().getIntExtra("id", 0);
		if(StudentID == 0) Toast.makeText(this, "Invalid Student", Toast.LENGTH_SHORT).show();
		
		setContentView(R.layout.activity_student);
		setupProgressDialog();
		settings = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
		newActivity = "";
		adapterStatus = 0;
		mAdapters = new StuffAdapter[6];
		initializeAllAdapters();
		stateBeforeReload = 0;
		
	}

	private void initializeAllAdapters() {
		
		MotesWrapper mWrapper = new MotesWrapper(this);
		
		mWrapper.GetActivities(adapterStatus, StudentID);
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.student, menu);
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
        		intent = new Intent(getApplicationContext(), AllStudentsActivity.class);
    	    	startActivity(intent);
    	    	return true;
    	    	
        	case R.id.menu_add:
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setTitle("New Entry");

        		// Set up the input
        		final EditText input = new EditText(this);
        		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        		input.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        		builder.setView(input);

        		// Set up the buttons
        		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() { 
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		    	
        		    	MotesWrapper myWrapper = new MotesWrapper(StudentA.this);
        		    	
        		        newActivity = input.getText().toString();
        		        myWrapper.SetActivity(mViewPager.getCurrentItem(), StudentID, newActivity);
        		        
        		        setupProgressDialog();
        				adapterStatus = 0;
        				stateBeforeReload = mViewPager.getCurrentItem();
        				initializeAllAdapters();
        		    }
        		});
        		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        dialog.cancel();
        		    }
        		});

        		builder.show();
        		return true;
        		
	        default:
        		return super.onOptionsItemSelected(item);
    
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		Context context;

		public SectionsPagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			this.context = context;

		}

		@Override
		public Fragment getItem(int position) {
			

			
			//Toast.makeText(context, "getItem called: " + position, Toast.LENGTH_SHORT).show();
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return StudentDetailFragment.newInstance(position, StudentID);
			
		}

		@Override
		public int getCount() {
			
			return 6;
		}


	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class StudentDetailFragment extends ListFragment {
		
		
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static StudentDetailFragment newInstance(int sectionNumber, int studentID) {
			StudentDetailFragment fragment = new StudentDetailFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public StudentDetailFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_student,
					container, false);
			
			Bundle bundle = getArguments(); 
    		int section = bundle.getInt(ARG_SECTION_NUMBER);
			
			TextView titleView = (TextView) rootView.findViewById(R.id.section_label);
			
			Locale l = Locale.getDefault();
			String title;
			switch (section) {
			case 0:
				title = getString(R.string.title_section1).toUpperCase(l);
				break;
			case 1:
				title = getString(R.string.title_section2).toUpperCase(l);
				break;
			case 2:
				title = getString(R.string.title_section3).toUpperCase(l);
				break;
			case 3:
				title = getString(R.string.title_section4).toUpperCase(l);
				break;
			case 4:
				title = getString(R.string.title_section5).toUpperCase(l);
				break;
			case 5:
				title = getString(R.string.title_section6).toUpperCase(l);
				break;
			default:
				title = "nope";
				break;
			}
			
			titleView.setText(title);
			
			
			return rootView;
			
		}
		
		@Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);

			
			Bundle bundle = getArguments(); 
    		int section = bundle.getInt(ARG_SECTION_NUMBER);
    		
    		try{
    			StuffAdapter tAdapter = ((StudentA) getActivity()).mAdapters[section];
    			setListAdapter(tAdapter);
    		}
    		catch(Exception e){}
    		
			
	    }

	}
	
	private void logout() {
		
		settingsEditor = settings.edit();
		settingsEditor.remove("key");
		settingsEditor.commit();
		
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent);
		
	}

	@Override
	public void onLoginFinished(Boolean success) {

		
	}

	@Override
	public void onModRequestFinished(Boolean success) {

		String message;
		if(success) message = "Saved";
		else message = "Sorry, something went wrong";
		
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRequestFinished(MotesObject result) {
		
		if(!(result == null)){
			StuffAdapter tempAdapter = result.new StuffAdapter(this, 0, result.stuff);
			mAdapters[adapterStatus] = tempAdapter;

		}
		
		if(adapterStatus < 5){
			adapterStatus++;
			initializeAllAdapters();
		}
		else{
			 pDia.dismiss();
			 
			 mSectionsPagerAdapter = new SectionsPagerAdapter(
						getSupportFragmentManager(), this);

			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);
			
			mViewPager.setCurrentItem(stateBeforeReload);
		}
		
	}

	@Override
	public void onRequestStatusUpdate(int progress) {
		pDia.setProgress(progress*33);
		
	}
	
	public void setupProgressDialog(){
		pDia = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
		pDia.setMessage("Loading Activities...");
		pDia.setMax(99);
		pDia.setProgress(0);
		pDia.show();
	}
	
}
