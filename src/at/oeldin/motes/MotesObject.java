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

        public List<UniversalObject> stuff;
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
        
        public class StuffAdapter extends ArrayAdapter<UniversalObject> {
            private Activity activity;
            private List<UniversalObject> lStuff;
            private LayoutInflater inflater = null;

            public StuffAdapter (Activity activity, int textViewResourceId, List<UniversalObject> stuff) {
                super(activity, textViewResourceId, stuff);
                
                try {
                    this.activity = activity;
                    this.lStuff = stuff;

                    inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                } catch (Exception e) {}
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = convertView;
        
                try {
                        
                    if(rowView.equals(null)) rowView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        
                    TextView display_name = (TextView) rowView.findViewById(android.R.id.text1);
                    display_name.setText(lStuff.get(position).name);
                    rowView.setTag(lStuff.get(position).id);
        
                } catch (Exception e) {}
        
                
                return rowView;
            }
        }
        
        public class NoteAdapter extends ArrayAdapter<NoteObject> {
            private Activity activity;
            private List<NoteObject> lNote;
            private LayoutInflater inflater = null;

            public NoteAdapter (Activity activity, int textViewResourceId, List<NoteObject> notes) {
                super(activity, textViewResourceId, notes);
                
                try {
                    this.activity = activity;
                    this.lNote = notes;

                    inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                } catch (Exception e) {}
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = convertView;
        
                try {
                        
                    if(rowView.equals(null)) rowView = inflater.inflate(R.layout.listitem_note, null);
        
                    TextView display_text = (TextView) rowView.findViewById(R.id.listitem_note_text);
                    TextView display_teacher = (TextView) rowView.findViewById(R.id.listitem_note_teacher);
                    TextView display_date = (TextView) rowView.findViewById(R.id.listitem_note_date);
                    
                    display_name.setText(lNote.get(position).name);
                    display_teacher.setText(lNote.get(position).teacher);
                    display_date.setText(lNote.get(position).created);
                    
                    rowView.setTag(lNote.get(position).id);
        
                } catch (Exception e) {}
        
                
                return rowView;
            }
        }
 
}
