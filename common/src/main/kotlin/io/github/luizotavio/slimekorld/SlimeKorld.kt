package io.github.luizotavio.slimekorld

import io.github.luizotavio.slimekorld.factory.LegacySlimeFactory
import io.github.luizotavio.slimekorld.factory.SlimeWorldFactory
import io.github.luizotavio.slimekorld.storage.AbstractSlimeWorldStorage
import io.github.luizotavio.slimekorld.storage.LegacySlimeStorage
import org.bukkit.Bukkit
import java.io.File

object SlimeKorld {

    private lateinit var delegator: SlimeDelegator

    fun createDelegator(storage: File = Bukkit.getWorldContainer()): SlimeDelegator {
        if (::delegator.isInitialized) {
            throw IllegalStateException("SlimeDelegator already created")
        }

        delegator = SlimeDelegator(
            LegacySlimeStorage(storage),
            LegacySlimeFactory(),
        )

        return delegator
    }

    fun createSlimeWorld(file: File, name: String): SlimeWorld? {
        return ensureFacade().createSlimeWorld(name, file)
    }

    fun createSlimeWorld(file: File): SlimeWorld? {
        return ensureFacade().createSlimeWorld(file)
    }

    fun save(slimeWorld: SlimeWorld) {
        ensureFacade().save(slimeWorld)
    }

    fun getStorage(): AbstractSlimeWorldStorage {
        return ensureFacade().abstractSlimeWorldStorage
    }

    private fun ensureFacade(): SlimeDelegator {
        if (!::delegator.isInitialized) {
            throw IllegalStateException("SlimeDelegator not created")
        }

        return delegator
    }

    class SlimeDelegator(
        val abstractSlimeWorldStorage: AbstractSlimeWorldStorage,
        val slimeWorldFactory: SlimeWorldFactory
    ) {

        fun createSlimeWorld(name: String, file: File): SlimeWorld? {
            var slimeWorld: SlimeWorld? = null

            try {
                slimeWorld = slimeWorldFactory.createSlimeWorld(file, name)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return slimeWorld
        }

        fun createSlimeWorld(file: File): SlimeWorld? {
            return createSlimeWorld(file.nameWithoutExtension, file)
        }

        fun save(slimeWorld: SlimeWorld) {
            abstractSlimeWorldStorage.save(slimeWorld)
        }
    }

}