package com.example.samplephoneapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.Node

class ConnectionViewModel: ViewModel() {
    private val mutableItemOfInterest = MutableLiveData<ConnectionDetails>()
    val latestData: LiveData<ConnectionDetails> get() = mutableItemOfInterest

    fun updateData(wearNodes: Set<Node>?, connectedNodes: List<Node>?){
        mutableItemOfInterest.value?.wearNodesWithApp = wearNodes
        mutableItemOfInterest.value?.allConnectedNodes = connectedNodes
    }

    class ConnectionDetails {
        var wearNodesWithApp: Set<Node>? = null
        var allConnectedNodes: List<Node>? = null
    }
}