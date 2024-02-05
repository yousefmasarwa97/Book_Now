//package com.myapp.booknow;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//
//public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {
//
//    private List<ServiceProvider> providerList;
//
//    public ProviderAdapter(List<ServiceProvider> providerList) {
//        this.providerList = providerList;
//    }
//
//    @NonNull
//    @Override
//    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_item, parent, false);
//        return new ProviderViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
//        ServiceProvider provider = providerList.get(position);
//        holder.tvProviderName.setText(provider.getName());
//        // Set other fields as needed
//    }
//
//    @Override
//    public int getItemCount() {
//        return providerList.size();
//    }
//
//    static class ProviderViewHolder extends RecyclerView.ViewHolder {
//        TextView tvProviderName;
//
//        ProviderViewHolder(View itemView) {
//            super(itemView);
//            tvProviderName = itemView.findViewById(R.id.providerNameEditText);
//            // Initialize other views here
//        }
//    }
//}
//
