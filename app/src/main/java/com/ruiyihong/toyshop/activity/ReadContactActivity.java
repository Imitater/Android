/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.activity;

import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.ContactInfo;
import com.ruiyihong.toyshop.util.ContactUtils;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.view.CommonLoadingView;
import com.ruiyihong.toyshop.view.read_lxr.ContactsModel;
import com.ruiyihong.toyshop.view.read_lxr.SideBar;
import com.ruiyihong.toyshop.view.read_lxr.SideBarAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opensource.jpinyin.PinyinHelper;

public class ReadContactActivity extends BaseActivity {

    private ListView listView;
    private TextView txtShowCurrentLetter;
    private SideBar sideBar;
    private SideBarAdapter myAdapter;
    private List<ContactsModel> list;
    private List<ContactsModel> list1;
    private CommonLoadingView loadingView;
    private EditText et_search_contact;
    private TextView tv_search_icon;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_contact;
    }
    @Override
    protected void initView() {
        txtShowCurrentLetter =  findViewById(R.id.txt_show_current_letter);
        et_search_contact = findViewById(R.id.et_search_contact);
        loadingView = findViewById(R.id.common_LoadingView);
        tv_search_icon = findViewById(R.id.tv_search_icon);
        listView =  findViewById(R.id.list_view);
        loadingView.setVisibility(View.VISIBLE);
        sideBar =  findViewById(R.id.side_bar);
    }

    @Override
    protected void initData() {
        setCallbackInterface();
        loadingView.load();

        new Thread(new Runnable() {
            @Override
            public void run() {
                list1 = initDatas();
                chineseToPinyin(list1);
                //将联系人列表的标题字母排序
                Collections.sort(list1, new Comparator<ContactsModel>() {
                    @Override
                    public int compare(ContactsModel lhs, ContactsModel rhs) {
                        return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
                    }
                });
                //将联系人列表的标题字母放到List<String>列表中，准备数据去重
                List<String> getLetter = new ArrayList<>();
                for (int i = 0; i < list1.size(); i++) {
                    getLetter.add(list1.get(i).getFirstLetter());
                }
                //数据去重
                getLetter = removeDuplicate(getLetter);
                //将联系人列表的字母标题排序
                Collections.sort(getLetter, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return lhs.compareTo(rhs);
                    }
                });
                //设置已排序好的标题
                sideBar.setLetter(getLetter);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAdapter = new SideBarAdapter(ReadContactActivity.this, list1, R.layout.adapter_side_bar);
                        listView.setAdapter(myAdapter);
                        loadingView.loadSuccess(false);
                    }
                });

            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactsModel contact = list1.get(i);
                String s = contact.getName() +":"+ contact.getPhone();
                finish_thisForResult(s);
            }
        });
    }

    /**
     * 将中文转化为拼音
     */
    public void chineseToPinyin(List<ContactsModel> list) {
        for (int i = 0; i < list.size(); i++) {
            ContactsModel contactsModel1 = list.get(i);
            //将汉字转换为拼音
            String pinyinString = PinyinHelper.getShortPinyin(list.get(i).getName());
            //将拼音字符串转换为大写拼音
            String upperCasePinyinString = String.valueOf(pinyinString.charAt(0)).toUpperCase();
            //获取大写拼音字符串的第一个字符
            char tempChar = upperCasePinyinString.charAt(0);
            if (tempChar < 'A' || tempChar > 'Z') {
                contactsModel1.setFirstLetter("#");
            } else {
                contactsModel1.setFirstLetter(String.valueOf(tempChar));
            }
        }
    }
    /**
     * 设置回调接口
     */
    public void setCallbackInterface() {
        //回调接口
        sideBar.setOnCurrentLetterListener(new SideBar.OnCurrentLetterListener() {
            @Override
            public void showCurrentLetter(String currentLetter) {
                txtShowCurrentLetter.setVisibility(View.VISIBLE);
                txtShowCurrentLetter.setText(currentLetter);

                int position = myAdapter.getCurrentLetterPosition(currentLetter);
                if (position != -1)
                    listView.setSelection(position);
            }
            @Override
            public void hideCurrentLetter() {
                txtShowCurrentLetter.setVisibility(View.GONE);
            }
        });
    }
    /**
     * 初始化数据
     */
    public List<ContactsModel> initDatas() {
        list = new ArrayList<>();
        ContactsModel contactsModel;
        //todo 动态申请权限
        List<ContactInfo> allContacts;
        if (Build.VERSION.SDK_INT >= 23) {
            allContacts = ContactUtils.getContactsList(ReadContactActivity.this);
        } else {
            allContacts = ContactUtils.getAllContacts(ReadContactActivity.this);
        }
        for (int i = 0; i < allContacts.size(); i++) {
            contactsModel = new ContactsModel();
            contactsModel.setName(allContacts.get(i).name);
            contactsModel.setPhone(allContacts.get(i).phone);
            list.add(contactsModel);
        }
        return list;
    }
    @Override
    protected void initEvent() {
        et_search_contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_search_icon.setVisibility(View.GONE);
                for (int q=0;q<list.size();q++){
                    ContactsModel cc = list.get(q);
                    if(!TextUtils.isEmpty(charSequence)){
                        if(cc.getName().contains(charSequence.toString())||cc.getPhone().contains(charSequence)){
                            listView.setSelection(q);
                           // LogUtil.e("Condition: "+charSequence+" Name: "+cc.getName()+" Phone:"+cc.getPhone()+"q:" +q);
                        }
                    }else{
                        tv_search_icon.setVisibility(View.VISIBLE);
                        listView.setSelection(0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    @Override
    protected void processClick(View v) throws IOException {

    }

    public <T> List<T> removeDuplicate(List<T> list) {
        Set<T> h = new HashSet<>(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    private void finish_thisForResult(String phone) {
        Intent intent = new Intent();
        intent.putExtra("phone",phone);
        setResult(1,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish_thisForResult("");
    }
}
