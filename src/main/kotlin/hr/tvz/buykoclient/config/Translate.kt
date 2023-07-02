package hr.tvz.buykoclient.config

import javax.xml.parsers.DocumentBuilderFactory

class Translate {

    fun readXML (id: Int, lang: String): String {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = builder.parse("src/main/resources/language-pack.xml")
        doc.normalizeDocument()
        val root = doc.documentElement
        val wordExpression = root.getElementsByTagName(lang).item(id).textContent
        return wordExpression
    }

}