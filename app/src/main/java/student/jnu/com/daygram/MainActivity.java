package student.jnu.com.daygram;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Day> dayList=new ArrayList<Day>();
    private List<Bottom> bottomMonthList=new ArrayList<Bottom>();
    private List<Bottom> bottomYearList=new ArrayList<Bottom>();

    RecyclerView recyclerView_show;
    RecyclerView recyclerView_bottom_year;
    RecyclerView recyclerView_bottom_month;

    public DayAdapter adapter;
    public BottomAdapter adapter_bottom_month;
    public BottomAdapter adapter_bottom_year;
    TextView txt1; //txt1 for month
    TextView txt2; //txt2 for year

    private ImageButton imageButton;
    private Calendar instance;

    public static final int REQUEST_CODE_B = 77;

    //记录现在的年月以及下面选中的年月
    int present_month;
    int present_year;
    int down_year;
    int down_month;
    int day_count;

    private String []MonthArray={"JANUARY  ","FEBRUARY  ","MARCH  ","APRIL  ","MAY  ","JUNE  ","JULY  ","AUGUST  ","SEPTEMBER  ","OCTOBER  ","NOVEMBER  ","DECEMBER  "};
    private String []WeekdayArray={"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
    private String []WeekdayArray2={"SUN","MON","TUE","WED","THU","FRI","SAT"};
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_bottom_month();
        init_bottom_year();

        Calendar cal_init=Calendar.getInstance();


        readMonthIntoDaylist(cal_init.get(Calendar.YEAR),cal_init.get(Calendar.MONTH)+1);


        //设置主list
        //ListView listView=(ListView)findViewById(R.id.list_view);
        recyclerView_show=(RecyclerView)findViewById(R.id.recycler_view);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView_show.setLayoutManager(linearLayoutManager);
       // adapter=new DayAdapter( this,dayList);
        adapter=new DayAdapter(MainActivity.this,dayList,DayAdapter.SHOWTYPE_ORDINARY);
        //listView.setAdapter(adapter);
        recyclerView_show.setAdapter(adapter);

        //设置底部的那个月的选择
        recyclerView_bottom_month=(RecyclerView)findViewById(R.id.recycler_view_bottom_month);
        LinearLayoutManager linearLayoutManager_bottom_month=new LinearLayoutManager(this);
        linearLayoutManager_bottom_month.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_bottom_month.setLayoutManager(linearLayoutManager_bottom_month);
        adapter_bottom_month=new BottomAdapter(this,bottomMonthList);
        recyclerView_bottom_month.setAdapter(adapter_bottom_month);
        //设置底部的那个年的选择
        recyclerView_bottom_year=(RecyclerView)findViewById(R.id.recycler_view_bottom_year);
        LinearLayoutManager linearLayoutManager_bottom_year=new LinearLayoutManager(this);
        linearLayoutManager_bottom_year.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_bottom_year.setLayoutManager(linearLayoutManager_bottom_year);
        adapter_bottom_year=new BottomAdapter(this,bottomYearList);
        recyclerView_bottom_year.setAdapter(adapter_bottom_year);


        //点击底部月份选择
        txt1=(TextView)findViewById(R.id.txt1);
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_bottom_month.setVisibility(View.VISIBLE);

            }
        });
        //点击底部年份选择
        txt2=(TextView)findViewById(R.id.txt2);
        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_bottom_year.setVisibility(View.VISIBLE);

            }
        });


        //初始化设置底部现在的年月
        instance=Calendar.getInstance();
        present_month=instance.get(Calendar.MONTH)+1;
        present_year=instance.get(Calendar.YEAR);
        String MonthStr=MonthArray[present_month-1];
        String YearStr=String.valueOf(present_year);
        txt1.setText(MonthStr);
        txt2.setText(YearStr);

        //设置添加日记 + 的功能
        imageButton=(ImageButton)findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH)+1;
                int day=calendar.get(Calendar.DAY_OF_MONTH);


                Day present_Dayitem=new Day();
                if(present_Dayitem.read(MainActivity.this,year,month,day)==false){
                    present_Dayitem=new Day(year,month,day,"");//这个if语句主要是判断是否已经存在今天的日记了，防止重复添加今天的日记
                }
                Intent intent=new Intent(MainActivity.this,DayEditingActivity.class);
                intent.putExtra("day_item",present_Dayitem);
                startActivityForResult(intent,REQUEST_CODE_B);
            }
        });

        //月度日记浏览模式按钮设置
        ImageButton btn_browse=(ImageButton)findViewById(R.id.btn_browse);
        btn_browse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(adapter.getShowType()==DayAdapter.SHOWTYPE_ORDINARY) {
                    adapter.setShowType(DayAdapter.SHOWTYPE_BROWSE);
                    loadDayItem_refresh(DayAdapter.SHOWTYPE_BROWSE);
                }else{
                    adapter.setShowType(DayAdapter.SHOWTYPE_ORDINARY);
                    loadDayItem_refresh(DayAdapter.SHOWTYPE_ORDINARY);
                }
            }
        });


    }

//    private void initDayNote(){
//
//        readAMonth(2018,11);
//  等等直接在前面一个语句就好了
//    }

    //刷新载入本月的view
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadDayItem_refresh(int showType){
        readMonthIntoDaylist(down_year,down_month);
        adapter=new DayAdapter(this,dayList,showType);
        recyclerView_show.setAdapter(adapter);
        adapter.notifyDataSetChanged();  //每次调用该方法都导致数组的变化，所以要notify要更新一下。
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void readMonthIntoDaylist(int year, int month){
        Calendar calendar=Calendar.getInstance();
        int present_year=calendar.get(Calendar.YEAR);
        int present_month=calendar.get(Calendar.MONTH)+1;
        int present_day=calendar.get(Calendar.DAY_OF_MONTH);
        //在这里设置一下calendar的年月，就可以得出选中的年月所拥有的天数day_count
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);//这里也别忘了－1

        this.down_year=year;
        this.down_month=month;

        if(year==present_year && month==present_month){
            day_count=present_day;
        }else{
            day_count=calendar.getActualMaximum(calendar.DAY_OF_MONTH);
        }

        dayList.clear();
        for(int i=1;i<=day_count;i++){
            Day day=new Day();
            if(day.read(MainActivity.this,year,month,i)==true){
                dayList.add(day);//如果已有内容即读取成功则放入数组
            }else{
                dayList.add(day);//如果没有内容即读取失败也放入数组
            }

        }

    }











@RequiresApi(api = Build.VERSION_CODES.N)
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode){
        case REQUEST_CODE_B:
            loadDayItem_refresh(adapter.getShowType());
            break;
        default:
    }

}





//

    private void init_bottom_year(){

        bottomYearList.add(new Bottom("2009"));
        bottomYearList.add(new Bottom("2010"));
        bottomYearList.add(new Bottom("2011"));
        bottomYearList.add(new Bottom("2012"));
        bottomYearList.add(new Bottom("2013"));
        bottomYearList.add(new Bottom("2014"));
        bottomYearList.add(new Bottom("2015"));
        bottomYearList.add(new Bottom("2016"));
        bottomYearList.add(new Bottom("2017"));
        bottomYearList.add(new Bottom("2018"));

    }


    private void init_bottom_month(){

        bottomMonthList.add(new Bottom("JAN"));
        bottomMonthList.add(new Bottom("FEB"));
        bottomMonthList.add(new Bottom("MAR"));
        bottomMonthList.add(new Bottom("APR"));
        bottomMonthList.add(new Bottom("MAY"));
        bottomMonthList.add(new Bottom("JUN"));
        bottomMonthList.add(new Bottom("JUL"));
        bottomMonthList.add(new Bottom("AUG"));
        bottomMonthList.add(new Bottom("SEP"));
        bottomMonthList.add(new Bottom("OCT"));
        bottomMonthList.add(new Bottom("NOV"));
        bottomMonthList.add(new Bottom("DEC"));

    }

    public int getDown_year(){
        return down_year;
    }

    public void setDown_year(int year){
        this.down_year=year;
    }

    public int getDown_month(){
        return down_month;
    }
    public void setDown_month(int month){
        this.down_month=month;
    }
    public int getPresent_year(){
        return present_year;
    }
    public int getPresent_month(){
        return present_month;
    }

//    public void setPresent_year(int year){
//        this.present_year=year;
//    }
//    public void setPresent_month(int month){
//        this.present_month=month;
//    }


}
