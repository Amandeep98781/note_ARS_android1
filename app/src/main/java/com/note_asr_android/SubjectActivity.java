package com.note_asr_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.note_asr_android.Adapter.SubjectAdapter;
import com.note_asr_android.Models.Subjects;

import java.util.List;

public class SubjectActivity extends AppCompatActivity {

    TextView drawer_txt,new_note,txt_title;
    RecyclerView recyclerView;
    SubjectAdapter madapter;
    List<Subjects> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        drawer_txt=(TextView)findViewById(R.id.drawer_icon);
        new_note=(TextView)findViewById(R.id.new_note);
        txt_title=(TextView)findViewById(R.id.txt_title);

        recyclerView=(RecyclerView) findViewById(R.id.note_recycler);
        drawer_txt.setVisibility(View.VISIBLE);
        drawer_txt.setText("Back");
        new_note.setText("New");
        txt_title.setText("Subjects");
        list = NotesDatabase.getInstance(SubjectActivity.this).getSubjectDao().getAll();
        drawer_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                Intent i=new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(i);
            }
        });

        new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mainDialog = new AlertDialog.Builder(SubjectActivity.this);
                LayoutInflater inflater = (LayoutInflater)getApplicationContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.subject_dialog, null);
                mainDialog.setView(dialogView);

                final Button save = (Button) dialogView.findViewById(R.id.save);
                final ImageView cross=(ImageView) dialogView.findViewById(R.id.cross);
                final EditText sub = (EditText) dialogView.findViewById(R.id.sub_txt);
                final AlertDialog alertDialog = mainDialog.create();
                alertDialog.show();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //save item
                        Subjects subject =  new Subjects(sub.getText().toString());

                        NotesDatabase.getInstance(SubjectActivity.this).getSubjectDao().insert(subject);
                        madapter.list.clear();
                        madapter.list.addAll(NotesDatabase.getInstance(SubjectActivity.this).getSubjectDao().getAll());
                        recyclerView.getAdapter().notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

        madapter = new SubjectAdapter(this,list) {
            @Override
            public void deleteAddress(final int pos) {

                final AlertDialog.Builder mainDialog = new AlertDialog.Builder(SubjectActivity.this);
                LayoutInflater inflater = (LayoutInflater)getApplicationContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.alert_dialog, null);
                mainDialog.setView(dialogView);

                final Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                final Button save = (Button) dialogView.findViewById(R.id.save);
                final ImageView cross=(ImageView) dialogView.findViewById(R.id.cross);
                final TextView cat_name=(TextView)dialogView.findViewById(R.id.cat_name);
                final AlertDialog alertDialog = mainDialog.create();
                alertDialog.show();
                 cat_name.setText("You want to delete this subject?");
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //delete item
                        NotesDatabase.getInstance(SubjectActivity.this).getSubjectDao().delete( list.get(pos));
                        list.remove(pos);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }

            @Override
            public void editSubject(final int i) {

                final AlertDialog.Builder mainDialog = new AlertDialog.Builder(SubjectActivity.this);
                LayoutInflater inflater = (LayoutInflater)getApplicationContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.subject_dialog, null);
                mainDialog.setView(dialogView);

                final Button save = (Button) dialogView.findViewById(R.id.save);
                final ImageView cross=(ImageView) dialogView.findViewById(R.id.cross);
                final EditText sub = dialogView.findViewById(R.id.sub_txt);
                sub.setText(list.get(i).getSubject_name());
                final AlertDialog alertDialog = mainDialog.create();
                alertDialog.show();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //save item
                        Subjects subject =  list.get(i);
                        subject.setSubject_name(sub.getText().toString());
                        NotesDatabase.getInstance(SubjectActivity.this).getSubjectDao().update(subject);
                        alertDialog.dismiss();
                    }
                });
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }

            @Override
            public void selectSubject(int i) {
                Intent intent = new Intent();
                Subjects subject =  list.get(i);
                intent.putExtra("data", subject.getSubject_id());
                setResult(RESULT_OK, intent);
                finish();
            }
        };
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(madapter);

    }
}
