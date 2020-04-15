package com.paysera.currency.exchange.common.util

import android.R
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by eduardo.delito on 4/14/20.
 */
object ViewUtil {
    const val ALPHA_VISIBLE = 1.0f
    const val ALPHA_HIDDEN = 0.0f
    const val NO_RESOURCE = -1
    private const val HERO_IMAGE_WIDTH_RATIO = 16
    private const val HERO_IMAGE_HEIGHT_RATIO = 9
    private val nextGeneratedId =
        AtomicInteger(1)

    /**
     * This is exact the same code that [View.generateViewId] has
     * but that method is only available on api level 17 or above.
     * We are using api level 16
     *
     * @return a generated ID value
     */
    private fun generateViewId(): Int {
        while (true) {
            val result = nextGeneratedId.get()
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            var newValue = result + 1
            if (newValue > 0x00FFFFFF) {
                // Roll over to 1, not 0.
                newValue = 1
            }
            if (nextGeneratedId.compareAndSet(result, newValue)) {
                return result
            }
        }
    }

    /**
     * Sets an id to the view
     *
     *
     * NOTE: This is useful while creating view programmatically because the default id of a view is -1
     * and if for example we want to refer to a view in a RelativeLayout we should assign
     * a valid id
     *
     * @param view to set the id
     */
    fun setIdToView(view: View) {
        view.id = View.generateViewId()
    }

    /**
     * Builds day tag. Can be used to find view in view hierarchy.
     *
     * @param year
     * @param month
     * @param day
     *
     * @return
     */
    fun buildDateTag(year: Int, month: Int, day: Int): String {
        return "" + year + month + day
    }

//    fun setTextAppearance(textView: TextView, @StyleRes resId: Int) {
//        if (Build.VERSION.SDK_INT < 23) {
//            textView.setTextAppearance(textView.context, resId)
//        } else {
//            textView.setTextAppearance(resId)
//        }
//    }

    fun setBorder(
        context: Context,
        view: View,
        @DimenRes widthId: Int,
        @ColorRes colorId: Int,
        @DimenRes radiusId: Int
    ) {
        val width = context.resources.getDimensionPixelOffset(widthId)
        val color = ContextCompat.getColor(context, colorId)
        val background = ContextCompat.getColor(context, R.color.white)
        val border = GradientDrawable()
        border.setStroke(width, color)
        border.setColor(background)
        if (radiusId != -1) {
            val radius = context.resources.getDimension(radiusId)
            border.cornerRadius = radius
        }
        view.background = border
    }

    fun getDrawable(
        context: Context,
        @DrawableRes drawableId: Int
    ): Drawable {
        return context.resources.getDrawable(drawableId, null)
    }

    fun getRealHeight(view: View?): Int {
        if (view != null) {
            view.measure(0, 0)
            return view.measuredHeight
        }
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    fun getDrawableBox(
        context: Context,
        color: Int,
        strokeColor: Int,
        strokeDimension: Int
    ): GradientDrawable {
        val background = GradientDrawable()
        background.shape = GradientDrawable.RECTANGLE
        if (strokeColor != NO_RESOURCE) {
            background.setStroke(
                context.resources.getDimensionPixelSize(strokeDimension),
                ContextCompat.getColor(context, strokeColor)
            )
        }
        if (color != NO_RESOURCE) {
            background.setColor(ContextCompat.getColor(context, color))
        }
        return background
    }

    fun makeStateListDrawable(
        selectedDrawable: Drawable?,
        normalDrawable: Drawable?
    ): StateListDrawable {
        val backgroundStates =
            StateListDrawable()
        backgroundStates.addState(intArrayOf(R.attr.state_selected), selectedDrawable)
        backgroundStates.addState(intArrayOf(), normalDrawable)
        return backgroundStates
    }

//    /**
//     * Compat method to retrieve an Html Spanned
//     *
//     * @param text The given text
//     *
//     * @return a Html [Spanned]
//     */
//    fun getHtmlText(text: String?): Spanned {
//        var htmlDescription: Spanned = if (AndroidVersionUtil.isNougatOrAbove()) {
//            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
//        } else {
//            Html.fromHtml(text)
//        }
//        val descriptionWithOutExtraSpace =
//            htmlDescription.toString().trim { it <= ' ' }
//        htmlDescription =
//            htmlDescription.subSequence(0, descriptionWithOutExtraSpace.length) as Spanned
//        return htmlDescription
//    }

//    /**
//     * Compat method to retrieve a formatted Html as String
//     *
//     * @param text The given text
//     *
//     * @return a formatted Html [String]
//     */
//    fun toStringHtml(text: Spanned?): String {
//        return if (AndroidVersionUtil.isNougatOrAbove()) {
//            Html.toHtml(text, Html.FROM_HTML_MODE_LEGACY)
//        } else {
//            Html.toHtml(text)
//        }
//    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Int {
        val resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    /**
     * Changes the image ratio after the image is loaded. For now, it is 16:9.
     */
    fun setHeroAspectRatio(view: View) {
        view.post {
            val width = view.width
            view.layoutParams.height =
                width * HERO_IMAGE_HEIGHT_RATIO / HERO_IMAGE_WIDTH_RATIO
            view.requestLayout()
        }
    }

    /**
     * Determines if an specific view is visible in the screen region.
     * @param view View to determine if it is visible.
     * @param context Context
     * @param percentage percentage of the View being shown to consider is visible.
     * @return
     */
    fun isViewVisibleInScreen(
        view: View,
        context: Context,
        percentage: Int
    ): Boolean {
//        Preconditions.checkArgument(
//            0 <= percentage && percentage <= 100,
//            "Percentage value should range 0-100"
//        )
        val displayMetrics = context.resources.displayMetrics
        val windowHeight =
            (view.context as AppCompatActivity).findViewById<View>(R.id.content)
                .measuredHeight

        //Offset between window height and screen height.
        val offsetHeight = displayMetrics.heightPixels - windowHeight
        val location = IntArray(2)
        view.getLocationInWindow(location)

        //We adjust the Y position, because it is relative to the screen.
        val viewY = location[1] - offsetHeight
        val viewHeight = view.measuredHeight
        val visibleHeight: Int
        visibleHeight = if (viewY <= 0) {
            viewY + viewHeight
        } else {
            windowHeight - viewY
        }
        return visibleHeight >= viewHeight * percentage / 100.0f
    }

    /**
     * Extract an Activity from the activity Context.
     *
     * @param context Activity Context.
     * @return AppCompactActivity.
     */
    fun extractActivityFromContext(context: Context?): AppCompatActivity {
        val appCompatActivity: AppCompatActivity = if (context is AppCompatActivity) {
            context
        } else if (context is ContextWrapper && context.baseContext is AppCompatActivity) {
            context.baseContext as AppCompatActivity
        } else {
            throw IllegalArgumentException("The context should be the Context from an activity")
        }
        return appCompatActivity
    }

    /**
     * Get the screen width in pixels
     *
     * @param context
     * @return screen width
     */
    fun getScreenWidth(context: Context?): Int {
        val windowManager =
            context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    /**
     * Get coordinates (x, y) for a specific view on the screen
     * @param view the view to get the coordinates
     * @return an array of two integers, position 0 for x and position 1 for y
     */
    fun getViewCoordinatesOnScreen(view: View): IntArray {
        val viewCoordinates = IntArray(2)
        view.getLocationOnScreen(viewCoordinates)
        return viewCoordinates
    }

//    /**
//     * This method converts dp unit to equivalent pixels, depending on device density.
//     *
//     * @param context Context to get resources and device specific display metrics
//     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
//     * @return A float value to represent px equivalent to dp depending on device density
//     */
//    fun dpToPixels(dp: Int, context: Context): Float {
//        return TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            1.0f,
//            context.resources.displayMetrics
//        )
//    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    fun pixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}
