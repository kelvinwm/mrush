package bth.rushhour.makindu.mrush.Adapters;

/**
 * Created by lenovo on 1/12/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bth.rushhour.makindu.mrush.R;
import bth.rushhour.makindu.mrush.Setters.Specialist;

public class SpecialistAdapter extends RecyclerView.Adapter<SpecialistAdapter.ProductViewHolder> implements Filterable {


    //this context we will use to inflate the layout
    private Context mCtx;
    private ContactsAdapterListener listener;
    private List<Specialist> productList;
    private List<Specialist> contactList;

    //getting the context and product list with constructor
    public SpecialistAdapter(Context mCtx, List<Specialist> productList){
        this.mCtx = mCtx;
        this.productList = productList;
        this.contactList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_specialist, null);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Specialist product = productList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewShortDesc.setText(product.getShortdesc());
        holder.textViewRating.setTag(position);
        holder.textViewRating.setRating((float) product.getRating());
        holder.textViewRating.equals(String.valueOf(product.getRating()));
        holder.textViewPrice.setText(product.getPhone());

        Picasso.with(mCtx)
                .load(product.getImage())
                .placeholder(R.drawable.icon)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewPrice;
        ImageView imageView;
        RatingBar textViewRating;


        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productList = contactList;
                } else {
                    List<Specialist> filteredList = new ArrayList<>();
                    for (Specialist row : productList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())
                                ||row.getShortdesc().toLowerCase().contains(charString.toLowerCase())||
                                row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    productList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productList = (ArrayList<Specialist>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Specialist contact);
    }
}