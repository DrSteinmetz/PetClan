package com.example.android2project.view.fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.android2project.R;
import com.example.android2project.model.AdsAdapter;
import com.example.android2project.model.Advertisement;
import com.example.android2project.model.ViewModelEnum;
import com.example.android2project.model.ViewModelFactory;
import com.example.android2project.viewmodel.MarketPlaceViewModel;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

public class MarketPlaceFragment extends Fragment {
    private MarketPlaceViewModel mViewModel;
    private AdsAdapter mAdsAdapter;

    private final String TAG = "MarketPlaceFragment";

    public static MarketPlaceFragment newInstance() {
        return new MarketPlaceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext(),
                ViewModelEnum.MarketPlace)).get(MarketPlaceViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.market_place_fragment, container, false);
        final RecyclerView adsRecycler = rootView.findViewById(R.id.ads_recycler);
        final ImageButton searchBtn = rootView.findViewById(R.id.search_btn);
        final Spinner optionsFilter = rootView.findViewById(R.id.filter_option_spinner);
        final RadioGroup radioGroup = rootView.findViewById(R.id.radio_group_rg);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<Advertisement> options = new FirestorePagingOptions.Builder<Advertisement>()
                .setLifecycleOwner(this)
                .setQuery(mViewModel.getAds(), config, Advertisement.class)
                .build();

        adsRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));
        adsRecycler.setHasFixedSize(true);
        mAdsAdapter = new AdsAdapter(options);
        adsRecycler.setAdapter(mAdsAdapter);


        return rootView;
    }
}
