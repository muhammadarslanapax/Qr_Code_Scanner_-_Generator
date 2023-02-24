package com.wifiqrcode.qrcodescannerbarcodescanner.GalleryImport

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.wifiqrcode.qrcodescannerbarcodescanner.R
import kotlinx.android.synthetic.main.fragment_import.*
import java.util.*


@Suppress("DEPRECATION")
class ImportFragment : Fragment() {
    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var scanner: BarcodeScanner
    var temp_text: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_import, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et.visibility = View.GONE


        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        scanner = BarcodeScanning.getClient(options)


        import_galleryBtn.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }


        scan_galleryBtn.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Please wait...")
            builder.setMessage("loadiing")
            val game_message = builder.create()
            game_message.show()



            Handler().postDelayed({

                game_message.dismiss()


                if (imageUri == null) {
                    Toast.makeText(context, "import from gallery", Toast.LENGTH_SHORT).show()
                } else {
                    val inputImage = InputImage.fromFilePath(requireContext(), imageUri!!)
                    scanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                val rawValue = barcode.rawValue

                                when (barcode.valueType) {
                                    Barcode.TYPE_WIFI -> {
                                        val ssid = barcode.wifi!!.ssid
                                        val password = barcode.wifi!!.password
                                        val type = barcode.wifi!!.encryptionType
                                        temp_text =
                                            "Name:  $ssid \n \nPassword: $password \n \nEncryption: $type"
                                           et.visibility = View.VISIBLE

                                        et.setText(temp_text)
                                    }

                                    Barcode.TYPE_URL -> {
                                        val title = barcode.url!!.title
                                        val url = barcode.url!!.url
                                        if (title != "") {
                                            Toast.makeText(context, "$title", Toast.LENGTH_SHORT).show()
                                        }
                                          et.visibility = View.VISIBLE
                                        et.setText("$title\n $url")
                                        val intent = Intent(Intent.ACTION_WEB_SEARCH)
                                        intent.putExtra(SearchManager.QUERY, url.toString())
                                        startActivity(intent)


                                    }
                                    Barcode.TYPE_CONTACT_INFO -> {
                                        val type_contect = barcode.contactInfo
                                        val title = type_contect?.title
                                        val organization = type_contect?.organization
                                        val name =
                                            "${type_contect?.name?.first} ${type_contect?.name?.last} "
                                        val phone = "${type_contect?.phones?.get(0)?.number}"

                                        val txt = "$title \n $organization \n $name \n $phone"
                                          et.visibility = View.VISIBLE
                                        et.setText(txt)

                                    }

                                    else -> {
                                        temp_text = "$rawValue"
                                          et.visibility = View.VISIBLE
                                        et.setText(temp_text)
                                    }
                                }

                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "no result found", Toast.LENGTH_SHORT).show()
                        }
                }


            },1000)

        }


    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data

            gallery_imageview.setImageURI(imageUri)
            gallery_imageview.setPadding(10, 10, 10, 10)

        }
    }

}