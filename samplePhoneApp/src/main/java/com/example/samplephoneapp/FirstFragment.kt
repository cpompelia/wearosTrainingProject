package com.example.samplephoneapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.samplephoneapp.databinding.FragmentFirstBinding
import com.example.samplephoneapp.utils.sendNotification
import com.example.samplephoneapp.viewModel.ConnectionViewModel

class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    val handler: Handler? = null
    var recvdMessageNumber: Int = 0
    var sentMessageNumber: Int = 0

    private val viewModel: ConnectionViewModel by activityViewModels()
    override fun onCreateView(
                    inflater: LayoutInflater, container: ViewGroup?,
                    savedInstanceState: Bundle?
                ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        createChannel(
            getString(R.string.sample_notification_channel_id),
            getString(R.string.sample_notification_channel_name)
        )

        viewModel.latestData.observe( viewLifecycleOwner, Observer { item ->

            val data = viewModel.latestData.value
            when {
                data?.wearNodesWithApp == null || data.allConnectedNodes == null -> {
                    Log.d(TAG, "Waiting on Results for both connected nodes and nodes with app")
                    binding.informationTextView.text = getString(R.string.message_checking)
                }
                data.allConnectedNodes!!.isEmpty() -> {
                    Log.d(TAG, "No devices")
                    binding.informationTextView.text = getString(R.string.message_checking)
                }
                data.wearNodesWithApp!!.isEmpty() -> {
                    Log.d(TAG, "Missing on all devices")
                    binding.informationTextView.text = getString(R.string.message_missing_all)
                }
                data.wearNodesWithApp!!.size < data.allConnectedNodes!!.size -> {

                    Log.d(TAG, "Installed on some devices")
                    binding.informationTextView.text =
                        getString(R.string.message_some_installed, data.wearNodesWithApp.toString())
                }
                else -> {
                    Log.d(TAG, "Installed on all devices")
                    binding.informationTextView.text =
                        getString(R.string.message_all_installed, data.wearNodesWithApp.toString())
                }
            }
        })

        val view = binding.root
        return view
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.sample_notification_channel_desc)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonNotification.setOnClickListener{
            android.widget.Toast.makeText(context, "Clickaroo", android.widget.Toast.LENGTH_SHORT).show()
            val notificationManager = context?.let { it1 -> ContextCompat.getSystemService(it1, NotificationManager::class.java) } as NotificationManager
            notificationManager.sendNotification(
                requireContext().getText(R.string.notification_text).toString(),
                requireContext()
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "FirstFragment(EUD)"

    }
}