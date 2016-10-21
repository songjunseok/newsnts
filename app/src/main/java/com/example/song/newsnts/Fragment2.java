package com.example.song.newsnts;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import static com.example.song.newsnts.R.id.listView1;

/**
 * Created by EOM on 2015-08-20.
 */
public class Fragment2 extends ListFragment {

    //ListView listView1;
    FriendChatListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        //LinearLayout rootView = (LinearLayout) inflater.inflate( R.layout.fragment1, null );
        //LinearLayout rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        View view = inflater.inflate(R.layout.fragment2, null);
        //ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU);

        ListView listview = (ListView) view.findViewById(listView1) ;


        //listview.setAdapter(adapter) ;


        // 리스트뷰 객체 참조

        // 어댑터 객체 생성
        adapter = new FriendChatListAdapter(getActivity());

        // 아이템 데이터 만들기
        Resources res = getResources();

        adapter.addItem(new FriendChatListItem(res.getDrawable(R.drawable.user), "송준석 외 2명", "오늘따라 피곤하네..", "9"));
        adapter.addItem(new FriendChatListItem(res.getDrawable(R.drawable.user), "황지순 외 3명", "조장님 php안되여..", "15"));
        adapter.addItem(new FriendChatListItem(res.getDrawable(R.drawable.user), "김서영님과의 대화", "크리스마스엔 오키나와!", "9"));
        adapter.addItem(new FriendChatListItem(res.getDrawable(R.drawable.user), "오세헌님과의 대화", "가정의 평화를...", "4"));
        adapter.addItem(new FriendChatListItem(res.getDrawable(R.drawable.user), "홍석천님과의 대화", " ", "1"));
        adapter.addItem(new FriendChatListItem(res.getDrawable(R.drawable.user), "아이유님과의 대화", " ", "4"));
        adapter.addItem(new FriendChatListItem(res.getDrawable(R.drawable.user), "강타님과의 대화", " ", "5 "));


        // 리스트뷰에 어댑터 설정
        listview.setAdapter(adapter) ;

        ((ViewGroup)listview.getParent()).removeView(listview);

        return listview;


    }
}
