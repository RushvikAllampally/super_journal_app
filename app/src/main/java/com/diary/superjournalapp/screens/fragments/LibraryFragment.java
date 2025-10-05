package com.diary.superjournalapp.screens.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.adapters.LibraryPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A fragment that provides access to all journal content types (All, Bookmarks, Tags)
 * Updated with improved UI using ViewPager2 and TabLayout
 */
public class LibraryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MaterialCardView searchCard;
    private EditText searchEditText;
    private ImageButton searchClearButton;
    
    public LibraryFragment() {
        // Required empty public constructor
    }

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library_improved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        initializeViews(view);
        
        // Set up the ViewPager and TabLayout
        setupViewPagerAndTabs();
        
        // Set up the search functionality
        setupSearch();
    }
    
    /**
     * Initialize all views
     */
    private void initializeViews(View view) {
        tabLayout = view.findViewById(R.id.library_tabs);
        viewPager = view.findViewById(R.id.library_view_pager);
        searchCard = view.findViewById(R.id.search_card);
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchClearButton = view.findViewById(R.id.search_clear_button);
    }
    
    /**
     * Set up the ViewPager2 with TabLayout
     */
    private void setupViewPagerAndTabs() {
        // Create the adapter for the ViewPager
        LibraryPagerAdapter pagerAdapter = new LibraryPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Connect TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All Journals");
                    tab.setIcon(R.drawable.ic_diary);
                    break;
                case 1:
                    tab.setText("Bookmarks");
                    tab.setIcon(R.drawable.bookmark_24);
                    break;
                case 2:
                    tab.setText("Tags");
                    tab.setIcon(R.drawable.tag_24);
                    break;
            }
        }).attach();
        
        // Listen for page changes to update UI accordingly
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Reset search field when changing tabs
                if (searchEditText != null) {
                    searchEditText.setText("");
                }
            }
        });
    }
    
    /**
     * Set up search functionality
     */
    private void setupSearch() {
        // Search is now permanently visible, so we just need to handle text changes
        
        // Handle search text changes
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                searchClearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                
                // Filter current fragment based on search text
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment instanceof Searchable) {
                    ((Searchable) currentFragment).onSearch(s.toString());
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
        
        // Set up clear button
        searchClearButton.setOnClickListener(v -> {
            searchEditText.setText("");
            searchClearButton.setVisibility(View.GONE);
        });
    }
    
    
    /**
     * Get the currently displayed fragment from the ViewPager
     * 
     * @return The current fragment
     */
    private Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentByTag("f" + viewPager.getCurrentItem());
    }
    
    /**
     * Interface for fragments that can be searched
     */
    public interface Searchable {
        void onSearch(String query);
    }
    
}
