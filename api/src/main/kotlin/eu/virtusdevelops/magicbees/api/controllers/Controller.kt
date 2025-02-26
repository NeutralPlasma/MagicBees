package eu.virtusdevelops.magicbees.api.controllers

interface Controller {

    fun init() : Boolean

    fun reload()
}