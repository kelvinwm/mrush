package bth.rushhour.makindu.mrush;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import bth.rushhour.makindu.mrush.Adapters.BiasharaAdapter;
import bth.rushhour.makindu.mrush.Adapters.SlidingBzner_adapter;
import bth.rushhour.makindu.mrush.Additemclass.AddBuznerProduct;
import bth.rushhour.makindu.mrush.Detailsclass.BiznerDetails;
import bth.rushhour.makindu.mrush.Helperclass.CustomItemClickListener;
import bth.rushhour.makindu.mrush.Helperclass.LinksActivity;
import bth.rushhour.makindu.mrush.Helperclass.RecyclerTouchListener;
import bth.rushhour.makindu.mrush.Setters.Links;
import bth.rushhour.makindu.mrush.Setters.bizner;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Biashara.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Biashara#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Biashara extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BiasharaAdapter adapter;
    List<bizner> albumList ;
    private DatabaseReference dbref;
    ProgressBar loader2;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    CirclePageIndicator indicator;
    //the recyclerview
    RecyclerView recyclerView;


    private OnFragmentInteractionListener mListener;

    public Biashara() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Biashara.
     */
    // TODO: Rename and change types and number of parameters
    public static Biashara newInstance() {
        Biashara fragment = new Biashara();
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
        // Inflate the layout for this fragment
       View rootView= inflater.inflate(R.layout.fragment_biashara, container, false);

        FloatingActionButton addproduct =rootView.findViewById(R.id.addproduct);
        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toAddbiznerprdct =new Intent(getActivity(),AddBuznerProduct.class);
                startActivity(toAddbiznerprdct);

            }
        });

        dbref= FirebaseDatabase.getInstance().getReference().child("Buzner");
        loader2 = rootView.findViewById(R.id.loader2);
        recyclerView = rootView.findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        Collections.reverse(albumList);
        albumList.clear();
        adapter = new BiasharaAdapter(getContext(), albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mPager = rootView.findViewById(R.id.pager);
        indicator = rootView.findViewById(R.id.indicator);
        //getting the recyclerview from xml

        recyclerView.setAdapter(adapter);

        try {

            Picasso.with(getActivity())
                    .load(R.drawable.cover)
                    .fit()
                    .centerCrop()
                    .into((ImageView) rootView.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new CustomItemClickListener() {

            @Override
            public void onClick(View view, int position) {

                final bizner biznerItem=albumList.get(position);
                final String rate= String.valueOf(biznerItem.getRating());

                final Intent i = new Intent(getActivity(), BiznerDetails.class);

                i.putExtra("timestamp", biznerItem.getTimestamp());
                i.putExtra("bizphone", biznerItem.getPhone());
                i.putExtra("bizname", biznerItem.getTitle());
                i.putExtra("bizdetails", biznerItem.getShortdesc());
                i.putExtra("bizrating",rate );
                i.putExtra("bizUserName", biznerItem.getCurrentuser());
                i.putExtra("bizimage", biznerItem.getImage());

//                            Toast.makeText(getActivity(), "Account with "+ obj.toString()+" exits", Toast.LENGTH_SHORT).show();
                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        fetchBiznerLinks();
        deleteOldAdverts();
        loadAdverts();
        return rootView;
    }

    private void populatefsheader(String dec2,String dec3,String dec4,String dec5,String link2,String link3,String link4,String link5){

        final List<Links> albumLis= new ArrayList<>();
        Links getInfo2=new Links(link2,dec2);
        albumLis.add(getInfo2);
        Links getInfo3=new Links(link3,dec3);
        albumLis.add(getInfo3);
        Links getInfo4=new Links(link4,dec4);
        albumLis.add(getInfo4);
        Links getInfo5=new Links(link5,dec5);
        albumLis.add(getInfo5);

        mPager.setAdapter(new SlidingBzner_adapter(getActivity(), albumLis));
                    indicator.setViewPager(mPager);
                    NUM_PAGES = albumLis.size();

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
                    }, 6000, 9000);

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

    public  void fetchBiznerLinks() {
        DatabaseReference dbreff= FirebaseDatabase.getInstance().getReference().child("Links");
        dbreff.child("BiasharaLinks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator items=dataSnapshot.getChildren().iterator();

                String dec2=null,dec3=null,dec4=null,dec5=null,link2=null,link3=null,link4=null,link5=null;
                while (items.hasNext()){
                    String dec1=(String)((DataSnapshot)items.next()).getValue();
                    dec2=(String)((DataSnapshot)items.next()).getValue();
                    dec3=(String)((DataSnapshot)items.next()).getValue();
                    dec4=(String)((DataSnapshot)items.next()).getValue();
                    dec5=(String)((DataSnapshot)items.next()).getValue();
                    String link1=(String)((DataSnapshot)items.next()).getValue();
                    link2=(String)((DataSnapshot)items.next()).getValue();
                    link3=(String)((DataSnapshot)items.next()).getValue();
                    link4=(String)((DataSnapshot)items.next()).getValue();
                    link5=(String)((DataSnapshot)items.next()).getValue();

                }

                populatefsheader(dec2,dec3,dec4,dec5,link2,link3,link4,link5);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(),"error updating",Toast.LENGTH_LONG).show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /**
     * Adding few albums for testing
     */
    private void loadAdverts() {
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    albumList.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        bizner getbizner = postSnapShot.getValue(bizner.class);

                        albumList .add(
                                new bizner(
                                        getbizner.getCurrentuser(),
                                        getbizner.getTitle(),
                                        getbizner.getShortdesc(),
                                        getbizner.getRating(),
                                        getbizner.getPhone(),
                                        getbizner.getImage(),
                                        getbizner.getTimestamp()));

                        Collections.reverse(albumList);
                        adapter.notifyDataSetChanged();

                    }
                    loader2.setVisibility(View.INVISIBLE);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(),"error updating",Toast.LENGTH_LONG).show();
            }
        });
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
                outRect.right = (0) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

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
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void deleteOldAdverts(){

        final long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
        Query oldItems = dbref.orderByChild("timestamp").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {

                    itemSnapshot.getRef().removeValue();

                    /**remember to add delete image method*/
                    String imagepath=itemSnapshot.child("image").getValue().toString();

                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imagepath);
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            Log.d("TAG", "onSuccess: deleted file");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            Log.d("TAG", "onFailure: did not delete file");
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

}
