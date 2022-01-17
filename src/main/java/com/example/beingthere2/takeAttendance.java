package com.example.beingthere2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;


public class takeAttendance extends AppCompatActivity {

    String Lecture;
    String class_selected;
    String teacher_id;

    ArrayList<String> selectedItems;
    ArrayList<String> nonselectedItems;
    EditText rollnoAbsent;
    String[] parts;
    String[] year;

    final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

    ArrayList Userlist = new ArrayList<>();
    ArrayList Usernames = new ArrayList<>();
    ArrayList absent =new ArrayList();

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbAttendance;
    String date,batch;

    Button submitbutton;
    ImageButton voice;
    TextView classname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        rollnoAbsent = findViewById(R.id.rollno);
        submitbutton=findViewById(R.id.btshow);
        submitbutton.setEnabled(false);
        voice=findViewById(R.id.voicebutton);
        View view=findViewById(R.id.voicebutton);
        Snackbar.make(view, "Enter the Roll numbers of absentees separated by commas", Snackbar.LENGTH_LONG).show();
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null){
                    if(!rollnoAbsent.getText().toString().equals(""))
                            rollnoAbsent.setText(rollnoAbsent.getText()+","+matches.get(0).replaceAll("[^0-9]", "") );
                    else
                        rollnoAbsent.setText(matches.get(0).replaceAll("[^0-9]", ""));
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        // ArrayList Userlist;
        selectedItems = new ArrayList<String>();

        classname = findViewById(R.id.textView);

        //to get class name from teacherActivity
        Bundle bundle1 = getIntent().getExtras();
        teacher_id=bundle1.getString("tid");
        Lecture=bundle1.getString("subject");
        class_selected=bundle1.getString("year");
        date=bundle1.getString("date");

        classname.setText(class_selected+" "+Lecture+" "+date);

        if(Lecture.equals("MADL") || Lecture.equals("CNL") || Lecture.equals("CGL")){
            //Toast.makeText(this, Lecture, Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Enter the Batch");
            final LayoutInflater inflater = this.getLayoutInflater();
            View add_menu_layout = inflater.inflate(R.layout.selectbatch, null);
            final EditText batchSelect=(EditText)add_menu_layout.findViewById(R.id.batchSslect);
            alertDialog.setView(add_menu_layout);

            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    batch=batchSelect.getText().toString();
                }
            });
            alertDialog.show();
        }
        voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        rollnoAbsent.setHint("You will see input here");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        //rollnoAbsent.setText("");
                        rollnoAbsent.setHint("Listening...");
                        break;
                }
                return false;
            }
        });
    }

    public void nextFunc(final View view) {
        AlphaAnimation buttonClick = new AlphaAnimation(2F, 0.9F);
        view.startAnimation(buttonClick);

        String line = rollnoAbsent.getText().toString();
        parts = line.split(",");
        absent = new ArrayList<>(Arrays.asList(parts));
        submitbutton.setEnabled(true);

        DatabaseReference dbuser = ref.child("Student");
        Userlist.clear();
        if(Lecture.equals("MADL") || Lecture.equals("CNL") || Lecture.equals("CGL")){
            dbuser.orderByChild("sbatch").equalTo(batch).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Userlist.add(dsp.child("rollno").getValue().toString());
                            Usernames.add(dsp.child("sname").getValue().toString());
                        }
                        Userlist.removeAll(absent);
                        OnStart(Userlist,absent);
                    }catch (Exception e){
                        Toast.makeText(takeAttendance.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            dbuser.orderByChild("classname").equalTo(class_selected).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            Userlist.add(dsp.child("rollno").getValue().toString());
                            Usernames.add(dsp.child("sname").getValue().toString());
                        }
                        Collections.sort(Userlist);
                        Userlist.removeAll(absent);
                        OnStart(Userlist,absent);
                    } catch (Exception e) {
                        Toast.makeText(takeAttendance.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void OnStart(ArrayList<String> userlist,ArrayList<String> absent) {

        Collections.sort(absent);
        nonselectedItems = userlist;
        ListView chl = findViewById(R.id.checkable_list);
        chl.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.checkable_list_layout, R.id.txt_title, absent);
        chl.setAdapter(aa);
        chl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItems.contains(selectedItem))
                    selectedItems.remove(selectedItem);
                else
                    selectedItems.add(selectedItem);
            }
        });
    }


    public void showSelectedItems(View view) {
        AlphaAnimation buttonClick = new AlphaAnimation(2F, 0.9F);
        view.startAnimation(buttonClick);

        String selItems = "";

        ref = FirebaseDatabase.getInstance().getReference();
        dbAttendance = ref.child("attendance").child(class_selected);

        for (String item : selectedItems) {
            Toast.makeText(this, "Attendance created Successfully", Toast.LENGTH_SHORT).show();
            nonselectedItems.remove(item);
            dbAttendance.child(date).child(item).child(Lecture).setValue("A"+"/"+teacher_id);
            if (selItems == "")
                selItems = item;
            else
                selItems += "/" + item;
        }
        //for making present
        for (String item : nonselectedItems) {
            Toast.makeText(this, "Attendance created Successfully", Toast.LENGTH_SHORT).show();
            dbAttendance.child(date).child(item).child(Lecture).setValue("P"+"/"+teacher_id);
        }
    }
}

