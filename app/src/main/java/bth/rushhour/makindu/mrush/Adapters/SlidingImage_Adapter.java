package bth.rushhour.makindu.mrush.Adapters;

/**
 * Created by lenovo on 1/31/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prof.rssparser.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bth.rushhour.makindu.mrush.Detailsclass.DetailsActivity;
import bth.rushhour.makindu.mrush.R;


public class SlidingImage_Adapter extends PagerAdapter {

    private ArrayList<Article> articles;
    private LayoutInflater inflater;
    private Context context;


    public SlidingImage_Adapter(Context context, ArrayList<Article> articles) {
        this.context = context;
        this.articles = articles;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void clearData() {
        if (articles != null)
            articles.clear();
    }
//
    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.header, view, false);
        final Article currentArticle = articles.get(position);

        assert imageLayout != null;
        final ImageView imageView =imageLayout.findViewById(R.id.backdrop);
         TextView headline =imageLayout.findViewById(R.id.newsheadline);
        headline.setText(currentArticle.getTitle());

        Picasso.with(context)
                .load(currentArticle.getImage())
                .placeholder(R.drawable.news)
                .fit()
                .centerCrop()
                .into(imageView);
        imageLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailsActivity.class);
                i.putExtra("url", currentArticle.getLink());
                context.startActivity(i);
            }

        });
        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
