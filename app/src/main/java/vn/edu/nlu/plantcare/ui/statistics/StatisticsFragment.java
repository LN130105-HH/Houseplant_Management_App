package vn.edu.nlu.plantcare.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.repository.StatisticsRepository;
import vn.edu.nlu.plantcare.databinding.FragmentStatisticsBinding;
import vn.edu.nlu.plantcare.ui.auth.AuthViewModel;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private StatisticsViewModel statsViewModel;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        setupCharts();
        observeStatistics();

        String userId = authViewModel.getCurrentUserId();
        statsViewModel.loadStatistics(userId);
    }

    private void setupCharts() {
        // BarChart Setup
        BarChart barChart = binding.barChartCare;
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

        barChart.getAxisLeft().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        barChart.getAxisLeft().setGranularity(1f);

        // PieChart Setup
        PieChart pieChart = binding.pieChartRatio;
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleRadius(50f);
        pieChart.getLegend().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
    }

    private void observeStatistics() {
        statsViewModel.getCareOverview().observe(getViewLifecycleOwner(), overview -> {
            if (overview == null) return;

            binding.tvStatsWaterCount.setText(String.valueOf(overview.totalWaterings));
            binding.tvStatsFertilizerCount.setText(String.valueOf(overview.totalFertilizers));
            binding.tvStatsJournalCount.setText(String.valueOf(overview.totalJournals));
            binding.tvMostCaredPlant.setText(overview.mostCaredPlantName);

            updateBarChart(overview.last7DaysStats);
            updatePieChart(overview.totalWaterings, overview.totalFertilizers, overview.totalJournals);
        });
    }

    private void updateBarChart(List<StatisticsRepository.DayCareStats> dayStats) {
        if (dayStats == null || dayStats.isEmpty()) return;

        List<BarEntry> waterEntries = new ArrayList<>();
        List<BarEntry> fertilizerEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < dayStats.size(); i++) {
            StatisticsRepository.DayCareStats stat = dayStats.get(i);
            waterEntries.add(new BarEntry(i, stat.waterCount));
            fertilizerEntries.add(new BarEntry(i, stat.fertilizerCount));
            labels.add(stat.dayLabel);
        }

        BarDataSet waterSet = new BarDataSet(waterEntries, "Tưới nước");
        waterSet.setColor(ContextCompat.getColor(requireContext(), R.color.water_blue));

        BarDataSet fertilizerSet = new BarDataSet(fertilizerEntries, "Bón phân");
        fertilizerSet.setColor(ContextCompat.getColor(requireContext(), R.color.fertilizer_amber));

        BarData data = new BarData(waterSet, fertilizerSet);
        float groupSpace = 0.3f;
        float barSpace = 0.05f;
        float barWidth = 0.3f;

        data.setBarWidth(barWidth);
        binding.barChartCare.setData(data);
        binding.barChartCare.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChartCare.getXAxis().setAxisMinimum(0);
        binding.barChartCare.getXAxis().setAxisMaximum(dayStats.size());
        binding.barChartCare.groupBars(0, groupSpace, barSpace);
        binding.barChartCare.animateY(800);
        binding.barChartCare.invalidate();
    }

    private void updatePieChart(int waterCount, int fertilizerCount, int journalCount) {
        List<PieEntry> entries = new ArrayList<>();
        if (waterCount > 0) entries.add(new PieEntry(waterCount, "Tưới nước"));
        if (fertilizerCount > 0) entries.add(new PieEntry(fertilizerCount, "Bón phân"));
        if (journalCount > 0) entries.add(new PieEntry(journalCount, "Nhật ký"));

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "Chưa có hoạt động"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(requireContext(), R.color.water_blue));
        colors.add(ContextCompat.getColor(requireContext(), R.color.fertilizer_amber));
        colors.add(ContextCompat.getColor(requireContext(), R.color.primary));
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.pieChartRatio));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        binding.pieChartRatio.setData(data);
        binding.pieChartRatio.animateY(900);
        binding.pieChartRatio.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
