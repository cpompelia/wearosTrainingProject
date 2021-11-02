package com.example.wearossample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.annotation.WorkerThread
import androidx.fragment.app.FragmentActivity
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.example.wearossample.databinding.ActivityMainBinding
import com.google.android.gms.wearable.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException

class MainActivity : FragmentActivity(), CapabilityClient.OnCapabilityChangedListener {
    private lateinit var remoteActivityHelper: RemoteActivityHelper
    private lateinit var nodeClient: NodeClient
    private lateinit var capabilityClient: CapabilityClient
    private var androidPhoneNodeWithApp: Node? = null

    private var mContext: android.content.Context? = null
    /*private var mTextView: TextView? = null*/
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this@MainActivity
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        capabilityClient = Wearable.getCapabilityClient(this)
        nodeClient = Wearable.getNodeClient(this)
        remoteActivityHelper = RemoteActivityHelper(this)

    }

    override fun onPause() {
        super.onPause()
        Wearable.getCapabilityClient(this).removeListener(this, CAPABILITY_PHONE_APP)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getCapabilityClient(this).addListener(this, CAPABILITY_PHONE_APP)
        lifecycleScope.launchWhenResumed {
            checkIfPhoneHasApp()
        }
    }

    /*
     * Updates UI when capabilities change (install/uninstall phone app).
     */
    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(TAG, "onCapabilityChanged(): $capabilityInfo")
        // There should only ever be one phone in a node set (much less w/ the correct capability), so
        // I am just grabbing the first one (which should be the only one).
        androidPhoneNodeWithApp = capabilityInfo.nodes.firstOrNull()
        updateUi()
    }

    private fun updateUi() {
        binding.numOfCompanions.text = androidPhoneNodeWithApp.toString()
    }

    @WorkerThread
    private suspend fun checkIfPhoneHasApp() {
        Log.d(TAG, "checkIfPhoneHasApp()")

        try {
            val capabilityInfo = capabilityClient
                .getCapability(CAPABILITY_PHONE_APP, CapabilityClient.FILTER_ALL)

            Log.d(TAG, "Capability request succeeded.")

            withContext(Dispatchers.Main) {
                // There should only ever be one phone in a node set (much less w/ the correct capability), so
                // I am just grabbing the first one (which should be the only one).
                androidPhoneNodeWithApp = capabilityInfo.result.nodes.firstOrNull()
                updateUi()
            }
        } catch (cancellationException: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            Log.d(TAG, "Capability request failed to return any results.")
        }
    }

    companion object {
        private const val TAG = "MainActivity(WEARABLE)"

        // Name of capability listed in Phone app's wear.xml.
        // IMPORTANT NOTE: This should be named differently than your Wear app's capability.
        private const val CAPABILITY_PHONE_APP = "verify_remote_eud_app"

        // Links to install mobile app for both Android (Play Store)
       /* private const val ANDROID_MARKET_APP_URI =
            "market://details?id=com.example.android.wearable.wear.wearverifyremoteapp"*/

    }

    fun sendPhoneRequest(view: View) {}


}