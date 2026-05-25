package com.example.btarduino

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter(
    private var items: List<BluetoothDevice>,
    private val onClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.deviceName)
        val mac: TextView = v.findViewById(R.id.deviceMac)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<BluetoothDevice>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val d = items[position]
        holder.name.text = d.name ?: "(sin nombre)"
        holder.mac.text = d.address
        holder.itemView.setOnClickListener { onClick(d) }
    }
}
