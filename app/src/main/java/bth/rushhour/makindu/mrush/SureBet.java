package bth.rushhour.makindu.mrush;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;
import com.viewpagerindicator.CirclePageIndicator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import bth.rushhour.makindu.mrush.Adapters.BetAdapter;
import bth.rushhour.makindu.mrush.Adapters.SlidingImage_Adapter;
import bth.rushhour.makindu.mrush.Helperclass.CustomItemClickListener;
import bth.rushhour.makindu.mrush.Helperclass.RecyclerTouchListener;
import bth.rushhour.makindu.mrush.Setters.betting;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SureBet.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SureBet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SureBet extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Article> ModelArrayList;
    private ProgressBar progressBar;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ImageView backdrop;
    CirclePageIndicator indicator;
    private DatabaseReference dbref;
    private BetAdapter adapter;
    List<betting> betList ;
    RecyclerView recyclerView;
    private OnFragmentInteractionListener mListener;
    public SureBet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SureBet.
     */
    // TODO: Rename and change types and number of parameters
    public static SureBet newInstance() {
        SureBet fragment = new SureBet();
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
        // Infl
        View rootView =inflater.inflate(R.layout.fragment_sure_bet, container, false);
        ModelArrayList = new ArrayList<>();
        mPager = rootView.findViewById(R.id.pager);
        indicator = rootView.findViewById(R.id.indicator);
        progressBar = rootView.findViewById(R.id.loader5);
        backdrop = rootView.findViewById(R.id.backdrop);
        betList = new ArrayList<>();
        adapter = new BetAdapter(getContext(), betList);
        recyclerView =rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new CustomItemClickListener() {
        @Override
        public void onClick(View view, int position) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentUser = user.getDisplayName();
            betting betitem=betList.get(position);
            String betpredictor=betitem.getPredictor();
            final String time=betitem.getGame().toString();
            if(currentUser.equals(betpredictor)){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder
                    .setCancelable(false)
                    .setTitle("Alert")
                    .setMessage("Delete bet?")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                            /**delete bets*/
                            deleteNode(time);

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

            }
        }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        dbref= FirebaseDatabase.getInstance().getReference().child("Betpredictions");

        FloatingActionButton addproduct =rootView.findViewById(R.id.addbets);
        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.prompts, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText game = (EditText) promptsView
                        .findViewById(R.id.game);
                final EditText results = (EditText) promptsView
                        .findViewById(R.id.results);
                final EditText passkey = (EditText) promptsView
                        .findViewById(R.id.passkey);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                            if( game.getText().toString().isEmpty()||results.getText().toString().isEmpty()||passkey.getText().toString().isEmpty()){
                                Toast.makeText(getActivity(), "Enter all Fields",Toast.LENGTH_LONG).show();
                                return;
                            }

            checkPasskey(passkey.getText().toString().trim(),game.getText().toString().trim(),results.getText().toString().trim());


                                    }
                                }).setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
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

        fetchSportsLinks();
        loadBets();
        deleteOldBets();
        return rootView;
    }

    private void checkPasskey(final String pass,final String game,final String results){
        DatabaseReference dbref2=FirebaseDatabase.getInstance().getReference().child("PASSKEY");
            // get user input and set it to result
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUser = user.getDisplayName();
        final long timestamp= System.currentTimeMillis();

        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator items=dataSnapshot.getChildren().iterator();
                while (items.hasNext()){
                    String snewss=(String)((DataSnapshot)items.next()).getValue();
                    if( pass.contentEquals(snewss)){

                        DatabaseReference newPost=dbref.push();
                        betting newSpec= new betting(timestamp,currentUser, game,results);
                        newPost.setValue(newSpec);
                    }
                    else {
                        Toast.makeText(getActivity(), "wrong pass key contact Admin",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    public void fetchSportsLinks() {
        DatabaseReference dbref2= FirebaseDatabase.getInstance().getReference().child("Links");
        dbref2.child("SportsLink").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator items=dataSnapshot.getChildren().iterator();
                String snewss=null;
                while (items.hasNext()){
                    snewss=(String)((DataSnapshot)items.next()).getValue();
                }
                populateHeaderList(snewss);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(),"error updating",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void populateHeaderList(String sportlink) {
        Parser parser = new Parser();
        parser.execute(sportlink);
        parser.onFinish(new Parser.OnTaskCompleted() {
            //what to do when the parsing is done
            @Override
            public void onTaskCompleted(ArrayList<Article> list) {
                //list is an Array List with all article's information
                mPager.setAdapter(new SlidingImage_Adapter(getActivity(), list));
                progressBar.setVisibility(View.GONE);
                indicator.setViewPager(mPager);
                NUM_PAGES = list.size();
                // Aut start of viewpager
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
    private void deleteOldBets(){

        final long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(18, TimeUnit.HOURS);
        Query oldItems = dbref.orderByChild("timestamp").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    itemSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }


    private void loadBets() {
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    betList.clear();
                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        betting getbizner = postSnapShot.getValue(betting.class);
                        betList .add(
                                new betting(
                                        getbizner.getTimestamp(),
                                        getbizner.getPredictor(),
                                        getbizner.getGame(),
                                        getbizner.getResults()
                                        ));
                        Collections.reverse(betList);
                        adapter.notifyDataSetChanged();

                    }
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(),"error updating",Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1)* spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    /**
     * Converting dp to pixel
     9*/
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    private void deleteNode(final String game){
        /** where timestamp send from click equal time stamp in database*/
        dbref= FirebaseDatabase.getInstance().getReference().child("Betpredictions");
        dbref.orderByChild("game").equalTo(game).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            // do something with the individual "issues"
                            Object obj = issue.getKey();
                            DatabaseReference ref=dbref.child(obj.toString());
                            ref.setValue(null);


                            Toast.makeText(getActivity(),obj.toString(),Toast.LENGTH_LONG).show();
                        }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
