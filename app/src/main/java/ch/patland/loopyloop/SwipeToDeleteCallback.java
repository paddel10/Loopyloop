package ch.patland.loopyloop;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

abstract class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                         @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlag);
    }

    @Override
    public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target
    ) {
        return false;
    }
}
