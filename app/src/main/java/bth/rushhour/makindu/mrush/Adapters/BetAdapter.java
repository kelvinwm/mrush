package bth.rushhour.makindu.mrush.Adapters;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bth.rushhour.makindu.mrush.R;
import bth.rushhour.makindu.mrush.Setters.Specialist;
import bth.rushhour.makindu.mrush.Setters.betting;

/**
 * Created by lenovo on 2/22/2018.
 */

public class BetAdapter extends RecyclerView.Adapter<BetAdapter.ProductViewHolder> implements Filterable {


    //this context we will use to inflate the layout
    private Context mCtx;
    private BetAdapter.ContactsAdapterListener listener;
    private List<betting> productList;
    private List<betting> contactList;

    //getting the context and product list with constructor
    public BetAdapter(Context mCtx, List<betting> betList){
        this.mCtx = mCtx;
        this.productList = betList;
        this.contactList = betList;
    }

    @Override
    public BetAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.betitem, null);

        return new BetAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BetAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
        betting product = productList.get(position);

        Long dateInMillis=product.getTimestamp();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(dateInMillis));

        //binding the data with the viewholder views
        holder.displayname.setText(product.getPredictor());
        holder.date.setText(dateString);
        holder.game.setText(product.getGame());
        holder.win.setText(product.getResults());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView displayname, date, game,win;

        public ProductViewHolder(View itemView) {
            super(itemView);

            displayname = itemView.findViewById(R.id.displayname);
            date = itemView.findViewById(R.id.date);
            game = itemView.findViewById(R.id.game);
            win = itemView.findViewById(R.id.win);
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
                    List<betting> filteredList = new ArrayList<>();
                    for (betting row : productList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getPredictor().toLowerCase().contains(charString.toLowerCase())
                                ||row.getGame().toLowerCase().contains(charString.toLowerCase())||
                                row.getResults().contains(charSequence)) {
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
                productList = (ArrayList<betting>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(betting contact);
    }
}