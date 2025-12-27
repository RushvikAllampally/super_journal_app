package com.diary.superjournalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.diary.superjournalapp.database.DatabaseHelper;

/**
 * Manages premium feature access and subscription limits
 */
public class PremiumFeatureManager {
    
    private static final String PREFS_NAME = "premium_prefs";
    private static final String IS_PREMIUM_KEY = "is_premium_user";
    
    // Lock feature is PREMIUM ONLY - no free locks
    private static final int FREE_LOCK_LIMIT = 0; // Free users cannot lock journals
    
    // FOR TESTING ONLY - Set to true to bypass premium checks
    // IMPORTANT: Set to false before production release!
    private static final boolean ENABLE_LOCK_FOR_TESTING = false;
    
    // NEW: Export feature testing flag
    // IMPORTANT: Set to false before production release!
    private static final boolean ENABLE_EXPORT_FOR_TESTING = false;
    
    // NEW: Backup feature testing flag
    // IMPORTANT: Set to false before production release!
    private static final boolean ENABLE_BACKUP_FOR_TESTING = false;
    
    private static PremiumFeatureManager instance;
    private Context context;
    private SharedPreferences prefs;
    
    private PremiumFeatureManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Get singleton instance of PremiumFeatureManager
     */
    public static synchronized PremiumFeatureManager getInstance(Context context) {
        if (instance == null) {
            instance = new PremiumFeatureManager(context);
        }
        return instance;
    }
    
    /**
     * Check if user has premium subscription
     * Verifies with Google Play Billing for active subscriptions
     * 
     * @return true if user is premium subscriber
     */
    public boolean isPremiumUser() {
        // For testing mode - bypass real verification
        if (ENABLE_LOCK_FOR_TESTING || ENABLE_EXPORT_FOR_TESTING || ENABLE_BACKUP_FOR_TESTING) {
            return prefs.getBoolean(IS_PREMIUM_KEY, false);
        }
        
        // PRODUCTION: Check with Google Play Billing for active subscriptions
        return hasActiveSubscription();
    }
    
    /**
     * Check for active subscriptions with Google Play Billing
     * This is the REAL verification that prevents subscription bypass
     * 
     * @return true if user has active subscription
     */
    private boolean hasActiveSubscription() {
        try {
            // For frequent checks, use cached status if recent
            long lastCheck = prefs.getLong("premium_last_check", 0);
            long currentTime = System.currentTimeMillis();
            long fiveMinutes = 5 * 60 * 1000; // 5 minutes cache for frequent checks
            
            if (currentTime - lastCheck < fiveMinutes) {
                return getCachedPremiumStatus();
            }
            
            // Initialize billing client for verification
            com.android.billingclient.api.BillingClient billingClient = 
                com.android.billingclient.api.BillingClient.newBuilder(context)
                    .setListener((billingResult, purchases) -> {})
                    .enablePendingPurchases()
                    .build();
            
            // For real-time verification, we need async connection
            billingClient.startConnection(new com.android.billingclient.api.BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(com.android.billingclient.api.BillingResult billingResult) {
                    if (billingResult.getResponseCode() == com.android.billingclient.api.BillingClient.BillingResponseCode.OK) {
                        checkActivePurchases(billingClient);
                        // Update last check time
                        prefs.edit().putLong("premium_last_check", System.currentTimeMillis()).apply();
                    }
                    billingClient.endConnection();
                }
                
                @Override
                public void onBillingServiceDisconnected() {
                    android.util.Log.w("PremiumFeatureManager", "Billing service disconnected during verification");
                }
            });
            
            // Return cached status while async verification happens
            return getCachedPremiumStatus();
            
        } catch (Exception e) {
            android.util.Log.e("PremiumFeatureManager", "Error checking subscription: " + e.getMessage());
            // Fallback to cached status on error
            return getCachedPremiumStatus();
        }
    }
    
    /**
     * Check active purchases from Google Play
     */
    private boolean checkActivePurchases(com.android.billingclient.api.BillingClient billingClient) {
        // Query active subscriptions using current API
        billingClient.queryPurchasesAsync(
            com.android.billingclient.api.QueryPurchasesParams.newBuilder()
                .setProductType(com.android.billingclient.api.BillingClient.ProductType.SUBS)
                .build(),
            (billingResult, purchases) -> {
                if (billingResult.getResponseCode() == com.android.billingclient.api.BillingClient.BillingResponseCode.OK) {
                    updatePremiumStatusFromPurchases(purchases);
                }
            }
        );
        
        // For now, check cached status while async verification runs
        return getCachedPremiumStatus();
    }
    
    /**
     * Update premium status based on active purchases from Google Play
     */
    private void updatePremiumStatusFromPurchases(java.util.List<com.android.billingclient.api.Purchase> purchases) {
        boolean hasActivePremium = false;
        String activeProductId = null;
        
        if (purchases != null && !purchases.isEmpty()) {
            for (com.android.billingclient.api.Purchase purchase : purchases) {
                try {
                    // Check if purchase is for our premium products
                    java.util.List<String> products = purchase.getProducts();
                    for (String productId : products) {
                        if ("diaryverse_premium_monthly".equals(productId) || 
                            "diaryverse_premium_yearly".equals(productId) ||
                            "diaryverse_premium_lifetime".equals(productId)) {
                            
                            // Verify purchase is acknowledged and active
                            if (purchase.getPurchaseState() == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED &&
                                purchase.isAcknowledged()) {
                                hasActivePremium = true;
                                activeProductId = productId;
                                break;
                            }
                        }
                    }
                    if (hasActivePremium) break;
                } catch (Exception e) {
                    android.util.Log.e("PremiumFeatureManager", "Error processing purchase: " + e.getMessage());
                }
            }
        }
        
        // Update cached status with detailed logging
        boolean previousStatus = getCachedPremiumStatus();
        setCachedPremiumStatus(hasActivePremium);
        
        // Log status change
        if (previousStatus != hasActivePremium) {
            android.util.Log.i("PremiumFeatureManager", 
                String.format("Premium status changed: %s -> %s (Product: %s)", 
                    previousStatus, hasActivePremium, activeProductId));
        }
        
        android.util.Log.d("PremiumFeatureManager", 
            String.format("Updated premium status from Google Play: %s (Active products: %d)", 
                hasActivePremium, purchases != null ? purchases.size() : 0));
    }
    
    /**
     * Get cached premium status with expiry check
     */
    private boolean getCachedPremiumStatus() {
        // Check if cached status has expired (24 hours)
        long lastVerified = prefs.getLong("premium_last_verified", 0);
        long currentTime = System.currentTimeMillis();
        long twentyFourHours = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        
        if (currentTime - lastVerified > twentyFourHours) {
            // Status expired, assume non-premium for security
            android.util.Log.w("PremiumFeatureManager", "Premium status cache expired, defaulting to free");
            return false;
        }
        
        return prefs.getBoolean(IS_PREMIUM_KEY, false);
    }
    
    /**
     * Set cached premium status with timestamp
     */
    private void setCachedPremiumStatus(boolean isPremium) {
        prefs.edit()
            .putBoolean(IS_PREMIUM_KEY, isPremium)
            .putLong("premium_last_verified", System.currentTimeMillis())
            .apply();
    }
    
    /**
     * Set premium subscription status (for testing or after successful purchase)
     * 
     * @param isPremium true if user should have premium access
     */
    public void setPremiumStatus(boolean isPremium) {
        setCachedPremiumStatus(isPremium);
    }
    
    /**
     * Set premium status with specific product type tracking
     * This allows the app to know which subscription type is active
     */
    public void setPremiumStatusWithProduct(boolean isPremium, String productId) {
        setCachedPremiumStatus(isPremium);
        
        // Store the active product type
        if (isPremium && productId != null) {
            prefs.edit().putString("active_premium_product", productId).apply();
            android.util.Log.i("PremiumFeatureManager", "Premium activated with product: " + productId);
        } else {
            prefs.edit().remove("active_premium_product").apply();
            android.util.Log.i("PremiumFeatureManager", "Premium deactivated");
        }
    }
    
    /**
     * Get the currently active premium product type
     */
    public String getActivePremiumProduct() {
        return prefs.getString("active_premium_product", null);
    }
    
    /**
     * Check if user has specific subscription type
     */
    public boolean hasMonthlySubscription() {
        return "diaryverse_premium_monthly".equals(getActivePremiumProduct());
    }
    
    public boolean hasYearlySubscription() {
        return "diaryverse_premium_yearly".equals(getActivePremiumProduct());
    }
    
    public boolean hasLifetimeSubscription() {
        return "diaryverse_premium_lifetime".equals(getActivePremiumProduct());
    }
    
    /**
     * Get user-friendly subscription type name
     */
    public String getSubscriptionTypeName() {
        String product = getActivePremiumProduct();
        if (product == null) return "Free";
        
        switch (product) {
            case "diaryverse_premium_monthly":
                return "Monthly Premium";
            case "diaryverse_premium_yearly":
                return "Yearly Premium";
            case "diaryverse_premium_lifetime":
                return "Lifetime Premium";
            default:
                return "Premium";
        }
    }
    
    /**
     * Get the current count of locked journals
     * 
     * @return number of currently locked journals
     */
    public int getLockedJournalsCount() {
        DatabaseHelper db = DatabaseHelper.getDb(context);
        return db.journalDao().getLockedJournals().size();
    }
    
    /**
     * Check if user can lock more journals
     * Lock feature is PREMIUM ONLY (no free locks)
     * 
     * @return true if user can lock another journal
     */
    public boolean canLockMoreJournals() {
        // FOR TESTING: Bypass premium check
        if (ENABLE_LOCK_FOR_TESTING) {
            return true;
        }
        
        // PRODUCTION: Only premium users can lock journals
        return isPremiumUser();
    }
    
    /**
     * Get the maximum number of journals a user can lock
     * 
     * @return maximum lock limit for current user
     */
    public int getMaxLockLimit() {
        return isPremiumUser() ? Integer.MAX_VALUE : FREE_LOCK_LIMIT;
    }
    
    /**
     * Get remaining lock slots for free users
     * 
     * @return number of journals user can still lock
     */
    public int getRemainingLockSlots() {
        if (isPremiumUser()) {
            return Integer.MAX_VALUE;
        }
        
        return Math.max(0, FREE_LOCK_LIMIT - getLockedJournalsCount());
    }
    
    /**
     * Check if user can use the per-journal lock feature
     * 
     * @return true if feature is available
     */
    public boolean canUseLockFeature() {
        return isPremiumUser() || getLockedJournalsCount() < FREE_LOCK_LIMIT;
    }
    
    /**
     * Get feature description for current user
     * 
     * @return description of lock feature limits
     */
    public String getLockFeatureDescription() {
        if (ENABLE_LOCK_FOR_TESTING) {
            return "Testing Mode: Lock feature enabled";
        }
        
        if (isPremiumUser()) {
            return "Premium: Unlimited journal locks";
        }
        
        return "Premium Feature: Upgrade to lock journals";
    }
    
    /**
     * Check if a specific premium feature is available
     * 
     * @param feature The feature to check
     * @return true if feature is available
     */
    public boolean isFeatureAvailable(PremiumFeature feature) {
        switch (feature) {
            case PER_JOURNAL_LOCKING:
                return canUseLockFeature();
            case UNLIMITED_LOCKS:
                return isPremiumUser();
            case EXPORT_LOCKED_JOURNALS:
                return isPremiumUser();
            // NEW: Export features
            case EXPORT_PDF:
            case EXPORT_TXT:
            case BATCH_EXPORT:
                return canExportJournals();
            // NEW: Backup features
            case BACKUP_TO_CLOUD:
            case RESTORE_FROM_BACKUP:
            case AUTO_BACKUP:
                return canUseBackupFeature();
            default:
                return isPremiumUser();
        }
    }
    
    /**
     * Enum for different premium features
     */
    public enum PremiumFeature {
        PER_JOURNAL_LOCKING,
        UNLIMITED_LOCKS,
        EXPORT_LOCKED_JOURNALS,
        // NEW: Export features
        EXPORT_PDF,
        EXPORT_TXT,
        BATCH_EXPORT,
        // NEW: Backup features
        BACKUP_TO_CLOUD,
        RESTORE_FROM_BACKUP,
        AUTO_BACKUP,
        // Future features
        CLOUD_SYNC,
        ADVANCED_THEMES,
        PREMIUM_TEMPLATES
    }
    
    /**
     * Get upgrade message for when limits are reached
     * 
     * @return user-friendly upgrade message
     */
    public String getUpgradeMessage() {
        return "Journal locking is a Premium feature. " +
               "Upgrade to Premium to lock your private journals and access more exclusive features!";
    }
    
    /**
     * NEW: Check if user can export journals
     * Export is a PREMIUM feature
     * 
     * @return true if user can export journals
     */
    public boolean canExportJournals() {
        // FOR TESTING: Bypass premium check
        if (ENABLE_EXPORT_FOR_TESTING) {
            return true;
        }
        
        // PRODUCTION: Only premium users can export
        return isPremiumUser();
    }
    
    /**
     * NEW: Get export feature description for current user
     * 
     * @return description of export feature limits
     */
    public String getExportFeatureDescription() {
        if (ENABLE_EXPORT_FOR_TESTING) {
            return "Testing Mode: Export feature enabled";
        }
        
        if (isPremiumUser()) {
            return "Premium: Unlimited journal exports";
        }
        
        return "Premium Feature: Upgrade to export journals";
    }
    
    /**
     * NEW: Get upgrade message specifically for export feature
     * 
     * @return user-friendly upgrade message for export
     */
    public String getExportUpgradeMessage() {
        return "Journal export is a Premium feature.\n\n" +
               "Upgrade to DiaryVerse Premium to:\n" +
               "✨ Export journals to beautiful PDFs\n" +
               "📝 Create plain text backups\n" +
               "📚 Batch export multiple journals\n" +
               "🎨 Customize export formatting\n" +
               "💾 Share your memories easily";
    }
    
    /**
     * NEW: Check if user can use backup features
     * Backup is a PREMIUM feature
     * 
     * @return true if user can use backup features
     */
    public boolean canUseBackupFeature() {
        // FOR TESTING: Bypass premium check
        if (ENABLE_BACKUP_FOR_TESTING) {
            return true;
        }
        
        // PRODUCTION: Only premium users can use backup
        return isPremiumUser();
    }
    
    /**
     * NEW: Get backup feature description for current user
     * 
     * @return description of backup feature limits
     */
    public String getBackupFeatureDescription() {
        if (ENABLE_BACKUP_FOR_TESTING) {
            return "Testing Mode: Backup feature enabled";
        }
        
        if (isPremiumUser()) {
            return "Premium: Unlimited backups & restores";
        }
        
        return "Premium Feature: Upgrade to backup journals";
    }
    
    /**
     * NEW: Get upgrade message specifically for backup feature
     * 
     * @return user-friendly upgrade message for backup
     */
    public String getBackupUpgradeMessage() {
        return "Backup & Restore is a Premium feature.\n\n" +
               "Upgrade to DiaryVerse Premium to:\n" +
               "☁️ Secure cloud backups to Google Drive\n" +
               "🔒 Military-grade encryption (AES-256)\n" +
               "🔄 Automatic daily backups\n" +
               "📱 Restore on any device\n" +
               "🛡️ Never lose your memories again!";
    }
    
    /**
     * Show premium upgrade dialog for the backup feature
     * 
     * @param context The context to show dialog from
     */
    public void showPremiumUpgradeDialog(Context context) {
        // Launch the premium upgrade activity
        android.content.Intent intent = new android.content.Intent(context, 
            com.diary.superjournalapp.screens.PremiumUpgradeActivity.class);
        context.startActivity(intent);
    }
    
    /**
     * NEW: Get subscription product IDs
     * 
     * @return array of subscription product IDs for Google Play
     */
    public static String[] getSubscriptionProductIds() {
        return new String[]{
            "diaryverse_premium_monthly",
            "diaryverse_premium_yearly"
        };
    }
    
    /**
     * NEW: Get premium subscription prices (will be loaded from Google Play)
     * 
     * @return description of available subscription plans
     */
    public String getSubscriptionPlansDescription() {
        return "Choose your DiaryVerse Premium plan:\n\n" +
               "📅 Monthly Premium - Full access to all features ($1.99/month)\n" +
               "💰 Yearly Premium - Save 37% with annual billing ($14.99/year)\n" +
               "👑 Lifetime Premium - Pay once, own forever ($39.99 one-time)\n\n" +
               "All plans include:\n" +
               "🔒 Unlimited journal locking\n" +
               "☁️ Cloud backup & restore\n" +
               "📄 PDF & text export\n" +
               "🎨 Premium themes\n" +
               "⭐ Priority support\n" +
               "🚫 Ad-free experience";
    }
    
    /**
     * NEW: Check if testing flags should be disabled for production
     * 
     * @return true if app is ready for production
     */
    public boolean isProductionReady() {
        return !ENABLE_LOCK_FOR_TESTING && 
               !ENABLE_EXPORT_FOR_TESTING && 
               !ENABLE_BACKUP_FOR_TESTING;
    }
    
    /**
     * NEW: Get list of features that are currently in testing mode
     * 
     * @return list of features with testing enabled
     */
    public java.util.List<String> getTestingEnabledFeatures() {
        java.util.List<String> testingFeatures = new java.util.ArrayList<>();
        
        if (ENABLE_LOCK_FOR_TESTING) {
            testingFeatures.add("Journal Locking");
        }
        if (ENABLE_EXPORT_FOR_TESTING) {
            testingFeatures.add("Export Features");
        }
        if (ENABLE_BACKUP_FOR_TESTING) {
            testingFeatures.add("Backup & Restore");
        }
        
        return testingFeatures;
    }
    
    /**
     * Force refresh premium status from Google Play
     * Call this after successful purchases or when user requests refresh
     */
    public void refreshPremiumStatus() {
        try {
            // Clear cache to force fresh check
            prefs.edit()
                .remove("premium_last_check")
                .remove("premium_last_verified")
                .apply();
            
            // Force new verification
            hasActiveSubscription();
            android.util.Log.d("PremiumFeatureManager", "Premium status refresh initiated");
        } catch (Exception e) {
            android.util.Log.e("PremiumFeatureManager", "Error refreshing premium status: " + e.getMessage());
        }
    }
    
    /**
     * Check if premium verification is recent (within 5 minutes)
     */
    public boolean isVerificationRecent() {
        long lastCheck = prefs.getLong("premium_last_check", 0);
        long currentTime = System.currentTimeMillis();
        long fiveMinutes = 5 * 60 * 1000;
        return (currentTime - lastCheck) < fiveMinutes;
    }
    
    /**
     * Get premium status info for debugging
     */
    public String getPremiumStatusInfo() {
        boolean isPremium = isPremiumUser();
        long lastVerified = prefs.getLong("premium_last_verified", 0);
        long lastCheck = prefs.getLong("premium_last_check", 0);
        boolean isRecentVerification = isVerificationRecent();
        
        return String.format(
            "Premium: %s | Last Verified: %s | Last Check: %s | Recent: %s",
            isPremium,
            lastVerified > 0 ? new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(lastVerified)) : "Never",
            lastCheck > 0 ? new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(lastCheck)) : "Never",
            isRecentVerification
        );
    }
    
    /**
     * TESTING HELPER: Simulate premium purchase for testing
     * Call this to test the upgrade flow
     */
    public void simulatePremiumPurchase(Context context) {
        setPremiumStatus(true);
        android.widget.Toast.makeText(context, 
            "🎉 Premium activated! All features unlocked.", 
            android.widget.Toast.LENGTH_LONG).show();
        android.util.Log.d("PremiumFeatureManager", "TESTING: Premium status activated");
    }
    
    /**
     * TESTING HELPER: Simulate free user for testing
     * Call this to test premium restrictions
     */
    public void simulateFreeUser(Context context) {
        setPremiumStatus(false);
        android.widget.Toast.makeText(context, 
            "📱 Free user mode. Premium features restricted.", 
            android.widget.Toast.LENGTH_LONG).show();
        android.util.Log.d("PremiumFeatureManager", "TESTING: Free user mode activated");
    }
}
