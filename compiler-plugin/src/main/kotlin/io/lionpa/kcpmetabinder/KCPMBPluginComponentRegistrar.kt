package io.lionpa.kcpmetabinder

import io.lionpa.kcpmetabuilder.BuildConfig
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

class KCPMBPluginComponentRegistrar : CompilerPluginRegistrar() {
    override val pluginId: String
        get() = BuildConfig.KOTLIN_PLUGIN_ID
    override val supportsK2: Boolean
        get() = true

    companion object {
        lateinit var messageCollector: MessageCollector
    }

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY) ?: MessageCollector.NONE

        FirExtensionRegistrarAdapter.registerExtension(KCPMBPluginRegistrar())
    }
}
