package com.wifiqrcode.qrcodescannerbarcodescanner.Camera_Capture

import android.app.SearchManager
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.wifiqrcode.qrcodescannerbarcodescanner.R
import kotlinx.android.synthetic.main.activity_scanner_result.*


class Scanner_Result : AppCompatActivity() {
    lateinit var scanner: BarcodeScanner
    lateinit var temp_text: String
    lateinit var imageUri: Uri
    val PICTURE_RESULT = 1
    lateinit var values: ContentValues

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_result)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#6CC5AC")))

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.Default)
        }







        res_tv.hint = "No result found"
        type.text = ""

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        scanner = BarcodeScanning.getClient(options)

        values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "QR scanner")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")

        imageUri = this.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )!!

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, PICTURE_RESULT)


        rcopy_btn.setOnClickListener {
            if (res_tv.text.isEmpty()) {
                Toast.makeText(this, "no text found", Toast.LENGTH_SHORT).show()
            } else {

                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val data = ClipData.newPlainText("Text", res_tv.text.toString())
                clipboard.setPrimaryClip(data)
                Toast.makeText(this, "Coped", Toast.LENGTH_SHORT).show()
            }
        }

        rshare_btn.setOnClickListener {
            if (res_tv.text.isEmpty()) {
                Toast.makeText(this, "no text found", Toast.LENGTH_SHORT).show()
            } else {

                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_TEXT, res_tv.text)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))

            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK)
            try {
                scan()

            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    fun scan() {

        val inputImage = InputImage.fromFilePath(this, imageUri)
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue

                    when (barcode.valueType) {
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                            temp_text = "Name:  $ssid \n Password:  $password"

                            res_tv.visibility = View.VISIBLE
                            res_tv.setText(temp_text)

                        }

                        Barcode.TYPE_URL -> {
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url
                            if (title != "") {
                                Toast.makeText(this, "$title", Toast.LENGTH_SHORT).show()
                            }
                            temp_text = "$title\n$url"

                            res_tv.visibility = View.VISIBLE
                            res_tv.setText(temp_text)
                            val intent = Intent(Intent.ACTION_WEB_SEARCH)
                            intent.putExtra(SearchManager.QUERY, url.toString())
                            startActivity(intent)

                        }
                        Barcode.TYPE_EMAIL -> {
                            val email = barcode.email

                            val address = "${email?.address}"
                            val body = "${email?.body}"
                            val subject = "${email?.subject}"

                            temp_text = "$address\n$body\n$subject"
                            res_tv.visibility = View.VISIBLE
                            res_tv.setText(temp_text)
                        }
                        Barcode.TYPE_CONTACT_INFO -> {
                            val type_contect = barcode.contactInfo
                            val title = type_contect?.title
                            val organization = type_contect?.organization
                            val name = "${type_contect?.name?.first} ${type_contect?.name?.last} "
                            val phone = "${type_contect?.phones?.get(0)?.number}"

                            temp_text = "$title \n $organization \n $name \n $phone"

                            res_tv.visibility = View.VISIBLE
                            res_tv.setText(temp_text)

                        }

                        else -> {
                            temp_text = "$rawValue"
                            res_tv.visibility = View.VISIBLE
                            res_tv.setText(temp_text)
                        }
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show()
            }
    }

}