package bth.rushhour.makindu.mrush;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bth.rushhour.makindu.mrush.Adapters.SpecialistAdapter;
import bth.rushhour.makindu.mrush.Additemclass.Specialist_upload;
import bth.rushhour.makindu.mrush.Detailsclass.SpecialistDetails;
import bth.rushhour.makindu.mrush.Helperclass.CustomItemClickListener;
import bth.rushhour.makindu.mrush.Helperclass.RecyclerTouchListener;
import bth.rushhour.makindu.mrush.Setters.Specialist;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Huduma.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Huduma#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Huduma extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    List<Specialist> productList;
    private DatabaseReference dbref;
    ProgressBar loader;
    RecyclerView recyclerView;
    SpecialistAdapter adapter;
    private SearchView searchView;
    private OnFragmentInteractionListener mListener;

    public Huduma() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Huduma.
     */
    // TODO: Rename and change types and number of parameters
    public static Huduma newInstance() {
        Huduma fragment = new Huduma();
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
        View rootView = inflater.inflate(R.layout.fragment_huduma, container, false);

        FloatingActionButton fab =rootView.findViewById(R.id.fabsps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toSpecialistupload =new Intent(getActivity(),Specialist_upload.class);
                startActivity(toSpecialistupload);

            }
        });

        productList = new ArrayList<>();
        adapter = new SpecialistAdapter(getContext(), productList);
        recyclerView =rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        dbref= FirebaseDatabase.getInstance().getReference().child("Rushhour");
        loader = rootView.findViewById(R.id.loader);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new CustomItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                final Specialist specialist=productList.get(position);

                String rate= String.valueOf(specialist.getRating());
                Intent i = new Intent(getActivity(), SpecialistDetails.class);
                i.putExtra("specphone", specialist.getPhone());
                i.putExtra("specname", specialist.getTitle());
                i.putExtra("specdetails", specialist.getShortdesc());
                i.putExtra("specrating",rate );
                i.putExtra("specimage", specialist.getImage());
                i.putExtra("specUserName", specialist.getUserName());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        searchView = rootView.findViewById(R.id.search);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed

                adapter.getFilter().filter(query);
                return false;
            }
        });
        fetchData();
        return rootView;
    }

    private void fetchData() {

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    productList.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        Specialist getSpecialist=postSnapShot.getValue(Specialist.class);
                        productList.add(
                                new Specialist(getSpecialist.getUserName(),
                                        getSpecialist.getTitle(),
                                        getSpecialist.getShortdesc(),
                                        getSpecialist.getRating(),
                                        getSpecialist.getPhone(),
                                        getSpecialist.getImage(),
                                        getSpecialist.getTimestamp()));
                        Collections.shuffle(productList);
                        adapter.notifyDataSetChanged();
                    }
                }

                loader.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(),"error updating",Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.setAdapter(adapter);
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
}
