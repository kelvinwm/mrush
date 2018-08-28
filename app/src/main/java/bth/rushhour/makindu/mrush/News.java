package bth.rushhour.makindu.mrush;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;
import com.viewpagerindicator.CirclePageIndicator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import bth.rushhour.makindu.mrush.Adapters.SlidingImage_Adapter;
import bth.rushhour.makindu.mrush.Helperclass.Function;
import bth.rushhour.makindu.mrush.Helperclass.LinksActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link News.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link News#newInstance} factory method to
 * create an instance of this fragment.
 */
public class News extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference dbref;
    private ArrayList<Article> ModelArrayList;
    private ProgressBar progressBar;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ImageView backdrop;
    CirclePageIndicator indicator;
    WebView webView;
    private OnFragmentInteractionListener mListener;
    public News() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static News newInstance() {
        News fragment = new News();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment \
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        FloatingActionButton addproduct =rootView.findViewById(R.id.addlink);
        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(getActivity());
                    View promptsView = li.inflate(R.layout.newslinkprompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);
                    final EditText game = (EditText) promptsView
                        .findViewById(R.id.passwd);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {

                            if( game.getText().toString().isEmpty()){
                                Toast.makeText(getActivity(), "Enter all Fields",Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(game.getText().toString().trim().equals("me4u")){

                                Intent toAddlink =new Intent(getActivity(),LinksActivity.class);
                                startActivity(toAddlink);
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
            }
        });
        ModelArrayList = new ArrayList<>();
        dbref= FirebaseDatabase.getInstance().getReference().child("Links");
        mPager = rootView.findViewById(R.id.pager);
        indicator = rootView.findViewById(R.id.indicator);
        progressBar = rootView.findViewById(R.id.loader3);
        webView = (WebView) rootView.findViewById(R.id.webView);
        backdrop = rootView.findViewById(R.id.backdrop);
        if(Function.isNetworkAvailable(getContext()))
        {
            fetchBiznerLinks();
        }else{
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        return rootView;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void populateHeaderList(String declink) {
        Parser parser = new Parser();
        parser.execute(declink);
        parser.onFinish(new Parser.OnTaskCompleted() {
            //what to do when the parsing is done
        @Override
        public void onTaskCompleted(ArrayList<Article> list) {
            //list is an Array List with all article's information
        mPager.setAdapter(new SlidingImage_Adapter(getActivity(), list));
        progressBar.setVisibility(View.GONE);
        indicator.setViewPager(mPager);
        NUM_PAGES = list.size();
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 6000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }
            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }
            //what to do in case of error
            @Override
            public void onError() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Unable to load data.",
                                Toast.LENGTH_LONG).show();
                        Log.i("Unable to load ", "articles");
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void WebAction(String newslink){

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.loadUrl(newslink);
        webView.setWebViewClient(new WebViewClient(){
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.loadUrl("file:///android_assets/error.html");
            }
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                progressBar.setVisibility(View.GONE);
            }

        });

    }

    public  void fetchBiznerLinks() {

        dbref.child("BiasharaLinks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator items=dataSnapshot.getChildren().iterator();

                String dec1 = null;
                String link1 = null;
                while (items.hasNext()){
                    dec1=(String)((DataSnapshot)items.next()).getValue();
                    String dec2=(String)((DataSnapshot)items.next()).getValue();
                    String dec3=(String)((DataSnapshot)items.next()).getValue();
                    String dec4=(String)((DataSnapshot)items.next()).getValue();
                    String dec5=(String)((DataSnapshot)items.next()).getValue();
                    link1=(String)((DataSnapshot)items.next()).getValue();
                    String link2=(String)((DataSnapshot)items.next()).getValue();
                    String link3=(String)((DataSnapshot)items.next()).getValue();
                    String link4=(String)((DataSnapshot)items.next()).getValue();
                    String link5=(String)((DataSnapshot)items.next()).getValue();

                }
                populateHeaderList(dec1);
                WebAction(link1);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(),"error updating",Toast.LENGTH_LONG).show();
            }
        });
    }
}
