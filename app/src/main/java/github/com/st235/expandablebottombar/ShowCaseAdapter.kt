package github.com.st235.expandablebottombar

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.content_showcase_item.view.*

typealias OnItemClickListener = (info: ShowCaseInfo) -> Unit

class ShowCaseAdapter(
    private val items: List<ShowCaseInfo>,
    private val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<ShowCaseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.content_showcase_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title = itemView.title
        val description = itemView.description

        init {
            itemView.isClickable = true
            itemView.isFocusable = true
            itemView.setOnClickListener(this)

            description.movementMethod = LinkMovementMethod.getInstance()
        }

        fun bind(showCaseInfo: ShowCaseInfo) {
            title.text = showCaseInfo.title
            description.text = showCaseInfo.description
        }

        override fun onClick(v: View?) {
            onItemClickListener(items[adapterPosition])
        }
    }
}
