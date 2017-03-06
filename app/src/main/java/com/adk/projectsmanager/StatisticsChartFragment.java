package com.adk.projectsmanager;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

public class StatisticsChartFragment extends Fragment implements OnChartValueSelectedListener {

    BarChart barChart;
    BarDataSet barDataSet;
    BarData data;
    ArrayList<BarEntry> barGroup1;
    ArrayList<String> list;
    LoadData loadData;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_chart, container, false);
        barChart = (BarChart)view.findViewById(R.id.barchart);
        //reference to LoadData class to load the necessary data
        loadData = new LoadData();
        //hides titles and background
        //barChart.getXAxis().setEnabled(false);
        //barChart.getAxisLeft().setEnabled(false);
        //barChart.getAxisRight().setEnabled(false);
        barChart.setDescription("");
        barChart.getLegend().setEnabled(true);
        // barChart.setBackgroundColor(Color.parseColor("#34FF89"));

        barGroup1 = new ArrayList<>();
        barGroup1.add(new BarEntry(8, 0));
        barGroup1.add(new BarEntry(15, 1));
        barGroup1.add(new BarEntry(5, 2));
        barGroup1.add(new BarEntry(20, 3));
        barGroup1.add(new BarEntry(15, 4));
        barGroup1.add(new BarEntry(19, 5));

        list = new ArrayList<>();
        list.add("Team members");
        list.add("Top priority");
        list.add("WIP");
        list.add("Completed");
        list.add("Deadline");
        list.add("On hold");

        barDataSet = new BarDataSet(barGroup1, "Project statistics");
        data = new BarData(list, barDataSet);
        // set the data and list of labels into chart
        barChart.setData(data);
        barChart.invalidate();
        //animate speed for bars
        barChart.animateY(3000);
        //sets colors for bars
        barDataSet.setColors(barColors(), 180);
        //highlight selected bar
        barDataSet.setHighLightAlpha(0);
        //onClickListener for each bar
        barChart.setOnChartValueSelectedListener(this);
        //disables zoom
        barChart.setScaleEnabled(false);
        //draws values inside the bars
        barChart.setDrawValueAboveBar(false);
        //format the values to int
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "" + ((int) value);
            }
        });
        //sets values text size
        barDataSet.setValueTextSize(10f);
        //hides values
        barDataSet.setDrawValues(true);

        return view;
    }




    public int[] barColors(){
        return new int[] {Color.parseColor("#53ebdb"), Color.parseColor("#45c4b6")};
        //should use loadData.getAccentLight(); and loadData.getAccentDark();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Toast.makeText(getActivity(), barGroup1.indexOf(e)+"", Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onNothingSelected() {

    }



    //class that provides data for the graphs and by using setters the titles and values are loaded into the bars
    public class LoadData{
        //String teamMembers, topPriority, WIP, Completed, Deadline, onHold;
        int teamMembersValue, topPriorityValue, WIPValue, CompletedValue, DeadlineValue, onHoldValue;
        int deadlineDays;
        String accentLight, accentDark;

        public LoadData(int teamMembersValue, String accentDark, String accentLight, int deadlineDays, int onHoldValue, int deadlineValue, int completedValue, int WIPValue, int topPriorityValue) {
            this.teamMembersValue = teamMembersValue;
            this.accentDark = accentDark;
            this.accentLight = accentLight;
            this.deadlineDays = deadlineDays;
            this.onHoldValue = onHoldValue;
            DeadlineValue = deadlineValue;
            CompletedValue = completedValue;
            this.WIPValue = WIPValue;
            this.topPriorityValue = topPriorityValue;
        }

        LoadData() {
        }

        int getTeamMembersValue() {
            return teamMembersValue;
        }

        String getAccentDark() {
            return accentDark;
        }

        String getAccentLight() {
            return accentLight;
        }

        int getDeadlineDays() {
            return deadlineDays;
        }

        int getOnHoldValue() {
            return onHoldValue;
        }

        int getDeadlineValue() {
            return DeadlineValue;
        }

        int getCompletedValue() {
            return CompletedValue;
        }

        int getWIPValue() {
            return WIPValue;
        }

        int getTopPriorityValue() {
            return topPriorityValue;
        }

        public void setTeamMembersValue(int teamMembersValue) {
            this.teamMembersValue = teamMembersValue;
        }

        public void setAccentDark(String accentDark) {
            this.accentDark = accentDark;
        }

        public void setAccentLight(String accentLight) {
            this.accentLight = accentLight;
        }

        public void setDeadlineDays(int deadlineDays) {
            this.deadlineDays = deadlineDays;
        }

        public void setOnHoldValue(int onHoldValue) {
            this.onHoldValue = onHoldValue;
        }

        public void setDeadlineValue(int deadlineValue) {
            DeadlineValue = deadlineValue;
        }

        public void setCompletedValue(int completedValue) {
            CompletedValue = completedValue;
        }

        public void setWIPValue(int WIPValue) {
            this.WIPValue = WIPValue;
        }

        public void setTopPriorityValue(int topPriorityValue) {
            this.topPriorityValue = topPriorityValue;
        }
    }
}

