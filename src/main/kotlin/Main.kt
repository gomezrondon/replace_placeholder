import java.io.File



/*

---- file1
example:
call some_storeProcedure(':USER',':VALUE');

---- file2

USER:javier|VALUE:admin
USER:maria|VALUE:admin,normal user
USER:juan|VALUE:normal user, security

// java -jar replace_placeholder.jar "C:\temp\file1.txt" "C:\temp\file2.txt"

*/

fun main(args : Array<String>) {
    val measureTimeMillis = kotlin.system.measureTimeMillis {

        var file1 = args[0].toLowerCase()
        var file2 = args[1].toLowerCase()

        var fileScript = File(file1).readText()
        var fileScriptValues = File(file2).readLines()

        //how to pick up the placeHolder
        // :[\w]+
     //   var regexStr = """'(.*?)'""";
        var regexStr = """:[\w]+""";
        val placeHolderList = findRegexMatches(regexStr, fileScript).map { it.replace(":","") }
        regexStr = """(?<=xxx:)(.*?)(?=\||$)""";

        val fileValueList = fileScriptValues.map { line -> line.split("|") }

        var listOfResults = ArrayList<String>()
        val totalPlaceHolder = placeHolderList.size

        fileValueList.forEach {x ->
            var copyOfScript = fileScript
            x.forEach { line ->
                var counter = 1
                placeHolderList.forEach { placeHolder ->
                    copyOfScript =  pepe(regexStr, placeHolder, line, copyOfScript, listOfResults, counter, totalPlaceHolder)

                    counter++
                }
            }

        }

        listOfResults.forEach { println("${it}") }

    }
    println("\n total time:$measureTimeMillis milliseconds")
}

private fun pepe(regexStr: String, placeHolder: String, line: String, copyOfScript: String, listOfResults: ArrayList<String>, counter: Int, totalPlaceHolder: Int):String {
    var copyOfScript1 = copyOfScript
    val newRegex = regexStr.replace("xxx", placeHolder)
    val val1 = findRegexMatches(newRegex, line)
    for (findRegexMatch in val1) {
        val split = findRegexMatch.split(",")
        if (split.size > 1) {
            split.forEach { s ->
                //       println("2 $placeHolder :  $s")
                var copyOfCopy = copyOfScript1.replace(":"+placeHolder, s.trim())
                listOfResults.add(copyOfCopy)
            }
        } else {
            //   println("1 $placeHolder :  $findRegexMatch")
            copyOfScript1 = copyOfScript1.replace(":"+placeHolder, findRegexMatch)
            if (counter == totalPlaceHolder) {
                listOfResults.add(copyOfScript1)
            }

        }
    }
    return copyOfScript1
}

data class PlaceHolder(var pHolderName:String="", var pHolderValue:String="", var valueList:List<String> = ArrayList<String>())

private fun findRegexMatches(regexStr: String, fileScript: String): List<String> {
   // val toRegex = regexStr.toRegex(RegexOption.MULTILINE)
    val toRegex = regexStr.toRegex()
    val matchEntire = toRegex.findAll(fileScript)
    val toList = matchEntire.map { it.value }.toList()
    return toList
}



