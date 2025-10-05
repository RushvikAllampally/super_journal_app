# Dark Mode Implementation Guide

This guide outlines all the changes made to fix dark mode issues in the Super Journal App.

## Overview of Changes

1. Created theme-aware vector drawables for icons
2. Fixed text and background colors to use theme attributes
3. Added proper tinting to all icons
4. Created reusable theme components for dividers
5. Updated chart appearance in dark mode

## Files Created

- `ic_profile.xml`: Theme-aware profile icon
- `ic_streak.xml`: Theme-aware streak icon
- `ic_bookmark.xml`: Theme-aware bookmark icon
- `ic_bookmark_filled.xml`: Theme-aware filled bookmark icon
- `ic_total_entries.xml`: Statistics icon for entries
- `ic_streak_count.xml`: Statistics icon for streak count
- `ic_weekly.xml`: Statistics icon for weekly view
- `ic_most_productive.xml`: Statistics icon for productivity
- `theme_divider.xml`: Reusable divider that adapts to theme
- `reminder_item_background.xml`: Theme-aware background
- `chart_styles.xml`: Theme-specific chart appearance

## Implementation Steps

### 1. Use the New Icons

Replace the image-based icons with the vector drawables:

```xml
<!-- Before -->
android:background="@drawable/profile_icon"

<!-- After -->
android:background="?attr/selectableItemBackgroundBorderless"
android:src="@drawable/ic_profile"
```

### 2. Fix Text Colors

All text colors should use theme attributes:

```xml
<!-- Before -->
android:textColor="@color/black"

<!-- After -->
android:textColor="?attr/colorOnSurface"
```

### 3. Fix Backgrounds

Backgrounds should use theme attributes:

```xml
<!-- Before -->
android:background="@color/white"

<!-- After -->
android:background="?attr/colorSurface"
```

### 4. Use Themed Dividers

Replace all divider views:

```xml
<!-- Before -->
<View
    android:layout_width="match_parent"
    android:layout_height="1dp"
    android:background="@color/black" />

<!-- After -->
<View
    android:layout_width="match_parent"
    android:layout_height="1dp"
    android:background="@drawable/theme_divider" />
```

### 5. Apply Icon Tinting

Add tint to icons:

```xml
<!-- Before -->
android:src="@drawable/some_icon"

<!-- After -->
android:src="@drawable/some_icon"
android:tint="?attr/colorOnSurface"
```

## Next Steps

1. Replace `activity_reminder_screen.xml` with `activity_reminder_screen_fixed.xml` 
2. Test the app thoroughly in both light and dark modes
3. Apply similar fixes to any other screens not covered by these changes
4. Update `MoodStatisticsFragment.java` to handle chart colors in dark mode

## Known Issues to Fix

- Chart axis colors and legend colors in statistics view
- Journal list row text subtext colors
- Affirmation card text contrast in dark mode

## Testing

To ensure dark mode works correctly:
1. Test on different devices
2. Check all transitions between light and dark mode
3. Verify text is always readable against its background
