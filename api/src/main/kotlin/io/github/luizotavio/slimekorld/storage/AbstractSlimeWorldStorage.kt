package io.github.luizotavio.slimekorld.storage

import io.github.luizotavio.slimekorld.SlimeWorld
import io.github.luizotavio.slimekorld.exception.SlimeStorageException
import org.bukkit.World
import java.io.File
import kotlin.jvm.Throws

abstract class AbstractSlimeWorldStorage(
    val path: File
) {

    @Throws(SlimeStorageException::class)
    abstract fun save(slimeWorld: SlimeWorld)

    @Throws(SlimeStorageException::class)
    abstract fun refresh(slimeWorld: SlimeWorld): World
}