package spd.difhub.model

data class Lastapprovedversion(val major: Int = 0,
                               val minor: Int = 0,
                               val revision: Int = 0)


data class CompletionsItem(val completedbymessage: String = "",
                           val completed: String = "",
                           val completedby: String = "",
                           val status: String = "")


data class ParametersItem(val field: Field?,
                          val location: String = "",
						  val id: String = "",
						  val name: String = "",
						  val description: String = "")


data class LayoutsItem(val unit: String = "",
                       val identity: Identity,
                       val schedule: Schedule?,
                       val platform: String = "",
                       val location: Location?)

data class Field(val reference: String = "",
				 val access: Int = 0,
				 val identity: Identity,
				 val keys: Boolean = false,
				 val usage: String = "",
				 val count: Int = 0,
				 val format: String = "",
				 val privacy: String = "",
				 val optional: Boolean = false,
				 val type: String = "",
				 val value: String = "",
				 val order: Int = 0,
				 val size: Int = 0)


data class ResponsesItem(val code: String = "",
                         val field: Field?,
						 val id: String = "",
						 val name: String = "",
						 val description: String = "")


data class Schedule(val name: String?)


data class OperationsItem(val identity: Identity,
                          val deprecated: Boolean = false,
                          val action: String = "",
                          val responses: List<ResponsesItem>?,
                          val parameters: List<ParametersItem>?)

data class Location(val name: String?)


