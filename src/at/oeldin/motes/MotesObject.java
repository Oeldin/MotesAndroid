package at.oeldin.motes;

import java.util.List;

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
 
}
