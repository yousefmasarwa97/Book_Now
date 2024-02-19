//package com.myapp.booknow.HelperClasses.HomeAdapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.myapp.booknow.R;
//import com.myapp.booknow.business.BusinessAdapter;
//
//import java.util.ArrayList;
//
//public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {
//
//    ArrayList<FeaturedHelperClass> featuredBusinesses;
//
//
//    // Will hold the data
//    public FeaturedAdapter(ArrayList<FeaturedHelperClass> featuredBusinesses){
//        this.featuredBusinesses = featuredBusinesses;
//    }
//
//    @NonNull
//    @Override
//    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.featured_card_design,parent,false);
//        return new FeaturedViewHolder(view);
//    }
//
//    // Binds the design and the code
//    @Override
//    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
//            FeaturedHelperClass featuredHelperClass = featuredBusinesses.get(position);
//
//            holder.image.setImageResource(featuredHelperClass.getImage());
//            holder.title.setText(featuredHelperClass.getTitle());
//            holder.desc.setText(featuredHelperClass.getDescription());
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//
//
//
//
//
//    // Will hold the view of recyclerView (image, title and description)
//    public static class FeaturedViewHolder extends RecyclerView.ViewHolder{
//        ImageView image;
//        TextView title,desc;
//        public FeaturedViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            image = itemView.findViewById(R.id.featured_image);
//            title = itemView.findViewById(R.id.featured_title);
//            desc = itemView.findViewById(R.id.featured_descreption);
//
//        }
//    }
//
//}
