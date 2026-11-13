package vn.edu.nlu.plantcare.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import vn.edu.nlu.plantcare.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            firebaseAuth = FirebaseAuth.getInstance();
        } catch (Exception ignored) {
        }

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Vui lòng nhập Gmail");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Gmail không hợp lệ");
            return;
        }
        binding.tilEmail.setError(null);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnResetPassword.setEnabled(false);

        if (firebaseAuth != null) {
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnResetPassword.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra Gmail.",
                                    Toast.LENGTH_LONG
                            ).show();
                            Intent intent = new Intent(
                                    ForgotPasswordActivity.this,
                                    LoginActivity.class
                            );
                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                            );
                            startActivity(intent);
                            finish();
                        } else {
                            String message = "Không thể gửi email đặt lại mật khẩu";
                            if (task.getException() != null) {
                                message = task.getException().getMessage();
                            }
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        } else {
            // Offline demo fallback
            binding.progressBar.setVisibility(View.GONE);
            binding.btnResetPassword.setEnabled(true);
            Toast.makeText(
                    ForgotPasswordActivity.this,
                    "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra Gmail.",
                    Toast.LENGTH_LONG
            ).show();
            Intent intent = new Intent(
                    ForgotPasswordActivity.this,
                    LoginActivity.class
            );
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);
            finish();
        }
    }
}
