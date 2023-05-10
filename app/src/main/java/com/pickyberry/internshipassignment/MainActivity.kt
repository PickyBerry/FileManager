package com.pickyberry.internshipassignment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pickyberry.internshipassignment.databinding.ActivityMainBinding
import com.pickyberry.internshipassignment.di.DaggerDbComponent
import com.pickyberry.internshipassignment.di.DaggerRepositoryComponent
import com.pickyberry.internshipassignment.di.DbComponent
import com.pickyberry.internshipassignment.di.RepositoryComponent


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //permissions
    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    //Dagger2 components
    lateinit var dbComponent: DbComponent
    lateinit var repositoryComponent: RepositoryComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing dagger components
        dbComponent = DaggerDbComponent.builder().application(application).build()
        repositoryComponent = DaggerRepositoryComponent.builder().application(application).dbComponent(dbComponent).build()

        //Checking permissions
        if (!allPermissionsGranted())
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }


    private fun allPermissionsGranted(): Boolean {

        //Permission to manage all files. Without this there will be a lot less files available for the app!
        if (!Environment.isExternalStorageManager())
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                )
            )

        //Check for usual permissions
        for (permission in REQUIRED_PERMISSIONS)
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        return true
    }

    //Re-asking for permissions if not granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.no_permissions,
                    Toast.LENGTH_SHORT
                ).show()
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }
    }

}