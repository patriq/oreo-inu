package api.script

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.util.Modules
import org.rspeer.game.script.TaskScript

abstract class GuiceTaskScript : TaskScript(), Module {
    override fun createInjector(): Injector {
        return Guice.createInjector(this)
    }

    override fun configure(binder: Binder) {
        binder.bind(this.javaClass).toInstance(this)
        binder.install(Modules.combine(modules()))
    }

    protected open fun modules(): List<Module> {
        return listOf()
    }
}