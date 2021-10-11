package com.example.pharmago.ui.notification;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pharmago.R;
import com.example.pharmago.controller.NotificationController;
import com.example.pharmago.model.NotificationM;
import com.example.pharmago.util.SharedPref;
import com.example.pharmago.util.TextDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationFragment extends Fragment {

    private SharedPref sharedPref;
    private ListView listView;
    private ArrayList<NotificationM> notificationMS;
    private NotificationAdapter adapter;

    public NotificationFragment() {
    }

    public static NotificationFragment getInstance() {

        return new NotificationFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        sharedPref = new SharedPref(getActivity());
      //  sharedPref.setFragData(0);
        listView = (ListView) rootView.findViewById(R.id.list_notification);


        notificationMS = NotificationController.getAllNotifications(getActivity());

        adapter = new NotificationAdapter(notificationMS, getActivity());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationM notificationM = notificationMS.get(position);
                //     Toast.makeText(getActivity(),notificationM.getNotificationContent(),Toast.LENGTH_SHORT).show();

                Fragment fragment = NotificationViewerFragment.getInstance();

                Bundle args = new Bundle();
                args.putInt("id", notificationM.getNotificationId());
                args.putString("title", notificationM.getNotificationTitle());
                args.putString("content", notificationM.getNotificationContent());
                args.putInt("type", notificationM.getTypeId());
                args.putString("additional", notificationM.getAdditionalData());
                fragment.setArguments(args);

                changeFragment(fragment, "");
            }
        });


        return rootView;
    }


    public class NotificationAdapter extends BaseAdapter {

        private ArrayList<NotificationM> mArrayList;
        private Context context;
        private LayoutInflater inflater = null;

        public NotificationAdapter(ArrayList<NotificationM> mArrayList, Context context) {
            this.mArrayList = mArrayList;
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public NotificationM getItem(int position) {
            return mArrayList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();

                convertView = inflater.inflate(R.layout.notification_item, null);
                holder.image_text = (ImageView) convertView.findViewById(R.id.image_text);
                holder.text_notification_title = (TextView) convertView.findViewById(R.id.text_notification_title);
                holder.text_notification_date = (TextView) convertView.findViewById(R.id.text_notification_date);

            } else {
                holder = (Holder) convertView.getTag();


            }


            NotificationM notificationM = mArrayList.get(position);

            if (notificationM.getIsSeen()) {
                convertView.setBackgroundColor(Color.parseColor("#e6f2ff"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            }


            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(notificationM.getNotificationTitle().substring(0, 1), Color.parseColor("#26c6da"));

            try {

                holder.image_text.setImageDrawable(drawable);

                holder.text_notification_title.setText(notificationM.getNotificationTitle());

                Date date = new Date(notificationM.getTimeStamp());

                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                holder.text_notification_date.setText(simpleDateFormat.format(date));
            }catch (Exception e)
            {
                Log.e("ERROR",e.toString());
            }


            return convertView;
        }

        @Override
        public int getCount() {
            return mArrayList.size();
        }
    }

    public class Holder {

        TextView text_notification_title, text_notification_date;
        ImageView image_text;


    }

    private void changeFragment(Fragment fragment, String title) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}
