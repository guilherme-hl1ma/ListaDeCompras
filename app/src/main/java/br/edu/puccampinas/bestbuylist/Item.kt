package br.edu.puccampinas.bestbuylist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name="description") val description: String,
    @ColumnInfo(name="checked") val checked: Boolean
)