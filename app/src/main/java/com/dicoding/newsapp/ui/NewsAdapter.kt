package com.dicoding.newsapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.newsapp.R
import com.dicoding.newsapp.data.local.entity.NewsEntity
import com.dicoding.newsapp.databinding.ItemNewsBinding
import com.dicoding.newsapp.ui.NewsAdapter.MyViewHolder
import com.dicoding.newsapp.utils.DateFormatter

// Adapter untuk RecyclerView yang menampilkan daftar berita
class NewsAdapter(private val onBookmarkClick: (NewsEntity) -> Unit) : ListAdapter<NewsEntity, MyViewHolder>(DIFF_CALLBACK) {

    // Membuat ViewHolder baru ketika RecyclerView membutuhkannya
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // Mengikat data berita ke ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)

        val ivBookmark = holder.binding.ivBookmark
        // Mengatur ikon bookmark berdasarkan status bookmark berita
        if (news.isBookmarked) {
            ivBookmark.setImageDrawable(ContextCompat.getDrawable(ivBookmark.context, R.drawable.ic_bookmarked_white))
        } else {
            ivBookmark.setImageDrawable(ContextCompat.getDrawable(ivBookmark.context, R.drawable.ic_bookmark_white))
        }
        // Menangani klik pada ikon bookmark
        ivBookmark.setOnClickListener {
            onBookmarkClick(news)
        }
    }

    // ViewHolder untuk item berita
    class MyViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        // Mengikat data berita ke tampilan
        fun bind(news: NewsEntity) {
            binding.tvItemTitle.text = news.title
            binding.tvItemPublishedDate.text = DateFormatter.formatDate(news.publishedAt)
            // Memuat gambar berita menggunakan Glide
            Glide.with(itemView.context)
                .load(news.urlToImage)
                // Menerapkan opsi permintaan, termasuk placeholder (gambar sementara saat gambar asli dimuat) dan gambar error (gambar yang ditampilkan jika terjadi kesalahan saat memuat gambar).
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(binding.imgPoster)
            // Menangani klik pada item berita untuk membuka URL berita di browser
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(news.url)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        // Callback untuk menghitung perbedaan antara dua daftar berita
        val DIFF_CALLBACK: DiffUtil.ItemCallback<NewsEntity> =
            object : DiffUtil.ItemCallback<NewsEntity>() {
                // Memeriksa apakah dua item berita sama berdasarkan judulnya
                override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                    return oldItem.title == newItem.title
                }

                // Memeriksa apakah konten dari dua item berita sama
                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
