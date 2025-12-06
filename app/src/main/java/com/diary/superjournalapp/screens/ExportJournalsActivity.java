package com.diary.superjournalapp.screens;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.adapters.ExportJournalAdapter;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.export.ExportManager;
import com.diary.superjournalapp.export.ExportOptions;
import com.diary.superjournalapp.export.ExportProgressListener;
import com.diary.superjournalapp.export.ExportResult;
import com.diary.superjournalapp.utils.PremiumFeatureManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity for batch exporting journals by date range
 */
public class ExportJournalsActivity extends AppCompatActivity {
    
    // UI Components
    private ImageButton backButton;
    private Button presetWeekButton, presetMonthButton, presetAllButton;
    private com.google.android.material.card.MaterialCardView dateRangeCard;
    private TextView selectedDateRangeText;
    private LinearLayout selectionControls, emptyState;
    private CheckBox selectAllCheckbox;
    private TextView selectedCountText;
    private RecyclerView journalsRecyclerView;
    private com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton exportButton;
    
    // Data
    private Calendar fromDate, toDate;
    private DatabaseHelper databaseHelper;
    private ExportJournalAdapter adapter;
    private PremiumFeatureManager premiumFeatureManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_journals);
        
        // Initialize
        databaseHelper = DatabaseHelper.getDb(this);
        premiumFeatureManager = PremiumFeatureManager.getInstance(this);
        
        // Check premium access
        if (!premiumFeatureManager.canExportJournals()) {
            showUpgradeDialog();
            return;
        }
        
        // Initialize dates (default: last 30 days)
        toDate = Calendar.getInstance();
        fromDate = Calendar.getInstance();
        fromDate.add(Calendar.DAY_OF_MONTH, -30);
        
        // Initialize UI
        initializeViews();
        setupRecyclerView();
        setupListeners();
        updateDateRangeDisplay();
    }
    
    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        presetWeekButton = findViewById(R.id.preset_week_button);
        presetMonthButton = findViewById(R.id.preset_month_button);
        presetAllButton = findViewById(R.id.preset_all_button);
        dateRangeCard = findViewById(R.id.date_range_card);
        selectedDateRangeText = findViewById(R.id.selected_date_range_text);
        selectionControls = findViewById(R.id.selection_controls);
        emptyState = findViewById(R.id.empty_state);
        selectAllCheckbox = findViewById(R.id.select_all_checkbox);
        selectedCountText = findViewById(R.id.selected_count_text);
        journalsRecyclerView = findViewById(R.id.journals_recycler_view);
        exportButton = findViewById(R.id.export_button);
    }
    
    private void setupRecyclerView() {
        adapter = new ExportJournalAdapter(selectedCount -> {
            // Update UI when selection changes
            updateSelectionUI(selectedCount);
        });
        
        journalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        journalsRecyclerView.setAdapter(adapter);
    }
    
    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
        
        // Preset buttons
        presetWeekButton.setOnClickListener(v -> {
            setDateRange(7);
            loadJournals();
        });
        
        presetMonthButton.setOnClickListener(v -> {
            setDateRange(30);
            loadJournals();
        });
        
        presetAllButton.setOnClickListener(v -> {
            setDateRangeAll();
            loadJournals();
        });
        
        // Custom date range card
        dateRangeCard.setOnClickListener(v -> showCustomDateRangePicker());
        
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adapter.selectAll();
            } else {
                adapter.deselectAll();
            }
        });
        
        exportButton.setOnClickListener(v -> startExport());
    }
    
    /**
     * Set date range to last N days
     */
    private void setDateRange(int days) {
        toDate = Calendar.getInstance();
        fromDate = Calendar.getInstance();
        fromDate.add(Calendar.DAY_OF_MONTH, -days);
        updateDateRangeDisplay();
    }
    
    /**
     * Set date range to all time
     */
    private void setDateRangeAll() {
        toDate = Calendar.getInstance();
        fromDate = Calendar.getInstance();
        fromDate.add(Calendar.YEAR, -10); // 10 years back
        updateDateRangeDisplay();
    }
    
    /**
     * Update the date range display text
     */
    private void updateDateRangeDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String rangeText = dateFormat.format(fromDate.getTime()) + " - " + dateFormat.format(toDate.getTime());
        selectedDateRangeText.setText(rangeText);
    }
    
    /**
     * Show custom date range picker dialog
     */
    private void showCustomDateRangePicker() {
        // Show from date picker first
        DatePickerDialog fromPicker = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                fromDate.set(Calendar.YEAR, year);
                fromDate.set(Calendar.MONTH, month);
                fromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                // Then show to date picker
                DatePickerDialog toPicker = new DatePickerDialog(
                    this,
                    (view2, year2, month2, dayOfMonth2) -> {
                        toDate.set(Calendar.YEAR, year2);
                        toDate.set(Calendar.MONTH, month2);
                        toDate.set(Calendar.DAY_OF_MONTH, dayOfMonth2);
                        updateDateRangeDisplay();
                        loadJournals();
                    },
                    toDate.get(Calendar.YEAR),
                    toDate.get(Calendar.MONTH),
                    toDate.get(Calendar.DAY_OF_MONTH)
                );
                toPicker.setTitle("Select End Date");
                toPicker.show();
            },
            fromDate.get(Calendar.YEAR),
            fromDate.get(Calendar.MONTH),
            fromDate.get(Calendar.DAY_OF_MONTH)
        );
        fromPicker.setTitle("Select Start Date");
        fromPicker.show();
    }
    
    /**
     * Load journals for selected date range
     */
    private void loadJournals() {
        // Validate date range
        if (fromDate.after(toDate)) {
            Toast.makeText(this, "From date must be before To date", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get journals from database
        List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
        
        // Filter by date range
        List<Journal> filteredJournals = new java.util.ArrayList<>();
        Date fromTime = fromDate.getTime();
        Date toTime = toDate.getTime();
        
        // Set time to end of day for toDate
        Calendar toDateEnd = Calendar.getInstance();
        toDateEnd.setTime(toTime);
        toDateEnd.set(Calendar.HOUR_OF_DAY, 23);
        toDateEnd.set(Calendar.MINUTE, 59);
        toDateEnd.set(Calendar.SECOND, 59);
        toTime = toDateEnd.getTime();
        
        for (Journal journal : allJournals) {
            Date journalDate = journal.getJournalCreatedOn();
            if (!journalDate.before(fromTime) && !journalDate.after(toTime)) {
                filteredJournals.add(journal);
            }
        }
        
        // Sort by date (newest first)
        filteredJournals.sort((j1, j2) -> j2.getJournalCreatedOn().compareTo(j1.getJournalCreatedOn()));
        
        // Update UI
        if (filteredJournals.isEmpty()) {
            showEmptyState();
            Toast.makeText(this, "No journals found in this date range", Toast.LENGTH_SHORT).show();
        } else {
            showJournalsList(filteredJournals);
            Toast.makeText(this, "Found " + filteredJournals.size() + " journals", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Show empty state
     */
    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        journalsRecyclerView.setVisibility(View.GONE);
        selectionControls.setVisibility(View.GONE);
        exportButton.setVisibility(View.GONE);
    }
    
    /**
     * Show journals list
     */
    private void showJournalsList(List<Journal> journals) {
        adapter.setJournals(journals);
        emptyState.setVisibility(View.GONE);
        journalsRecyclerView.setVisibility(View.VISIBLE);
        selectionControls.setVisibility(View.VISIBLE);
        exportButton.setVisibility(View.VISIBLE);
        updateSelectionUI(0);
    }
    
    /**
     * Update selection UI
     */
    private void updateSelectionUI(int selectedCount) {
        selectedCountText.setText(selectedCount + " selected");
        exportButton.setEnabled(selectedCount > 0);
        
        // Update select all checkbox without triggering listener
        selectAllCheckbox.setOnCheckedChangeListener(null);
        selectAllCheckbox.setChecked(adapter.isAllSelected());
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adapter.selectAll();
            } else {
                adapter.deselectAll();
            }
        });
        
        // Update export button text
        if (selectedCount > 0) {
            exportButton.setText("Export " + selectedCount + " Journal" + (selectedCount > 1 ? "s" : ""));
        } else {
            exportButton.setText("Export Selected Journals");
        }
    }
    
    /**
     * Start batch export - Show dialog for export options
     */
    private void startExport() {
        List<Journal> selectedJournals = adapter.getSelectedJournals();
        
        if (selectedJournals.isEmpty()) {
            Toast.makeText(this, "Please select at least one journal", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create export options dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_export_options, null);
        
        // Get dialog views
        RadioGroup formatGroup = dialogView.findViewById(R.id.format_radio_group);
        RadioButton formatPdfOption = dialogView.findViewById(R.id.format_pdf);
        CheckBox includeTagsOption = dialogView.findViewById(R.id.include_tags);
        CheckBox includeMetadataOption = dialogView.findViewById(R.id.include_metadata);
        
        // Set defaults
        formatPdfOption.setChecked(true);
        includeTagsOption.setChecked(true);
        includeMetadataOption.setChecked(true);
        
        // Show dialog
        new AlertDialog.Builder(this)
            .setTitle("Export Options")
            .setView(dialogView)
            .setPositiveButton("Export", (dialog, which) -> {
                // Gather options from dialog
                ExportOptions options = new ExportOptions();
                
                // Format
                int selectedFormatId = formatGroup.getCheckedRadioButtonId();
                if (selectedFormatId == R.id.format_pdf) {
                    options.setFormat(ExportOptions.ExportFormat.PDF);
                } else {
                    options.setFormat(ExportOptions.ExportFormat.TXT);
                }
                
                // Options
                options.setIncludeTags(includeTagsOption.isChecked());
                options.setIncludeMetadata(includeMetadataOption.isChecked());
                
                // Perform export
                performExport(selectedJournals, options);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    /**
     * Perform the actual export
     */
    private void performExport(List<Journal> journals, ExportOptions options) {
        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Exporting Journals");
        progressDialog.setMessage("Preparing export...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Run export in background thread
        new Thread(() -> {
            ExportManager exportManager = new ExportManager(this);
            
            exportManager.exportJournals(journals, options, new ExportProgressListener() {
                @Override
                public void onProgress(int progress, String message) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressDialog.setProgress(progress);
                        progressDialog.setMessage(message);
                    });
                }
                
                @Override
                public void onSuccess(ExportResult result) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressDialog.dismiss();
                        showSuccessDialog(result);
                    });
                }
                
                @Override
                public void onError(String error) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(ExportJournalsActivity.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }).start();
    }
    
    /**
     * Show success dialog after export
     */
    private void showSuccessDialog(ExportResult result) {
        new AlertDialog.Builder(this)
            .setTitle("Export Complete")
            .setMessage(result.getMessage() + "\n\nFiles saved to: Downloads/DiaryVerse Exports/")
            .setPositiveButton("OK", (dialog, which) -> {
                // Clear selections
                adapter.deselectAll();
            })
            .show();
    }
    
    /**
     * Show upgrade dialog for non-premium users
     */
    private void showUpgradeDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Premium Feature")
            .setMessage(premiumFeatureManager.getExportUpgradeMessage())
            .setPositiveButton("Upgrade to Premium", (dialog, which) -> {
                // TODO: Launch premium upgrade activity
                Toast.makeText(this, "Premium upgrade coming soon!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .setNegativeButton("Go Back", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }
}
