package stanyliaINC.hungrymykola.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.model.*

class DishRepository(private val dishDao: DishDao) {
    private val firebaseDb = FirebaseDatabase.getInstance("").getReference("dishes")

    suspend fun insert(dish: Dish) {
        try {
            dishDao.insert(dish)
            uploadDishToFirebase(dish)
        } catch (e: Exception) {
            Log.e("insert: ", "Error inserting: $dish", e)
        }
    }

    suspend fun getAllDishes(): List<Dish> {
        return dishDao.getAllDishes()
    }

    suspend fun getDishByName(name: String): Dish? {
        return dishDao.getDishByName(name)
    }
    suspend fun updateDish(dish: Dish) {
        dishDao.update(dish)
        uploadDishToFirebase(dish)
    }

    private suspend fun uploadDishToFirebase(dish: Dish) {
        firebaseDb.child(dish.dishName).setValue(dish).await()
    }

    private suspend fun uploadDishesToFirebase(dishes: List<Dish>) {
        for (dish in dishes) {
            uploadDishToFirebase(dish)
        }
    }

    fun listenForDishUpdates() {
        firebaseDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dishList = mutableListOf<Dish>()

                if (snapshot.exists()) {
                    for (dishSnapshot in snapshot.children) {
                        val dish = dishSnapshot.getValue(Dish::class.java)
                        if (dish != null) {
                            dishList.add(dish)
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    dishDao.insertAll(dishList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DishRepository", "Error listening to dishes: ${error.message}")
            }
        })
    }

    fun syncAllDishesFromFirebase() {
        firebaseDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dishList = mutableListOf<Dish>()

                if (snapshot.exists()) {
                    for (dishSnapshot in snapshot.children) {
                        val dish = dishSnapshot.getValue(Dish::class.java)
                        if (dish != null) {
                            dishList.add(dish)
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    dishDao.insertAll(dishList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DishRepository", "Error syncing dishes: ${error.message}")
            }
        })
    }
}
