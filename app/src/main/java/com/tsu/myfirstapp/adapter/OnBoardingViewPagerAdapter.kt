package com.tsu.myfirstapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.tsu.myfirstapp.OnBoardingData
import com.tsu.myfirstapp.R

class OnBoardingViewPagerAdapter(private var context: Context, private var onBoardingDataList: List<OnBoardingData>) : PagerAdapter() {
    override fun getCount(): Int {
        return onBoardingDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.onboarding_screen_layout, null);

        val imageView: ImageView = view.findViewById(R.id.imageViewBoard)
        val titleB: TextView = view.findViewById(R.id.titleBoard)
        val descB: TextView = view.findViewById(R.id.descBoard)

        imageView.setImageResource(onBoardingDataList[position].imageUrl)
        titleB.text = onBoardingDataList[position].title
        descB.text = onBoardingDataList[position].desc

        container.addView(view)
        return view
    }
}