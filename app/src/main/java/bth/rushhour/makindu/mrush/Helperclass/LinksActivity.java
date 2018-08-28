package bth.rushhour.makindu.mrush.Helperclass;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bth.rushhour.makindu.mrush.Additemclass.Specialist_upload;
import bth.rushhour.makindu.mrush.MainActivity;
import bth.rushhour.makindu.mrush.R;
import bth.rushhour.makindu.mrush.Setters.Links;
import bth.rushhour.makindu.mrush.Setters.Specialist;

public class LinksActivity extends AppCompatActivity {

    Button submitnews, submitbiashara,passkeys;
    private DatabaseReference dbref;
    EditText newshead, mainnews,sports,bs1, bs2,bs3,bs4,bs5,bs6,bs7,bs8,bs9,bs10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);

        sports=findViewById(R.id.sports);
        bs1=findViewById(R.id.bs1);
        bs2=findViewById(R.id.bs2);
        bs3=findViewById(R.id.bs3);
        bs4=findViewById(R.id.bs4);
        bs5=findViewById(R.id.bs5);
        bs6=findViewById(R.id.bs6);
        bs7=findViewById(R.id.bs7);
        bs8=findViewById(R.id.bs8);
        bs9=findViewById(R.id.bs9);
        bs10=findViewById(R.id.bs10);
        submitnews=findViewById(R.id.submitnews);
        passkeys=findViewById(R.id.passkeys);
        submitbiashara=findViewById(R.id.submitbiashara);
        dbref= FirebaseDatabase.getInstance().getReference().child("Links");

        passkeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toAddlink =new Intent(LinksActivity.this,PasskeyActivity.class);
                startActivity(toAddlink);
            }
        });
        submitnews.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /**send news links to fire base*/

                String sportsheader=sports.getText().toString();
                if(TextUtils.isEmpty(sportsheader))
                {
                    Toast.makeText(LinksActivity.this, "fill all the information please", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String,Object> map1= new HashMap<String,Object>();
                map1.put("sportsheader",sportsheader);
                dbref.child("SportsLink").updateChildren(map1);
            }
        });

        submitbiashara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**send biashara links and description to fire base*/

                String bss1,bss2,bss3,bss4,bss5,bss6,bss7,bss8,bss9,bss10;

                bss1=bs1.getText().toString();
                bss2=bs2.getText().toString();
                bss3=bs3.getText().toString();
                bss4=bs4.getText().toString();
                bss5=bs5.getText().toString();
                bss6=bs6.getText().toString();
                bss7=bs7.getText().toString();
                bss8=bs8.getText().toString();
                bss9=bs9.getText().toString();
                bss10=bs10.getText().toString();
                if(TextUtils.isEmpty(bss1) && TextUtils.isEmpty(bss1)
                        || TextUtils.isEmpty(bss2) || TextUtils.isEmpty(bss3)
                        || TextUtils.isEmpty(bss4) || TextUtils.isEmpty(bss5)
                        || TextUtils.isEmpty(bss6) || TextUtils.isEmpty(bss7)
                        || TextUtils.isEmpty(bss8) || TextUtils.isEmpty(bss9)
                        ||TextUtils.isEmpty(bss10)) {

                    Toast.makeText(LinksActivity.this, "fill all the information please", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String,Object> map2= new HashMap<String,Object>();
                map2.put("link1",bss1);
                map2.put("desc1",bss2);
                map2.put("link2",bss3);
                map2.put("desc2",bss4);
                map2.put("link3",bss5);
                map2.put("desc3",bss6);
                map2.put("link4",bss7);
                map2.put("desc4",bss8);
                map2.put("link5",bss9);
                map2.put("desc5",bss10);
                dbref.child("BiasharaLinks").updateChildren(map2);

            }
        });

        fetchNewsLinks();
        fetchBiznerLinks();
    }


    public void fetchNewsLinks() {
        dbref.child("SportsLink").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator items=dataSnapshot.getChildren().iterator();

                while (items.hasNext()){
                    String snewss=(String)((DataSnapshot)items.next()).getValue();
                    sports.setText(snewss);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(LinksActivity.this,"error updating",Toast.LENGTH_LONG).show();
            }
        });
    }

    public  void fetchBiznerLinks() {

        dbref.child("BiasharaLinks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator items=dataSnapshot.getChildren().iterator();

                while (items.hasNext()){
                    String dec1=(String)((DataSnapshot)items.next()).getValue();
                    String dec2=(String)((DataSnapshot)items.next()).getValue();
                    String dec3=(String)((DataSnapshot)items.next()).getValue();
                    String dec4=(String)((DataSnapshot)items.next()).getValue();
                    String dec5=(String)((DataSnapshot)items.next()).getValue();
                    String link1=(String)((DataSnapshot)items.next()).getValue();
                    String link2=(String)((DataSnapshot)items.next()).getValue();
                    String link3=(String)((DataSnapshot)items.next()).getValue();
                    String link4=(String)((DataSnapshot)items.next()).getValue();
                    String link5=(String)((DataSnapshot)items.next()).getValue();
                    bs1.setText(link1);
                    bs2.setText(dec1);
                    bs3.setText(link2);
                    bs4.setText(dec2);
                    bs5.setText(link3);
                    bs6.setText(dec3);
                    bs7.setText(link4);
                    bs8.setText(dec4);
                    bs9.setText(link5);
                    bs10.setText(dec5);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(LinksActivity.this,"error updating",Toast.LENGTH_LONG).show();
            }
        });
    }
}
