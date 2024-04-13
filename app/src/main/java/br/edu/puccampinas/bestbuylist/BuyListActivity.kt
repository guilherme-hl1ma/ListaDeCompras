package br.edu.puccampinas.bestbuylist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import br.edu.puccampinas.bestbuylist.databinding.ActivityBuyListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuyListActivity : AppCompatActivity(), OnDeleteItem, OnCheckItem {
    // referenciando todas as views por binding.
    private lateinit var binding: ActivityBuyListBinding

    // array list com os itens da lista.
    private lateinit var items: ArrayList<Item>

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // instanciando o database
        createDatabase()

        // setup do layout
        setupViewBinding()

        // ajustando as dimensoes do constraint (Edge to Edge) para versões do android a partir da 19.
        adjustLayoutConstraint()

        // preparando a recycler view
        prepareRecycleView()

        // carregando a lista de itens.
        loadBuyList()

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddNewItemActivity::class.java))
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
        binding = ActivityBuyListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun prepareRecycleView() {
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.setHasFixedSize(true)
    }

    private fun createDatabase() {
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "build_list_db"
        ).build()
    }

    // carregar a lista
    private fun loadBuyList() {
        getBuyListItems(this@BuyListActivity, this@BuyListActivity)
    }

    override fun delete(item: Item) {
        deleteBuyListItem(item)
    }

    override fun updateItemCheck(item: Item) {
        updateItemChecker(item)
    }

    private fun getBuyListItems(onDeleteListener: OnDeleteItem, onCheckerListener: OnCheckItem ) {
        CoroutineScope(Dispatchers.IO).launch {
            val itemsQuery = db.itemDao().getAll()
            withContext(Dispatchers.Main) {
                val itemsAdapter = AdapterListItem(itemsQuery, onDeleteListener, onCheckerListener)
                binding.rvItems.adapter = itemsAdapter
            }
        }
    }

    private fun deleteBuyListItem(item: Item) {
        CoroutineScope(Dispatchers.IO).launch {
            db.itemDao().delete(item)
            withContext(Dispatchers.Main) {
                recreate()
            }
        }
    }

    private fun updateItemChecker(item: Item) {
        CoroutineScope(Dispatchers.IO).launch {
            db.itemDao().update(Item(item.id, item.description, !item.checked))
        }
    }
}