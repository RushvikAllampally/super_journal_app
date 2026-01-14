package com.diary.superjournalapp.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.diary.superjournalapp.R;
import com.diary.superjournalapp.adapters.SubscriptionPlansAdapter;
import com.diary.superjournalapp.billing.BillingManager;
import com.diary.superjournalapp.utils.PremiumFeatureManager;

import java.util.List;

/**
 * Activity for premium subscription upgrade
 */
public class PremiumUpgradeActivity extends AppCompatActivity implements BillingManager.BillingCallback {
    
    private static final String TAG = "PremiumUpgrade";
    
    private BillingManager billingManager;
    private RecyclerView subscriptionPlansRecycler;
    private SubscriptionPlansAdapter adapter;
    private TextView featuresText;
    private Button restorePurchaseBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_upgrade);
        
        initializeViews();
        setupBilling();
        displayPremiumFeatures();
    }
    
    private void initializeViews() {
        subscriptionPlansRecycler = findViewById(R.id.subscription_plans_recycler);
        featuresText = findViewById(R.id.premium_features_text);
        restorePurchaseBtn = findViewById(R.id.restore_purchase_btn);
        
        subscriptionPlansRecycler.setLayoutManager(new LinearLayoutManager(this));
        
        restorePurchaseBtn.setOnClickListener(v -> restorePurchases());
        
        // Set up back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("DiaryVerse Premium");
        }
    }
    
    private void setupBilling() {
        billingManager = new BillingManager(this, this);
    }
    
    private void displayPremiumFeatures() {
        PremiumFeatureManager premiumManager = PremiumFeatureManager.getInstance(this);
        featuresText.setText(premiumManager.getSubscriptionPlansDescription());
    }
    
    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "Billing initialized successfully");
    }
    
    @Override
    public void onProductsLoaded(List<ProductDetails> products) {
        Log.d(TAG, "Products loaded: " + products.size());
        
        // DEBUG: Log all loaded products
        for (ProductDetails product : products) {
            Log.d(TAG, "Loaded product: " + product.getProductId() + " - " + product.getName());
        }
        
        if (products.size() > 0) {
            // Hide loading and show subscription plans
            subscriptionPlansRecyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Subscription plans visible - hiding fallback UI");
        } else {
            Log.w(TAG, "No products loaded - showing fallback UI");
            showFallbackSubscriptionPlans();
        }
    }
    /**
     * Show fallback subscription plans when Google Play products aren't set up yet
     */
    private void showFallbackSubscriptionPlans() {
        Log.w(TAG, "No subscription products loaded - showing fallback UI");
        
        // Create mock products for testing/demo
        subscriptionPlansRecycler.setVisibility(android.view.View.GONE);
        
        // Show message about setting up Google Play Console
        android.widget.TextView fallbackText = new android.widget.TextView(this);
        fallbackText.setText("⚠️ Subscription products not configured in Google Play Console yet.\n\n" +
                            "To enable purchases:\n" +
                            "1. Create products in Google Play Console\n" +
                            "2. Product IDs: 'diaryverse_premium_monthly', 'diaryverse_premium_yearly'\n" +
                            "3. Set prices and activate products\n\n" +
                            "For testing: Use the 'Restore Previous Purchase' button to simulate premium access.");
        fallbackText.setPadding(32, 32, 32, 32);
        fallbackText.setTextSize(14f);
        
        // Add fallback text to the layout
        ((android.widget.LinearLayout) findViewById(android.R.id.content).getRootView()
            .findViewById(R.id.subscription_plans_recycler).getParent())
            .addView(fallbackText);
    }
    
    @Override
    public void onPurchaseSuccess(String productId) {
        Log.d(TAG, "Purchase successful: " + productId);
        
        Toast.makeText(this, "Welcome to DiaryVerse Premium! 🎉", Toast.LENGTH_LONG).show();
        
        // Close the upgrade activity
        setResult(RESULT_OK);
        finish();
    }
    
    @Override
    public void onPurchaseError(String error) {
        Log.e(TAG, "Purchase failed: " + error);
        Toast.makeText(this, "Purchase failed: " + error, Toast.LENGTH_SHORT).show();
    }
    
    private void purchaseSubscription(String productId) {
        Log.d(TAG, "Starting purchase for: " + productId);
        billingManager.purchaseSubscription(this, productId);
    }
    
    private void restorePurchases() {
        // Billing manager automatically checks for existing purchases on initialization
        Toast.makeText(this, "Checking for previous purchases...", Toast.LENGTH_SHORT).show();
        
        // Re-initialize billing to trigger purchase restoration
        if (billingManager != null) {
            billingManager.destroy();
        }
        setupBilling();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingManager != null) {
            billingManager.destroy();
        }
    }
}
