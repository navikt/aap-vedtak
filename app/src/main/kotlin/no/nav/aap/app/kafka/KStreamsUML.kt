package no.nav.aap.app.kafka

import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyDescription
import java.io.File

internal const val EOL = "\n"
internal const val TAB = "\t"

/**
 * Uses topology description to create PlantUML
 */
class KStreamsUML {
    companion object {
        fun file(topology: Topology, path: String = "build/topology.puml"): File {
            val umlGenerator = KStreamsUML()
            return File(path).apply {
                writeText(umlGenerator.plantUML(topology))
            }
        }
    }

    private fun plantUML(topology: Topology): String = topology.describe().let { description ->
        val stores = description.globalStores().map { processStore(it) }.toSet()
        val subtopologies = description.subtopologies().map { processSubtopology(it) }
        val queues = description.getTopics().map { formatTopicToUmlQueue(it) }.toSet()
        val uml = PlantUML(stores + subtopologies, queues)
        uml.toString()
    }

    private fun TopologyDescription.getTopics(): Set<String> = this.let {
        val storeTopics = this.globalStores().flatMap { store -> store.source().topicSet() }
        val subtopologyTopics = this.subtopologies().flatMap { it.getTopics() }
        storeTopics.plus(subtopologyTopics).toSet()
    }

    private fun TopologyDescription.Subtopology.getTopics(): Set<String> = this.nodes().let { nodes ->
        val sinkTopics = nodes.filterIsInstance<TopologyDescription.Sink>().map { it.topic() }
        val sourceTopics = nodes.filterIsInstance<TopologyDescription.Source>().flatMap { it.topicSet() }
        sinkTopics.plus(sourceTopics).toSet()
    }

    private fun processStore(store: TopologyDescription.GlobalStore): Package {
        val source = processNode(store.source())
        val processor = processNode(store.processor())
        val (declarations, relations) = source.join(processor)
        return Package(store.id(), declarations, relations)
    }

    private fun processSubtopology(subtopology: TopologyDescription.Subtopology): Package {
        val agents =
            subtopology.nodes().map { node -> formatNodeToUmlAgent(node.name(), node::class.simpleName!!) }.toSet()

        val downstreamRelations =
            subtopology.nodes().flatMap { node -> node.successors().map { node.name() to it.name() } }
                .map { (node, downstreamNode) -> formatNodeToUmlRelation(node, downstreamNode) }.toSet()

        val nodes = subtopology.nodes().map { processNode(it) }
            .reduce { acc, pair -> Pair(acc.first.union(pair.first), acc.second.union(pair.second)) }

        val (declarations, relations) = nodes.join(Pair(agents, emptySet())).join(Pair(emptySet(), downstreamRelations))

        return Package(subtopology.id(), declarations, relations)
    }

    private fun processNode(node: TopologyDescription.Node): Pair<Set<String>, Set<String>> = when (node) {
        is TopologyDescription.Source -> node.sourceToUml()
        is TopologyDescription.Processor -> node.processorToUml()
        is TopologyDescription.Sink -> node.sinkToUml()
        else -> Pair(emptySet(), emptySet())
    }

    private fun TopologyDescription.Source.sourceToUml(): Pair<Set<String>, Set<String>> =
        this.topicSet().map(::formatPlantUmlNames)
            .map { formattedTopicName -> formatSourceToUmlRelation(formattedTopicName, this.name()) }
            .let { Pair(emptySet(), it.toSet()) }

    private fun TopologyDescription.Processor.processorToUml(): Pair<Set<String>, Set<String>> =
        this.stores().map { it to formatPlantUmlNames(it) }.map { (storeName, formattedStoreName) ->
            formatStoreToUmlDatabase(storeName, formattedStoreName) to formatStoreToUmlRelation(
                formattedStoreName, this.name()
            )
        }.split()

    private fun TopologyDescription.Sink.sinkToUml(): Pair<Set<String>, Set<String>> = this.let {
        val formattedSinkName = formatPlantUmlNames(it.topic())
        val nodeName = this.name()
        Pair(emptySet(), setOf(formatSinkToUmlRelation(formattedSinkName, nodeName)))
    }

    private fun List<Pair<String, String>>.split(): Pair<Set<String>, Set<String>> = this.let {
        val declarations = it.map { pair -> pair.first }.toSet()
        val relations = it.map { pair -> pair.second }.toSet()
        declarations to relations
    }

    private fun Pair<Set<String>, Set<String>>.join(other: Pair<Set<String>, Set<String>>): Pair<Set<String>, Set<String>> =
        this.let { Pair(it.first.union(other.first), it.second.union(other.second)) }

    private fun formatPlantUmlNames(string: String) =
        string.removeSurrounding("[", "]").replace("[^A-Za-z0-9_]".toRegex(), "_")

    private fun formatTopicToUmlQueue(topicName: String) = """
        queue "$topicName" <<topic>> as ${formatPlantUmlNames(topicName)}
    """.trimIndent()

    private fun formatSourceToUmlRelation(formattedTopicName: String, nodeName: String) = """
        $formattedTopicName --> ${formatPlantUmlNames(nodeName)}
    """.trimIndent()

    private fun formatStoreToUmlDatabase(storeName: String, formattedStoreName: String) = """
        database "$storeName" <<State Store>> as $formattedStoreName
    """.trimIndent()

    private fun formatStoreToUmlRelation(formattedStoreName: String, nodeName: String) = """
        $formattedStoreName -- ${formatPlantUmlNames(nodeName)}
    """.trimIndent()

    private fun formatSinkToUmlRelation(formattedSinkName: String, nodeName: String) = """
        $formattedSinkName <-- ${formatPlantUmlNames(nodeName)}
    """.trimIndent()

    private fun formatNodeToUmlAgent(nodeName: String, nodeType: String) = """
        agent "$nodeName" <<$nodeType>> as ${formatPlantUmlNames(nodeName)}
    """.trimIndent()

    private fun formatNodeToUmlRelation(nodeName: String, downstreamNodeName: String) = """
        ${formatPlantUmlNames(nodeName)} --> ${formatPlantUmlNames(downstreamNodeName)}
    """.trimIndent()

    private data class Package(val id: Int, val declarations: Set<String>, val relations: Set<String>) {
        override fun toString(): String {
            return """
                | package "Sub-topology: $id" {
                | $TAB${declarations.joinToString(EOL + TAB)}
                | $TAB${relations.joinToString(EOL + TAB)}
                | }
            """.trimMargin("| ")
        }
    }

    private data class PlantUML(val packages: Set<Package>, val queue: Set<String>) {
        override fun toString(): String {
            return """
                | @startuml
                | !theme lightgray
                | ${queue.joinToString(EOL)}
                | ${packages.joinToString(EOL) { it.toString() }}
                | @enduml
            """.trimMargin("| ")
        }
    }

    private data class Markdown(val diagram: PlantUML) {
        override fun toString(): String {
            return """
                | ```plantuml
                | $diagram
                | ```
            """.trimMargin("| ")
        }
    }
}