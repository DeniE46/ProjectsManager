package com.adk.projectsmanager;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.List;


class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

     private List<TasksModel> tasksList = new ArrayList<>();
     private TasksFragment tasksFragment;


    //TODO: check why fragment context is needed
      TaskAdapter(List<TasksModel> tasksList, TasksFragment tasksFragment) {
         this.tasksFragment = tasksFragment;
         this.tasksList = tasksList;

     }


     class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
         final FoldingCell foldingCell;
         CardView card;
         //visible part of FoldingCell
         TextView taskNumber, taskName, taskOwner, taskDeadlineDate, taskDifficulty;
         ImageView taskStatusColor;
         Button expandContentViewCard;

         //folded part of Folding Cell
         TextView taskContentNumber, taskContentName, taskContentOwner, taskContentPeopleWorking, taskContentPreviousStatus, taskContentCurrentStatus, taskContentNextStatus, taskContentDifficulty, taskContentPriority, taskContentDescription;
         ImageView taskContentOwnerPic;
         Button collapseContentViewCard;



         int taskCardPosition;
         int[] popupLocation = new int[2];

         MyViewHolder(View view){
            super (view);
             //the visible part of Folding cell
             card = (CardView)view.findViewById(R.id.cell_title_view);
             foldingCell = (FoldingCell)view.findViewById(R.id.folding_cell);
             foldingCell.setTag(card);
             foldingCell.setOnClickListener(this);
             //visible part of FoldingCell
             taskNumber = (TextView)view.findViewById(R.id.task_card_number);
             taskName = (TextView)view.findViewById(R.id.task_name);
             taskOwner = (TextView)view.findViewById(R.id.task_owner);
             taskDeadlineDate = (TextView)view.findViewById(R.id.task_deadline_date);
             taskDifficulty = (TextView)view.findViewById(R.id.task_difficulty);
             taskStatusColor = (ImageView)view.findViewById(R.id.task_deadline_color);
             expandContentViewCard = (Button)view.findViewById(R.id.expand_content_view_cell);
             expandContentViewCard.setOnClickListener(this);

             //folded part of Folding Cell
             taskContentNumber = (TextView)view.findViewById(R.id.task_content_task_card_number);
             taskContentName = (TextView)view.findViewById(R.id.task_content_task_name);
             taskContentOwner = (TextView)view.findViewById(R.id.task_content_owner_name);
             taskContentPeopleWorking = (TextView)view.findViewById(R.id.task_content_people_working);
             taskContentPreviousStatus = (TextView)view.findViewById(R.id.task_content_previous_task_status);
             taskContentCurrentStatus = (TextView)view.findViewById(R.id.task_content_current_task_status);
             taskContentNextStatus = (TextView)view.findViewById(R.id.task_content_next_task_status);
             taskContentDifficulty = (TextView)view.findViewById(R.id.task_content_task_difficulty);
             taskContentPriority = (TextView)view.findViewById(R.id.task_content_task_priority);
             taskContentDescription = (TextView)view.findViewById(R.id.task_content_task_description);
             taskContentOwnerPic = (ImageView)view.findViewById(R.id.task_content_task_owner_pic);
             collapseContentViewCard = (Button) view.findViewById(R.id.task_content_collapse_content_view_cell);
             collapseContentViewCard.setOnClickListener(this);

         }



         @Override
         public void onClick(View view) {
             switch(view.getId()){
                 case R.id.expand_content_view_cell:
                     foldingCell.toggle(false);
                     foldingCell.fold(true);
                     break;
                 case R.id.folding_cell:
                     card.getLocationOnScreen(popupLocation);
                     taskCardPosition =  getAdapterPosition();
                     TasksFragment.PopUpWindow popUpWindow = tasksFragment. new PopUpWindow();
                     popUpWindow.showPopup(taskCardPosition, popupLocation);
                     break;
                 case R.id.task_content_collapse_content_view_cell:
                     foldingCell.toggle(true);
                     foldingCell.fold(false);


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




    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){

        TasksModel tm = tasksList.get(position);
        holder.foldingCell.fold(true);
        String cardPosition = Integer.toString(position +1);

        GradientDrawable contentTaskPreviousStatusBackground, contentTaskCurrentStatusBackground, contentTaskNextStatusBackground;
        contentTaskPreviousStatusBackground = (GradientDrawable) holder.taskContentPreviousStatus.getBackground();
        contentTaskCurrentStatusBackground = (GradientDrawable) holder.taskContentCurrentStatus.getBackground();
        contentTaskNextStatusBackground = (GradientDrawable) holder.taskContentNextStatus.getBackground();




        //TODO:check if this listener is needed
        holder.card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        //visible part of Folding Cell
        holder.taskNumber.setText(cardPosition);
        holder.taskName.setText(tm.getTaskName());
        holder.taskOwner.setText(tm.getTaskOwner());
        holder.taskDeadlineDate.setText(tm.getTaskDeadlineDate());
        holder.taskDifficulty.setText(tm.getTaskDifficulty());

        //checks for task status and sets the color respectfully
        if(tm.getTaskStatus().equals("Completed")){
            holder.taskStatusColor.setBackgroundColor(Color.argb(190, 223, 230, 225));
        }
        else if(tm.getTaskStatus().equals("Paused")){
            holder.taskStatusColor.setBackgroundColor(Color.argb(190, 85, 85, 85));
        }
        //checks how many days are left to deadline for a task and colors the cardView respectfully
        else if (tm.getTimeRemaining() >= 10) {
            //green
            contentTaskPreviousStatusBackground.setColor(Color.argb(190, 77, 218, 203));
            contentTaskCurrentStatusBackground.setColor(Color.argb(190, 119, 253, 61));
            contentTaskNextStatusBackground.setColor(Color.argb(190, 253, 247, 61));
            holder.taskStatusColor.setBackgroundColor(Color.argb(190, 119, 253, 61));
        }
        else if ((tm.getTimeRemaining() < 10) && (tm.getTimeRemaining() >= 5)) {
            //yellow
            contentTaskPreviousStatusBackground.setColor(Color.argb(190, 119, 253, 61));
            contentTaskCurrentStatusBackground.setColor(Color.argb(190, 253, 247, 61));
            contentTaskNextStatusBackground.setColor(Color.argb(190, 253, 144, 61));
            holder.taskStatusColor.setBackgroundColor(Color.argb(190, 253, 247, 61));
        }
        else if ((tm.getTimeRemaining() < 5) && (tm.getTimeRemaining() >= 3)) {
            //orange
            contentTaskPreviousStatusBackground.setColor(Color.argb(190, 253, 247, 61));
            contentTaskCurrentStatusBackground.setColor(Color.argb(190, 253, 144, 61));
            contentTaskNextStatusBackground.setColor(Color.argb(190, 253, 61, 61));
            holder.taskStatusColor.setBackgroundColor(Color.argb(190, 253, 144, 61));
        }
        else if (tm.getTimeRemaining() < 3 && tm.getTimeRemaining() >=0) {
            //red
            contentTaskPreviousStatusBackground.setColor(Color.argb(190, 253, 144, 61));
            contentTaskCurrentStatusBackground.setColor(Color.argb(190, 253, 61, 61));
            contentTaskNextStatusBackground.setColor(Color.argb(190, 77, 218, 203));
            holder.taskStatusColor.setBackgroundColor(Color.argb(190, 253, 61, 61));
        }
        else if(tm.getTimeRemaining() < 0){
            contentTaskPreviousStatusBackground.setColor(Color.argb(190, 253, 144, 61));
            contentTaskCurrentStatusBackground.setColor(Color.argb(190, 253, 61, 61));
            contentTaskNextStatusBackground.setColor(Color.argb(190, 77, 218, 203));
            holder.taskStatusColor.setBackgroundColor(Color.argb(190, 253, 61, 61));
        }
        //./checks how many days are left to deadline for a task and colors the cardView respectfully

        //folded part of Folding Cell
        holder.taskContentNumber.setText(cardPosition);
        holder.taskContentName.setText(tm.getTaskName());
        holder.taskContentOwner.setText(tm.getTaskOwner());
        holder.taskContentPeopleWorking.setText(tm.getTaskPeopleWorking());
        holder.taskContentPreviousStatus.setText("Previous status");
        holder.taskContentCurrentStatus.setText(tm.getTaskStatus());
        holder.taskContentNextStatus.setText("Next status");
        holder.taskContentDifficulty.setText(Character.toString(tm.getTaskDifficulty().charAt(0)));
        holder.taskContentPriority.setText(Character.toString(tm.getTaskPriority().charAt(0)));
        holder.taskContentDescription.setText(tm.getTaskDescription());
        //holder.taskContentOwnerPic.setImageDrawable();


    }



    @Override
    public int getItemCount() {
        return tasksList.size();
    }

}
