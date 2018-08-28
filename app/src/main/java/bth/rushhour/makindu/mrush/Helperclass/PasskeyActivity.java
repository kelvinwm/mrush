package bth.rushhour.makindu.mrush.Helperclass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import bth.rushhour.makindu.mrush.R;

public class PasskeyActivity extends AppCompatActivity {

    EditText passtxt;
    Button passbtn;
    private DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passkey);

         passtxt=findViewById(R.id.passtxt);
         passbtn=findViewById(R.id.passbtn);
        dbref= FirebaseDatabase.getInstance().getReference();
        passbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String passkeyy=passtxt.getText().toString();
                 if(TextUtils.isEmpty(passkeyy))
                 {
                     Toast.makeText(PasskeyActivity.this, "fill all the information please", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 Map<String,Object> map1= new HashMap<String,Object>();
                 map1.put("passkey",passkeyy);
                 dbref.child("PASSKEY").updateChildren(map1);
             }
         });
    }
}
