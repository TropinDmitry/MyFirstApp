package com.tsu.myfirstapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.DrawableContainer
import android.media.Image
import android.nfc.NfcAdapter.OnTagRemovedListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.tsu.myfirstapp.adapter.OnBoardingViewPagerAdapter
import com.tsu.myfirstapp.databinding.ActivityMainBinding
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var indicatorsContainer: LinearLayout

    var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter? = null
    var tabLayout: TabLayout? = null
    var onBoardingViewPager: ViewPager? = null
    var next: TextView? = null
    var position = 0
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*
        if(restorePrefData()) {
            val i = Intent(applicationContext, SignActivity::class.java)
            startActivity(i)
            finish()
        }*/
/*
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar!!.hide()*/

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tabLayout = findViewById(R.id.tab_indicator)

        next = findViewById(R.id.next)

        val onBoardingData: MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(OnBoardingData("Learn anytime\n and anywhere",
            "Quarantine is the perfect time to spend your day learning something new, from anywhere!", R.drawable.cool_kids_long_distance_relationship))
        onBoardingData.add(OnBoardingData("Find a course\n for you",
            "Quarantine is the perfect time to spend your day learning something new, from anywhere!", R.drawable.cool_kids_staying_home))
        onBoardingData.add(OnBoardingData("Improve",
            "Quarantine is the perfect time to spend your day learning something new, from anywhere!", R.drawable.cool_kids_high_tech))

        setOnBoardingViewPagerAdapter(onBoardingData)

        position = onBoardingViewPager!!.currentItem
        next?.setOnClickListener {
            if(position < onBoardingData.size) {
                position++
                onBoardingViewPager!!.currentItem = position
            }
            if(position == onBoardingData.size) {
                savePrefData()
                val i = Intent(applicationContext, SignActivity::class.java)
                startActivity(i)
            }
        }

        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if(tab.position == onBoardingData.size - 1){
                    next!!.text = "Let's Start"
                } else {
                    next!!.text = "Next"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

    fun onClick(v: View) {
        when(v) {
            binding.textView -> {
                val i = Intent(applicationContext, SignActivity::class.java)
                startActivity(i)
            }
        }
    }


    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>)  {
        onBoardingViewPager = findViewById(R.id.screenPager)
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }

    private fun savePrefData() {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()
        editor.putBoolean("isFirstTimeRun", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun", false)
    }
/*
    private fun setupIndicators() {
        indicatorsContainer = findViewById(R.id.tab_indicator)
        val indicators = onBoardingViewPagerAdapter?.let { arrayOfNulls<ImageView>(it.count) }
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        if (indicators != null) {
            for(i in indicators.indices) {
                indicators[i] = ImageView()
            }
        }
    }*/
/*
        binding.button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }*/

}