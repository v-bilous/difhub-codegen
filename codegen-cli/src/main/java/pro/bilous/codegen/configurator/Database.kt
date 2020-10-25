package pro.bilous.codegen.configurator

data class Database(val name: String, val driver: String, val type: String, val port: Int, val dependency: String)
