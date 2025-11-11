package com.example.appinterface.Api.contacto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R

class ContactAdapter(
    private var contactos: List<ContactoFormularioResponseDTO>,
    private val onEditClick: (ContactoFormularioResponseDTO) -> Unit,
    private val onDeleteClick: (ContactoFormularioResponseDTO) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    fun actualizarLista(nuevaLista: List<ContactoFormularioResponseDTO>) {
        contactos = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contacto, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contacto = contactos[position]
        holder.nombre.text = contacto.nombre ?: ""
        holder.correo.text = contacto.correo ?: ""
        holder.telefono.text = contacto.telefono ?: ""
        holder.estado.text = contacto.estado ?: ""
        holder.btnEdit.setOnClickListener { onEditClick(contacto) }
        holder.btnDelete.setOnClickListener { onDeleteClick(contacto) }
    }

    override fun getItemCount(): Int = contactos.size

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.txtNombre)
        val correo: TextView = view.findViewById(R.id.txtCorreo)
        val telefono: TextView = view.findViewById(R.id.txtTelefono)
        val estado: TextView = view.findViewById(R.id.txtEstado)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }
}
