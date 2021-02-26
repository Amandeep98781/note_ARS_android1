package com.note_asr_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.note_asr_android.Adapter.notesAdapter;
import com.note_asr_android.Models.Notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  TextView drawer_txt,new_note,txt_title;
  ImageView search_icon;
  EditText search;
  RecyclerView recyclerView;
  notesAdapter madapter;
  NotesDatabase notesDatabase;
  List<Notes> listNotes;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    drawer_txt=(TextView)findViewById(R.id.drawer_icon);
    txt_title=(TextView)findViewById(R.id.txt_title);
    new_note=(TextView)findViewById(R.id.new_note);
    search=(EditText) findViewById(R.id.search_txt);
    search_icon=(ImageView) findViewById(R.id.search_icon);
    recyclerView=(RecyclerView) findViewById(R.id.note_recycler);
    drawer_txt.setVisibility(View.GONE);
    new_note.setText("New");
    txt_title.setText("Notes");
    notesDatabase = NotesDatabase.getInstance(MainActivity.this);
    listNotes =  NotesDatabase.getInstance(MainActivity.this).getNoteDao().getAll();
    new_note.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i=new Intent(getApplicationContext(),NewActivity.class);
        startActivity(i);
      }
    });

    search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
          //do here your stuff f
         // hideSoftKeyboard();
          InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        String  message = search.getText().toString();
          Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
          return true;
        }
        return false;
      }
    });
    search.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        filter(s.toString());
      }
    });

    madapter = new notesAdapter(this,listNotes,NotesDatabase.getInstance(MainActivity.this).getSubjectDao().getAll()) {
      @Override
      public void deleteAddress(final int pos) {

        final AlertDialog.Builder mainDialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater)getApplicationContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        mainDialog.setView(dialogView);

        final Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        final Button save = (Button) dialogView.findViewById(R.id.save);
        final  ImageView cross=(ImageView) dialogView.findViewById(R.id.cross);
        final AlertDialog alertDialog = mainDialog.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            alertDialog.dismiss();

          }
        });

        save.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            notesDatabase.getNoteDao().delete(listNotes.get(pos));
            listNotes.remove(pos);
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
    };
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setAdapter(madapter);


  }

  private void filter(String text) {
    listNotes =  NotesDatabase.getInstance(MainActivity.this).getNoteDao().getAll();
    List<Notes> temp = new ArrayList();
    for (Notes n :listNotes) {
      if(n.getTitle().toLowerCase().contains(text.toLowerCase()) || n.getDescription().toLowerCase().contains(text.toLowerCase())){
        temp.add(n);
      }
    }
    madapter.notes = temp;
    recyclerView.getAdapter().notifyDataSetChanged();
  }
}