package pro.bilous.difhub.load

import pro.bilous.difhub.model.Model

interface IModelLoader {
    fun loadModel(reference: String): Model?
    fun loadModels(reference: String): List<Model>?
}
