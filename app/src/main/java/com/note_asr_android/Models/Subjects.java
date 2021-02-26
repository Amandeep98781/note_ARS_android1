package com.note_asr_android.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Subjects{
   @PrimaryKey(autoGenerate = true)
   private int subject_id;
   private String subject_name;

   public Subjects(String subject_name) {
      this.subject_name = subject_name;
   }

   public int getSubject_id() {
      return subject_id;
   }

   public void setSubject_id(int subject_id) {
      this.subject_id = subject_id;
   }

   public String getSubject_name() {
      return subject_name;
   }

   public void setSubject_name(String subject_name) {
      this.subject_name = subject_name;
   }
}
