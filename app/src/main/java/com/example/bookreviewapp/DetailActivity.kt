package com.example.bookreviewapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.bookreviewapp.databinding.ActivityDetailBinding
import com.example.bookreviewapp.model.Book
import com.example.bookreviewapp.model.Review

class DetailActivity: AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding    //뷰바인딩 사용
    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

       db = getAppDatabase(this)

        val model = intent.getParcelableExtra<Book>("bookModel")    //직렬화된 북 모델을 받아옴.

        binding.titleTextView.text = model?.title.orEmpty() //모델이 안 넘어와서 null일 가능성 체크
        binding.descriptionTextView.text = model?.description.orEmpty()

        Glide.with(binding.coverImageView.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        Thread{
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?:0)

            runOnUiThread{
                binding.reviewEditText.setText(review.review.orEmpty())
            }
        }.start()


        binding.saveButton.setOnClickListener {
            Thread{
                db.reviewDao().saveReview(Review(model?.id?.toInt() ?: 0,
                binding.reviewEditText.text.toString()))
            }.start()
        }



        
    }
}