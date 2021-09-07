package com.example.bookreviewapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookreviewapp.databinding.ItemBookBinding
import com.example.bookreviewapp.model.Book


class BookAdapter : ListAdapter<Book,BookAdapter.BookItemViewHolder>(diffUtil) {

    inner class BookItemViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root){// 미리 그려진 뷰

        fun bind(bookModel: Book){
            binding.titleTextView.text = bookModel.title
            binding.descriptionTextView.text = bookModel.description

            Glide//글라이드를 통해 이미지 로딩
                .with(binding.coverImageView)
                .load(bookModel.coverSmallUrl)
                .into(binding.coverImageView) //서버에서 url이미지를 가져와서 추가

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder { //미리 만들어진 뷰 홀더가 없을 경우 새로 생성
        return BookItemViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) { //실제로 뷰 홀더가 뷰에 그려지게 됐을 때 데이터를 바인드해주는 함수
        holder.bind(currentList[position]) //데이터를 가져와서 bind함수를 통해서
    }

    // diffUtil 리사이클러뷰가 실제로 뷰에 포지션이 변경되었을 때 새로운 값을 할당할지말지 기준
    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<Book>(){
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                //아이템이 같으냐?
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {

                //컨텐츠가 같으냐?
                return oldItem.id == newItem.id
            }

        }
    }

}