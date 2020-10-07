package com.example.android2project.view.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android2project.R;
import com.example.android2project.model.AdsAdapter;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.DeleteDialog;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.MarketPlaceViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MarketPlaceFragment extends Fragment {

    private MarketPlaceViewModel mViewModel;
    private AdsAdapter mAdsAdapter;
    private RecyclerView mMarketRecycler;

    private Advertisement mCurrentAd;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Observer<List<Advertisement>> mOnDownloadAdsSucceed;
    private Observer<String> mOnDownloadAdsFailed;

    private Observer<Integer> mOnAdDeletionSucceed;
    private Observer<String> mOnAdDeletionFailed;

    private final String TAG = "MarketPlaceFragment";

    public MarketPlaceFragment() {
    }

    public static MarketPlaceFragment newInstance() {
        return new MarketPlaceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.MarketPlace)).get(MarketPlaceViewModel.class);

        mOnDownloadAdsSucceed = new Observer<List<Advertisement>>() {
            @Override
            public void onChanged(List<Advertisement> advertisements) {
                if (mAdsAdapter != null) {
                    mAdsAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        };

        mOnDownloadAdsFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        mOnAdDeletionSucceed = new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                mAdsAdapter.notifyItemRemoved(position);
            }
        };

        mOnAdDeletionFailed = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        };

        startObservation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_market_place, container, false);
        mMarketRecycler = rootView.findViewById(R.id.ads_recycler);
        final ImageButton searchBtn = rootView.findViewById(R.id.search_btn);
        final Spinner optionsFilter = rootView.findViewById(R.id.filter_option_spinner);
        final RadioGroup radioGroup = rootView.findViewById(R.id.radio_group_rg);
        final FloatingActionButton addAdBtn = rootView.findViewById(R.id.add_ad_btn);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.getAllAds();
            }
        });

        mMarketRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mMarketRecycler.setHasFixedSize(true);
        mAdsAdapter = new AdsAdapter(getContext(), mViewModel.getCurrentUser().getEmail(), mViewModel.getAdList());


        mAdsAdapter.setAdsAdapterListener(new AdsAdapter.AdsAdapterInterface() {
            @Override
            public void onAdClick(View view, int position) {
                mCurrentAd = mViewModel.getAdList().get(position);
                DisplayAdFragment.newInstance(mCurrentAd, mViewModel.getCurrentUser().getEmail())
                        .show(getChildFragmentManager(), "");
            }

            @Override
            public void onEditOptionClicked(int position, View view) {
                mCurrentAd = mViewModel.getAdList().get(position);
                AdvertisementFragment.newInstance(mCurrentAd)
                        .show(getChildFragmentManager(), "advertisement_fragment");
            }

            @Override
            public void onDeleteOptionClicked(final int position, View view) {
                mCurrentAd = mViewModel.getAdList().get(position);
                final DeleteDialog deleteDialog = new DeleteDialog(getContext());
                deleteDialog.setPromptText("Are You Sure You Want To Delete Your Advertisement?");
                deleteDialog.setOnActionListener(new DeleteDialog.DeleteDialogActionListener() {
                    @Override
                    public void onYesBtnClicked() {
                        mViewModel.deleteAdvertisement(mCurrentAd, position);
                        deleteDialog.dismiss();
                    }

                    @Override
                    public void onNoBtnClicked() {
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.show();

            }
        });

        mMarketRecycler.setAdapter(mAdsAdapter);

        addAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdvertisementFragment.newInstance(null).show(getChildFragmentManager(), "advertisement_fragment");
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orderBy = optionsFilter.getSelectedItemPosition();
                final boolean isDes = radioGroup.getCheckedRadioButtonId() == R.id.filter_high_to_low_rb;
                mViewModel.getFilteredAds(orderBy, isDes);
            }
        });

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getDownloadAdsSucceed().observe(this, mOnDownloadAdsSucceed);
            mViewModel.getDownloadAdsFailed().observe(this, mOnDownloadAdsFailed);
            mViewModel.getAdDeletionSucceed().observe(this, mOnAdDeletionSucceed);
            mViewModel.getAdDeletionFailed().observe(this, mOnAdDeletionFailed);
        }
    }

    public void onUploadAdSucceed(Advertisement ad, AlertDialog loadingDialog) {
        mViewModel.getAdList().add(0, ad);
        mAdsAdapter.notifyItemInserted(0);
        loadingDialog.dismiss();
    }
}
