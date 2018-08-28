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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

import bth.rushhour.makindu.mrush.R;
import bth.rushhour.makindu.mrush.Setters.bizner;

public class AddBuznerProduct extends AppCompatActivity {

    ImageView Prdctimage;
    EditText textName,textViewJPrdtDesc, textPhone;
    Button uploadPrdbtn;
    Uri resultUri= Uri.parse("");
    private StorageReference storageRef;
    private DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_buzner_product);

        Prdctimage=findViewById(R.id.Prdctimage);
        textName=findViewById(R.id.textName);
        textViewJPrdtDesc=findViewById(R.id.textViewJPrdtDesc);
        textPhone=findViewById(R.id.textPhone);
        uploadPrdbtn=findViewById(R.id.uploadPrdbtn);
        storageRef= FirebaseStorage.getInstance().getReference();
        dbref= FirebaseDatabase.getInstance().getReference().child("Buzner");

        Prdctimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)  .setMinCropResultSize(100,100)
                        .setMaxCropResultSize(1000,1000)
                        .start(AddBuznerProduct.this);

            }
        });
        uploadPrdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadSpecialistProfileInfo();
            }
        });
    }



    private void uploadSpecialistProfileInfo() {

        final long timestamp= System.currentTimeMillis();
        final String name,joddesc,phone;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUser = user.getDisplayName();
        name=textName.getText().toString();
        joddesc=textViewJPrdtDesc.getText().toString();
        phone=textPhone.getText().toString();

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(joddesc)||
                TextUtils.isEmpty(phone)||Uri.EMPTY.equals(resultUri))
        {
            Toast.makeText(AddBuznerProduct.this, "fill all the information please", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference refpath = storageRef.child("images/"+ UUID.randomUUID().toString());
        refpath.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Uri downloadUrl=taskSnapshot.getDownloadUrl();
                        DatabaseReference newPost=dbref.push();
                        bizner newSpec= new bizner(currentUser, name.trim(),joddesc.trim(),1,phone.trim(),downloadUrl.toString(), timestamp);
                        newPost.setValue(newSpec);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddBuznerProduct.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Prdctimage.setImageURI(resultUri);
//                Toast.makeText(this,"we are here",Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
