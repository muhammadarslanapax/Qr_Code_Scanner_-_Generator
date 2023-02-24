package com.wifiqrcode.qrcodescannerbarcodescanner.GenerateCode

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.wifiqrcode.qrcodescannerbarcodescanner.R
import kotlinx.android.synthetic.main.fragment_generate_code.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class Generate_Code : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_generate_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gen_btn.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Please wait...")
            builder.setMessage("loadiing")
            val game_message = builder.create()
            game_message.show()


            Handler().postDelayed({

                game_message.dismiss()

                if (req_text.text.toString().isEmpty()) {
                    Toast.makeText(activity, "enter text", Toast.LENGTH_SHORT).show()
                } else {
                    val text = req_text.text.toString()
                    val encoder = BarcodeEncoder()
                    val bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
                    code_image.setImageBitmap(bitmap)
                    code_image.setPadding(5, 5, 5, 5)

                    save_btn.visibility = View.VISIBLE

                    save_btn.setOnClickListener {
                        if (save_btn.visibility == View.VISIBLE) {

                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1
                            )
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                1
                            )


                            val bitmap = getScreenShot(code_image)
                            if (bitmap != null) {
                                saveToGallery(bitmap)
                            }

                        }
                    }

                }

            },1000)


        }

    }

    private fun getScreenShot(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot Because:" + e.message)
        }
        return screenshot
    }

    private fun saveToGallery(bitmap: Bitmap) {

        val filename = "${System.currentTimeMillis()}.jpg"
        var output_Stream: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity?.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply {

                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                output_Stream = imageUri?.let { resolver.openOutputStream(it) }
            }

        } else {

            val imageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imageDir, filename)
            output_Stream = FileOutputStream(image)

        }

        output_Stream?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(activity, "saved", Toast.LENGTH_SHORT).show()
        }
    }

}