package pro.bilous.difhub.model

data class Frame(val position: Position)


data class Left(val color: String = "")


data class Parent(val name: String = "",
                  val id: String = "")


data class Position(val top: Int = 0,
                    val left: Int = 0,
                    val width: Int = 0,
                    val height: Int = 0)


data class Data(val records: List<RecordsItem> = listOf(),
				val examples: List<Any> = listOf())

data class RecordsItem(val values: List<String>?,
					   val index: Int = 0)

data class History(val updatedby: String = "",
				   val createdby: String = "",
				   val created: String = "",
				   val completions: List<CompletionsItem>?,
				   val updated: String = "",
				   val mirrored: String = "")


data class ElementsItem(val identity: Identity,
						val style: Style?,
						val frame: Frame?)


data class Contact(val identity: Identity,
				   val url: String = "",
				   val email: String = "")


data class Version(val major: Int = 0,
                   val minor: Int = 0,
                   val revision: Int = 0)


data class FieldsItem(val reference: String = "",
					  val identity: Identity,
					  val keys: Boolean = false,
					  val usage: String = "",
					  val privacy: String = "",
					  val optional: Boolean = false,
					  val size: Int = 0,
					  val type: String = "",
					  val format: String = "",
					  val value: String = "",
					  val count: Int? = null,
					  val order: Int = 0,
					  val properties: List<Property>?)


data class Model(val external: Boolean = false,
				 val data: Data?,
				 val identity: Identity = Identity(name = "default"),
				 val subscription: Subscription? = null,
				 val _path: String = "",
				 val path: String = "",
				 val version: Version?,
				 val structure: Structure?,
				 val operations: List<OperationsItem>?,
				 val responses: List<ResponsesItem>?,
				 val parameters: List<ParametersItem>?,
				 val layouts: List<LayoutsItem>?,
				 val `object`: Object? = null)

data class Style(val border: Border)


data class Object(val parent: Parent?,
				  val access: String = "",
				  val usage: String = "",
				  val subscriptionCount: Int = 0,
				  val subscriptioncount: Int = 0,
				  val history: History?,
				  val type: String = "",
				  val picture: String = "",
				  val publicationCount: Int = 0,
				  val publicationcount: Int = 0,
				  val contact: Contact? = null,
				  val lastapprovedversion: Lastapprovedversion?,
				  val elements: List<ElementsItem>?,
				  val tags: List<Identity>?,
				  val documents: List<Object>?,
				  val properties: List<Property>?,
				  val name: String?)

data class Identity(val name: String = "",
					val id: String? = null,
					val description: String? = null
)

data class Subscription(val name: String = "")


data class Structure(val fields: List<FieldsItem>?)


data class Border(val left: Left)

data class Property(val identity: Identity? = null,
					val type: String? = null,
					val value: String? = null)


