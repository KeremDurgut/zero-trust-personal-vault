package me.keremdurgut.zero_trust_personal_vault.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import me.keremdurgut.zero_trust_personal_vault.R;
import me.keremdurgut.zero_trust_personal_vault.databinding.FragmentOnboardingBinding;
import me.keremdurgut.zero_trust_personal_vault.util.PinManager;

public class OnboardingFragment extends Fragment {

    private FragmentOnboardingBinding binding;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

                navigateToLogin();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (PinManager.isOnboardingDone(requireContext())) {
            navigateToLogin();
            return;
        }

        OnboardingAdapter adapter = new OnboardingAdapter(this::finishOnboarding);
        binding.viewPager.setAdapter(adapter);
    }

    private void finishOnboarding() {
        PinManager.setOnboardingDone(requireContext(), true);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                navigateToLogin();
            }
        } else {
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_onboarding_to_login);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    static class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

        private final int[] onboardingLayouts = {
                R.layout.fragment_onboarding_1,
                R.layout.fragment_onboarding_2,
                R.layout.fragment_onboarding_3
        };

        private final Runnable onFinish;

        OnboardingAdapter(Runnable onFinish) {
            this.onFinish = onFinish;
        }

        @NonNull
        @Override
        public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new OnboardingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
            View beginBtn = holder.itemView.findViewById(R.id.beginButton);

            if (beginBtn != null) {
                beginBtn.setOnClickListener(v -> onFinish.run());
            }
        }

        @Override
        public int getItemCount() {
            return onboardingLayouts.length;
        }

        @Override
        public int getItemViewType(int position) {
            return onboardingLayouts[position];
        }

        static class OnboardingViewHolder extends RecyclerView.ViewHolder {
            OnboardingViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
