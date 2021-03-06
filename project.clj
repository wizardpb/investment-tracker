(defproject investment-tracker "0.1.0-SNAPSHOT"
  :description "A web application to track investment performance and status"
  :url "http://wizardpb.github.com/investment-tracker"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clojure"]
  :dependencies [
                 [org.clojure/clojure "1.9.0-alpha14"]
                 [com.datomic/datomic-pro "0.9.5544"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/slf4j-log4j12]]
                 [ch.qos.logback/logback-classic "1.0.1"]
                 [org.clojure/data.csv "0.1.3"]
                 [de.jollyday/jollyday "0.5.1"]
                 [clojure.java-time "0.2.0"]
                 ;[clj-http "2.3.0"]
                 ;[cheshire "5.6.3"]
                 [com.cemerick/url "0.1.1"]
                 [com.vaadin/vaadin-server "7.7.4"]
                 [com.vaadin/vaadin-client-compiled "7.7.4"]
                 [com.vaadin/vaadin-themes "7.7.4"]
                 [com.prajnainc/functional-vaadin "0.3.0-snapshot"]
                 [com.prajnainc/yql-finance "0.1.0-SNAPSHOT"]
                 [buddy/buddy-core "1.1.0"]
                 [clojurewerkz/scrypt "1.2.0"]
                 ]
  :profiles {:dev      {:aot          [investment-tracker.ui.UI]
                        :source-paths ["src/clojure" "dev"]
                        :dependencies [[org.apache.directory.studio/org.apache.commons.io "2.4"]
                                       [org.clojure/tools.nrepl "0.2.11"]
                                       [org.clojure/tools.namespace "0.2.11"]
                                       [org.clojure/java.classpath "0.2.3"]
                                       [org.eclipse.jetty/jetty-server "9.3.8.v20160314"]
                                       [org.eclipse.jetty/jetty-servlet "9.3.8.v20160314"]
                                       [javax.servlet/javax.servlet-api "3.1.0"]
                                       ]
                        }
             :jar      {:aot          [investment-tracker.ui.UI]}
             :uberjar  {:aot          [investment-tracker.ui.UI]}
             }

  )
