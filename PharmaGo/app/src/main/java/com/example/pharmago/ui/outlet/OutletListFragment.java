package com.example.pharmago.ui.outlet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pharmago.R;

import com.example.pharmago.util.NumberFormatUtil;
import com.example.pharmago.util.TextDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OutletListFragment extends Fragment {

    private ExpandableListView listOutlet;
    private EditText text_search_outlet;



    int tot_chemist = 0;
    int tot_productive = 0;
    int tot_un_productive = 0;
    int tot_collage = 0;

    private TextView txt_route, txt_college_count, txt_productivity_count;



    public static OutletListFragment getInstance() {
        return new OutletListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_outlet_list, container, false);
        listOutlet = rootView.findViewById(R.id.list_outlet);

        txt_route = (TextView) rootView.findViewById(R.id.txt_route);
        txt_college_count = (TextView) rootView.findViewById(R.id.txt_callage);
        txt_productivity_count = (TextView) rootView.findViewById(R.id.txt_productive);


        txt_route.setText("Sample Route");

        text_search_outlet = (EditText) rootView.findViewById(R.id.text_search_outlet);

        text_search_outlet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

              //  adapter.getFilter().filter(s);
            }
        });


        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        int selected_date = cal1.get(Calendar.DATE);




        tot_collage = tot_productive + tot_un_productive;





        txt_college_count.setText(tot_collage + " - " + tot_chemist);
        txt_productivity_count.setText(tot_productive + " - " + tot_chemist);


        listOutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
        return rootView;
    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.cus_add_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_cus) {




            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void changeFragment(Fragment fragment) {

        //   ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(schedule.getName());

        Bundle args = new Bundle();
        args.putInt("planned_visit", 0);



        fragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }


}
