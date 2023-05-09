package com.pickyberry.internshipassignment.presentation

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.pickyberry.internshipassignment.R
import com.pickyberry.internshipassignment.databinding.ItemFileBinding
import com.pickyberry.internshipassignment.domain.FileItem
import org.apache.commons.io.FileUtils
import java.io.File


class FilesAdapter(private val context: Context) :
    RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {

    val folderClicked = MutableLiveData<String>()
    var currentList = listOf<FileItem>()

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
        val fileItem = currentList[position]
        val binding = holder.binding
        holder.itemView.apply {
            binding.name.text = fileItem.path.split('/').last()
            binding.size.text = FileUtils.byteCountToDisplaySize(fileItem.size).toString()
            binding.date.text = fileItem.creationDate.toString()
            if (fileItem.isDirectory)
                binding.icon.setImageResource(R.drawable.folder_icon)
            else binding.icon.setImageResource(
                iconFromFileExtension(
                    fileItem.path.split('.').last()
                )
            )
        }
        holder.itemView.setOnClickListener {
            if (fileItem.isDirectory)
                folderClicked.postValue(fileItem.path)
            else {
                val path = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    File(fileItem.path)
                );
                context.grantUriPermission(
                    context.packageName,
                    path,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val type =
                    MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(fileItem.path.split('.').last())
                val intent = Intent(Intent.ACTION_VIEW)
                    .setDataAndType(path, type)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Can't open file!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.itemView.setOnLongClickListener {
            if (!fileItem.isDirectory) {
                shareFile(fileItem)
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false
        }
    }

    private fun shareFile(fileItem: FileItem) {
        val file = File(fileItem.path)
        val path = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        context.grantUriPermission(
            context.packageName,
            path,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sharingIntent.type =
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileItem.path.split('.').last())
        sharingIntent.putExtra(Intent.EXTRA_STREAM, path)
        sharingIntent.clipData = ClipData(
            "",
            arrayOf(
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileItem.path.split('.').last())
            ),
            ClipData.Item(path)
        )
        context.startActivity(Intent.createChooser(sharingIntent, "Share to..."))
    }


    fun setData(list: List<FileItem>) {
        currentList = list
        notifyDataSetChanged()
    }


    override fun getItemCount() = currentList.size


    class FileViewHolder(
        val binding: ItemFileBinding,
    ) : RecyclerView.ViewHolder(binding.root)


    private fun iconFromFileExtension(extension: String): Int {
        val imageExtensions = setOf("jpg", "jpeg", "png", "bmp", "gif", "svg", "tiff")
        val textExtensions = setOf("txt", "doc", "docx", "rtf", "pdf")
        val audioExtensions = setOf("mp3", "wav", "ogg")
        val videoExtensions = setOf("mp4", "avi", "mov", "wmv", "mpeg")

        return if (imageExtensions.contains(extension)) R.drawable.image_icon
        else if (textExtensions.contains(extension)) R.drawable.text_icon
        else if (audioExtensions.contains(extension)) R.drawable.audio_icon
        else if (videoExtensions.contains(extension)) R.drawable.video_icon
        else R.drawable.file_icon
    }

}