package me.elsiff.morefish.hooker

import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.elsiff.morefish.MoreFish
import me.elsiff.morefish.configuration.format.Format
import me.elsiff.morefish.fishing.competition.FishingCompetition
import org.bukkit.entity.Player

/**
 * Created by elsiff on 2019-01-24.
 * Update the papi from 2.7.1 to 2.10.9 by Mical on 2021-1-25
 */
class PlaceholderApiHooker : PluginHooker {
    override val pluginName = "PlaceholderAPI"
    override var hasHooked = false

    override fun hook(plugin: MoreFish) {
        // MoreFishPlaceholder(plugin).hook()
        MoreFishPlaceholder(plugin).register()
        Format.init(this)
        hasHooked = true
    }

    fun tryReplacing(string: String, player: Player? = null): String {
        return PlaceholderAPI.setPlaceholders(player, string)
    }

    class MoreFishPlaceholder constructor(
        val moreFish: MoreFish
    ) : PlaceholderExpansion() {
        private val competition: FishingCompetition = moreFish.competition

        override fun getIdentifier(): String = "morefish"

        override fun getAuthor(): String = moreFish.description.authors.toString()

        override fun getVersion(): String = moreFish.description.version

        override fun onPlaceholderRequest(player: Player?, identifier: String): String? {
            return when {
                identifier.startsWith("top_player_") -> {
                    val number = identifier.replace("top_player_", "").toInt()
                    if (competition.ranking.size >= number)
                        competition.recordOf(number).fisher.name
                    else
                        "no one"
                }
                identifier.startsWith("top_fish_length_") -> {
                    val number = identifier.replace("top_fish_length_", "").toInt()
                    if (competition.ranking.size >= number)
                        competition.recordOf(number).fish.length.toString()
                    else
                        "0.0"
                }
                identifier.startsWith("top_fish_") -> {
                    val number = identifier.replace("top_fish_", "").toInt()
                    if (competition.ranking.size >= number)
                        competition.recordOf(number).fish.type.name
                    else
                        "none"
                }
                identifier == "rank" -> {
                    require(player != null) { "'rank' placeholder requires a player" }
                    if (competition.containsContestant(player)) {
                        val record = competition.recordOf(player)
                        competition.rankNumberOf(record).toString()
                    } else {
                        "0"
                    }
                }
                identifier == "fish_length" -> {
                    require(player != null) { "'fish_length' placeholder requires a player" }
                    if (competition.containsContestant(player))
                        competition.recordOf(player).fish.length.toString()
                    else
                        "0.0"
                }
                identifier == "fish" -> {
                    require(player != null) { "'fish' placeholder requires a player" }
                    if (competition.containsContestant(player))
                        competition.recordOf(player).fish.type.name
                    else
                        "none"
                }
                else -> null
            }
        }

    }
}