package bth.rushhour.makindu.mrush.Detailsclass;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

import bth.rushhour.makindu.mrush.R;

public class SpecialistDetails extends AppCompatActivity {
    private DatabaseReference dbref;
    String phone, name, descr;
    EditText txtViewName, ViewJobShortDesc, txtViewPhone;
    RatingBar txtRating;
    String rating, image, userName, itemkeey;
    TextView editbtn;
    Button callbtn, changesbtn;
    ProgressDialog progressDialog;
    Uri resultUri=null;
    ImageView imagViewup;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialist_details);

        dbref = FirebaseDatabase.getInstance().getReference().child("Rushhour");
        Intent intent = getIntent();
        phone = intent.getStringExtra("specphone");
        image = intent.getStringExtra("specimage");
        name = intent.getStringExtra("specname");
        descr = intent.getStringExtra("specdetails");
        rating = intent.getStringExtra("specrating");
        userName = intent.getStringExtra("specUserName");
        progressDialog = new ProgressDialog(this);


        txtViewName = findViewById(R.id.txtViewName);
        callbtn = findViewById(R.id.callbtn);
        changesbtn = findViewById(R.id.changesbtn);
        ViewJobShortDesc = findViewById(R.id.ViewJobShortDesc);
        txtViewPhone = findViewById(R.id.txtViewPhone);
        imagViewup = findViewById(R.id.imagViewup);
        txtRating = findViewById(R.id.txtRating);

        changesbtn.setVisibility(View.GONE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        viewUser(name, descr, phone, image, rating);
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUser = user.getDisplayName();

        changesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentUser.equals(userName)) {
                update();
            }
            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                call(txtViewPhone.getText().toString());
            }
        });
        imagViewup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentUser.equals(userName)) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON).setMinCropResultSize(100, 100)
                        .setMaxCropResultSize(1000, 1000)

                        .start(SpecialistDetails.this);
            }

            }
        });
        setEditable(false);
    }


    public void viewUser(String name, String shortdesc, String phone, String image, String ratings) {

        txtViewName.setText(name);
        ViewJobShortDesc.setText(shortdesc);
//        Toast.makeText(SpecialistDetails.this, "Account with "+ratings+" exits", Toast.LENGTH_SHORT).show();
        txtRating.setRating(Float.parseFloat(ratings));
        txtViewPhone.setText(phone);

        Picasso.with(SpecialistDetails.this)
                .load(image).error(R.drawable.icon)
                .placeholder(R.drawable.progress_animation)
                .fit()
                .centerCrop()
                .into(imagViewup);

    }

    public void setEditable(boolean type) {
        txtViewName.setEnabled(type);
        ViewJobShortDesc.setEnabled(type);
        txtViewPhone.setEnabled(type);
        changesbtn.setEnabled(type);

        if (type) {
            changesbtn.setVisibility(View.VISIBLE);
        }
    }

    private void unhideKeyboard(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        txtViewName.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(txtViewName, InputMethodManager.SHOW_FORCED);
    }


    private void call(String num) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + num));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("hello example", "Call failed", e);
        }
    }

    private void update(){

        dbref.orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        progressDialog.dismiss();
                        Object obj = issue.getKey();
                        DatabaseReference ref=dbref.child(obj.toString());
                        ref.child("title").setValue(txtViewName.getText().toString());
                        ref.child("shortdesc").setValue(ViewJobShortDesc.getText().toString());
                        ref.child("phone").setValue(txtViewPhone.getText().toString());
                        setEditable(false);
                        changesbtn.setVisibility(View.GONE);
                    }
                }

//                Toast.makeText(SpecialistDetails.this, "Account updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            StorageReference storageRef= FirebaseStorage.getInstance().getReference();
            final StorageReference refpath = storageRef.child("images/"+ UUID.randomUUID().toString());

            if (resultCode == RESULT_OK) {
                deleteFile();
                resultUri = result.getUri();
                imagViewup.setImageURI(resultUri);
//                Toast.makeText(this,"we are here",Toast.LENGTH_LONG).show();

                refpath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                //
                                final Uri downloadUrl=taskSnapshot.getDownloadUrl();
                                dbref.orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // dataSnapshot is the "issue" node with all children with id 0
                                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                                // do something with the individual "issues"
                                                progressDialog.dismiss();
                                                Object obj = issue.getKey();
                                                DatabaseReference ref=dbref.child(obj.toString());
                                                ref.child("image").setValue(downloadUrl.toString());
                                            }
                                        }

//                Toast.makeText(SpecialistDetails.this, "Account updated", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                dbref.orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // dataSnapshot is the "issue" node with all children with id 0
                                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                                // do something with the individual "issues"
                                                progressDialog.dismiss();
                                                Object obj = issue.getKey();
                                                DatabaseReference ref=dbref.child(obj.toString());
                                                ref.child("image").setValue(downloadUrl.toString());
                                            }
                                        }

//                Toast.makeText(SpecialistDetails.this, "Account updated", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(SpecialistDetails.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded "+(int)progress+"%");

                            }


                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void deleteFile(){

        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
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
    private void deleteNode(){

        dbref.orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Object obj = issue.getKey();
                        DatabaseReference ref=dbref.child(obj.toString());
                        ref.setValue(null);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        deleteFile();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        String currentUser = user.getDisplayName();
        switch (item.getItemId()) {
            case R.id.editing:
                if(currentUser.equals(userName)) {
                setEditable(true);
                unhideKeyboard();
                }
                return true;
            case R.id.delete:
                if(currentUser.equals(userName)) {
                deleteNode();
        }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
