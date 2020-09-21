package pro.bilous.difhub.config

data class DifHub(
	val api: String,
	val organization: String
) {
	fun getSystemsUrl() = "organizations/$organization/systems"

	fun getApplicationsUrl(system: String)
			= "${getSystemsUrl()}/$system/applications"

	fun getApplicationUrl(system: String, app: String)
			= "${getApplicationsUrl(system)}/$app"

	fun getApplicationSettingsUrl(system: String, app: String)
			= "${getApplicationsUrl(system)}/$app/settings"

	fun getInterfacesUrl(system: String, application: String)
			= "${getApplicationsUrl(system)}/$application/interfaces"

	fun getInterfaceUrl(system: String, application: String, name: String)
			= "${getInterfacesUrl(system, application)}/$name"

	fun getDatasetsUrl(system: String, application: String)
			= "${getApplicationsUrl(system)}/$application/datasets"

	fun getDatatsetTypeUrl(system: String, application: String, type: String)
			= "${getDatasetsUrl(system, application)}/$type"
}

data class Config(val difhub: DifHub)
