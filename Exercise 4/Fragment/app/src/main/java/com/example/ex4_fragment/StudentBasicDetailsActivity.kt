package com.example.ex4_fragment

import android.app.Fragment
import android.os.Bundle
//import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class StudentBasicDetailsActivity: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_basic_details_activity, container, false)
    }
}