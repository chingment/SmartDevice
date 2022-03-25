package com.lumos.smartdevice.activity.booker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lumos.smartdevice.R;
import com.lumos.smartdevice.activity.booker.adapter.BookerBorrowReturnBookAdapter;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.vo.BookerBookVo;
import com.lumos.smartdevice.ui.my.MyListView;
import com.lumos.smartdevice.utils.NoDoubleClickUtil;

import java.util.List;

public class BookerBorrowReturnOverviewActivity extends BookerBaseActivity {

    private static final String TAG = "BookerBorrowReturnOverviewActivity";

    private View btn_Nav_Footer_Finish;
    private View btn_Nav_Footer_GoHelp;


    private MyListView lv_BorrowBooks;
    private MyListView lv_ReturnBooks;
    private TextView tv_BorrowCount;
    private TextView tv_ReturnCount;

    private RetBookerBorrowReturn retBookerBorrowReturn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booker_borrow_return_overview);
        setNavHeaderTtile(R.string.aty_nav_title_booker_borrow_return_overview);
        retBookerBorrowReturn = (RetBookerBorrowReturn) getIntent().getSerializableExtra("ret_booker_borrow_return");


        initView();
        initEvent();
        initData();
        setTimerByActivityFinish(120);
    }

    public void initView() {
        btn_Nav_Footer_Finish = findViewById(R.id.btn_Nav_Footer_Finish);
        btn_Nav_Footer_GoHelp = findViewById(R.id.btn_Nav_Footer_GoHelp);
        tv_BorrowCount = findViewById(R.id.tv_BorrowCount);
        tv_ReturnCount = findViewById(R.id.tv_ReturnCount);
        lv_BorrowBooks = findViewById(R.id.lv_BorrowBooks);
        lv_ReturnBooks = findViewById(R.id.lv_ReturnBooks);
    }

    public void initEvent() {
        btn_Nav_Footer_Finish.setOnClickListener(this);
        btn_Nav_Footer_GoHelp.setOnClickListener(this);
    }

    public void initData() {

        List<BookerBookVo> borrowBooks = retBookerBorrowReturn.getBorrowBooks();
        List<BookerBookVo> returnBooks = retBookerBorrowReturn.getReturnBooks();

        retBookerBorrowReturn.setBorrowBooks(borrowBooks);
        retBookerBorrowReturn.setReturnBooks(returnBooks);


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
                openActivity(intent);
                finish();
            } else if (id == R.id.btn_Nav_Footer_GoHelp) {
                Intent intent = new Intent(getAppContext(), BookerMainActivity.class);
                openActivity(intent);
                finish();
            }
        }
    }
}