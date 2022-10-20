package onlab.mlkit.tiktok.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.absoluteValue
import kotlin.math.max

class RectOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private val transformationMatrix = Matrix()
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val STROKE_WIDTH = 3f // has to be float
    private val drawColor = Color.RED

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }
    private val paintGreen = Paint().apply {
        color = Color.GREEN
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(Color.TRANSPARENT)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    fun clear() {
        extraCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    internal fun drawLine(
        startLandmark: PoseLandmark?,
        endLandmark: PoseLandmark?
    ) {
        val start = startLandmark!!.position
        val end = endLandmark!!.position
        if(startLandmark.inFrameLikelihood >0.8 && endLandmark.inFrameLikelihood > 0.8){
            extraCanvas.drawLine(
                (width - (start.x)/0.5f+200), start.y/0.5f+200, (width - end.x/0.5f+200), end.y/0.5f+200, paintGreen
            )
        }


        invalidate()
    }

    internal fun drawCircle(landmark: PoseLandmark?) {

        if (landmark != null) {
            if (-width/2 < landmark.position3D.x && landmark.position3D.x < width/2 && landmark.position3D.y < height/2 && landmark.position3D.y > -height/2){
                if (landmark.inFrameLikelihood > 0.8){
                    var posX=landmark.position3D.x
                    var posY=landmark.position3D.y
                    extraCanvas.drawCircle(width-posX/0.50f+200,posY/0.50f+200,8.0f,paint)
                }
            }
        }
        invalidate()
    }
}



