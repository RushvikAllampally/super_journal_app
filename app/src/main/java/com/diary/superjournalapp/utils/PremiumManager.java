package com.diary.superjournalapp.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.diary.superjournalapp.billing.BillingManager;

/**
 * Central manager for all premium features and billing operations
 * This class provides a unified interface for premium functionality
 */
public class PremiumManager implements BillingManager.BillingCallback {
    
    private static final String TAG = "PremiumManager";
    
    private Context context;
    private BillingManager billingManager;
    private PremiumFeatureManager featureManager;
    private PremiumCallback callback;
    
    public interface PremiumCallback {
        void onPremiumStatusChanged(boolean isPremium);
        void onPurchaseSuccess(String productId);
        void onPurchaseError(String error);
        void onProductsLoaded(int productCount);
    }
    
    public PremiumManager(Context context, PremiumCallback callback) {
        this.context = context;
        this.callback = callback;
        this.featureManager = PremiumFeatureManager.getInstance(context);
        this.billingManager = new BillingManager(context, this);
    }
    
    // ========== BillingManager.BillingCallback Implementation ==========
    
    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "Billing system initialized successfully");
    }
    
    @Override
    public void onPurchaseSuccess(String productId) {
        Log.i(TAG, "Purchase successful: " + productId);
        
        // Premium access is automatically granted by BillingManager
        boolean isPremium = featureManager.isPremiumUser();
        
        if (callback != null) {
            callback.onPremiumStatusChanged(isPremium);
            callback.onPurchaseSuccess(productId);
        }
    }
    
    @Override
    public void onPurchaseError(String error) {
        Log.e(TAG, "Purchase failed: " + error);
        if (callback != null) {
            callback.onPurchaseError(error);
        }
    }
    
    @Override
    public void onProductsLoaded(java.util.List<com.android.billingclient.api.ProductDetails> products) {
        Log.d(TAG, "Loaded " + products.size() + " subscription products");
        if (callback != null) {
            callback.onProductsLoaded(products.size());
        }
    }
    
    // ========== Public Premium API ==========
    
    /**
     * Check if user has premium access
     */
    public boolean isPremiumUser() {
        return featureManager.isPremiumUser();
    }
    
    /**
     * Start a subscription purchase
     */
    public void purchaseSubscription(Activity activity, String productId) {
        if (billingManager.isReady()) {
            billingManager.purchaseSubscription(activity, productId);
        } else {
            Log.w(TAG, "Billing not ready for purchase");
            if (callback != null) {
                callback.onPurchaseError("Billing system not ready. Please try again.");
            }
        }
    }
    
    /**
     * Check premium feature availability
     */
    public boolean canLockJournals() {
        return featureManager.canLockMoreJournals();
    }
    
    public boolean canExportJournals() {
        return featureManager.canExportJournals();
    }
    
    public boolean canBackupToCloud() {
        return featureManager.canUseBackupFeature();
    }
    
    /**
     * Get current locked journals count
     */
    public int getLockedJournalsCount() {
        return featureManager.getLockedJournalsCount();
    }
    
    /**
     * Show premium upgrade dialog
     */
    public void showUpgradeDialog(Context context) {
        featureManager.showPremiumUpgradeDialog(context);
    }
    
    /**
     * Force refresh premium status from Google Play
     */
    public void refreshPremiumStatus() {
        featureManager.refreshPremiumStatus();
    }
    
    /**
     * Get detailed premium status for debugging
     */
    public String getStatusInfo() {
        return featureManager.getPremiumStatusInfo();
    }
    
    /**
     * Get current subscription type information
     */
    public String getSubscriptionType() {
        return featureManager.getSubscriptionTypeName();
    }
    
    public String getActiveProduct() {
        return featureManager.getActivePremiumProduct();
    }
    
    public boolean hasMonthlySubscription() {
        return featureManager.hasMonthlySubscription();
    }
    
    public boolean hasYearlySubscription() {
        return featureManager.hasYearlySubscription();
    }
    
    public boolean hasLifetimeSubscription() {
        return featureManager.hasLifetimeSubscription();
    }
    
    /**
     * Clean up resources
     */
    public void destroy() {
        if (billingManager != null) {
            billingManager.destroy();
        }
    }
    
    // ========== Testing Helpers ==========
    
    /**
     * FOR TESTING: Simulate premium purchase
     */
    public void simulatePremiumPurchase() {
        featureManager.simulatePremiumPurchase(context);
        if (callback != null) {
            callback.onPremiumStatusChanged(true);
        }
    }
    
    /**
     * FOR TESTING: Simulate free user
     */
    public void simulateFreeUser() {
        featureManager.simulateFreeUser(context);
        if (callback != null) {
            callback.onPremiumStatusChanged(false);
        }
    }
}
