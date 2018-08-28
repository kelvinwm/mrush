package bth.rushhour.makindu.mrush.Additemclass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

import bth.rushhour.makindu.mrush.R;
import bth.rushhour.makindu.mrush.Setters.Specialist;

public class Specialist_upload extends AppCompatActivity {

    ImageView img;
    EditText textViewName, textViewJobShortDesc, textViewPhone;
    Button uploadbtn;
    Uri resultUri= Uri.parse("");
    private StorageReference storageRef;
    private DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialist_upload);
        img=findViewById(R.id.imageViewup);
        textViewName=findViewById(R.id.textViewName);
        textViewJobShortDesc=findViewById(R.id.textViewJobShortDesc);
        textViewPhone=findViewById(R.id.textViewPhone);
        uploadbtn=findViewById(R.id.uploadbtn);
        storageRef= FirebaseStorage.getInstance().getReference();
        dbref= FirebaseDatabase.getInstance().getReference().child("Rushhour");
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)  .setMinCropResultSize(100,100)
                        .setMaxCropResultSize(1000,1000)
                        .start(Specialist_upload.this);

            }
        });
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadSpecialistProfileInfo();
            }
        });
    }

    private void uploadSpecialistProfileInfo() {
        final String name,joddesc,phone;
        final long timestamp= System.currentTimeMillis();
        name=textViewName.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUser = user.getDisplayName();
        joddesc=textViewJobShortDesc.getText().toString();
        phone=textViewPhone.getText().toString();
        if(TextUtils.isEmpty(name)
                ||TextUtils.isEmpty(joddesc)|| TextUtils.isEmpty(phone)||Uri.EMPTY.equals(resultUri))
        {
            Toast.makeText(Specialist_upload.this, "fill all the information please", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final StorageReference refpath = storageRef.child("images/"+ UUID.randomUUID().toString());
        Query query = dbref.orderByKey().equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        progressDialog.dismiss();
                        Toast.makeText(Specialist_upload.this, "Account with "+phone+" exits", Toast.LENGTH_SHORT).show();
                    }

                    return;
                }

                refpath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                //
                                Uri downloadUrl=taskSnapshot.getDownloadUrl();
                                DatabaseReference newPost=dbref.push();
                                Specialist newSpec= new Specialist(currentUser.trim(),name.trim(),joddesc.trim(),1,phone.trim(),downloadUrl.toString(),timestamp);
                                dbref.child(phone).setValue(newSpec);

                                finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(Specialist_upload.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                img.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
