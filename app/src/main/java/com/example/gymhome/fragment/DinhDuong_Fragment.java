package com.example.gymhome.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymhome.BuildConfig;
import com.example.gymhome.R;
import com.example.gymhome.adapter.ChatAdapter;
import com.example.gymhome.model.ChatMessage;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DinhDuong_Fragment extends Fragment {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int CAMERA_PERMISSION_CODE = 101;

    private ImageView imgHinhAnh, btnRemoveImage;
    private EditText edtNhapNoiDung;
    private RecyclerView rvChat, rvHistory;
    private LinearLayout layout_goi_y;
    private View layoutPreview;
    private ProgressBar progressBar;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private Bitmap selectedBitmap;

    private List<ChatSession> historyList;
    private HistoryAdapter historyAdapter;

    private GenerativeModelFutures model;
    private ChatFutures chatSession;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String userId;
    private String currentSessionId;
    private DrawerLayout drawerLayout;

    public DinhDuong_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.GEMINI_API_KEY
        );
        model = GenerativeModelFutures.from(gm);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dinhduong, container, false);

        // Ánh xạ view
        drawerLayout = view.findViewById(R.id.drawer_layout);
        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        View ic_camera = view.findViewById(R.id.ic_camera);
        View ic_image = view.findViewById(R.id.ic_image);
        imgHinhAnh = view.findViewById(R.id.imgHinhAnh);
        btnRemoveImage = view.findViewById(R.id.btnRemoveImage);
        View btnGui = view.findViewById(R.id.btnGui);
        edtNhapNoiDung = view.findViewById(R.id.edtNhapNoiDung);
        rvChat = view.findViewById(R.id.rvChat);
        rvHistory = view.findViewById(R.id.rvHistory);
        layout_goi_y = view.findViewById(R.id.layout_goi_y);
        layoutPreview = view.findViewById(R.id.layoutPreview);
        progressBar = view.findViewById(R.id.progressBar);
        View btnNewChat = view.findViewById(R.id.btnNewChat);

        // Nút mở menu lịch sử
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Nút Cuộc trò chuyện mới - Xóa trắng màn hình
        btnNewChat.setOnClickListener(v -> {
            chatMessages.clear();
            chatAdapter.notifyDataSetChanged();
            chatSession = model.startChat(); // Reset AI session
            currentSessionId = null; // Reset session ID
            layout_goi_y.setVisibility(View.VISIBLE);
            drawerLayout.closeDrawer(GravityCompat.START);
            Toast.makeText(requireContext(), "Bắt đầu cuộc trò chuyện mới", Toast.LENGTH_SHORT).show();
        });

        // Setup RecyclerView Chat chính
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        rvChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvChat.setAdapter(chatAdapter);

        // Setup RecyclerView Lịch sử
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList, new HistoryAdapter.OnHistoryActionListener() {
            @Override
            public void onItemClick(ChatSession session) {
                drawerLayout.closeDrawer(GravityCompat.START);
                loadChatMessages(session.id);
            }

            @Override
            public void onMoreClick(View view, ChatSession session) {
                showPopupMenu(view, session);
            }
        });
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setAdapter(historyAdapter);

        // Load dữ liệu ban đầu
        if (userId != null) {
            loadHistoryMenu();
            chatSession = model.startChat();
        }

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

        btnRemoveImage.setOnClickListener(v -> {
            selectedBitmap = null;
            layoutPreview.setVisibility(View.GONE);
        });

        btnGui.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void showPopupMenu(View view, ChatSession session) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        final String pinAction = session.isPinned ? "Bỏ ghim" : "Ghim";
        popupMenu.getMenu().add(pinAction);
        popupMenu.getMenu().add("Đổi tên");
        popupMenu.getMenu().add("Xóa");

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle() == null) return false;
            String title = item.getTitle().toString();
            if (title.equals(pinAction)) {
                togglePinSession(session);
            } else if (title.equals("Đổi tên")) {
                showRenameDialog(session);
            } else if (title.equals("Xóa")) {
                showDeleteConfirmDialog(session);
            }
            return true;
        });
        popupMenu.show();
    }

    private void togglePinSession(ChatSession session) {
        if (userId == null) return;
        boolean newPinnedState = !session.isPinned;
        db.collection("NguoiDung").document(userId)
                .collection("LichSuTroChuyen").document(session.id)
                .update("isPinned", newPinnedState)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), newPinnedState ? "Đã ghim" : "Đã bỏ ghim", Toast.LENGTH_SHORT).show();
                    loadHistoryMenu();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showRenameDialog(ChatSession session) {
        EditText editText = new EditText(requireContext());
        editText.setText(session.title);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);

        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(padding, 8, padding, 8);
        editText.setLayoutParams(params);
        container.addView(editText);

        new AlertDialog.Builder(requireContext())
                .setTitle("Đổi tên cuộc trò chuyện")
                .setView(container)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newTitle = editText.getText().toString().trim();
                    if (!newTitle.isEmpty()) {
                        renameSession(session.id, newTitle);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void renameSession(String sessionId, String newTitle) {
        if (userId == null) return;
        db.collection("NguoiDung").document(userId)
                .collection("LichSuTroChuyen").document(sessionId)
                .update("tieuDe", newTitle)
                .addOnSuccessListener(aVoid -> loadHistoryMenu());
    }

    private void showDeleteConfirmDialog(ChatSession session) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa cuộc trò chuyện")
                .setMessage("Bạn có chắc chắn muốn xóa cuộc trò chuyện này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteSession(session.id))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteSession(String sessionId) {
        if (userId == null) return;

        // Lấy tất cả tin nhắn con để xóa sạch trước
        db.collection("NguoiDung").document(userId)
                .collection("LichSuTroChuyen").document(sessionId)
                .collection("TinNhan")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();

                    // Thêm các lệnh xóa tin nhắn vào batch
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.delete(doc.getReference());
                    }

                    // Sau đó thêm lệnh xóa chính document phiên chat
                    DocumentReference sessionRef = db.collection("NguoiDung").document(userId)
                            .collection("LichSuTroChuyen").document(sessionId);
                    batch.delete(sessionRef);

                    // Thực thi toàn bộ lệnh xóa
                    batch.commit().addOnSuccessListener(aVoid -> {
                        if (sessionId.equals(currentSessionId)) {
                            chatMessages.clear();
                            chatAdapter.notifyDataSetChanged();
                            currentSessionId = null;
                            layout_goi_y.setVisibility(View.VISIBLE);
                            chatSession = model.startChat();
                        }
                        loadHistoryMenu();
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Đã xóa cuộc trò chuyện", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }

    private void loadHistoryMenu() {
        if (userId == null) return;
        db.collection("NguoiDung").document(userId)
                .collection("LichSuTroChuyen")
                .orderBy("thoiGian", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String title = doc.getString("tieuDe");
                        String id = doc.getId();
                        Boolean pinned = doc.getBoolean("isPinned");
                        if (title != null) {
                            historyList.add(new ChatSession(id, title, pinned != null && pinned));
                        }
                    }
                    // Sắp xếp lại danh sách: đưa mục đã ghim lên đầu
                    java.util.Collections.sort(historyList, (s1, s2) -> {
                        if (s1.isPinned == s2.isPinned) return 0;
                        return s1.isPinned ? -1 : 1;
                    });
                    historyAdapter.notifyDataSetChanged();
                });
    }

    private void loadChatMessages(String sessionId) {
        currentSessionId = sessionId;
        db.collection("NguoiDung").document(userId)
                .collection("LichSuTroChuyen").document(sessionId)
                .collection("TinNhan")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Content> history = new ArrayList<>();
                    layout_goi_y.setVisibility(View.GONE);
                    chatMessages.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String text = doc.getString("text");
                        Boolean isUser = doc.getBoolean("isUser");
                        String imageUrl = doc.getString("imageUrl");
                        if (text != null && isUser != null) {
                            ChatMessage msg;
                            if (imageUrl != null) {
                                msg = new ChatMessage(text, imageUrl, isUser);
                            } else {
                                msg = new ChatMessage(text, isUser);
                            }
                            
                            // Đánh dấu tin nhắn cũ trong lịch sử là đã chạy hiệu ứng
                            if (!isUser) {
                                msg.setAnimated(true);
                            }
                            
                            chatMessages.add(msg);

                            String role = isUser ? "user" : "model";
                            Content.Builder contentBuilder = new Content.Builder();
                            contentBuilder.setRole(role);
                            contentBuilder.addText(text);
                            history.add(contentBuilder.build());
                        }
                    }
                    chatAdapter.notifyDataSetChanged();
                    rvChat.scrollToPosition(chatMessages.size() - 1);
                    chatSession = model.startChat(history);
                });
    }

    private void uploadImageAndSaveMessage(String text, Bitmap bitmap) {
        if (userId == null) return;

        StorageReference storageRef = storage.getReference().child("chat_images/" + userId + "/" + System.currentTimeMillis() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            saveMessageToFirestore(text, uri.toString(), true);
        })).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            saveMessageToFirestore(text, null, true);
        });
    }

    private void saveMessageToFirestore(String text, String imageUrl, boolean isUser) {
        if (userId == null) return;

        final String finalEffectiveText = text != null ? text : (isUser ? "[Hình ảnh]" : "");

        if (currentSessionId == null && isUser) {
            // Tạo phiên trò chuyện mới nếu là tin nhắn đầu tiên của user
            Map<String, Object> session = new HashMap<>();
            String title = finalEffectiveText.length() > 35 ? finalEffectiveText.substring(0, 35) + "..." : finalEffectiveText;
            session.put("tieuDe", title);
            session.put("thoiGian", FieldValue.serverTimestamp());
            session.put("isPinned", false);

            db.collection("NguoiDung").document(userId)
                    .collection("LichSuTroChuyen")
                    .add(session)
                    .addOnSuccessListener(documentReference -> {
                        currentSessionId = documentReference.getId();
                        addMessageToSession(currentSessionId, finalEffectiveText, imageUrl, isUser);
                        loadHistoryMenu();
                    });
        } else if (currentSessionId != null) {
            addMessageToSession(currentSessionId, finalEffectiveText, imageUrl, isUser);
        } else {
            // Thử lại nếu session đang được tạo
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                if (currentSessionId != null) {
                    addMessageToSession(currentSessionId, finalEffectiveText, imageUrl, isUser);
                }
            }, 2000);
        }
    }

    private void addMessageToSession(String sessionId, String text, String imageUrl, boolean isUser) {
        Map<String, Object> message = new HashMap<>();
        message.put("text", text);
        message.put("isUser", isUser);
        if (imageUrl != null) {
            message.put("imageUrl", imageUrl);
        }
        message.put("timestamp", FieldValue.serverTimestamp());

        db.collection("NguoiDung").document(userId)
                .collection("LichSuTroChuyen").document(sessionId)
                .collection("TinNhan")
                .add(message);
    }

    private void sendMessage() {
        String query = edtNhapNoiDung.getText().toString().trim();
        if (query.isEmpty() && selectedBitmap == null) return;

        layout_goi_y.setVisibility(View.GONE);

        ChatMessage userMessage = new ChatMessage(query, selectedBitmap, true);
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChat.scrollToPosition(chatMessages.size() - 1);

        if (selectedBitmap != null) {
            uploadImageAndSaveMessage(query, selectedBitmap);
        } else {
            saveMessageToFirestore(query, null, true);
        }

        edtNhapNoiDung.setText("");
        Bitmap bitmapToSend = selectedBitmap;
        selectedBitmap = null;
        layoutPreview.setVisibility(View.GONE);

        // Ẩn ProgressBar, dùng tin nhắn AI "Thinking" thay thế
        progressBar.setVisibility(View.GONE);

        // Thêm tin nhắn AI đang suy nghĩ vào danh sách
        ChatMessage thinkingMessage = new ChatMessage("", false, true);
        chatMessages.add(thinkingMessage);
        int thinkingPos = chatMessages.size() - 1;
        chatAdapter.notifyItemInserted(thinkingPos);
        rvChat.scrollToPosition(thinkingPos);

        Content.Builder contentBuilder = new Content.Builder();
        if (bitmapToSend != null) {
            contentBuilder.addText(query.isEmpty() ? "Phân tích món ăn này" : query);
            contentBuilder.addImage(bitmapToSend);
        } else {
            contentBuilder.addText(query);
        }
        Content content = contentBuilder.build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response;
        if (chatSession != null) {
            response = chatSession.sendMessage(content);
        } else {
            response = model.generateContent(content);
        }

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiText = result.getText();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        String finalAiText = (aiText != null && !aiText.isEmpty()) ? aiText : "Tôi chưa có câu trả lời.";
                        
                        // Cập nhật tin nhắn "Thinking" thành nội dung thật
                        thinkingMessage.setThinking(false);
                        thinkingMessage.setText(finalAiText);
                        chatAdapter.notifyItemChanged(thinkingPos);
                        
                        rvChat.scrollToPosition(chatMessages.size() - 1);
                        saveMessageToFirestore(finalAiText, null, false);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Xóa tin nhắn thinking nếu lỗi
                        if (chatMessages.contains(thinkingMessage)) {
                            chatMessages.remove(thinkingMessage);
                            chatAdapter.notifyDataSetChanged();
                        }
                        Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }, executor);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {
                selectedBitmap = (Bitmap) data.getExtras().get("data");
                imgHinhAnh.setImageBitmap(selectedBitmap);
                layoutPreview.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri imageUri = data.getData();
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    imgHinhAnh.setImageBitmap(selectedBitmap);
                    layoutPreview.setVisibility(View.VISIBLE);
                } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    // Model cho Phiên trò chuyện
    public static class ChatSession {
        public String id;
        public String title;
        public boolean isPinned;

        public ChatSession(String id, String title) {
            this(id, title, false);
        }

        public ChatSession(String id, String title, boolean isPinned) {
            this.id = id;
            this.title = title;
            this.isPinned = isPinned;
        }
    }

    // Adapter nội bộ cho Lịch sử
    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final List<ChatSession> list;
        private final OnHistoryActionListener listener;

        public interface OnHistoryActionListener {
            void onItemClick(ChatSession session);
            void onMoreClick(View view, ChatSession session);
        }

        public HistoryAdapter(List<ChatSession> list, OnHistoryActionListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_chat, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatSession session = list.get(position);
            holder.tvTitle.setText(session.title);
            
            if (session.isPinned) {
                holder.btnMore.setImageResource(R.drawable.ic_pin);
                holder.btnMore.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_main));
            } else {
                holder.btnMore.setImageResource(R.drawable.ic_more_vert);
                holder.btnMore.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black));
            }

            holder.itemView.setOnClickListener(v -> listener.onItemClick(session));
            holder.btnMore.setOnClickListener(v -> listener.onMoreClick(v, session));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ImageView btnMore;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                btnMore = itemView.findViewById(R.id.btnMore);
            }
        }
    }
}
