package bth.rushhour.makindu.mrush.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import bth.rushhour.makindu.mrush.Detailsclass.BiznerDetails;
import bth.rushhour.makindu.mrush.Detailsclass.DetailsActivity;
import bth.rushhour.makindu.mrush.R;
import bth.rushhour.makindu.mrush.Setters.Links;
import bth.rushhour.makindu.mrush.Setters.bizner;

/**
 * Created by lenovo on 2/15/2018.
 */

public class SlidingBzner_adapter extends PagerAdapter {

    private List<Links> articles;
    private LayoutInflater inflater;
    private Context context;


    public SlidingBzner_adapter(Context context, List<Links> articles) {
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
        View imageLayout = inflater.inflate(R.layout.biznerheader, view, false);
        final Links currentArticle = articles.get(position);

        assert imageLayout != null;
        final WebView webView =imageLayout.findViewById(R.id.designer_view);
        TextView headline =imageLayout.findViewById(R.id.newsheadline);

        final ProgressBar loader = (ProgressBar) imageLayout.findViewById(R.id.loader);
        headline.setText(currentArticle.getBiasharadesc() +" CLICK HERE");
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(currentArticle.getBiasharalink());
        webView.setWebViewClient(new WebViewClient(){
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.loadUrl("file:///android_assets/error.html");

            }
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                loader.setVisibility(View.GONE);
            }
        });
        webView.post(new Runnable() {
            public void run() {
                if (webView.getContentHeight() * webView.getScale() >= webView.getScrollY() ){
                    webView.scrollBy(0, (int)webView.getHeight());
                }
            }
        });
        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailsActivity.class);
                i.putExtra("url", currentArticle.getBiasharalink());
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
