package com.adk.projectsmanager;



import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;


//import java lists
import java.util.List;

 class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.MyViewHolder>  {

    public List<MembersModel> membersList;
    private final static int FADE_DURATION = 1000; // in milliseconds


     class MyViewHolder extends RecyclerView.ViewHolder{

          TextView name, occupation;
          ImageView PicId;


          MyViewHolder(View view){
             super (view);

             name = (TextView)view.findViewById(R.id.person_name);
             occupation = (TextView)view.findViewById(R.id.person_occupation);
             PicId = (ImageView)view.findViewById(R.id.person_photo);

         }



    }


    //Constructor
     TeamMembersAdapter(List<MembersModel> membersList){
        this.membersList = membersList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
     View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_row, parent, false);

        return new MyViewHolder(itemView);



    }




    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){

        MembersModel membersModel = membersList.get(position);
        holder.name.setText(membersModel.getName());
        holder.occupation.setText(membersModel.getOccupation());
        holder.PicId.setImageResource(membersModel.getPicId());
       // setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

}
