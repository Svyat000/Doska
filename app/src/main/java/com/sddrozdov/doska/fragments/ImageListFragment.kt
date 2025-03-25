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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.sddrozdov.doska.R
import com.sddrozdov.doska.act.EditAdsActivity
import com.sddrozdov.doska.databinding.ImageListFragmentBinding
import com.sddrozdov.doska.dialogs.ProgressDialog
import com.sddrozdov.doska.interfaces.AdapterCallback
import com.sddrozdov.doska.interfaces.FragmentCloseInterface
import com.sddrozdov.doska.recyclerViewAdapters.SelectImageAdapterInFragment
import com.sddrozdov.doska.utilites.ImageManager
import com.sddrozdov.doska.utilites.ImagePicker
import com.sddrozdov.doska.utilites.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(
    private val onFragmentCloseInterface: FragmentCloseInterface
) : BaseAdsFragment(), AdapterCallback {

    private lateinit var binding: ImageListFragmentBinding

    private val adapter = SelectImageAdapterInFragment(this)

    private val dragCallback = ItemTouchMoveCallback(adapter)

    private val touchHelper = ItemTouchHelper(dragCallback)

    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImageListFragmentBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()

        binding.apply {
            touchHelper.attachToRecyclerView(recyclerViewImageItem)
            recyclerViewImageItem.layoutManager = LinearLayoutManager(activity)
            recyclerViewImageItem.adapter = adapter
        }
        //if (newList != null) resizeSelectedImages(newList, true)
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

//    override fun onDetach() {
//        super.onDetach()
//
//    }



    private fun setUpToolbar() {

        binding.apply {
            imageListFragmentToolBar.inflateMenu(R.menu.menu_choose_image)
            imageListFragmentToolBar.apply {
                setNavigationOnClickListener {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.remove(this@ImageListFragment)
                        ?.commit()
                    onFragmentCloseInterface.onFragClose(adapter.mainArray)
                    job?.cancel()
                }
                menu.findItem(R.id.delete_image)
                    .setOnMenuItemClickListener {
                        adapter.updateAdapter(ArrayList(), true)
                        imageListFragmentToolBar.menu.findItem(R.id.add_image).isVisible = true
                        true
                    }
                if (adapter.mainArray.size > 2) binding.imageListFragmentToolBar.menu.findItem(R.id.add_image).isVisible =
                    false
                menu.findItem(R.id.add_image).setOnMenuItemClickListener {
                    val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
                    ImagePicker.addImages(activity as EditAdsActivity, imageCount)
                    true
                }
            }
        }

    }

    fun updateAdapter(newList: ArrayList<Uri>, activity: Activity) {
        resizeSelectedImages(newList, false, activity)
    }

    fun setSingleImage(uri: Uri, position: Int) {

        val progressBar =
            binding.recyclerViewImageItem[position].findViewById<ProgressBar>(R.id.ProgressBarInSingleItem)

        job = CoroutineScope(Dispatchers.Main).launch {
            progressBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(arrayListOf(uri), activity as Activity)
            progressBar.visibility = View.GONE
            adapter.mainArray[position] = bitmapList[0]
            adapter.notifyItemChanged(position)
        }
    }

    fun resizeSelectedImages(newList: ArrayList<Uri>, needClear: Boolean, activity: Activity) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity)
            val bitmapList = ImageManager.imageResize(newList, activity)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainArray.size > 2) binding.imageListFragmentToolBar.menu.findItem(R.id.add_image).isVisible =
                false
        }
    }

    override fun onItemDelete() {
        binding.imageListFragmentToolBar.menu.findItem(R.id.add_image).isVisible = true
    }


}