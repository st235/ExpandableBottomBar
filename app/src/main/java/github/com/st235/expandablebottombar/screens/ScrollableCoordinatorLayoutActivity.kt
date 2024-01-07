package github.com.st235.expandablebottombar.screens

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import github.com.st235.expandablebottombar.R
import github.com.st235.expandablebottombar.databinding.ActivityScrollableCoordinatorLayoutBinding
import github.com.st235.expandablebottombar.databinding.ItemCatBinding

class ScrollableCoordinatorLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityScrollableCoordinatorLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            recyclerView.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = Adapter()
            }

            fab.setOnClickListener { view ->
                Snackbar.make(view, "Meow", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    internal fun loadBitmap(@DrawableRes id: Int, view: AppCompatImageView) {
        Picasso.get()
            .load(id)
            .into(view)
    }

    @WorkerThread
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private inner class ViewHolder(val binding: ItemCatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setResource(@DrawableRes id: Int) {
            loadBitmap(id, binding.itemIv)
        }
    }

    private inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        private val cats = listOf(
            R.drawable.cat1,
            R.drawable.cat2,
            R.drawable.cat3,
            R.drawable.cat4,
            R.drawable.cat5,
            R.drawable.cat6,
            R.drawable.cat7,
            R.drawable.cat8
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val ctx = parent.context
            val v = ItemCatBinding.inflate(LayoutInflater.from(ctx), parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int = 100

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val cat = cats[position % cats.size]
            holder.setResource(cat)
        }
    }
}
