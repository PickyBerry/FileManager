package com.pickyberry.internshipassignment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
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

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    //Setting up DI components for the project

    lateinit var dbComponent: DbComponent
    lateinit var repositoryComponent: RepositoryComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbComponent = DaggerDbComponent.builder().application(application).build()
        repositoryComponent = DaggerRepositoryComponent.builder().application(application).dbComponent(dbComponent).build()

        if (!allPermissionsGranted())
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)


        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        //Доступ ко всем файлам!
        /*
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                uri
            )
        ) */
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS)
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        return true
    }

    //Re-asking for permissions if not all are granted
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