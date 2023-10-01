package com.example.realfoodforall

data class DonorFoodData(
    var foodDonationId:String? = null,
    val foodDonorUID:String? = null,
    val foodName:String? = null,
    val foodPortion:String? = null,
    val foodLocation:String? = null,
    val foodDate:String? = null,
    val foodFromTime:String? = null,
    val foodToTime:String? = null,
    val foodURL:String? = null
)