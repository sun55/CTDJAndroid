package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.CountryBean;
import com.ctdj.djandroid.bean.UpdatePersonalBean;
import com.ctdj.djandroid.bean.UploadBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityEditBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.net.UserInfoBean;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditActivity extends BaseActivity {

    ActivityEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        LinearLayoutCompat.LayoutParams l = (LinearLayoutCompat.LayoutParams) binding.titleView.getLayoutParams();
        l.topMargin = DisplayUtil.getStatusBarHeight(this);
        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        binding.itemNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, EditNameActivity.class);
                if (bean == null || TextUtils.isEmpty(bean.mname)) {
                    intent.putExtra("nickname", "");
                } else {
                    intent.putExtra("nickname", bean.mname);
                }
                startActivity(intent);
            }
        });

        binding.itemBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBirthday();
            }
        });
        binding.itemLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCity();
            }
        });
        parserCity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillView();
    }

    UserInfoBean bean;

    private void fillView() {
        bean = MyApplication.getInstance().getUserInfo();
        Glide.with(this).load(bean.headimg).into(binding.avatar);
        binding.itemNickname.setRightText(bean.mname);
        binding.itemSex.setRightText(bean.sex == 1 ? "男" : "女");
        if (TextUtils.isEmpty(bean.birthday)) {
            binding.itemBirthday.setRightText("");
        } else {
            int age = Utils.getAge(bean.birthday);
            String constellation = Utils.constellation(bean.birthday);
            binding.itemBirthday.setRightText(age + " " + constellation);
        }
        binding.itemLocation.setRightText(TextUtils.isEmpty(bean.province) || TextUtils.isEmpty(bean.city) ? "" : bean.province + " " + bean.city);
    }

    public void selectPhoto() {
        if (!Utils.checkPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            LogUtil.e("请求权限");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return;
        }
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
//                    .theme(R.style.picture_white_style)
                .isCamera(true)
                .imageEngine(GlideEngine.createGlideEngine())
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
//                .isSingleDirectReturn(true)
                .isEnableCrop(true)
                .withAspectRatio(1, 1)
                .freeStyleCropEnabled(true)
                .showCropFrame(true)
                .isDragFrame(false)
//                .isCompress(true)
                .maxSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .synOrAsy(true)
//                .compressQuality(80)
                .forResult(1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
        if (list == null || list.size() <= 0) {
            return;
        }
        if (requestCode == 1000) {
            List<String> filePaths = new ArrayList<>();
            if (list.get(0).getPath().contains("content://")) {
                LogUtil.e(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getCutPath())));
                filePaths.add(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getCutPath())));
            } else {
                filePaths.add(list.get(0).getCutPath());
            }
            uploadImage(filePaths.get(0));
        }
    }

    private void uploadImage(String path) {
        Utils.showLoadingDialog(this);
        HttpClient.uploadImage(this, path, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.hideLoadingDialog();
                Utils.showToast(EditActivity.this, "上传成功！");
                UploadBean bean = new Gson().fromJson(result, UploadBean.class);
                updatePersonal(1, bean.url);
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(EditActivity.this, msg);
                Utils.hideLoadingDialog();
            }
        });
    }

    TimePickerView timePickerView;

    public void selectBirthday() {
        if (timePickerView == null) {
            Calendar selectedDate = Calendar.getInstance();
            if (TextUtils.isEmpty(bean.birthday)) {
                selectedDate.set(2002, 0, 1);
            } else {
                selectedDate.setTime(Utils.getDateByString(bean.birthday, ""));
            }
            Calendar startDate = Calendar.getInstance();
            startDate.set(1960, 0, 1);
            Calendar endDate = Calendar.getInstance();
            endDate.set(2018, 11, 30);
            timePickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    LogUtil.e("month:" + month + ", day:" + day);
                    String birthday = calendar.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
                    updatePersonal(3, birthday);
                }
            }).setLayoutRes(R.layout.pickerview_custom_time_view, new CustomListener() {
                @Override
                public void customLayout(View v) {
                    TextView cancelTv = v.findViewById(R.id.cancel_tv);
                    TextView sureTv = v.findViewById(R.id.sure_tv);
                    cancelTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timePickerView.dismiss();
                        }
                    });
                    sureTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timePickerView.returnData();
                            timePickerView.dismiss();
                        }
                    });
                }
            }).setType(new boolean[]{true, true, true, false, false, false})
                    .setDividerColor(Color.parseColor("#00000000"))
                    .setDate(selectedDate)
                    .setBgColor(Color.parseColor("#22252f"))
                    .setRangDate(startDate, endDate)
                    .setTextColorCenter(Color.parseColor("#ebebed"))
                    .setOutSideCancelable(false)
                    .setLineSpacingMultiplier(2.4f)
                    .build();
        }
        timePickerView.show();
    }

    private OptionsPickerView optionsPickerView;

    public void setCity() {
        if (provinceList.size() == 0) {
            Utils.showToast(this, "正在加载城市数据。。。");
            parserCity();
            return;
        }
        if (optionsPickerView == null) {
            optionsPickerView = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    String province = provinceList.get(options1);
                    String city = cityList.get(options1).get(options2);
                    updatePersonal(4, province + "-" + city);
                }
            }).setLayoutRes(R.layout.pickerview_custom_city_view, new CustomListener() {
                @Override
                public void customLayout(View v) {
                    TextView cancelTv = v.findViewById(R.id.cancel_tv);
                    TextView sureTv = v.findViewById(R.id.sure_tv);
                    cancelTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            optionsPickerView.dismiss();
                        }
                    });
                    sureTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            optionsPickerView.returnData();
                            optionsPickerView.dismiss();
                        }
                    });
                }
            }).setDividerColor(Color.parseColor("#00000000"))
                    .setBgColor(Color.parseColor("#22252f"))
                    .setTextColorCenter(Color.parseColor("#ebebed"))
                    .setOutSideCancelable(false)
                    .setLineSpacingMultiplier(2.4f)
                    .build();
            if (!TextUtils.isEmpty(bean.province) && !TextUtils.isEmpty(bean.city)) {
                optionsPickerView.setSelectOptions(provinceList.indexOf(bean.province), cityList.get(provinceList.indexOf(bean.province)).indexOf(bean.city));
            }
            optionsPickerView.setPicker(provinceList, cityList);
        }
        optionsPickerView.show();
    }


    List<String> provinceList = new ArrayList<>();
    List<List<String>> cityList = new ArrayList<>();

    private void parserCity() {
        binding.titleView.post(new Runnable() {
            @Override
            public void run() {
                CountryBean countryBean = new Gson().fromJson(getResources().getString(R.string.city_json), CountryBean.class);
                for (CountryBean.Province province : countryBean.getProvince()) {
                    provinceList.add(province.getName());
                    List<String> cityBeans = new ArrayList<>();
                    for (CountryBean.Province.City c : province.getCity()) {
                        cityBeans.add(c.getName());
                    }
                    cityList.add(cityBeans);
                }
            }
        });
    }

    private void updatePersonal(int type, String param) {
        HttpClient.updatePersonal(this, type, param, "", new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                UpdatePersonalBean bean = new Gson().fromJson(result, UpdatePersonalBean.class);
                MyApplication.getInstance().saveUserInfo(bean.data);
                Utils.showToast(EditActivity.this, "修改成功");
                fillView();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(EditActivity.this, msg);
            }
        });
    }
}