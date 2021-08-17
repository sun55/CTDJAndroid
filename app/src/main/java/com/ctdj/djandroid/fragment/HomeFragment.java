package com.ctdj.djandroid.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.MainActivity;
import com.ctdj.djandroid.activity.WeekCompetitionActivity;
import com.ctdj.djandroid.bean.BoxBean;
import com.ctdj.djandroid.bean.IntoHomeBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.UIHelper;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.FragmentHomeBinding;
import com.ctdj.djandroid.dialog.RadarScanDialog;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.google.gson.Gson;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    IntoHomeBean homeBean;
    BoxBean boxBean;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        RelativeLayout.LayoutParams topLayoutParams = (RelativeLayout.LayoutParams) binding.rlTop.getLayoutParams();
        topLayoutParams.topMargin = DisplayUtil.getStatusBarHeight(getActivity()) + DisplayUtil.dip2px(getActivity(), 27);
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams) binding.banner.getLayoutParams();
        layoutParams.topMargin = DisplayUtil.getStatusBarHeight(getActivity()) + DisplayUtil.dip2px(getActivity(), 74);
        Utils.setTextTypeface(binding.tvPurpleCount, "fonts/Avanti.ttf");
        Utils.setTextTypeface(binding.tvDiamondCount, "fonts/Avanti.ttf");
        Utils.setTextTypeface(binding.tvGoldCount, "fonts/Avanti.ttf");

        Utils.setTextTypeface(binding.tvPackageIndex1, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageIndex2, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageIndex3, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageIndex4, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageIndex5, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageValue1, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageValue2, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageValue3, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageValue4, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvPackageValue5, "fonts/avantibold.ttf");

    }

    private void initData() {
        requestHomeData();
        requestBoxData();
    }

    private void requestHomeData() {
        HttpClient.intoHome(getActivity(), new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                homeBean = new Gson().fromJson(result, IntoHomeBean.class);
                fillView();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(getActivity(), msg);
            }
        });
    }

    private void requestBoxData() {
        HttpClient.queryBoxList(getActivity(), new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                boxBean = new Gson().fromJson(result, BoxBean.class);
                fillBoxView();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(getActivity(), msg);
            }
        });
    }

    private void fillBoxView() {
        for (int i = 0; i < boxBean.getData().size(); i++) {
            BoxBean.Data data = boxBean.getData().get(i);
            switch (i) {
                case 0:
                    binding.tvPackageIndex1.setText(data.getTitle());
                    if (data.getSta() == 1) {
                        binding.ivPackageIcon1.setBackgroundResource(R.drawable.home_icon_7);
                        binding.tvPackageValue1.setVisibility(View.GONE);
                        binding.tvPackageIndex1.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 2) {
                        binding.ivPackageIcon1.setBackgroundResource(R.drawable.home_icon_6);
                        binding.tvPackageValue1.setVisibility(View.GONE);
                        binding.tvPackageIndex1.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 3) {
                        binding.ivPackageIcon1.setBackgroundResource(R.drawable.home_icon_3);
                        binding.tvPackageValue1.setVisibility(View.VISIBLE);
                        binding.tvPackageValue1.setText(data.getVal() + "");
                        binding.tvPackageIndex1.setTextColor(Color.parseColor("#80EBEBED"));
                    }
                    break;
                case 1:
                    binding.tvPackageIndex2.setText(data.getTitle());
                    if (data.getSta() == 1) {
                        binding.ivPackageIcon2.setBackgroundResource(R.drawable.home_icon_7);
                        binding.tvPackageValue2.setVisibility(View.GONE);
                        binding.tvPackageIndex2.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 2) {
                        binding.ivPackageIcon2.setBackgroundResource(R.drawable.home_icon_6);
                        binding.tvPackageValue2.setVisibility(View.GONE);
                        binding.tvPackageIndex2.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 3) {
                        binding.ivPackageIcon2.setBackgroundResource(R.drawable.home_icon_3);
                        binding.tvPackageValue2.setVisibility(View.VISIBLE);
                        binding.tvPackageValue2.setText(data.getVal() + "");
                        binding.tvPackageIndex2.setTextColor(Color.parseColor("#80EBEBED"));
                    }
                    break;
                case 2:
                    binding.tvPackageIndex3.setText(data.getTitle());
                    if (data.getSta() == 1) {
                        binding.ivPackageIcon3.setBackgroundResource(R.drawable.home_icon_7);
                        binding.tvPackageValue3.setVisibility(View.GONE);
                        binding.tvPackageIndex3.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 2) {
                        binding.ivPackageIcon3.setBackgroundResource(R.drawable.home_icon_6);
                        binding.tvPackageValue3.setVisibility(View.GONE);
                        binding.tvPackageIndex3.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 3) {
                        binding.ivPackageIcon3.setBackgroundResource(R.drawable.home_icon_3);
                        binding.tvPackageValue3.setVisibility(View.VISIBLE);
                        binding.tvPackageValue3.setText(data.getVal() + "");
                        binding.tvPackageIndex3.setTextColor(Color.parseColor("#80EBEBED"));
                    }
                    break;
                case 3:
                    binding.tvPackageIndex4.setText(data.getTitle());
                    if (data.getSta() == 1) {
                        binding.ivPackageIcon4.setBackgroundResource(R.drawable.home_icon_7);
                        binding.tvPackageValue4.setVisibility(View.GONE);
                        binding.tvPackageIndex4.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 2) {
                        binding.ivPackageIcon4.setBackgroundResource(R.drawable.home_icon_6);
                        binding.tvPackageValue4.setVisibility(View.GONE);
                        binding.tvPackageIndex4.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 3) {
                        binding.ivPackageIcon4.setBackgroundResource(R.drawable.home_icon_3);
                        binding.tvPackageValue4.setVisibility(View.VISIBLE);
                        binding.tvPackageValue4.setText(data.getVal() + "");
                        binding.tvPackageIndex4.setTextColor(Color.parseColor("#80EBEBED"));
                    }
                    break;
                case 4:
                    binding.tvPackageIndex5.setText(data.getTitle());
                    if (data.getSta() == 1) {
                        binding.ivPackageIcon5.setBackgroundResource(R.drawable.home_icon_7);
                        binding.tvPackageValue5.setVisibility(View.GONE);
                        binding.tvPackageIndex5.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 2) {
                        binding.ivPackageIcon5.setBackgroundResource(R.drawable.home_icon_6);
                        binding.tvPackageValue5.setVisibility(View.GONE);
                        binding.tvPackageIndex5.setTextColor(Color.parseColor("#EBEBED"));
                    } else if (data.getSta() == 3) {
                        binding.ivPackageIcon5.setBackgroundResource(R.drawable.home_icon_3);
                        binding.tvPackageValue5.setVisibility(View.VISIBLE);
                        binding.tvPackageValue5.setText(data.getVal() + "");
                        binding.tvPackageIndex5.setTextColor(Color.parseColor("#80EBEBED"));
                    }
                    break;

            }
        }
    }

    private void fillView() {
        binding.tvPurpleCount.setText(homeBean.getData().getStar() + "");
        binding.tvGoldCount.setText(homeBean.getData().getGold() + "");
        binding.tvDiamondCount.setText(homeBean.getData().getBalance() + "");
        binding.banner.setAdapter(new BannerImageAdapter<IntoHomeBean.Data.AdvList>(homeBean.getData().getAdvList()) {
            @Override
            public void onBindView(BannerImageHolder holder, IntoHomeBean.Data.AdvList data, int position, int size) {
                Glide.with(getActivity()).load(data.getImg()).centerCrop().into(holder.imageView);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.getType() == 2) {
                            UIHelper.showWebViewActivity(getActivity(), data.getUrl(), "");
                        }
                    }
                });
            }
        });
        for (int i = 0; i < homeBean.getData().getGoldChallenge().size(); i++) {
            if (i == 0) {
                binding.goldAvatar1.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(homeBean.getData().getGoldChallenge().get(i).getHeadimg()).into(binding.goldAvatar1);
            } else if (i == 1) {
                binding.goldAvatar2.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(homeBean.getData().getGoldChallenge().get(i).getHeadimg()).into(binding.goldAvatar2);
            } else if (i == 2) {
                binding.goldAvatar3.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(homeBean.getData().getGoldChallenge().get(i).getHeadimg()).into(binding.goldAvatar3);
            } else if (i == 3) {
                binding.goldAvatar4.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(homeBean.getData().getGoldChallenge().get(i).getHeadimg()).into(binding.goldAvatar4);
            }
        }

        for (int i = 0; i < homeBean.getData().getBalanceChallenge().size(); i++) {
            if (i == 0) {
                binding.diamondAvatar1.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(homeBean.getData().getBalanceChallenge().get(i).getHeadimg()).into(binding.diamondAvatar1);
            } else if (i == 1) {
                binding.diamondAvatar2.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(homeBean.getData().getBalanceChallenge().get(i).getHeadimg()).into(binding.diamondAvatar2);
            } else if (i == 2) {
                binding.diamondAvatar3.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(homeBean.getData().getBalanceChallenge().get(i).getHeadimg()).into(binding.diamondAvatar3);
            } else if (i == 3) {
                binding.diamondAvatar4.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(homeBean.getData().getBalanceChallenge().get(i).getHeadimg()).into(binding.diamondAvatar4);
            }
        }
    }

    private void initEvents() {
        binding.rlWeekCompetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), WeekCompetitionActivity.class));
            }
        });
        binding.llGoldPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showRadarDialog(1);
            }
        });

        binding.llDiamondPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showRadarDialog(2);
            }
        });

        binding.ivPackageIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxBean != null && boxBean.getData().size() == 5) {
                    if (boxBean.getData().get(0).getSta() == 1) {
                        Utils.showToast(getActivity(), "要进行约战比赛才能开红包");
                    } else if (boxBean.getData().get(0).getSta() == 2) {
                        updateReceiveBoxTask(boxBean.getData().get(0).getDay(), boxBean.getData().get(0).getCodeX());
                    }
                }
            }
        });

        binding.ivPackageIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxBean != null && boxBean.getData().size() == 5) {
                    if (boxBean.getData().get(1).getSta() == 1) {
                        Utils.showToast(getActivity(), "要进行约战比赛才能开红包");
                    } else if (boxBean.getData().get(1).getSta() == 2) {
                        updateReceiveBoxTask(boxBean.getData().get(1).getDay(), boxBean.getData().get(1).getCodeX());
                    }
                }
            }
        });

        binding.ivPackageIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxBean != null && boxBean.getData().size() == 5) {
                    if (boxBean.getData().get(2).getSta() == 1) {
                        Utils.showToast(getActivity(), "要进行约战比赛才能开红包");
                    } else if (boxBean.getData().get(2).getSta() == 2) {
                        updateReceiveBoxTask(boxBean.getData().get(2).getDay(), boxBean.getData().get(2).getCodeX());
                    }
                }
            }
        });

        binding.ivPackageIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxBean != null && boxBean.getData().size() == 5) {
                    if (boxBean.getData().get(3).getSta() == 1) {
                        Utils.showToast(getActivity(), "要进行约战比赛才能开红包");
                    } else if (boxBean.getData().get(3).getSta() == 2) {
                        updateReceiveBoxTask(boxBean.getData().get(3).getDay(), boxBean.getData().get(3).getCodeX());
                    }
                }
            }
        });

        binding.ivPackageIcon5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxBean != null && boxBean.getData().size() == 5) {
                    if (boxBean.getData().get(4).getSta() == 1) {
                        Utils.showToast(getActivity(), "要进行约战比赛才能开红包");
                    } else if (boxBean.getData().get(4).getSta() == 2) {
                        updateReceiveBoxTask(boxBean.getData().get(4).getDay(), boxBean.getData().get(4).getCodeX());
                    }
                }
            }
        });
    }

    private void updateReceiveBoxTask(String day, int code) {
        HttpClient.updateReceiveBoxTask(getActivity(), day, code, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                initData();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(getActivity(), msg);
            }
        });
    }
}