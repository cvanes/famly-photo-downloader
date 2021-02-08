package com.cvanes.famly.cli

import com.cvanes.famly.api.ApiClient
import com.cvanes.famly.newHttpClient
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Paths
import java.time.LocalDate
import java.time.ZoneOffset.UTC
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(name = "photos", description = ["Download photos"], mixinStandardHelpOptions = true)
class PhotoDownloader : Callable<Int> {

    @Option(names = ["--email"], description = ["Email"], required = true)
    lateinit var email: String

    @Option(names = ["--password"], description = ["Password"], required = true)
    lateinit var password: String

    @Option(names = ["--start"], description = ["Start date"])
    var start: String? = null

    @Option(names = ["--end"], description = ["End date"])
    var end: String? = null

    @Option(names = ["--output"], description = ["Download location"])
    var output: String = Paths.get(System.getProperty("user.home"), "Downloads", "Famly").toString()

    override fun call(): Int = runBlocking {
        val downloadLocation = Paths.get(output, "$startDate to $endDate").toFile()
        val apiClient = ApiClient(email, password, downloadLocation, newHttpClient())
        apiClient.downloadPhotos(startDate.atStartOfDay().atOffset(UTC), endDate.plusDays(1).atStartOfDay().atOffset(UTC))
        0
    }

    private val startDate: LocalDate by lazy {
        if (!start.isNullOrBlank()) {
            LocalDate.parse(start)
        } else {
            LocalDate.now().minusMonths(1).withDayOfMonth(1)
        }
    }

    private val endDate: LocalDate by lazy {
        if (!end.isNullOrBlank()) {
            LocalDate.parse(end)
        } else {
            LocalDate.now().withDayOfMonth(1).minusDays(1)
        }
    }

}

fun main(args: Array<String>) : Unit = exitProcess(CommandLine(PhotoDownloader()).execute(*args))