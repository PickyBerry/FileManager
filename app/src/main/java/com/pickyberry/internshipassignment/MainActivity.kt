package com.pickyberry.internshipassignment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pickyberry.internshipassignment.databinding.ActivityMainBinding
import com.pickyberry.internshipassignment.presentation.FilesAdapter
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private lateinit var spinnerAdapter: ArrayAdapter<CharSequence>
    private lateinit var recyclerFilesAdapter: FilesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted())
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        setupRecycler()
        setupSpinner()
    }

    private fun setupRecycler() {
        recyclerFilesAdapter = FilesAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = recyclerFilesAdapter
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun setupSpinner() {
        spinnerAdapter = ArrayAdapter.createFromResource(
            this as AppCompatActivity,
            R.array.sorts,
            R.layout.custom_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.sortSpinner.adapter = spinnerAdapter
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View?, selectedItemPosition: Int, selectedId: Long,
            ) {
                lifecycleScope.launch {
                    recyclerFilesAdapter.sort(SortTypes.from(selectedItemPosition)!!,null)
                }
                binding.recyclerView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    binding.recyclerView.scrollToPosition(0)
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
                    "no permission",
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