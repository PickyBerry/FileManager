package com.pickyberry.internshipassignment.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pickyberry.internshipassignment.R
import com.pickyberry.internshipassignment.domain.SortTypes
import com.pickyberry.internshipassignment.databinding.FragmentFileListBinding
import kotlinx.coroutines.launch


class FileListFragment : Fragment() {

    private lateinit var binding: FragmentFileListBinding
    private val viewModel = FileListViewModel()
    private lateinit var spinnerAdapter: ArrayAdapter<CharSequence>
    private lateinit var recyclerFilesAdapter: FilesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileListBinding.inflate(layoutInflater)
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.sortSpinner.visibility = View.GONE
                binding.btnSwitch.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.sortSpinner.visibility = View.VISIBLE
                binding.btnSwitch.visibility = View.VISIBLE
            }
        }
        setupRecycler()
        setupSpinner()
        viewModel.currentFiles.observe(viewLifecycleOwner) {
            recyclerFilesAdapter.setData(it)
        }


        binding.btnSwitch.setOnClickListener {
            viewModel.switchBetweenAllAndUpdated()
            binding.btnSwitch.text =
                if (viewModel.showingUpdatedFiles) resources.getString(R.string.see_all)
                else resources.getString(R.string.see_updated)
        }
        return binding.root
    }


    private fun setupRecycler() {
        recyclerFilesAdapter = FilesAdapter(requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = recyclerFilesAdapter
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun setupSpinner() {
        spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.sorts, R.layout.custom_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.sortSpinner.adapter = spinnerAdapter
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View?, selectedItemPosition: Int, selectedId: Long,
            ) {
                lifecycleScope.launch {
                  //  viewModel.sort(SortTypes.from(selectedItemPosition)!!)
                    viewModel.sort(SortTypes.from(selectedItemPosition)!!)
                }
                binding.recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    binding.recyclerView.scrollToPosition(0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

}