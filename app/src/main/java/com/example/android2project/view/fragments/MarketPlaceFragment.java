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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android2project.R;
import com.example.android2project.model.AdsAdapter;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.MarketPlaceViewModel;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MarketPlaceFragment extends Fragment {
    private MarketPlaceViewModel mViewModel;
    private AdsAdapter mAdsAdapter;
    private RecyclerView mMarketRecycler;

    private Advertisement currentAd;

    private final String TAG = "MarketPlaceFragment";
    private FirestorePagingOptions<Advertisement> mOptions;

    private Observer<Boolean> mOnDeletingAdSucceed;


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


        mOnDeletingAdSucceed = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mAdsAdapter.refresh();
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
        final SwipeRefreshLayout mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdsAdapter.refresh();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        mOptions = new FirestorePagingOptions.Builder<Advertisement>()
                .setLifecycleOwner(this)
                .setQuery(mViewModel.getAds(), config, Advertisement.class)
                .build();

        mMarketRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mMarketRecycler.setHasFixedSize(true);
        mAdsAdapter = new AdsAdapter(getContext(), mOptions);

        mAdsAdapter.setAdsAdapterListener(new AdsAdapter.AdsAdapterInterface() {
            @Override
            public void onAdClick(View view, int position) {
                currentAd = mAdsAdapter.getCurrentAd(position);
                DisplayAdFragment.newInstance(currentAd, mViewModel.getCurrentUser().getEmail())
                        .show(getChildFragmentManager(), "");
            }

            @Override
            public void onEditOptionClicked(int position, View view) {
                currentAd = mAdsAdapter.getCurrentAd(position);
                AdvertisementFragment.newInstance(currentAd).show(getChildFragmentManager(),"advertisement_fragment");
            }

            @Override
            public void onDeleteOptionClicked(int position, View view) {
                currentAd = mAdsAdapter.getCurrentAd(position);
                mViewModel.deleteAdvertisement(currentAd);
            }
        });

        mMarketRecycler.setAdapter(mAdsAdapter);

        addAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdvertisementFragment.newInstance(null).show(getChildFragmentManager(),"advertisement_fragment");
            }
        });

        return rootView;
    }

    private void startObservation() {
        if (mViewModel != null) {
            mViewModel.getOnAdDeletingSucceed().observe(this,mOnDeletingAdSucceed);
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mAdsAdapter.startListening();
        Log.d(TAG, "onStart: zxc adapter" + mAdsAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdsAdapter.stopListening();
        Log.d(TAG, "onStop: zxc adapter "+mAdsAdapter);
    }


    public void uploadAdSucceed(Advertisement ad, AlertDialog loadingDialog) {
        Log.d(TAG, "uploadAdSucceed: zxc adapter "+mAdsAdapter);
        loadingDialog.dismiss();
        mAdsAdapter.refresh();

    }


}
