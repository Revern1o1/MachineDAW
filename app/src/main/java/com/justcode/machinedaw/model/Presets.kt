package com.justcode.machinedaw.model

import androidx.compose.runtime.Immutable

/**
 * Preset library models (SDD §4.2 / Milestone B).
 * Factory presets ship in-app; project only stores preset id + param drift later.
 */
@Immutable
data class MachinePreset(
    val id: String,
    val typeId: String,
    val name: String,
    val category: String,
    /** Full param vector indexed by ParamUiDef.id */
    val params: FloatArray,
    val isFactory: Boolean = true,
    val tags: List<String> = emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MachinePreset) return false
        return id == other.id && typeId == other.typeId
    }

    override fun hashCode(): Int = id.hashCode() * 31 + typeId.hashCode()
}

object PresetLibrary {

    fun factoryFor(typeId: String): List<MachinePreset> = when (typeId) {
        "swarm" -> SwarmFactory
        "sine_test" -> SineTestFactory
        else -> emptyList()
    }

    fun defaultFor(typeId: String): MachinePreset? =
        factoryFor(typeId).firstOrNull()

    fun find(typeId: String, presetId: String): MachinePreset? =
        factoryFor(typeId).find { it.id == presetId }

    fun cycleNext(typeId: String, currentId: String?): MachinePreset? {
        val list = factoryFor(typeId)
        if (list.isEmpty()) return null
        val idx = list.indexOfFirst { it.id == currentId }.let { if (it < 0) 0 else it }
        return list[(idx + 1) % list.size]
    }

    fun cyclePrev(typeId: String, currentId: String?): MachinePreset? {
        val list = factoryFor(typeId)
        if (list.isEmpty()) return null
        val idx = list.indexOfFirst { it.id == currentId }.let { if (it < 0) 0 else it }
        return list[(idx - 1 + list.size) % list.size]
    }

    // -------------------------------------------------------------------------
    // Swarm factory (param order matches MachineParamCatalog / SwarmMachine)
    // 0 Waveform  1 Cutoff  2 Res  3 A  4 D  5 S  6 R  7 Level
    // -------------------------------------------------------------------------
    private val SwarmFactory = listOf(
        preset(
            id = "swarm_init",
            typeId = "swarm",
            name = "Init",
            category = "Init",
            tags = listOf("Init"),
            params = floatArrayOf(0f, 1200f, 0.20f, 0.01f, 0.20f, 0.70f, 0.30f, 0.40f),
        ),
        preset(
            id = "swarm_soft_pad",
            typeId = "swarm",
            name = "Soft Pad",
            category = "Pad",
            tags = listOf("Pad", "Ambient"),
            params = floatArrayOf(0f, 800f, 0.15f, 0.40f, 0.50f, 0.80f, 0.70f, 0.35f),
        ),
        preset(
            id = "swarm_pluck",
            typeId = "swarm",
            name = "Pluck",
            category = "Pluck",
            tags = listOf("Pluck", "Percussive"),
            params = floatArrayOf(1f, 2800f, 0.35f, 0.005f, 0.18f, 0.10f, 0.12f, 0.45f),
        ),
        preset(
            id = "swarm_bass",
            typeId = "swarm",
            name = "Bass",
            category = "Bass",
            tags = listOf("Bass"),
            params = floatArrayOf(0f, 450f, 0.40f, 0.01f, 0.25f, 0.55f, 0.20f, 0.50f),
        ),
        preset(
            id = "swarm_bright_lead",
            typeId = "swarm",
            name = "Bright Lead",
            category = "Lead",
            tags = listOf("Lead"),
            params = floatArrayOf(1f, 4500f, 0.45f, 0.01f, 0.22f, 0.45f, 0.25f, 0.42f),
        ),
        preset(
            id = "swarm_drift",
            typeId = "swarm",
            name = "Swarm Drift",
            category = "Pad",
            tags = listOf("Pad", "Moving"),
            params = floatArrayOf(0f, 1800f, 0.55f, 0.08f, 0.35f, 0.60f, 0.45f, 0.38f),
        ),
        preset(
            id = "swarm_glass",
            typeId = "swarm",
            name = "Glass",
            category = "Keys",
            tags = listOf("Keys", "Bright"),
            params = floatArrayOf(1f, 5500f, 0.25f, 0.02f, 0.40f, 0.25f, 0.50f, 0.32f),
        ),
        preset(
            id = "swarm_warm_keys",
            typeId = "swarm",
            name = "Warm Keys",
            category = "Keys",
            tags = listOf("Keys"),
            params = floatArrayOf(0f, 1600f, 0.18f, 0.02f, 0.30f, 0.75f, 0.35f, 0.40f),
        ),
    )

    private val SineTestFactory = listOf(
        preset(
            id = "sine_init",
            typeId = "sine_test",
            name = "Init",
            category = "Init",
            tags = listOf("Init"),
            params = floatArrayOf(440f, 0.25f),
        ),
        preset(
            id = "sine_low",
            typeId = "sine_test",
            name = "Low Tone",
            category = "Tone",
            tags = listOf("Bass"),
            params = floatArrayOf(110f, 0.30f),
        ),
        preset(
            id = "sine_high",
            typeId = "sine_test",
            name = "High Tone",
            category = "Tone",
            tags = listOf("Lead"),
            params = floatArrayOf(880f, 0.20f),
        ),
    )

    private fun preset(
        id: String,
        typeId: String,
        name: String,
        category: String,
        tags: List<String>,
        params: FloatArray,
    ) = MachinePreset(
        id = id,
        typeId = typeId,
        name = name,
        category = category,
        params = params,
        isFactory = true,
        tags = tags,
    )
}
