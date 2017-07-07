package com.adk.projectsmanager;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ramotion.foldingcell.FoldingCell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

      List<TasksModel> tasksList = new ArrayList<>();



     public String filterWIP = "null";



     public void setFilterWIP(String filterWIP) {
         this.filterWIP = filterWIP;
     }

     private final static int FADE_DURATION = 300; // in milliseconds

     private TasksFragment tasksFragment;


     public TaskAdapter(List<TasksModel> tasksList, TasksFragment tasksFragment) {
         this.tasksFragment = tasksFragment;
         this.tasksList = tasksList;

     }



     class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

         TextView taskName, taskOwner, taskDaysLeft, taskDescription, taskStatus, taskNumber, taskDeadlineDate;
         ImageView taskDeadlineColor;
         CardView card;
         Button taskCardExpand, taskCardCollapse;
         final FoldingCell foldingCell;
         int position;

         int[] popupLocation = new int[2];

         MyViewHolder(View view){
            super (view);
            //gets control over the cardView and widgets


             card = (CardView)view.findViewById(R.id.cell_title_view);
             foldingCell = (FoldingCell)view.findViewById(R.id.folding_cell);

             taskCardExpand = (Button)view.findViewById(R.id.expand_content_view_cell);
             taskCardExpand.setOnClickListener(this);
             //taskCardCollapse.setOnClickListener(this);
             taskNumber = (TextView)view.findViewById(R.id.task_card_number);

             //foldingCell.initialize(1000, Color.DKGRAY, 5);

             foldingCell.setTag(card);
             foldingCell.setOnClickListener(this);
             foldingCell.getLocationOnScreen(popupLocation);

             taskName = (TextView)view.findViewById(R.id.task_name);
             taskOwner = (TextView)view.findViewById(R.id.task_owner);
             taskDeadlineDate = (TextView)view.findViewById(R.id.task_deadline_date);
             //taskDaysLeft = (TextView)view.findViewById(R.id.days_left);
             //taskDescription = (TextView)view.findViewById(R.id.task_description);
             //taskStatus = (TextView)view.findViewById(R.id.task_status);
             taskDeadlineColor = (ImageView)view.findViewById(R.id.task_deadline_color);





         }



         @Override
         public void onClick(View view) {
             switch(view.getId()){
                 case R.id.expand_content_view_cell:
                     foldingCell.toggle(false);
                     foldingCell.fold(true);
                     break;
                 case R.id.folding_cell:
                     //TasksFragment tasksFragment = new TasksFragment();
                     //passing position and context. Context is obtained by getting the context of any object inside the class
                     //tasksFragment.taskOptions(position, 0);
                     card.getLocationOnScreen(popupLocation);
                     position =  getAdapterPosition();
                     //Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
                     //showPopup(view);
                     //PopUpWindow popUpWindow = new PopUpWindow();
                     //popUpWindow.showPopup(context, position, popupLocation, tasksFragment);
                     TasksFragment.PopUpWindow popUpWindow = tasksFragment. new PopUpWindow();
                     popUpWindow.showPopup(position, popupLocation);
                     break;
                 case R.id.collapse_content_view_cell:
                     foldingCell.toggle(true);
                     foldingCell.fold(false);

                     break;
             }
         }
     }


     @Override
     public int getItemViewType(int position) {
         return super.getItemViewType(position);
     }

     @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_row, parent, false);
        return new MyViewHolder(itemView);
    }



    //loads information in cards
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
       //statements of type holder.*.setText(tm.get*) set the text for the textView in cardView

        TasksModel tm = tasksList.get(position);
        holder.foldingCell.fold(true);
        String cardPosition = Integer.toString(position +1);
        holder.card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });


        //checks how many days are left to deadline for a task and colors the cardView respectfully




            //silver
            holder.taskNumber.setText(cardPosition);
            holder.taskName.setText(tm.getTaskName());
            holder.taskOwner.setText(tm.getTaskOwner());
            holder.taskDeadlineDate.setText(tm.getTaskDeadlineDate());


            // holder.taskDescription.setText(tm.getTaskDescription());
            // String daysToGo = String.valueOf(tm.getTimeRemaining());
            //holder.card.setCardBackgroundColor(Color.argb(190, 223, 230, 225));
            // holder.taskStatus.setText(R.string.task_status_ready);
            //holder.taskDaysLeft.setText(R.string.task_days_left);


             if(tm.getTaskStatus().equals("Completed")){
                 //silver
                 holder.taskDeadlineColor.setBackgroundColor(Color.argb(190, 223, 230, 225));
             }
            else if(tm.getTaskStatus().equals("Paused")){
                 //black
                 holder.taskDeadlineColor.setBackgroundColor(Color.argb(190, 85, 85, 85));
             }
            else if (tm.getTimeRemaining() >= 10) {
               // holder.taskDaysLeft.setText(daysToGo);
                //green
                //holder.card.setCardBackgroundColor(Color.argb(190, 119, 253, 61));
                holder.taskDeadlineColor.setBackgroundColor(Color.argb(190, 119, 253, 61));
            }
            else if ((tm.getTimeRemaining() < 10) && (tm.getTimeRemaining() >= 5)) {
               // holder.taskDaysLeft.setText(daysToGo);
                //yellow
               // holder.card.setCardBackgroundColor(Color.argb(190, 253, 247, 61));
                holder.taskDeadlineColor.setBackgroundColor(Color.argb(190, 253, 247, 61));
            }
            else if ((tm.getTimeRemaining() < 5) && (tm.getTimeRemaining() >= 3)) {
               // holder.taskDaysLeft.setText(daysToGo);
                //orange
               // holder.card.setCardBackgroundColor(Color.argb(190, 253, 144, 61));
                holder.taskDeadlineColor.setBackgroundColor(Color.argb(190, 253, 144, 61));
            }
            else if (tm.getTimeRemaining() < 3 && tm.getTimeRemaining() >=0) {
                //holder.taskDaysLeft.setText(daysToGo);
                //red
               // holder.card.setCardBackgroundColor(Color.argb(190, 253, 61, 61));
                holder.taskDeadlineColor.setBackgroundColor(Color.argb(190, 253, 61, 61));
            }
            else if(tm.getTimeRemaining() < 0){
               // holder.taskStatus.setText(R.string.task_status_expired);
               // holder.taskDaysLeft.setText(R.string.task_days_left);
                //red
               // holder.card.setCardBackgroundColor(Color.argb(190, 253, 61, 61));
                holder.taskDeadlineColor.setBackgroundColor(Color.argb(190, 253, 61, 61));
            }
            //./checks how many days are left to deadline for a task and colors the cardView respectfully


        //setFadeAnimation(holder.itemView);
       // setScaleAnimation(holder.itemView);

    }



    //animation when tasks are loading
    private void setScaleAnimation(View view) {
        //TODO: looks at animation classes to see if there is a show from left
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }




    @Override
    public int getItemCount() {
        return tasksList.size();
    }

}
