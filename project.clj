(defproject investment-tracker "0.1.0-SNAPSHOT"
  :description "A web application to track investment performance and status"
  :url "http://wizardpb.github.com/investment-tracker"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [com.datomic/datomic-free "0.9.5344"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/slf4j-log4j12]]
                 [ch.qos.logback/logback-classic "1.0.1"]
                 [org.clojure/data.csv "0.1.3"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-codec "1.0.0"]
                 ;[ring.middleware.logger "0.5.0"]
                 [clj-http "2.1.0"]
                 ]
  :plugins [[lein-ring "0.9.7"]]
  :ring {
         :handler investment-tracker.core/app
         ;; :init ring-server.core/reload-data
         ;; :nrepl {:start? true}
         }
  )
