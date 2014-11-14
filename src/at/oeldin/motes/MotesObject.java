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

        public List<UniversalObject> students;
        public List<UniversalObject> subjects;
        public List<UniversalObject> teachers;
        public List<UniversalObject> activities;
        public List<NoteObject> notes;
        
        public class UniversalObject {
            public int id;
            public String name;
            
            UniversalObject(int id, String name){
                    this.id = id;
                    this.name = name;
            }
        }
        
        public class NoteObject {
            public int id;
            public String text;
            public String teacher;
            public String created;
            
            NoteObject(int id, String text, String teacher, String created){
                this.id = id;
                this.text = text;
                this.teacher = teacher;
                this.created = created;
            }
            
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

                } catch (Exception e) {}
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = convertView;
        
                try {
                        
                    if(rowView.equals(null)) rowView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        
                    TextView display_name = (TextView) rowView.findViewById(android.R.id.text1);
                    display_name.setText(lStudent.get(position).name);
                    rowView.setTag(lStudent.get(position).id);
        
                } catch (Exception e) {}
        
                
                return rowView;
            }
        }
 
}
