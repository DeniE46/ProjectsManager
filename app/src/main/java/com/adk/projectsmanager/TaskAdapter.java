package com.adk.projectsmanager;


import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import java.util.List;

 class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>   {

    public List<TasksModel> tasksList;
    private final static int FADE_DURATION = 1000; // in milliseconds

     public TaskAdapter() {

     }


     class MyViewHolder extends RecyclerView.ViewHolder{

         TextView taskName, taskOwner, taskDaysLeft, taskDescription, taskStatus;
        CardView card;


         MyViewHolder(View view){
            super (view);
            //gets control over the cardView and widgets
            taskName = (TextView)view.findViewById(R.id.task_name);
            taskOwner = (TextView)view.findViewById(R.id.task_owner);
            taskDaysLeft = (TextView)view.findViewById(R.id.days_left);
            taskDescription = (TextView)view.findViewById(R.id.task_description);
            taskStatus = (TextView)view.findViewById(R.id.task_status);
            card = (CardView)view.findViewById(R.id.cv);
        }


     }


    //Constructor
     TaskAdapter(List<TasksModel> tasksList){
        this.tasksList = tasksList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_row, parent, false);
        return new MyViewHolder(itemView);
    }




    //loads information in cards
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
       //statements of type holder.*.setText(tm.get*) set the text for the textView in cardView
        TasksModel tm = tasksList.get(position);
        holder.taskName.setText(tm.getTaskName());
        holder.taskOwner.setText(tm.getTaskOwner());
        holder.taskDescription.setText(tm.getTaskDescription());

        String daysToGo = String.valueOf(tm.getTimeRemaining());

        //checks how many days are left to deadline for a task and colors the cardView respectfully
        if(tm.getTaskIsCompleted().equals("true")){
            holder.taskStatus.setText(R.string.task_status_ready);
            holder.taskDaysLeft.setText(R.string.task_days_left);
            //silver
            holder.card.setCardBackgroundColor(Color.argb(190, 223, 230, 225));
        }
        else if(tm.getTaskIsCompleted().toString().equals("false")) {

            holder.taskStatus.setText(R.string.task_status_wip);
            if (tm.getTimeRemaining() >= 10) {
                holder.taskDaysLeft.setText(daysToGo);
                //green
                holder.card.setCardBackgroundColor(Color.argb(190, 119, 253, 61));
            }
            else if ((tm.getTimeRemaining() < 10) && (tm.getTimeRemaining() >= 5)) {
                holder.taskDaysLeft.setText(daysToGo);
                //yellow
                holder.card.setCardBackgroundColor(Color.argb(190, 253, 247, 61));
            }
            else if ((tm.getTimeRemaining() < 5) && (tm.getTimeRemaining() >= 3)) {
                holder.taskDaysLeft.setText(daysToGo);
                //orange
                holder.card.setCardBackgroundColor(Color.argb(190, 253, 144, 61));
            }
            else if (tm.getTimeRemaining() < 3 && tm.getTimeRemaining() >=0) {
                holder.taskDaysLeft.setText(daysToGo);
                //red
                holder.card.setCardBackgroundColor(Color.argb(190, 253, 61, 61));
            }
            else if(tm.getTimeRemaining() < 0){
                holder.taskStatus.setText(R.string.task_status_expired);
                holder.taskDaysLeft.setText(R.string.task_days_left);
                //red
                holder.card.setCardBackgroundColor(Color.argb(190, 253, 61, 61));
            }
            //./checks how many days are left to deadline for a task and colors the cardView respectfully
        }
        //setFadeAnimation(holder.itemView);

    }



    //animation when tasks are loading
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }





    @Override
    public int getItemCount() {
        return tasksList.size();
    }

}
