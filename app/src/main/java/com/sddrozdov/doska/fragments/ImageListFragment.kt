package com.sddrozdov.doska.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.sddrozdov.doska.R
import com.sddrozdov.doska.act.EditAdsActivity
import com.sddrozdov.doska.databinding.ImageListFragmentBinding
import com.sddrozdov.doska.dialogs.ProgressDialog
import com.sddrozdov.doska.recyclerViewAdapters.SelectImageAdapterInFragment
import com.sddrozdov.doska.utilites.ImageManager
import com.sddrozdov.doska.utilites.ImagePicker
import com.sddrozdov.doska.utilites.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(
    private val onFragmentCloseInterface: FragmentCloseInterface,
    private val newList: ArrayList<Uri>?
) : Fragment() {

    private lateinit var binding: ImageListFragmentBinding

    private val adapter = SelectImageAdapterInFragment()

    private val dragCallback = ItemTouchMoveCallback(adapter)

    private val touchHelper = ItemTouchHelper(dragCallback)

    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ImageListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        touchHelper.attachToRecyclerView(binding.recyclerViewImageItem)

        binding.recyclerViewImageItem.layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewImageItem.adapter = adapter

        if (newList != null) resizeSelectedImages(newList, true)
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    private fun setUpToolbar() {
        binding.imageListFragmentToolBar.inflateMenu(R.menu.menu_choose_image)
        val deleteItem = binding.imageListFragmentToolBar.menu.findItem(R.id.delete_image)
        val addItem = binding.imageListFragmentToolBar.menu.findItem(R.id.add_image)

        binding.imageListFragmentToolBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        deleteItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)

            true
        }
        addItem.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.launcher(activity as EditAdsActivity, imageCount)
            true
        }
    }

    fun updateAdapter(newList: ArrayList<Uri>) {
        resizeSelectedImages(newList, false)
    }

    fun setSingleImage(uri: Uri, position: Int) {

        val progressBar = binding.recyclerViewImageItem[position].findViewById<ProgressBar>(R.id.ProgressBarInSingleItem)

        job = CoroutineScope(Dispatchers.Main).launch {
            progressBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(arrayListOf(uri), activity as Activity)
            progressBar.visibility = View.GONE
            adapter.mainArray[position] = bitmapList[0]
            adapter.notifyItemChanged(position)
        }
    }

    private fun resizeSelectedImages(newList: ArrayList<Uri>, needClear: Boolean) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList, activity as Activity)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            //if(adapter.mainArray.size > 2) addImageItem?.isVisible = false
        }
    }
}