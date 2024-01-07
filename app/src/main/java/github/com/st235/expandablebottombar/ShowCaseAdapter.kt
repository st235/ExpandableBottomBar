package github.com.st235.expandablebottombar

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import github.com.st235.expandablebottombar.databinding.ContentShowcaseItemBinding

typealias OnItemClickListener = (info: ShowCaseInfo) -> Unit

class ShowCaseAdapter(
    private val items: List<ShowCaseInfo>,
    private val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<ShowCaseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val v = ContentShowcaseItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ContentShowcaseItemBinding) :RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.isClickable = true
            itemView.isFocusable = true
            itemView.setOnClickListener(this)

            binding.description.movementMethod = LinkMovementMethod.getInstance()
        }

        fun bind(showCaseInfo: ShowCaseInfo) {
            binding.title.text = showCaseInfo.title
            binding.description.text = showCaseInfo.description
        }

        override fun onClick(v: View?) {
            onItemClickListener(items[adapterPosition])
        }
    }
}
