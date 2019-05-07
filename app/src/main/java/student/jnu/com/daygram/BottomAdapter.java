package student.jnu.com.daygram;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ASUS on 2018/11/26.
 */

public class BottomAdapter extends RecyclerView.Adapter<BottomAdapter.ViewHolder> {
    private List<Bottom>bottomList;
    private MainActivity mainActivity;
    private String []MonthArray={"JANUARY  ","FEBRUARY  ","MARCH  ","APRIL  ","MAY  ","JUNE  ","JULY  ","AUGUST  ","SEPTEMBER  ","OCTOBER  ","NOVEMBER  ","DECEMBER  "};

    static class ViewHolder extends RecyclerView.ViewHolder{
        View bottomView;
        TextView year_or_month;

        public ViewHolder(View view){
            super(view);
            bottomView=view;
            year_or_month=(TextView)view.findViewById(R.id.year_or_month);
        }
    }


    public BottomAdapter(MainActivity mainActivity,List<Bottom> bottomList){
        this.mainActivity=mainActivity;
        this.bottomList=bottomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_item,parent,false);
       final ViewHolder holder=new ViewHolder(view);
        holder.bottomView.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Bottom bottom=bottomList.get(position);
                if(bottom.getYear_or_month().length()==3){  //证明显示的是月份的选择
                    if(mainActivity.getDown_year()==mainActivity.getPresent_year()&&position+1>mainActivity.getPresent_month()){

                    }else {
                        mainActivity.setDown_month(position+1);
                        mainActivity.loadDayItem_refresh(mainActivity.adapter.getShowType());
                        mainActivity.txt1.setText(MonthArray[position]);
                        mainActivity.recyclerView_bottom_month.setVisibility(View.INVISIBLE);
                    }
                }else{  //这里是年份的选择
                    mainActivity.setDown_year(Integer.parseInt(bottom.getYear_or_month()));
                    mainActivity.loadDayItem_refresh(mainActivity.adapter.getShowType());
                    mainActivity.txt2.setText(bottom.getYear_or_month());
                    mainActivity.recyclerView_bottom_year.setVisibility(View.INVISIBLE);
                }
            }
        });

        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Bottom bottom=bottomList.get(position);
        holder.year_or_month.setText(bottom.getYear_or_month());
    }



    @Override
    public int getItemCount() {
        return bottomList.size();
    }




}
