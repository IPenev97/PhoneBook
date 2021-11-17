package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phonebook.models.Contact;
import com.example.phonebook.viewmodels.ContactViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEditContactActivity extends AppCompatActivity {


    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 106;

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextNickname;
    private EditText editTextPhoneNumber;
    private ImageView profileImage;
    private ContactViewModel contactViewModel;
    private String currentImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        this.editTextFirstName = findViewById(R.id.first_name);
        this.editTextLastName = findViewById(R.id.last_name);
        this.editTextNickname = findViewById(R.id.nickname);
        this.editTextPhoneNumber = findViewById(R.id.phone_number);
        this.profileImage = findViewById(R.id.add_profile_image);
        this.contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPhotoDialog();
            }
        });

        if(getIntent().getStringExtra("action").equals("edit")){
            Contact contact = getIntent().getParcelableExtra("contact");
            editTextFirstName.setText(contact.getFirstName());
            editTextLastName.setText(contact.getLastName());
            editTextNickname.setText(contact.getNickname());
            editTextPhoneNumber.setText(contact.getPhoneNumber());
            if(contact.getProfileImagePath()!=null) {
                Glide.with(this)
                        .load(contact.getProfileImagePath())
                        .into(profileImage);
            }
            setTitle("Edit Contact");
        }
        else{
            setTitle("Add Contact");
        }



    }

    private void initPhotoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_dialog_options);

         Button takePhotoButton = dialog.findViewById(R.id.take_photo_button);
         Button galleryPhotoButton = dialog.findViewById(R.id.select_photo_from_gallery_button);



         takePhotoButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                askCameraPermission();
                dialog.dismiss();


             }
         });

         galleryPhotoButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 askStoragePermission();
                 dialog.dismiss();


             }
         });

        dialog.show();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_contact_menu, menu);
        MenuItem deleteIcon = menu.getItem(0);
        MenuItem favoriteIcon = menu.getItem(1);
        if(getIntent().getStringExtra("action").equals("edit")){

            deleteIcon.setVisible(true);
            favoriteIcon.setVisible(true);
        }
        else{
            deleteIcon.setVisible(false);
            favoriteIcon.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_contact:

                if(getIntent().getStringExtra("action").equals("edit")){
                    updateContact();
                    return true;
                }
                saveContact();
                return true;
            case R.id.delete_contact:

                deleteContact();
                return true;

            case R.id.favourite_contact:
                favoriteContact();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void favoriteContact(){
        Contact contact = getIntent().getParcelableExtra("contact");
        if(contact.isFavorite()){
            contact.setFavorite(false);
            Toast.makeText(this, "Contact removed from favorites", Toast.LENGTH_SHORT).show();
        }
        else {
            contact.setFavorite(true);
            Toast.makeText(this, "Contact added to favorites", Toast.LENGTH_SHORT).show();
        }
        contactViewModel.update(contact);

    }

    private void deleteContact(){
        Contact contact = getIntent().getParcelableExtra("contact");
        contactViewModel.delete(contact);
        finish();
    }

    private void updateContact(){
        Contact passedContact = getIntent().getParcelableExtra("contact");
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String nickname = editTextNickname.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        if(currentImagePath == null){
            currentImagePath = passedContact.getProfileImagePath();
        }



        if(phoneNumber.trim().isEmpty()){
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }


        Contact contact = new Contact(firstName,lastName,nickname,phoneNumber, currentImagePath, false);

        contact.setId(passedContact.getId());

        contactViewModel.update(contact);
        Toast.makeText(this, "Contact Updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveContact() {
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String nickname = editTextNickname.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();



        if(phoneNumber.trim().isEmpty()){
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }



        Contact contact = new Contact(firstName,lastName,nickname,phoneNumber, currentImagePath, false);
        contactViewModel.insert(contact);
        Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
        finish();
    }



    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        else {
            dispatchTakePictureIntent();
        }
    }

    private void askStoragePermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
        }
        else{
            selectImageFromGallery();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission required!", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery();
            } else {
                Toast.makeText(this, "Gallery permission required!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE) {

            if(resultCode == Activity.RESULT_OK){

                File file = new File(currentImagePath);
                Uri contentUri = Uri.fromFile(file);
                profileImage.setImageURI(contentUri);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);


            }
        }
        else if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                profileImage.setImageURI(contentUri);
                currentImagePath = getRealPathFromURI(contentUri);


            }
        }
    }

    private void selectImageFromGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }





    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        System.out.println("here");

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentImagePath = image.getAbsolutePath();
        return image;
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

}