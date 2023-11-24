package com.example.superjournalapp.recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superjournalapp.R;
import com.example.superjournalapp.entity.Journal;
import com.example.superjournalapp.screens.journals.BulletJournal;
import com.example.superjournalapp.screens.journals.DreamJournal;
import com.example.superjournalapp.screens.journals.GratitudeJournal;
import com.example.superjournalapp.screens.journals.ReflectiveJournal;
import com.example.superjournalapp.utils.JournalUtils;

import java.util.ArrayList;

public class JournalRecyclerAdaptor extends RecyclerView.Adapter<JournalRecyclerAdaptor.ViewHolder> {

    Context context;
    ArrayList<Journal> journalArrayList;

    public JournalRecyclerAdaptor(Context context,ArrayList<Journal> journalsList){
        this.context=context;
        System.out.println(journalsList);
        this.journalArrayList =journalsList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdaptor.ViewHolder holder, int position) {

        holder.journalDate.setText(JournalUtils.getDateFromJavaDate(journalArrayList.get(position).getJournalCreatedOn()));
        holder.journalMonth.setText(JournalUtils.getMonthFromJavaDate(journalArrayList.get(position).getJournalCreatedOn()));
        holder.journalTitle.setText(journalArrayList.get(position).getTitle());
        holder.journalContent.setText(journalArrayList.get(position).getJournalStartText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String journalCategory = journalArrayList.get(holder.getAdapterPosition()).getJournalCategory();
                Intent intent;
                switch (journalCategory){
                    case "Gratitude Journal":
                        intent = new Intent(view.getContext(), GratitudeJournal.class);
                        intent.putExtra("journalId",String.valueOf(journalArrayList.get(holder.getAdapterPosition()).getJournalId()));
                        view.getContext().startActivity(intent);
                        break;
                    case "Bullet Journal":
                        intent = new Intent(view.getContext(), BulletJournal.class);
                        intent.putExtra("journalId",String.valueOf(journalArrayList.get(holder.getAdapterPosition()).getJournalId()));
                        view.getContext().startActivity(intent);
                        break;
                    case "Dream Journal":
                        intent = new Intent(view.getContext(), DreamJournal.class);
                        intent.putExtra("journalId",String.valueOf(journalArrayList.get(holder.getAdapterPosition()).getJournalId()));
                        view.getContext().startActivity(intent);
                        break;
                    case "My Diary":
                        intent = new Intent(view.getContext(), ReflectiveJournal.class);
                        intent.putExtra("journalId",String.valueOf(journalArrayList.get(holder.getAdapterPosition()).getJournalId()));
                        view.getContext().startActivity(intent);
                        break;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return journalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView journalDate;
        TextView journalMonth;
        TextView journalTitle;
        TextView journalContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            journalDate = itemView.findViewById(R.id.journal_row_date);
            journalMonth = itemView.findViewById(R.id.journal_row_month);
            journalTitle=itemView.findViewById(R.id.journal_row_title);
            journalContent= itemView.findViewById(R.id.journal_row_details);
        }
    }

    public void updateData() {
        // Notify the adapter of the data change
        notifyDataSetChanged();
    }

}
