package ch.patland.loopyloop;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

abstract class SwipeToDeleteCallback: ItemTouchHelper.Callback() {

    @Override
    override fun getMovementFlags(@NonNull recyclerView: RecyclerView,
        @NonNull viewHolder: RecyclerView.ViewHolder): Int {
        val swipeFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(0, swipeFlag);
    }

    @Override
    override fun onMove(
            @NonNull recyclerView: RecyclerView,
            @NonNull viewHolder: RecyclerView.ViewHolder,
            @NonNull target: RecyclerView.ViewHolder
    ): Boolean {
        return false;
    }
}
