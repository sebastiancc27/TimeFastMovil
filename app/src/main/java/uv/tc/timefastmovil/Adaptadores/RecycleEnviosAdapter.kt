package uv.tc.timefastmovil.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import uv.tc.timefastmovil.Interfaces.ListenerRecycleEnvios
import uv.tc.timefastmovil.Poko.Envio
import uv.tc.timefastmovil.R

class RecycleEnviosAdapter(var lista : ArrayList<Envio>, var listener : ListenerRecycleEnvios): RecyclerView.Adapter<RecycleEnviosAdapter.MyViewHolder>() {

    class MyViewHolder ( itemView : View) : RecyclerView.ViewHolder(itemView){
        var destino : TextView = itemView.findViewById(R.id.card_et_destino)
        var estatus : TextView = itemView.findViewById(R.id.card_et_estatus)
        var itemRecycle :CardView = itemView.findViewById(R.id.item_recycle_envios)
        var noGuia : TextView = itemView.findViewById(R.id.tv_no_guia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycle_envios, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val envio = lista[position]
        holder.destino.text=envio.destino
        holder.estatus.text= envio.estatus
        holder.noGuia.text = envio.noGuia.toString()
        holder.itemRecycle.setOnClickListener{
            listener.clickEnvio(position)
        }
    }

}