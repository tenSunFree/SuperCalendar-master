package com.hqyxjy.ldf.supercalendar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;

import java.util.ArrayList;

/**
 * Created by ldf on 16/11/4.
 */

public class SyllabusActivity extends AppCompatActivity {

    TextView textViewYearDisplay;
    TextView textViewMonthDisplay;
    TextView backToday;
    MonthPager monthPager;

    private ArrayList<Calendar> currentCalendars = new ArrayList<>();
    private CalendarViewAdapter calendarAdapter;
    private OnSelectDateListener onSelectDateListener;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;
    private Context context;
    private CalendarDate currentDate;
    private boolean initiated = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        context = this;
        monthPager = (MonthPager) findViewById(R.id.calendar_view);
        textViewYearDisplay = (TextView) findViewById(R.id.show_year_view);
        textViewMonthDisplay = (TextView) findViewById(R.id.show_month_view);
        backToday = (TextView) findViewById(R.id.back_today_button);

        initCurrentDate();                                                                                              // 將頁面 刷新為今天
        initCalendarView();                                                                                             // 產生calendarAdapter, 並放入monthPager, 再設置monthPager 相關設定
        initToolbarClickListener();                                                                                     // 對backToday 點擊監聽
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !initiated) {
            refreshMonthPager();
            initiated = true;
        }
    }

    private void initToolbarClickListener() {
        backToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBackToDayBtn();                                                                                  // 刷新畫面為 今天日期
            }
        });
    }

    private void initCurrentDate() {
        currentDate = new CalendarDate();                                                                               // 從本機取得當前日期, 201x-xx-xx, 再賦予currentDate
        textViewYearDisplay.setText(currentDate.getYear() + "年");
        textViewMonthDisplay.setText(currentDate.getMonth() + "");
    }

    private void initCalendarView() {                                                                                   // 產生calendarAdapter, 並放入monthPager, 再設置monthPager 相關設定
        initListener();                                                                                                 // 點選日期的監聽, 以及月的切換監聽
        CustomDayView customDayView = new CustomDayView(context, R.layout.custom_day);
        calendarAdapter = new CalendarViewAdapter(
                context,
                onSelectDateListener,
                CalendarAttr.CalendayType.MONTH,
                customDayView);
        calendarAdapter.switchToMonth();
        initMonthPager();                                                                                               // 初始化關於日曆
    }

    private void initListener() {                                                                                       // 點選日期的監聽, 以及月的切換監聽
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {                                                               // 當選擇某天的時候
                refreshClickDate(date);                                                                                 // 將取得的data, 賦予currentDate, 重新刷新年月日 相關訊息
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                monthPager.selectOtherMonth(offset);                                                                    //偏移量 -1表示上一个月 ， 1表示下一个月
            }
        };
    }

    private void refreshClickDate(CalendarDate date) {                                                                  // 刷新為 今天的年月數字
        currentDate = date;
        textViewYearDisplay.setText(date.getYear() + "年");
        textViewMonthDisplay.setText(date.getMonth() + "");
    }

    private void initMonthPager() {                                                                                     // 初始化關於日曆
        monthPager.setAdapter(calendarAdapter);
        monthPager.setPageTransformer(true, new RotationPageTransformer());                                             // 實現滑動動畫效果
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);                                                    // 設置當前Item位置, 預設值為1000, 為了避免負值的情況發生


        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {                                      // 針對monthPager 翻頁時的監聽
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {                                                                  // 當翻到其他頁面時, 想做些什麼事?
                mCurrentPage = position;
                currentCalendars = calendarAdapter.getPagers();

                if (currentCalendars.get(position % currentCalendars.size()) instanceof Calendar) {
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    currentDate = date;
                    textViewYearDisplay.setText(date.getYear() + "年");
                    textViewMonthDisplay.setText(date.getMonth() + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onClickBackToDayBtn() {
        refreshMonthPager();
    }

    private void refreshMonthPager() {
        CalendarDate today = new CalendarDate();                                                                        // 獲取本機的年月日, 並賦予today
        calendarAdapter.notifyDataChanged(today);
        refreshClickDate(today);                                                                                        // 刷新為 今天的年月數字
    }
}

