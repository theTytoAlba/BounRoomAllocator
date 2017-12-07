package com.thetytoalba.bounroomallocator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import static com.thetytoalba.bounroomallocator.Constants.TAG_FRIDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_MONDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SATURDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUNDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_THURSDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_TUESDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_WEDNESDAY;

public class AddLectureActivity extends AppCompatActivity {

    JSONObject week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecture);
        initJSONWeek();
        initCalendarView();
    }

    private void initJSONWeek(){
        week = new JSONObject();
        try {
            week.put(TAG_MONDAY, getJSONDay());
            week.put(TAG_TUESDAY, getJSONDay());
            week.put(TAG_WEDNESDAY, getJSONDay());
            week.put(TAG_THURSDAY, getJSONDay());
            week.put(TAG_FRIDAY, getJSONDay());
            week.put(TAG_SATURDAY, getJSONDay());
            week.put(TAG_SUNDAY, getJSONDay());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJSONDay(){
        JSONObject dayObj = new JSONObject();
        try {
            dayObj.put("09", false);
            dayObj.put("10", false);
            dayObj.put("11", false);
            dayObj.put("12", false);
            dayObj.put("13", false);
            dayObj.put("14", false);
            dayObj.put("15", false);
            dayObj.put("16", false);
            dayObj.put("17", false);
            dayObj.put("18", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayObj;
    }

    private void initCalendarView() {
        try {
            initDay(TAG_MONDAY);
            initDay(TAG_TUESDAY);
            initDay(TAG_WEDNESDAY);
            initDay(TAG_THURSDAY);
            initDay(TAG_FRIDAY);
            initDay(TAG_SATURDAY);
            initDay(TAG_SUNDAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initDay(final String day) {
        LinearLayout holder = (LinearLayout) findViewById(R.id.addLecture_daysHolder);
        LinearLayout dayLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_day_continer, null);
        TextView dayName = dayLayout.findViewById(R.id.dayContainer_dayName);
        dayName.setText(day);
        final TextView clock09 = dayLayout.findViewById(R.id.clock09);
        final TextView clock10 = dayLayout.findViewById(R.id.clock10);
        final TextView clock11 = dayLayout.findViewById(R.id.clock11);
        final TextView clock12 = dayLayout.findViewById(R.id.clock12);
        final TextView clock13 = dayLayout.findViewById(R.id.clock13);
        final TextView clock14 = dayLayout.findViewById(R.id.clock14);
        final TextView clock15 = dayLayout.findViewById(R.id.clock15);
        final TextView clock16 = dayLayout.findViewById(R.id.clock16);
        final TextView clock17 = dayLayout.findViewById(R.id.clock17);
        final TextView clock18 = dayLayout.findViewById(R.id.clock18);

        clock09.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("09");
                    selected = !selected;
                    if (selected) {
                        clock09.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock09.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.getJSONObject(day).put("09", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("10");
                    selected = !selected;
                    if (selected) {
                        clock10.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock10.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.getJSONObject(day).put("10", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("11");
                    selected = !selected;
                    if (selected) {
                        clock11.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock11.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.getJSONObject(day).put("11", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("12");
                    selected = !selected;
                    if (selected) {
                        clock12.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock12.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.getJSONObject(day).put("12", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("13");
                    selected = !selected;
                    if (selected) {
                        clock13.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock13.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.getJSONObject(day).put("13", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("14");
                    selected = !selected;
                    if (selected) {
                        clock14.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock14.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.getJSONObject(day).put("14", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("15");
                    selected = !selected;
                    if (selected) {
                        clock15.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock15.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.getJSONObject(day).put("15", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("16");
                    selected = !selected;
                    if (selected) {
                        clock16.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock16.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.getJSONObject(day).put("16", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("17");
                    selected = !selected;
                    if (selected) {
                        clock17.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock17.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.getJSONObject(day).put("17", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("18");
                    selected = !selected;
                    if (selected) {
                        clock18.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock18.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.getJSONObject(day).put("18", selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        holder.addView(dayLayout);
    }


}
