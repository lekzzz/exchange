package com.arudanovsky.exchange.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.arudanovsky.exchange.R
import com.arudanovsky.exchange.view.currencies.CurrenciesFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = CurrenciesFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.activity_main, fragment, "currencies").commit()
        }
    }
}
