package com.example.superjournalapp.recyclerviews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class BulletRecyclerRowMoveCallback extends ItemTouchHelper.Callback {

    private RecyclerViewRowTouchHelperContract touchHelperContract;

    public BulletRecyclerRowMoveCallback(RecyclerViewRowTouchHelperContract touchHelperContract) {
        this.touchHelperContract = touchHelperContract;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlag, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        this.touchHelperContract.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof BulletRecyclerAdaptor.ViewHolder) {
                BulletRecyclerAdaptor.ViewHolder myViewHolder = (BulletRecyclerAdaptor.ViewHolder) viewHolder;
                touchHelperContract.onRowSelected(myViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof BulletRecyclerAdaptor.ViewHolder) {
            BulletRecyclerAdaptor.ViewHolder myViewHolder = (BulletRecyclerAdaptor.ViewHolder) viewHolder;
            touchHelperContract.onRowClear(myViewHolder);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public interface RecyclerViewRowTouchHelperContract {
        void onRowMoved(int from, int to);

        void onRowSelected(BulletRecyclerAdaptor.ViewHolder myViewHolder);

        void onRowClear(BulletRecyclerAdaptor.ViewHolder myViewHolder);
    }
}