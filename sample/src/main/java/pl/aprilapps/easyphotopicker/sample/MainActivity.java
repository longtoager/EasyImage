package pl.aprilapps.easyphotopicker.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import logutils.LogUtil;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import utils.CommonUtils;
import utils.FileUtil;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.image_view)
    protected ImageView imageView;
    private  final int ACTIVITY_CAMERA_REQUESTCODE=3005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        onClick();

        /**
         * If saving in public app folder inside Pictures by using saveInAppExternalFilesDir,
         * write permission after SDK 18 is NOT required as can be seen in manifest.
         *
         * If saving in the root of sdcard inside Pictures by using saveInRootPicturesDirectory,
         * permission is required.
         *
         * By default, if no configuration is set Images Folder Name will be EasyImage, and save
         * location will be in the ExternalFilesDir of the app.
         * */
        EasyImage.configuration(this)
                .setImagesFolderName("Sample app images")
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);

//        EasyImage.configuration(this)
//                .setImagesFolderName("Sample app images")
//                .saveInRootPicturesDirectory();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.camera_button)
    protected void onTakePhotoClicked() {

        /**Permission check only required if saving pictures to root of sdcard*/
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openCamera(this, 0);
        } else {
            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openCamera(MainActivity.this, 0);
                }

                @Override
                public void permissionRefused() {

                }
            });
        }
    }

    @OnClick(R.id.documents_button)
    protected void onPickFromDocumentsClicked() {
        /** Some devices such as Samsungs which have their own gallery app require write permission. Testing is advised! */

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openDocuments(this, 0);
        } else {
            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openDocuments(MainActivity.this, 0);
                }

                @Override
                public void permissionRefused() {

                }
            });
        }
    }

    @OnClick(R.id.gallery_button)
    protected void onPickFromGaleryClicked() {
        /** Some devices such as Samsungs which have their own gallery app require write permission. Testing is advised! */
        EasyImage.openGallery(this, 0);
    }

    @OnClick(R.id.chooser_button)
    protected void onChooserClicked() {
        EasyImage.openChooserWithDocuments(this, "Pick source", 0);
    }

    @OnClick(R.id.chooser_button2)
    protected void onChooserWithGalleryClicked() {
        EasyImage.openChooserWithGallery(this, "Pick source", 0);
    }


    protected void onClick(){


             findViewById(R.id.chooser_button3).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (CommonUtils.isExistCamera(MainActivity.this)) {
                         Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
                         Uri imageUri = Uri.fromFile(FileUtil.getHeadPhotoFileRaw());
                         intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                         intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                         startActivityForResult(intent, ACTIVITY_CAMERA_REQUESTCODE);
                     } else {
                         Toast.makeText(MainActivity.this,
                                 "未发现您的摄像头，请确认您的设备存在摄像头再使用此功能～",
                                 Toast.LENGTH_SHORT).show();
                     }
                 }
             });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        switch (requestCode) {

            case ACTIVITY_CAMERA_REQUESTCODE:
                if (resultCode == Activity.RESULT_OK) {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 2;



                    int degree = FileUtil.readPictureDegree( FileUtil.getHeadPhotoFileRaw().getAbsolutePath());
                    Bitmap cameraBitmap = BitmapFactory.decodeFile(FileUtil.getHeadPhotoFileRaw().getAbsolutePath(), bitmapOptions);


                    cameraBitmap = FileUtil.rotaingImageView(degree, cameraBitmap);



                    LogUtil.i("MainActivityabc", FileUtil.saveCutBitmapForCache2(MainActivity.this,cameraBitmap).getAbsolutePath());

                    Picasso.with(this)
                            .load(FileUtil.saveCutBitmapForCache2(MainActivity.this,cameraBitmap))
                            .fit()
                            .centerCrop()
                            .into(imageView);


                }
                break;

        }









        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {


                LogUtil.i("MainActivity_image",e.toString()+"__Exception");
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                LogUtil.i("MainActivity_image",imageFile.getAbsolutePath()+"___"+imageFile.length());

                //Handle the image
                onPhotoReturned(imageFile);





            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MainActivity.this);
                    if (photoFile != null) photoFile.delete();
                }



                LogUtil.i("MainActivity_image","onCanceled");
            }
        });
    }

    private void onPhotoReturned(File photoFile) {
        Picasso.with(this)
                .load(photoFile)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    @Override
    protected void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }
}
