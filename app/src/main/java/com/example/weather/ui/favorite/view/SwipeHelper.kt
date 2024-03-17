package com.example.weather.ui.favorite.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.response.SavedLocations
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class SwipeHelper (
    val context: Context,
    private val item: FavoriteAdapter,
    private val onSwipeCallback:(it:SavedLocations)->Unit
):ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){

        private val deleteIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_delete)
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            //Up&Down move
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val swipedItem = item.currentList[position]
            onSwipeCallback(swipedItem)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean,
        ) {
            /*if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + deleteIcon.intrinsicHeight
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + deleteIcon.intrinsicWidth

                if (dX > 0) { // Swiping to the right
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.draw(c)
                } else { // Other Swiping directions
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                }

            }*/
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

}