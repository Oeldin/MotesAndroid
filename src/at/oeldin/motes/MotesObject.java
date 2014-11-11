package at.oeldin.motes;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MotesObject {

        public List<StudentObject> students;
        public List<SubjectObject> subjects;
        public List<TeacherObject> teachers;
        public List<ActivityObject> activities;
        public List<NoteObject> notes;
        
        public class StudentObject {
            public int id;
            public String name;
        }
        
        public class SubjectObject {
        	public int id;
            public String name;
        }
        
        public class TeacherObject {
            public int id;
            public String name;
        }
        
        public class ActivityObject {
            public int id;
            public String text;
        }
        
        public class NoteObject {
            public int id;
            public String text;
            public String teacher;
            public String created;
        }
        
        public class StudentAdapter extends ArrayAdapter<StudentObject> {
            private Activity activity;
            private List<StudentObject> lStudent;
            private LayoutInflater inflater = null;

            public StudentAdapter (Activity activity, int textViewResourceId, List<StudentObject> students) {
                super(activity, textViewResourceId, students);
                
                try {
                    this.activity = activity;
                    this.lStudent = students;

                    inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                } catch (Exception e) {

                }
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View vi = convertView;
                try {

                    vi = inflater.inflate(R.layout.listitem_student, null);

                    TextView display_name = (TextView) vi.findViewById(R.id.listitem_student_name);
                    display_name.setText(lStudent.get(position).name);
                    vi.setTag(lStudent.get(position).id);

                } catch (Exception e) {}
                return vi;
            }
        }
 
}
