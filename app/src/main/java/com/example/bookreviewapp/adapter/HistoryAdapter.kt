package com.example.bookreviewapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookreviewapp.databinding.ItemBookBinding
import com.example.bookreviewapp.databinding.ItemHistoryBinding
import com.example.bookreviewapp.model.Book
import com.example.bookreviewapp.model.History

class HistoryAdapter(val historyDeleteClickedListener : (String)->Unit) : ListAdapter<History, HistoryAdapter.HistoryItemViewHolder>(diffUtil) {

    inner class HistoryItemViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root){// 미리 그려진 뷰

        fun bind(historyModel: History){
            binding.historyKeywordTextView.text = historyModel.keyword
            binding.historyKeywordDeleteButton.setOnClickListener {
                historyDeleteClickedListener(historyModel.keyword.orEmpty())
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder { //미리 만들어진 뷰 홀더가 없을 경우 새로 생성
        return HistoryItemViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) { //실제로 뷰 홀더가 뷰에 그려지게 됐을 때 데이터를 바인드해주는 함수
        holder.bind(currentList[position]) //데이터를 가져와서 bind함수를 통해서
    }

    // diffUtil 리사이클러뷰가 실제로 뷰에 포지션이 변경되었을 때 새로운 값을 할당할지말지 기준
    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<History>(){
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                //아이템이 같으냐?
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {

                //컨텐츠가 같으냐?
                return oldItem.keyword == newItem.keyword
            }

        }
    }

}