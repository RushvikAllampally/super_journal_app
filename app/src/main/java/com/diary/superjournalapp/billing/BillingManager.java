package com.diary.superjournalapp.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.*;
import com.diary.superjournalapp.utils.PremiumFeatureManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages Google Play Billing for DiaryVerse Premium subscriptions
 */
public class BillingManager implements PurchasesUpdatedListener, BillingClientStateListener {
    
    private static final String TAG = "BillingManager";
    
    // Subscription product IDs (must match Google Play Console)
    public static final String MONTHLY_PREMIUM = "diaryverse_premium_monthly";
    public static final String YEARLY_PREMIUM = "diaryverse_premium_yearly";
    public static final String LIFETIME_PREMIUM = "diaryverse_premium_lifetime";
    
    private BillingClient billingClient;
    private Context context;
    private BillingCallback callback;
    private List<ProductDetails> subscriptionProducts = new ArrayList<>();
    private boolean isServiceConnected = false;
    private boolean isInitializing = false;
    
    public interface BillingCallback {
        void onBillingInitialized();
        void onPurchaseSuccess(String productId);
        void onPurchaseError(String error);
        void onProductsLoaded(List<ProductDetails> products);
    }
    
    public BillingManager(Context context, BillingCallback callback) {
        this.context = context;
        this.callback = callback;
        
        billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build();
                
        startConnection();
    }
    
    private void startConnection() {
        if (isInitializing) {
            Log.w(TAG, "Already initializing billing client");
            return;
        }
        
        isInitializing = true;
        billingClient.startConnection(this);
    }
    
    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        isInitializing = false;
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        
        Log.d(TAG, "Billing setup finished. Response code: " + responseCode + ", Debug message: " + debugMessage);
        
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Billing setup successful");
            isServiceConnected = true;
            
            // Check for existing purchases first
            checkExistingPurchases();
            
            // Load available products
            loadSubscriptionProducts();
            
            if (callback != null) {
                callback.onBillingInitialized();
            }
        } else {
            // Handle setup failure
            isServiceConnected = false;
            String errorMsg = getBillingErrorMessage(responseCode);
            Log.e(TAG, "Billing setup failed. Code: " + responseCode + ", Message: " + debugMessage + ", Readable: " + errorMsg);
            
            if (callback != null) {
                callback.onPurchaseError("Billing setup failed: " + errorMsg);
            }
        }
    }
    
    @Override
    public void onBillingServiceDisconnected() {
        Log.w(TAG, "Billing service disconnected");
        isServiceConnected = false;
        // Service will automatically try to reconnect
    }
    
    /**
     * Reconnect to billing service if disconnected
     */
    public void reconnectIfNeeded() {
        if (!isServiceConnected && !isInitializing) {
            Log.d(TAG, "Reconnecting to billing service");
            startConnection();
        }
    }
    
    /**
     * Convert billing response code to readable error message
     */
    private String getBillingErrorMessage(int responseCode) {
        switch (responseCode) {
            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                return "Service timeout - try again";
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                return "Billing not supported on this device";
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                return "Google Play Store not available";
            case BillingClient.BillingResponseCode.USER_CANCELED:
                return "User canceled";
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                return "Google Play Store service unavailable";
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                return "Billing API version not supported";
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                return "Subscription products not available";
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                return "Developer error - check app setup in Play Console";
            case BillingClient.BillingResponseCode.ERROR:
                return "Fatal error during API action";
            default:
                return "Unknown error (code: " + responseCode + ")";
        }
    }
    
    private void loadProducts() {
        Log.d(TAG, "Loading products from Google Play...");
        Log.d(TAG, "Product IDs: " + MONTHLY_PREMIUM + ", " + YEARLY_PREMIUM + ", " + LIFETIME_PREMIUM);
        
        List<QueryProductDetailsParams.Product> productList = Arrays.asList(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(MONTHLY_PREMIUM)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(YEARLY_PREMIUM)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(LIFETIME_PREMIUM)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        );
        
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();
                
        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                subscriptionProducts = productDetailsList;
                Log.d(TAG, "Loaded " + productDetailsList.size() + " subscription products");
                
                if (callback != null) {
                    callback.onProductsLoaded(productDetailsList);
                }
            } else {
                Log.e(TAG, "Failed to load products: " + billingResult.getDebugMessage());
            }
        });
    }
    
    public void purchaseSubscription(Activity activity, String productId) {
        // Validate prerequisites
        if (!isServiceConnected) {
            Log.e(TAG, "Billing service not connected");
            reconnectIfNeeded();
            if (callback != null) {
                callback.onPurchaseError("Billing service not available. Please try again.");
            }
            return;
        }
        
        if (activity == null || activity.isFinishing()) {
            Log.e(TAG, "Invalid activity provided for purchase");
            if (callback != null) {
                callback.onPurchaseError("Unable to process purchase");
            }
            return;
        }
        
        ProductDetails productDetails = findProductDetails(productId);
        if (productDetails == null) {
            Log.e(TAG, "Product not found: " + productId);
            if (callback != null) {
                callback.onPurchaseError("Product not available");
            }
            return;
        }
        
        try {
            BillingFlowParams.ProductDetailsParams productDetailsParams;
            
            // Handle different product types
            if (LIFETIME_PREMIUM.equals(productId)) {
                // One-time purchase (lifetime)
                productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build();
            } else {
                // Subscription (monthly/yearly)
                List<ProductDetails.SubscriptionOfferDetails> offers = productDetails.getSubscriptionOfferDetails();
                if (offers == null || offers.isEmpty()) {
                    Log.e(TAG, "No subscription offers available for: " + productId);
                    if (callback != null) {
                        callback.onPurchaseError("Subscription not available");
                    }
                    return;
                }
                
                ProductDetails.SubscriptionOfferDetails offerDetails = offers.get(0);
                productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerDetails.getOfferToken())
                    .build();
            }
            
            List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
            productDetailsParamsList.add(productDetailsParams);
            
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
                
            BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
            
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                String errorMsg = getBillingErrorMessage(billingResult.getResponseCode());
                Log.e(TAG, "Failed to launch billing flow: " + billingResult.getDebugMessage());
                if (callback != null) {
                    callback.onPurchaseError("Purchase failed: " + errorMsg);
                }
            } else {
                Log.d(TAG, "Billing flow launched successfully for: " + productId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during purchase: " + e.getMessage());
            if (callback != null) {
                callback.onPurchaseError("Purchase failed due to unexpected error");
            }
        }
    }
    
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Purchase canceled by user");
        } else {
            Log.e(TAG, "Purchase failed: " + billingResult.getDebugMessage());
            if (callback != null) {
                callback.onPurchaseError("Purchase failed");
            }
        }
    }
    
    private void handlePurchase(Purchase purchase) {
        try {
            List<String> products = purchase.getProducts();
            if (products.isEmpty()) {
                Log.e(TAG, "No products in purchase");
                return;
            }
            
            String productId = products.get(0);
            Log.d(TAG, "Handling purchase: " + productId);
            
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                // Verify purchase security
                if (!isValidPurchase(purchase)) {
                    Log.e(TAG, "Invalid purchase detected: " + productId);
                    if (callback != null) {
                        callback.onPurchaseError("Purchase verification failed");
                    }
                    return;
                }
                
                // Acknowledge purchase if not already done
                if (!purchase.isAcknowledged()) {
                    acknowledgePurchase(purchase);
                }
                
                // Grant premium access
                grantPremiumAccess(productId);
                
                if (callback != null) {
                    callback.onPurchaseSuccess(productId);
                }
            } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                Log.d(TAG, "Purchase pending: " + productId);
                // Handle pending state (payment methods like cash)
            } else {
                Log.w(TAG, "Purchase in unknown state: " + purchase.getPurchaseState());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling purchase: " + e.getMessage());
            if (callback != null) {
                callback.onPurchaseError("Failed to process purchase");
            }
        }
    }
    
    private void checkExistingPurchases() {
        if (!isServiceConnected) {
            Log.w(TAG, "Cannot check purchases - service not connected");
            return;
        }
        
        // Check subscriptions
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            (billingResult, purchases) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Found " + (purchases != null ? purchases.size() : 0) + " subscription purchases");
                    if (purchases != null) {
                        for (Purchase purchase : purchases) {
                            handleExistingPurchase(purchase);
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to query subscription purchases: " + billingResult.getDebugMessage());
                }
            });
        
        // Check one-time purchases (lifetime)
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            (billingResult, purchases) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Found " + (purchases != null ? purchases.size() : 0) + " in-app purchases");
                    if (purchases != null) {
                        for (Purchase purchase : purchases) {
                            handleExistingPurchase(purchase);
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to query in-app purchases: " + billingResult.getDebugMessage());
                }
            });
    }
    
    private void grantPremiumAccess(String productId) {
        try {
            PremiumFeatureManager premiumManager = PremiumFeatureManager.getInstance(context);
            
            // Set premium status with product tracking
            premiumManager.setPremiumStatusWithProduct(true, productId);
            
            // Force refresh to ensure cache is updated
            premiumManager.refreshPremiumStatus();
            
            Log.d(TAG, "Premium access granted for: " + productId);
            Log.d(TAG, "Subscription type: " + premiumManager.getSubscriptionTypeName());
            Log.d(TAG, "Premium status: " + premiumManager.getPremiumStatusInfo());
        } catch (Exception e) {
            Log.e(TAG, "Error granting premium access: " + e.getMessage());
        }
    }
    
    private ProductDetails findProductDetails(String productId) {
        for (ProductDetails product : subscriptionProducts) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }
    
    private void handleExistingPurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            List<String> products = purchase.getProducts();
            if (!products.isEmpty()) {
                String productId = products.get(0);
                grantPremiumAccess(productId);
                Log.d(TAG, "Restored existing purchase: " + productId);
            }
        }
    }
    
    private boolean isValidPurchase(Purchase purchase) {
        // Basic validation - in production, implement server-side verification
        try {
            return purchase != null && 
                   !purchase.getProducts().isEmpty() &&
                   purchase.getPurchaseToken() != null &&
                   !purchase.getPurchaseToken().isEmpty();
        } catch (Exception e) {
            Log.e(TAG, "Purchase validation error: " + e.getMessage());
            return false;
        }
    }
    
    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = 
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
                
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully");
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: " + billingResult.getDebugMessage());
            }
        });
    }
    
    /**
     * Clean up billing client resources
     */
    public void destroy() {
        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
            isServiceConnected = false;
            Log.d(TAG, "Billing client destroyed");
        }
    }
    
    /**
     * Check if billing service is ready for transactions
     */
    public boolean isReady() {
        return isServiceConnected && billingClient != null && billingClient.isReady();
    }
    
    /**
     * Get all loaded subscription products
     */
    public List<ProductDetails> getSubscriptionProducts() {
        return new ArrayList<>(subscriptionProducts);
    }
}
