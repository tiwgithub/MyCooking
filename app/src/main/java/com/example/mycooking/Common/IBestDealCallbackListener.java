package com.example.mycooking.Common;

import com.example.mycooking.Model.Foodmenu;

import java.util.List;

public interface IBestDealCallbackListener {
    void onBestDealLoadSuccess(List<Foodmenu> bestDealModels);
    void onBestDealLoadFailed (String message);
}
