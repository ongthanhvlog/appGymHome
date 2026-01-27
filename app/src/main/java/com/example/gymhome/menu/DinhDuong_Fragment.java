    package com.example.gymhome.menu;

    import android.Manifest;
    import android.app.Activity;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.Bitmap;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.Toast;

    import com.example.gymhome.R;

    import java.io.IOException;

    public class DinhDuong_Fragment extends Fragment {

        private static final int REQUEST_CAMERA = 1;
        private static final int REQUEST_GALLERY = 2;
        private static final int CAMERA_PERMISSION_CODE = 101;

        ImageView ic_camera, ic_image, imgHinhAnh;
        Button btnGui;

        public DinhDuong_Fragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_dinh_duong, container, false);

            // Ánh xạ view
            ic_camera = view.findViewById(R.id.ic_camera);
            ic_image = view.findViewById(R.id.ic_image);
            imgHinhAnh = view.findViewById(R.id.imgHinhAnh);
            btnGui = view.findViewById(R.id.btnGui);

            // Mở camera
            ic_camera.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_CODE);
                } else {
                    openCamera();
                }
            });

            // Mở thư viện ảnh
            ic_image.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);
            });

            btnGui.setOnClickListener(v -> {
                // TODO: Gửi text + ảnh
            });

            return view;
        }

        private void openCamera() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == Activity.RESULT_OK && data != null) {

                // Ảnh từ camera
                if (requestCode == REQUEST_CAMERA) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imgHinhAnh.setImageBitmap(bitmap);
                }

                // Ảnh từ thư viện
                if (requestCode == REQUEST_GALLERY) {
                    Uri imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().getContentResolver(), imageUri);
                        imgHinhAnh.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == CAMERA_PERMISSION_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(requireContext(), "Camera permission is required to use camera.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
