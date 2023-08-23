package de.pme.collector.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.pme.collector.R;
import de.pme.collector.interfaces.RecyclerViewClickInterface;
import de.pme.collector.model.Item;
import de.pme.collector.viewModel.ItemListViewModel;


public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder> {

    private final RecyclerViewClickInterface recyclerViewClickInterface;

    private final Context context;

    // cached copy of items
    private List<Item> itemList;

    private static ItemListViewModel itemListViewModel;


    // constructor
    public ItemRecyclerViewAdapter(Context context, RecyclerViewClickInterface recyclerViewClickInterface,
                                   ItemListViewModel itemListViewModel) {
        this.context = context;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        ItemRecyclerViewAdapter.itemListViewModel = itemListViewModel;
    }


    // inflate layout -> give a look to the rows
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.recycler_view_row_item, parent, false);

        return new ItemViewHolder(itemView, recyclerViewClickInterface, this);
    }


    // assign values to the "recycler_view_row_item.xml"-layout
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        if (this.itemList != null && !this.itemList.isEmpty()) {
            Item currentItem = this.itemList.get(position);
            holder.currentItemId = currentItem.getId();

            holder.itemName.setText(currentItem.getName());
            holder.itemLocation.setText(currentItem.getLocation());
            // TODO: TEMP
            holder.itemImage.setImageResource(R.drawable.recycler_view_placeholder_item);

            if (currentItem.isAcquired()) {
                holder.itemObtainedText.setText(R.string.item_obtained);
                holder.itemObtainedCheckbox.setChecked(true);
                holder.itemCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_found));
            }
            else {
                holder.itemObtainedText.setText(R.string.item_not_obtained);
                holder.itemObtainedCheckbox.setChecked(false);
                holder.itemCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_not_found));
            }
        }
        else {
            // if data is not ready yet
            holder.itemName.setText(R.string.item_list_empty);
        }
    }


    // number of items to be displayed
    @Override
    public int getItemCount() {
        if (this.itemList != null && !this.itemList.isEmpty()) {
            return this.itemList.size();
        }

        return 0;
    }


    // get views from "recycler_view_row_item.xml"-layout & assign them to variables
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView  itemName;
        TextView  itemLocation;
        TextView  itemObtainedText;
        CheckBox  itemObtainedCheckbox;
        CardView  itemCardView;

        int currentItemId = -1;

        @SuppressLint("NotifyDataSetChanged")
        public ItemViewHolder(@NonNull View itemView, RecyclerViewClickInterface recyclerViewClickInterface,
                              ItemRecyclerViewAdapter itemRecyclerViewAdapter) {
            super(itemView);

            itemImage            = itemView.findViewById(R.id.recycler_view_item_image);
            itemName             = itemView.findViewById(R.id.recycler_view_item_name);
            itemLocation         = itemView.findViewById(R.id.recycler_view_item_location);
            itemObtainedText     = itemView.findViewById(R.id.recycler_view_item_obtained);
            itemObtainedCheckbox = itemView.findViewById(R.id.recycler_view_item_obtained_checkbox);
            itemCardView         = itemView.findViewById(R.id.recycler_view_item_card_view);

            // handle click on the checkbox
            itemObtainedCheckbox.setOnClickListener(view -> {
                setObtainedStatus(((CheckBox) view).isChecked(), currentItemId);
                itemRecyclerViewAdapter.notifyDataSetChanged();
            });

            // handle click on an item-list element
            itemView.setOnClickListener(view ->
                    recyclerViewClickInterface.onElementClicked(currentItemId)
            );
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Item> itemList) {
        this.itemList = itemList;

        // notify observers
        notifyDataSetChanged();
    }


    private static void setObtainedStatus(boolean obtained, int itemId) {
        itemListViewModel.setObtainedStatus(obtained, itemId);
    }
}