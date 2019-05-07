package student.jnu.com.daygram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by ASUS on 2018/11/16.
 */

public class DayEditingActivity extends AppCompatActivity{
    TextView txt1,txt2,txt3,txt4;
    Day day;
    EditText editText;
    int tag;   //用来标记是否保存
    private Calendar instance;
    private String []MonthArray={"JANUARY  ","FEBRUARY  ","MARCH  ","APRIL  ","MAY  ","JUNE  ","JULY  ","AUGUST  ","SEPTEMBER  ","OCTOBER  ","NOVEMBER  ","DECEMBER  "};
    private String []WeekdayArray={"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayediting);

        Intent intent = getIntent();
        day=(Day)intent.getSerializableExtra("day_item");

        tag=0;

        //设置顶部日期
        Calendar calendar=Calendar.getInstance();
        calendar.set(day.getYear(),day.getMonth()-1,day.getDay());
        int weekday=calendar.get(Calendar.DAY_OF_WEEK);

        txt1=(TextView)findViewById(R.id.day);
        txt1.setText(WeekdayArray[weekday-1]);
        if(weekday==1) {txt1.setTextColor(Color.RED);}
        txt2=(TextView)findViewById(R.id.month);
        txt2.setText(MonthArray[day.getMonth()-1]);
        txt3=(TextView)findViewById(R.id.date);
        txt3.setText(Integer.toString(day.getDay()))    ;
        txt4=(TextView)findViewById(R.id.year);
        txt4.setText(Integer.toString(day.getYear()));




        final EditText editText=(EditText)findViewById(R.id.edit_text);
        final ImageButton btn_clock=(ImageButton)findViewById(R.id.btn_clock);
        final TextView txt_DONE=(TextView)findViewById(R.id.txt_DONE);
        btn_clock.setVisibility(View.INVISIBLE);
        txt_DONE.setVisibility(View.INVISIBLE);

        editText.setText(day.getContent());

        editText.setFocusable(false);
        editText.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                editText.requestFocusFromTouch();
                btn_clock.setVisibility(View.VISIBLE);
                txt_DONE.setVisibility(View.VISIBLE);



                return false;
            }
        });

        //DONE键保存
        txt_DONE.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String return_content = editText.getText().toString();
                day.setContent(return_content);
                day.write(getApplicationContext());
                Toast.makeText(DayEditingActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                tag=1;
                btn_clock.setVisibility(View.INVISIBLE);
                txt_DONE.setVisibility(View.INVISIBLE);
                editText.setFocusable(false);

            }
        });

        //时钟图标插入当前时间
        btn_clock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String time="";
                SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a");
                Date now=new Date();
                time+=sdf.format(now);
                int index=editText.getSelectionStart();
                Editable editable=editText.getText();
                editable.insert(index,time);
            }
        });

        //返回按钮,增加了判断是否保存
        ImageButton btn_ok = (ImageButton)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag==1) {
                    Intent intent_return = new Intent();
                    setResult(RESULT_OK, intent_return);
                    finish();
                }else{
                    AlertDialog.Builder dialog=new AlertDialog.Builder(DayEditingActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("日记未保存！请问要继续吗？");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent_return = new Intent();
                            setResult(RESULT_OK, intent_return);
                            finish();
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }

            }
        });
    }
}
