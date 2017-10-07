package com.adk.projectsmanager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by DeniE46 on 2/20/2017.
 */

public class CalendarFragment extends Fragment implements View.OnClickListener{

    FloatingActionButton fab;
    LinearLayout viewNote, newNote;
    Animation animationExit, animationEnter;
    Button saveNote;
    CalendarView datePicker, calendarView;
    TextView displayNote, displayTitle;
    EditText readNote, readTitle;
    List<EventDay> mEventDays;
    String title;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment_layout, container, false);
        setHasOptionsMenu(true);
        fab = (FloatingActionButton) view.findViewById(R.id.calendar_fab);
        fab.bringToFront();
        fab.setOnClickListener(this);
        viewNote = (LinearLayout)view.findViewById(R.id.view_note);
        newNote = (LinearLayout)view.findViewById(R.id.new_note);
        newNote.setVisibility(View.GONE);
        saveNote = (Button)view.findViewById(R.id.save_note);
        saveNote.setOnClickListener(this);
        datePicker = (CalendarView) view.findViewById(R.id.datePicker);
        datePicker.setVisibility(View.GONE);
        calendarView = (CalendarView)view.findViewById(R.id.calendarView);
        displayNote = (TextView)view.findViewById(R.id.set_note);
        displayTitle = (TextView)view.findViewById(R.id.set_title);
        readNote = (EditText)view.findViewById(R.id.get_note);
        readTitle = (EditText)view.findViewById(R.id.get_title);
        mEventDays = new ArrayList<>();
        title = "Test Title";
        //myEventDay = new MyEventDay();
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                //displayNote.setText(getFormattedDate(eventDay.getCalendar().getTime()));
                    try {
                        MyEventDay myEventDay = (MyEventDay) eventDay;

                        displayNote.setText(myEventDay.getNote());
                        displayTitle.setText(myEventDay.getTitle());
                    }
                    catch (ClassCastException e){
                        displayTitle.setText("No notes");
                        displayNote.setText("");
                    }


            }
        });
        return view;
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calendar_fragment_menu_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        animationExit = AnimationUtils.loadAnimation(getActivity(), R.anim.exit_to_top);
        animationEnter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_top);

        switch(v.getId()) {
            case R.id.calendar_fab:
                if (newNote.getVisibility() == View.VISIBLE) {
                    newNote.setAnimation(animationExit);
                    newNote.setVisibility(View.GONE);
                    viewNote.setAnimation(animationEnter);
                    viewNote.setVisibility(View.VISIBLE);

                    datePicker.setAnimation(animationExit);
                    datePicker.setVisibility(View.GONE);
                    calendarView.setAnimation(animationEnter);
                    calendarView.setVisibility(View.VISIBLE);
                } else {
                    newNote.setAnimation(animationEnter);
                    newNote.setVisibility(View.VISIBLE);
                    viewNote.setAnimation(animationExit);
                    viewNote.setVisibility(View.GONE);

                    datePicker.setAnimation(animationEnter);
                    datePicker.setVisibility(View.VISIBLE);
                    calendarView.setAnimation(animationExit);
                    calendarView.setVisibility(View.GONE);
                }
                break;
            case R.id.save_note:

                MyEventDay myEventDay = new MyEventDay(datePicker.getSelectedDate(), R.drawable.ic_note, readNote.getText().toString(), readTitle.getText().toString());
                mEventDays.add(myEventDay);
                calendarView.setEvents(mEventDays);

                newNote.setAnimation(animationExit);
                newNote.setVisibility(View.GONE);
                viewNote.setAnimation(animationEnter);
                viewNote.setVisibility(View.VISIBLE);

                datePicker.setAnimation(animationExit);
                datePicker.setVisibility(View.GONE);
                calendarView.setAnimation(animationEnter);
                calendarView.setVisibility(View.VISIBLE);

                readTitle.setText("");
                readNote.setText("");

                break;
        }
    }
}
