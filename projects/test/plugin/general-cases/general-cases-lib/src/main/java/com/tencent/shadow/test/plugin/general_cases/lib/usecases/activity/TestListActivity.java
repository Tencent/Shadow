package com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;

import java.util.ArrayList;
import java.util.List;

public class TestListActivity extends Activity {
    List<String> mItemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<String> activities = getIntent().getStringArrayListExtra("activities");
        if (activities != null) {
            mItemList.addAll(activities);
        }

        ListView listView = findViewById(R.id.al_list);
        listView.setAdapter(new InnerAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String className = mItemList.get(position);
                try {
                    Intent intent = new Intent(TestListActivity.this, Class.forName(className));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class InnerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.layout_list_item, null);
                holder = new Holder();
                holder.textView = convertView.findViewById(R.id.lli_text);
                convertView.setTag(holder);
            } else {
                holder = (Holder)convertView.getTag();
            }
            String className = getItem(position).toString();
            if (className.indexOf(".") != -1) {
                className = className.substring(className.lastIndexOf(".") + 1);
            }
            holder.textView.setText(className);
            return convertView;
        }

        class Holder {
            TextView textView;
        }
    }
}
