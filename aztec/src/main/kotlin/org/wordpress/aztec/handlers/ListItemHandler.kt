package org.wordpress.aztec.handlers

import android.text.Spannable
import org.wordpress.aztec.spans.AztecListItemSpan

class ListItemHandler() : BlockHandler<AztecListItemSpan>(AztecListItemSpan::class.java) {

    override fun handleNewlineAtStartOfBlock() {
        // newline added at start of bullet so, add a new bullet
        newListItem(text, newlineIndex, newlineIndex + 1, block.span.nestingLevel)

        // push current bullet forward
        block.start = newlineIndex + 1
    }

    override fun handleNewlineAtEmptyLineAtBlockEnd() {
        // just remove listitem when entering a newline on an empty item at the end of the list
        block.remove()
    }

    override fun handleNewlineAtEmptyBody() {
        // just remove listitem when entering a newline on an empty item at the end of the list
        block.remove()
    }

    // fun handleNewlineAtTextEnd()
    // got a newline while being at the end-of-text. We'll let the current list item engulf it and will wait
    //  for the end-of-text marker event in order to attach the new list item to it when that happens.

    override fun handleNewlineInBody() {
        // newline added at some position inside the bullet so, end the current bullet and append a new one
        newListItem(text, newlineIndex + 1, block.end, block.span.nestingLevel)
        block.end = newlineIndex + 1
    }

    override fun handleEndOfBufferMarker() {
        if (block.start == markerIndex) {
            // ok, this list item has the marker as its first char so, nothing more to do here.
            return
        }

        // attach a new bullet around the end-of-text marker
        newListItem(text, markerIndex, markerIndex + 1, block.span.nestingLevel)

        // the current list item has bled over to the marker so, let's adjust its range to just before the marker.
        //  There's a newline there hopefully :)
        block.end = markerIndex
    }

    companion object {
        fun newListItem(text: Spannable, start: Int, end: Int, nestingLevel: Int) {
            set(text, AztecListItemSpan(nestingLevel), start, end)
        }
    }
}
