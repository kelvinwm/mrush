package bth.rushhour.makindu.mrush.Adapters;

/**
 * Created by lenovo on 1/12/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import bth.rushhour.makindu.mrush.R;
import bth.rushhour.makindu.mrush.Setters.bizner;

public class BiasharaAdapter extends RecyclerView.Adapter<BiasharaAdapter.AbumViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;


    //we are storing all the products in a list
    private List<bizner> productList;

    //getting the context and product list with constructor
    public BiasharaAdapter(Context mCtx, List<bizner> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public AbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.bizneritem, null);
        return new AbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AbumViewHolder holder, int position) {
        //getting the product of the specified position
        bizner product = productList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewShortDesc.setText(product.getShortdesc());
        holder.textViewRating.setTag(position);
        holder.textViewRating.setRating((float) product.getRating());
        holder.textViewRating.equals(String.valueOf(product.getRating()));
        holder.textViewPrice.setText(product.getPhone());
        try {
            Picasso.with(mCtx)
                    .load(product.getImage()).error(R.drawable.advertise)
                    .placeholder(R.drawable.progress_animation)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    class AbumViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewPrice;
        ImageView imageView;
        RatingBar textViewRating;


        public AbumViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}