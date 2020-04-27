package com.example.barcodenew

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Point
import android.os.Bundle
import android.util.AndroidRuntimeException
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec.getSize
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MyActivity"

class MainActivity : AppCompatActivity() {

    private val spinnerItems = arrayOf(
        // barcode list
        "QR",
        "JAN8",
        "JAN13",
        "EAN8(Source Marking)",
        "EAN8(Instore Marking Non-PLU)",
        "EAN8(Instore Marking PLU)",
        "EAN13",
        "UPC-A(Source Marking)",            // NS=0
        "UPC-A(Instore Marking Non-PLU)",   // NS=2
        "UPC-A(Source Marking NDC,NHRIC)",  // NS=3
        "UPC-A(Source Marking PLU)",        // NS=4
        "UPC-A(Source Marking Coupon)",     // NS=5
        "UPC-E",
        "NW7",
        "Code39",
        "Code128",
        "GS1 Databar(Omnidirectional)",
        "GS1 Databar(Stacked)",
        "GS1 Databar(StackedOmnidirectional)",
        "GS1 Databar Limited(Limited)",
        "GS1 Databar Expanded(Expanded)",
        "GS1 Databar Expanded(ExpandedStacked)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun getDisplayAreaSize(array: MutableList<Int>) {
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            array.set(0,dm.widthPixels)
            array.set(1,dm.heightPixels)
            Log.d("TAG", "[width,height]=$array")
        }

        //getDisplayAreaSize(this.applicationContext)
        fun setTitleView(){
            val array = mutableListOf(0, 0)
            getDisplayAreaSize(array)
            val width = array.get(0)
            val height = array.get(1)
            Log.d(TAG, "width=$width, height=$height")

            val left = width / 40;
            val top = height / 100;
            val right = (width - ((width / 2) + left))
            val bottom = (height - ((height / 60) + top));
            Log.d(TAG, "left=$left, top=$top, right=$right, bottom=$bottom")
            //val text: Array<TextView?> = arrayOfNulls(1)
            //textView2.layout(left, top, right, bottom)

        }
        // Titleを設定する
        setTitleView()


        val button = findViewById<Button>(R.id.button)
        val editText = findViewById<EditText>(R.id.editText)
        val spinnerItemList = Array<String>(1,{""})

        // ArrayAdapter
        val adapter = ArrayAdapter(applicationContext,
            android.R.layout.simple_spinner_item, spinnerItems)

        // spinner に adapter をセット
        // Kotlin Android Extensions
        spinner.adapter = adapter

        // リスナーを登録
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            val str0 = textView.text
            // アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View?, position: Int, id: Long) {
                imageView.setImageDrawable(null)
                imageView2.setImageDrawable(null)
                val spinnerParent = parent as Spinner
                spinnerItemList[0] = spinnerParent.selectedItem as String
                val str1: String = ":"
                textView.text = spinnerItemList[0] + "" + str1 + "" + str0
            }
            // アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }

        fun createQR(){
            //QRコード化する文字列
            val data = editText.text.toString()
            //QRコード画像の大きさを指定(pixel)
            val size = 500


            try {
                val barcodeEncoder = BarcodeEncoder()
                //QRコードをBitmapで作成
                val bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, size, size)

                //作成したQRコードを画面上に配置
                val imageViewQrCode = findViewById<View>(R.id.imageView) as ImageView
                imageViewQrCode.setImageBitmap(bitmap)

            } catch (e: WriterException) {
                throw AndroidRuntimeException("Barcode Error.", e)
            }
        }

        button.setOnClickListener {

            when {
                spinnerItemList[0] == "QR" ->{
                    createQR()
                }
                spinnerItemList[0] == "NW7" ->{
                	// TODO 関数化
                    val imageViewBarcodeCode = findViewById<View>(R.id.imageView2) as ImageView
                    // バーコードの各種設定
                    val targetData = "ABCD390123456" //バーコードに変換する対象データ

                    val width = 1000 //作成するバーコードの幅

                    val height = 200

                    // データ変換用クラスをインスタンス化する
                    // 変更点1
                    // ・CodaBarWriter -> MyCodaBarWriter

                    // データ変換用クラスをインスタンス化する
                    // 変更点1
                    // ・CodaBarWriter -> MyCodaBarWriter
                    val writer = MyCodaBarWriter()

                    try {

                        // 対象データを変換する
                        // 変更点2
                        // ・BitMatrix -> MyBitMatrix
                        // ・引数の数が少なくなった
                        val bitMatrix: MyBitMatrix = writer.encode(targetData, width, height)


                        // BitMatrixのデータが「true」の時は「黒」を設定し、「false」の時は「白」を設定する
                        val pixels = IntArray(width * height)
                        for (y in 0 until height) {
                            val offset = y * width
                            for (x in 0 until width) {
                                pixels[offset + x] =
                                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                            }
                        }

                        // ビットマップ形式に変換する
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

                        //ビットマップの回転(縦に表示するため)
                        val mat = Matrix()
                        val fval = 180.0F
                        mat.postRotate(fval)
                        val bmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, mat, true)

                        // イメージビューに表示する
                        imageViewBarcodeCode.setImageBitmap(bmp)
                    } catch (e: Exception) {
                    	//
                    }
                }
            }
        }
    }

}
