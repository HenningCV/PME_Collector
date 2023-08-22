package de.pme.collector.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.pme.collector.R;
import de.pme.collector.interfaces.RecyclerViewClickInterface;
import de.pme.collector.model.Item;


public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder> {

    private final RecyclerViewClickInterface recyclerViewClickInterface;

    private final Context context;

    // cached copy of items
    private List<Item> itemList;


    // constructor
    public ItemRecyclerViewAdapter(Context context, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }


    // inflate layout -> give a look to the rows
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.recycler_view_row_item, parent, false);

        return new ItemViewHolder(itemView, recyclerViewClickInterface);
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
                holder.itemObtainedText.setText(R.string.recycler_view_item_obtained);
                holder.itemObtainedCheckbox.setChecked(true);
            }
            else {
                holder.itemObtainedText.setText(R.string.recycler_view_item_not_obtained);
                holder.itemObtainedCheckbox.setChecked(false);
            }
        }
        else {
            // if data is not ready yet
            holder.itemName.setText(R.string.fragment_item_list_empty);
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


    // get views from "recycler_view_row_game.xml"-layout & assign them to variables
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView  itemName;
        TextView  itemLocation;
        TextView  itemObtainedText;
        CheckBox  itemObtainedCheckbox;

        int currentItemId = -1;

        public ItemViewHolder(@NonNull View itemView, RecyclerViewClickInterface recyclerViewClickInterface) {
            super(itemView);

            itemImage            = itemView.findViewById(R.id.recycler_view_item_image);
            itemName             = itemView.findViewById(R.id.recycler_view_item_name);
            itemLocation         = itemView.findViewById(R.id.recycler_view_item_location);
            itemObtainedText     = itemView.findViewById(R.id.recycler_view_item_obtained);
            itemObtainedCheckbox = itemView.findViewById(R.id.recycler_view_item_obtained_checkbox);

            // handle click on a game-list element
            itemView.setOnClickListener(v ->
                    recyclerViewClickInterface.onElementClicked(currentItemId)
            );
        }
    }


    public void setItems(List<Item> itemList) {
        this.itemList = itemList;

        // notify observers
        notifyDataSetChanged();
    }
}