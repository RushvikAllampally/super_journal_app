package com.diary.superjournalapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.diary.superjournalapp.screens.fragments.BookmarkedJournalsFragment;
import com.diary.superjournalapp.screens.fragments.JournalListFragment;
import com.diary.superjournalapp.screens.fragments.TagSearchFragment;

/**
 * Adapter for the ViewPager2 in the Library tab that handles page transitions between
 * All Journals, Bookmarks, and Tags tabs
 */
public class LibraryPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;
    public static final int ALL_JOURNALS_PAGE = 0;
    public static final int BOOKMARKS_PAGE = 1;
    public static final int TAGS_PAGE = 2;

    public LibraryPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Always create new fragment instances to avoid "Fragment already added" errors
        switch (position) {
            case ALL_JOURNALS_PAGE:
                return new JournalListFragment();
            case BOOKMARKS_PAGE:
                // Don't use getInstance() which returns a singleton - create a new instance
                return new BookmarkedJournalsFragment();
            case TAGS_PAGE:
                return new TagSearchFragment();
            default:
                return new JournalListFragment(); // Default case
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
