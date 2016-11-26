(ns investment-tracker.system
  (:require [investment-tracker.dbinit.core :as dbi]
            [datomic.api :as d])
  (:import (com.vaadin.server VaadinServlet)
           (org.eclipse.jetty.servlet ServletContextHandler)
           (org.eclipse.jetty.server Server)))

(defn jetty-server [ui-name resource-base]
  (doto (Server. 8080)
    (.setHandler
      (doto (ServletContextHandler. ServletContextHandler/SESSIONS)
        (.setContextPath "/")
        (.setInitParameter "UI" ui-name)
        (.setInitParameter "legacyPropertyToString" "true")
        (.setResourceBase resource-base)
        (.addServlet VaadinServlet "/*")))))

(defn system [resource-base]
  {:db     {:uri dbi/uri}
   :server (jetty-server "investment_tracker.ui.UI" resource-base)})

(defn- start-server [s]
  (println "Starting system")
  (.start (:server s))
  s)

(defn- stop-server [s]
  (println "Stopping system" )
  (.stop (:server s)))

(defn start [s]
  (-> s
    ;(assoc-in [:db :conn] (d/connect (get-in s [:db :uri])))
    (start-server))
  )

(defn stop [s]
  (-> s
    (stop-server)
    (dissoc :db :conn)))
