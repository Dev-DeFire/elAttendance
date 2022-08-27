package com.eldaas.attendance;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<PersonModelClass> userList;
    UserRecyclerViewOnClickListner listner;

    public UserAdapter(Context context, List<PersonModelClass> userList, UserRecyclerViewOnClickListner listner) {
        this.context = context;
        this.userList = userList;
        this.listner=listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final  PersonModelClass user = userList.get(position);

        holder.empUid.setText(user.getId());
        holder.empName.setText(user.getName());
        holder.empOut.setVisibility(View.GONE);
        holder.empStatus.setText("Active");
        holder.empStartDate.setText(user.getStartDate());
        holder.itemView.setOnClickListener(view ->{
            listner.onIttemClick(user.getName(),user.getEmail(),user.getId(),user.getOrganization(),user.getEmpUid(),user.getPhone(),user.getEmbeddings(),user.getAdmin(),user.getFather(),user.getBloodGrp(),user.getStartDate());
        });
        holder.itemView.setOnLongClickListener(view -> {
            listner.onLongItemClick(user.getEmpUid(),user.getName());
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView empUid,empName,empStartDate,empStatus,empOut;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            empUid   = itemView.findViewById(R.id.collegeIdDisplay);
            empName = itemView.findViewById(R.id.nameDisplay);
            empStartDate=itemView.findViewById(R.id.timeDisplay);
            empStatus=itemView.findViewById(R.id.statusDisplay);
            empOut=itemView.findViewById(R.id.outtimeDisplay);


        }
    }
    public interface UserRecyclerViewOnClickListner{
        void onLongItemClick(String emplUid,String name);
        void onIttemClick(String name, String email, String id, String organization, String uid, String phone, String embeddings, String admin, String father, String bloodGrp, String startDate);
    }

}
