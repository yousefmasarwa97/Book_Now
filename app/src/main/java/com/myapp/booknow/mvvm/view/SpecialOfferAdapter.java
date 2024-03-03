package com.myapp.booknow.mvvm.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.BusinessSpecialOffers;

import java.util.List;

/**
 * Adapter for RecyclerView to display business information.
 * This adapter handles the layout and binding of business data
 * to the views defined in the ViewHolder.
 */
public class SpecialOfferAdapter extends RecyclerView.Adapter<SpecialOfferAdapter.ViewHolder> {

    private List<BusinessSpecialOffers> offersList;

    public SpecialOfferAdapter(List<BusinessSpecialOffers> offersList) {
        this.offersList = offersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_card_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BusinessSpecialOffers offer = offersList.get(position);
        holder.offername.setText(offer.getName());
        holder.offerDescription.setText(offer.getDescription());

        //----------------------------//
        //----------------------------//

        Glide.with(holder.itemView)
                .load(offer.getImageURL())
                .placeholder(R.drawable.business_icon)
                .error(R.drawable.ic_menu_gallery)//should change !!
                .into(holder.offerLogo);
        //we can set other attributes !
//        holder.itemView.setOnClickListener(new View.OnClickListener(){

//            @Override
//            public void onClick(View v) {
//                Log.d("CHECKING IF BUSINESS INFO ARE CORRECT !!!! :: ","Name = " + offer.getName() + "and description is = "+ offer.getDescription()
//                        +" and imageURL= " + offer.getImageURL());
//                Intent intent = new Intent(v.getContext(), ShowBusinessActivity.class);
//                intent.putExtra("businessId",offer.getOfferId());
////                v.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return offersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView offername; // and other views
        public TextView offerDescription;
        public TextView status;
        public ImageView offerLogo;
        public ViewHolder(View view) {
            super(view);
            offername = view.findViewById(R.id.featured_title); // can replace with actual view ID
            offerLogo = view.findViewById(R.id.featured_image);
            offerDescription = view.findViewById(R.id.featured_descreption);
            status=view.findViewById(R.id.business_status);
            status.setText(null);
            // Initialize other views
        }
    }


    public void setBusinesses(List<BusinessSpecialOffers> offers) {
        this.offersList = offers;
    }


}
