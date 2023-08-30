package de.pme.collector.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
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


    // inflate layout -> "give a look to the rows"
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a LayoutInflater-instance from the context
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // inflate layout for a single item-view using the layoutInflater
        View itemView = layoutInflater.inflate(R.layout.recycler_view_row_item, parent, false);

        return new ItemViewHolder(itemView, recyclerViewClickInterface, this);
    }


    // assign values to the "recycler_view_row_item.xml"-layout
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        if (this.itemList != null && !this.itemList.isEmpty()) {
            Item currentItem = this.itemList.get(position);
            holder.currentItemId = currentItem.getId();

            // set item name & location
            holder.itemName.setText(currentItem.getName());
            holder.itemLocation.setText(currentItem.getLocation());

            // set item image
            setItemImage(currentItem, holder);

            // turn the item card green & check the checkbox when the item is obtained
            if (currentItem.isAcquired()) {
                holder.itemObtainedText.setText(R.string.item_obtained);
                holder.itemObtainedCheckbox.setChecked(true);
                holder.itemCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_found));
            }
            // turn the item card red & uncheck the checkbox when the item is not obtained
            else {
                holder.itemObtainedText.setText(R.string.item_not_obtained);
                holder.itemObtainedCheckbox.setChecked(false);
                holder.itemCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_not_found));
            }
        }
        else {
            // if data is not ready yet
            holder.itemName.setText(R.string.item_list_empty_list);
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
                // notify observers
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


    private void setItemImage(Item currentItem, ItemViewHolder holder) {

        String imagePath = currentItem.getImagePath();

        // load item image
        File itemImageFile = new File(imagePath);

        // the if() is just to assign the initial items their images, after that images from the device are used
        if (imagePath.contains("@drawable/")) {

            // split the image-path after the '@drawable/' to get the index of the image
            String[] splitImagePath = imagePath.split("@drawable/");

            // load images for initial items from the drawable-folder via the values-array of those images
            TypedArray itemImagesArray = holder.itemView.getResources().obtainTypedArray(R.array.initial_item_images);

            // get the id for the corresponding image
            int imageResourceId = itemImagesArray.getResourceId(Integer.parseInt(splitImagePath[1]), 0);

            // if the resource was not found use the default image
            if (imageResourceId == 0) {
                setDefaultImage(holder);
            }
            else {
                // set image to the ImageView
                holder.itemImage.setImageResource(imageResourceId);
            }

            // recycle itemImagesArray to avoid memory leaks
            itemImagesArray.recycle();
            return;
        }
        else {
            if (itemImageFile.exists()) {
                // load image from device
                Bitmap myBitmap = BitmapFactory.decodeFile(itemImageFile.getAbsolutePath());

                // set image to the ImageView
                holder.itemImage.setImageBitmap(myBitmap);
                return;
            }
        }

        // set default image if the system image doesn't exist anymore
        setDefaultImage(holder);
    }


    private void setDefaultImage(ItemViewHolder holder) {
        holder.itemImage.setImageResource(R.drawable.item_placeholder);
    }
}