package com.sddrozdov.doska.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.components.ComponentRuntime
import com.sddrozdov.doska.R
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
    private val newList: ArrayList<String>
) : Fragment() {

    private lateinit var binding: ImageListFragmentBinding

    private val adapter = SelectImageAdapterInFragment()

    private val dragCallback = ItemTouchMoveCallback(adapter)

    private val touchHelper = ItemTouchHelper(dragCallback)

    private lateinit var job: Job

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

        job = CoroutineScope(Dispatchers.Main).launch {
            //ImageManager.imageResize(newList) //TODO()
        }

        //adapter.updateAdapter(newList, true)
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentCloseInterface.onFragClose(adapter.mainArray)
        job.cancel()
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
            // ImagePicker.getImages(activity as AppCompatActivity , 9) TODO
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>) {
        adapter.updateAdapter(newList, false)
    }

    fun setSingleImage(uri: String, position: Int) {
//        adapter.mainArray[position] = uri
//        adapter.notifyDataSetChanged()
    }
}