package com.adk.projectsmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
import java.util.List;

public class StatisticsChartFragment extends Fragment implements OnChartValueSelectedListener {

    BarChart barChart;
    BarDataSet barDataSet;
    BarData data;
    ArrayList<BarEntry> barGroup1;
    ArrayList<String> list;
    //LoadData loadData;
    //TasksModel tasksModel;
    ViewPager viewPager;
    TaskAdapter taskAdapter;
    List<TasksModel> tasksModelList = new ArrayList<>();
    TasksFragment tasksFragment;
    //Load data for barChart
    static int teamMembersValue, topPriorityValue, WIPValue, CompletedValue, DeadlineValue, onHoldValue;
    static int deadlineDays;
    static String accentLight, accentDark;
    FilterListener filterListener;
    public Firebase mRef;
    private String mUserId;
    private String membersUrl;
    private ArrayList<Long> nodesList;


    public interface FilterListener{
         void setFilter(String filterWIP);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            filterListener = (FilterListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_chart, container, false);
        barChart = (BarChart)view.findViewById(R.id.barchart);
        //tasksModel = new TasksModel();
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);



        //syncMembers();
        //taskAdapter = new TaskAdapter(tasksModelList, getActivity());
        //reference to LoadData class to load the necessary data
        //loadData = new LoadData();


        tasksFragment = new TasksFragment();

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    public void syncMembers(){
        mRef = new Firebase(FirebaseConfig.FIREBASE_URL);
        //login/reg
        try {
            mUserId = mRef.getAuth().getUid();
        } catch (Exception e) {
            Log.d("StatisticsChart", "error");
        }

        membersUrl = FirebaseConfig.FIREBASE_URL  + "/users/" + mUserId;
        nodesList = new ArrayList<>();
        //syncing with firebase
        Firebase ref = new Firebase(membersUrl);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getChildrenCount();
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    nodesList.add(snap.getChildrenCount());
                }
                initiateStatistics();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }


    private void initiateStatistics(){
        //hides titles and background
        //barChart.getXAxis().setEnabled(false);
        //barChart.getAxisLeft().setEnabled(false);
        //barChart.getAxisRight().setEnabled(false);
        barChart.setDescription("");
        barChart.getLegend().setEnabled(true);
        // barChart.setBackgroundColor(Color.parseColor("#34FF89"));
        barGroup1 = new ArrayList<>();
        barGroup1.add(new BarEntry(nodesList.get(1), 0));
        barGroup1.add(new BarEntry(nodesList.get(2), 1));
        barGroup1.add(new BarEntry(nodesList.get(0), 2));
        barGroup1.add(new BarEntry(0, 3));
        barGroup1.add(new BarEntry(0, 4));
        barGroup1.add(new BarEntry(0, 5));

        list = new ArrayList<>();
        list.add("Team members");
        list.add("Tasks");
        list.add("Description");
        list.add("Calendar");
        list.add("Events");
        list.add("Notes");

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
        //forces all labels from list object to be visible
        barChart.getXAxis().setLabelsToSkip(0);
    }

    public int[] barColors(){
        return new int[] {Color.parseColor("#53ebdb"), Color.parseColor("#45c4b6")};
        //should use loadData.getAccentLight(); and loadData.getAccentDark();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        int chart = barGroup1.indexOf(e);
        switch(chart) {
            case 0:
                //Team members
                filterListener.setFilter("Members");
                viewPager.setCurrentItem(1);
                break;
            case 1:
                //Top priority
                filterListener.setFilter("Priority");
                viewPager.setCurrentItem(2);
                break;
            case 2:
                //WIP
                filterListener.setFilter("WIP");
                viewPager.setCurrentItem(2);
                break;
            case 3:
                //Completed
                filterListener.setFilter("Completed");
                viewPager.setCurrentItem(2);
                break;
            case 4:
                //On hold
                filterListener.setFilter("Pause");
                viewPager.setCurrentItem(2);
                break;
            case 5:
                //Deadline
                //TODO: set a deadline
                filterListener.setFilter("Deadline");
                viewPager.setCurrentItem(2);
                break;
        }

        }


    @Override
    public void onNothingSelected() {

    }



    //class that provides data for the graphs and by using setters the titles and values are loaded into the bars

    public class LoadData{
        //String teamMembers, topPriority, WIP, Completed, Deadline, onHold;
    }

}

