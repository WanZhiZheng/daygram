package student.jnu.com.daygram;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Created by ASUS on 2018/11/16.
 */

public class DayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String []WeekdayArray={"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
    private String []WeekdayArray2={"SUN","MON","TUE","WED","THU","FRI","SAT"};
    private List<Day> mDayList;
    MainActivity mainActivity;
    private int showType=SHOWTYPE_ORDINARY; //初始化进去的是普通界面

    //标记日记显示类型
    public static final int SHOWTYPE_ORDINARY=1;
    public static final int SHOWTYPE_BROWSE=2;
    //标记该项有无日记
    public static final int ITEMTYPE_NOEMPTY=3;
    public static final int ITEMTYPE_EMPTY=4;





  static class NoemptyHolder extends RecyclerView.ViewHolder{
        View dayview;
        TextView day_short;
        TextView date_of_month;
        TextView content;

      public NoemptyHolder(View view){
          super(view);
          dayview=view;
          day_short=(TextView)view.findViewById(R.id.day_short);
          date_of_month=(TextView)view.findViewById(R.id.date_of_month);
          content=(TextView)view.findViewById(R.id.day_content);
      }

  }

  static class EmptyHolder extends RecyclerView.ViewHolder{
      ImageView point;
      public EmptyHolder(View view){
          super(view);
          point=(ImageView)view.findViewById(R.id.empty_day);
      }
  }
    static class NullHolder extends RecyclerView.ViewHolder{
        public NullHolder(View view){
            super(view);
        }
    }


    public  DayAdapter(MainActivity mainActivity,List<Day> dayList,int showType){
        this.mainActivity=mainActivity;
        this.mDayList=dayList;
        this.showType=showType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if(showType==SHOWTYPE_ORDINARY) {
            // 主界面模式
          if (viewType == ITEMTYPE_NOEMPTY) { // 主界面有日记项
              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
              final NoemptyHolder holder = new NoemptyHolder(view);

                //单击 跳转至编辑模式
              holder.dayview.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int position = holder.getAdapterPosition();
                      Day day = mDayList.get(position);
                      Intent intent = new Intent(v.getContext(), DayEditingActivity.class);
                      intent.putExtra("day_item", day);
                      mainActivity.startActivityForResult(intent, mainActivity.REQUEST_CODE_B);
                  }
              });
                //长按  删除该项日记
              holder.dayview.setOnLongClickListener(new View.OnLongClickListener() {
                  @Override
                  public boolean onLongClick(View v) {
                      int position=holder.getAdapterPosition();
                      final Day day=mDayList.get(position);

                      AlertDialog.Builder dialog=new AlertDialog.Builder(mainActivity);
                      dialog.setTitle("提示");
                      dialog.setMessage("是否要删除该项日记?");
                      dialog.setCancelable(false);
                      dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                          @RequiresApi(api = Build.VERSION_CODES.N)
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              day.delete(mainActivity);
                              mainActivity.loadDayItem_refresh(mainActivity.adapter.getShowType());
                              //点击则删除该日记  并刷新列表
                          }
                      });
                      dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              //点击 Cancel 则不做操作
                          }
                      });
                      dialog.show();

                      return true;
                  }
              });

              return holder;
              //
          } else if(viewType==ITEMTYPE_EMPTY) { //主界面无日记项  即黑（红）点
              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_day, parent, false);
              final EmptyHolder holder = new EmptyHolder(view);

              holder.point.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int position = holder.getAdapterPosition();
                      Day day = new Day(mainActivity.getDown_year(), mainActivity.getDown_month(), position + 1, "");
                      Intent intent = new Intent(v.getContext(), DayEditingActivity.class);
                      intent.putExtra("day_item", day);
                      mainActivity.startActivityForResult(intent, mainActivity.REQUEST_CODE_B);
                  }
              });
              return holder;

          }else{
              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
              final EmptyHolder holder = new EmptyHolder(view);
              return holder;
          }
      }else {
          //showType== SHOWTYPE_BROWSE 月度浏览模式
          if (viewType == ITEMTYPE_NOEMPTY) {  //月度浏览模式 有日记项
              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item_browse, parent, false);
              final NoemptyHolder holder = new NoemptyHolder(view);
              //单击日记项跳转至日记编辑模式
              holder.dayview.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      int position = holder.getAdapterPosition();
                      Day day = mDayList.get(position);
                      Intent intent = new Intent(v.getContext(), DayEditingActivity.class);
                      intent.putExtra("day_item", day);
                      mainActivity.startActivityForResult(intent, mainActivity.REQUEST_CODE_B);
                  }
              });
                //长按 删除该项日记
              holder.dayview.setOnLongClickListener(new View.OnLongClickListener() {
                  @Override
                  public boolean onLongClick(View v) {
                      int position=holder.getAdapterPosition();
                      final Day day=mDayList.get(position);

                      AlertDialog.Builder dialog=new AlertDialog.Builder(mainActivity);
                      dialog.setTitle("提示");
                      dialog.setMessage("是否要删除该项日记?");
                      dialog.setCancelable(false);
                      dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                          @RequiresApi(api = Build.VERSION_CODES.N)
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              day.delete(mainActivity);
                              mainActivity.loadDayItem_refresh(mainActivity.adapter.getShowType());
                              //点击则删除该日记  并刷新列表
                          }
                      });
                      dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              //点击 Cancel 则不做操作
                          }
                      });
                      dialog.show();

                      return true;
                  }
              });


              return holder;
          }else{//月度显示模式 无日记项 回一个空的view即可
              View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_item, parent, false);
              final NullHolder holder = new NullHolder(view);
              return holder;
          }
      }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
        if(holder instanceof NoemptyHolder) {
            NoemptyHolder noemptyHolder=(NoemptyHolder) holder;
            Day day = mDayList.get(position);
            Calendar calendar=Calendar.getInstance();
            calendar.set(day.getYear(),day.getMonth()-1,day.getDay());//这里千万别忘了月份要减一!!!!!才符合calendar的对月份的定义
            int weekday=calendar.get(Calendar.DAY_OF_WEEK);
            if(showType==SHOWTYPE_ORDINARY){
                noemptyHolder.day_short.setText(WeekdayArray2[weekday-1]);
            }else{
                noemptyHolder.day_short.setText(WeekdayArray[weekday-1]+" / ");
            }

            if(weekday==1)noemptyHolder.day_short.setTextColor(Color.RED);
            else noemptyHolder.day_short.setTextColor(Color.BLACK);  //这里不太懂为什么不设置else滑过字体颜色就会变红，很怪。

            noemptyHolder.date_of_month.setText(Integer.toString(day.getDay()));  //几号

           noemptyHolder.content.setText(day.getContent());  //内容
        }else if(holder instanceof EmptyHolder){
            EmptyHolder emptyHolder=(EmptyHolder)holder;
            Calendar calendar=Calendar.getInstance();
            calendar.set(mainActivity.getDown_year(),mainActivity.getDown_month()-1,position+1);
            int weekday=calendar.get(Calendar.DAY_OF_WEEK);
            if(weekday==1)
                emptyHolder.point.setImageResource(R.drawable.empty_day_red);
            else
                emptyHolder.point.setImageResource(R.drawable.empty_day_black);

        }
  }

  @Override
  public int getItemCount(){
      return mDayList.size();

  }


    @Override
    public int getItemViewType(int position) {
        Day day = mDayList.get(position);
        if(day.Empty()){
            return ITEMTYPE_EMPTY;
        } else {
            return ITEMTYPE_NOEMPTY;
        }

    }

    public int getShowType(){return showType;}

    public void setShowType(int showType){this.showType=showType;}

}