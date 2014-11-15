package at.oeldin.motes;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import at.oeldin.motes.MotesObject.NoteAdapter;
import at.oeldin.motes.MotesObject.StuffSpinnerAdapter;
import at.oeldin.motes.MotesWrapper.MotesCallbackInterface;

public class NotesActivity extends ActionBarActivity implements MotesCallbackInterface, OnItemSelectedListener {

	private SharedPreferences settings;
	private SharedPreferences.Editor settingsEditor;
	private ProgressDialog pDia;
	private String newNote;
	private int spinnerLoadingStatus;
	private int spinnerTeacher;
	private int spinnerStudent;
	private int spinnerSubject;
	Spinner teacherSpinner;
	Spinner studentSpinner;
	Spinner subjectSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new NotesFragment()).commit();
			
			settings = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
			newNote = "";
			spinnerLoadingStatus = 0;
			
		}
	}
	
	public void loadSpinners(){
		teacherSpinner = (Spinner) findViewById(R.id.spinner_teacher);
		studentSpinner = (Spinner) findViewById(R.id.spinner_student);
		subjectSpinner = (Spinner) findViewById(R.id.spinner_subject);
		
		teacherSpinner.setTag(0);
		teacherSpinner.setOnItemSelectedListener(this);
		
		studentSpinner.setTag(1);
		studentSpinner.setOnItemSelectedListener(this);
		
		subjectSpinner.setTag(2);
		subjectSpinner.setOnItemSelectedListener(this);

	}
	
	public void onItemSelected(AdapterView<?> parent, View view, 
	        int pos, long id) {
		
			int idSpinner = Integer.parseInt(view.getTag().toString());
			
			switch(Integer.parseInt(parent.getTag().toString())){
				
				case 0:
					spinnerTeacher = idSpinner;
					break;
					
				case 1:
					spinnerStudent = idSpinner;
					break;
					
				case 2:
					spinnerSubject = idSpinner;
					break;
			}
			
			if(spinnerLoadingStatus == 5){
				setupProgressDialog();
				reloadNotes();
			}
			
			
			//finish loading --There is so much which can go wrong
			else if(spinnerLoadingStatus == 4) spinnNext(null);
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notes, menu);
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

        	case R.id.menu_students:
        		intent = new Intent(getApplicationContext(), AllStudentsActivity.class);
    	    	startActivity(intent);
    	    	return true;
    	    	
        	case R.id.menu_addnote:
        		if(spinnerStudent != 0 && spinnerSubject != 0){
	        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        		builder.setTitle("New Note");
	
	        		// Set up the input
	        		final EditText input = new EditText(this);
	        		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
	        		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
	        		builder.setView(input);
	
	        		// Set up the buttons
	        		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() { 
	        		    @Override
	        		    public void onClick(DialogInterface dialog, int which) {
	        		    	
	        		    	MotesWrapper modWrapper = new MotesWrapper(NotesActivity.this);
	        		    	
	        		        newNote = input.getText().toString();
	        		        modWrapper.SetNote(spinnerSubject, spinnerStudent, newNote);
	        		    }
	        		});
	        		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        		    @Override
	        		    public void onClick(DialogInterface dialog, int which) {
	        		        dialog.cancel();
	        		    }
	        		});
	        		builder.show();
        		}
        		else Toast.makeText(this, "Please set the necessary filters before creating a new note", Toast.LENGTH_SHORT).show();
        		return true;
    	    	
	        default:
	    		return super.onOptionsItemSelected(item);
        
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class NotesFragment extends ListFragment {

		public NotesFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_notes,
					container, false);
			return rootView;
		}
		
		@Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);

			((NotesActivity) getActivity()).loadSpinners();
	        ((NotesActivity) getActivity()).reloadNotes();
			((NotesActivity) getActivity()).setupProgressDialog();
	    }
		
		public void setMyListAdapter(MotesObject result){
			
			NoteAdapter myAdapter = result.new NoteAdapter(getActivity(), 0, result.notes);
		       	setListAdapter(myAdapter);
		}
	}
	
	private void reloadNotes(){
		
		MotesWrapper myWrapper = new MotesWrapper(this);
		myWrapper.GetNotes(spinnerTeacher, spinnerStudent, spinnerSubject);
		
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
		
		if(spinnerLoadingStatus == 5){
			setupProgressDialog();
			reloadNotes();
		}
		
	}

	@Override
	public void onRequestFinished(MotesObject result) {
		if((spinnerLoadingStatus == 0)|(spinnerLoadingStatus == 5)){
			
			NotesFragment fragment = (NotesFragment) getSupportFragmentManager()
					.findFragmentById(R.id.container);
			
			if(result == null){
				Toast.makeText(this, "No notes", Toast.LENGTH_SHORT).show();
				
				fragment.getListView().setAdapter(null);
				
			}
			else{
				
				fragment.setMyListAdapter(result);
			}
			
			spinnNext(null);
		}
		else{

			result.stuff.add(0, result.new UniversalObject(0, "All"));
			StuffSpinnerAdapter myAdapter = result.new StuffSpinnerAdapter(this, 0, result.stuff);

	       	spinnNext(myAdapter);
		}
		
		
	}
	
	public void spinnNext(StuffSpinnerAdapter myAdapter){
		
		spinnerLoadingStatus++;
		
		MotesWrapper mWrapper = new MotesWrapper(this);
		
		switch(spinnerLoadingStatus){
		case 1:
			mWrapper.GetTeachers();
			break;
		
		case 2:
			teacherSpinner.setAdapter(myAdapter);
			mWrapper.GetStudents();
			break;
			
		case 3:
			studentSpinner.setAdapter(myAdapter);
			mWrapper.GetSubjects();
			break;
			
		case 4:
			subjectSpinner.setAdapter(myAdapter);
			break;
		
		default:
			spinnerLoadingStatus = 5;
			pDia.dismiss();
		}
		
	}
	
	public void setupProgressDialog(){
		pDia = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
		pDia.setMessage("Loading Notes...");
		pDia.setMax(99);
		pDia.setProgress(0);
		pDia.show();
	}

	@Override
	public void onRequestStatusUpdate(int progress) {
		pDia.setProgress(progress*33);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
}
