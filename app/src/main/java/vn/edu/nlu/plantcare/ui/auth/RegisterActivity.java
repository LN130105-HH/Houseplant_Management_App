package vn.edu.nlu.plantcare.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import vn.edu.nlu.plantcare.databinding.ActivityRegisterBinding;
import vn.edu.nlu.plantcare.ui.MainActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> handleRegister());

        binding.tvGoToLogin.setOnClickListener(v -> finish());

        authViewModel.getAuthSuccess().observe(this, success -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnRegister.setEnabled(true);
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finishAffinity();
            }
        });

        authViewModel.getAuthError().observe(this, error -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnRegister.setEnabled(true);
            if (error != null) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRegister() {
        String displayName = binding.etDisplayName.getText() != null ? binding.etDisplayName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(displayName)) {
            binding.tilDisplayName.setError("Vui lòng nhập họ và tên");
            return;
        } else {
            binding.tilDisplayName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Vui lòng nhập Email");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.tilPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnRegister.setEnabled(false);
        authViewModel.register(email, password, displayName);
    }
}
