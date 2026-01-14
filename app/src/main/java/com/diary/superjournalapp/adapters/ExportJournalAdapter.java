package com.diary.superjournalapp.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.entity.Journal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Adapter for displaying journals in export screen with checkboxes
 */
public class ExportJournalAdapter extends RecyclerView.Adapter<ExportJournalAdapter.ViewHolder> {
    
    private List<Journal> journals;
    private Set<Long> selectedJournalIds;
    private OnSelectionChangeListener selectionListener;
    
    public interface OnSelectionChangeListener {
        void onSelectionChanged(int selectedCount);
    }
    
    public ExportJournalAdapter(OnSelectionChangeListener listener) {
        this.journals = new ArrayList<>();
        this.selectedJournalIds = new HashSet<>();
        this.selectionListener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_export_journal, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Journal journal = journals.get(position);
        long journalId = journal.getJournalId();
        
        // Set title
        holder.titleText.setText(journal.getTitle());
        
        // Set category
        holder.categoryText.setText(journal.getJournalCategory());
        
        // Set date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String dateStr = dateFormat.format(journal.getJournalCreatedOn());
        holder.dateText.setText(dateStr);
        
        // Set preview (strip HTML tags)
        String preview = getJournalPreview(journal);
        holder.previewText.setText(preview);
        
        // Set checkbox state
        holder.checkbox.setChecked(selectedJournalIds.contains(journalId));
        
        // Show lock indicator if locked
        holder.lockIndicator.setVisibility(journal.isLocked() ? View.VISIBLE : View.GONE);
        
        // Handle checkbox clicks
        holder.checkbox.setOnClickListener(v -> {
            if (holder.checkbox.isChecked()) {
                selectedJournalIds.add(journalId);
            } else {
                selectedJournalIds.remove(journalId);
            }
            if (selectionListener != null) {
                selectionListener.onSelectionChanged(selectedJournalIds.size());
            }
        });
        
        // Handle item clicks (toggle checkbox)
        holder.itemView.setOnClickListener(v -> {
            holder.checkbox.setChecked(!holder.checkbox.isChecked());
            if (holder.checkbox.isChecked()) {
                selectedJournalIds.add(journalId);
            } else {
                selectedJournalIds.remove(journalId);
            }
            if (selectionListener != null) {
                selectionListener.onSelectionChanged(selectedJournalIds.size());
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return journals.size();
    }
    
    /**
     * Get preview text from journal (strip HTML)
     */
    private String getJournalPreview(Journal journal) {
        String description = journal.getJournalStartText();
        if (description == null || description.isEmpty()) {
            return "No preview available";
        }
        
        // Strip HTML tags
        String plainText = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY).toString();
        plainText = plainText.trim();
        
        // Limit length
        if (plainText.length() > 100) {
            plainText = plainText.substring(0, 100) + "...";
        }
        
        return plainText;
    }
    
    /**
     * Update journal list
     */
    public void setJournals(List<Journal> journals) {
        this.journals = new ArrayList<>(journals);
        notifyDataSetChanged();
    }
    
    /**
     * Get list of selected journal IDs
     */
    public List<Long> getSelectedJournalIds() {
        return new ArrayList<>(selectedJournalIds);
    }
    
    /**
     * Get list of selected journals
     */
    public List<Journal> getSelectedJournals() {
        List<Journal> selected = new ArrayList<>();
        for (Journal journal : journals) {
            if (selectedJournalIds.contains(journal.getJournalId())) {
                selected.add(journal);
            }
        }
        return selected;
    }
    
    /**
     * Select all journals
     */
    public void selectAll() {
        selectedJournalIds.clear();
        for (Journal journal : journals) {
            selectedJournalIds.add(journal.getJournalId());
        }
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(selectedJournalIds.size());
        }
    }
    
    /**
     * Deselect all journals
     */
    public void deselectAll() {
        selectedJournalIds.clear();
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(0);
        }
    }
    
    /**
     * Get selection count
     */
    public int getSelectedCount() {
        return selectedJournalIds.size();
    }
    
    /**
     * Check if all journals are selected
     */
    public boolean isAllSelected() {
        return selectedJournalIds.size() == journals.size() && journals.size() > 0;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView titleText;
        TextView categoryText;
        TextView dateText;
        TextView previewText;
        ImageView lockIndicator;
        
        ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.journal_checkbox);
            titleText = itemView.findViewById(R.id.journal_title);
            categoryText = itemView.findViewById(R.id.journal_category);
            dateText = itemView.findViewById(R.id.journal_date);
            previewText = itemView.findViewById(R.id.journal_preview);
            lockIndicator = itemView.findViewById(R.id.lock_indicator);
        }
    }
}
