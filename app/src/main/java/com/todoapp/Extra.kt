package com.todoapp

import java.util.Locale




class Extra {
    //Adding swipe to delete functionality
    /*private fun setItemTouchHelper(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder, ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val itemPosition = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.RIGHT) {
                    //Getting the item at a particular position
                    val saveDeletedItem = dataList[itemPosition]
                    Log.d("Tag", saveDeletedItem.toString())

                    //Remove item from our array list
                    dataList.removeAt(itemPosition)

                    //Notify our item is removed from adapter
                    rvAdapter.notifyItemRemoved(itemPosition)

                    // Show a SnackBar with an Undo option
                    showUndoSnackBar(saveDeletedItem, itemPosition)
                }
            }

            //It is used for erasing the part of a canvas
            private val clearPaint = Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }

            //which is called when a child view within the RecyclerView
            //is being drawn due to user interaction.
            override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean,) {

                val itemView = viewHolder.itemView
                val height = itemView.bottom.toFloat() - itemView.top.toFloat()

                //This will draw the background along the swipe of item. if the swipe action is
                //canceled then it clears the canvas design and continues with the default behavior.
                val isCanceled = dX == 0f && !isCurrentlyActive
                if (isCanceled) {
                    clearCanvas(canvas, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                // Set the background color for swipe
                val paint = Paint().apply {
                    color = Color.RED
                }
                //val paint = Paint()
                //paint.color = Color.RED

                // Draw the background color round
                val cornerRadius = 30f
                val rect = RectF(itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

                // Create a Path with rounded corners for the left side
                val path = Path().apply {
                    addRoundRect(
                        rect,
                        floatArrayOf(
                            cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius
                        ),
                        Path.Direction.CW
                    )
                }

                //val path = Path()
                //path.addRoundRect(rect, floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, 0f, 0f, cornerRadius, cornerRadius), Path.Direction.CW)

                // Draw the path with rounded left corners
                canvas.drawPath(path, paint)

                // Draw the delete icon
                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete)
                val iconWidth = deleteIcon?.intrinsicWidth
                val iconHeight = deleteIcon?.intrinsicHeight
                val halfIcon = iconWidth?.div(2)

                // Calculate position of delete icon
                val top = itemView.top + ((height - iconHeight!!) / 2).toInt()
                val bottom = top + iconHeight
                val left = itemView.left + halfIcon!!
                val right = left + iconWidth

                // Draw the delete icon
                deleteIcon?.setBounds(left, top, right, bottom)
                deleteIcon?.draw(canvas)

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
                c?.drawRect(left, top, right, bottom, clearPaint)
            }

        }).attachToRecyclerView(binding.rvAllEvents)
    }*/

}