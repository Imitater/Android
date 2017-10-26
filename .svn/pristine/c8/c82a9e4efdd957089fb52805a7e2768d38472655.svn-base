/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.ruiyihong.toyshop.bean.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Burt on 2017/7/16 0016.
 */

public class ContactUtils {

    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

    /** 联系人显示名称 **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /** 电话号码 **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /** 头像ID **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /** 联系人的ID **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

    public static List<ContactInfo> getAllContacts(Context context) {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        // 获取解析者
        ContentResolver resolver = context.getContentResolver();
        try {
            // 获取手机联系人
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONES_PROJECTION, null, null, null);
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    ContactInfo info = new ContactInfo();

                    // 得到手机号码
                    String phoneNumber = phoneCursor
                            .getString(PHONES_NUMBER_INDEX);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                        info.phone=phoneNumber;
                    // 得到联系人名称
                    String contactName = phoneCursor
                            .getString(PHONES_DISPLAY_NAME_INDEX);
                    info.name=contactName;
                    // 得到联系人ID
                    Long contactid = phoneCursor
                            .getLong(PHONES_CONTACT_ID_INDEX);
                    info.id=contactid+"";
                    list.add(info);
                }
                phoneCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public static List<ContactInfo> getContactsList(Context context) {

        List<ContactInfo> list = new ArrayList<ContactInfo>();
        ContactInfo bean = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor == null && cursor.getCount() <= 0) {
            return null;
        }

        while (cursor.moveToNext()) {
            bean = new ContactInfo();

            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));//姓名

            wirteNumbers(resolver, name, bean);
            list.add(bean);
        }
        cursor.close();
        return list;
    }

    /**
     * 根据联系人姓名查询电话
     * 并写入
     */
    private static void wirteNumbers(final ContentResolver contentResolver, String name, final ContactInfo bean) {

        Cursor dataCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.DATA1},
                ContactsContract.Data.DISPLAY_NAME + "= ? ",
                new String[]{name}, null);
        if (dataCursor == null) {

            return;
        }
        if (dataCursor.getCount() > 0) {
            bean.name=name;
            while (dataCursor.moveToNext()) {
                String number = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DATA1));

                if (TextUtils.isEmpty(number) || !TextUtils.isDigitsOnly(number = number.replace(" ", ""))) {
                    continue;
                }
                bean.phone=number;
            }
            dataCursor.close();
        } else {
        }
        return;
    }

}
