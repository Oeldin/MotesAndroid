package at.oeldin.motes;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.*;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import at.oeldin.motes.MotesObject.*;



public class MotesWrapper {
	    //private Boolean repeat;
	    private String adress;
	    private SharedPreferences settings;
	    private SharedPreferences.Editor settingsEditor;
	    private String DEFAULT_YEAR = "14";
	    private Context context;
	
	    public MotesWrapper(Context context)
	    {
	    	this.context = context;
	    	disableConnectionReuseIfNecessary();
	        adress = "http://motes.at/api/";
	        //settings = PreferenceManager.getDefaultSharedPreferences(context);
	        settings = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE);
	        
	    }
	
	    public void GetStudents()
	    {
	        String req = "?action=getstudents";
	        new ConnectionTask().execute(req);
	    }
	    public void GetStudents(int Student)
	    {
	        String req = "?action=getstudents&student="+Student;
	        new ConnectionTask().execute(req);
	    }
	
	    public void GetAllStudents()
	    {
	        String req = "?action=getallstudents";
	        new ConnectionTask().execute(req);
	    }
	
	    public void GetSubjects()
	    {
	        String req = "?action=getsubjects";
	        new ConnectionTask().execute(req);
	    }
	
	    public void GetTeachers()
	    {
	        String req = "?action=getteachers";
	        new ConnectionTask().execute(req);
	    }
	
	    public void GetNotes(int Teacher, int Student, int Subject)
	    {
	        String req = "?action=getnotes&year=" + settings.getString("year", DEFAULT_YEAR);
	
	        if (Teacher != 0) req += "&teacher=" + Teacher;
	        if (Student != 0) req += "&student=" + Student;
	        if (Subject != 0) req += "&subject=" + Subject;
	
	        new ConnectionTask().execute(req);
	    }
	
	    public void GetActivities(int Category, int Student)
	    {
	        String req = String.format(Locale.getDefault(), "?action=getactivities&category=%d&student=%d&year=%s", Category, Student, settings.getString("year", DEFAULT_YEAR));
	        new ConnectionTask().execute(req);
	    }
	
	    public void SetActivity(int Category, int Student, String Text)
	    {
	    	try {
				Text = URLEncoder.encode(Text, "UTF-8");
			} catch (UnsupportedEncodingException e) {}
	    	
	        String req = String.format(Locale.getDefault(), "?action=setactivity&category=%d&student=%d&year=%s&text=%s", Category, Student, settings.getString("year", DEFAULT_YEAR), Text);
	        new ModConnectionTask().execute(req);
	    }
	
	    public void SetNote(int Subject, int Student, String Text)
	    {
	    	try {
				Text = URLEncoder.encode(Text, "UTF-8");
			} catch (UnsupportedEncodingException e) {}
			
	        String req = String.format(Locale.getDefault(), "?action=setnote&student=%d&subject=%d&year=%s&text=%s", Student, Subject, settings.getString("year", DEFAULT_YEAR), Text);
	        new ModConnectionTask().execute(req);
	    }
	
	    public void DeleteNote(int Note)
	    {
	        String req = String.format(Locale.getDefault(), "?action=deletenote&year=%s&note=%d", settings.getString("year", DEFAULT_YEAR), Note);
	        new ModConnectionTask().execute(req);
	    }
	
	    public void DeleteActivity(int Activity)
	    {
	        String req = String.format(Locale.getDefault(), "?action=deleteactivity&year=%s&activity=%d", settings.getString("year", DEFAULT_YEAR), Activity);
	        new ModConnectionTask().execute(req);
	    }
	
	    public void Login()
	    {
	        new LoginTask().execute();
	    }
	    
	    public interface MotesCallbackInterface{
	    	
	    	void onLoginFinished(Boolean success);
	    	void onModRequestFinished(Boolean success);
	    	void onRequestFinished(MotesObject result);
	    	void onRequestStatusUpdate(int progress);
	    	
	    }
	    
	    private class LoginTask extends AsyncTask<Void, Void, Boolean>{
	    	
	    	public MotesCallbackInterface del = (MotesCallbackInterface) context;
	    	
		    @Override
		    protected Boolean doInBackground(Void... params) {
		    	try{
		    		
		    		String req = "?action=login";
		        	
		        	URL myUrl = new URL(adress + req);
		        	HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
		        	
		        	connection.addRequestProperty("Cache-Control", "no-cache");
		        	connection.setRequestMethod("POST");
		        	connection.setDoOutput(true);
		        	
		        	DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		        	wr.writeBytes("user=" + settings.getString("name", "") + "&pw=" + settings.getString("pass", ""));
		        	
		        	InputStream in = new BufferedInputStream(connection.getInputStream());
		            String IDres = readInputStream(in);
		            
		            if (!IDres.equals("Invalid Login!"))
		            {
		            	settingsEditor = settings.edit();
		            	settingsEditor.putString("key", IDres);
		            	settingsEditor.commit();
		                return true;
		            } 
		            else return false;
		            
		    	}
		    	catch(Exception e){
		    		return false;
		    	}

		    }

		    @Override
		    protected void onPostExecute(Boolean result) {
		    	del.onLoginFinished(result);
		    }

		}
	    
	    public String readInputStream(InputStream inputStream)
	    {
	        try {
				return new String(readFully(inputStream), "UTF-8");
			} catch (Exception e) {
				return "Invalid Login!";
			}
	    }    

	    private byte[] readFully(InputStream inputStream) throws IOException
	    {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        byte[] buffer = new byte[1024];
	        int length = 0;
	        while ((length = inputStream.read(buffer)) != -1) {
	            baos.write(buffer, 0, length);
	        }
	        return baos.toByteArray();
	    }

	    private MotesObject deserializeToMotes(JSONObject jObject) {
			MotesObject mobject = new MotesObject();
			
			
			try{
				String mappedKey = jObject.names().getString(0);
				
				JSONArray tempArray = jObject.getJSONArray(mappedKey);
				
				if(tempArray != null && !mappedKey.equals("notes")){
					List<UniversalObject> tempList = new ArrayList<UniversalObject>();
					
					for(int i = 0; i<tempArray.length();i++){
						JSONObject tempObject = tempArray.getJSONObject(i);
						String nameName;
						
						if(mappedKey.equals("activities")) nameName = "text";
						else nameName = "name";

						tempList.add(
							mobject.new UniversalObject(
								tempObject.getInt("id"), 
								tempObject.getString(nameName)
								)
							);
					}
					
					mobject.stuff = tempList;
					return mobject;
				}
				else if(mappedKey.equals("notes")){
					List<NoteObject> tempList = new ArrayList<NoteObject>();
					
					for(int i = 0; i<tempArray.length();i++){
						JSONObject tempObject = tempArray.getJSONObject(i);

						tempList.add(
							mobject.new NoteObject(
								tempObject.getInt("id"), 
								tempObject.getString("text"),
								tempObject.getString("teacher"),
								tempObject.getString("created")
								)
							);
					}
					
					mobject.notes = tempList;
					return mobject;
				}
				else return null;
			}
			catch(Exception e){
				return null;
			}
			
		}

	private void disableConnectionReuseIfNecessary() {
	   // Work around pre-Froyo bugs in HTTP connection reuse.
	   if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
	     System.setProperty("http.keepAlive", "false");
	   
	 }}

	private class ModConnectionTask extends AsyncTask<String, Void, Boolean>{
	    
	    public MotesCallbackInterface del = (MotesCallbackInterface) context;
	    
	    @Override
	    protected Boolean doInBackground(String... requests) {
	    	try{
	    		String mrequest = requests[0];
	    		
		    	String appAuth = "&shortuser=" + settings.getString("name", "") + "&key=" + settings.getString("key", "");
	                URL myUrl = new URL(adress+mrequest+appAuth);
	                
	                Log.d("WETEEF", adress+mrequest+appAuth);
	            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
		    	connection.addRequestProperty("Cache-Control", "no-cache");
		
		    	InputStream in = new BufferedInputStream(connection.getInputStream());
		    	
		        if(readInputStream(in) != "false")
		        {
		            return true;
		        }
		        else throw new Exception("Wrong");
	    	}
	    	catch(Exception e){
	    		return false;
	    	}
		
	    }

	    @Override
	    protected void onPostExecute(Boolean result) {
	
		del.onModRequestFinished(result);
	    }

	}
	
	private class ConnectionTask extends AsyncTask<String, Integer, MotesObject>{
	    
	    public MotesCallbackInterface del = (MotesCallbackInterface) context;
	    
	    @Override
	    protected MotesObject doInBackground(String... requests) {
	    	try{
	    		String mrequest = requests[0];
		    	String appAuth = "&shortuser=" + settings.getString("name", "") + "&key=" + settings.getString("key", "");
		    		
	                URL myUrl = new URL(adress+mrequest+appAuth);
		        	HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
		        	connection.addRequestProperty("Cache-Control", "no-cache");
		        	
		        	publishProgress(1);
		        	InputStream in = new BufferedInputStream(connection.getInputStream());
		        	String jsonString = readInputStream(in);
		        	
		        	publishProgress(2);
		        	JSONObject jObject = new JSONObject(jsonString);

		        	MotesObject myObject = deserializeToMotes(jObject);
		        	
		        	publishProgress(3);
	                return myObject;
	    	}
	    	catch(Exception e){
	    		return null;
	    	}
		
	    }
	    
	    @Override
	    protected void onProgressUpdate(Integer... status){
	    	del.onRequestStatusUpdate(status[0]);
	    }

	    @Override
	    protected void onPostExecute(MotesObject result) {
	    	del.onRequestFinished(result);
	    }

	}
	
}


