package c.vadiole.recyclerview;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<Nabor> mListItems;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private View root;

    private Nabor  mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;

    MyRecyclerViewAdapter(Context context, ArrayList<Nabor> data, View root) {
        this.mInflater = LayoutInflater.from(context);
        this.mListItems = data;
        this.root = root;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Nabor myNabor = mListItems.get(position);

        String name = myNabor.getName();
        int person = myNabor.getPerson();
        int price = myNabor.getPrice();

        holder.myNameTextView.setText(name);
        holder.myPersonTextView.setText(String.valueOf(person));
        holder.myPriceTextView.setText(String.valueOf(price));
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myNameTextView;
        TextView myPersonTextView;
        TextView myPriceTextView;
        CardView myCardView;

        ViewHolder(View itemView) {
            super(itemView);
            myCardView = itemView.findViewById(R.id.cardView);
            myNameTextView = itemView.findViewById(R.id.nameTextView);
            myPersonTextView = itemView.findViewById(R.id.personTextView);
            myPriceTextView = itemView.findViewById(R.id.pricePerPersonTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Nabor getItem(int id) {
        return mListItems.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    private void showUndoSnackbar(View root) {
        View view = root.findViewById(R.id.recyclerview);
        Snackbar snackbar = Snackbar
                .make(view, R.string.snack_bar_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.snack_bar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        mListItems.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }

    private void onItemAdd(Nabor nabor) {
        mListItems.add(1, nabor);
        notifyItemInserted(1);
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    @Override
    public void onItemDismiss(int position) {
        mRecentlyDeletedItem = mListItems.get(position);
        mRecentlyDeletedItemPosition = position;

        mListItems.remove(position);
        notifyItemRemoved(position);

        showUndoSnackbar(root);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mListItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mListItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }
}

