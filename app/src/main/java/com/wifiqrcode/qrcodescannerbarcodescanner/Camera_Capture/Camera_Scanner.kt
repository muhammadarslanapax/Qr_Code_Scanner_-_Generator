package com.wifiqrcode.qrcodescannerbarcodescanner.Camera_Capture

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wifiqrcode.qrcodescannerbarcodescanner.R
import kotlinx.android.synthetic.main.fragment_camera__scanner.*


class Camera_Scanner : Fragment() {
    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_camera__scanner, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        scan_btn.setOnClickListener {

            startActivity(Intent(activity, Scanner_Result::class.java))

        }

    }

}