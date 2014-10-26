package at.oeldin.motes;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.*;
import android.os.Build;
import at.oeldin.motes.MotesObject.*;



public class MotesWrapper extends Activity{
	    private Boolean repeat;
	    private String adress;
	    private SharedPreferences settings;
	    private SharedPreferences.Editor settingsEditor;
	    private String DEFAULT_YEAR = "14";
	
	    public MotesWrapper()
	    {
	    	disableConnectionReuseIfNecessary();
	        repeat = true;
	        adress = "http://motes.at/api/";
	        settings = getPreferences(0);
	        settingsEditor = settings.edit();
	    }
	
	    public MotesObject GetStudents()
	    {
	        String req = "?action=getstudents";
	        return submitRequest(req);
	    }
	    public MotesObject GetStudents(int Student)
	    {
	        String req = "?action=getstudents&student="+Student;
	        return submitRequest(req);
	    }
	
	    public MotesObject GetAllStudents()
	    {
	        String req = "?action=getallstudents";
	        return submitRequest(req);
	    }
	
	    public MotesObject GetSubjects()
	    {
	        String req = "?action=getsubjects";
	        return submitRequest(req);
	    }
	
	    public MotesObject GetTeachers()
	    {
	        String req = "?action=getteachers";
	        return submitRequest(req);
	    }
	
	    public MotesObject GetNotes(int Teacher, int Student, int Subject)
	    {
	        String req = "?action=getnotes&year=" + settings.getString("myear", DEFAULT_YEAR);
	
	        if (Teacher != 0) req += "&teacher=" + Teacher;
	        if (Student != 0) req += "&student=" + Student;
	        if (Subject != 0) req += "&subject=" + Subject;
	
	        //repeat = false;
	
	        return submitRequest(req);
	    }
	
	    public MotesObject GetActivities(int Category, int Student)
	    {
	        String req = String.format("?action=getactivities&category=%d&student=%d&year=%s", Category, Student, settings.getString("year", DEFAULT_YEAR));
	        return submitRequest(req);
	    }
	
	    public Boolean SetActivity(int Category, int Student, String Text)
	    {
	        String req = String.format("?action=setactivity&category=%d&student=%d&year=%s&text=%s", Category, Student, settings.getString("year", DEFAULT_YEAR), Text);
	        return submitModRequest(req);
	    }
	
	    public Boolean SetNote(int Subject, int Student, String Text)
	    {
	        String req = String.format("?action=setnote&student=%d&subject=%d&year=&s&text=%s", Student, Subject, settings.getString("year", DEFAULT_YEAR), Text);
	        return submitModRequest(req);
	    }
	
	    public Boolean DeleteNote(int Note)
	    {
	        String req = String.format("?action=deletenote&year=%s&note=%d", settings.getString("year", DEFAULT_YEAR), Note);
	        return submitModRequest(req);
	    }
	
	    public Boolean DeleteActivity(int Activity)
	    {
	        String req = String.format("?action=deleteactivity&year=%s&activity=%d", settings.getString("year", DEFAULT_YEAR), Activity);
	        return submitModRequest(req);
	    }
	
	    public Boolean Login()
	    {
	        
	        try
	        {
	        	String req = "?action=login";
	        	
	        	URL myUrl = new URL(adress + req);
	        	HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
	        	
	        	connection.addRequestProperty("Cache-Control", "no-cache");
	        	connection.setRequestMethod("POST");
	        	connection.setDoOutput(true);
	        	
	        	DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	        	wr.writeBytes("user=" + settings.getString("name", "") + "pw=" + settings.getString("pass", ""));
	        	
	        	InputStream in = new BufferedInputStream(connection.getInputStream());
	            String IDres = readInputStream(in);
	
	            if (IDres != "Invalid Login!")
	            {
	            	settingsEditor.putString("key", IDres);
	                return true;
	            } 
	            else return false;
	            
	        }
	        catch (Exception e)
	        {
	            return false;
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
	    
	    private MotesObject submitRequest(String request)
	    {
	        try
	            {
	                String appAuth = "&shortuser=" + settings.getString("name", "") + "&key=" + settings.getString("key", "");

	                URL myUrl = new URL(adress+request+appAuth);
		        	HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
		        	connection.addRequestProperty("Cache-Control", "no-cache");

		        	InputStream in = new BufferedInputStream(connection.getInputStream());
		        	JSONObject jObject = new JSONObject(readInputStream(in));

	                repeat = true;
	                return deserializeToMotes(jObject);
	            }
	            catch (Exception e)
	            {
	                if (repeat)
	                {
	                    if (Login())
	                    {
	                        repeat = false;
	                        return submitRequest(request);
	                    }
	                    else return null;
	                }
	                else
	                {
	                    repeat = true;
	                    return null;
	                }
	
	                
	            }
	    }
	
	    private MotesObject deserializeToMotes(JSONObject jObject) {
			MotesObject mobject = new MotesObject();
			
			//try every fucking array
			try{
				//should throw exception if it wasn't the specified request
				JSONArray tempArray = jObject.getJSONArray("students");
				if(tempArray != null){
					List<StudentObject> tempStudents = new ArrayList<StudentObject>();
					
					for(int i = 0; i<tempArray.length();i++){
						JSONObject tempObject = tempArray.getJSONObject(i);
						StudentObject tempSObject = new StudentObject();
						
						tempSObject.id = tempObject.getInt("id");
						tempSObject.name = tempObject.getString("name");
						
						tempStudents.add(tempSObject);
					}
					
					mobject.students = tempStudents;
					return mobject;
				}
			}
			catch(Exception e){}
			
			try{
				//should throw exception if it wasn't the specified request
				JSONArray tempArray = jObject.getJSONArray("subjects");
				if(tempArray != null){
					List<StudentObject> tempStudents = new ArrayList<StudentObject>();
					
					for(int i = 0; i<tempArray.length();i++){
						JSONObject tempObject = tempArray.getJSONObject(i);
						StudentObject tempSObject = new StudentObject();
						
						tempSObject.id = tempObject.getInt("id");
						tempSObject.name = tempObject.getString("name");
						
						tempStudents.add(tempSObject);
					}
					
					mobject.students = tempStudents;
					return mobject;
				}
			}
			catch(Exception e){}
			
			subjects;
	        teachers;
	        activities;
	        notes
			
		}

		Boolean submitModRequest(String request)
	    {
	        try
	            {
	                String appAuth = "&shortuser=" + settings.getString("mname", "") + "&key=" + settings.getString("key", "");
	
	                URL myUrl = new URL(adress+request+appAuth);
		        	HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
		        	connection.addRequestProperty("Cache-Control", "no-cache");

		        	InputStream in = new BufferedInputStream(connection.getInputStream());
	                if(readInputStream(in) != "false")
	                {
	                    repeat = true;
	                    return true;
	                }
	                else throw new Exception("Wrong");
	            }
	            catch (Exception e)
	            {
	                if (repeat)
	                {
	                    if (Login())
	                    {
	                        repeat = !repeat;
	
	                        return submitModRequest(request);
	                    }
	                    else return false;
	                }
	                else
	                {
	                    repeat = !repeat;
	                    return false;
	                }
	
	                
	            }
	    }

	private void disableConnectionReuseIfNecessary() {
	   // Work around pre-Froyo bugs in HTTP connection reuse.
	   if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
	     System.setProperty("http.keepAlive", "false");
	   
	 }}

	
	
}


