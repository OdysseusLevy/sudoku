(defproject cosmosgame-sudoku "0.1.0-SNAPSHOT"
  :description "Simple sudoku solver. Example of how fun it is to work with clojure"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"] 
		 [log4j/log4j "1.2.16" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]
                 [org.clojure/tools.logging "0.2.3"]
		]
  :jvm-opts ["-Xmx500m"]
  :main org.cosmosgame.sudoku.core
)
