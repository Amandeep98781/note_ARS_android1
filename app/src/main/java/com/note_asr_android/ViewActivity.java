package com.note_asr_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.note_asr_android.Models.Notes;
import com.note_asr_android.Models.Subjects;
import com.note_asr_android.utils.DataConverter;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class ViewActivity extends AppCompatActivity {

    TextView drawer_txt,new_note,txt_title,deal_txt;
    private static final int CAMERA_REQUEST = 102;
    private static final int GALLERY_REQUEST = 101;
    EditText title,description;
    Button record,subject,map;
    RelativeLayout share_layout;
    ImageView share_pic,deal_icon;
    FrameLayout share_frame;
    String pathAudio;
    Bitmap image;
    NotesDatabase notesDatabase;
    Subjects selectedSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        drawer_txt=(TextView)findViewById(R.id.drawer_icon);
        new_note=(TextView)findViewById(R.id.new_note);
        title=(EditText) findViewById(R.id.new_title);
        description=(EditText) findViewById(R.id.description);
        record=(Button) findViewById(R.id.record);
        map=(Button) findViewById(R.id.onMap);
        subject=(Button) findViewById(R.id.select_subject);
        share_layout=(RelativeLayout) findViewById(R.id.share_layout);
        share_pic=(ImageView) findViewById(R.id.share_pic);
        share_frame=(FrameLayout) findViewById(R.id.share_frame);
        txt_title=(TextView)findViewById(R.id.txt_title);
        deal_txt=(TextView)findViewById(R.id.deal_txt);
        deal_icon=(ImageView) findViewById(R.id.deal_icon);
        drawer_txt.setVisibility(View.VISIBLE);
        drawer_txt.setText("Back");
        new_note.setText("Update");
        txt_title.setText("View Note");
        share_layout.setVisibility(View.VISIBLE);
        notesDatabase = NotesDatabase.getInstance(ViewActivity.this);
        drawer_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
        new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckValidation()) {
                    List<Notes> notes = notesDatabase.getNoteDao().getAll();
                    int index = getIntent().getIntExtra("selectedIndex",-1);
                    if (index != -1){
                        Notes note = notes.get(index);
                        note.setTitle(title.getText().toString());
                        note.setDescription(description.getText().toString());
                        note.setNote_audio(pathAudio);
                        note.setSubject_id_fk(selectedSubject.getSubject_id());
                        if(image != null){
                            note.setNote_image(DataConverter.convertImage2ByteArray(image));
                        }
                        notesDatabase.getNoteDao().update(note);
                    }

                    drawer_txt.performClick();
                }
            }
        });
        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),SubjectActivity.class);
                startActivityForResult(i, 11);
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MapsActivity.class);
                int index = getIntent().getIntExtra("selectedIndex",-1);
                i.putExtra("selectedIndex",index);
                startActivity(i);
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),AudioActivity.class);
                startActivityForResult(i,112);
            }
        });

        share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        getAndSetNotes();
    }

    private void getAndSetNotes() {
        List<Notes> notes = notesDatabase.getNoteDao().getAll();
        int index = getIntent().getIntExtra("selectedIndex",-1);
        if (index != -1){
            Notes note = notes.get(index);
            title.setText(note.getTitle());
            description.setText(note.getDescription());
            byte[] data = note.getNote_image();
            if(data != null){
                image = DataConverter.convertByteArray2Bitmap(data);
                share_pic.setImageBitmap(image);
                deal_icon.setVisibility(View.INVISIBLE);
                deal_txt.setVisibility(View.INVISIBLE);
            }
            Subjects sub = notesDatabase.getSubjectDao().getSubject(note.getSubject_id_fk()).get(0);
            subject.setText(sub.getSubject_name());
             selectedSubject = sub;

        }
    }

    public boolean CheckValidation(){

        if (title.getText().toString().trim().length() == 0) {
            title.setError("Please enter title");
            title.requestFocus();
            return false;

        }else if(description.getText().toString().trim().length() == 0) {

            description.setError("Please enter description");
            description.requestFocus();
            return false;
        }
        return true;


    }

    private void selectImage() {
        gallery();

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
             image = (Bitmap) data.getExtras().get("data");
            share_pic.setImageBitmap(image);
            deal_icon.setVisibility(View.INVISIBLE);
            deal_txt.setVisibility(View.INVISIBLE);
        }
        else if(reqCode == 112 && resultCode == RESULT_OK){
             pathAudio = data.getStringExtra("audio");
        }
        else if(reqCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri uri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                share_pic.setImageBitmap(image);
                deal_icon.setVisibility(View.INVISIBLE);
                deal_txt.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        else if (reqCode == 11){
            if(resultCode == RESULT_OK) {
                int subjectID = data.getIntExtra("data",-1);
                if(subjectID != -1){
                    for (Subjects sub: notesDatabase.getSubjectDao().getAll()) {
                        if(sub.getSubject_id() == subjectID){
                            selectedSubject = sub;
                            subject.setText(sub.getSubject_name());
                        }
                    }
                    //  selectedSubject =
                }
            }
        }

    }

    public void gallery() {

        final CharSequence[] items = { "Take Photo", "Choose from Library","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //  boolean result= Utility.checkPermission(ShareDeal.this);

                if (items[item].equals("Take Photo")) {

                    CaptureImage();
                } else if (items[item].equals("Choose from Library")) {
                    OpenGallery();
                } else if(items[item].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    public void CaptureImage() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void OpenGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }
}
