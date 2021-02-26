package com.note_asr_android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.note_asr_android.MapsActivity;
import com.note_asr_android.Models.Notes;
import com.note_asr_android.Models.Subjects;
import com.note_asr_android.NotesDatabase;
import com.note_asr_android.R;
import com.note_asr_android.ViewActivity;
import com.note_asr_android.utils.DataConverter;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public abstract class notesAdapter extends RecyclerView.Adapter<notesAdapter.Viewholder> {

    Context context;
    public List<Notes> notes;
    public List<Subjects> subjects;

    public notesAdapter(Context context, List<Notes> list,List<Subjects> subjects) {
        this.context = context;
        this.notes = list;
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  itemview= LayoutInflater.from(context).inflate(R.layout.note_item,parent,false);
        return new Viewholder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position) {

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAddress(position);
            }
        });
        holder.title.setText(notes.get(position).getTitle());
        holder.description.setText(notes.get(position).getDescription());
        for(Subjects sub:subjects){
            if(sub.getSubject_id() == notes.get(position).getSubject_id_fk()){
                holder.txtSubjectItem.setText("Subject: "+sub.getSubject_name());
            }
        }

        long millisecond = notes.get(position).getCreated();
        // or you already have long value of date, use this instead of milliseconds variable.
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        holder.date.setText(dateString);

        if(notes.get(position).getNote_image() != null){
            holder.note_img.setVisibility(View.VISIBLE);
            Bitmap image = DataConverter.convertByteArray2Bitmap(notes.get(position).getNote_image());
            if(image != null){
                holder.note_img.setImageBitmap(image);
            }

        }
        else{
            holder.note_img.setVisibility(View.GONE);
            holder.note_img.setImageDrawable(null);
        }


    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView date,title,description,txtSubjectItem;
        ImageView note_img,delete;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            date=(TextView)itemView.findViewById(R.id.date);
            title=(TextView)itemView.findViewById(R.id.title);
            description=(TextView)itemView.findViewById(R.id.desc);
            note_img=(ImageView) itemView.findViewById(R.id.note_image);
            delete=(ImageView) itemView.findViewById(R.id.delete);
            txtSubjectItem =  (TextView)itemView.findViewById(R.id.txtSubjectItem);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            Intent i=new Intent(context, ViewActivity.class);
            i.putExtra("selectedIndex",getAdapterPosition());
            context.startActivity(i);

        }
    }

    public abstract void deleteAddress(int i);

}
