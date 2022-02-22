package com.lumos.smartdevice.activity.scenebooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.adapter.BookerBorrowReturnBookAdapter;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturnCloseAction;
import com.lumos.smartdevice.model.BookerBookBean;
import com.lumos.smartdevice.ui.BaseFragmentActivity;
import com.lumos.smartdevice.ui.my.MyListView;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.ArrayList;
import java.util.List;

public class BookerBorrowReturnOverviewActivity extends BaseFragmentActivity {

    private static final String TAG = "BookerBorrowReturnOverviewActivity";

    private View btn_Nav_Footer_Finish;
    private View btn_Nav_Footer_GoHelp;


    private MyListView lv_BorrowBooks;
    private MyListView lv_ReturnBooks;
    private TextView tv_BorrowCount;
    private TextView tv_ReturnCount;

    private RetBookerBorrowReturnCloseAction retBookerBorrowReturnCloseAction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_borrow_return_overview);
        setNavHeaderTtile(R.string.aty_nav_title_booker_borrow_return_overview);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btn_Nav_Footer_Finish = findViewById(R.id.btn_Nav_Footer_Finish);
        btn_Nav_Footer_GoHelp = findViewById(R.id.btn_Nav_Footer_GoHelp);
        tv_BorrowCount = findViewById(R.id.tv_BorrowCount);
        tv_ReturnCount = findViewById(R.id.tv_ReturnCount);
        lv_BorrowBooks = findViewById(R.id.lv_BorrowBooks);
        lv_ReturnBooks = findViewById(R.id.lv_ReturnBooks);
    }

    private void initEvent() {
        btn_Nav_Footer_Finish.setOnClickListener(this);
        btn_Nav_Footer_GoHelp.setOnClickListener(this);
    }

    private void initData() {

        retBookerBorrowReturnCloseAction = new RetBookerBorrowReturnCloseAction();
        List<BookerBookBean> borrowBooks = new ArrayList<>();

        borrowBooks.add(new BookerBookBean("1", "1", "安徒生童话故事1", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静2", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静3", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静4", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静5", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静6", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静7", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静8", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静9", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静10", "1", "1"));
        borrowBooks.add(new BookerBookBean("1", "1", "这个杀手不太冷静11", "1", "1"));

        List<BookerBookBean> returnBooks = new ArrayList<>();
        returnBooks.add(new BookerBookBean("1", "1", "西游记1", "1", "1"));
        returnBooks.add(new BookerBookBean("1", "1", "红楼梦2", "1", "1"));
        returnBooks.add(new BookerBookBean("1", "1", "西游记3", "1", "1"));
        returnBooks.add(new BookerBookBean("1", "1", "红楼梦4", "1", "1"));
        returnBooks.add(new BookerBookBean("1", "1", "西游记5", "1", "1"));
        returnBooks.add(new BookerBookBean("1", "1", "红楼梦6", "1", "1"));

        retBookerBorrowReturnCloseAction.setBorrowBooks(borrowBooks);
        retBookerBorrowReturnCloseAction.setReturnBooks(returnBooks);


        tv_BorrowCount.setText(String.valueOf(borrowBooks.size()));
        BookerBorrowReturnBookAdapter borrowBooksAdapter=new BookerBorrowReturnBookAdapter(BookerBorrowReturnOverviewActivity.this,borrowBooks);
        lv_BorrowBooks.setFocusable(false);
        lv_BorrowBooks.setClickable(false);
        lv_BorrowBooks.setPressed(false);
        lv_BorrowBooks.setEnabled(false);
        lv_BorrowBooks.setAdapter(borrowBooksAdapter);

        tv_ReturnCount.setText(String.valueOf(returnBooks.size()));
        BookerBorrowReturnBookAdapter returnBooksAdapter=new BookerBorrowReturnBookAdapter(BookerBorrowReturnOverviewActivity.this,returnBooks);
        lv_ReturnBooks.setFocusable(false);
        lv_ReturnBooks.setClickable(false);
        lv_ReturnBooks.setPressed(false);
        lv_ReturnBooks.setEnabled(false);
        lv_ReturnBooks.setAdapter(returnBooksAdapter);

    }

    @Override
    public void onClick(View v) {
        if (!NoDoubleClickUtil.isDoubleClick()) {

            int id = v.getId();

            if (id == R.id.btn_Nav_Footer_Finish) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.btn_Nav_Footer_GoHelp) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}