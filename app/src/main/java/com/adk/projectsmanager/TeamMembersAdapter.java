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


     class MyViewHolder extends RecyclerView.ViewHolder{

          TextView name, occupation, phoneNumber, facebookAccount;
          ImageView PicId;


          MyViewHolder(View view){
             super (view);

             name = (TextView)view.findViewById(R.id.person_name);
             occupation = (TextView)view.findViewById(R.id.person_occupation);
             PicId = (ImageView)view.findViewById(R.id.person_photo);
             phoneNumber = (TextView)view.findViewById(R.id.person_phone);
             facebookAccount = (TextView)view.findViewById(R.id.person_fb);
         }



    }



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
        holder.phoneNumber.setText(membersModel.getPhone());
        holder.facebookAccount.setText(membersModel.getFacebookAccount());
    }


    @Override
    public int getItemCount() {
        return membersList.size();
    }

}
