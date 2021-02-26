package com.note_asr_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.note_asr_android.R;
import com.note_asr_android.Models.Subjects;

import java.util.List;

public abstract class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.Viewholder> {

    public Context context;
    public List<Subjects> list;

    public SubjectAdapter(Context context,List<Subjects> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  itemview= LayoutInflater.from(context).inflate(R.layout.subject_item,parent,false);
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
        holder.subject.setText(list.get(position).getSubject_name());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView subject;
        ImageView delete;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            subject=(TextView)itemView.findViewById(R.id.subject_txt);
            delete=(ImageView) itemView.findViewById(R.id.sub_delete);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            selectSubject(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            editSubject(getAdapterPosition());
            return true;
        }
    }
    public abstract void deleteAddress(int i);
    public abstract void editSubject(int i);
    public abstract void selectSubject(int i);

}
