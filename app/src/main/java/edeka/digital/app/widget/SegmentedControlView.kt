/*
 * Copyright (c) 2021 EDEKA AG
 *
 *  ███████╗    ███████╗██████╗ ██████╗ ██╗       `        `
 *  ██╔════╝    ██╔════╝██╔══██╗██╔══██╗██║    ```````  ```````
 *  █████╗      █████╗  ██║  ██║██║  ██║██║    ``````◆◆◆◆``````
 *  ██╔══╝      ██╔══╝  ██║  ██║██║  ██║██║      ``◆◆◆◆◆◆◆◆``
 *  ███████╗    ███████╗██████╔╝██████╔╝██║        `◆◆◆◆◆◆`
 *  ╚══════╝    ╚══════╝╚═════╝ ╚═════╝ ╚═╝          `◆◆`
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package edeka.digital.app.widget

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.annotation.StyleRes
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import edeka.digital.app.marketsearch.R
import kotlinx.android.synthetic.main.segment_control_layout.view.*

/**
 * A custom view that represents the Android version of iOS Segmented control.
 *
 * To use this view add it to the XML layout, and specify how many segments it should have
 * by setting the property app:segmentCount to 2 or 3 (other amounts are unsupported).
 *
 * Next, make sure to define a string array with the titles for the segments using
 * app:segmentTitles. Make sure that the count of the segments and titles match.
 *
 * This view has fixed height and width.
 * */
class SegmentedControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var segmentCount: Int = 2
    private var selectedSegmentIndex = 0
    private lateinit var segments: MutableList<CardView>
    private var activeTextStyle: Int = 0

    @StyleRes
    private var inactiveTextStyle: Int = 0
    private var activeSegmentColor: Int = 0
    private var inactiveSegmentColor: Int = 0
    private var segmentChangedListener: OnSegmentChangedListener? = null
    private var viewPager: ViewPager? = null

    lateinit var segmentTitles: Array<CharSequence>
    lateinit var segmentIcons: IntArray

    init {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SegmentedControlView, 0, 0)

        segmentCount = typedArray.getInteger(R.styleable.SegmentedControlView_segmentCount, 2)
        if (segmentCount != 2 && segmentCount != 3) throw IllegalArgumentException("We support 2 or 3 segments only.")
        segmentTitles = typedArray.getTextArray(R.styleable.SegmentedControlView_segmentTitles)
        if (segmentTitles.size != segmentCount) throw IllegalStateException("Segment count and segment titles count mismatch!")
        segmentIcons =
            if (typedArray.hasValue(R.styleable.SegmentedControlView_segmentIcons)) {
                resources.obtainTypedArray(
                    typedArray.getResourceId(R.styleable.SegmentedControlView_segmentIcons, 0)
                ).let {
                    val drawableResourceIds = mutableListOf<Int>()
                    for (i in 0 until it.length()) {
                        drawableResourceIds.add(it.getResourceId(i, 0))
                    }
                    drawableResourceIds.toIntArray()
                }
            } else {
                IntArray(segmentCount) { 0 }
            }
        if (segmentIcons.size != segmentCount) throw IllegalStateException("Segment count and segment icon count mismatch!")

        val defaultActiveTextStyle = R.style.Title_1_Active
        activeTextStyle =
            if (typedArray.hasValue(R.styleable.SegmentedControlView_textStyleActive)) {
                val typedValue = TypedValue()
                typedArray.getValue(R.styleable.SegmentedControlView_textStyleActive, typedValue)
                typedValue.resourceId
            } else {
                defaultActiveTextStyle
            }

        val defaultInactiveTextStyle = R.style.Title_1_Inactive
        inactiveTextStyle =
            if (typedArray.hasValue(R.styleable.SegmentedControlView_textStyleInactive)) {
                val typedValue = TypedValue()
                typedArray.getValue(R.styleable.SegmentedControlView_textStyleInactive, typedValue)
                typedValue.resourceId
            } else {
                defaultInactiveTextStyle
            }
        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        setupView(segmentTitles, segmentIcons)
        super.onAttachedToWindow()
    }

    private fun setupView(segmentTitles: Array<CharSequence>, segmentIcons: IntArray) {
        inflater.inflate(R.layout.segment_control_layout, this, true)
        segments = mutableListOf(segment1, segment2, segment3)

        // if we only need 2 segments remove the 3rd
        if (segmentCount == 2) {
            segment3.visibility = View.GONE
            segments.remove(segment3)
        }

        // setup titles
        segmentTitles.forEachIndexed { index, title ->
            when (index) {
                0 -> title1.text = title
                1 -> title2.text = title
                2 -> title3.text = title
            }
        }

        // setup icons
        segmentIcons.forEachIndexed { index, iconResourceId ->
            iconResourceId.let { if (it != 0) ContextCompat.getDrawable(context, it) else null }
                ?.let {
                    when (index) {
                        0 -> title1.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
                        1 -> title2.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
                        2 -> title3.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
                    }
                }
        }

        // make CardView do something when clicked
        segment1.setOnClickListener(this)
        segment2.setOnClickListener(this)
        segment3.setOnClickListener(this)

        activeSegmentColor = ContextCompat.getColor(context, R.color.white)
        inactiveSegmentColor = ContextCompat.getColor(context, R.color.grey_white)

        // set initial active segment (with slight delay so the icon color is correct)
        Handler().post { setActiveSegment(0) }
    }

    override fun onClick(segment: View?) {
        when (segment?.id) {
            segment1.id -> setActiveSegment(0)
            segment2.id -> setActiveSegment(1)
            segment3.id -> setActiveSegment(2)
            else -> throw IllegalStateException("This should never happen! :)")
        }
    }

    private fun updateIconColor(textView: TextView) =
        textView.handler.post {
            // Make sure this is executed after text has the correct color
            textView.setCompoundDrawablesWithIntrinsicBounds(
                textView.compoundDrawables[0]?.mutate()
                    ?.apply { setTint(textView.currentTextColor) },
                null,
                null,
                null
            )
        }

    /**
     * Sets the specified segment active, deactivates other segments.
     * Make sure you
     *
     * @param segmentIndex The segment index - 0, 1, or 2
     * */
    fun setActiveSegment(@IntRange(from = 0, to = 2) segmentIndex: Int) {
        selectedSegmentIndex = segmentIndex

        segments.forEachIndexed { index, segment ->
            // each segment (CardView) has only one child of type TextView
            val title = segment.getChildAt(0) as TextView

            if (index == selectedSegmentIndex) {
                segment.setCardBackgroundColor(activeSegmentColor)
                // 8dp elevation -> biggest shadow
                segment.elevation = 12f * resources.displayMetrics.density
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    title.setTextAppearance(activeTextStyle)
                } else {
                    @Suppress("DEPRECATION")
                    title.setTextAppearance(context, activeTextStyle)
                }
                updateIconColor(title)
            } else {
                segment.setCardBackgroundColor(inactiveSegmentColor)
                updateIconColor(title)
                // 0dp elevation -> no shadow
                segment.elevation = 0f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    title.setTextAppearance(inactiveTextStyle)
                } else {
                    @Suppress("DEPRECATION")
                    title.setTextAppearance(context, inactiveTextStyle)
                }
            }
        }

        // notify any listeners that the active segment changed
        segmentChangedListener?.onSegmentChanged(segmentIndex)
        // swipe viewpager when segment gets clicked
        viewPager?.currentItem = segmentIndex

        Log.d("SegmentClick", "Segment changed: $segmentIndex")
    }

    /**
     * Set the listener to be notified when the active segment changes.
     * Note that this method should not be used to link the SegmentedControlView with
     * a ViewPager, use [setupWithViewPager] method instead.
     * */
    fun setOnSegmentChangedListener(listener: OnSegmentChangedListener) {
        segmentChangedListener = listener
    }

    /**
     * Removes segment change listener, if any
     * */
    fun removeOnSegmentChangedListener() {
        segmentChangedListener = null
    }

    /**
     * Convnience method to link a ViewPager to the SegmentedControlView.
     * Selecting a segment will update the ViewPager
     * */
    fun setupWithViewPager(pager: ViewPager) {
        viewPager = pager

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // unused
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // unused
            }

            override fun onPageSelected(position: Int) {
                // update segments when viewpager swiped
                if (selectedSegmentIndex != position) {
                    setActiveSegment(position)
                }
            }

        })
    }

    /**
     * Implement this interface and register it with [SegmentedControlView] to be notified when the
     * selected segment changes
     * */
    interface OnSegmentChangedListener {
        /**
         * Notify the listener that a new segment got activated
         *
         * @param activeSegmentIndex index of the active segment
         * */
        fun onSegmentChanged(activeSegmentIndex: Int)
    }

}