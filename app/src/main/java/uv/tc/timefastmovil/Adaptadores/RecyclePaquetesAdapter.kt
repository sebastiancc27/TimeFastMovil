package uv.tc.timefastmovil.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import uv.tc.timefastmovil.Poko.Paquete
import uv.tc.timefastmovil.R

class RecyclePaquetesAdapter(var lista : ArrayList<Paquete>) : RecyclerView.Adapter<RecyclePaquetesAdapter.MyViewHolder>(){

    class MyViewHolder ( itemView : View) : RecyclerView.ViewHolder(itemView){
        var descripcion : TextView = itemView.findViewById(R.id.tv_descripcion)
        var alto : TextView = itemView.findViewById(R.id.tv_alto)
        var ancho : TextView = itemView.findViewById(R.id.tv_ancho)
        var peso : TextView = itemView.findViewById(R.id.tv_peso)
        var profundidad : TextView = itemView.findViewById(R.id.tv_profundidad)
        var noPaquete : TextView = itemView.findViewById(R.id.tv_no_paquete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycle_paquetes, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val paquete = lista[position]
        holder.descripcion.text = paquete.descripcion
        holder.alto.text ="Alto: "+ paquete.alto.toString()+" cm"
        holder.ancho.text = "Ancho: "+paquete.ancho.toString()+" cm"
        holder.peso.text = "Peso: "+paquete.peso.toString()+" kg"
        holder.profundidad.text = "Profundidad: "+paquete.profundidad.toString()+" cm"
        holder.noPaquete.text = paquete.noPaquete.toString()
    }


}