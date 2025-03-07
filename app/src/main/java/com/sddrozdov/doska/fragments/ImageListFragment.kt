package com.sddrozdov.doska.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.components.ComponentRuntime
import com.sddrozdov.doska.R
import com.sddrozdov.doska.act.EditAdsActivity
import com.sddrozdov.doska.databinding.ImageListFragmentBinding
import com.sddrozdov.doska.recyclerViewAdapters.SelectImageAdapterInFragment
import com.sddrozdov.doska.utilites.ImageManager
import com.sddrozdov.doska.utilites.ImagePicker
import com.sddrozdov.doska.utilites.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
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

        if (newList != null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                val bitmapList = ImageManager.imageResize(newList,activity as Activity) //TODO()
                adapter.updateAdapter(bitmapList, true)
            }
        }
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
//        job = CoroutineScope(Dispatchers.Main).launch {
//            val bitmapList = ImageManager.imageResize(newList) //TODO()
//            adapter.updateAdapter(bitmapList, false)
//        }
        resizeSelectedImages(newList, false)
    }

    fun setSingleImage(uri: Uri, position: Int) {

        job = CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.imageResize(arrayListOf(uri), activity as Activity)
            adapter.mainArray[position] = bitmapList[0]
            adapter.notifyDataSetChanged()
        }
    }

    private fun resizeSelectedImages(newList: ArrayList<Uri>, needClear: Boolean) {
        job = CoroutineScope(Dispatchers.Main).launch {
            // val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList, activity as Activity)
            // dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            // if(adapter.mainArray.size > 2) addImageItem?.isVisible = false
        }
    }


}