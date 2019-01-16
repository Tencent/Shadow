package com.tencent.shadow.demo.main;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ryg.expandable.ui.PinnedHeaderExpandableListView;
import com.ryg.expandable.ui.PinnedHeaderExpandableListView.OnHeaderUpdateListener;
import com.ryg.expandable.ui.StickyLayout;
import com.ryg.expandable.ui.StickyLayout.OnGiveUpTouchEventListener;
import com.tencent.shadow.demo.main.cases.CaseSummaryFragment;
import com.tencent.shadow.demo.main.cases.TestCaseManager;
import com.tencent.shadow.demo.main.cases.entity.TestCase;
import com.tencent.shadow.demo.main.cases.entity.TestCategory;

import java.util.List;

public class MainActivity extends Activity implements
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener,
        OnHeaderUpdateListener, OnGiveUpTouchEventListener {

    private PinnedHeaderExpandableListView expandableListView;
    private StickyLayout stickyLayout;
    private List<TestCategory> categoryList;
    private SparseBooleanArray expandStatus;
    private SlidingMenu slidingMenu;

    private ExpandableListAdapter adapter;
    private CaseSummaryFragment caseSummaryFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        expandableListView = findViewById(R.id.expandablelist);
        stickyLayout = findViewById(R.id.sticky_layout);
        slidingMenu = findViewById(R.id.slidingmenu);

        caseSummaryFragment = new CaseSummaryFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, caseSummaryFragment, "CaseSummaryFragment");
        fragmentTransaction.commitAllowingStateLoss();

        categoryList = TestCaseManager.testCases;
        expandStatus = new SparseBooleanArray();

        adapter = new ExpandableListAdapter(this);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnHeaderUpdateListener(this);
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupClickListener(this);

        stickyLayout.setOnGiveUpTouchEventListener(this);
        slidingMenu.showMenu();

    }


    class ExpandableListAdapter extends BaseExpandableListAdapter {
        private LayoutInflater inflater;

        public ExpandableListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        // 返回父列表个数
        @Override
        public int getGroupCount() {
            return categoryList.size();
        }

        // 返回子列表个数
        @Override
        public int getChildrenCount(int groupPosition) {
            return categoryList.get(groupPosition).caseList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {

            return categoryList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return categoryList.get(groupPosition).caseList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {

            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            CaseCategoryHolder groupHolder = null;
            if (convertView == null) {
                groupHolder = new CaseCategoryHolder();
                convertView = inflater.inflate(R.layout.layout_case_category_item, null);
                groupHolder.textCategory = (TextView) convertView
                        .findViewById(R.id.tv_category);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (CaseCategoryHolder) convertView.getTag();
            }
            expandStatus.put(groupPosition, isExpanded);
            String title = ((TestCategory) getGroup(groupPosition)).title;
            groupHolder.textCategory.setText(isExpanded ? title + " - " : title + " + ");
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            CaseItemHolder childHolder = null;
            if (convertView == null) {
                childHolder = new CaseItemHolder();
                convertView = inflater.inflate(R.layout.layout_case_item, null);

                childHolder.textName = (TextView) convertView
                        .findViewById(R.id.tv_case);
                convertView.setTag(childHolder);
            } else {
                childHolder = (CaseItemHolder) convertView.getTag();
            }

            childHolder.textName.setText(((TestCase) getChild(groupPosition,
                    childPosition)).name);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public boolean onGroupClick(final ExpandableListView parent, final View v,
                                int groupPosition, final long id) {
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        TestCase testCase = categoryList.get(groupPosition).caseList.get(childPosition);
        caseSummaryFragment.setCase(testCase);

        slidingMenu.showMenu();
        return false;
    }

    class CaseCategoryHolder {
        TextView textCategory;
    }

    class CaseItemHolder {
        TextView textName;
    }

    @Override
    public View getPinnedHeader() {
        View headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_case_category_item, null);
        headerView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        TestCategory firstVisibleGroup = (TestCategory) adapter.getGroup(firstVisibleGroupPos);
        TextView textView =  headerView.findViewById(R.id.tv_category);
        String title = firstVisibleGroup.title;
        textView.setText(expandStatus.get(firstVisibleGroupPos) ? title + " - " : title + " + ");
    }

    @Override
    public boolean giveUpTouchEvent(MotionEvent event) {
        if (expandableListView.getFirstVisiblePosition() == 0) {
            View view = expandableListView.getChildAt(0);
            if (view != null && view.getTop() >= 0) {
                return true;
            }
        }
        return false;
    }

}
