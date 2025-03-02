package com.sddrozdov.doska.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.sddrozdov.doska.R
import com.sddrozdov.doska.databinding.ImageListFragmentBinding
import com.sddrozdov.doska.models.SelectImageItem
import com.sddrozdov.doska.recyclerViewAdapters.RcViewSelectImageAdapter
import com.sddrozdov.doska.utilites.ItemTouchMoveCallback

class ImageListFragment(private val onFragmentCloseInterface: FragmentCloseInterface,private val newList : ArrayList<String>) : Fragment() {

    private val binding: ImageListFragmentBinding? = null

    private val adapter  = RcViewSelectImageAdapter()

    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val touchHelper = ItemTouchHelper(dragCallback)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_list_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding?.recyclerViewImageItem

        touchHelper.attachToRecyclerView(recyclerView)

        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter

        val updateListTemp = ArrayList<SelectImageItem>()
        for (i in 0 until newList.size){
            updateListTemp.add(SelectImageItem(i.toString(),newList[i]))
        }
        adapter.updateAdapter(updateListTemp)

        val bBack = binding?.ImageFragmentBack
        bBack?.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

        }
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentCloseInterface.onFragClose()
    }

}