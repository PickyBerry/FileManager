package com.pickyberry.internshipassignment.presentation

import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pickyberry.internshipassignment.R
import com.pickyberry.internshipassignment.SortTypes
import com.pickyberry.internshipassignment.data.RepositoryImpl
import com.pickyberry.internshipassignment.databinding.ItemFileBinding
import com.pickyberry.internshipassignment.domain.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

class FilesAdapter() :
    RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {


    //Using DiffUtils
    private val differCallback = object : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
    var sortedBy = SortTypes.NAMES_ASC

    init {
        sort(SortTypes.NAMES_ASC)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFileBinding.inflate(inflater, parent, false)
        return FileViewHolder(binding)
    }

    //On binding of the view holder,
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileItem = differ.currentList[position]
        val binding = holder.binding
        holder.itemView.apply {
            binding.name.text = fileItem.name
            binding.size.text = FileUtils.byteCountToDisplaySize(fileItem.size).toString()
            binding.date.text = fileItem.creationDate.toString()
            binding.icon.setImageResource(iconFromFileExtension(fileItem.name.split('.').last()))
        }
    }

    fun sort(type: SortTypes) {
        if (differ.currentList.isEmpty()){
            val repositoryImpl = RepositoryImpl()
            val list = mutableListOf<FileItem>()
            GlobalScope.launch(Dispatchers.IO) {
                repositoryImpl.getAllFiles(
                    File(Environment.getExternalStorageDirectory().absolutePath),
                    list
                )
                withContext(Dispatchers.Main) {
                    differ.submitList(list)
                    notifyDataSetChanged()
                    sortedBy = type
                }
            }
        } else {
            val list = differ.currentList.toMutableList()
            if (shouldReverse(type)) list.reverse()
            else when (type) {
                SortTypes.NAMES_ASC -> Collections.sort(list, FileItem.sortNamesAscending())
                SortTypes.NAMES_DESC -> Collections.sort(list, FileItem.sortNamesDescending())
                SortTypes.SIZE_ASC -> Collections.sort(list, FileItem.sortSizesAscending())
                SortTypes.SIZE_DESC -> Collections.sort(list, FileItem.sortSizesDescending())
                SortTypes.DATE_ASC -> Collections.sort(list, FileItem.sortDatesAscending())
                SortTypes.DATE_DESC -> Collections.sort(list, FileItem.sortDatesDescending())
                SortTypes.EXT_ASC -> Collections.sort(list, FileItem.sortExtensionssAscending())
                SortTypes.EXT_DESC -> Collections.sort(list, FileItem.sortExtensionsDescending())
            }
            differ.submitList(list)
            notifyDataSetChanged()
            sortedBy = type
        }
    }

    private fun shouldReverse(type: SortTypes): Boolean {
        return ((type == SortTypes.NAMES_ASC && sortedBy == SortTypes.NAMES_DESC) ||
                (type == SortTypes.NAMES_DESC && sortedBy == SortTypes.NAMES_ASC) ||
                (type == SortTypes.SIZE_ASC && sortedBy == SortTypes.SIZE_DESC) ||
                (type == SortTypes.SIZE_DESC && sortedBy == SortTypes.SIZE_ASC) ||
                (type == SortTypes.DATE_ASC && sortedBy == SortTypes.DATE_DESC) ||
                (type == SortTypes.DATE_DESC && sortedBy == SortTypes.DATE_ASC) ||
                (type == SortTypes.EXT_ASC && sortedBy == SortTypes.EXT_DESC) ||
                (type == SortTypes.EXT_DESC && sortedBy == SortTypes.EXT_ASC))
    }

    override fun getItemCount() = differ.currentList.size


    class FileViewHolder(
        val binding: ItemFileBinding,
    ) : RecyclerView.ViewHolder(binding.root)


    fun iconFromFileExtension(extension: String): Int {
        val imageExtensions = setOf("jpg", "jpeg", "png", "bmp", "gif", "svg", "tiff")
        val textExtensions = setOf("txt", "doc", "docx", "rtf", "pdf")
        val audioExtensions = setOf("mp3", "wav", "ogg")
        val videoExtensions = setOf("mp4", "avi", "mov", "wmv", "mpeg")

        if (imageExtensions.contains(extension)) return R.drawable.image_icon
        else if (textExtensions.contains(extension)) return R.drawable.text_icon
        else if (audioExtensions.contains(extension)) return R.drawable.audio_icon
        else if (videoExtensions.contains(extension)) return R.drawable.video_icon
        else return R.drawable.file_icon
    }

}