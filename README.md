# amazon-reviews-analyser


To implement this task I've resorted to well-known tools at the field of multi-threading environment, namely: Scala, AKKA, sbt.
To find top 1000 most frequent entities I've used a well-know 'frequency map', i.e. ConcurrentHashMap with LongAdder updated
concurrently from multiple threads running actors.
To implement the translation part I've used apache html-client along with jackson library.


being asked to translate the reviews and told about the limits to preserve precious resources (charging by every API call)
this implementation can be improved a bit further in the following way
(keeping in mind that this can lead to losing the emotional coloring of the messages)

* filtered out possible html tags,
* removed 'extra' spaces (i.e. in case of several of them in a row)
* removed 'extra' signs (i.e. several exclamation/question marks in a row)



TO RUN:
* place "Reviews.csv" file into resources directory
and
* $> sbt run
or
* $> sbt run translate=true
