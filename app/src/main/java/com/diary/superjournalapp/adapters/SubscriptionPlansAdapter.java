package com.diary.superjournalapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.diary.superjournalapp.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * Adapter for displaying subscription plans in premium upgrade screen
 */
public class SubscriptionPlansAdapter extends RecyclerView.Adapter<SubscriptionPlansAdapter.PlanViewHolder> {
    
    private List<ProductDetails> subscriptionPlans;
    private OnPlanSelectedListener listener;
    
    public interface OnPlanSelectedListener {
        void onPlanSelected(String productId);
    }
    
    public SubscriptionPlansAdapter(List<ProductDetails> plans, OnPlanSelectedListener listener) {
        this.subscriptionPlans = plans;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subscription_plan, parent, false);
        return new PlanViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        ProductDetails product = subscriptionPlans.get(position);
        holder.bind(product, listener);
    }
    
    @Override
    public int getItemCount() {
        return subscriptionPlans.size();
    }
    
    static class PlanViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView planNameText;
        private TextView planPriceText;
        private TextView planDescriptionText;
        private TextView savingsText;
        private Button selectPlanBtn;
        
        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.plan_card);
            planNameText = itemView.findViewById(R.id.plan_name_text);
            planPriceText = itemView.findViewById(R.id.plan_price_text);
            planDescriptionText = itemView.findViewById(R.id.plan_description_text);
            savingsText = itemView.findViewById(R.id.plan_savings_text);
            selectPlanBtn = itemView.findViewById(R.id.select_plan_btn);
        }
        
        public void bind(ProductDetails product, OnPlanSelectedListener listener) {
            String productId = product.getProductId();
            
            // Set plan details based on product ID
            if (productId.equals("diaryverse_premium_monthly")) {
                planNameText.setText("Monthly Premium");
                planDescriptionText.setText("Full access to all premium features");
                savingsText.setVisibility(View.GONE);
                
                // Highlight as popular choice
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.app_blue));
                cardView.setStrokeWidth(3);
                
            } else if (productId.equals("diaryverse_premium_yearly")) {
                planNameText.setText("Yearly Premium");
                planDescriptionText.setText("Best value - Save 37% with annual billing");
                savingsText.setText("💰 Best Value - Save 37%");
                savingsText.setVisibility(View.VISIBLE);
                
                // Highlight as best value
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.success_green));
                cardView.setStrokeWidth(3);
                
            } else if (productId.equals("diaryverse_premium_lifetime")) {
                planNameText.setText("👑 Lifetime Premium");
                planDescriptionText.setText("Pay once, own forever - Never pay again!");
                savingsText.setText("⭐ Most Popular - One-time payment");
                savingsText.setVisibility(View.VISIBLE);
                
                // Highlight as premium choice with gold color
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.premium_gold));
                cardView.setStrokeWidth(4);
            }
            
            // Get price from subscription offer details
            if (product.getSubscriptionOfferDetails() != null && 
                !product.getSubscriptionOfferDetails().isEmpty()) {
                
                ProductDetails.SubscriptionOfferDetails offerDetails = 
                    product.getSubscriptionOfferDetails().get(0);
                    
                if (offerDetails.getPricingPhases() != null &&
                    offerDetails.getPricingPhases().getPricingPhaseList() != null &&
                    !offerDetails.getPricingPhases().getPricingPhaseList().isEmpty()) {
                    
                    ProductDetails.PricingPhase pricingPhase = 
                        offerDetails.getPricingPhases().getPricingPhaseList().get(0);
                    
                    planPriceText.setText(pricingPhase.getFormattedPrice());
                }
            }
            
            // Set up click listener
            selectPlanBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlanSelected(productId);
                }
            });
        }
    }
}
