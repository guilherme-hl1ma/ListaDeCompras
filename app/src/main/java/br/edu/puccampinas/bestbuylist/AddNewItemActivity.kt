package br.edu.puccampinas.bestbuylist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import br.edu.puccampinas.bestbuylist.databinding.ActivityAddNewItemBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewItemBinding

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewBinding()

        adjustLayoutConstraint()

        createDatabase()

        binding.btnAddProduto.setOnClickListener{
            addNewItem()
        }

        binding.btnHome.setOnClickListener{
            startActivity(Intent(this, BuyListActivity::class.java))
            finish()
        }
    }

    private fun adjustLayoutConstraint() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewBinding() {
        binding = ActivityAddNewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun createDatabase() {
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "build_list_db"
        ).build()
    }

    /*
    * Função que faz a inserção do item
    * */
    private fun addNewItem() {
        if (binding.etDescricao.text?.isNotEmpty() == true) {
            CoroutineScope(Dispatchers.IO).launch {
                db.itemDao().insert(Item(0, binding.etDescricao.text.toString(), false))
            }
            binding.etDescricao.text = null
            Snackbar.make(binding.clSnackbar, "Item adicionado", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(binding.clSnackbar, "Preencha a descrição", Snackbar.LENGTH_LONG).show()
        }
    }
}